package com.example.data.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Base64
import android.util.Log
import java.io.File
import java.io.FileInputStream

class AudioRecorderManager(private val context: Context) {
  private var mediaRecorder: MediaRecorder? = null
  private var currentFile: File? = null
  var isRecording = false
    private set

  fun startRecording(): Boolean {
    return try {
      val outputDir = context.cacheDir
      currentFile = File(outputDir, "medix_voice_query_${System.currentTimeMillis()}.m4a")

      mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
      } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
      }.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setAudioEncodingBitRate(128000)
        setAudioSamplingRate(44100)
        setOutputFile(currentFile?.absolutePath)
        prepare()
        start()
      }
      isRecording = true
      true
    } catch (e: Exception) {
      Log.e("AudioRecorderManager", "Failed to start recording: ${e.message}", e)
      isRecording = false
      false
    }
  }

  fun stopRecording(): String? {
    return try {
      if (isRecording) {
        mediaRecorder?.apply {
          stop()
          release()
        }
        mediaRecorder = null
        isRecording = false

        // Read file into Base64
        currentFile?.let { file ->
          if (file.exists() && file.length() > 0) {
            val bytes = ByteArray(file.length().toInt())
            FileInputStream(file).use { it.read(bytes) }
            Base64.encodeToString(bytes, Base64.NO_WRAP)
          } else {
            null
          }
        }
      } else {
        null
      }
    } catch (e: Exception) {
      Log.e("AudioRecorderManager", "Failed to stop recording: ${e.message}", e)
      mediaRecorder?.release()
      mediaRecorder = null
      isRecording = false
      null
    }
  }

  fun cancelRecording() {
    try {
      if (isRecording) {
        mediaRecorder?.apply {
          stop()
          release()
        }
        mediaRecorder = null
        isRecording = false
        currentFile?.delete()
      }
    } catch (e: Exception) {
      // Ignored
      mediaRecorder = null
      isRecording = false
    }
  }
}
