package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ExperimentTrial
import com.example.data.GroupStemProjectEntity
import com.example.data.StemRepository
import com.example.data.StudentEntity
import com.example.pdf.PdfReportGenerator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.pow
import kotlin.math.roundToInt

enum class AppScreen {
    ROLE_SELECTION,
    STUDENT_LOGIN,
    TEACHER_LOGIN,
    STUDENT_HOME,
    STUDENT_MATERIAL,
    STUDENT_STEM_GUIDE,
    STUDENT_QUIZ,
    STUDENT_GROUP_PROJECT,
    TEACHER_DASHBOARD
}

enum class StudentTab {
    HOME,
    MATERI,
    PANDUAN_STEM,
    KUIS,
    PROYEK_KELOMPOK
}

enum class TeacherTab {
    RINGKASAN,
    REKAP_SISWA,
    REKAP_KELOMPOK,
    RUBRIK_PENILAIAN
}

class StemViewModel(
    private val repository: StemRepository
) : ViewModel() {

    val allStudents: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allGroups: StateFlow<List<GroupStemProjectEntity>> = repository.allGroupProjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentScreen = MutableStateFlow(AppScreen.ROLE_SELECTION)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentStudent = MutableStateFlow<StudentEntity?>(null)
    val currentStudent: StateFlow<StudentEntity?> = _currentStudent.asStateFlow()

    private val _selectedGroupId = MutableStateFlow(1)
    val selectedGroupId: StateFlow<Int> = _selectedGroupId.asStateFlow()

    private val _teacherTab = MutableStateFlow(TeacherTab.RINGKASAN)
    val teacherTab: StateFlow<TeacherTab> = _teacherTab.asStateFlow()

    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents: SharedFlow<String> = _uiEvents.asSharedFlow()

    // Ohm's Law Simulator State
    val ohmVoltage = MutableStateFlow(6f) // 1 - 24V
    val ohmResistance = MutableStateFlow(20f) // 1 - 100 Ohm

    // Windmill Simulation State
    val simBladeCount = MutableStateFlow(3)
    val simWindSpeed = MutableStateFlow(5f) // m/s (0 - 15)

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setTeacherTab(tab: TeacherTab) {
        _teacherTab.value = tab
    }

    fun loginAsStudent(student: StudentEntity) {
        _currentStudent.value = student
        _selectedGroupId.value = student.groupId
        _currentScreen.value = AppScreen.STUDENT_HOME
    }

    fun loginAsTeacher(pin: String): Boolean {
        // Teacher PIN: default "1997" or "1234"
        return if (pin == "1997" || pin == "1234" || pin == "123456") {
            _currentScreen.value = AppScreen.TEACHER_DASHBOARD
            true
        } else {
            false
        }
    }

    fun logout() {
        _currentStudent.value = null
        _currentScreen.value = AppScreen.ROLE_SELECTION
    }

    fun selectGroupForReview(groupId: Int) {
        _selectedGroupId.value = groupId
    }

    fun submitQuizScore(score: Int) {
        val student = _currentStudent.value ?: return
        viewModelScope.launch {
            repository.updateStudentQuizScore(student.id, score)
            // Update local state
            _currentStudent.value = student.copy(
                quizScore = score,
                quizCompletedAt = System.currentTimeMillis()
            )
            _uiEvents.emit("Selamat! Kuis IPA selesai. Nilai kamu: $score/100")
        }
    }

    fun updateGroupProject(
        groupId: Int,
        bladeMaterial: String,
        bladeLengthCm: Float,
        bladeCount: Int,
        pitchAngleDeg: Float,
        step1: Boolean,
        step2: Boolean,
        step3: Boolean,
        step4: Boolean,
        trials: List<ExperimentTrial>,
        conclusion: String
    ) {
        viewModelScope.launch {
            val existing = repository.getGroupProjectById(groupId) ?: return@launch
            val updated = existing.copy(
                bladeMaterial = bladeMaterial,
                bladeLengthCm = bladeLengthCm,
                bladeCountTested = bladeCount,
                pitchAngleDeg = pitchAngleDeg,
                step1DesignDone = step1,
                step2AssemblyDone = step2,
                step3TestingDone = step3,
                step4AnalysisDone = step4,
                experimentDataJson = ExperimentTrial.listToJsonString(trials),
                conclusionText = conclusion
            )
            repository.updateGroupProject(updated)
            _uiEvents.emit("Progres LKPD Kelompok $groupId berhasil disimpan!")
        }
    }

    fun saveRubricScore(
        groupId: Int,
        science: Int,
        tech: Int,
        eng: Int,
        math: Int,
        notes: String
    ) {
        viewModelScope.launch {
            repository.updateRubricScore(
                groupId = groupId,
                science = science,
                tech = tech,
                eng = eng,
                math = math,
                notes = notes
            )
            _uiEvents.emit("Nilai Rubrik STEM Kelompok $groupId berhasil disimpan!")
        }
    }

    fun generateAndOpenGroupPdf(context: Context, groupId: Int) {
        viewModelScope.launch {
            val group = repository.getGroupProjectById(groupId)
            if (group == null) {
                _uiEvents.emit("Data kelompok tidak ditemukan!")
                return@launch
            }
            val members = allStudents.value.filter { it.groupId == groupId }
            val pdfFile = PdfReportGenerator.generateGroupReportPdf(context, group, members)
            if (pdfFile != null && pdfFile.exists()) {
                PdfReportGenerator.openOrSharePdf(context, pdfFile)
                _uiEvents.emit("Laporan PDF Kelompok $groupId berhasil diunduh!")
            } else {
                _uiEvents.emit("Gagal membuat file PDF.")
            }
        }
    }

    fun generateAndOpenClassRecapPdf(context: Context) {
        viewModelScope.launch {
            val students = allStudents.value
            val groups = allGroups.value
            val pdfFile = PdfReportGenerator.generateClassRecapPdf(context, students, groups)
            if (pdfFile != null && pdfFile.exists()) {
                PdfReportGenerator.openOrSharePdf(context, pdfFile)
                _uiEvents.emit("Rekapitulasi Lengkap Kelas 9 PDF berhasil diunduh!")
            } else {
                _uiEvents.emit("Gagal membuat rekapitulasi PDF.")
            }
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetDatabase()
            _uiEvents.emit("Data berhasil direset ke standar kelas 9!")
        }
    }

    // Helper calculations for interactive simulation
    fun calculateWindmillOutput(speedMps: Float, blades: Int): WindmillSimResult {
        if (speedMps <= 1.0f) {
            return WindmillSimResult(
                rpm = 0,
                voltageVolt = 0f,
                currentMa = 0f,
                powerMw = 0f,
                ledStatus = "Mati",
                efficiencyPercent = 0
            )
        }

        // Efficiency modifier by blade count:
        // 3 blades: high tip-speed ratio, great at medium to high wind
        // 4 blades: balanced start torque & high speed
        // 6 blades: high initial torque at low wind, more drag at high wind
        val torqueFactor = when (blades) {
            3 -> 0.95f
            4 -> 1.05f
            6 -> 1.15f
            else -> 1.0f
        }

        val highSpeedDrag = when (blades) {
            3 -> 1.0f
            4 -> 0.96f
            6 -> 0.88f
            else -> 1.0f
        }

        val effectiveSpeed = (speedMps * torqueFactor * (if (speedMps > 8f) highSpeedDrag else 1.0f))
        val rpm = (effectiveSpeed * 75f).roundToInt().coerceAtMost(1200)

        // Voltage generated by DC motor as generator: V ~ rpm * k
        val rawVolt = (rpm * 0.0042f).coerceAtMost(5.2f)
        val voltage = (rawVolt * 10).roundToInt() / 10f

        // When load LED connected (assume forward voltage threshold = 2.0V)
        val currentMa = if (voltage >= 1.8f) {
            ((voltage - 1.8f) / 0.045f).coerceAtMost(60f)
        } else {
            0f
        }

        val powerMw = (voltage * currentMa * 10).roundToInt() / 10f

        val ledStatus = when {
            voltage < 1.8f -> "Mati"
            voltage < 2.5f -> "Menyala Redup"
            voltage < 3.5f -> "Menyala Terang"
            else -> "Sangat Terang (Optimal)"
        }

        val efficiency = ((powerMw / (0.5f * 1.2f * (speedMps.pow(3)) * 0.04f + 1f)) * 10f)
            .roundToInt().coerceIn(12, 48)

        return WindmillSimResult(
            rpm = rpm,
            voltageVolt = voltage,
            currentMa = (currentMa * 10).roundToInt() / 10f,
            powerMw = powerMw,
            ledStatus = ledStatus,
            efficiencyPercent = efficiency
        )
    }

    companion object {
        fun provideFactory(context: Context): androidx.lifecycle.ViewModelProvider.Factory {
            return object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = AppDatabase.getDatabase(context)
                    val repo = StemRepository(db.studentDao(), db.groupProjectDao())
                    return StemViewModel(repo) as T
                }
            }
        }
    }
}

data class WindmillSimResult(
    val rpm: Int,
    val voltageVolt: Float,
    val currentMa: Float,
    val powerMw: Float,
    val ledStatus: String,
    val efficiencyPercent: Int
)
