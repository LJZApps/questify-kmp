//
//  CreateQuestSheet.swift
//  Questify
//
//  Created by Leon Zapke on 24.11.25.
//
import SwiftUI
import SharedKMP

struct CreateQuestSheet: View {
    @Environment(\.dismiss) var dismiss
    
    private let viewModel: CreateQuestViewModel = ProvideViewModel.shared.getCreateQuestViewModel(selectedQuestCategoryIndex: nil)
    
    @State private var state: CreateQuestUiState
    @State private var title: String = ""
    @State private var notes: String = ""
    
    init() {
        _state = State(initialValue: ProvideViewModel.shared.getCreateQuestViewModel(selectedQuestCategoryIndex: nil).uiState.value)
    }
    
    var body: some View {
        NavigationStack {
            Form {
                Section {
                    TextField("Titel", text: $title)
                    
                    TextField("Notizen", text: $notes, axis: .vertical)
                        .lineLimit(3...5)
                }
            }
            .navigationTitle("Quest erstellen")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button(action: { dismiss() }) { Image(systemName: "xmark") }
                }
                
                ToolbarItem(placement: .topBarTrailing) {
                    Button(action: {
                        viewModel.onUiEvent(event: CreateQuestUiEventOnCreateQuest())
                        dismiss()
                    }) {
                        Image(systemName: "checkmark")
                    }
                }
            }
        }
    }
}
