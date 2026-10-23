#import <dispatch/dispatch.h>
#import "CRSHingeObserverView.h"

/*
 * Reads the iPhone Duo hinge and reserved regions (iOS 27.1) through the Objective-C runtime.
 *
 * No iOS 27 type is named here. Classes are looked up by name, selectors are declared in private
 * protocols so the compiler emits ordinary message sends with the right ownership, and every send is
 * guarded by a responds check. On a system without the API the view stays inert: no hinge is reported
 * and no regions are read.
 */

@interface CRSRegion ()
- (instancetype)initWithFrame:(CGRect)frame active:(BOOL)active;
@end

@interface CRSHingeObserverView ()
@property (nonatomic, copy) void (^changeHandler)(void);
@property (nonatomic, strong) id interaction;
@property (nonatomic) NSUInteger retryGeneration;
- (void)installInteraction;
- (void)retryRegionsWithGeneration:(NSUInteger)generation remaining:(int)remaining;
- (BOOL)regionsAgreeWithHinge;
- (void)readRegions;
- (NSArray<CRSRegion *> *)regionsOfKind:(id)kind options:(NSUInteger)options;
- (void)notifyChange;
@end

@protocol CRSHingeInteractionInit
- (instancetype)initWithUpdateHandler:(void (^)(id interaction, id update))updateHandler;
@end

@protocol CRSReservedRegionKinds
- (id)divisionRegionKind;
- (id)occlusionRegionKind;
@end

@protocol CRSReservedRegionsView
- (NSArray *)reservedRegionsOfKind:(id)kind options:(NSUInteger)options;
@end

@protocol CRSReservedRegion
- (CGRect)frame;
- (BOOL)isActive;
@end

static const NSUInteger CRSIncludeInactive = 1;
static const NSInteger CRSStatusPartiallyOpen = 2;
static const int CRSMaxRegionRetries = 20;
static const int64_t CRSRegionRetryDelay = 100 * NSEC_PER_MSEC;

@implementation CRSRegion
- (instancetype)initWithFrame:(CGRect)frame active:(BOOL)active {
    if ((self = [super init])) {
        _x = frame.origin.x;
        _y = frame.origin.y;
        _width = frame.size.width;
        _height = frame.size.height;
        _active = active;
    }
    return self;
}
@end

@implementation CRSHingeObserverView

- (instancetype)initWithChangeHandler:(void (^)(void))changeHandler {
    if ((self = [super initWithFrame:CGRectZero])) {
        _changeHandler = [changeHandler copy];
        _divisions = @[];
        _occlusions = @[];
        self.userInteractionEnabled = NO;
        self.backgroundColor = UIColor.clearColor;
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
        [self installInteraction];
    }
    return self;
}

- (BOOL)hingeSupported {
    return _interaction != nil;
}

- (void)installInteraction {
    Class cls = NSClassFromString(@"UIHingeInteraction");
    if (cls == Nil || ![cls instancesRespondToSelector:@selector(initWithUpdateHandler:)]) return;
    __weak CRSHingeObserverView *weakSelf = self;
    id interaction = [(id<CRSHingeInteractionInit>)[cls alloc] initWithUpdateHandler:^(id interaction, id update) {
        [weakSelf handleUpdate:update];
    }];
    // addInteraction: raises for an object that is not a UIInteraction.
    if (![interaction conformsToProtocol:@protocol(UIInteraction)]) return;
    [self addInteraction:(id<UIInteraction>)interaction];
    _interaction = interaction;
}

- (void)detach {
    if (_interaction != nil) {
        [self removeInteraction:(id<UIInteraction>)_interaction];
        _interaction = nil;
    }
    _retryGeneration++;
    // The handler reaches back into Kotlin; dropping it breaks the cycle through the view.
    _changeHandler = nil;
    [self removeFromSuperview];
}

- (void)handleUpdate:(id)update {
    id hinge = [update respondsToSelector:@selector(hinge)] ? [(id<CRSHingeUpdate>)update hinge] : nil;
    if (hinge != nil && [hinge respondsToSelector:@selector(status)] && [hinge respondsToSelector:@selector(angle)]) {
        _hasHinge = YES;
        _hingeStatus = [(id<CRSHinge>)hinge status];
        _hingeAngle = [(id<CRSHinge>)hinge angle];
    } else {
        _hasHinge = NO;
        _hingeStatus = 0;
        _hingeAngle = 0;
    }
    [self readRegions];
    [self notifyChange];
    // The division lags the hinge by up to a second and no layout pass follows on its own, so the
    // regions are read again until they agree with the hinge.
    [self retryRegionsWithGeneration:++_retryGeneration remaining:CRSMaxRegionRetries];
}

- (void)retryRegionsWithGeneration:(NSUInteger)generation remaining:(int)remaining {
    if (remaining <= 0 || [self regionsAgreeWithHinge]) return;
    __weak CRSHingeObserverView *weakSelf = self;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, CRSRegionRetryDelay), dispatch_get_main_queue(), ^{
        CRSHingeObserverView *strongSelf = weakSelf;
        if (strongSelf == nil || strongSelf.retryGeneration != generation) return;
        [strongSelf refreshRegions];
        [strongSelf retryRegionsWithGeneration:generation remaining:remaining - 1];
    });
}

- (BOOL)regionsAgreeWithHinge {
    BOOL folded = _hasHinge && _hingeStatus == CRSStatusPartiallyOpen;
    for (CRSRegion *division in _divisions) {
        if (division.active != folded) return NO;
    }
    return YES;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    [self refreshRegions];
}

- (void)refreshRegions {
    [self readRegions];
    [self notifyChange];
}

- (void)readRegions {
    id kinds = NSClassFromString(@"UIViewReservedRegionKind");
    id division = [kinds respondsToSelector:@selector(divisionRegionKind)]
        ? [(id<CRSReservedRegionKinds>)kinds divisionRegionKind]
        : nil;
    id occlusion = [kinds respondsToSelector:@selector(occlusionRegionKind)]
        ? [(id<CRSReservedRegionKinds>)kinds occlusionRegionKind]
        : nil;
    _divisions = [self regionsOfKind:division options:CRSIncludeInactive];
    // An inactive occlusion is a camera that is not in use, which hides nothing.
    _occlusions = [self regionsOfKind:occlusion options:0];
}

- (NSArray<CRSRegion *> *)regionsOfKind:(id)kind options:(NSUInteger)options {
    if (kind == nil || ![self respondsToSelector:@selector(reservedRegionsOfKind:options:)]) return @[];
    NSArray *raw = [(id<CRSReservedRegionsView>)self reservedRegionsOfKind:kind options:options];
    if (![raw isKindOfClass:[NSArray class]]) return @[];
    NSMutableArray<CRSRegion *> *regions = [NSMutableArray arrayWithCapacity:raw.count];
    for (id region in raw) {
        if (![region respondsToSelector:@selector(frame)]) continue;
        CGRect frame = [(id<CRSReservedRegion>)region frame];
        BOOL active = [region respondsToSelector:@selector(isActive)]
            ? [(id<CRSReservedRegion>)region isActive]
            : !CGRectIsEmpty(frame);
        [regions addObject:[[CRSRegion alloc] initWithFrame:frame active:active]];
    }
    return regions;
}

- (void)notifyChange {
    if (_changeHandler != nil) _changeHandler();
}

@end
