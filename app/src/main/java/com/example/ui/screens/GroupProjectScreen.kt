package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExperimentTrial
import com.example.data.GroupStemProjectEntity
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.StemViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupProjectScreen(
    viewModel: StemViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allGroups by viewModel.allGroups.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val currentStudent by viewModel.currentStudent.collectAsState()
    val selectedGroupId by viewModel.selectedGroupId.collectAsState()

    // Find group
    val targetGroupId = currentStudent?.groupId ?: selectedGroupId
    val group = allGroups.find { it.groupId == targetGroupId }
    val members = allStudents.filter { it.groupId == targetGroupId }

    BackHandler {
        if (currentStudent != null) {
            viewModel.navigateTo(AppScreen.STUDENT_HOME)
        } else {
            viewModel.navigateTo(AppScreen.TEACHER_DASHBOARD)
        }
    }

    if (group == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Memuat data kelompok...")
        }
        return
    }

    // Local form states initialized from entity
    var bladeMaterial by remember(group.groupId) { mutableStateOf(group.bladeMaterial) }
    var bladeLengthText by remember(group.groupId) { mutableStateOf(group.bladeLengthCm.toString()) }
    var pitchAngleText by remember(group.groupId) { mutableStateOf(group.pitchAngleDeg.toString()) }

    var step1 by remember(group.groupId) { mutableStateOf(group.step1DesignDone) }
    var step2 by remember(group.groupId) { mutableStateOf(group.step2AssemblyDone) }
    var step3 by remember(group.groupId) { mutableStateOf(group.step3TestingDone) }
    var step4 by remember(group.groupId) { mutableStateOf(group.step4AnalysisDone) }

    var conclusion by remember(group.groupId) { mutableStateOf(group.conclusionText) }

    val trialsList = remember(group.groupId, group.experimentDataJson) {
        mutableStateListOf<ExperimentTrial>().apply {
            addAll(ExperimentTrial.listFromJsonString(group.experimentDataJson))
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "LKPD Kelompok $targetGroupId",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = group.groupName,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentStudent != null) viewModel.navigateTo(AppScreen.STUDENT_HOME)
                            else viewModel.navigateTo(AppScreen.TEACHER_DASHBOARD)
                        },
                        modifier = Modifier.testTag("btn_back_group_project")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.generateAndOpenGroupPdf(context, targetGroupId)
                        },
                        modifier = Modifier.testTag("btn_top_download_group_pdf")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Unduh PDF Kelompok",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Group Header Card & Members
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = group.groupName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Progres: ${group.progressPercent}% Selesai",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F766E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Anggota Kelompok (${members.size} Siswa):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    members.forEach { m ->
                        Text(
                            text = "• Absen ${m.studentNumber}. ${m.name} ${if (m.quizScore != null) "(Kuis: ${m.quizScore})" else ""}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // PDF Download Big Button
                    Button(
                        onClick = { viewModel.generateAndOpenGroupPdf(context, targetGroupId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_download_group_pdf"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Unduh Laporan Progres Kelompok (PDF Otomatis)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Progress Checklist
            Text(
                text = "1. CHECKLIST TAHAPAN KERJA PROYEK",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    StepCheckRow("Tahap 1: Desain & Potong Baling-Baling Kincir", step1) { step1 = it }
                    StepCheckRow("Tahap 2: Perakitan Tiang Menara & Pemasangan Dinamo DC", step2) { step2 = it }
                    StepCheckRow("Tahap 3: Pembuatan Rangkaian & Uji Multimeter / LED", step3) { step3 = it }
                    StepCheckRow("Tahap 4: Pengolahan Data Eksperimen & Analisis Efisiensi", step4) { step4 = it }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Design Specifications
            Text(
                text = "2. SPESIFIKASI RANCANG BANGUN KINCIR",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = bladeMaterial,
                        onValueChange = { bladeMaterial = it },
                        label = { Text("Bahan Pembuatan Bilah Kincir") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_blade_material"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = bladeLengthText,
                            onValueChange = { bladeLengthText = it },
                            label = { Text("Panjang Bilah (cm)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_blade_length"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = pitchAngleText,
                            onValueChange = { pitchAngleText = it },
                            label = { Text("Sudut Pitch (°)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_pitch_angle"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Experiment Trials Table
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "3. TABEL DATA HASIL PENGUJIAN",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedButton(
                    onClick = {
                        val newTrialNumber = trialsList.size + 1
                        trialsList.add(
                            ExperimentTrial(
                                trialNumber = newTrialNumber,
                                bladeCount = 4,
                                windSpeed = "Sedang (5 m/s)",
                                voltageVolt = 3.0f,
                                currentMa = 30f,
                                powerMw = 90f,
                                ledStatus = "Menyala Terang",
                                notes = "Uji coba tambahan"
                            )
                        )
                    },
                    modifier = Modifier.testTag("btn_add_trial_row")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah Uji", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (trialsList.isEmpty()) {
                Text(
                    text = "Belum ada data pengujian. Klik tombol 'Tambah Uji' di atas.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                trialsList.forEachIndexed { index, trial ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Uji #${trial.trialNumber}: ${trial.bladeCount} Bilah • ${trial.windSpeed}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                IconButton(
                                    onClick = { trialsList.removeAt(index) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus Baris",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Tegangan: ${trial.voltageVolt} V",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFD97706)
                                )
                                Text(
                                    text = "Arus: ${trial.currentMa} mA",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0284C7)
                                )
                                Text(
                                    text = "Daya: ${trial.powerMw} mW",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF9333EA)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Kondisi LED: ${trial.ledStatus} | Catatan: ${trial.notes}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Conclusion & Discussion
            Text(
                text = "4. ANALISIS & KESIMPULAN KELOMPOK",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = conclusion,
                        onValueChange = { conclusion = it },
                        label = { Text("Tuliskan analisis hasil pengujian & kesimpulan kelompok...") },
                        minLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_conclusion"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save Progress Button
            Button(
                onClick = {
                    val bladeLen = bladeLengthText.toFloatOrNull() ?: 12f
                    val pitchAngle = pitchAngleText.toFloatOrNull() ?: 30f
                    viewModel.updateGroupProject(
                        groupId = targetGroupId,
                        bladeMaterial = bladeMaterial,
                        bladeLengthCm = bladeLen,
                        bladeCount = 4,
                        pitchAngleDeg = pitchAngle,
                        step1 = step1,
                        step2 = step2,
                        step3 = step3,
                        step4 = step4,
                        trials = trialsList.toList(),
                        conclusion = conclusion
                    )
                    Toast.makeText(context, "Progres LKPD Kelompok $targetGroupId berhasil disimpan!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_save_group_progress"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Data LKPD Kelompok", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Teacher Rubric Section (If Graded)
            if (group.totalRubricScore > 0) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Penilaian Guru (Bu Maratus Sholichah, S.Pd.)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78350F)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sains: ${group.scienceScore}/25", fontSize = 12.sp, color = Color(0xFF92400E))
                            Text("Teknologi: ${group.techScore}/25", fontSize = 12.sp, color = Color(0xFF92400E))
                            Text("Engineering: ${group.engScore}/25", fontSize = 12.sp, color = Color(0xFF92400E))
                            Text("Matematika: ${group.mathScore}/25", fontSize = 12.sp, color = Color(0xFF92400E))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "TOTAL NILAI STEM: ${group.totalRubricScore}/100",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFB45309)
                        )
                        if (group.teacherNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Catatan: ${group.teacherNotes}",
                                fontSize = 12.sp,
                                color = Color(0xFF78350F)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun StepCheckRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0F766E))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal,
            color = if (checked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
