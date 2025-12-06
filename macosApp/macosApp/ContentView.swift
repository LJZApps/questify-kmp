//
//  ContentView.swift
//  macosApp
//
//  Created by Leon Zapke on 06.12.25.
//

import SwiftUI
import SharedKMP

struct ContentView: View {
    @State private var vibrateOnRing = false
    
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text("Hello, world!")
            Button("Hallo") {
                
            }
            
            Toggle(isOn: $vibrateOnRing) {
                Text("Vibrate on Ring")
            }
            .toggleStyle(SwitchToggleStyle())
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
