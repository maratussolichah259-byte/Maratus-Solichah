package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentNumber: Int,
    val name: String,
    val className: String = "Kelas 9-A",
    val groupId: Int,
    val quizScore: Int? = null,
    val quizCompletedAt: Long? = null
)
