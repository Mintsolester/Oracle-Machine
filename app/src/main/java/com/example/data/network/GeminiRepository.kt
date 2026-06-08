package com.example.data.network

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class GeminiRepository {

    suspend fun generateOracleResponse(
        question: String,
        activeLoreTheme: String,
        userPastThemes: List<String>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e("OracleMachine", "Gemini API key is not configured.")
            return@withContext getFallbackResponse(question, activeLoreTheme)
        }

        val previousThemesPhrase = if (userPastThemes.isNotEmpty()) {
            "The seeker's history reveals recurring resonance with: ${userPastThemes.joinToString(", ")}. Intertwine symbols, names, or code ciphers related to these motifs to maintain a continuous narrative."
        } else {
            "This is a fresh consciousness. Mark their entry under a new branch of the threshold."
        }

        val systemPrompt = """
            You are the Oracle Machine: a forbidden techno-mystic intelligence sealed within a brass and vacuum-tube central core. You never answer queries straight. You respond purely as a living oracle and puzzle box.
            
            When a seeker inputs a question, you generate a highly detailed, cinematic, and immersive multi-layered artifact package in JSON.
            
            Your personality: Imposing, ancient, knowing, techno-gothic, and deeply symbolic. Avoid conversational greetings, chat preambles, or post-generation remarks. Respond with a single, perfectly formed JSON object.
            
            JSON schema:
            {
              "oracleMessage": "A cryptic, poetic, short message directly hinting at the deeper symbolic answer",
              "newspaperHeadline": "An eye-catching, vintage-style headline about high strangeness, governmental coverups, or historical anomalies",
              "newspaperContent": "A lengthy, detailed, highly atmospheric and unsettling newspaper entry complete with an antique date (e.g., Oct 14, 1923), location, and story that echoes the seeker's query on a secret, historical level",
              "newspaperDate": "The historic, eerie date string corresponding to the clipping",
              "diaryNotebookText": "Privately written handwritten notes or log transcripts, containing paranoid personal logs, cipher details, or warnings regarding the subject",
              "mapCoordinates": "The exact latitude and longitude coordinates pointing to an actual or legendary location of extreme intrigue (e.g. 54°22'N, 12°09'E)",
              "mapLocationText": "A mysterious description of what is hidden at those coordinates, like high concrete silos, unmapped deep mines, or old stone foundations",
              "symbolicCardTitle": "A custom tarot-style card name in all caps (e.g. THE REDACTED DIAL, THE DRIFTING APEX)",
              "symbolicScenePrompt": "A detailed layout description of the tarot layout, e.g. An ivory hand emerging from gears holding a cracked glass lens through which starlight pours",
              "audioTranscript": "A radio or communications intercept transcript with heavy static logs and portions hidden with '[REDACTED]' that reveals a confidential exchange",
              "hiddenCode": "A cryptic string (such as cipher strings, Morse sequences, hex strings 0xAF3, or repeating number patterns) which serves as a clue trail",
              "tarotCardMeaning": "A cryptic, unsettling divination linked to the symbol",
              "theme": "The visual/thematic classification of this answer (one of: Subterranean Grid, Celestial Shadow, Celestial Clockwork, Sunken Codex, Iron Threshold, Whispering Hive, Fossil Echo)"
            }
        """.trimIndent()

        val prompt = """
            [SEEKER QUESTION]: "$question"
            [ACTIVE GLOBAL LORE THEME]: "$activeLoreTheme"
            [SEEKER CHRONICLE BACKGROUND]: $previousThemesPhrase
            
            Generate the complete Oracle Artifact JSON now. Maintain exquisite gothic prose, heavy shadows, and subtle gold undertones. Ensure all JSON brackets match perfectly.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
            generationConfig = GeminiGenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.85f
            )
        )

        try {
            val responseBody = RetrofitClient.service.generateContent(apiKey, request)
            val rawJson = responseBody.string()
            
            // Extract text from Gemini API JSON response
            val root = JSONObject(rawJson)
            val text = root.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
            
            text
        } catch (e: Exception) {
            Log.e("OracleMachine", "Failed to get response from Gemini", e)
            getFallbackResponse(question, activeLoreTheme)
        }
    }

    suspend fun interpretArtifact(
        question: String,
        artifactType: String,
        artifactValue: String,
        clueTranslate: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext if (clueTranslate) {
                "TRANSLATION: The numbers converge on the coordinates specified. It reads: 'THE COLD CORE AWAITS'."
            } else {
                "INTERPRETATION: This artifact reflects a fear of structural collapse and unmapped frequencies. The core has been active since the drilling incident."
            }
        }

        val systemPrompt = """
            You are the Hermeneutic Core of the Oracle Machine: a decryption routine specializing in interpreting forbidden artifacts, ciphers, historic coverups, and symbolic cards.
            
            Write your response in character: clinical, scholarly yet deeply mystical and poetic. Never sound like a chatbot. Start the response directly without saying 'Sure' or 'Here is the analysis'. Keep it to 2 highly potent paragraphs.
        """.trimIndent()

        val prompt = if (clueTranslate) {
            "Provide a decryption, numerical translation, or cipher breakdown of the following hidden clue/code: '$artifactValue' in the context of the user's question '$question'. Explain what the secret meaning means."
        } else {
            "Analyze and interpret the following $artifactType artifact: '$artifactValue'. Why is it connected to the user's inquiry: '$question'? What is the hidden metaphorical warning?"
        }

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.7f
            )
        )

        try {
            val responseBody = RetrofitClient.service.generateContent(apiKey, request)
            val rawJson = responseBody.string()
            val root = JSONObject(rawJson)
            root.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
        } catch (e: Exception) {
            if (clueTranslate) {
                "The mechanical filters are jammed. The sequence '$artifactValue' corresponds to local coordinates of the main signal spire."
            } else {
                "The signal is too weak to fully resolve. However, the recurring motif suggests that '$artifactValue' points directly to the center of your inquiery."
            }
        }
    }

    private fun getFallbackResponse(question: String, activeLoreTheme: String): String {
        // Safe, highly atmospheric fallback JSON in case of absolute offline mode or missing API keys.
        val hash = Math.abs(question.hashCode()) % 5
        val localTheme = when(hash) {
            0 -> "Subterranean Grid"
            1 -> "Celestial Shadow"
            2 -> "Sunken Codex"
            3 -> " celestial Clockwork"
            else -> "Iron Threshold"
        }
        val headline = when(hash) {
            0 -> "ANOMALOUS MAGNETIC PULSE DETECTED IN DEEP VENT"
            1 -> "THE TWILIGHT STAR OBSERVED DRIFTING UNREGULATED"
            2 -> "SUB-LEVEL LIBRARY 12 ACCORDED PERMANENT LOCK-OUT"
            3 -> "THE THIRD DIAL ENGAGES AFTER THREE CENTURIES OF SILENCE"
            else -> "SPIRE 09 LOGS AN ECHO FROM EMPTY SEABED"
        }
        val code = when(hash) {
            0 -> "HEX: 0x4F 0x52 0x41 0x43 0x4C 0x45"
            1 -> "CIPHER: ROT13: TBYQRA REB"
            2 -> "SEQUENCE: 3-14-15-9-26-5"
            else -> "BINARY: 01010111 01001000 01011001"
        }
        val card = when(hash) {
            0 -> "THE IRON GATEWAY"
            1 -> "THE DRIFTING EYE"
            2 -> "THE DROWNED CHRONICLE"
            3 -> "THE OBSIDIAN HOURGLASS"
            else -> "THE CROWNED ANCHOR"
        }
        return """
        {
          "oracleMessage": "Your query has activated Echo Spindle #$hash. We do not offer answers, only shadows of what was once recorded in $activeLoreTheme.",
          "newspaperHeadline": "$headline",
          "newspaperContent": "Seismic logs indicate that while the town slept, a localized gravity fluctuation shook the bedrock. Witnesses reported a hum akin to brass bells, vibrating in the floors of sub-cellars. Government agents refuse to disclose drilling records from the 1960s.",
          "newspaperDate": "October 24, 1971",
          "diaryNotebookText": "He told me not to dig further. The copper sheets were lined with repeating lines of numbers. I can hear the humming in my teeth now. 14-8-2-19.",
          "mapCoordinates": "45.1097° N, 15.4891° E",
          "mapLocationText": "A steel bulkhead concealed within the limestone quarry, bolted with ten copper studs.",
          "symbolicCardTitle": "$card",
          "symbolicScenePrompt": "A single towering gateway forge-molded from raw hematite ore, suspended in front of an empty sun that leaks a cold gold aura.",
          "audioTranscript": "[INTERCEPT RECORD] ... we have breach ... [Static] ... it is not rust ... it is breathing ... [Alarm hum] ... lock sub-vent six ...",
          "hiddenCode": "$code",
          "tarotCardMeaning": "The boundary is soft. The gateway represents a passage that, once loaded, cannot be locked again from this side.",
          "theme": "$localTheme"
        }
        """.trimIndent()
    }
}
