//
//  CreateQuestViewModel.swift
//  Questify
//
//  Created by Leon Zapke on 17.12.25.
//
import Foundation
import SharedKMP

@MainActor
class CreateQuestViewModel: ObservableObject {
    let deps = SharedKMP.KoinDependencies()
    
    @Published var title: String = ""
    @Published var notes: String = ""
    
    func createQuest(onSuccess: @escaping () -> Void) {
        guard !title.isEmpty else { return }
        
        Task {
            do {
                QuestEntity
                /*
                try await dependencies.upsertQuestUseCase.invoke(
                    title: title,
                    notes: notes
                )
                
                onSuccess()
                 */
            }
        }
    }
}
