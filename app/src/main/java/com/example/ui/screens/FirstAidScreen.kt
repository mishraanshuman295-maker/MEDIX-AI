package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmergencyDirectory
import com.example.data.model.FirstAidProtocol
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer
import com.example.ui.theme.EmergencyRedOnContainer
import com.example.ui.theme.SafeEmerald
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer

@Composable
fun FirstAidScreen(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var expandedProtocolId by remember { mutableStateOf<String?>("cpr") }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("first_aid_screen")
  ) {
    item {
      MedicalDisclaimerCard(compact = true)
    }

    // Emergency Hotlines Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EmergencyRedContainer),
        modifier = Modifier.fillMaxWidth().testTag("emergency_hotlines_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Emergency,
              contentDescription = null,
              tint = EmergencyRed,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "One-Touch Emergency Hotlines",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = EmergencyRedOnContainer
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Tap to immediately open phone dialer in an emergency:",
            fontSize = 12.sp,
            color = EmergencyRedOnContainer.copy(alpha = 0.8f)
          )

          Spacer(modifier = Modifier.height(10.dp))
          EmergencyDirectory.emergencyNumbers.forEach { hotline ->
            Surface(
              onClick = {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${hotline.number}"))
                context.startActivity(intent)
              },
              shape = RoundedCornerShape(10.dp),
              color = Color.White,
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("dial_button_${hotline.number}")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Phone,
                  contentDescription = null,
                  tint = EmergencyRed,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = hotline.label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = hotline.description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                  )
                }
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = EmergencyRed
                ) {
                  Text(
                    text = hotline.number,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // Header for Protocols
    item {
      Text(
        text = "Critical First-Aid & Red Flag Protocols",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 4.dp)
      )
    }

    // Protocol List
    items(EmergencyDirectory.protocols, key = { it.id }) { protocol ->
      val isExpanded = expandedProtocolId == protocol.id
      val isCritical = protocol.urgencyLevel == "CRITICAL"

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("protocol_card_${protocol.id}")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isCritical) EmergencyRedContainer else WarningAmberContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isCritical) Icons.Default.Emergency else Icons.Default.LocalHospital,
                contentDescription = null,
                tint = if (isCritical) EmergencyRed else WarningAmber,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = protocol.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = protocol.hindiTitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary
              )
            }
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = if (isCritical) EmergencyRed else WarningAmber
            ) {
              Text(
                text = protocol.urgencyLevel,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            IconButton(
              onClick = {
                expandedProtocolId = if (isExpanded) null else protocol.id
              },
              modifier = Modifier.testTag("expand_protocol_${protocol.id}")
            ) {
              Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand"
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Key Warning
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(if (isCritical) EmergencyRedContainer.copy(alpha = 0.5f) else WarningAmberContainer.copy(alpha = 0.5f))
              .padding(8.dp)
          ) {
            Row(verticalAlignment = Alignment.Top) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = if (isCritical) EmergencyRed else WarningAmber,
                modifier = Modifier.size(16.dp).padding(top = 1.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = protocol.keyWarning,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isCritical) EmergencyRedOnContainer else MaterialTheme.colorScheme.onSurface
              )
            }
          }

          AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
              Text(
                text = "Immediate Action Steps:",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.height(4.dp))
              protocol.immediateSteps.forEach { step ->
                Row(
                  verticalAlignment = Alignment.Top,
                  modifier = Modifier.padding(vertical = 3.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SafeEmerald,
                    modifier = Modifier.size(14.dp).padding(top = 2.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = step,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "What NOT to do (Critical Safety):",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = EmergencyRed
              )
              Spacer(modifier = Modifier.height(4.dp))
              protocol.whatNotToDo.forEach { warning ->
                Row(
                  verticalAlignment = Alignment.Top,
                  modifier = Modifier.padding(vertical = 2.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    tint = EmergencyRed,
                    modifier = Modifier.size(14.dp).padding(top = 2.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = warning,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = EmergencyRedOnContainer
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
