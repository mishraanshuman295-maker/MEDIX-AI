package com.example.data.model

data class BodyRegion(
  val id: String,
  val title: String,
  val hindiTitle: String,
  val iconName: String,
  val commonSymptoms: List<String>
)

data class SymptomCheckAssessment(
  val region: String,
  val selectedSymptoms: List<String>,
  val duration: String,
  val severity: Int, // 1 to 10
  val hasRedFlags: Boolean,
  val userNotes: String = ""
)

object SymptomCategoriesCatalog {
  val regions = listOf(
    BodyRegion(
      id = "head_neuro",
      title = "Head & Neurological",
      hindiTitle = "सिर व तंत्रिका (Head/Brain)",
      iconName = "Psychology",
      commonSymptoms = listOf(
        "Throbbing Headache / Migraine",
        "Dizziness / Vertigo / Lightheadedness",
        "Blurry Vision / Light Sensitivity",
        "Neck Stiffness",
        "Confusion / Brain Fog",
        "Facial Tingling or Numbness"
      )
    ),
    BodyRegion(
      id = "chest_resp",
      title = "Chest & Respiratory",
      hindiTitle = "छाती व सांस (Chest/Lungs)",
      iconName = "Air",
      commonSymptoms = listOf(
        "Chest Tightness or Pressure",
        "Shortness of Breath / Wheezing",
        "Persistent Dry Cough",
        "Cough with Phlegm/Mucus",
        "Racing or Irregular Heartbeat",
        "Pain worsening with deep inhalation"
      )
    ),
    BodyRegion(
      id = "digestive",
      title = "Abdomen & Digestive",
      hindiTitle = "पेट व पाचन (Digestive)",
      iconName = "Restaurant",
      commonSymptoms = listOf(
        "Stomach Burning / Acidity / Heartburn",
        "Nausea / Vomiting",
        "Sharp Upper Abdominal Cramps",
        "Lower Abdominal Pain / Bloating",
        "Diarrhea / Loose Stools",
        "Constipation (>3 days)"
      )
    ),
    BodyRegion(
      id = "skin",
      title = "Skin, Hair & Nails",
      hindiTitle = "त्वचा व चकत्ते (Skin/Rash)",
      iconName = "Face",
      commonSymptoms = listOf(
        "Itchy Red Rash or Hives",
        "Dry, Peeling or Flaking Skin",
        "Acne Breakouts / Pimples",
        "Blisters or Fluid-Filled Bumps",
        "Sudden Bruising",
        "Burning sensation on skin"
      )
    ),
    BodyRegion(
      id = "bones_joints",
      title = "Muscles, Joints & Bones",
      hindiTitle = "हड्डी व जोड़ (Joints/Muscles)",
      iconName = "Accessibility",
      commonSymptoms = listOf(
        "Knee Pain & Stiffness",
        "Lower Back Ache / Lumbar Strain",
        "Shoulder / Neck Muscle Spasm",
        "Ankle Swelling or Sprain",
        "Morning Joint Stiffness",
        "Radiating leg tingling (Sciatica)"
      )
    ),
    BodyRegion(
      id = "general_fever",
      title = "General & Immune",
      hindiTitle = "बुखार व कमजोरी (Fever/Vitals)",
      iconName = "Thermostat",
      commonSymptoms = listOf(
        "Fever (Low to Moderate)",
        "Shivering & Chills",
        "Extreme Fatigue & Body Aches",
        "Loss of Appetite",
        "Excessive Night Sweats",
        "Dehydration & Dry Mouth"
      )
    )
  )

  val quickSymptomPrompts = listOf(
    "Chest heaviness and shortness of breath",
    "Severe throbbing headache on right side with nausea",
    "Persistent dry cough for 5 days with low fever",
    "Severe stomach acidity and burning in throat after meals",
    "Sudden red itchy hives all over forearms",
    "Knee joint swelling and stiffness when walking upstairs",
    "High fever 102°F with shivering and body ache in Hinglish"
  )
}
