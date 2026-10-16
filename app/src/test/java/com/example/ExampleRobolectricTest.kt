package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PresetData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("STEM Kincir Angin", appName)
    }

    @Test
    fun `verify 32 students and 8 groups preset`() {
        val students = PresetData.initialStudents
        assertEquals("Total murid minimal 32", 32, students.size)

        val groups = PresetData.getInitialGroupProjects()
        assertEquals("Total kelompok STEM adalah 8", 8, groups.size)

        // Verify each group has students
        for (groupId in 1..8) {
            val members = students.filter { it.groupId == groupId }
            assertEquals("Kelompok $groupId memiliki 4 anggota", 4, members.size)
        }

        val questions = PresetData.quizQuestions
        assertEquals("Total soal kuis IPA adalah 10", 10, questions.size)
    }
}
