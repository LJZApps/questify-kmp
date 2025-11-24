//
//  HomeScene.swift
//  iosApp
//
//  Created by Leon Zapke on 24.11.25.
//
import SwiftUI
import SharedKMP


struct HomeScreen: View {
    @State private var path = NavigationPath()

    var body: some View {
        NavigationStack(path: $path) {
            List {
                Section(header: Text("Missions")) {
                    NavigationLink(value: AppScreen.questOverview) {
                        Label("Quests", systemImage: "checkmark.circle")
                    }
                }
            }
            .frame(minWidth: 200)
            .listStyle(.sidebar)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button(action: {
                        path.append(AppScreen.settingsMain)
                    }) {
                        Image(systemName: "gear")
                    }
                }
            }
            .navigationTitle("Questify")
            .navigationDestination(for: AppScreen.self) { destination in
                switch destination {
                    case .questOverview:
                        QuestOverviewScreen()
                    case .settingsMain:
                        SettingsMainScreen()
                }
            }
        }
    }
}
