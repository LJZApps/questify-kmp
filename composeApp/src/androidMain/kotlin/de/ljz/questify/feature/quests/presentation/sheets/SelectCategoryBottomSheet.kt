package de.ljz.questify.feature.quests.presentation.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NewLabel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import de.ljz.questify.R
import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectCategoryBottomSheet(
    categories: List<QuestCategoryEntity>,
    onCategorySelect: (QuestCategoryEntity) -> Unit,
    onCreateCategory: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    val filteredLists = categories
        .filter {
            it.text.contains(
                searchText.trim(),
                ignoreCase = true
            )
        }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(R.string.select_category_bottom_sheet_placeholder))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                    ),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    shape = CircleShape,
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
                ) {
                    val showCreateOption = searchText.trim().isNotEmpty() && filteredLists.none {
                        it.text.equals(searchText.trim(), ignoreCase = true)
                    }
                    val showEmptyHint = searchText.trim().isEmpty() && filteredLists.isEmpty()

                    val totalItemCount = if (showEmptyHint) 1 else (if (showCreateOption) 1 else 0) + filteredLists.size
                    var currentItemIndex = 0

                    val singleItemShapes = ListItemShapes(
                        shape = MaterialTheme.shapes.large,
                        selectedShape = MaterialTheme.shapes.large,
                        pressedShape = MaterialTheme.shapes.large,
                        focusedShape = MaterialTheme.shapes.large,
                        hoveredShape = MaterialTheme.shapes.large,
                        draggedShape = MaterialTheme.shapes.large
                    )

                    if (showCreateOption) {
                        SegmentedListItem(
                            onClick = {
                                onCreateCategory(searchText)
                            },
                            content = {
                                Text(
                                    text = stringResource(
                                        R.string.select_category_bottom_sheet_create_list,
                                        searchText
                                    )
                                )
                            },
                            shapes = if (totalItemCount == 1) singleItemShapes else ListItemDefaults.segmentedShapes(
                                index = currentItemIndex,
                                count = totalItemCount
                            ),
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.NewLabel,
                                    contentDescription = null
                                )
                            }
                        )
                        currentItemIndex++
                    }

                    if (showEmptyHint) {
                        SegmentedListItem(
                            onClick = {},
                            content = {
                                Text(
                                    text = stringResource(R.string.select_category_bottom_sheet_empty_list_hint)
                                )
                            },
                            shapes = singleItemShapes,
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null
                                )
                            }
                        )
                    } else {
                        filteredLists.forEach { list ->
                            SegmentedListItem(
                                onClick = {
                                    onCategorySelect(list)
                                },
                                content = {
                                    Text(
                                        text = list.text
                                    )
                                },
                                shapes = if (totalItemCount == 1) singleItemShapes else ListItemDefaults.segmentedShapes(
                                    index = currentItemIndex,
                                    count = totalItemCount
                                ),
                                colors = ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.Label,
                                        contentDescription = null
                                    )
                                }
                            )
                            currentItemIndex++
                        }
                    }
                }
            }
        }
    }
}