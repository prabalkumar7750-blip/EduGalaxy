package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AmberGold
import com.example.ui.theme.ElectricSky
import com.example.ui.theme.GalaxyGold
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import com.example.ui.theme.NeonViolet

data class EducationTier(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val accentColor: Color,
    val levels: List<String>,
    val streams: List<String> = emptyList()
)

data class SubjectMetadata(
    val id: String,
    val name: String,
    val category: String, // "School", "College", "Competitive"
    val shortCode: String,
    val accentColor: Color,
    val secondaryColor: Color,
    val description: String,
    val chapters: List<ChapterInfo>
)

data class ChapterInfo(
    val id: String,
    val title: String,
    val topics: List<String>,
    val estimatedMinutes: Int
)

object EducationCatalog {

    val educationTiers: List<EducationTier> = listOf(
        EducationTier(
            id = "primary",
            title = "Class 1–5",
            subtitle = "Primary Foundation",
            badge = "NCERT • Foundational",
            accentColor = NeonBlue,
            levels = listOf("Class 1", "Class 2", "Class 3", "Class 4", "Class 5")
        ),
        EducationTier(
            id = "middle",
            title = "Class 6–8",
            subtitle = "Middle School",
            badge = "Concept Builder",
            accentColor = NeonGreen,
            levels = listOf("Class 6", "Class 7", "Class 8")
        ),
        EducationTier(
            id = "secondary",
            title = "Class 9–10",
            subtitle = "Board Foundation",
            badge = "Board Mastery",
            accentColor = AmberGold,
            levels = listOf("Class 9", "Class 10")
        ),
        EducationTier(
            id = "senior_sec",
            title = "Class 11–12",
            subtitle = "Senior Secondary",
            badge = "Streams & Boards",
            accentColor = NeonPurple,
            levels = listOf("Class 11", "Class 12"),
            streams = listOf("Science (PCM)", "Science (PCB)", "Commerce", "Arts / Humanities")
        ),
        EducationTier(
            id = "college",
            title = "College & UG",
            subtitle = "Degrees & Technical",
            badge = "UG • Polytechnic",
            accentColor = NeonCyan,
            levels = listOf(
                "BA", "BSc", "BCom", "BCA", "BBA", "BTech", "BE",
                "BSc Computer Science", "BSc Data Science", "BSc AI & ML",
                "BSc Mathematics", "BSc Physics", "BSc Chemistry", "BSc Biology",
                "BVoc", "Diploma", "ITI", "Polytechnic"
            )
        ),
        EducationTier(
            id = "postgrad",
            title = "Postgraduate",
            subtitle = "Masters & Research",
            badge = "PG • MTech • MBA",
            accentColor = NeonPink,
            levels = listOf(
                "MA", "MSc", "MCom", "MCA", "MBA", "MTech",
                "MSc Data Science", "MSc AI & ML", "Other PG Programs"
            )
        ),
        EducationTier(
            id = "competitive",
            title = "Competitive Exams",
            subtitle = "National & State",
            badge = "JEE • NEET • UPSC",
            accentColor = GalaxyGold,
            levels = listOf(
                "IIT JEE", "NEET", "CUET", "UPSC", "SSC", "Railway RRB",
                "Banking", "NDA", "CDS", "State Government Exams",
                "Teaching Exams", "Police Recruitment", "Defence Recruitment",
                "Other Government Exams"
            )
        )
    )

