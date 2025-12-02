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
    @State private var showingCreateQuestSheet = false
    
    init() {
        _state = State(initialValue: ProvideViewModel.shared.getQuestOverviewViewModel().uiState.value)
    }
    
    var body: some View {
        VStack {
            if state.allQuestPageState.quests.isEmpty {
                ContentUnavailableView {
                    Label("Du hast noch keine Quests erstellt.", systemImage: "checkmark.circle")
                } description: {
                    Button(action: {
                        showingCreateQuestSheet = true
                    }) {
                        Text("Jetzt eine Quest erstellen")
                    }
                }
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
                            viewModel.onUiEvent(event: QuestOverviewUiEventShowDialog(questOverviewDialogState: .DeleteQuestConfirmation(id: questWithSub.quest.id)))
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
                                    viewModel.onUiEvent(event: QuestOverviewUiEventShowDialog(questOverviewDialogState: .DeleteQuestConfirmation(id: questWithSub.quest.id)))
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
            ToolbarItem {
                Button(action: {
                    showingCreateQuestSheet = true
                }) {
                    Image(systemName: "plus")
                }
            }
            
            ToolbarItem {
                Menu {
                    Section("Sortierung") {
                        Button {
                            viewModel.onUiEvent(event: QuestOverviewUiEventUpdateQuestSortingDirection(sortingDirections: .ascending))
                        } label: {
                            Label("Aufsteigend", systemImage: "arrow.up")
                        }
                        .disabled(state.allQuestPageState.sortingDirections == .ascending)

                        Button {
                            viewModel.onUiEvent(event: QuestOverviewUiEventUpdateQuestSortingDirection(sortingDirections: .descending))
                        } label: {
                            Label("Absteigend", systemImage: "arrow.down")
                        }
                        .disabled(state.allQuestPageState.sortingDirections == .descending)
                    }

                    Section("Filter") {
                        Toggle(isOn: Binding(
                            get: { state.allQuestPageState.showCompleted },
                            set: { newValue in
                                viewModel.onUiEvent(event: QuestOverviewUiEventUpdateShowCompletedQuests(value: newValue))
                            }
                        )) {
                            Label("Erledigte anzeigen", systemImage: "checkmark.circle")
                        }
                    }
                    
                } label: {
                    Image(systemName: "line.3.horizontal.decrease.circle")
                }
            }
        }
        .sheet(isPresented: $showingCreateQuestSheet) {
            CreateQuestSheet()
        }
        .alert(
            "Quest löschen?",
            isPresented: Binding(
                get: {
                    state.dialogState is QuestOverviewDialogState.DeleteQuestConfirmation
                },
                set: { isPresented in
                    if !isPresented {
                        viewModel.onUiEvent(event: QuestOverviewUiEventCloseDialog())
                    }
                }
            ),
        ) {o
            Button("Löschen", role: .destructive) {
                if let deleteState = state.dialogState as? QuestOverviewDialogState.DeleteQuestConfirmation {
                    viewModel.onUiEvent(event: QuestOverviewUiEventOnQuestDelete(id: deleteState.id))
                }
                viewModel.onUiEvent(event: QuestOverviewUiEventCloseDialog())
            }

            Button("Abbrechen", role: .cancel) {
                viewModel.onUiEvent(event: QuestOverviewUiEventCloseDialog())
            }
        }
    }
}
