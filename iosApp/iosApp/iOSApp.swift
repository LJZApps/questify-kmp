import SwiftUI
import SharedKMP

@main
struct iOSApp: App {
    
    init() {
        HelperKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
