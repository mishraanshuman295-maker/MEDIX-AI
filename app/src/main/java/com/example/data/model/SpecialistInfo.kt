package com.example.data.model

data class MedicalSpecialist(
  val id: String,
  val name: String,
  val hindiName: String,
  val iconName: String,
  val overview: String,
  val commonConditions: List<String>,
  val whenToSee: String,
  val questionsToAsk: List<String>
)

object SpecialistsCatalog {
  val list = listOf(
    MedicalSpecialist(
      id = "general_physician",
      name = "General Physician (Internist)",
      hindiName = "सामान्य चिकित्सक (General Physician)",
      iconName = "MedicalServices",
      overview = "Your primary point of care for common fevers, infections, initial symptom evaluations, routine checkups, and triage to specific specialists.",
      commonConditions = listOf(
        "Fever, Chills & Viral flu",
        "Fatigue & General weakness",
        "Hypertension & Blood pressure screening",
        "Digestive upset & minor infections",
        "Preventive health checkups"
      ),
      whenToSee = "When experiencing new, unexplained symptoms lasting more than a couple of days, mild-to-moderate fever, or whenever you are unsure which specialist to visit.",
      questionsToAsk = listOf(
        "What do you suspect is causing these symptoms?",
        "Do I need any blood tests, scans, or culture tests?",
        "Should I consult a specific organ specialist if this doesn't improve in 48-72 hours?",
        "What lifestyle or dietary modifications should I follow right now?",
        "What red-flag signs should prompt an emergency hospital visit?"
      )
    ),
    MedicalSpecialist(
      id = "cardiologist",
      name = "Cardiologist",
      hindiName = "हृदय रोग विशेषज्ञ (Heart Specialist)",
      iconName = "Favorite",
      overview = "Specializes in diagnosing and treating cardiovascular diseases, heart rhythms, arteries, and blood pressure abnormalities.",
      commonConditions = listOf(
        "Chest tightness, heaviness or angina",
        "Heart palpitations, irregular or racing pulse",
        "High blood pressure (Hypertension)",
        "Shortness of breath on mild exertion",
        "Swelling in feet/ankles related to heart function"
      ),
      whenToSee = "Seek immediate emergency care for sudden crushing chest pain radiating to the jaw/arm. See a cardiologist for recurring palpitations, exertional breathlessness, or uncontrolled high BP.",
      questionsToAsk = listOf(
        "Is my heart rhythm and ECG normal?",
        "Do you recommend an Echocardiogram, Holter monitor, or TMT stress test?",
        "Could my symptoms be related to stress, reflux, or an underlying cardiovascular condition?",
        "What safe exercise intensity is appropriate for my heart?",
        "What specific threshold of chest discomfort requires emergency hospital admission?"
      )
    ),
    MedicalSpecialist(
      id = "pulmonologist",
      name = "Pulmonologist (Chest & Lung Specialist)",
      hindiName = "श्वसन व फेफड़ा विशेषज्ञ (Lung Specialist)",
      iconName = "Air",
      overview = "Expert in respiratory system diseases, lungs, bronchial tubes, and chronic breathing issues.",
      commonConditions = listOf(
        "Persistent chronic cough (>3 weeks)",
        "Asthma, wheezing & airway constriction",
        "Chronic Bronchitis & COPD",
        "Pneumonia & chest congestion",
        "Sleep apnea & severe snoring"
      ),
      whenToSee = "When you have difficulty taking a full breath, audible wheezing, chronic mucus production, or severe cough unresponsive to standard cough syrups.",
      questionsToAsk = listOf(
        "Do I need a Spirometry / Pulmonary Function Test (PFT) or Chest X-ray?",
        "Are my symptoms triggered by allergies, environmental pollutants, or infection?",
        "How do I correctly use my inhaler/spacer device (if prescribed)?",
        "What is my personal action plan if an acute asthma/breathing flare-up occurs?"
      )
    ),
    MedicalSpecialist(
      id = "dermatologist",
      name = "Dermatologist",
      hindiName = "त्वचा रोग विशेषज्ञ (Skin & Hair Specialist)",
      iconName = "Face",
      overview = "Diagnoses and treats conditions affecting the skin, hair, scalp, nails, and mucous membranes.",
      commonConditions = listOf(
        "Eczema, Psoriasis & Contact Dermatitis",
        "Unexplained rashes, hives (urticaria) & itching",
        "Severe acne, cystic breakouts & rosacea",
        "Fungal infections (Ringworm, athlete's foot)",
        "Changing moles or suspicious skin growths"
      ),
      whenToSee = "When skin rashes are rapidly spreading, itchy, blistering, or when a mole changes color, size, or border.",
      questionsToAsk = listOf(
        "Is this rash infectious, allergic, or autoimmune?",
        "Could my laundry detergent, soap, or cosmetics be the trigger?",
        "What gentle, non-irritating moisturizers and sunscreens do you recommend?",
        "Do I need a skin patch test or biopsy for persistent lesions?"
      )
    ),
    MedicalSpecialist(
      id = "gastroenterologist",
      name = "Gastroenterologist",
      hindiName = "पेट व पाचन विशेषज्ञ (Stomach Specialist)",
      iconName = "Restaurant",
      overview = "Focuses on the digestive tract, esophagus, stomach, liver, gallbladder, pancreas, and intestines.",
      commonConditions = listOf(
        "Acid reflux (GERD) & chronic heartburn",
        "Irritable Bowel Syndrome (IBS) & bloating",
        "Gastric ulcers & persistent stomach pain",
        "Fatty liver disease & jaundice",
        "Chronic diarrhea, constipation, or rectal bleeding"
      ),
      whenToSee = "When experiencing persistent abdominal pain, difficulty swallowing, frequent heartburn, blood in stool, or unexplained weight loss.",
      questionsToAsk = listOf(
        "Could my symptoms be GERD, gastritis, or gallbladder related?",
        "Do you recommend an Endoscopy, Ultrasound, or H. pylori test?",
        "What specific trigger foods should I temporarily eliminate?",
        "How can I distinguish benign acidity from an ulcer or more serious issue?"
      )
    ),
    MedicalSpecialist(
      id = "neurologist",
      name = "Neurologist",
      hindiName = "मस्तिष्क व तंत्रिका विशेषज्ञ (Brain & Nerve Specialist)",
      iconName = "Psychology",
      overview = "Deals with disorders of the nervous system, brain, spinal cord, nerves, and neuromuscular junctions.",
      commonConditions = listOf(
        "Frequent Migraines & chronic cluster headaches",
        "Dizziness, Vertigo & balance loss",
        "Numbness, tingling or neuropathy in limbs",
        "Tremors, seizures & epilepsy",
        "Memory fog & cognitive changes"
      ),
      whenToSee = "Seek emergency care for sudden facial drooping or arm weakness (FAST stroke signs). See a neurologist for severe throbbing migraines, persistent tingling in feet/hands, or unprovoked fainting.",
      questionsToAsk = listOf(
        "Is an MRI or CT scan warranted for my headache/symptoms?",
        "What are common dietary, sleep, or posture triggers for my episodes?",
        "How can I distinguish a migraine aura from something requiring emergency care?",
        "What non-pharmacological therapies (hydration, sleep hygiene, physiotherapy) can help?"
      )
    ),
    MedicalSpecialist(
      id = "orthopedist",
      name = "Orthopedic Surgeon / Specialist",
      hindiName = "हड्डी व जोड़ विशेषज्ञ (Bone & Joint Specialist)",
      iconName = "Accessibility",
      overview = "Treats musculoskeletal trauma, sports injuries, spine diseases, joint degeneration, and bone health.",
      commonConditions = listOf(
        "Osteoarthritis & knee joint stiffness",
        "Lower back pain, sciatica & herniated discs",
        "Shoulder rotator cuff tears & frozen shoulder",
        "Ligament sprains & bone fractures",
        "Carpal tunnel syndrome & tendonitis"
      ),
      whenToSee = "When experiencing inability to bear weight on a leg/foot, visible deformity after injury, or joint stiffness interfering with daily walking.",
      questionsToAsk = listOf(
        "Do I need an X-ray or MRI to evaluate the joint/ligament?",
        "Is physical therapy and targeted muscle strengthening the right first course?",
        "What supportive braces, footwear, or ergonomic adjustments should I use?",
        "What movements or exercises must I avoid while recovering?"
      )
    ),
    MedicalSpecialist(
      id = "ent_specialist",
      name = "ENT Specialist (Otolaryngologist)",
      hindiName = "कान, नाक व गला विशेषज्ञ (ENT Doctor)",
      iconName = "Hearing",
      overview = "Manages disorders of the ear, nose, throat, sinuses, vocal cords, and related head/neck structures.",
      commonConditions = listOf(
        "Chronic Sinusitis & nasal congestion",
        "Tinnitus (ringing in ears) & hearing loss",
        "Tonsillitis & persistent severe sore throat",
        "Ear infections & fluid accumulation",
        "Hoarseness & vocal cord strain"
      ),
      whenToSee = "When experiencing persistent ear pain, reduced hearing, sinus pressure lasting >10 days, or throat pain with difficulty swallowing liquids.",
      questionsToAsk = listOf(
        "Is my sinusitis bacterial or allergic in origin?",
        "Are my Eustachian tubes blocked or inflamed?",
        "Do you recommend nasal saline rinses or specific steam protocols?",
        "Is there any sign of eardrum perforation or middle-ear fluid?"
      )
    )
  )
}
