package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_projects")
data class GroupStemProjectEntity(
    @PrimaryKey
    val groupId: Int,
    val groupName: String,
    val bladeMaterial: String = "Plastik Botol / Karton",
    val bladeLengthCm: Float = 12f,
    val bladeCountTested: Int = 4,
    val pitchAngleDeg: Float = 30f,
    val step1DesignDone: Boolean = false,
    val step2AssemblyDone: Boolean = false,
    val step3TestingDone: Boolean = false,
    val step4AnalysisDone: Boolean = false,
    val experimentDataJson: String = "",
    val conclusionText: String = "",
    val scienceScore: Int = 0, // max 25
    val techScore: Int = 0,    // max 25
    val engScore: Int = 0,     // max 25
    val mathScore: Int = 0,    // max 25
    val teacherNotes: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val totalRubricScore: Int
        get() = scienceScore + techScore + engScore + mathScore

    val progressPercent: Int
        get() {
            var count = 0
            if (step1DesignDone) count++
            if (step2AssemblyDone) count++
            if (step3TestingDone) count++
            if (step4AnalysisDone) count++
            return (count * 25)
        }
}
