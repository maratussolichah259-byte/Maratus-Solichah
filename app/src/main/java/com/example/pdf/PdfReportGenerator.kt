package com.example.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.ExperimentTrial
import com.example.data.GroupStemProjectEntity
import com.example.data.StudentEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    private const val PAGE_WIDTH = 595 // Standard A4 width in points
    private const val PAGE_HEIGHT = 842 // Standard A4 height in points
    private const val MARGIN = 40f

    /**
     * Generate an official A4 PDF report for a single STEM group project
     */
    fun generateGroupReportPdf(
        context: Context,
        group: GroupStemProjectEntity,
        members: List<StudentEntity>
    ): File? {
        val document = PdfDocument()

        try {
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            drawGroupReportPage(canvas, group, members)

            document.finishPage(page)

            // Save to app documents directory
            val outputDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Laporan_STEM")
            if (!outputDir.exists()) outputDir.mkdirs()

            val sanitizedGroupName = group.groupName.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val fileName = "Laporan_STEM_${sanitizedGroupName}.pdf"
            val file = File(outputDir, fileName)

            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }

            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            document.close()
        }
    }

    /**
     * Generate a class-wide summary PDF containing all 32 students and 8 groups
     */
    fun generateClassRecapPdf(
        context: Context,
        students: List<StudentEntity>,
        groups: List<GroupStemProjectEntity>
    ): File? {
        val document = PdfDocument()

        try {
            // Page 1: 32 Students Table
            val pageInfo1 = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page1 = document.startPage(pageInfo1)
            drawClassStudentsPage(page1.canvas, students)
            document.finishPage(page1)

            // Page 2: 8 Groups STEM Summary & Rubric
            val pageInfo2 = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 2).create()
            val page2 = document.startPage(pageInfo2)
            drawClassGroupsPage(page2.canvas, groups)
            document.finishPage(page2)

            val outputDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Laporan_STEM")
            if (!outputDir.exists()) outputDir.mkdirs()

            val file = File(outputDir, "Rekap_Lengkap_Kelas9_STEM_ListrikDinamis.pdf")
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }

            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            document.close()
        }
    }

    private fun drawGroupReportPage(
        canvas: Canvas,
        group: GroupStemProjectEntity,
        members: List<StudentEntity>
    ) {
        val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
        val paintFill = Paint(Paint.ANTI_ALIAS_FLAG)
        val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG)

        var y = MARGIN

        // Header Background Banner
        paintFill.color = Color.parseColor("#0D47A1")
        canvas.drawRoundRect(RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + 65f), 8f, 8f, paintFill)

        paintText.color = Color.WHITE
        paintText.textSize = 13f
        paintText.isFakeBoldText = true
        paintText.textAlign = Paint.Align.CENTER
        canvas.drawText("LEMBAR KERJA & LAPORAN PROYEK STEM IPA KELAS 9 SMP", PAGE_WIDTH / 2f, y + 25f, paintText)

        paintText.textSize = 10f
        paintText.isFakeBoldText = false
        canvas.drawText("Penerapan Listrik Dinamis: Kincir Angin Pembangkit Listrik Sederhana", PAGE_WIDTH / 2f, y + 42f, paintText)
        paintText.textSize = 8.5f
        canvas.drawText("Mata Pelajaran IPA Terpadu • Semester Gasal • Kurikulum Merdeka", PAGE_WIDTH / 2f, y + 56f, paintText)

        y += 75f

        // Info Card
        paintFill.color = Color.parseColor("#F1F5F9")
        paintStroke.color = Color.parseColor("#CBD5E1")
        paintStroke.style = Paint.Style.STROKE
        paintStroke.strokeWidth = 1f

        val infoRect = RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + 80f)
        canvas.drawRoundRect(infoRect, 6f, 6f, paintFill)
        canvas.drawRoundRect(infoRect, 6f, 6f, paintStroke)

        paintText.textAlign = Paint.Align.LEFT
        paintText.color = Color.parseColor("#0F172A")
        paintText.textSize = 10.5f
        paintText.isFakeBoldText = true
        canvas.drawText(group.groupName, MARGIN + 12f, y + 18f, paintText)

        val memberNames = members.joinToString(", ") { "${it.studentNumber}. ${it.name}" }
        paintText.textSize = 8.5f
        paintText.isFakeBoldText = false
        paintText.color = Color.parseColor("#334155")
        canvas.drawText("Anggota Kelompok: $memberNames", MARGIN + 12f, y + 34f, paintText)

        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val dateStr = sdf.format(Date(group.lastUpdated))
        canvas.drawText("Tanggal Uji: $dateStr • Guru Pembimbing: Bu Maratus Sholichah, S.Pd.", MARGIN + 12f, y + 48f, paintText)

        val specStr = "Bahan Bilah: ${group.bladeMaterial} | Panjang: ${group.bladeLengthCm} cm | Sudut Pitch: ${group.pitchAngleDeg}°"
        canvas.drawText(specStr, MARGIN + 12f, y + 62f, paintText)

        val progressStr = "Progres: ${group.progressPercent}% Selesai (Desain: ${if (group.step1DesignDone) "✓" else "—"}, Rakit: ${if (group.step2AssemblyDone) "✓" else "—"}, Uji: ${if (group.step3TestingDone) "✓" else "—"}, Analisis: ${if (group.step4AnalysisDone) "✓" else "—"})"
        paintText.isFakeBoldText = true
        paintText.color = Color.parseColor("#0F766E")
        canvas.drawText(progressStr, MARGIN + 12f, y + 74f, paintText)

        y += 92f

        // Section 1: Konsep Listrik Dinamis & STEM
        paintText.textSize = 10f
        paintText.isFakeBoldText = true
        paintText.color = Color.parseColor("#0D47A1")
        canvas.drawText("A. DASAR TEORI LISTRIK DINAMIS & RANCANGAN STEM", MARGIN, y, paintText)

        y += 14f
        paintFill.color = Color.parseColor("#F8FAFC")
        val theoryRect = RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + 48f)
        canvas.drawRoundRect(theoryRect, 4f, 4f, paintFill)
        canvas.drawRoundRect(theoryRect, 4f, 4f, paintStroke)

        paintText.textSize = 8f
        paintText.isFakeBoldText = false
        paintText.color = Color.parseColor("#1E293B")
        canvas.drawText("1. Hukum Ohm: V = I × R (Beda potensial berbanding lurus dengan kuat arus pada beban konstan)", MARGIN + 8f, y + 14f, paintText)
        canvas.drawText("2. Daya Listrik: P = V × I (Daya yang dihasilkan generator dalam miliWatt diukur dari voltase dan arus LED)", MARGIN + 8f, y + 26f, paintText)
        canvas.drawText("3. Konversi Energi: Energi Kinetik Angin -> Energi Kinetik Rotasi Turbin -> Induksi Generator Dinamo DC -> Listrik", MARGIN + 8f, y + 38f, paintText)

        y += 60f

        // Section 2: Tabel Pengujian Eksperimen
        paintText.textSize = 10f
        paintText.isFakeBoldText = true
        paintText.color = Color.parseColor("#0D47A1")
        canvas.drawText("B. TABEL DATA HASIL PENGUJIAN KINCIR ANGIN", MARGIN, y, paintText)

        y += 10f

        // Table Header
        val colWidths = floatArrayOf(45f, 65f, 110f, 75f, 75f, 75f, 70f)
        val colHeaders = arrayOf("Uji", "Bilah", "Kec. Angin", "Tegangan", "Arus", "Daya (P)", "Kondisi LED")
        val tableStartX = MARGIN
        val rowHeight = 18f

        paintFill.color = Color.parseColor("#1E293B")
        canvas.drawRect(tableStartX, y, PAGE_WIDTH - MARGIN, y + rowHeight, paintFill)

        paintText.color = Color.WHITE
        paintText.textSize = 8f
        paintText.isFakeBoldText = true
        paintText.textAlign = Paint.Align.CENTER

        var curX = tableStartX
        for (i in colHeaders.indices) {
            val cellCenterX = curX + colWidths[i] / 2f
            canvas.drawText(colHeaders[i], cellCenterX, y + 12f, paintText)
            curX += colWidths[i]
        }

        y += rowHeight

        // Table Rows
        val trials = ExperimentTrial.listFromJsonString(group.experimentDataJson)
        val trialList = if (trials.isNotEmpty()) trials else listOf(
            ExperimentTrial(1, 3, "Pelan (2 m/s)", 1.5f, 15f, 22.5f, "Mati", "-"),
            ExperimentTrial(2, 3, "Sedang (5 m/s)", 2.8f, 30f, 84.0f, "Redup", "-"),
            ExperimentTrial(3, 3, "Kencang (8 m/s)", 3.6f, 42f, 151.2f, "Terang", "-")
        )

        paintText.isFakeBoldText = false
        paintText.textSize = 7.5f

        for ((idx, trial) in trialList.withIndex()) {
            val bgCol = if (idx % 2 == 0) Color.WHITE else Color.parseColor("#F8FAFC")
            paintFill.color = bgCol
            canvas.drawRect(tableStartX, y, PAGE_WIDTH - MARGIN, y + rowHeight, paintFill)

            // Grid lines
            canvas.drawRect(tableStartX, y, PAGE_WIDTH - MARGIN, y + rowHeight, paintStroke)

            val rowData = arrayOf(
                "#${trial.trialNumber}",
                "${trial.bladeCount} Bilah",
                trial.windSpeed,
                "${String.format(Locale.US, "%.2f", trial.voltageVolt)} V",
                "${String.format(Locale.US, "%.1f", trial.currentMa)} mA",
                "${String.format(Locale.US, "%.1f", trial.powerMw)} mW",
                trial.ledStatus
            )

            curX = tableStartX
            paintText.textAlign = Paint.Align.CENTER
            paintText.color = Color.parseColor("#1E293B")
            for (i in rowData.indices) {
                val cellCenterX = curX + colWidths[i] / 2f
                canvas.drawText(rowData[i], cellCenterX, y + 12f, paintText)
                curX += colWidths[i]
            }

            y += rowHeight
        }

        y += 12f

        // Section 3: Analisis & Kesimpulan Kelompok
        paintText.textAlign = Paint.Align.LEFT
        paintText.textSize = 10f
        paintText.isFakeBoldText = true
        paintText.color = Color.parseColor("#0D47A1")
        canvas.drawText("C. ANALISIS HASIL & KESIMPULAN KELOMPOK", MARGIN, y, paintText)

        y += 10f
        val conclusionRect = RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + 54f)
        paintFill.color = Color.parseColor("#F8FAFC")
        canvas.drawRoundRect(conclusionRect, 4f, 4f, paintFill)
        canvas.drawRoundRect(conclusionRect, 4f, 4f, paintStroke)

        val conclusion = if (group.conclusionText.isNotBlank()) group.conclusionText
        else "Berdasarkan pengujian, mini turbin kincir angin menghasilkan tegangan yang meningkat seiring bertambahnya kecepatan hembusan angin. Lampu LED menyala terang saat tegangan melampaui forward voltage 2.5V."

        paintText.textSize = 8f
        paintText.isFakeBoldText = false
        paintText.color = Color.parseColor("#334155")
        drawMultilineText(canvas, conclusion, MARGIN + 8f, y + 14f, PAGE_WIDTH - MARGIN * 2 - 16f, 11f, paintText)

        y += 68f

        // Section 4: Rubrik Penilaian Guru
        paintText.textSize = 10f
        paintText.isFakeBoldText = true
        paintText.color = Color.parseColor("#0D47A1")
        canvas.drawText("D. RUBRIK PENILAIAN STEM OLEH GURU", MARGIN, y, paintText)

        y += 10f
        val rubricBox = RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + 80f)
        paintFill.color = Color.parseColor("#FEF3C7")
        paintStroke.color = Color.parseColor("#F59E0B")
        canvas.drawRoundRect(rubricBox, 6f, 6f, paintFill)
        canvas.drawRoundRect(rubricBox, 6f, 6f, paintStroke)

        paintText.textSize = 8.5f
        paintText.isFakeBoldText = true
        paintText.color = Color.parseColor("#78350F")

        val sStr = "Sains: ${group.scienceScore}/25"
        val tStr = "Teknologi: ${group.techScore}/25"
        val eStr = "Engineering: ${group.engScore}/25"
        val mStr = "Matematika: ${group.mathScore}/25"
        val totalStr = "TOTAL SKOR: ${group.totalRubricScore}/100"

        canvas.drawText(sStr, MARGIN + 12f, y + 18f, paintText)
        canvas.drawText(tStr, MARGIN + 125f, y + 18f, paintText)
        canvas.drawText(eStr, MARGIN + 245f, y + 18f, paintText)
        canvas.drawText(mStr, MARGIN + 375f, y + 18f, paintText)

        paintText.textSize = 10f
        paintText.color = Color.parseColor("#92400E")
        canvas.drawText(totalStr, MARGIN + 12f, y + 38f, paintText)

        paintText.textSize = 8f
        paintText.isFakeBoldText = false
        paintText.color = Color.parseColor("#451A03")
        val teacherNotes = if (group.teacherNotes.isNotBlank()) group.teacherNotes else "Kinerja kelompok sangat baik dan menunjukkan penerapan prinsip STEM secara nyata."
        canvas.drawText("Catatan Guru: $teacherNotes", MARGIN + 12f, y + 54f, paintText)

        y += 94f

        // Signatures
        paintText.textAlign = Paint.Align.CENTER
        paintText.textSize = 8.5f
        paintText.color = Color.parseColor("#1E293B")
        paintText.isFakeBoldText = false

        val leftCenterX = MARGIN + 100f
        val rightCenterX = PAGE_WIDTH - MARGIN - 100f

        canvas.drawText("Ketua Kelompok,", leftCenterX, y, paintText)
        canvas.drawText("Guru Pembimbing IPA,", rightCenterX, y, paintText)

        y += 38f
        val leaderName = members.firstOrNull()?.name ?: "Ketua Kelompok"
        paintText.isFakeBoldText = true
        canvas.drawText("( $leaderName )", leftCenterX, y, paintText)
        canvas.drawText("( Bu Maratus Sholichah, S.Pd. )", rightCenterX, y, paintText)

        paintText.isFakeBoldText = false
        paintText.textSize = 7f
        paintText.color = Color.parseColor("#64748B")
        canvas.drawText("NISN Siswa", leftCenterX, y + 10f, paintText)
        canvas.drawText("NIP. 19970512 202203 2 004", rightCenterX, y + 10f, paintText)
    }

    private fun drawClassStudentsPage(canvas: Canvas, students: List<StudentEntity>) {
        val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
        val paintFill = Paint(Paint.ANTI_ALIAS_FLAG)
        val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG)
        paintStroke.style = Paint.Style.STROKE
        paintStroke.strokeWidth = 0.8f
        paintStroke.color = Color.parseColor("#CBD5E1")

        var y = MARGIN

        // Header Banner
        paintFill.color = Color.parseColor("#0D47A1")
        canvas.drawRoundRect(RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + 50f), 6f, 6f, paintFill)

        paintText.color = Color.WHITE
        paintText.textSize = 12f
        paintText.isFakeBoldText = true
        paintText.textAlign = Paint.Align.CENTER
        canvas.drawText("REKAPITULASI PROGRES & NILAI KUIS IPA KELAS 9", PAGE_WIDTH / 2f, y + 22f, paintText)

        paintText.textSize = 9f
        paintText.isFakeBoldText = false
        canvas.drawText("Materi: Listrik Dinamis & Proyek STEM Kincir Angin • Total: ${students.size} Siswa", PAGE_WIDTH / 2f, y + 38f, paintText)

        y += 62f

        // Table Header
        val colWidths = floatArrayOf(35f, 40f, 180f, 95f, 75f, 90f)
        val colHeaders = arrayOf("No", "Absen", "Nama Siswa", "Kelompok", "Nilai Kuis", "Status Kuis")
        val tableStartX = MARGIN
        val rowHeight = 16.5f

        paintFill.color = Color.parseColor("#1E293B")
        canvas.drawRect(tableStartX, y, PAGE_WIDTH - MARGIN, y + rowHeight, paintFill)

        paintText.color = Color.WHITE
        paintText.textSize = 7.5f
        paintText.isFakeBoldText = true

        var curX = tableStartX
        for (i in colHeaders.indices) {
            val cellCenterX = curX + colWidths[i] / 2f
            canvas.drawText(colHeaders[i], cellCenterX, y + 11.5f, paintText)
            curX += colWidths[i]
        }

        y += rowHeight

        // Rows for up to 32 students
        paintText.textSize = 7f
        paintText.isFakeBoldText = false

        for ((idx, st) in students.take(32).withIndex()) {
            paintFill.color = if (idx % 2 == 0) Color.WHITE else Color.parseColor("#F8FAFC")
            canvas.drawRect(tableStartX, y, PAGE_WIDTH - MARGIN, y + rowHeight, paintFill)
            canvas.drawRect(tableStartX, y, PAGE_WIDTH - MARGIN, y + rowHeight, paintStroke)

            val scoreStr = if (st.quizScore != null) "${st.quizScore}" else "—"
            val statusStr = if (st.quizScore != null) "Selesai ✓" else "Belum Kuis"

            curX = tableStartX
            // No
            paintText.textAlign = Paint.Align.CENTER
            paintText.color = Color.parseColor("#1E293B")
            canvas.drawText("${idx + 1}", curX + colWidths[0] / 2f, y + 11f, paintText)
            curX += colWidths[0]

            // Absen
            canvas.drawText("${st.studentNumber}", curX + colWidths[1] / 2f, y + 11f, paintText)
            curX += colWidths[1]

            // Nama
            paintText.textAlign = Paint.Align.LEFT
            canvas.drawText(st.name, curX + 6f, y + 11f, paintText)
            curX += colWidths[2]

            // Kelompok
            paintText.textAlign = Paint.Align.CENTER
            canvas.drawText("Kelompok ${st.groupId}", curX + colWidths[3] / 2f, y + 11f, paintText)
            curX += colWidths[3]

            // Nilai
            paintText.isFakeBoldText = true
            if (st.quizScore != null && st.quizScore >= 80) paintText.color = Color.parseColor("#0F766E")
            else paintText.color = Color.parseColor("#78350F")
            canvas.drawText(scoreStr, curX + colWidths[4] / 2f, y + 11f, paintText)
            paintText.isFakeBoldText = false
            curX += colWidths[4]

            // Status
            paintText.color = if (st.quizScore != null) Color.parseColor("#0F766E") else Color.parseColor("#94A3B8")
            canvas.drawText(statusStr, curX + colWidths[5] / 2f, y + 11f, paintText)

            y += rowHeight
        }

        y += 18f
        // Class summary footnote
        val avgScore = students.mapNotNull { it.quizScore }.average()
        val completedCount = students.count { it.quizScore != null }
        paintText.textAlign = Paint.Align.LEFT
        paintText.textSize = 8f
        paintText.color = Color.parseColor("#334155")
        paintText.isFakeBoldText = true
        val avgStr = if (avgScore.isNaN()) "0" else String.format(Locale.US, "%.1f", avgScore)
        canvas.drawText("Ringkasan: $completedCount dari ${students.size} siswa telah menyelesaikan kuis IPA • Rata-rata Nilai: $avgStr / 100", MARGIN, y, paintText)
    }

    private fun drawClassGroupsPage(canvas: Canvas, groups: List<GroupStemProjectEntity>) {
        val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
        val paintFill = Paint(Paint.ANTI_ALIAS_FLAG)
        val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG)
        paintStroke.style = Paint.Style.STROKE
        paintStroke.strokeWidth = 0.8f
        paintStroke.color = Color.parseColor("#CBD5E1")

        var y = MARGIN

        // Header Banner
        paintFill.color = Color.parseColor("#0D47A1")
        canvas.drawRoundRect(RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + 50f), 6f, 6f, paintFill)

        paintText.color = Color.WHITE
        paintText.textSize = 12f
        paintText.isFakeBoldText = true
        paintText.textAlign = Paint.Align.CENTER
        canvas.drawText("REKAPITULASI PROYEK STEM 8 KELOMPOK SISWA", PAGE_WIDTH / 2f, y + 22f, paintText)

        paintText.textSize = 9f
        paintText.isFakeBoldText = false
        canvas.drawText("Mini Turbin Kincir Angin Pembangkit Listrik Sederhana (Kelas 9)", PAGE_WIDTH / 2f, y + 38f, paintText)

        y += 62f

        // Table Header
        val colWidths = floatArrayOf(30f, 140f, 95f, 60f, 50f, 50f, 90f)
        val colHeaders = arrayOf("ID", "Nama Kelompok", "Bahan Bilah", "Progres", "Uji", "Skor", "Catatan Guru")
        val tableStartX = MARGIN
        val rowHeight = 28f

        paintFill.color = Color.parseColor("#1E293B")
        canvas.drawRect(tableStartX, y, PAGE_WIDTH - MARGIN, y + 20f, paintFill)

        paintText.color = Color.WHITE
        paintText.textSize = 7.5f
        paintText.isFakeBoldText = true

        var curX = tableStartX
        for (i in colHeaders.indices) {
            val cellCenterX = curX + colWidths[i] / 2f
            canvas.drawText(colHeaders[i], cellCenterX, y + 13f, paintText)
            curX += colWidths[i]
        }

        y += 20f

        // 8 Groups Rows
        paintText.textSize = 7.5f
        paintText.isFakeBoldText = false

        for ((idx, gp) in groups.withIndex()) {
            paintFill.color = if (idx % 2 == 0) Color.WHITE else Color.parseColor("#F8FAFC")
            canvas.drawRect(tableStartX, y, PAGE_WIDTH - MARGIN, y + rowHeight, paintFill)
            canvas.drawRect(tableStartX, y, PAGE_WIDTH - MARGIN, y + rowHeight, paintStroke)

            val trials = ExperimentTrial.listFromJsonString(gp.experimentDataJson)
            val maxVolt = trials.maxOfOrNull { it.voltageVolt } ?: 0f

            curX = tableStartX
            // ID
            paintText.textAlign = Paint.Align.CENTER
            paintText.color = Color.parseColor("#1E293B")
            canvas.drawText("${gp.groupId}", curX + colWidths[0] / 2f, y + 17f, paintText)
            curX += colWidths[0]

            // Group Name
            paintText.textAlign = Paint.Align.LEFT
            paintText.isFakeBoldText = true
            canvas.drawText(gp.groupName, curX + 4f, y + 14f, paintText)
            paintText.isFakeBoldText = false
            paintText.textSize = 6.5f
            paintText.color = Color.parseColor("#64748B")
            canvas.drawText("P:${gp.bladeLengthCm}cm | Sudut:${gp.pitchAngleDeg}°", curX + 4f, y + 24f, paintText)
            paintText.textSize = 7.5f
            curX += colWidths[1]

            // Bahan Bilah
            paintText.textAlign = Paint.Align.LEFT
            paintText.color = Color.parseColor("#1E293B")
            canvas.drawText(gp.bladeMaterial.take(18), curX + 4f, y + 17f, paintText)
            curX += colWidths[2]

            // Progres
            paintText.textAlign = Paint.Align.CENTER
            paintText.isFakeBoldText = true
            paintText.color = Color.parseColor("#0F766E")
            canvas.drawText("${gp.progressPercent}%", curX + colWidths[3] / 2f, y + 17f, paintText)
            paintText.isFakeBoldText = false
            curX += colWidths[3]

            // Uji (Max V)
            paintText.color = Color.parseColor("#1E293B")
            val voltStr = if (maxVolt > 0) "${maxVolt}V" else "—"
            canvas.drawText(voltStr, curX + colWidths[4] / 2f, y + 17f, paintText)
            curX += colWidths[4]

            // Skor STEM
            paintText.isFakeBoldText = true
            paintText.color = Color.parseColor("#78350F")
            canvas.drawText("${gp.totalRubricScore}", curX + colWidths[5] / 2f, y + 17f, paintText)
            paintText.isFakeBoldText = false
            curX += colWidths[5]

            // Catatan
            paintText.textAlign = Paint.Align.LEFT
            paintText.textSize = 6.5f
            paintText.color = Color.parseColor("#475569")
            val notePreview = if (gp.teacherNotes.isNotBlank()) gp.teacherNotes.take(24) + "..." else "Belum dinilai"
            canvas.drawText(notePreview, curX + 4f, y + 17f, paintText)
            paintText.textSize = 7.5f

            y += rowHeight
        }

        y += 30f

        // Signature section
        paintText.textAlign = Paint.Align.RIGHT
        paintText.textSize = 8.5f
        paintText.color = Color.parseColor("#1E293B")
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val dateStr = sdf.format(Date())
        canvas.drawText("Diverifikasi pada: $dateStr", PAGE_WIDTH - MARGIN, y, paintText)
        y += 14f
        canvas.drawText("Guru Pengampu IPA Kelas 9,", PAGE_WIDTH - MARGIN, y, paintText)
        y += 35f
        paintText.isFakeBoldText = true
        canvas.drawText("Bu Maratus Sholichah, S.Pd.", PAGE_WIDTH - MARGIN, y, paintText)
        paintText.isFakeBoldText = false
        paintText.textSize = 7.5f
        paintText.color = Color.parseColor("#64748B")
        canvas.drawText("NIP. 19970512 202203 2 004", PAGE_WIDTH - MARGIN, y + 12f, paintText)
    }

    private fun drawMultilineText(
        canvas: Canvas,
        text: String,
        x: Float,
        startY: Float,
        maxWidth: Float,
        lineHeight: Float,
        paint: Paint
    ) {
        val words = text.split(" ")
        var line = ""
        var currentY = startY

        for (word in words) {
            val testLine = if (line.isEmpty()) word else "$line $word"
            val width = paint.measureText(testLine)
            if (width > maxWidth) {
                canvas.drawText(line, x, currentY, paint)
                line = word
                currentY += lineHeight
            } else {
                line = testLine
            }
        }
        if (line.isNotEmpty()) {
            canvas.drawText(line, x, currentY, paint)
        }
    }

    /**
     * Intent helper to open the PDF or share it with external apps
     */
    fun openOrSharePdf(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            if (viewIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(viewIntent)
            } else {
                // Fallback to share sheet
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, file.name)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val chooser = Intent.createChooser(shareIntent, "Buka atau Bagikan Laporan PDF")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Laporan PDF tersimpan di: ${file.name}", Toast.LENGTH_LONG).show()
        }
    }
}