    val subjects: List<SubjectMetadata> = listOf(
        // School & Core Subjects
        SubjectMetadata(
            id = "mathematics",
            name = "Mathematics",
            category = "School",
            shortCode = "MATH",
            accentColor = NeonBlue,
            secondaryColor = ElectricSky,
            description = "Arithmetic, Algebra, Geometry, Trigonometry & Calculus",
            chapters = listOf(
                ChapterInfo("math_real", "Real Numbers & Number Systems", listOf("Euclid's Division", "Fundamental Theorem of Arithmetic", "Irrational Numbers"), 35),
                ChapterInfo("math_quad", "Quadratic Equations", listOf("Factorisation", "Quadratic Formula", "Nature of Roots"), 45),
                ChapterInfo("math_trig", "Trigonometry & Identities", listOf("Trigonometric Ratios", "Heights & Distances", "Standard Identities"), 50),
                ChapterInfo("math_calc", "Differential & Integral Calculus", listOf("Limits & Continuity", "Derivatives", "Definite Integrals"), 60)
            )
        ),
        SubjectMetadata(
            id = "science",
            name = "Science",
            category = "School",
            shortCode = "SCI",
            accentColor = NeonCyan,
            secondaryColor = NeonBlue,
            description = "Integrated Physics, Chemistry, Biology & Scientific Inquiry",
            chapters = listOf(
                ChapterInfo("sci_chem_rxn", "Chemical Reactions & Equations", listOf("Balancing Equations", "Types of Reactions", "Oxidation & Reduction"), 40),
                ChapterInfo("sci_life", "Life Processes", listOf("Nutrition", "Respiration", "Transportation & Excretion"), 45),
                ChapterInfo("sci_light", "Light: Reflection & Refraction", listOf("Spherical Mirrors", "Snell's Law", "Lens Formula"), 50)
            )
        ),
        SubjectMetadata(
            id = "physics",
            name = "Physics",
            category = "School",
            shortCode = "PHY",
            accentColor = NeonPurple,
            secondaryColor = NeonViolet,
            description = "Mechanics, Electromagnetism, Optics & Modern Physics",
            chapters = listOf(
                ChapterInfo("phy_laws", "Laws of Motion & Kinematics", listOf("Newton's Laws", "Friction", "Circular Dynamics"), 50),
                ChapterInfo("phy_elec", "Electrostatics & Gauss Law", listOf("Coulomb's Law", "Electric Flux", "Capacitance"), 55),
                ChapterInfo("phy_optics", "Ray & Wave Optics", listOf("Total Internal Reflection", "Interference", "Diffraction"), 45)
            )
        ),
        SubjectMetadata(
            id = "chemistry",
            name = "Chemistry",
            category = "School",
            shortCode = "CHEM",
            accentColor = NeonGreen,
            secondaryColor = NeonCyan,
            description = "Physical, Organic & Inorganic Chemistry",
            chapters = listOf(
                ChapterInfo("chem_atom", "Atomic Structure & Periodicity", listOf("Quantum Numbers", "Orbitals", "Periodic Trends"), 45),
                ChapterInfo("chem_bond", "Chemical Bonding", listOf("VSEPR Theory", "Hybridization", "Molecular Orbital Theory"), 50),
                ChapterInfo("chem_org", "Organic Reactions & Mechanisms", listOf("Nucleophilic Substitution", "Aldol Condensation", "Aromaticity"), 55)
            )
        ),
        SubjectMetadata(
            id = "biology",
            name = "Biology",
            category = "School",
            shortCode = "BIO",
            accentColor = AmberGold,
            secondaryColor = GalaxyGold,
            description = "Botany, Zoology, Genetics & Human Physiology",
            chapters = listOf(
                ChapterInfo("bio_cell", "Cell: The Unit of Life", listOf("Prokaryotic vs Eukaryotic", "Organelles", "Cell Cycle"), 40),
                ChapterInfo("bio_gen", "Molecular Basis of Inheritance", listOf("DNA Replication", "Transcription", "Genetic Code"), 55),
                ChapterInfo("bio_eco", "Ecology & Biodiversity", listOf("Ecosystem Pyramids", "Conservation", "Biogeochemical Cycles"), 35)
            )
        ),
        SubjectMetadata(
            id = "social_science",
            name = "Social Science",
            category = "School",
            shortCode = "SST",
            accentColor = ElectricSky,
            secondaryColor = NeonGreen,
            description = "History, Geography, Civics & Economics Foundations",
            chapters = listOf(
                ChapterInfo("sst_nat", "Nationalism in India", listOf("Non-Cooperation Movement", "Civil Disobedience", "Salt March"), 40),
                ChapterInfo("sst_fed", "Federalism & Democracy", listOf("Union & State Lists", "Decentralisation", "Constitutional Rights"), 35)
            )
        ),
        SubjectMetadata(
            id = "english",
            name = "English",
            category = "School",
            shortCode = "ENG",
            accentColor = NeonViolet,
            secondaryColor = NeonBlue,
            description = "Grammar, Reading Comprehension, Literature & Writing",
            chapters = listOf(
                ChapterInfo("eng_gram", "Tenses, Voice & Narration", listOf("Active/Passive", "Direct/Indirect Speech", "Modals"), 35),
                ChapterInfo("eng_comp", "Comprehension & Essay Writing", listOf("Critical Inference", "Formal Letters", "Precis Writing"), 40)
            )
        ),
        SubjectMetadata(
            id = "hindi",
            name = "Hindi",
            category = "School",
            shortCode = "HIN",
            accentColor = AmberGold,
            secondaryColor = NeonRose,
            description = "हिंदी व्याकरण, गद्य-पद्य साहित्य एवं रचनात्मक लेखन",
            chapters = listOf(
                ChapterInfo("hin_vyak", "संधि, समास एवं अलंकार", listOf("स्वर संधि", "तत्पुरुष समास", "अर्थालंकार"), 40),
                ChapterInfo("hin_sahitya", "काव्य एवं गद्य खंड", listOf("कबीर के दोहे", "प्रेमचंद की कहानियाँ", "निबंध लेखन"), 35)
            )
        ),
        SubjectMetadata(
            id = "sanskrit",
            name = "Sanskrit",
            category = "School",
            shortCode = "SKT",
            accentColor = GalaxyGold,
            secondaryColor = AmberGold,
            description = "संस्कृत व्याकरण, श्लोक, धातु रूप एवं अनुवाद",
            chapters = listOf(
                ChapterInfo("skt_gram", "शब्द रूप एवं धातु रूप", listOf("लट् लकार", "लृट् लकार", "कारक विभक्ति"), 30),
                ChapterInfo("skt_shlok", "सुभाषितानि एवं श्लोक व्याख्या", listOf("श्लोक अन्वय", "संधि विच्छेद"), 30)
            )
        ),
        SubjectMetadata(
            id = "history",
            name = "History",
            category = "School",
            shortCode = "HIS",
            accentColor = NeonGreen,
            secondaryColor = GalaxyGold,
            description = "Ancient, Medieval, Modern Indian & World History",
            chapters = listOf(
                ChapterInfo("his_indus", "Harappan & Vedic Civilisation", listOf("Town Planning", "Trade & Seals", "Vedic Texts"), 40),
                ChapterInfo("his_mod", "Indian Freedom Struggle", listOf("1857 Revolt", "Gandhian Era", "Constituent Assembly"), 50)
            )
        ),
        SubjectMetadata(
            id = "geography",
            name = "Geography",
            category = "School",
            shortCode = "GEO",
            accentColor = NeonCyan,
            secondaryColor = NeonGreen,
            description = "Physical Geography, Indian Monsoon, Resources & Maps",
            chapters = listOf(
                ChapterInfo("geo_climate", "Indian Monsoon & Climate", listOf("Jet Streams", "ITCZ", "Western Disturbances"), 40),
                ChapterInfo("geo_drain", "Drainage & River Systems", listOf("Himalayan Rivers", "Peninsular Rivers"), 35)
            )
        ),
        SubjectMetadata(
            id = "civics",
            name = "Civics",
            category = "School",
            shortCode = "CIV",
            accentColor = NeonBlue,
            secondaryColor = NeonPurple,
            description = "Indian Constitution, Parliament, Judiciary & Governance",
            chapters = listOf(
                ChapterInfo("civ_const", "Fundamental Rights & Duties", listOf("Article 14–32", "Directive Principles", "Writs"), 45),
                ChapterInfo("civ_parl", "Parliament & Executive", listOf("Lok Sabha & Rajya Sabha", "Legislative Process"), 40)
            )
        ),
        SubjectMetadata(
            id = "economics",
            name = "Economics",
            category = "School",
            shortCode = "ECO",
            accentColor = NeonPink,
            secondaryColor = NeonPurple,
            description = "Microeconomics, Macroeconomics, Indian Economy & Finance",
            chapters = listOf(
                ChapterInfo("eco_gdp", "National Income & GDP", listOf("GDP vs GNP", "Value Added Method", "Fiscal Policy"), 45),
                ChapterInfo("eco_mon", "Money, Banking & RBI", listOf("Repo Rate", "Credit Creation", "Monetary Policy"), 40)
            )
        ),
        // College & Tech Subjects
        SubjectMetadata(
            id = "computer_science",
            name = "Computer Science",
            category = "College",
            shortCode = "CS",
            accentColor = NeonBlue,
            secondaryColor = NeonCyan,
            description = "Programming, OS, Computer Networks & Architecture",
            chapters = listOf(
                ChapterInfo("cs_os", "Operating Systems & Concurrency", listOf("Process Scheduling", "Deadlocks", "Virtual Memory"), 50),
                ChapterInfo("cs_net", "Computer Networks & TCP/IP", listOf("OSI Layers", "Subnetting", "HTTP & TLS"), 45)
            )
        ),
        SubjectMetadata(
            id = "programming",
            name = "Programming",
            category = "College",
            shortCode = "CODE",
            accentColor = ElectricSky,
            secondaryColor = NeonViolet,
            description = "C, C++, Java, Python & Kotlin Problem Solving",
            chapters = listOf(
                ChapterInfo("prog_oop", "Object-Oriented Design", listOf("Encapsulation", "Polymorphism", "SOLID Principles"), 45),
                ChapterInfo("prog_rec", "Recursion & Dynamic Programming", listOf("Memoization", "Tabulation", "Backtracking"), 55)
            )
        ),
        SubjectMetadata(
            id = "data_structures",
            name = "Data Structures",
            category = "College",
            shortCode = "DSA",
            accentColor = NeonCyan,
            secondaryColor = NeonGreen,
            description = "Arrays, Trees, Graphs, Heaps & Algorithm Complexity",
            chapters = listOf(
                ChapterInfo("dsa_trees", "Binary Trees, BST & AVL", listOf("Traversals", "Lowest Common Ancestor", "Rotations"), 55),
                ChapterInfo("dsa_graphs", "Graph Algorithms", listOf("BFS & DFS", "Dijkstra", "Minimum Spanning Tree"), 60)
            )
        ),
        SubjectMetadata(
            id = "dbms",
            name = "Database Management",
            category = "College",
            shortCode = "DBMS",
            accentColor = NeonPurple,
            secondaryColor = NeonCyan,
            description = "Relational Algebra, SQL, Normalization & Transactions",
            chapters = listOf(
                ChapterInfo("dbms_norm", "Normalization & Functional Dependencies", listOf("1NF to BCNF", "Lossless Join"), 45),
                ChapterInfo("dbms_acid", "Transactions & Concurrency Control", listOf("ACID Properties", "2PL Locking", "B+ Trees"), 50)
            )
        ),
        SubjectMetadata(
            id = "ai_ml",
            name = "Data Science & AI",
            category = "College",
            shortCode = "AI",
            accentColor = NeonCyan,
            secondaryColor = NeonPurple,
            description = "Machine Learning, Neural Networks, Statistics & Data Analytics",
            chapters = listOf(
                ChapterInfo("ai_ml_sup", "Supervised Learning & Regression", listOf("Gradient Descent", "Bias-Variance Tradeoff", "Regularization"), 55),
                ChapterInfo("ai_nn", "Deep Learning & Transformers", listOf("Backpropagation", "CNNs", "Self-Attention Mechanism"), 60)
            )
        ),
        SubjectMetadata(
            id = "statistics",
            name = "Statistics",
            category = "College",
            shortCode = "STAT",
            accentColor = ElectricSky,
            secondaryColor = AmberGold,
            description = "Probability Distributions, Hypothesis Testing & Bayesian Inference",
            chapters = listOf(
                ChapterInfo("stat_prob", "Probability & Random Variables", listOf("Bayes Theorem", "Normal Distribution", "Poisson"), 45),
                ChapterInfo("stat_test", "Hypothesis Testing & ANOVA", listOf("p-value", "t-test", "Chi-Square"), 50)
            )
        ),
        SubjectMetadata(
            id = "accounts",
            name = "Accounts",
            category = "College",
            shortCode = "ACCT",
            accentColor = AmberGold,
            secondaryColor = NeonGreen,
            description = "Financial Accounting, Balance Sheets & Corporate Accounting",
            chapters = listOf(
                ChapterInfo("acct_bal", "Journal, Ledger & Trial Balance", listOf("Golden Rules", "Depreciation", "Bank Reconciliation"), 45),
                ChapterInfo("acct_corp", "Company Accounts & Cash Flow", listOf("Share Capital", "Ratio Analysis", "Cash Flow Statement"), 50)
            )
        ),
        SubjectMetadata(
            id = "business_studies",
            name = "Business Studies",
            category = "College",
            shortCode = "BBA",
            accentColor = NeonPink,
            secondaryColor = GalaxyGold,
            description = "Management Principles, Marketing, Finance & Entrepreneurship",
            chapters = listOf(
                ChapterInfo("bs_mgmt", "Principles of Management", listOf("Fayol's 14 Principles", "Planning & Organising"), 35),
                ChapterInfo("bs_mkt", "Marketing Mix & Financial Markets", listOf("4 Ps of Marketing", "SEBI & Stock Exchanges"), 40)
            )
        ),
        SubjectMetadata(
            id = "botany_zoology",
            name = "Botany & Zoology",
            category = "College",
            shortCode = "B/Z",
            accentColor = NeonGreen,
            secondaryColor = AmberGold,
            description = "Plant Physiology, Taxonomy, Chordates & Developmental Biology",
            chapters = listOf(
                ChapterInfo("bz_plant", "Plant Physiology & Photosynthesis", listOf("C3 & C4 Cycle", "Transpiration"), 45),
                ChapterInfo("bz_anim", "Animal Kingdom & Embryology", listOf("Chordate Classification", "Cleavage & Gastrulation"), 45)
            )
        ),
        // Competitive Exam Subjects
        SubjectMetadata(
            id = "reasoning",
            name = "Reasoning",
            category = "Competitive",
            shortCode = "RSN",
            accentColor = NeonRose,
            secondaryColor = AmberGold,
            description = "Logical, Analytical, Verbal & Non-Verbal Reasoning",
            chapters = listOf(
                ChapterInfo("rsn_syl", "Syllogisms & Seating Arrangement", listOf("Venn Diagrams", "Circular Puzzles", "Blood Relations"), 40),
                ChapterInfo("rsn_ser", "Number Series & Coding-Decoding", listOf("Pattern Recognition", "Direction Sense"), 35)
            )
        ),
        SubjectMetadata(
            id = "gk_gs",
            name = "GK & GS",
            category = "Competitive",
            shortCode = "GK",
            accentColor = NeonGreen,
            secondaryColor = GalaxyGold,
            description = "General Knowledge, General Science, Current Affairs & Computer Awareness",
            chapters = listOf(
                ChapterInfo("gk_pol", "Indian Polity & Static GK", listOf("Constitutional Amendments", "National Parks", "Awards"), 35),
                ChapterInfo("gk_ca", "Science & Tech Current Affairs", listOf("ISRO Missions", "Digital India", "Global Summits"), 30)
            )
        ),
        SubjectMetadata(
            id = "quant_aptitude",
            name = "Quantitative Aptitude",
            category = "Competitive",
            shortCode = "QUANT",
            accentColor = GalaxyGold,
            secondaryColor = NeonBlue,
            description = "Percentages, Profit & Loss, Time & Work, Speed & Data Interpretation",
            chapters = listOf(
                ChapterInfo("qa_pct", "Percentages, Ratio & Profit/Loss", listOf("Successive Discounts", "Compound Interest"), 45),
                ChapterInfo("qa_tw", "Time, Work & Data Interpretation", listOf("Pipes & Cisterns", "Bar & Pie Charts"), 45)
            )
        )
    )

