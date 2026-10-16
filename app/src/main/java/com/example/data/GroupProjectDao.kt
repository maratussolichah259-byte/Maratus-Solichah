package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupProjectDao {
    @Query("SELECT * FROM group_projects ORDER BY groupId ASC")
    fun getAllGroupProjects(): Flow<List<GroupStemProjectEntity>>

    @Query("SELECT * FROM group_projects WHERE groupId = :groupId LIMIT 1")
    fun getGroupProjectFlow(groupId: Int): Flow<GroupStemProjectEntity?>

    @Query("SELECT * FROM group_projects WHERE groupId = :groupId LIMIT 1")
    suspend fun getGroupProjectById(groupId: Int): GroupStemProjectEntity?

    @Query("SELECT COUNT(*) FROM group_projects")
    suspend fun getGroupCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupProjects(groups: List<GroupStemProjectEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupProject(group: GroupStemProjectEntity)

    @Update
    suspend fun updateGroupProject(group: GroupStemProjectEntity)

    @Query("UPDATE group_projects SET scienceScore = :s, techScore = :t, engScore = :e, mathScore = :m, teacherNotes = :notes, lastUpdated = :now WHERE groupId = :groupId")
    suspend fun updateRubricScore(groupId: Int, s: Int, t: Int, e: Int, m: Int, notes: String, now: Long)
}
