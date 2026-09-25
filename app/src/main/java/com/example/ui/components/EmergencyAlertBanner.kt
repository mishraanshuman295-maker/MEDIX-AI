package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer
import com.example.ui.theme.EmergencyRedOnContainer

@Composable
fun EmergencyAlertBanner(
  isVisible: Boolean,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  AnimatedVisibility(
    visible = isVisible,
    enter = fadeIn(),
    exit = fadeOut()
  ) {
    Box(
      modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(EmergencyRedContainer)
        .padding(14.dp)
        .testTag("emergency_alert_banner")
    ) {
      Column {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(
            imageVector = Icons.Default.Emergency,
            contentDescription = "Emergency Alert",
            tint = EmergencyRed,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Emergency Warning: Immediate Attention Needed",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = EmergencyRedOnContainer,
            modifier = Modifier.weight(1f)
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Dismiss emergency banner",
              tint = EmergencyRedOnContainer,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Your symptoms or query show signs that may require urgent medical intervention (e.g., severe chest pressure, breathing difficulty, or neurological signs). Do not delay.",
          fontSize = 12.sp,
          lineHeight = 16.sp,
          color = EmergencyRedOnContainer
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Button(
            onClick = {
              val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
              context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f).testTag("call_112_button")
          ) {
            Icon(
              imageVector = Icons.Default.Phone,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Call 112 (EU/India)", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:911"))
              context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = EmergencyRedOnContainer),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f).testTag("call_911_button")
          ) {
            Icon(
              imageVector = Icons.Default.Phone,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Call 911 (US)", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