    val pdfCategories: List<String> = listOf(
        "All", "NCERT Books", "Class Notes", "College Notes", "University Notes",
        "Chapter-wise Notes", "Formula Sheets", "Revision Notes", "Important Questions",
        "PYQ Papers", "Practical Files", "Syllabus PDFs", "Exam Preparation PDFs"
    )

    val universityCategories: List<String> = listOf(
        "All", "IIT", "NIT", "IIIT", "Central University", "State University",
        "Government College", "Private University", "Open University", "Technical Institution"
    )

    // Curated verified starter catalog (persisted/augmented with live Firestore admin items)
    val starterQuestions: List<QuestionItem> = listOf(
        QuestionItem(
            id = "q_math_quad_1",
            subject = "Mathematics",
            academicLevel = "Class 10",
            chapter = "Quadratic Equations",
            topic = "Nature of Roots",
            questionType = "MCQ",
            difficulty = "Medium",
            marks = 2,
            year = "Practice",
            source = "NCERT Exemplar Concept",
            statement = "For what value of k does the quadratic equation x^2 - 4x + k = 0 have two equal real roots?",
            options = listOf("k = 4", "k = 2", "k = -4", "k = 8"),
            correctAnswer = "k = 4",
            explanationEn = "A quadratic equation ax^2 + bx + c = 0 has equal real roots when the discriminant D = b^2 - 4ac = 0. Here a = 1, b = -4, c = k. So (-4)^2 - 4(1)(k) = 0 -> 16 - 4k = 0 -> k = 4.",
            explanationHi = "किसी द्विघात समीकरण ax^2 + bx + c = 0 के बराबर वास्तविक मूल तब होते हैं जब विविक्तकर D = b^2 - 4ac = 0 हो। यहाँ (-4)^2 - 4(1)(k) = 0 -> 16 - 4k = 0 -> k = 4 प्राप्त होता है।",
            aiGenerated = false,
            repeatedTag = true,
            published = true
        ),
        QuestionItem(
            id = "q_phy_gauss_1",
            subject = "Physics",
            academicLevel = "Class 12",
            chapter = "Electrostatics & Gauss Law",
            topic = "Electric Flux",
            questionType = "MCQ",
            difficulty = "Easy",
            marks = 4,
            year = "Practice",
            source = "NCERT Class 12 Physics Ch 1",
            statement = "A point charge +q is placed at the centre of a cube of side L. What is the total electric flux emerging out of all six faces of the cube?",
            options = listOf("q / epsilon_0", "q / (6 * epsilon_0)", "6q / epsilon_0", "Zero"),
            correctAnswer = "q / epsilon_0",
            explanationEn = "By Gauss's Law, the total electric flux through any closed surface equals the enclosed charge divided by permittivity of free space: Phi = q_enclosed / epsilon_0.",
            explanationHi = "गॉस के नियम के अनुसार किसी भी बंद पृष्ठ से गुजरने वाला कुल विद्युत फ्लक्स उसमें परिबद्ध आवेश और epsilon_0 के अनुपात (q / epsilon_0) के बराबर होता है।",
            aiGenerated = false,
            repeatedTag = true,
            published = true
        ),
        QuestionItem(
            id = "q_chem_bond_1",
            subject = "Chemistry",
            academicLevel = "IIT JEE",
            chapter = "Chemical Bonding",
            topic = "Hybridization",
            questionType = "MCQ",
            difficulty = "Medium",
            marks = 4,
            year = "Practice",
            source = "JEE Concept Bank",
            statement = "What is the hybridization and geometry of SF6 (Sulfur Hexafluoride)?",
            options = listOf("sp3d2, Octahedral", "sp3d, Trigonal bipyramidal", "sp3, Tetrahedral", "dsp2, Square planar"),
            correctAnswer = "sp3d2, Octahedral",
            explanationEn = "Sulfur in SF6 has 6 bond pairs and 0 lone pairs (steric number = 6), giving sp3d2 hybridization and a symmetrical octahedral geometry.",
            explanationHi = "SF6 में सल्फर के चारों ओर 6 आबंध युग्म और 0 एकाकी युग्म होते हैं (स्टेरिक संख्या = 6), जिससे इसका संकरण sp3d2 और ज्यामिति अष्टफलकीय (Octahedral) होती है।",
            aiGenerated = false,
            repeatedTag = true,
            published = true
        ),
        QuestionItem(
            id = "q_bio_dna_1",
            subject = "Biology",
            academicLevel = "NEET",
            chapter = "Molecular Basis of Inheritance",
            topic = "DNA Replication",
            questionType = "MCQ",
            difficulty = "Medium",
            marks = 4,
            year = "Practice",
            source = "NCERT Biology Class 12",
            statement = "Which enzyme catalyzes the polymerization of deoxynucleotides during DNA replication in the 5' -> 3' direction?",
            options = listOf("DNA Polymerase III", "RNA Primase", "DNA Ligase", "Topoisomerase"),
            correctAnswer = "DNA Polymerase III",
            explanationEn = "DNA-dependent DNA polymerase catalyzes polymerization only in one direction, that is 5' -> 3'. DNA Ligase joins Okazaki fragments.",
            explanationHi = "DNA निर्भर DNA पॉलीमरेज़ (DNA Polymerase III) केवल 5' -> 3' दिशा में ही डीऑक्सीन्यूक्लियोटाइड्स के बहुलकीकरण को उत्प्रेरित करता है।",
            aiGenerated = false,
            repeatedTag = false,
            published = true
        ),
        QuestionItem(
            id = "q_dsa_tree_1",
            subject = "Data Structures",
            academicLevel = "BTech",
            chapter = "Binary Trees, BST & AVL",
            topic = "Time Complexity",
            questionType = "MCQ",
            difficulty = "Easy",
            marks = 2,
            year = "Practice",
            source = "University CS Core",
            statement = "What is the worst-case time complexity of searching for a key in a balanced AVL Tree with n nodes?",
            options = listOf("O(log n)", "O(n)", "O(n log n)", "O(1)"),
            correctAnswer = "O(log n)",
            explanationEn = "An AVL tree maintains strict height balance (|balance factor| <= 1), guaranteeing height O(log n) and hence O(log n) search in the worst case.",
            explanationHi = "AVL ट्री की ऊँचाई हमेशा संतुलित रहती है (O(log n)), इसलिए इसमें सबसे खराब स्थिति (worst-case) में भी खोज का समय O(log n) होता है।",
            aiGenerated = false,
            repeatedTag = true,
            published = true
        ),
        QuestionItem(
            id = "q_ai_grad_1",
            subject = "Data Science & AI",
            academicLevel = "BSc AI & ML",
            chapter = "Supervised Learning & Regression",
            topic = "Regularization",
            questionType = "Conceptual",
            difficulty = "Hard",
            marks = 5,
            year = "AI Practice",
            source = "EduGalaxy AI Expected Practice",
            statement = "Explain how L1 (Lasso) and L2 (Ridge) regularization differ in controlling model overfitting.",
            options = listOf(
                "L1 adds |w| penalty producing sparse weights; L2 adds w^2 penalty shrinking weights smoothly",
                "L1 increases learning rate; L2 removes training samples",
                "Both L1 and L2 only apply to K-Means clustering",
                "L2 forces exact zero weights while L1 never shrinks weights"
            ),
            correctAnswer = "L1 adds |w| penalty producing sparse weights; L2 adds w^2 penalty shrinking weights smoothly",
            explanationEn = "L1 regularization penalizes the sum of absolute weights (|w|), driving less useful feature weights to zero (feature selection). L2 penalizes squared weights (w^2), distributing weight decay smoothly.",
            explanationHi = "L1 (Lasso) भार के निरपेक्ष मान |w| पर दंड लगाता है जिससे अनावश्यक फीचर्स शून्य हो जाते हैं, जबकि L2 (Ridge) w^2 पर दंड लगाकर सभी भारों को सुचारु रूप से कम करता है।",
            aiGenerated = true,
            repeatedTag = false,
            published = true
        ),
        QuestionItem(
            id = "q_quant_pct_1",
            subject = "Quantitative Aptitude",
            academicLevel = "SSC",
            chapter = "Percentages, Ratio & Profit/Loss",
            topic = "Successive Percentage",
            questionType = "Numerical",
            difficulty = "Medium",
            marks = 2,
            year = "Practice",
            source = "Aptitude Practice Set",
            statement = "If the price of a book is first increased by 20% and then decreased by 20%, what is the net percentage change?",
            options = listOf("4% decrease", "No change", "4% increase", "2% decrease"),
            correctAnswer = "4% decrease",
            explanationEn = "Using net change formula: a + b + (ab/100) = +20 - 20 + (20 * -20)/100 = -4%, meaning a 4% net decrease.",
            explanationHi = "शुद्ध प्रतिशत परिवर्तन सूत्र: a + b + (ab/100) = +20 - 20 - 400/100 = -4%, अर्थात 4% की कमी।",
            aiGenerated = false,
            repeatedTag = true,
            published = true
        ),
        QuestionItem(
            id = "q_math_calc_2",
            subject = "Mathematics",
            academicLevel = "Class 12",
            chapter = "Differential & Integral Calculus",
            topic = "Derivatives",
            questionType = "Short Answer",
            difficulty = "Medium",
            marks = 3,
            year = "Practice",
            source = "NCERT Class 12 Maths",
            statement = "Find dy/dx if y = sin(x^2 + 5).",
            options = listOf("2x * cos(x^2 + 5)", "cos(x^2 + 5)", "2x * sin(x^2 + 5)", "-2x * cos(x^2 + 5)"),
            correctAnswer = "2x * cos(x^2 + 5)",
            explanationEn = "By chain rule: d/dx[sin(u)] = cos(u) * du/dx. Here u = x^2 + 5, so du/dx = 2x, giving dy/dx = 2x * cos(x^2 + 5).",
            explanationHi = "श्रृंखला नियम (Chain Rule) से: d/dx[sin(x^2 + 5)] = cos(x^2 + 5) * d/dx(x^2 + 5) = 2x * cos(x^2 + 5)।",
            aiGenerated = false,
            repeatedTag = false,
            published = true
        ),
        QuestionItem(
            id = "q_econ_gdp_1",
            subject = "Economics",
            academicLevel = "Class 12",
            chapter = "National Income & GDP",
            topic = "Monetary Policy",
            questionType = "MCQ",
            difficulty = "Easy",
            marks = 1,
            year = "Practice",
            source = "Macroeconomics Concept Bank",
            statement = "Which institution acts as the Banker's Bank and controls Repo Rate in India?",
            options = listOf("Reserve Bank of India (RBI)", "SEBI", "NABARD", "NITI Aayog"),
            correctAnswer = "Reserve Bank of India (RBI)",
            explanationEn = "The Reserve Bank of India (RBI) is India's central bank that regulates liquidity using tools like Repo Rate, Reverse Repo Rate, and CRR.",
            explanationHi = "भारतीय रिज़र्व बैंक (RBI) भारत का केंद्रीय बैंक है जो रेपो रेट और सीआरआर के माध्यम से मुद्रा आपूर्ति को नियंत्रित करता है।",
            aiGenerated = false,
            repeatedTag = true,
            published = true
        ),
        QuestionItem(
            id = "q_gk_polity_1",
            subject = "GK & GS",
            academicLevel = "UPSC",
            chapter = "Indian Polity & Static GK",
            topic = "Constitutional Articles",
            questionType = "MCQ",
            difficulty = "Medium",
            marks = 2,
            year = "Practice",
            source = "Polity Foundation",
            statement = "Which Article of the Constitution of India guarantees the Right to Constitutional Remedies (described as the heart and soul of the Constitution)?",
            options = listOf("Article 32", "Article 19", "Article 21", "Article 44"),
            correctAnswer = "Article 32",
            explanationEn = "Article 32 empowers citizens to move the Supreme Court directly for the enforcement of Fundamental Rights via writs.",
            explanationHi = "अनुच्छेद 32 (Article 32) नागरिकों को मौलिक अधिकारों के हनन पर सीधे सर्वोच्च न्यायालय जाने का संवैधानिक उपचार का अधिकार देता है।",
            aiGenerated = false,
            repeatedTag = true,
            published = true
        )
    )

