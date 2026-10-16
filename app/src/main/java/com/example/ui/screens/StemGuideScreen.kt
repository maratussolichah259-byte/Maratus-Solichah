package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.StemViewModel
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StemGuideScreen(
    viewModel: StemViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.STUDENT_HOME)
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Simulator Virtual", "4 Pilar STEM", "Alat & Bahan", "4 Langkah Kerja")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Proyek STEM: Kincir Angin", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.STUDENT_HOME) },
                        modifier = Modifier.testTag("btn_back_stem_guide")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Beranda",
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
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> WindmillSimulatorTab(viewModel)
                    1 -> FourStemPillarsTab()
                    2 -> ToolsAndMaterialsTab()
                    3 -> FourStepsProjectTab()
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun WindmillSimulatorTab(viewModel: StemViewModel) {
    val bladeCount by viewModel.simBladeCount.collectAsState()
    val windSpeed by viewModel.simWindSpeed.collectAsState()

    val simResult = viewModel.calculateWindmillOutput(windSpeed, bladeCount)

    // Continuous rotation animation
    val durationMs = if (simResult.rpm > 0) {
        (60000 / simResult.rpm).coerceIn(80, 2000)
    } else {
        1000000
    }

    val infiniteTransition = rememberInfiniteTransition(label = "turbineRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationDeg"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Simulator Mini Turbin Kincir Angin Virtual",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Uji pengaruh kecepatan angin dan variasi jumlah bilah terhadap tegangan dinamo & nyala LED",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Turbine Simulation Box
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Canvas showing the rotating turbine & tower
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .background(Color(0xFFE2E8F0), CircleShape)
                        .border(2.dp, Color(0xFFCBD5E1), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Wind turbine tower and blades canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f

                        // Draw Tower Pole
                        drawLine(
                            color = Color(0xFF64748B),
                            start = Offset(cx, cy),
                            end = Offset(cx, size.height - 10f),
                            strokeWidth = 6f
                        )
                        drawLine(
                            color = Color(0xFF475569),
                            start = Offset(cx - 25f, size.height - 10f),
                            end = Offset(cx + 25f, size.height - 10f),
                            strokeWidth = 6f
                        )

                        // Draw Generator Motor Nacelle
                        drawCircle(
                            color = Color(0xFF334155),
                            radius = 16f,
                            center = Offset(cx, cy)
                        )
                    }

                    // Rotating Blades Layer
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                            .rotate(if (simResult.rpm > 0) rotation else 0f)
                    ) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val bladeRadius = size.width / 2f - 18f

                        // Draw blades based on bladeCount (3, 4, or 6)
                        val angleStep = (2 * PI / bladeCount).toFloat()
                        for (i in 0 until bladeCount) {
                            val angle = i * angleStep
                            val bladeTipX = cx + bladeRadius * cos(angle)
                            val bladeTipY = cy + bladeRadius * sin(angle)

                            val perpAngle1 = angle + PI.toFloat() / 2f
                            val perpAngle2 = angle - PI.toFloat() / 2f
                            val baseW = 10f

                            val p1x = cx + 8f * cos(angle) + baseW * cos(perpAngle1)
                            val p1y = cy + 8f * sin(angle) + baseW * sin(perpAngle1)
                            val p2x = cx + 8f * cos(angle) + baseW * cos(perpAngle2)
                            val p2y = cy + 8f * sin(angle) + baseW * sin(perpAngle2)

                            val path = Path().apply {
                                moveTo(cx, cy)
                                lineTo(p1x, p1y)
                                lineTo(bladeTipX, bladeTipY)
                                lineTo(p2x, p2y)
                                close()
                            }

                            drawPath(
                                path = path,
                                color = if (bladeCount == 3) Color(0xFF0284C7) else if (bladeCount == 4) Color(0xFFD97706) else Color(0xFF0F766E),
                                style = Fill
                            )
                            drawPath(
                                path = path,
                                color = Color.White,
                                style = Stroke(width = 1.2f)
                            )
                        }

                        // Hub cap center
                        drawCircle(color = Color(0xFFF1F5F9), radius = 8f, center = Offset(cx, cy))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Multimeter & LED Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalcStatBox(label = "Tegangan (V)", value = "${simResult.voltageVolt} V", color = Color(0xFFD97706))
                    CalcStatBox(label = "Arus (mA)", value = "${simResult.currentMa} mA", color = Color(0xFF0284C7))
                    CalcStatBox(label = "Daya (mW)", value = "${simResult.powerMw} mW", color = Color(0xFF9333EA))
                    CalcStatBox(label = "Putaran", value = "${simResult.rpm} RPM", color = Color(0xFF0F766E))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // LED Indicator Visual
                val ledColor = when {
                    simResult.voltageVolt < 1.8f -> Color(0xFF71717A)
                    simResult.voltageVolt < 2.5f -> Color(0xFFEF4444).copy(alpha = 0.5f)
                    simResult.voltageVolt < 3.5f -> Color(0xFFEF4444)
                    else -> Color(0xFFDC2626)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ledColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ledColor.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = ledColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lampu LED: ${simResult.ledStatus}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (simResult.voltageVolt >= 1.8f) Color(0xFFB91C1C) else Color(0xFF52525B)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Controls: Select Blade Count & Wind Speed
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Pilih Jumlah Bilah Kincir:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(3, 4, 6).forEach { count ->
                        FilterChip(
                            selected = bladeCount == count,
                            onClick = { viewModel.simBladeCount.value = count },
                            label = { Text("$count Bilah", fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kecepatan Angin:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.1f", windSpeed)} m/s (${when {
                            windSpeed <= 1.5f -> "Tenang"
                            windSpeed <= 4.0f -> "Pelan"
                            windSpeed <= 7.5f -> "Sedang"
                            else -> "Kencang"
                        }})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Slider(
                    value = windSpeed,
                    onValueChange = { viewModel.simWindSpeed.value = it },
                    valueRange = 0f..15f,
                    steps = 30,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Composable
fun FourStemPillarsTab() {
    Column {
        Text(
            text = "Integrasi 4 Pilar STEM Kincir Angin",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Pembelajaran IPA terpadu berbasis pendekatan Science, Technology, Engineering, & Mathematics.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        StemPillarCard(
            letter = "S",
            title = "Science (Sains)",
            icon = Icons.Default.Science,
            color = Color(0xFF0284C7),
            bgColor = Color(0xFFE0F2FE),
            points = listOf(
                "Hukum Kekekalan Energi: Mengubah energi kinetik aliran angin menjadi energi mekanik rotasi, lalu menjadi energi listrik dinamis.",
                "Induksi Elektromagnetik Faraday: Gerakan magnet memotong kumparan kawat tembaga dinamo menghasilkan gaya gerak listrik (GGL induksi).",
                "Arus searah (DC) mengalir ke lampu LED."
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        StemPillarCard(
            letter = "T",
            title = "Technology (Teknologi)",
            icon = Icons.Default.Memory,
            color = Color(0xFFD97706),
            bgColor = Color(0xFFFEF3C7),
            points = listOf(
                "Penggunaan Multimeter Digital (Voltmeter & Amperemeter) untuk membaca tegangan dan arus secara kuantitatif.",
                "Pemanfaatan Light Emitting Diode (LED) sebagai indikator pemanenan daya listrik ramah lingkungan.",
                "Aplikasi pencatat data dan generator laporan PDF otomatis."
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        StemPillarCard(
            letter = "E",
            title = "Engineering (Rekayasa)",
            icon = Icons.Default.Engineering,
            color = Color(0xFF0F766E),
            bgColor = Color(0xFFCCFBF1),
            points = listOf(
                "Merancang geometri dan jumlah bilah (3, 4, 6 bilah) untuk mendapatkan gaya angkat (lift) maksimal.",
                "Menentukan sudut kemiringan bilah (pitch angle 25° - 35°) terhadap arah datangnya angin.",
                "Membuat tiang menara penyangga yang kokoh dan minim getaran saat kincir berputar kencang."
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        StemPillarCard(
            letter = "M",
            title = "Mathematics (Matematika)",
            icon = Icons.Default.Functions,
            color = Color(0xFF9333EA),
            bgColor = Color(0xFFF3E8FF),
            points = listOf(
                "Menghitung rumus Hukum Ohm: V = I × R dan Daya Listrik: P = V × I (dalam mW).",
                "Menghitung rata-rata tegangan dari beberapa kali pengujian hembusan angin.",
                "Menganalisis grafik hubungan antara kecepatan angin terhadap tegangan yang dibangkitkan."
            )
        )
    }
}

@Composable
fun StemPillarCard(
    letter: String,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    bgColor: Color,
    points: List<String>
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = bgColor,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = letter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = color
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            points.forEach { pt ->
                Text(
                    text = "• $pt",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ToolsAndMaterialsTab() {
    val items = listOf(
        "Dinamo Motor DC Kecil (3V - 12V)" to "Berfungsi sebagai generator penghasil arus listrik induksi.",
        "Kertas Karton Tebal / Botol Plastik / Mika" to "Bahan pembuatan bilah baling-baling yang ringan dan aerodinamis.",
        "Stik Es Krim / Tusuk Bambu / Pipa PVC" to "Sebagai tiang menara dan dudukan motor DC generator.",
        "Kabel Capit Buaya (Merah & Hitam)" to "Menghubungkan kutub positif dan negatif dinamo ke beban.",
        "Lampu LED 3 Volt (Warna Bebas)" to "Sebagai beban listrik dan indikator menyala saat listrik terpicu.",
        "Multimeter Digital / Voltmeter DC" to "Mengukur nilai tegangan (Volt) dan arus (mA) yang dihasilkan.",
        "Lem Tembak (Hot Glue Gun) & Gunting" to "Untuk merekatkan bilah ke poros dinamo dan merakit menara.",
        "Kipas Angin / Pengering Rambut" to "Sumber angin terkontrol untuk pengujian kecepatan pelan, sedang, kencang."
    )

    Column {
        Text(
            text = "Daftar Alat & Bahan Proyek STEM",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Mudah didapatkan di sekitar rumah atau laboratorium sekolah SMP.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        items.forEachIndexed { idx, item ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${idx + 1}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.first,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = item.second,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FourStepsProjectTab() {
    val steps = listOf(
        "Tahap 1: Perancangan & Pembuatan Baling-Baling" to "Buat sketsa bilah dengan panjang 10-14 cm. Potong dari botol plastik atau karton laminasi. Buat variasi jumlah bilah (3 bilah, 4 bilah, dan 6 bilah) dengan kemiringan sudut pitch 30° pada tutup botol/hub pusat.",
        "Tahap 2: Perakitan Menara & Pemasangan Dinamo" to "Rangkai stik kayu atau pipa PVC membentuk menara segitiga yang stabil dengan tinggi sekitar 20-25 cm. Rekatkan dinamo motor DC di puncak menara secara horizontal menggunakan lem tembak.",
        "Tahap 3: Pembuatan Rangkaian Listrik & Uji Coba" to "Hubungkan kedua kutub kawat dinamo motor DC ke kabel capit buaya. Sambungkan ke lampu LED dan probe multimeter digital. Nyalakan kipas angin di depan kincir untuk menguji putaran dan tegangan output.",
        "Tahap 4: Pengambilan Data & Analisis Efisiensi" to "Catat tegangan (V) dan kondisi nyala LED pada lembar LKPD untuk setiap variasi kecepatan angin dan jumlah bilah. Buat kesimpulan dan unduh laporan resmi dalam format PDF!"
    )

    Column {
        Text(
            text = "4 Tahapan Kerja Proyek STEM",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(14.dp))

        steps.forEachIndexed { index, step ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${index + 1}",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = step.first,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = step.second,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}
