package com.example.data

import org.json.JSONArray
import org.json.JSONObject

data class ExperimentTrial(
    val trialNumber: Int = 1,
    val bladeCount: Int = 3,
    val windSpeed: String = "Sedang",
    val voltageVolt: Float = 0f,
    val currentMa: Float = 0f,
    val powerMw: Float = 0f,
    val ledStatus: String = "Mati",
    val notes: String = ""
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("trialNumber", trialNumber)
            put("bladeCount", bladeCount)
            put("windSpeed", windSpeed)
            put("voltageVolt", voltageVolt.toDouble())
            put("currentMa", currentMa.toDouble())
            put("powerMw", powerMw.toDouble())
            put("ledStatus", ledStatus)
            put("notes", notes)
        }
    }

    companion object {
        fun fromJson(obj: JSONObject): ExperimentTrial {
            return ExperimentTrial(
                trialNumber = obj.optInt("trialNumber", 1),
                bladeCount = obj.optInt("bladeCount", 3),
                windSpeed = obj.optString("windSpeed", "Sedang"),
                voltageVolt = obj.optDouble("voltageVolt", 0.0).toFloat(),
                currentMa = obj.optDouble("currentMa", 0.0).toFloat(),
                powerMw = obj.optDouble("powerMw", 0.0).toFloat(),
                ledStatus = obj.optString("ledStatus", "Mati"),
                notes = obj.optString("notes", "")
            )
        }

        fun listToJsonString(trials: List<ExperimentTrial>): String {
            val array = JSONArray()
            trials.forEach { array.put(it.toJson()) }
            return array.toString()
        }

        fun listFromJsonString(jsonStr: String): List<ExperimentTrial> {
            if (jsonStr.isBlank()) return emptyList()
            return try {
                val array = JSONArray(jsonStr)
                val list = mutableListOf<ExperimentTrial>()
                for (i in 0 until array.length()) {
                    list.add(fromJson(array.getJSONObject(i)))
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
