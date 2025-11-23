// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "KdumaAutoidCapacitorNotificationLight",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "KdumaAutoidCapacitorNotificationLight",
            targets: ["NotificationLightPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "NotificationLightPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/NotificationLightPlugin"),
        .testTarget(
            name: "NotificationLightPluginTests",
            dependencies: ["NotificationLightPlugin"],
            path: "ios/Tests/NotificationLightPluginTests")
    ]
)