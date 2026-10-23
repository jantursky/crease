#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

/** The shape of `UIHingeInteractionUpdate` in iOS 27.1: `hinge` is nil on a device without one. */
@protocol CRSHingeUpdate
- (nullable id)hinge;
@end

/** The shape of `UIHinge` in iOS 27.1. */
@protocol CRSHinge
/** Unknown 0, closed 1, partially open 2, fully open 3. */
- (NSInteger)status;
/** Radians: 0 shut, pi flat. */
- (CGFloat)angle;
@end

/** A reserved region in the observer's coordinates, in points. */
@interface CRSRegion : NSObject
@property (nonatomic, readonly) double x;
@property (nonatomic, readonly) double y;
@property (nonatomic, readonly) double width;
@property (nonatomic, readonly) double height;
@property (nonatomic, readonly) BOOL active;
@end

/**
 * A transparent view that fills its superview and takes no touches. It owns the hinge interaction and
 * reads the reserved regions during layout, so UIKit lays it out again whenever a region it read changes.
 */
@interface CRSHingeObserverView : UIView
- (instancetype)initWithChangeHandler:(void (^)(void))changeHandler;
/** Whether the system has the hinge interaction at all. */
@property (nonatomic, readonly) BOOL hingeSupported;
/** Whether an update has reported a hinge. Absence is reported only through an update. */
@property (nonatomic, readonly) BOOL hasHinge;
/** Raw UIHingeStatus: unknown 0, closed 1, partially open 2, fully open 3. */
@property (nonatomic, readonly) NSInteger hingeStatus;
/** Hinge angle in radians: 0 shut, pi flat. */
@property (nonatomic, readonly) double hingeAngle;
/** Fold divisions, inactive ones included. */
@property (nonatomic, readonly) NSArray<CRSRegion *> *divisions;
/** Active occlusions such as a camera housing in use. */
@property (nonatomic, readonly) NSArray<CRSRegion *> *occlusions;
/**
 * Takes an update from the hinge interaction. Any object answering the selectors of [CRSHingeUpdate]
 * works; missing selectors read as no hinge.
 */
- (void)handleUpdate:(id)update;
/** Reads the reserved regions again and reports a change. */
- (void)refreshRegions;
/** Stops observing, drops the change handler and leaves the superview. */
- (void)detach;
@end

NS_ASSUME_NONNULL_END
