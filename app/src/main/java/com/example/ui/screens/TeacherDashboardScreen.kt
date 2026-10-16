package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CastForEducation
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExperimentTrial
import com.example.data.GroupStemProjectEntity
import com.example.data.StudentEntity
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.StemViewModel
import com.example.ui.viewmodel.TeacherTab
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(
    viewModel: StemViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentTab by viewModel.teacherTab.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val allGroups by viewModel.allGroups.collectAsState()

    BackHandler {
        viewModel.navigateTo(AppScreen.ROLE_SELECTION)
    }

    val tabTitles = listOf("Ringkasan", "32 Siswa", "8 Kelompok", "Rubrik Nilai")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Dashboard Guru IPA",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Bu Maratus Sholichah, S.Pd. • Kelas 9 SMP",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.ROLE_SELECTION) },
                        modifier = Modifier.testTag("btn_back_teacher_dash")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Menu Awal",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.generateAndOpenClassRecapPdf(context) },
                        modifier = Modifier.testTag("btn_top_export_class_pdf")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Unduh Rekap Lengkap PDF",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            TabRow(
                selectedTabIndex = currentTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = currentTab.ordinal == index,
                        onClick = { viewModel.setTeacherTab(TeacherTab.entries[index]) },
                        text = { Text(title, fontSize = 11.5.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            when (currentTab) {
                TeacherTab.RINGKASAN -> TeacherOverviewTab(viewModel, allStudents, allGroups)
                TeacherTab.REKAP_SISWA -> TeacherStudentsRecapTab(allStudents)
                TeacherTab.REKAP_KELOMPOK -> TeacherGroupsRecapTab(viewModel, allGroups, allStudents)
                TeacherTab.RUBRIK_PENILAIAN -> TeacherRubricGradingTab(viewModel, allGroups)
            }
        }
    }
}

