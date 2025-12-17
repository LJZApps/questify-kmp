import SwiftUI
@_exported import SharedKMP

@main
struct iOSApp: App {
    
    init() {
        initKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            HomeScreen()
        }
    }
}
