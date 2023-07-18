package ir.amirroid.todo.models.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.amirroid.todo.utils.Constants


@Entity(Constants.CATEGORY)
data class CategoryInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String
)