@Composable
fun TeacherOverviewTab(
    viewModel: StemViewModel,
    students: List<StudentEntity>,
    groups: List<GroupStemProjectEntity>
) {
    val context = LocalContext.current
    val quizCompletedCount = students.count { it.quizScore != null }
    val quizScores = students.mapNotNull { it.quizScore }
    val avgScore = if (quizScores.isNotEmpty()) quizScores.average() else 0.0

    val finishedGroupsCount = groups.count { it.progressPercent == 100 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Class Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TeacherStatCard(
                title = "Total Siswa",
                value = "${students.size}",
                subtitle = "Kelas 9-A",
                color = Color(0xFF0284C7),
                modifier = Modifier.weight(1f)
            )
            TeacherStatCard(
                title = "Sudah Kuis",
                value = "$quizCompletedCount / ${students.size}",
                subtitle = "Rata-rata: ${String.format(Locale.US, "%.1f", avgScore)}",
                color = Color(0xFF0F766E),
                modifier = Modifier.weight(1f)
            )
            TeacherStatCard(
                title = "Kelompok STEM",
                value = "$finishedGroupsCount / 8",
                subtitle = "Selesai 100%",
                color = Color(0xFFD97706),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // PDF Auto-Download Master Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Unduh Rekap Lengkap Kelas 9 (PDF)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Dokumen resmi rekap 32 siswa + 8 kelompok STEM",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Menghasilkan berkas format PDF A4 siap cetak yang memuat rekapitulasi nilai kuis ke-32 siswa, ringkasan perakitan kincir angin 8 kelompok, voltase tertinggi, dan lembar pengesahan guru pembimbing.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { viewModel.generateAndOpenClassRecapPdf(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("btn_export_class_recap_pdf"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unduh Rekapitulasi PDF Otomatis", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Overview of 8 Groups Progress
        Text(
            text = "KEMAJUAN 8 KELOMPOK PROYEK STEM",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        groups.forEach { gp ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = gp.groupName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(${gp.progressPercent}%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (gp.progressPercent == 100) Color(0xFF0F766E) else Color(0xFFD97706)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { gp.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (gp.progressPercent == 100) Color(0xFF0F766E) else Color(0xFFD97706),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = { viewModel.generateAndOpenGroupPdf(context, gp.groupId) },
                        modifier = Modifier.testTag("btn_dash_pdf_${gp.groupId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Unduh PDF Kelompok ${gp.groupId}",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = {
                viewModel.resetData()
                Toast.makeText(context, "Data berhasil direset!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_reset_database"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Reset Data Contoh (Standar 32 Siswa)")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TeacherStudentsRecapTab(students: List<StudentEntity>) {
    var searchQuery by remember { mutableStateOf("") }
    var groupFilter by remember { mutableIntStateOf(0) }

    val filtered = students.filter { st ->
        val matchSearch = st.name.contains(searchQuery, ignoreCase = true) || st.studentNumber.toString().contains(searchQuery)
        val matchGroup = groupFilter == 0 || st.groupId == groupFilter
        matchSearch && matchGroup
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Cari nama siswa atau no. absen...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("teacher_search_students"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Total Siswa: ${filtered.size} dari ${students.size} Siswa Kelas 9-A",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filtered, key = { it.id }) { st ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${st.studentNumber}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = st.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Kelompok ${st.groupId} • ${st.className}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (st.quizScore != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFCCFBF1)
                            ) {
                                Text(
                                    text = "Nilai: ${st.quizScore}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F766E),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "Belum Kuis",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherGroupsRecapTab(
    viewModel: StemViewModel,
    groups: List<GroupStemProjectEntity>,
    students: List<StudentEntity>
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(groups, key = { it.groupId }) { gp ->
            val members = students.filter { it.groupId == gp.groupId }
            val trials = ExperimentTrial.listFromJsonString(gp.experimentDataJson)
            val maxVolt = trials.maxOfOrNull { it.voltageVolt } ?: 0f

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = gp.groupName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Bahan: ${gp.bladeMaterial} • ${gp.bladeLengthCm} cm",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (gp.progressPercent == 100) Color(0xFFCCFBF1) else Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = "${gp.progressPercent}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (gp.progressPercent == 100) Color(0xFF0F766E) else Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Anggota: " + members.joinToString(", ") { "${it.studentNumber}. ${it.name.split(" ").first()}" },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Max Tegangan: ${if (maxVolt > 0) "${maxVolt}V" else "—"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFD97706)
                        )
                        Text(
                            text = "Nilai STEM: ${if (gp.totalRubricScore > 0) "${gp.totalRubricScore}/100" else "Belum Dinilai"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (gp.totalRubricScore > 0) Color(0xFF0F766E) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.selectGroupForReview(gp.groupId)
                                viewModel.navigateTo(AppScreen.STUDENT_GROUP_PROJECT)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Buka LKPD", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.generateAndOpenGroupPdf(context, gp.groupId) },
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("btn_download_group_pdf_${gp.groupId}"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Unduh PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherRubricGradingTab(
    viewModel: StemViewModel,
    groups: List<GroupStemProjectEntity>
) {
    val context = LocalContext.current
    var selectedGroupId by remember { mutableIntStateOf(1) }

    val currentGroup = groups.find { it.groupId == selectedGroupId }

    var scienceScore by remember(selectedGroupId, currentGroup?.scienceScore) { mutableIntStateOf(currentGroup?.scienceScore ?: 20) }
    var techScore by remember(selectedGroupId, currentGroup?.techScore) { mutableIntStateOf(currentGroup?.techScore ?: 20) }
    var engScore by remember(selectedGroupId, currentGroup?.engScore) { mutableIntStateOf(currentGroup?.engScore ?: 20) }
    var mathScore by remember(selectedGroupId, currentGroup?.mathScore) { mutableIntStateOf(currentGroup?.mathScore ?: 20) }
    var teacherNotes by remember(selectedGroupId, currentGroup?.teacherNotes) { mutableStateOf(currentGroup?.teacherNotes ?: "") }

    val totalScore = scienceScore + techScore + engScore + mathScore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Pilih Kelompok Yang Dinilai:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Group selector row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            (1..4).forEach { gid ->
                FilterChip(
                    selected = selectedGroupId == gid,
                    onClick = { selectedGroupId = gid },
                    label = { Text("Kel $gid") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                    )
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            (5..8).forEach { gid ->
                FilterChip(
                    selected = selectedGroupId == gid,
                    onClick = { selectedGroupId = gid },
                    label = { Text("Kel $gid") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Total Score Display
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = currentGroup?.groupName ?: "Kelompok $selectedGroupId",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Rubrik Penilaian 4 Pilar STEM",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$totalScore",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "dari 100",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Sliders
        RubricSliderRow("1. Science (Sains - Pemahaman Konsep)", scienceScore, 25) { scienceScore = it }
        RubricSliderRow("2. Technology (Penggunaan Multimeter & LED)", techScore, 25) { techScore = it }
        RubricSliderRow("3. Engineering (Kerapian & Desain Bilah Kincir)", engScore, 25) { engScore = it }
        RubricSliderRow("4. Mathematics (Perhitungan V, I, P, & Grafik)", mathScore, 25) { mathScore = it }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = teacherNotes,
            onValueChange = { teacherNotes = it },
            label = { Text("Catatan & Masukan Guru untuk Kelompok $selectedGroupId") },
            minLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_teacher_notes"),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.saveRubricScore(
                    groupId = selectedGroupId,
                    science = scienceScore,
                    tech = techScore,
                    eng = engScore,
                    math = mathScore,
                    notes = teacherNotes
                )
                Toast.makeText(context, "Nilai Rubrik Kelompok $selectedGroupId tersimpan!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_save_rubric"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simpan Nilai Rubrik Kelompok $selectedGroupId", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                viewModel.generateAndOpenGroupPdf(context, selectedGroupId)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("btn_download_group_pdf_from_rubric"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cetak & Unduh Laporan PDF Kelompok $selectedGroupId")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun RubricSliderRow(
    title: String,
    score: Int,
    maxScore: Int,
    onScoreChange: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$score / $maxScore",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Slider(
                value = score.toFloat(),
                onValueChange = { onScoreChange(it.toInt()) },
                valueRange = 0f..maxScore.toFloat(),
                steps = maxScore - 1,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
    }
}

@Composable
fun TeacherStatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
