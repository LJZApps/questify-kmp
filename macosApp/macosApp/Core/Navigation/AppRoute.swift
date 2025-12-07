//
//  AppRoute.swift
//  Questify
//
//  Created by Leon Zapke on 06.12.25.
//

enum AppRoute: String, CaseIterable, Identifiable {
    case quests
    case habits
    
    var id: String { self.rawValue }
    
    var title: String {
        switch self {
        case .quests: return "Quests"
        case .habits: return "Habits"
        }
    }
    
    var icon: String {
        switch self {
        case .quests: return "checkmark.circle"
        case .habits: return "leaf"
        }
    }
}
