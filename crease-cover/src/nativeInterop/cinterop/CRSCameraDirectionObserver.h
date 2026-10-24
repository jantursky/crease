#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

/** One camera as the direction coordinator describes it. */
@interface CRSCameraDescriptor : NSObject
@property (nonatomic, readonly) NSString *uniqueID;
@property (nonatomic, readonly) NSString *localizedName;
/** The `AVCaptureDeviceType` string. */
@property (nonatomic, readonly) NSString *deviceType;
/** Raw `AVCaptureDevicePosition`: unspecified 0, back 1, front 2. */
@property (nonatomic, readonly) NSInteger position;
@end

/**
 * Watches which cameras face the viewer, from the point of view of a view, through
 * `AVCaptureDeviceDirectionCoordinator` (iOS 27.1, in AVKit). The class is looked up by name, so the
 * observer stays inert on a system without it: [supported] is NO and both lists stay empty.
 */
@interface CRSCameraDirectionObserver : NSObject
- (instancetype)initWithView:(UIView *)view changeHandler:(void (^)(void))changeHandler;
@property (nonatomic, readonly) BOOL supported;
/** Cameras facing the front of the display, empty until the first callback. */
@property (nonatomic, readonly) NSArray<CRSCameraDescriptor *> *forwardFacing;
/** Cameras facing the back of the display, empty until the first callback. */
@property (nonatomic, readonly) NSArray<CRSCameraDescriptor *> *backwardFacing;
/** Takes a direction map from the coordinator. Any object answering the selectors works. */
- (void)handleMap:(id)map;
/** Stops observing and drops the change handler. */
- (void)detach;
@end

NS_ASSUME_NONNULL_END