    val starterVideos: List<VideoLesson> = listOf(
        VideoLesson(
            id = "vid_phy_waves",
            title = "Gauss's Law & Electric Field Lines Visualized",
            subject = "Physics",
            academicLevel = "Class 12",
            chapter = "Electrostatics & Gauss Law",
            lessonMode = "3D Animated",
            durationMinutes = 14,
            language = "Bilingual",
            instructor = "Prof. Aarav Verma",
            summary = "Visualize electric flux vectors piercing spherical and cubic Gaussian surfaces in real-time 3D motion.",
            keyConcepts = listOf("Electric Flux (E·A cosθ)", "Gaussian Surface Symmetry", "Charge Density"),
            captionsEn = listOf(
                "00:00 — Electric field lines radiate outward from a positive charge +q.",
                "00:25 — Flux measures how many field lines pierce an area perpendicularly.",
                "00:50 — Regardless of surface shape, total enclosed flux stays q / epsilon_0."
            ),
            captionsHi = listOf(
                "00:00 — धनात्मक आवेश +q से विद्युत क्षेत्र रेखाएँ बाहर की ओर निकलती हैं।",
                "00:25 — फ्लक्स किसी क्षेत्रफल से लंबवत गुजरने वाली क्षेत्र रेखाओं की संख्या को दर्शाता है।",
                "00:50 — बंद पृष्ठ के आकार से स्वतंत्र, कुल फ्लक्स सदैव q / epsilon_0 रहता है।"
            ),
            published = true
        ),
        VideoLesson(
            id = "vid_math_parabola",
            title = "Quadratic Roots & Parabola Shifting",
            subject = "Mathematics",
            academicLevel = "Class 10",
            chapter = "Quadratic Equations",
            lessonMode = "2D Animated",
            durationMinutes = 12,
            language = "Bilingual",
            instructor = "Meera Nair",
            summary = "Watch how changing discriminant D = b² - 4ac shifts a parabola above, onto, or across the x-axis.",
            keyConcepts = listOf("Vertex (-b/2a)", "Discriminant D > 0, D = 0, D < 0", "Factor Roots"),
            captionsEn = listOf(
                "00:00 — Every quadratic polynomial forms a symmetric parabola.",
                "00:30 — When D > 0, the curve cuts the x-axis at two distinct real roots.",
                "01:00 — When D = 0, the vertex touches the x-axis at one repeated root."
            ),
            captionsHi = listOf(
                "00:00 — प्रत्येक द्विघात बहुपद एक सममित परवलय (Parabola) बनाता है।",
                "00:30 — जब D > 0 होता है, वक्र x-अक्ष को दो भिन्न वास्तविक मूलों पर काटता है।",
                "01:00 — जब D = 0 होता है, शीर्ष x-अक्ष को एक बिंदु पर स्पर्श करता है।"
            ),
            published = true
        ),
        VideoLesson(
            id = "vid_dsa_bfs_dfs",
            title = "Graph Traversal: BFS vs DFS Whiteboard",
            subject = "Data Structures",
            academicLevel = "BTech",
            chapter = "Graph Algorithms",
            lessonMode = "Whiteboard",
            durationMinutes = 18,
            language = "English",
            instructor = "Dr. Vikram Rao",
            summary = "Step-by-step whiteboard trace of Queue-based Breadth-First Search and Stack-based Depth-First Search.",
            keyConcepts = listOf("Adjacency List", "Queue FIFO Order", "Shortest Unweighted Path"),
            captionsEn = listOf(
                "00:00 — Breadth-First Search explores nodes level by level using a Queue.",
                "00:35 — Depth-First Search dives along a branch using recursion or a Stack.",
                "01:10 — Both run in O(V + E) time for adjacency list representations."
            ),
            captionsHi = listOf(
                "00:00 — BFS (ब्रेड्थ-फर्स्ट सर्च) Queue की सहायता से स्तर-दर-स्तर नोड्स की खोज करता है।",
                "00:35 — DFS (डेप्थ-फर्स्ट सर्च) Stack या रिकर्शन का उपयोग करके गहराई तक जाता है।",
                "01:10 — दोनों की समय जटिलता O(V + E) होती है।"
            ),
            published = true
        ),
        VideoLesson(
            id = "vid_ai_neural",
            title = "How Gradient Descent Trains Neural Networks",
            subject = "Data Science & AI",
            academicLevel = "BSc AI & ML",
            chapter = "Deep Learning & Transformers",
            lessonMode = "AI Voice",
            durationMinutes = 16,
            language = "Bilingual",
            instructor = "EduGalaxy AI Studio",
            summary = "Interactive animated loss-landscape descent explaining learning rate, partial derivatives, and backpropagation.",
            keyConcepts = listOf("Loss Surface", "Learning Rate Alpha", "Chain Rule Backprop"),
            captionsEn = listOf(
                "00:00 — Imagine standing on a hilly loss surface trying to reach the lowest valley.",
                "00:30 — The gradient points steepest uphill; we step in the opposite direction.",
                "01:00 — Choosing the right learning rate avoids overshooting or slow convergence."
            ),
            captionsHi = listOf(
                "00:00 — कल्पना करें कि आप लॉस सतह की पहाड़ी पर सबसे निचली घाटी खोज रहे हैं।",
                "00:30 — ग्रेडिएंट सबसे तेज चढ़ाई की दिशा बताता है; हम विपरीत दिशा में कदम बढ़ाते हैं।",
                "01:00 — सही लर्निंग रेट चुनने से मॉडल तेज़ी और सटीकता से प्रशिक्षित होता है।"
            ),
            published = true
        )
    )

