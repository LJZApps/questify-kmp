//
//  HomeScene.swift
//  iosApp
//
//  Created by Leon Zapke on 24.11.25.
//
import SwiftUI
import SharedKMP


struct HomeScreen: View {
    @State private var selection: AppRoute? = .quests

    var body: some View {
        NavigationSplitView {
            List(AppRoute.allCases, selection: $selection) { route in
                NavigationLink(value: route) {
                    Label(route.title, systemImage: route.icon)
                }
            }
            .navigationTitle("Questify")
            .listStyle(.sidebar)
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Button(action: {
                        // z.B. Sidebar einklappen oder Filter
                    }) {
                        Label("Settings", systemImage: "gear")
                    }
                }
            }
        } detail: {
            if let route = selection {
                switch route {
                case .quests:
                    ContentUnavailableView {
                        Label("You haven't created any quests yet.", systemImage: "checkmark.circle")
                    } description: {
                        Button(action: {
                            
                        }) {
                            Text("Create one now")
                        }
                    }
                }
            } else {
                Text("Wähle einen Menüpunkt")
            }
        }
        .toolbar {
            ToolbarItem(placement: .primaryAction) {
                Button(action: {
                    
                }) {
                    Label("Create quest", systemImage: "plus")
                }
            }
        }
        .navigationTitle("Quests")
    }
}
