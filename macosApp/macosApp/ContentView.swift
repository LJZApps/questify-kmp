//
//  ContentView.swift
//  macosApp
//
//  Created by Leon Zapke on 06.12.25.
//

import SwiftUI

struct ContentView: View {
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text("Hello, world!")
            Button("Hallo") {
                
            }
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
