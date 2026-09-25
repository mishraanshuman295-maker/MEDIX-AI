package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessageEntity
import com.example.data.remote.GroundingSource
import com.example.data.remote.NearbyMedicalPlace
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer
import com.example.ui.theme.EmergencyRedOnContainer
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.InfoBlueContainer
import com.example.ui.theme.InfoBlueOnContainer
import com.example.ui.theme.MedixCyanSecondary
import com.example.ui.theme.MedixTealPrimary
import com.example.ui.theme.SafeEmerald
import com.example.ui.theme.SafeEmeraldContainer
import com.example.ui.theme.SafeEmeraldOnContainer
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MedixResponseCard(
  message: ChatMessageEntity,
  onToggleBookmark: (Long, Boolean) -> Unit,
  onSpecialistClick: ((String) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isEmergency = message.urgency == "EMERGENCY"
  val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))

  // Parse grounding sources outside of Composable flow
  val parsedSources: List<GroundingSource> = remember(message.searchSourcesJson) {
    if (message.searchSourcesJson.isBlank()) emptyList()
    else {
      try {
        val list = mutableListOf<GroundingSource>()
        val arr = JSONArray(message.searchSourcesJson)
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          list.add(GroundingSource(obj.optString("title"), obj.optString("uri")))
        }
        list
      } catch (e: Exception) {
        emptyList()
      }
    }
  }

  // Parse nearby places outside of Composable flow
  val parsedPlaces: List<NearbyMedicalPlace> = remember(message.nearbyPlacesJson) {
    if (message.nearbyPlacesJson.isBlank()) emptyList()
    else {
      try {
        val list = mutableListOf<NearbyMedicalPlace>()
        val arr = JSONArray(message.nearbyPlacesJson)
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          list.add(
            NearbyMedicalPlace(
              name = obj.optString("name"),
              type = obj.optString("type"),
              addressOrVicinity = obj.optString("address"),
              mapsUri = obj.optString("mapsUri")
            )
          )
        }
        list
      } catch (e: Exception) {
        emptyList()
      }
    }
  }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .then(
        if (isEmergency) Modifier.border(2.dp, EmergencyRed, RoundedCornerShape(16.dp))
        else Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
      )
      .testTag("medix_response_card_${message.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Medix AI Badge + Model + Urgency indicator + Bookmark
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (isEmergency) EmergencyRed else MedixTealPrimary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isEmergency) Icons.Default.Emergency else Icons.Default.HealthAndSafety,
            contentDescription = "Medix AI",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Medix AI",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = if (isEmergency) EmergencyRedContainer else MaterialTheme.colorScheme.secondaryContainer
            ) {
              Text(
                text = if (isEmergency) "URGENT TRIAGE" else message.modelUsed.replace("-preview", "").replace("gemini-", "").uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isEmergency) EmergencyRedOnContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = timeFormatted,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
          )
        }

        IconButton(
          onClick = {
            onToggleBookmark(message.id, !message.isBookmarked)
          },
          modifier = Modifier.testTag("bookmark_button_${message.id}")
        ) {
          Icon(
            imageVector = if (message.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            contentDescription = "Bookmark this advice",
            tint = if (message.isBookmarked) MedixCyanSecondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
          )
        }

        IconButton(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Medix Advice", message.rawText)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Consultation advice copied to clipboard", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.testTag("copy_button_${message.id}")
        ) {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy advice",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Section 1: Sympathetic Acknowledgement
      val sympathy = message.sympathyNote.ifBlank {
        if (message.rawText.contains("[SYMPATHETIC ACKNOWLEDGEMENT]")) {
          message.rawText.substringAfter("[SYMPATHETIC ACKNOWLEDGEMENT]").substringBefore("[POTENTIAL CAUSES]").trim()
        } else {
          message.rawText.lines().firstOrNull() ?: ""
        }
      }

      if (sympathy.isNotBlank()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
            .padding(10.dp)
        ) {
          Text(
            text = sympathy,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
      }

      // Section 2: Potential Causes
      val causes = message.potentialCauses.ifBlank {
        if (message.rawText.contains("[POTENTIAL CAUSES]")) {
          message.rawText.substringAfter("[POTENTIAL CAUSES]").substringBefore("[RECOMMENDED NEXT STEPS").trim()
        } else {
          ""
        }
      }

      if (causes.isNotBlank()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Lightbulb,
              contentDescription = null,
              tint = MedixTealPrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Potential General Causes (Not a Diagnosis)",
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp,
              color = MedixTealPrimary
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = causes,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
      }

      // Section 3: Recommended Next Steps & Specialist
      val nextSteps = message.nextSteps.ifBlank {
        if (message.rawText.contains("[RECOMMENDED NEXT STEPS")) {
          message.rawText.substringAfter("]").substringBefore("[RED FLAGS").trim()
        } else {
          ""
        }
      }

      if (nextSteps.isNotBlank() || message.specialist.isNotBlank()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SafeEmeraldContainer.copy(alpha = 0.4f))
            .padding(12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.MedicalServices,
              contentDescription = null,
              tint = SafeEmerald,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Recommended Next Steps & Specialist",
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp,
              color = SafeEmeraldOnContainer
            )
          }

          if (message.specialist.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              AssistChip(
                onClick = { onSpecialistClick?.invoke(message.specialist) },
                label = { Text("Consult: ${message.specialist}", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                  )
                },
                colors = AssistChipDefaults.assistChipColors(
                  containerColor = MaterialTheme.colorScheme.surface,
                  labelColor = MedixTealPrimary
                )
              )
            }
          }

          if (nextSteps.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = nextSteps,
              fontSize = 13.sp,
              lineHeight = 18.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
      }

      // Section 4: Red Flags & Emergency Warning
      val redFlags = message.redFlags.ifBlank {
        if (message.rawText.contains("[RED FLAGS")) {
          message.rawText.substringAfter("[RED FLAGS").substringAfter("]").trim()
        } else {
          ""
        }
      }

      if (redFlags.isNotBlank() || isEmergency) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isEmergency) EmergencyRedContainer else WarningAmberContainer.copy(alpha = 0.4f))
            .padding(12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = if (isEmergency) EmergencyRed else WarningAmber,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Red Flags & When to Visit Emergency (ER)",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = if (isEmergency) EmergencyRedOnContainer else MaterialTheme.colorScheme.onSurface
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = redFlags.ifBlank { "Seek urgent medical attention if you experience severe shortness of breath, crushing chest discomfort, sudden numbness, or unremitting high fever." },
            fontSize = 12.sp,
            lineHeight = 17.sp,
            fontWeight = if (isEmergency) FontWeight.Medium else FontWeight.Normal,
            color = if (isEmergency) EmergencyRedOnContainer else MaterialTheme.colorScheme.onSurface
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
      }

      // Section 5: Google Search Grounding Sources (if available)
      if (parsedSources.isNotEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(InfoBlueContainer.copy(alpha = 0.35f))
            .padding(10.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Public,
              contentDescription = null,
              tint = InfoBlue,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Google Search Grounded Medical Sources",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = InfoBlueOnContainer
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          for (item in parsedSources.take(3)) {
            if (item.uri.isNotBlank()) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 2.dp)
                  .clickable {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.uri))
                    context.startActivity(browserIntent)
                  }
              ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null, tint = InfoBlue, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = item.title,
                  fontSize = 11.sp,
                  color = InfoBlue,
                  fontWeight = FontWeight.Medium,
                  maxLines = 1
                )
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
      }

      // Section 6: Google Maps Grounded Nearby Clinics & Hospitals
      if (parsedPlaces.isNotEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SafeEmeraldContainer.copy(alpha = 0.35f))
            .padding(10.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = SafeEmerald,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Google Maps Nearby ERs & Medical Clinics",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = SafeEmeraldOnContainer
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          for (place in parsedPlaces.take(3)) {
            Surface(
              onClick = {
                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(place.mapsUri))
                context.startActivity(mapIntent)
              },
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surface,
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.LocalHospital,
                  contentDescription = null,
                  tint = SafeEmerald,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = place.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "${place.type} • ${place.addressOrVicinity}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                  )
                }
                Text(
                  text = "Directions >",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = SafeEmerald
                )
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
      }

      // If parsing didn't catch sections, fallback to rawText display
      if (sympathy.isBlank() && causes.isBlank() && nextSteps.isBlank() && redFlags.isBlank()) {
        Text(
          text = message.rawText,
          fontSize = 13.sp,
          lineHeight = 19.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }
  }
}
