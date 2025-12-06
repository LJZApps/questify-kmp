//
//  macosAppApp.swift
//  macosApp
//
//  Created by Leon Zapke on 06.12.25.
//

import SwiftUI
import SharedKMP

@main
struct macosApp: App {
    
    init() {
        HelperKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            HomeScreen()
        }
        .commands {
            CommandMenu("Quests") {
                Button("Neue Quest erstellen") {
                    // Logik zum Öffnen des Sheets (das geht am besten über Window-Focus oder NotificationCenter)
                }
                .keyboardShortcut("n", modifiers: [.command])
                
                Divider()
                
                Button("Alle als erledigt markieren") {
                    // ...
                }
            }
        }
        
        Settings {
            
        }
    }
}