    val starterPdfs: List<PdfMaterial> = listOf(
        PdfMaterial(
            id = "pdf_ncert_math_10",
            title = "Class 10 Mathematics: Quadratic & Trigonometry Formula Sheet",
            category = "Formula Sheets",
            subject = "Mathematics",
            academicLevel = "Class 10",
            chapter = "Quadratic Equations & Trigonometry",
            source = "EduGalaxy Open Study Notes",
            pagesCount = 12,
            summary = "Complete quick-revision formula compendium covering discriminant rules, trigonometric identities, and coordinate geometry.",
            contentSections = listOf(
                "1. Quadratic Formula: x = (-b ± sqrt(b² - 4ac)) / 2a. Sum of roots α+β = -b/a, Product αβ = c/a.",
                "2. Discriminant Rules: D > 0 (Real & Distinct), D = 0 (Real & Equal), D < 0 (No Real Roots).",
                "3. Trigonometric Identities: sin²θ + cos²θ = 1; 1 + tan²θ = sec²θ; 1 + cot²θ = cosec²θ.",
                "4. Standard Values: sin 30° = 1/2, cos 60° = 1/2, tan 45° = 1, sin 90° = 1."
            ),
            published = true
        ),
        PdfMaterial(
            id = "pdf_phy_12_electro",
            title = "Class 12 Physics: Electrostatics Revision Notes",
            category = "Revision Notes",
            subject = "Physics",
            academicLevel = "Class 12",
            chapter = "Electrostatics & Gauss Law",
            source = "NCERT Concept Companion",
            pagesCount = 18,
            summary = "Concise derivations for Coulomb's law, electric dipole field, Gauss's theorem applications, and capacitor energy.",
            contentSections = listOf(
                "1. Coulomb's Law: F = (1 / 4πε₀) * (q₁q₂ / r²). Permittivity ε₀ = 8.854 × 10⁻¹² C²/N·m².",
                "2. Electric Dipole Moment: p = q × 2a (directed from -q to +q). Axial field = 2kp/r³, Equatorial = kp/r³.",
                "3. Gauss's Law: ∮ E·dA = q_enclosed / ε₀. Infinite line charge E = λ / (2πε₀r).",
                "4. Capacitor Energy: U = ½CV² = Q²/(2C) = ½QV."
            ),
            published = true
        ),
        PdfMaterial(
            id = "pdf_btech_dsa",
            title = "BTech CSE: Data Structures & Algorithms Cheat Sheet",
            category = "College Notes",
            subject = "Data Structures",
            academicLevel = "BTech",
            chapter = "Trees, Graphs & Sorting",
            source = "Open CS Curriculum Notes",
            pagesCount = 24,
            summary = "Big-O time/space complexity table, BFS/DFS templates, Dijkstra's algorithm, and Dynamic Programming patterns.",
            contentSections = listOf(
                "1. Sorting Complexities: Merge Sort O(n log n) guaranteed; Quick Sort O(n log n) avg / O(n²) worst.",
                "2. Tree Traversals: Inorder (Left-Root-Right) gives sorted output in a BST.",
                "3. Shortest Paths: BFS for unweighted graphs O(V+E); Dijkstra for non-negative weights O((V+E) log V).",
                "4. Dynamic Programming: Identify overlapping subproblems + optimal substructure before memoizing."
            ),
            published = true
        ),
        PdfMaterial(
            id = "pdf_upsc_polity",
            title = "UPSC & SSC: Indian Constitution Articles Summary",
            category = "Exam Preparation PDFs",
            subject = "GK & GS",
            academicLevel = "UPSC",
            chapter = "Indian Polity & Static GK",
            source = "Public Domain Constitutional Guide",
            pagesCount = 30,
            summary = "Structured summary of Fundamental Rights, Directive Principles, Union Executive, and key Constitutional Amendments.",
            contentSections = listOf(
                "1. Part III (Articles 12–35): Fundamental Rights — Equality (14–18), Freedom (19–22), Remedies (32).",
                "2. Part IV (Articles 36–51): Directive Principles of State Policy (DPSP), non-justiciable guiding principles.",
                "3. Part IVA (Article 51A): 11 Fundamental Duties added via 42nd (1976) and 86th (2002) Amendments.",
                "4. Schedules: 7th Schedule divides Union, State, and Concurrent legislative lists."
            ),
            published = true
        )
    )

