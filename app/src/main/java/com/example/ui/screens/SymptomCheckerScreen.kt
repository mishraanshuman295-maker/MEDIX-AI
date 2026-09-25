package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BodyRegion
import com.example.data.model.SymptomCategoriesCatalog
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer
import com.example.ui.theme.EmergencyRedOnContainer
import com.example.ui.theme.MedixTealPrimary
import com.example.ui.theme.SafeEmerald
import com.example.ui.theme.SafeEmeraldContainer
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.GuidedAssessmentUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SymptomCheckerScreen(
  state: GuidedAssessmentUiState,
  onSelectRegion: (BodyRegion) -> Unit,
  onToggleSymptom: (String) -> Unit,
  onDurationChange: (String) -> Unit,
  onSeverityChange: (Float) -> Unit,
  onRedFlagsChange: (Boolean) -> Unit,
  onNotesChange: (String) -> Unit,
  onRunAnalysis: () -> Unit,
  onReset: () -> Unit,
  onSpecialistClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val durations = listOf("< 24 hours", "1-2 days", "3-7 days", "1-2 weeks", "> 1 month")

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("symptom_checker_screen")
  ) {
    item {
      MedicalDisclaimerCard(compact = true)
    }

    // Title Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.MedicalInformation,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Guided Symptom Triage",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Step-by-step clinical exploration of your symptoms",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
              )
            }
          }
        }
      }
    }

    // Step 1: Select Body Region
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Step 1: Select Body Area",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(10.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(SymptomCategoriesCatalog.regions) { region ->
              val isSelected = state.selectedRegion?.id == region.id
              FilterChip(
                selected = isSelected,
                onClick = { onSelectRegion(region) },
                label = { Text(region.title, fontSize = 12.sp) },
                leadingIcon = {
                  if (isSelected) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = null,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MedixTealPrimary,
                  selectedLabelColor = Color.White
                ),
                modifier = Modifier.testTag("region_chip_${region.id}")
              )
            }
          }
        }
      }
    }

    // Step 2: Select Specific Symptoms
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Step 2: Choose Specific Symptoms",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Select all symptoms you are currently experiencing:",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
          )
          Spacer(modifier = Modifier.height(10.dp))

          val symptoms = state.selectedRegion?.commonSymptoms ?: emptyList()
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            symptoms.forEach { symptom ->
              val isChecked = state.selectedSymptoms.contains(symptom)
              FilterChip(
                selected = isChecked,
                onClick = { onToggleSymptom(symptom) },
                label = { Text(symptom, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.testTag("symptom_chip_${symptom.hashCode()}")
              )
            }
          }
        }
      }
    }

    // Step 3: Duration & Severity Slider
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Step 3: Timeline & Discomfort Level",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "How long have you had these symptoms?",
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(6.dp))
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(durations) { dur ->
              val isSel = state.duration == dur
              FilterChip(
                selected = isSel,
                onClick = { onDurationChange(dur) },
                label = { Text(dur, fontSize = 11.sp) },
                modifier = Modifier.testTag("duration_chip_${dur.hashCode()}")
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Severity Rating: ${state.severity.toInt()}/10",
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp,
              color = when {
                state.severity >= 8 -> EmergencyRed
                state.severity >= 5 -> WarningAmber
                else -> SafeEmerald
              },
              modifier = Modifier.weight(1f)
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = when {
                state.severity >= 8 -> EmergencyRedContainer
                state.severity >= 5 -> MaterialTheme.colorScheme.secondaryContainer
                else -> SafeEmeraldContainer
              }
            ) {
              Text(
                text = when {
                  state.severity >= 8 -> "Severe / Critical"
                  state.severity >= 5 -> "Moderate"
                  else -> "Mild"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                  state.severity >= 8 -> EmergencyRedOnContainer
                  state.severity >= 5 -> MaterialTheme.colorScheme.onSecondaryContainer
                  else -> SafeEmerald
                },
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
              )
            }
          }

          Slider(
            value = state.severity,
            onValueChange = onSeverityChange,
            valueRange = 1f..10f,
            steps = 8,
            colors = SliderDefaults.colors(
              thumbColor = MedixTealPrimary,
              activeTrackColor = MedixTealPrimary
            ),
            modifier = Modifier.fillMaxWidth().testTag("severity_slider")
          )

          Spacer(modifier = Modifier.height(12.dp))
          // Emergency Red Flags Checklist Switch
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(if (state.hasRedFlags) EmergencyRedContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              .padding(12.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = if (state.hasRedFlags) EmergencyRed else WarningAmber,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "High-Risk Red Flags Present?",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = if (state.hasRedFlags) EmergencyRedOnContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Chest pressure, difficulty breathing, slurred speech, sudden numbness, or fainting",
                  fontSize = 11.sp,
                  lineHeight = 15.sp,
                  color = if (state.hasRedFlags) EmergencyRedOnContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
              }
              Switch(
                checked = state.hasRedFlags,
                onCheckedChange = onRedFlagsChange,
                colors = SwitchDefaults.colors(checkedThumbColor = EmergencyRed),
                modifier = Modifier.testTag("red_flags_switch")
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = state.additionalNotes,
            onValueChange = onNotesChange,
            label = { Text("Any other details (e.g. food eaten, fever degree, medications)", fontSize = 12.sp) },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth().testTag("additional_notes_input")
          )
        }
      }
    }

    // Action Buttons
    item {
      Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedButton(
          onClick = onReset,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.weight(1f).testTag("reset_assessment_button")
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Reset", fontSize = 13.sp)
        }

        Button(
          onClick = onRunAnalysis,
          enabled = !state.isAnalyzing,
          colors = ButtonDefaults.buttonColors(containerColor = MedixTealPrimary),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.weight(2f).testTag("analyze_symptoms_button")
        ) {
          if (state.isAnalyzing) {
            CircularProgressIndicator(
              strokeWidth = 2.dp,
              color = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Analyzing...", fontSize = 13.sp, color = Color.White)
          } else {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Analyze with Medix AI", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Assessment Results Card
    if (state.result != null) {
      val res = state.result
      val isEmergency = res.urgencyLevel == "EMERGENCY"

      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier
            .fillMaxWidth()
            .then(
              if (isEmergency) Modifier.border(2.dp, EmergencyRed, RoundedCornerShape(16.dp))
              else Modifier.border(1.5.dp, MedixTealPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            )
            .testTag("assessment_result_card")
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "Medix Clinical Triage Summary",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
              )
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isEmergency) EmergencyRedContainer else SafeEmeraldContainer
              ) {
                Text(
                  text = res.urgencyLevel,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isEmergency) EmergencyRedOnContainer else SafeEmerald,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reassurance
            Text(
              text = res.sympathyNote,
              fontSize = 13.sp,
              lineHeight = 18.sp,
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Causes
            Text(
              text = "Potential General Causes:",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = MedixTealPrimary
            )
            Text(
              text = res.potentialCauses,
              fontSize = 13.sp,
              lineHeight = 18.sp,
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Recommended Doctor
            Text(
              text = "Recommended Specialist:",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = SafeEmerald
            )
            Button(
              onClick = { onSpecialistClick(res.recommendedSpecialist) },
              colors = ButtonDefaults.buttonColors(containerColor = SafeEmeraldContainer),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.padding(top = 4.dp).testTag("recommended_specialist_button")
            ) {
              Text(
                text = "🩺 ${res.recommendedSpecialist} (Tap to view questions to ask)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SafeEmerald
              )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = res.nextSteps,
              fontSize = 13.sp,
              lineHeight = 18.sp,
              color = MaterialTheme.colorScheme.onSurface
            )

            if (res.redFlags.isNotBlank()) {
              Spacer(modifier = Modifier.height(10.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isEmergency) EmergencyRedContainer else WarningAmber.copy(alpha = 0.15f))
                  .padding(10.dp)
              ) {
                Text(
                  text = "⚠️ Red Flags: ${res.redFlags}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium,
                  color = if (isEmergency) EmergencyRedOnContainer else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }
    }
  }
}
