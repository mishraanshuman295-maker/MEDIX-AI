package com.example.data.model

data class EmergencyHotline(
  val label: String,
  val number: String,
  val description: String,
  val category: String
)

data class FirstAidProtocol(
  val id: String,
  val title: String,
  val hindiTitle: String,
  val urgencyLevel: String, // "CRITICAL", "HIGH", "MODERATE"
  val keyWarning: String,
  val immediateSteps: List<String>,
  val whatNotToDo: List<String>
)

object EmergencyDirectory {
  val emergencyNumbers = listOf(
    EmergencyHotline(
      label = "Universal Emergency (India / Europe)",
      number = "112",
      description = "All-in-one emergency police, fire & medical dispatch",
      category = "Primary"
    ),
    EmergencyHotline(
      label = "Universal Emergency (USA / Global)",
      number = "911",
      description = "Immediate emergency dispatch services",
      category = "Primary"
    ),
    EmergencyHotline(
      label = "Ambulance / Medical SOS (India)",
      number = "102",
      description = "Free maternity and national patient ambulance service",
      category = "Medical"
    ),
    EmergencyHotline(
      label = "Disaster Medical Rescue (India)",
      number = "108",
      description = "Toll-free emergency response ambulance service",
      category = "Medical"
    ),
    EmergencyHotline(
      label = "Poison Information Center",
      number = "1800116117",
      description = "National Poison Information Center (AIIMS)",
      category = "Poison"
    )
  )

