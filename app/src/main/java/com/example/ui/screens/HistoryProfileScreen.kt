package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.HealthProfileEntity
import com.example.ui.components.MedixResponseCard
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer
import com.example.ui.theme.MedixCyanSecondary
import com.example.ui.theme.MedixTealPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryProfileScreen(
  sessions: List<ChatSessionEntity>,
  bookmarks: List<ChatMessageEntity>,
  profile: HealthProfileEntity?,
  currentSessionId: Long?,
  onSelectSession: (Long) -> Unit,
  onDeleteSession: (Long) -> Unit,
  onUpdateProfile: (String, String, String, String, String) -> Unit,
  onToggleBookmark: (Long, Boolean) -> Unit,
  onSpecialistClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Sessions, 1: Bookmarks, 2: Profile

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("history_profile_screen")
  ) {
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MedixTealPrimary
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Consultations (${sessions.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
        icon = { Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp)) },
        modifier = Modifier.testTag("tab_consultations")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Saved Advice (${bookmarks.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
        icon = { Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp)) },
        modifier = Modifier.testTag("tab_bookmarks")
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = { Text("Health Profile", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
        icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp)) },
        modifier = Modifier.testTag("tab_profile")
      )
    }

    when (selectedTab) {
      0 -> ConsultationsTab(
        sessions = sessions,
        currentSessionId = currentSessionId,
        onSelectSession = onSelectSession,
        onDeleteSession = onDeleteSession
      )
      1 -> BookmarksTab(
        bookmarks = bookmarks,
        onToggleBookmark = onToggleBookmark,
        onSpecialistClick = onSpecialistClick
      )
      2 -> ProfileTab(
        profile = profile,
        onUpdateProfile = onUpdateProfile
      )
    }
  }
}

@Composable
fun ConsultationsTab(
  sessions: List<ChatSessionEntity>,
  currentSessionId: Long?,
  onSelectSession: (Long) -> Unit,
  onDeleteSession: (Long) -> Unit
) {
  var sessionToDelete by remember { mutableStateOf<ChatSessionEntity?>(null) }

  if (sessionToDelete != null) {
    AlertDialog(
      onDismissRequest = { sessionToDelete = null },
      title = { Text("Delete Consultation?") },
      text = { Text("Are you sure you want to delete '${sessionToDelete?.title}'? This action cannot be undone.") },
      confirmButton = {
        Button(
          onClick = {
            sessionToDelete?.let { onDeleteSession(it.id) }
            sessionToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
        ) {
          Text("Delete", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { sessionToDelete = null }) {
          Text("Cancel")
        }
      }
    )
  }

  if (sessions.isEmpty()) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
      Text(
        text = "No previous consultations found. Start a new chat to see history here.",
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
      )
    }
  } else {
    LazyColumn(
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(sessions, key = { it.id }) { session ->
        val isCurrent = session.id == currentSessionId
        val dateFormatted = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(session.createdAt))

        Card(
          onClick = { onSelectSession(session.id) },
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth().testTag("session_item_${session.id}")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (session.urgencyLevel == "EMERGENCY") EmergencyRedContainer else MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = null,
                tint = if (session.urgencyLevel == "EMERGENCY") EmergencyRed else MedixTealPrimary,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = session.title,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
                if (isCurrent) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MedixTealPrimary
                  ) {
                    Text(
                      text = "ACTIVE",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                  }
                }
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = session.lastMessage.ifBlank { "No messages" },
                fontSize = 12.sp,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
              )
              Text(
                text = dateFormatted,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
              )
            }

            IconButton(
              onClick = { sessionToDelete = session },
              modifier = Modifier.size(32.dp).testTag("delete_session_${session.id}")
            ) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete consultation",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun BookmarksTab(
  bookmarks: List<ChatMessageEntity>,
  onToggleBookmark: (Long, Boolean) -> Unit,
  onSpecialistClick: (String) -> Unit
) {
  if (bookmarks.isEmpty()) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          imageVector = Icons.Default.Bookmark,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
          modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "No saved advice yet.",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Tap the bookmark icon on any Medix AI response to save critical doctor questions and care tips here.",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
      }
    }
  } else {
    LazyColumn(
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(bookmarks, key = { it.id }) { msg ->
        MedixResponseCard(
          message = msg,
          onToggleBookmark = onToggleBookmark,
          onSpecialistClick = onSpecialistClick
        )
      }
    }
  }
}

@Composable
fun ProfileTab(
  profile: HealthProfileEntity?,
  onUpdateProfile: (String, String, String, String, String) -> Unit
) {
  var name by remember(profile) { mutableStateOf(profile?.userName ?: "Patient") }
  var ageGroup by remember(profile) { mutableStateOf(profile?.ageGroup ?: "Adult (18-64)") }
  var allergies by remember(profile) { mutableStateOf(profile?.allergies ?: "None known") }
  var conditions by remember(profile) { mutableStateOf(profile?.chronicConditions ?: "None") }
  var language by remember(profile) { mutableStateOf(profile?.preferredLanguage ?: "English") }
  var savedSuccess by remember { mutableStateOf(false) }

  val ageGroups = listOf("Infant / Child (0-12)", "Teen (13-17)", "Adult (18-64)", "Senior (65+)")
  val languages = listOf("English", "Hindi", "Hinglish")

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxSize().testTag("profile_tab_column")
  ) {
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Personal Health Context",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Medix AI uses this background to tailor age-specific advice and allergy safeguards.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
          )

          Spacer(modifier = Modifier.height(14.dp))
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name / Nickname") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
          )

          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Age Category:",
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            ageGroups.take(2).forEach { group ->
              FilterChip(
                selected = ageGroup == group,
                onClick = { ageGroup = group },
                label = { Text(group, fontSize = 11.sp) },
                modifier = Modifier.weight(1f).testTag("age_chip_${group.hashCode()}")
              )
            }
          }
          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            ageGroups.drop(2).forEach { group ->
              FilterChip(
                selected = ageGroup == group,
                onClick = { ageGroup = group },
                label = { Text(group, fontSize = 11.sp) },
                modifier = Modifier.weight(1f).testTag("age_chip_${group.hashCode()}")
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Preferred Communication Language:",
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            languages.forEach { lang ->
              FilterChip(
                selected = language == lang,
                onClick = { language = lang },
                label = { Text(lang, fontSize = 12.sp) },
                modifier = Modifier.weight(1f).testTag("lang_chip_$lang")
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = allergies,
            onValueChange = { allergies = it },
            label = { Text("Known Drug or Food Allergies") },
            placeholder = { Text("e.g. Penicillin, Sulfa drugs, Peanuts, Pollen") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().testTag("profile_allergies_input")
          )

          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = conditions,
            onValueChange = { conditions = it },
            label = { Text("Chronic Medical Conditions") },
            placeholder = { Text("e.g. Asthma, Hypertension, Diabetes, Thyroid") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().testTag("profile_conditions_input")
          )

          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = {
              onUpdateProfile(name, ageGroup, allergies, conditions, language)
              savedSuccess = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = MedixTealPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().testTag("save_profile_button")
          ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Health Profile", fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }

          if (savedSuccess) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "✓ Health profile updated successfully!",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = MedixTealPrimary,
              modifier = Modifier.align(Alignment.CenterHorizontally)
            )
          }
        }
      }
    }
  }
}
