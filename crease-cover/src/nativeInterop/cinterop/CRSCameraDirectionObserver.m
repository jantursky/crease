#import <AVFoundation/AVFoundation.h>
#import "CRSCameraDirectionObserver.h"

/*
 * Reads the camera directions of the iPhone Duo (iOS 27.1) through the Objective-C runtime. Nothing from
 * iOS 27 is named at build time: the class is looked up by name and every send is guarded.
 */

@protocol CRSDirectionCoordinatorInit <NSObject>
- (instancetype)initWithView:(UIView *)view
                 deviceTypes:(NSArray *)deviceTypes
               changeHandler:(void (^)(id map))changeHandler;
@end

@protocol CRSDirectionMap <NSObject>
- (NSArray *)forwardFacingDeviceDescriptors;
- (NSArray *)backwardFacingDeviceDescriptors;
@end

@protocol CRSDeviceDescriptor <NSObject>
- (NSString *)uniqueID;
- (NSString *)localizedName;
- (NSString *)deviceType;
- (NSInteger)position;
@end

@interface CRSCameraDescriptor ()
- (instancetype)initWithDescriptor:(id)descriptor;
@end

@implementation CRSCameraDescriptor
- (instancetype)initWithDescriptor:(id)descriptor {
    if ((self = [super init])) {
        id<CRSDeviceDescriptor> d = descriptor;
        _uniqueID = [d respondsToSelector:@selector(uniqueID)] ? [d uniqueID] : @"";
        _localizedName = [d respondsToSelector:@selector(localizedName)] ? [d localizedName] : @"";
        _deviceType = [d respondsToSelector:@selector(deviceType)] ? [d deviceType] : @"";
        _position = [d respondsToSelector:@selector(position)] ? [d position] : 0;
    }
    return self;
}
@end

@interface CRSCameraDirectionObserver ()
@property (nonatomic, copy, nullable) void (^changeHandler)(void);
@property (nonatomic, strong, nullable) id coordinator;
@end

@implementation CRSCameraDirectionObserver

- (instancetype)initWithView:(UIView *)view changeHandler:(void (^)(void))changeHandler {
    if ((self = [super init])) {
        _changeHandler = [changeHandler copy];
        _forwardFacing = @[];
        _backwardFacing = @[];
        [self installCoordinatorForView:view];
    }
    return self;
}

- (void)installCoordinatorForView:(UIView *)view {
    Class cls = NSClassFromString(@"AVCaptureDeviceDirectionCoordinator");
    if (cls == Nil || ![cls instancesRespondToSelector:@selector(initWithView:deviceTypes:changeHandler:)]) return;
    NSMutableArray *types = [NSMutableArray arrayWithObjects:
        AVCaptureDeviceTypeBuiltInWideAngleCamera,
        AVCaptureDeviceTypeBuiltInTelephotoCamera,
        AVCaptureDeviceTypeBuiltInUltraWideCamera,
        AVCaptureDeviceTypeBuiltInTrueDepthCamera,
        nil];
    __weak CRSCameraDirectionObserver *weakSelf = self;
    _coordinator = [(id<CRSDirectionCoordinatorInit>)[cls alloc] initWithView:view
                                                                  deviceTypes:types
                                                                changeHandler:^(id map) {
        [weakSelf handleMap:map];
    }];
    _supported = _coordinator != nil;
}

- (void)handleMap:(id)map {
    _forwardFacing = [self describe:[map respondsToSelector:@selector(forwardFacingDeviceDescriptors)]
                                       ? [(id<CRSDirectionMap>)map forwardFacingDeviceDescriptors] : nil];
    _backwardFacing = [self describe:[map respondsToSelector:@selector(backwardFacingDeviceDescriptors)]
                                        ? [(id<CRSDirectionMap>)map backwardFacingDeviceDescriptors] : nil];
    if (_changeHandler != nil) _changeHandler();
}

- (NSArray<CRSCameraDescriptor *> *)describe:(nullable NSArray *)raw {
    if (![raw isKindOfClass:[NSArray class]]) return @[];
    NSMutableArray<CRSCameraDescriptor *> *out = [NSMutableArray arrayWithCapacity:raw.count];
    for (id descriptor in raw) [out addObject:[[CRSCameraDescriptor alloc] initWithDescriptor:descriptor]];
    return out;
}

- (void)detach {
    _coordinator = nil;
    // The handler reaches back into Kotlin; dropping it breaks the cycle through the observer.
    _changeHandler = nil;
}

@end