    val starterMockTests: List<MockTest> = listOf(
        MockTest(
            id = "test_daily_stem",
            title = "Daily STEM Foundation Sprint",
            category = "Daily Mock Test",
            subject = "Mathematics",
            academicLevel = "Class 10",
            durationMinutes = 10,
            negativeMarking = 0.25,
            questionCount = 4,
            totalMarks = 16,
            questionIds = listOf("q_math_quad_1", "q_phy_gauss_1", "q_chem_bond_1", "q_math_calc_2"),
            published = true
        ),
        MockTest(
            id = "test_jee_neet_mix",
            title = "JEE & NEET Science Mastery Test",
            category = "Subject-wise Test",
            subject = "Physics",
            academicLevel = "IIT JEE",
            durationMinutes = 15,
            negativeMarking = 1.0,
            questionCount = 4,
            totalMarks = 16,
            questionIds = listOf("q_phy_gauss_1", "q_chem_bond_1", "q_bio_dna_1", "q_math_calc_2"),
            published = true
        ),
        MockTest(
            id = "test_btech_cs_core",
            title = "BTech CS & AI Core Mock Exam",
            category = "Full-Length Test",
            subject = "Data Structures",
            academicLevel = "BTech",
            durationMinutes = 15,
            negativeMarking = 0.33,
            questionCount = 3,
            totalMarks = 12,
            questionIds = listOf("q_dsa_tree_1", "q_ai_grad_1", "q_quant_pct_1"),
            published = true
        ),
        MockTest(
            id = "test_comp_aptitude",
            title = "SSC / UPSC Aptitude & Polity Mock",
            category = "Weekly Mock Test",
            subject = "GK & GS",
            academicLevel = "UPSC",
            durationMinutes = 12,
            negativeMarking = 0.25,
            questionCount = 3,
            totalMarks = 12,
            questionIds = listOf("q_quant_pct_1", "q_gk_polity_1", "q_econ_gdp_1"),
            published = true
        )
    )

