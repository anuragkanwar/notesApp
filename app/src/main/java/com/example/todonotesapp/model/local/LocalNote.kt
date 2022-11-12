package com.example.todonotesapp.model.local

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todonotesapp.model.remote.NoteCheckpoints
import com.example.todonotesapp.utils.AppColors.blue
import com.example.todonotesapp.utils.AppColors.brown
import com.example.todonotesapp.utils.AppColors.purple
import com.example.todonotesapp.utils.AppColors.red
import com.example.todonotesapp.utils.AppColors.teal
import com.example.todonotesapp.utils.AppColors.yellow
import java.util.*

@Entity
data class LocalNote(
    var noteTitle: String? = null,
    var description: String? = null,
    var color: Int? = null,
    var date: Long = System.currentTimeMillis(),
    var locked: Boolean = false,
    var label: String? = null,
    var checkpoints: List<NoteCheckpoints>,
    var connected: Boolean = false,
    var locallyDeleted: Boolean = false,

    @PrimaryKey(autoGenerate = false)
    var noteId: String = UUID.randomUUID().toString()
) {
    companion object {
        val noteColors = listOf(blue, brown, purple, red, teal, yellow)

        val noteLabels = listOf<String>(
            "Random",
            "Code",
            "Todo",
            "Work",
            "Design",
            "Travel",
            "Routine",
            "Important",
            "Hobbies",
            "Household",
            "Blog"
        )


        val sampleLocalNote = LocalNote(
            noteTitle = "Travel",
            description = "Perfect time to finally create a list.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\"",
            date = System.currentTimeMillis(),
            locked = false,
            color = noteColors.first().toArgb(),
            label = "Travel",
            checkpoints = listOf(
                NoteCheckpoints(
                    checked = true,
                    "Canada",
                ),
                NoteCheckpoints(
                    checked = true,
                    "Finland Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                ),
                NoteCheckpoints(
                    checked = false,
                    "Norway",
                ),
                NoteCheckpoints(
                    checked = false,
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod",
                )
            )
        )

    }
}
