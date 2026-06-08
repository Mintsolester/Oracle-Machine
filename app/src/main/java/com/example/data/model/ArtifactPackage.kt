package com.example.data.model

import org.json.JSONObject

data class ArtifactPackage(
    val oracleMessage: String,
    val newspaperHeadline: String,
    val newspaperContent: String,
    val newspaperDate: String,
    val diaryNotebookText: String,
    val mapCoordinates: String,
    val mapLocationText: String,
    val symbolicCardTitle: String,
    val symbolicScenePrompt: String,
    val audioTranscript: String,
    val hiddenCode: String,
    val tarotCardMeaning: String,
    val theme: String
) {
    companion object {
        fun fromJson(jsonStr: String): ArtifactPackage {
            return try {
                val cleaned = cleanJsonString(jsonStr)
                val obj = JSONObject(cleaned)
                ArtifactPackage(
                    oracleMessage = obj.optString("oracleMessage", "The frequency shifts. The threshold does not answer immediately, yet the pattern persist."),
                    newspaperHeadline = obj.optString("newspaperHeadline", "UNEXPLAINED SOUNDS IN SUBTERRANEAN DRILLS"),
                    newspaperContent = obj.optString("newspaperContent", "On the morning of the drill, anomalous seismic registers began to mimic a repeating rhythmic pulse..."),
                    newspaperDate = obj.optString("newspaperDate", "Nov 12, 1982"),
                    diaryNotebookText = obj.optString("diaryNotebookText", "They are listening. Every time I write down the sequence, the terminal screen flickers. Key is 0x7F."),
                    mapCoordinates = obj.optString("mapCoordinates", "47.1102° N, 120.4831° W"),
                    mapLocationText = obj.optString("mapLocationText", "Grotto Section 9, ancient concrete seal"),
                    symbolicCardTitle = obj.optString("symbolicCardTitle", "THE WEAVER'S SPINDLE"),
                    symbolicScenePrompt = obj.optString("symbolicScenePrompt", "An obsidian loom weaving threads of silver light into a dark, bottomless hollow under two pale celestial bodies."),
                    audioTranscript = obj.optString("audioTranscript", "[Static] ... We found it under [REDACTED] feet of iron ore ... [High pitch screech] ... not biological ... [Static]"),
                    hiddenCode = obj.optString("hiddenCode", "CIPHER: 4-15-21-2-12-5"),
                    tarotCardMeaning = obj.optString("tarotCardMeaning", "Alignment. A thread is being pulled across timelines. Do not look directly into the weave."),
                    theme = obj.optString("theme", "Subterranean Grid")
                )
            } catch (e: Exception) {
                // Return generic fallback in case of absolute JSON failure
                ArtifactPackage(
                    oracleMessage = "The static on the line is heavy. Ask again when the stars align, or decode what has been given.",
                    newspaperHeadline = "RECORDS LOST",
                    newspaperContent = "A sudden thermal surge in Archive 04 erased 90% of the daily magnetic tapes.",
                    newspaperDate = "Unknown Epoch",
                    diaryNotebookText = "The ink has run completely wet... only the word 'THRESHOLD' is legible.",
                    mapCoordinates = "0.0000° N, 0.0000° E",
                    mapLocationText = "Deep Meridian Zero",
                    symbolicCardTitle = "THE SHATTERED COMPASS",
                    symbolicScenePrompt = "A brass compass with a cracked crystal lens, its needle point spinning wildly in pitch dark water.",
                    audioTranscript = "[STATIC INTERFERENCE] ... nothing remains ...",
                    hiddenCode = "0-0-0-0",
                    tarotCardMeaning = "Chaos. The compass spins. True North has migrated.",
                    theme = "Void Chamber"
                )
            }
        }

        private fun cleanJsonString(raw: String): String {
            var s = raw.trim()
            if (s.startsWith("```json")) {
                s = s.substringAfter("```json")
            } else if (s.startsWith("```")) {
                s = s.substringAfter("```")
            }
            if (s.endsWith("```")) {
                s = s.substringBeforeLast("```")
            }
            return s.trim()
        }
    }

    fun toJsonString(): String {
        val obj = JSONObject()
        obj.put("oracleMessage", oracleMessage)
        obj.put("newspaperHeadline", newspaperHeadline)
        obj.put("newspaperContent", newspaperContent)
        obj.put("newspaperDate", newspaperDate)
        obj.put("diaryNotebookText", diaryNotebookText)
        obj.put("mapCoordinates", mapCoordinates)
        obj.put("mapLocationText", mapLocationText)
        obj.put("symbolicCardTitle", symbolicCardTitle)
        obj.put("symbolicScenePrompt", symbolicScenePrompt)
        obj.put("audioTranscript", audioTranscript)
        obj.put("hiddenCode", hiddenCode)
        obj.put("tarotCardMeaning", tarotCardMeaning)
        obj.put("theme", theme)
        return obj.toString()
    }
}
