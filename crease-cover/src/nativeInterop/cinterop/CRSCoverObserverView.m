#import "CRSCoverObserverView.h"

/*
 * Registers a camera capture scene accessory for the iPhone Duo outer display (iOS 27.1) through the
 * Objective-C runtime. The shapes of the iOS 27 classes are declared in private protocols below, so the
 * compiler emits ordinary message sends and the build needs no iOS 27 SDK.
 */

@protocol CRSSceneAccessoryFactory
+ (id)cameraCaptureSceneAccessoryWithConfiguration:(id)configuration userInfo:(id)userInfo;
@end

@protocol CRSSceneAccessoryHost
- (id)registerSceneAccessory:(id)accessory;
- (void)unregisterSceneAccessory:(id)registration;
@end

@protocol CRSSceneAccessoryRegistration
- (BOOL)isAvailable;
- (BOOL)isEnabled;
- (void)setEnabled:(BOOL)enabled;
@end

@protocol CRSSceneConnectionOptions
- (nullable id)sceneAccessoryUserInfo;
@end

@interface CRSCoverObserverView ()
@property (nonatomic, weak) UIViewController *host;
@property (nonatomic, copy, nullable) UIViewController *_Nullable (^contentFactory)(void);
@property (nonatomic, copy, nullable) void (^changeHandler)(void);
@property (nonatomic, strong, nullable) id registration;
@property (nonatomic, strong, nullable) UIWindow *window_;
@property (nonatomic) BOOL lastAvailable;
- (void)sceneConnected:(UIWindowScene *)scene;
- (void)notifyChange;
@end

/** Rides along as the user info of the accessory, so the scene delegate can find the view again. */
@interface CRSCoverPayload : NSObject
@property (nonatomic, weak) CRSCoverObserverView *observer;
@end
@implementation CRSCoverPayload
@end

/** The delegate of the accessory scene: puts the content in a window and tells the view about it. */
@interface CRSCoverSceneDelegate : UIResponder <UIWindowSceneDelegate>
@property (nonatomic, weak) CRSCoverObserverView *observer;
@end

@implementation CRSCoverSceneDelegate

- (void)scene:(UIScene *)scene willConnectToSession:(UISceneSession *)session options:(UISceneConnectionOptions *)options {
    if (![scene isKindOfClass:[UIWindowScene class]]) return;
    id info = [options respondsToSelector:@selector(sceneAccessoryUserInfo)]
        ? [(id<CRSSceneConnectionOptions>)options sceneAccessoryUserInfo]
        : nil;
    if (![info isKindOfClass:[CRSCoverPayload class]]) return;
    _observer = ((CRSCoverPayload *)info).observer;
    [_observer sceneConnected:(UIWindowScene *)scene];
}

- (void)sceneDidDisconnect:(UIScene *)scene {
    [_observer sceneDisconnected];
}

@end

@implementation CRSCoverObserverView

- (instancetype)initWithHost:(UIViewController *)host
              contentFactory:(UIViewController *_Nullable (^)(void))contentFactory
               changeHandler:(void (^)(void))changeHandler {
    if ((self = [super initWithFrame:CGRectZero])) {
        _host = host;
        _contentFactory = [contentFactory copy];
        _changeHandler = [changeHandler copy];
        self.userInteractionEnabled = NO;
        self.backgroundColor = UIColor.clearColor;
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
        [self registerAccessory];
    }
    return self;
}

- (void)registerAccessory {
    Class accessoryClass = NSClassFromString(@"UISceneAccessory");
    SEL factory = @selector(cameraCaptureSceneAccessoryWithConfiguration:userInfo:);
    if (accessoryClass == Nil || ![accessoryClass respondsToSelector:factory]) return;
    if (![_host respondsToSelector:@selector(registerSceneAccessory:)]) return;
    Class configurationClass = NSClassFromString(@"UISceneConfiguration");
    UISceneConfiguration *configuration = [[configurationClass alloc] init];
    configuration.delegateClass = [CRSCoverSceneDelegate class];
    CRSCoverPayload *payload = [[CRSCoverPayload alloc] init];
    payload.observer = self;
    id accessory = [(id<CRSSceneAccessoryFactory>)accessoryClass cameraCaptureSceneAccessoryWithConfiguration:configuration
                                                                                                      userInfo:payload];
    if (accessory == nil) return;
    id registration = [(id<CRSSceneAccessoryHost>)_host registerSceneAccessory:accessory];
    if (registration == nil) return;
    _registration = registration;
    // Shown only once the app asks for it with `enabled`.
    [(id<CRSSceneAccessoryRegistration>)registration setEnabled:NO];
    _supported = YES;
    _lastAvailable = [(id<CRSSceneAccessoryRegistration>)registration isAvailable];
}

- (BOOL)available {
    return _registration != nil && [(id<CRSSceneAccessoryRegistration>)_registration isAvailable];
}

- (BOOL)enabled {
    return _registration != nil && [(id<CRSSceneAccessoryRegistration>)_registration isEnabled];
}

- (void)setEnabled:(BOOL)enabled {
    if (_registration == nil) return;
    [(id<CRSSceneAccessoryRegistration>)_registration setEnabled:enabled];
}

// UIKit tracks the properties read here and calls this again when `available` changes.
- (void)updateProperties {
    if (@available(iOS 26.0, *)) {
        [super updateProperties];
    }
    [self reportAvailability];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    [self reportAvailability];
}

- (void)reportAvailability {
    BOOL available = self.available;
    if (available == _lastAvailable) return;
    _lastAvailable = available;
    [self notifyChange];
}

- (void)sceneConnected:(UIWindowScene *)scene {
    UIViewController *content = _contentFactory != nil ? _contentFactory() : nil;
    if (content != nil) {
        UIWindow *window = [[UIWindow alloc] initWithWindowScene:scene];
        window.rootViewController = content;
        [window makeKeyAndVisible];
        _window_ = window;
    }
    _presenting = _window_ != nil;
    [self notifyChange];
}

- (void)sceneDisconnected {
    _window_.hidden = YES;
    _window_ = nil;
    _presenting = NO;
    [self notifyChange];
}

- (void)detach {
    if (_registration != nil && [_host respondsToSelector:@selector(unregisterSceneAccessory:)]) {
        [(id<CRSSceneAccessoryHost>)_host unregisterSceneAccessory:_registration];
    }
    _registration = nil;
    _window_.hidden = YES;
    _window_ = nil;
    // The handlers reach back into Kotlin; dropping them breaks the cycle through the view.
    _contentFactory = nil;
    _changeHandler = nil;
    [self removeFromSuperview];
}

- (void)notifyChange {
    if (_changeHandler != nil) _changeHandler();
}

@end