    val starterUniversities: List<UniversityInfo> = listOf(
        UniversityInfo(
            id = "univ_iit_delhi",
            name = "Indian Institute of Technology Delhi (IIT Delhi)",
            category = "IIT",
            location = "New Delhi",
            websiteUrl = "https://home.iitd.ac.in",
            resultsUrl = "https://home.iitd.ac.in/academics.php",
            admitCardUrl = "https://jeeadv.ac.in",
            courses = listOf("BTech", "MTech", "MSc", "MBA", "PhD"),
            departments = listOf("Computer Science & Engg", "Electrical Engg", "Mechanical Engg", "Mathematics", "Physics"),
            examInfo = "BTech admissions via JEE Advanced; MTech via GATE; MSc via IIT JAM.",
            notices = listOf("Academic calendar & semester syllabus published on official institute portal."),
            published = true
        ),
        UniversityInfo(
            id = "univ_du",
            name = "University of Delhi (DU)",
            category = "Central University",
            location = "New Delhi",
            websiteUrl = "https://www.du.ac.in",
            resultsUrl = "https://exam.du.ac.in",
            admitCardUrl = "https://admission.uod.ac.in",
            courses = listOf("BA", "BSc", "BCom", "MA", "MSc", "MCom", "MCA"),
            departments = listOf("Computer Science", "Mathematics", "Commerce", "Economics", "Physics", "English"),
            examInfo = "UG & PG admissions through CUET-UG and CUET-PG.",
            notices = listOf("Semester-wise NEP-UGCF syllabus PDFs available under Academics."),
            published = true
        ),
        UniversityInfo(
            id = "univ_nit_trichy",
            name = "National Institute of Technology Tiruchirappalli (NIT Trichy)",
            category = "NIT",
            location = "Tiruchirappalli, Tamil Nadu",
            websiteUrl = "https://www.nitt.edu",
            resultsUrl = "https://www.nitt.edu/home/academics/",
            admitCardUrl = "https://jeemain.nta.nic.in",
            courses = listOf("BTech", "MTech", "MCA", "MBA", "MSc"),
            departments = listOf("Computer Applications", "CSE", "ECE", "Civil Engg", "Data Analytics"),
            examInfo = "BTech via JEE Main & JoSAA; MCA via NIMCET; MTech via GATE/CCMT.",
            notices = listOf("Check official NIT Trichy Academics tab for semester timetables."),
            published = true
        ),
        UniversityInfo(
            id = "univ_iiit_hyd",
            name = "IIIT Hyderabad",
            category = "IIIT",
            location = "Hyderabad, Telangana",
            websiteUrl = "https://www.iiit.ac.in",
            resultsUrl = "https://www.iiit.ac.in/academics/",
            admitCardUrl = "https://ugadmissions.iiit.ac.in",
            courses = listOf("BTech", "MTech", "MSc AI & ML"),
            departments = listOf("Computer Science", "AI & Machine Learning", "Robotics", "Computational Linguistics"),
            examInfo = "Admissions via JEE Main, UGEE, and PGEE portals.",
            notices = listOf("Course curriculum and research labs listed on official website."),
            published = true
        ),
        UniversityInfo(
            id = "univ_ignou",
            name = "Indira Gandhi National Open University (IGNOU)",
            category = "Open University",
            location = "New Delhi (Pan-India)",
            websiteUrl = "https://www.ignou.ac.in",
            resultsUrl = "https://termendresult.ignou.ac.in",
            admitCardUrl = "https://hallticket.ignou.ac.in",
            courses = listOf("BA", "BSc", "BCom", "BCA", "BBA", "MA", "MSc", "MCA", "MBA"),
            departments = listOf("School of Computer & Information Sciences", "School of Sciences", "School of Management"),
            examInfo = "Term-End Examinations (TEE) conducted twice yearly in June and December.",
            notices = listOf("Free self-learning study material available via eGyanKosh portal."),
            published = true
        ),
        UniversityInfo(
            id = "univ_bhu",
            name = "Banaras Hindu University (BHU)",
            category = "Central University",
            location = "Varanasi, Uttar Pradesh",
            websiteUrl = "https://www.bhu.ac.in",
            resultsUrl = "https://bhuonline.in",
            admitCardUrl = "https://cuet.nta.nic.in",
            courses = listOf("BA", "BSc", "BCom", "BTech", "MA", "MSc", "MCA", "MBA"),
            departments = listOf("Institute of Science", "Faculty of Arts", "Faculty of Commerce"),
            examInfo = "Admissions via CUET-UG, CUET-PG, and JEE Advanced (IIT BHU).",
            notices = listOf("Verify official exam schedules directly on bhu.ac.in."),
            published = true
        )
    )

    val starterAnnouncements: List<Announcement> = listOf(
        Announcement(
            id = "ann_welcome",
            title = "EduGalaxy AI Tutor Live (Hindi & English)",
            message = "Ask step-by-step Mathematics, Physics, Coding, or Board Exam questions adapted to your selected class.",
            category = "AI Feature",
            published = true
        ),
        Announcement(
            id = "ann_daily_test",
            title = "Daily Mock Test Challenge Open",
            message = "Attempt today's timed mock test to earn +100 XP and boost your learning streak!",
            category = "Mock Test",
            published = true
        ),
        Announcement(
            id = "ann_offline_pdf",
            title = "Offline Study Notes Ready",
            message = "Save formula sheets and NCERT revision guides directly for offline reading anytime.",
            category = "Library",
            published = true
        )
    )
}
