package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import com.example.ui.theme.WarningAmberOnContainer

@Composable
fun MedicalDisclaimerCard(
  modifier: Modifier = Modifier,
  compact: Boolean = false
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(WarningAmberContainer.copy(alpha = 0.85f))
      .padding(if (compact) 10.dp else 14.dp)
      .testTag("medical_disclaimer_card")
  ) {
    Row(verticalAlignment = Alignment.Top) {
      Icon(
        imageVector = Icons.Default.Warning,
        contentDescription = "Medical Disclaimer Warning",
        tint = WarningAmber,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = "Official Medical AI Disclaimer",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = WarningAmberOnContainer
        )
        Text(
          text = if (compact) {
            "Medix AI provides health guidance and education only, not medical diagnosis or prescriptions. In emergencies, call 112 or 911 immediately."
          } else {
            "Medix AI is an educational assistant designed to help you explore symptoms and prepare for doctor visits. It is NOT a substitute for licensed medical judgment, diagnosis, or prescription. If you experience severe chest pain, breathing difficulty, or sudden numbness, seek emergency care immediately."
          },
          fontSize = 11.sp,
          lineHeight = 15.sp,
          color = WarningAmberOnContainer.copy(alpha = 0.9f),
          modifier = Modifier.padding(top = 2.dp)
        )
      }
    }
  }
}
