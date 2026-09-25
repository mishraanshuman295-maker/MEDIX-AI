package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicalSpecialist
import com.example.data.model.SpecialistsCatalog
import com.example.ui.theme.MedixTealPrimary
import com.example.ui.theme.SafeEmerald
import com.example.ui.theme.SafeEmeraldContainer

@Composable
fun SpecialistsScreen(
  onAskAboutSpecialist: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var expandedSpecialistId by remember { mutableStateOf<String?>("general_physician") }

  val filteredSpecialists = remember(searchQuery) {
    if (searchQuery.isBlank()) {
      SpecialistsCatalog.list
    } else {
      SpecialistsCatalog.list.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
          it.hindiName.contains(searchQuery, ignoreCase = true) ||
          it.overview.contains(searchQuery, ignoreCase = true) ||
          it.commonConditions.any { cond -> cond.contains(searchQuery, ignoreCase = true) }
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("specialists_screen")
  ) {
    // Header & Search
    Surface(
      tonalElevation = 2.dp,
      color = MaterialTheme.colorScheme.surface,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Medical Specialists & Doctor Questions",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Learn when to see each specialist and what smart questions to ask during your consultation.",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
          modifier = Modifier.padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by doctor (e.g. Heart, Skin, Stomach)...", fontSize = 13.sp) },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = MedixTealPrimary)
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("specialist_search_input")
        )
      }
    }

    LazyColumn(
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(filteredSpecialists, key = { it.id }) { specialist ->
        val isExpanded = expandedSpecialistId == specialist.id

        SpecialistCard(
          specialist = specialist,
          isExpanded = isExpanded,
          onToggleExpand = {
            expandedSpecialistId = if (isExpanded) null else specialist.id
          },
          onAskAboutSpecialist = { onAskAboutSpecialist(specialist.name) }
        )
      }
    }
  }
}

@Composable
fun SpecialistCard(
  specialist: MedicalSpecialist,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  onAskAboutSpecialist: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .testTag("specialist_card_${specialist.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.MedicalServices,
            contentDescription = null,
            tint = MedixTealPrimary,
            modifier = Modifier.size(22.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = specialist.name,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = specialist.hindiName,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.primary
          )
        }
        IconButton(
          onClick = onToggleExpand,
          modifier = Modifier.testTag("expand_specialist_${specialist.id}")
        ) {
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) "Collapse" else "Expand"
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = specialist.overview,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
      )

      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 12.dp)) {
          // When to see
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(SafeEmeraldContainer.copy(alpha = 0.35f))
              .padding(10.dp)
          ) {
            Column {
              Text(
                text = "When to consult this specialist:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = SafeEmerald
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = specialist.whenToSee,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Conditions treated
          Text(
            text = "Common conditions treated:",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(4.dp))
          specialist.commonConditions.forEach { condition ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(vertical = 2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SafeEmerald,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = condition,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Smart questions to ask
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              .padding(12.dp)
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.HelpOutline,
                  contentDescription = null,
                  tint = MedixTealPrimary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Smart Questions to Ask Your Doctor:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = MedixTealPrimary
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              specialist.questionsToAsk.forEachIndexed { index, question ->
                Text(
                  text = "${index + 1}. $question",
                  fontSize = 12.sp,
                  lineHeight = 16.sp,
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.padding(vertical = 2.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = onAskAboutSpecialist,
            colors = ButtonDefaults.buttonColors(containerColor = MedixTealPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().testTag("ask_about_${specialist.id}")
          ) {
            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Ask Medix AI about a ${specialist.name} visit", fontSize = 12.sp)
          }
        }
      }
    }
  }
}
