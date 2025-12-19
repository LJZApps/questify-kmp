package de.ljz.questify.feature.habits.data.models.descriptors

enum class HabitType {
    POSITIVE,
    NEGATIVE;

    companion object {
        fun fromIndex(index: Int): HabitType {
            return entries.getOrElse(index) { POSITIVE }
        }
    }
}