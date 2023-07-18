package ir.amirroid.todo.models.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ir.amirroid.todo.utils.Constants
import kotlinx.parcelize.Parcelize


@Entity(
    Constants.TASK, foreignKeys = [
        ForeignKey(
            CategoryInfo::class,
            arrayOf("id"),
            arrayOf("categoryId"),
            ForeignKey.CASCADE,
        )
    ]
)
@Parcelize
data class TaskInfo(
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    val categoryId: Int,
    val title: String,
    val isDone: Boolean,
    val dateCreated: Long = System.currentTimeMillis(),
    val startDate: Long,
    val endDate: Long,
    val startTime: Long,
    val endTime: Long,
    val isFavorite: Boolean,
    val alarmMode: Int
) : Parcelable