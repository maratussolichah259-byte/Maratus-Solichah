package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.StemViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialScreen(
    viewModel: StemViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.STUDENT_HOME)
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Besaran Pokok", "Simulator Ohm", "Seri vs Paralel", "Daya & Energi")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Materi: Listrik Dinamis", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.STUDENT_HOME) },
                        modifier = Modifier.testTag("btn_back_material")
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
                tabTitles.forEachIndexed { index, title ->
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
                    0 -> BasicElectricityContent()
                    1 -> OhmSimulatorContent(viewModel)
                    2 -> CircuitSeriesParallelContent()
                    3 -> PowerEnergyContent()
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun BasicElectricityContent() {
    Column {
        Text(
            text = "1. Konsep Dasar Listrik Dinamis",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Listrik Dinamis adalah muatan listrik (elektron) yang bergerak/mengalir melalui suatu penghantar listrik dalam waktu tertentu.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Besaran 1: Kuat Arus (I)
        FormulaCard(
            title = "Kuat Arus Listrik (I)",
            symbol = "I = Q / t",
            unit = "Satuan: Ampere (A)",
            desc = "Banyaknya muatan listrik (Q dalam Coulomb) yang mengalir melalui penampang penghantar tiap satuan waktu (t dalam detik). 1 Ampere = 1 Coulomb/detik.",
            color = Color(0xFF0284C7),
            bgColor = Color(0xFFE0F2FE)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Besaran 2: Beda Potensial / Tegangan (V)
        FormulaCard(
            title = "Beda Potensial / Tegangan (V)",
            symbol = "V = W / Q",
            unit = "Satuan: Volt (V)",
            desc = "Energi atau usaha (W dalam Joule) yang diperlukan untuk memindahkan muatan listrik (Q) antara dua titik pada suatu rangkaian. Beda potensial inilah yang mendorong elektron mengalir.",
            color = Color(0xFFD97706),
            bgColor = Color(0xFFFEF3C7)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Besaran 3: Hambatan Listrik (R)
        FormulaCard(
            title = "Hambatan Listrik (R)",
            symbol = "R = ρ · (L / A)",
            unit = "Satuan: Ohm (Ω)",
            desc = "Derajat kesulitan yang dihadapi elektron saat mengalir melalui suatu bahan. Dipengaruhi oleh hambat jenis bahan (ρ), panjang kawat penghantar (L), dan luas penampang kawat (A).",
            color = Color(0xFF0F766E),
            bgColor = Color(0xFFCCFBF1)
        )
    }
}

@Composable
fun OhmSimulatorContent(viewModel: StemViewModel) {
    val voltage by viewModel.ohmVoltage.collectAsState()
    val resistance by viewModel.ohmResistance.collectAsState()

    val currentAmp = voltage / resistance
    val currentMilliAmp = currentAmp * 1000f
    val powerWatt = voltage * currentAmp

    val bulbColor by animateColorAsState(
        targetValue = when {
            currentMilliAmp < 50f -> Color(0xFF71717A)
            currentMilliAmp < 200f -> Color(0xFFFDE047)
            currentMilliAmp < 500f -> Color(0xFFFBBF24)
            else -> Color(0xFFF59E0B)
        },
        label = "bulbColor"
    )

    Column {
        Text(
            text = "2. Simulator Interaktif Hukum Ohm",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Hukum Ohm: V = I × R. Kuat arus (I) berbanding lurus dengan tegangan (V) dan berbanding terbalik dengan hambatan (R).",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Bulb & Output Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Lamp Visualization
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(bulbColor.copy(alpha = 0.25f), CircleShape)
                        .border(2.dp, bulbColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Lampu Rangkaian",
                        tint = bulbColor,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = when {
                        currentMilliAmp < 50f -> "Kondisi Lampu: Mati / Sangat Redup"
                        currentMilliAmp < 250f -> "Kondisi Lampu: Menyala Sedang"
                        currentMilliAmp < 600f -> "Kondisi Lampu: Menyala Terang"
                        else -> "Kondisi Lampu: Sangat Terang!"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Real-time calculation boxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CalcStatBox(label = "Tegangan (V)", value = "${String.format(Locale.US, "%.1f", voltage)} V", color = Color(0xFFD97706))
                    CalcStatBox(label = "Hambatan (R)", value = "${resistance.toInt()} Ω", color = Color(0xFF0F766E))
                    CalcStatBox(label = "Kuat Arus (I)", value = "${String.format(Locale.US, "%.1f", currentMilliAmp)} mA", color = Color(0xFF0284C7))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Daya Listrik (P = V × I) = ${String.format(Locale.US, "%.2f", powerWatt)} Watt",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sliders for V and R
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Atur Beda Potensial / Tegangan (V): ${String.format(Locale.US, "%.1f", voltage)} Volt",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Slider(
                    value = voltage,
                    onValueChange = { viewModel.ohmVoltage.value = it },
                    valueRange = 1f..24f,
                    steps = 23,
                    modifier = Modifier.testTag("slider_voltage"),
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFD97706), activeTrackColor = Color(0xFFD97706))
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Atur Hambatan Listrik (R): ${resistance.toInt()} Ohm (Ω)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Slider(
                    value = resistance,
                    onValueChange = { viewModel.ohmResistance.value = it },
                    valueRange = 1f..100f,
                    steps = 99,
                    modifier = Modifier.testTag("slider_resistance"),
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF0F766E), activeTrackColor = Color(0xFF0F766E))
                )
            }
        }
    }
}

@Composable
fun CircuitSeriesParallelContent() {
    Column {
        Text(
            text = "3. Rangkaian Seri vs Rangkaian Paralel",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Memahami perbedaan karakteristik arus dan tegangan pada instalasi komponen listrik.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Rangkaian Seri Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE0F2FE),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Color(0xFF0284C7))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Rangkaian Seri (Sejajar Satu Jalur)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "• Rumus Hambatan Pengganti: Rs = R1 + R2 + R3\n• Kuat arus listrik sama besar di setiap hambatan (I total = I1 = I2)\n• Beda potensial terbagi (V total = V1 + V2)\n• Sifat Kritis: Jika salah satu lampu putus/dilepas, seluruh rangkaian terputus dan semua lampu padam.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Rangkaian Paralel Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFCCFBF1),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFF0F766E))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Rangkaian Paralel (Bercabang)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "• Rumus Hambatan Pengganti: 1/Rp = 1/R1 + 1/R2\n• Hambatan total lebih kecil dari hambatan terkecil komponennya\n• Beda potensial sama besar di setiap cabang (V total = V1 = V2)\n• Arus terbagi sesuai Hukum I Kirchhoff (I masuk = I1 + I2)\n• Keuntungan Utama: Digunakan di rumah tangga! Jika satu lampu padam, lampu di ruangan lain tetap menyala normal.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun PowerEnergyContent() {
    Column {
        Text(
            text = "4. Daya Listrik & Energi Alternatif",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Bagaimana energi listrik dinamis dihasilkan, dikonsumsi, dan dipanen dari alam.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormulaCard(
            title = "Daya Listrik (P)",
            symbol = "P = V × I = I² × R = V² / R",
            unit = "Satuan: Watt (W)",
            desc = "Laju pemakaian atau pembangkitan energi listrik per satuan waktu (1 Watt = 1 Joule/sekon). Pada mini turbin kincir angin kita, daya dihitung dari hasil kali tegangan voltmeter (Volt) dengan kuat arus LED (Ampere).",
            color = Color(0xFF9333EA),
            bgColor = Color(0xFFF3E8FF)
        )

        Spacer(modifier = Modifier.height(14.dp))

        FormulaCard(
            title = "Energi Listrik (W)",
            symbol = "W = P × t = V × I × t",
            unit = "Satuan: Joule (J) atau kWh",
            desc = "Total energi yang dihasilkan atau digunakan selama waktu (t). Pembangkit listrik tenaga angin (PLTB) memanfaatkan angin untuk memutar generator secara kontinu tanpa membakar bahan bakar fosil.",
            color = Color(0xFF0F766E),
            bgColor = Color(0xFFCCFBF1)
        )
    }
}

@Composable
fun FormulaCard(
    title: String,
    symbol: String,
    unit: String,
    desc: String,
    color: Color,
    bgColor: Color
) {
    Card(
        shape = RoundedCornerShape(14.dp),
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
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = bgColor
                ) {
                    Text(
                        text = unit,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = symbol,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = desc,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
fun CalcStatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
