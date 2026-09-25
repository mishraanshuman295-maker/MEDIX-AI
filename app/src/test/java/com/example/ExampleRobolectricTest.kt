package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.remote.GeminiApiClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Medix AI", appName)
  }

  @Test
  fun `emergency detection identifies critical chest and breathing symptoms`() {
    assertTrue(GeminiApiClient.detectImmediateEmergency("I have severe crushing chest pain radiating to left arm"))
    assertTrue(GeminiApiClient.detectImmediateEmergency("Patient cannot breathe and has blue lips"))
    assertTrue(GeminiApiClient.detectImmediateEmergency("Sudden face drooping and slurred speech"))
  }

  @Test
  fun `local triage engine produces structured 4-part medical response`() {
    val result = GeminiApiClient.generateLocalTriageResponse(
      query = "Severe migraine headache with throbbing pain on right side",
      language = "English",
      isEmergency = false
    )
    assertNotNull(result)
    assertTrue(result.sympathyNote.isNotBlank())
    assertTrue(result.potentialCauses.isNotBlank())
    assertTrue(result.nextSteps.isNotBlank())
    assertTrue(result.redFlags.isNotBlank())
    assertTrue(result.recommendedSpecialist.isNotBlank())
  }
}
