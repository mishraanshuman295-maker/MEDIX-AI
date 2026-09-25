package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.AiModelType
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.MedixCyanSecondary
import com.example.ui.theme.MedixTealPrimary
import com.example.ui.theme.SafeEmerald

@Composable
fun GeminiControlPanel(
  selectedModel: AiModelType,
  onModelSelected: (AiModelType) -> Unit,
  searchGroundingEnabled: Boolean,
  onToggleSearchGrounding: (Boolean) -> Unit,
  mapsGroundingEnabled: Boolean,
  onToggleMapsGrounding: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .testTag("gemini_control_panel")
  ) {
    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isExpanded = !isExpanded }
      ) {
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = null,
            tint = MedixTealPrimary,
            modifier = Modifier.size(16.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Gemini Engine: ${selectedModel.displayName.substringBefore(" (")}",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(6.dp))
            if (searchGroundingEnabled) {
              Icon(Icons.Default.Public, contentDescription = "Search active", tint = InfoBlue, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(3.dp))
            }
            if (mapsGroundingEnabled) {
              Icon(Icons.Default.LocationOn, contentDescription = "Maps active", tint = SafeEmerald, modifier = Modifier.size(12.dp))
            }
          }
          Text(
            text = "Model: ${selectedModel.modelName} • Tap to switch",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
          )
        }
        IconButton(
          onClick = { isExpanded = !isExpanded },
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null
          )
        }
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 10.dp)) {
          Text(
            text = "Select Gemini Model:",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = MedixTealPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))

          // Model Options
          AiModelType.values().forEach { model ->
            val isSelected = model == selectedModel
            Surface(
              onClick = { onModelSelected(model) },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .testTag("model_option_${model.name}")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                if (isSelected) {
                  Icon(Icons.Default.Check, contentDescription = null, tint = MedixTealPrimary, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = model.displayName,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "ID: ${model.modelName}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                  )
                }
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = MedixCyanSecondary.copy(alpha = 0.2f)
                ) {
                  Text(
                    text = model.badge,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MedixCyanSecondary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Grounding Toggles
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Public, contentDescription = null, tint = InfoBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text("Google Search Grounding", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              Text("Up-to-date web medical references", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Switch(
              checked = searchGroundingEnabled,
              onCheckedChange = onToggleSearchGrounding,
              colors = SwitchDefaults.colors(checkedThumbColor = InfoBlue),
              modifier = Modifier.testTag("search_grounding_toggle")
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = SafeEmerald, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text("Google Maps Grounding", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              Text("Find nearby clinics & emergency ERs", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Switch(
              checked = mapsGroundingEnabled,
              onCheckedChange = onToggleMapsGrounding,
              colors = SwitchDefaults.colors(checkedThumbColor = SafeEmerald),
              modifier = Modifier.testTag("maps_grounding_toggle")
            )
          }
        }
      }
    }
  }
}
