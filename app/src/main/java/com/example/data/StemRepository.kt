package com.example.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class StemRepository(
    private val studentDao: StudentDao,
    private val groupProjectDao: GroupProjectDao
) {
    val allStudents: Flow<List<StudentEntity>> = studentDao.getAllStudents()
    val allGroupProjects: Flow<List<GroupStemProjectEntity>> = groupProjectDao.getAllGroupProjects()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabaseIfEmpty()
        }
    }

    suspend fun seedDatabaseIfEmpty() {
        val studentCount = studentDao.getStudentCount()
        if (studentCount == 0) {
            studentDao.insertStudents(PresetData.initialStudents)
        }

        val groupCount = groupProjectDao.getGroupCount()
        if (groupCount == 0) {
            groupProjectDao.insertGroupProjects(PresetData.getInitialGroupProjects())
        }
    }

    suspend fun resetDatabase() {
        studentDao.insertStudents(PresetData.initialStudents)
        groupProjectDao.insertGroupProjects(PresetData.getInitialGroupProjects())
    }

    fun getStudentsByGroup(groupId: Int): Flow<List<StudentEntity>> {
        return studentDao.getStudentsByGroup(groupId)
    }

    suspend fun getStudentById(id: Int): StudentEntity? {
        return studentDao.getStudentById(id)
    }

    suspend fun getStudentByNumber(number: Int): StudentEntity? {
        return studentDao.getStudentByNumber(number)
    }

    suspend fun updateStudentQuizScore(studentId: Int, score: Int) {
        studentDao.updateQuizScore(studentId, score, System.currentTimeMillis())
    }

    fun getGroupProjectFlow(groupId: Int): Flow<GroupStemProjectEntity?> {
        return groupProjectDao.getGroupProjectFlow(groupId)
    }

    suspend fun getGroupProjectById(groupId: Int): GroupStemProjectEntity? {
        return groupProjectDao.getGroupProjectById(groupId)
    }

    suspend fun updateGroupProject(project: GroupStemProjectEntity) {
        groupProjectDao.updateGroupProject(project.copy(lastUpdated = System.currentTimeMillis()))
    }

    suspend fun updateRubricScore(
        groupId: Int,
        science: Int,
        tech: Int,
        eng: Int,
        math: Int,
        notes: String
    ) {
        groupProjectDao.updateRubricScore(
            groupId = groupId,
            s = science,
            t = tech,
            e = eng,
            m = math,
            notes = notes,
            now = System.currentTimeMillis()
        )
    }
}
