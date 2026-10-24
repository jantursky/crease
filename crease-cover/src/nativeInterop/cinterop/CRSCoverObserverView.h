#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

/**
 * A transparent view that registers a camera capture scene accessory (iOS 27.1) for its host view
 * controller, watches whether the system makes it available and hosts the content once the system
 * presents the accessory scene.
 *
 * Nothing from iOS 27 is named at build time. The system classes are looked up by name and every send is
 * guarded by a responds check, so the view stays inert on a system without the API: [supported] is NO.
 */
@interface CRSCoverObserverView : UIView
/**
 * [contentFactory] is called once for each accessory scene the system connects and returns the view
 * controller to show in it. [changeHandler] runs when [available] or [presenting] changes.
 */
- (instancetype)initWithHost:(UIViewController *)host
              contentFactory:(UIViewController *_Nullable (^)(void))contentFactory
               changeHandler:(void (^)(void))changeHandler;
/** Whether the system has scene accessories for camera capture and the registration succeeded. */
@property (nonatomic, readonly) BOOL supported;
/** Whether the system would present the accessory now, as the registration reports it. */
@property (nonatomic, readonly) BOOL available;
/** Whether an accessory scene is connected and showing the content. */
@property (nonatomic, readonly) BOOL presenting;
/** Whether the app wants the content shown. The system still decides whether it is. */
@property (nonatomic) BOOL enabled;
/** Called by the accessory scene's delegate when the system takes the scene away. */
- (void)sceneDisconnected;
/** Unregisters the accessory, drops the handlers and leaves the superview. */
- (void)detach;
@end

NS_ASSUME_NONNULL_END
