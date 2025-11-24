//
//  QuestOverviewScreen.swift
//  iosApp
//
//  Created by Leon Zapke on 24.11.25.
//
import SwiftUI
import SharedKMP

struct QuestOverviewScreen: View {
    private let viewModel: QuestOverviewViewModel = ProvideViewModel.shared.getQuestOverviewViewModel()
    private let createQuestViewModel: CreateQuestViewModel = ProvideViewModel.shared.getCreateQuestViewModel(selectedQuestCategoryIndex: nil)

    @State private var state: QuestOverviewUIState
    
    init() {
        _state = State(initialValue: ProvideViewModel.shared.getQuestOverviewViewModel().uiState.value)
        
        createQuestViewModel.onUiEvent(event: CreateQuestUiEventOnTitleUpdated(value: "Test-Quest"))
        createQuestViewModel.onUiEvent(event: CreateQuestUiEventOnDescriptionUpdated(value: "Test-Beschreibung mit langem Text hahahahahaha"))
        createQuestViewModel.onUiEvent(event: CreateQuestUiEventOnCreateQuest())
    }
    
    var body: some View {
        VStack {
            if state.allQuestPageState.quests.isEmpty {
                ContentUnavailableView("Keine Quests", systemImage: "list.bullet.clipboard")
            } else {
                List(state.allQuestPageState.quests, id: \.quest.id) { questWithSub in
                    HStack {
                        Image(systemName: questWithSub.quest.done ? "checkmark.circle.fill" : "circle")
                            .foregroundColor(questWithSub.quest.done ? .green : .gray)
                            .onTapGesture {
                                viewModel.onUiEvent(event: QuestOverviewUiEventOnQuestChecked(questEntity: questWithSub.quest))
                            }
                        
                        VStack(alignment: .leading) {
                            Text(questWithSub.quest.title)
                                .font(.headline)
                                .strikethrough(questWithSub.quest.done)
                            
                            if let notes = questWithSub.quest.notes {
                                Text(notes)
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                        }
                    }
                    .swipeActions {
                        Button(role: .destructive) {
                            viewModel.onUiEvent(event: QuestOverviewUiEventOnQuestDelete(id: questWithSub.quest.id))
                        } label: {
                            Label("Löschen", systemImage: "trash")
                        }
                    }
                    .contextMenu {
                        Group {
                            Button(
                                "Löschen",
                                role: .destructive,
                                action: {
                                    viewModel.onUiEvent(event: QuestOverviewUiEventOnQuestDelete(id: questWithSub.quest.id))
                                }
                            )
                        }
                    }
                }
            }
        }
        .navigationTitle("Quests")
        .task {
            for await newState in viewModel.uiState {
                withAnimation {
                    self.state = newState
                }
            }
        }
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: {}) {
                    Image(systemName: "plus")
                }
            }
            
            ToolbarItem(placement: .topBarTrailing) {
                Menu {
                    Button(action: {
                        viewModel.onUiEvent(event: QuestOverviewUiEventShowDialog(dialogState: DialogState___.SortingBottomSheet()))
                    }) {
                        Label("Sort", systemImage: "line.3.horizontal.decrease.circle")
                    }
                } label: {
                    Image(systemName: "ellipsis.circle")
                }
            }
        }
        .sheet(isPresented: Binding(
            get: {
                // LESEN: Ist der aktuelle State genau dieser Dialog?
                state.dialogState is DialogState___.SortingBottomSheet
            },
            set: { isPresented in
                // SCHREIBEN: Wenn SwiftUI das Sheet schließen will (isPresented == false)
                if !isPresented {
                    // Sende das Event an Kotlin, um den State zu resetten
                    // WICHTIG: Prüfe in 'QuestOverviewUiEvent.kt' wie dein Dismiss-Event heißt!
                    viewModel.onUiEvent(event: QuestOverviewUiEventCloseDialog())
                }
            }
        )) {
            SortingBottomSheet()
        }
    }
}

struct SortingBottomSheet: View {
    var body: some View {
        Text("Hallo")
    }
}