  val protocols = listOf(
    FirstAidProtocol(
      id = "cpr",
      title = "Hands-Only CPR (Cardiac Arrest)",
      hindiTitle = "कार्डिएक अरेस्ट में सीपीआर (CPR)",
      urgencyLevel = "CRITICAL",
      keyWarning = "Call 112 / 911 IMMEDIATELY before starting compressions if the person is unresponsive and not breathing normally.",
      immediateSteps = listOf(
        "1. Check responsiveness: Tap shoulders firmly and shout 'Are you OK?'",
        "2. Call emergency services (112 or 911) and ask a bystander to locate an AED (Automated External Defibrillator).",
        "3. Position person flat on a firm, level surface on their back.",
        "4. Place heel of one hand in the center of the chest (lower half of sternum), interlock fingers with your other hand.",
        "5. Push hard and fast: 100 to 120 compressions per minute (to the beat of 'Stayin' Alive').",
        "6. Depress chest at least 2 inches (5 cm) and allow full chest recoil between compressions.",
        "7. Continue uninterrupted until medical paramedics arrive or the AED guides you."
      ),
      whatNotToDo = listOf(
        "Do NOT stop compressions for more than 10 seconds.",
        "Do NOT perform CPR if the person is responsive or breathing normally.",
        "Do NOT give oral liquids or medications."
      )
    ),
    FirstAidProtocol(
      id = "choking",
      title = "Choking & Heimlich Maneuver",
      hindiTitle = "दम घुटना / हाइमलिक प्रविधि (Choking)",
      urgencyLevel = "CRITICAL",
      keyWarning = "Universal sign: Hands clutched to throat, inability to speak, cough, or breathe.",
      immediateSteps = listOf(
        "1. Ask: 'Are you choking?' If they cannot speak or make sound, act quickly.",
        "2. Deliver 5 sharp back blows between shoulder blades with heel of hand.",
        "3. Stand behind the person, wrap arms around their waist, lean them slightly forward.",
        "4. Make a fist with one hand, thumb inwards, just above their belly button.",
        "5. Grasp fist with other hand, thrust sharply inward and upward 5 times.",
        "6. Repeat 5 back blows and 5 abdominal thrusts until object is expelled or person becomes unconscious (if unconscious, begin CPR)."
      ),
      whatNotToDo = listOf(
        "Do NOT use abdominal thrusts on infants under 1 year (use 5 back slaps & 2-finger chest thrusts instead).",
        "Do NOT blindly sweep mouth with fingers unless you clearly see the loose foreign object."
      )
    ),
    FirstAidProtocol(
      id = "stroke_fast",
      title = "Stroke Detection (FAST Protocol)",
      hindiTitle = "स्ट्रोक के लक्षण पहचानें (FAST)",
      urgencyLevel = "CRITICAL",
      keyWarning = "Brain tissue dies every minute. Time lost is brain lost. Rush to hospital immediately!",
      immediateSteps = listOf(
        "F - Face Drooping: Ask them to smile. Does one side of the face droop or feel numb?",
        "A - Arm Weakness: Ask them to raise both arms. Does one arm drift downward?",
        "S - Speech Difficulty: Ask them to repeat a simple sentence. Is their speech slurred or strange?",
        "T - Time to Call: If ANY of these signs appear, call 112/911 immediately. Note the exact time symptoms began."
      ),
      whatNotToDo = listOf(
        "Do NOT give aspirin unless specifically instructed by emergency doctors (it can be fatal if stroke is hemorrhagic bleeding).",
        "Do NOT give food, water, or oral medicine as swallowing reflexes may be impaired."
      )
    ),
    FirstAidProtocol(
      id = "burns",
      title = "Thermal & Scald Burns",
      hindiTitle = "जलने पर प्राथमिक उपचार (Burns)",
      urgencyLevel = "HIGH",
      keyWarning = "Cool water only! Never apply ice or greasy home remedies.",
      immediateSteps = listOf(
        "1. Cool the burn immediately under gentle, cool running tap water for 15-20 minutes.",
        "2. Gently remove tight rings, watches, or clothing near the burn before swelling occurs (do NOT pull fabric stuck to burned flesh).",
        "3. Cover loosely with a sterile, non-stick dressing or clean plastic cling film.",
        "4. Keep the person warm to prevent hypothermia if large areas are cooled.",
        "5. Seek urgent medical care for burns larger than 3 inches, or burns on the face, hands, joints, or groin."
      ),
      whatNotToDo = listOf(
        "Do NOT apply ice, ice water, butter, toothpaste, turmeric, or oil to the burn.",
        "Do NOT burst blisters; blister skin protects against severe infection."
      )
    ),
    FirstAidProtocol(
      id = "severe_bleeding",
      title = "Severe Bleeding & Wound Hemorrhage",
      hindiTitle = "गंभीर रक्तस्राव पर दबाव (Severe Bleeding)",
      urgencyLevel = "HIGH",
      keyWarning = "Direct, continuous pressure is the single most effective way to stop external hemorrhage.",
      immediateSteps = listOf(
        "1. Protect yourself: Wear clean disposable gloves if available.",
        "2. Apply firm, continuous pressure directly over the wound with a clean sterile gauze, cloth, or towel.",
        "3. If blood soaks through, do NOT remove the first cloth; place additional padding on top and maintain steady pressure.",
        "4. If safe and without suspected broken bones, elevate the bleeding limb above heart level.",
        "5. Keep the patient calm, lying flat and warm to counteract shock until emergency help arrives."
      ),
      whatNotToDo = listOf(
        "Do NOT remove embedded objects (e.g., knife, glass shard); stabilize around the object with bulky dressings.",
        "Do NOT release direct pressure to check if bleeding has stopped prematurely."
      )
    ),
    FirstAidProtocol(
      id = "anaphylaxis",
      title = "Severe Allergic Reaction (Anaphylaxis)",
      hindiTitle = "गंभीर एलर्जी / एनाफिलेक्सिस (Anaphylaxis)",
      urgencyLevel = "CRITICAL",
      keyWarning = "Throat swelling, difficulty swallowing/breathing, widespread hives, and dizziness indicate a life-threatening reaction.",
      immediateSteps = listOf(
        "1. If patient has an Epinephrine Auto-Injector (EpiPen), administer it into the mid-outer thigh immediately.",
        "2. Call 112 / 911 immediately and state 'Severe allergic anaphylactic shock'.",
        "3. Have the person lie flat with legs elevated (or sit up slightly if breathing is very difficult).",
        "4. Loosen tight collar clothing.",
        "5. A second dose may be administered after 5-15 minutes if symptoms persist and emergency medical help has not yet arrived."
      ),
      whatNotToDo = listOf(
        "Do NOT have the person stand up or walk, as blood pressure can drop precipitously.",
        "Do NOT offer oral water or pills if breathing is laboured."
      )
    )
  )
}
