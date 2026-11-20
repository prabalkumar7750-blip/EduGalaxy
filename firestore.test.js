const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_student_1";
const BOB_UID = "bob_student_2";
const ADMIN_UID = "admin_prabal_1";
const ADMIN_EMAIL = "prabalkumar7750@gmail.com";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function pastDate(msOffset = 5000) {
  return new Date(Date.now() - msOffset);
}

function validUserProfilePayload(uid, email = "alice@example.com") {
  const now = pastDate();
  return {
    userId: uid,
    displayName: "Alice Sharma",
    email,
    role: "student",
    selectedLevel: "Class 10",
    selectedStream: "General",
    selectedCourse: "CBSE Board",
    selectedUniversity: "CBSE",
    preferredLanguage: "Bilingual",
    xp: 120,
    coins: 45,
    streakDays: 3,
    dailyStudyMinutes: 25,
    dailyGoalMinutes: 60,
    completedChaptersCount: 2,
    reducedMotion: false,
    badges: ["first_login", "streak_3"],
    createdAt: now,
    updatedAt: now,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users: cannot read or write any collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("questions").where("published", "==", true).get());
  await assertFails(unauthDb.collection("leaderboard").where("publicOptIn", "==", true).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set(validUserProfilePayload(ALICE_UID))
  );
});

test("User profile: owner create, partial update, cross-user PII block, and role escalation block", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID, {
    email: "alice@example.com",
    email_verified: true,
  }).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID, {
    email: "bob@example.com",
    email_verified: true,
  }).firestore();

  // Cannot self-assign admin role on create
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      ...validUserProfilePayload(ALICE_UID),
      role: "admin",
    })
  );

  // Valid create succeeds (no @DocumentId id field)
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validUserProfilePayload(ALICE_UID))
  );

  // Ghost field rejected
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      ...validUserProfilePayload(ALICE_UID),
      isVerified: true,
    })
  );

  // Partial update of allowed fields succeeds
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      selectedLevel: "IIT JEE",
      xp: 250,
      updatedAt: pastDate(1000),
    })
  );

  // Cannot tamper with role or createdAt
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      role: "admin",
      updatedAt: pastDate(500),
    })
  );
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: pastDate(100),
      updatedAt: pastDate(100),
    })
  );

  // Bob cannot read Alice's PII profile or list /users
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").get());
});

test("User subcollections: bookmarks, downloads, test_attempts, progress, ai_sessions isolated by owner", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = pastDate();

  // Bookmark
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("bookmarks").doc("bm_1").set({
      userId: ALICE_UID,
      itemType: "question",
      itemId: "q_math_1",
      title: "Quadratic Formula",
      subject: "Mathematics",
      subtitle: "Class 10 • Chapter 4",
      createdAt: now,
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("bookmarks").get()
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("bookmarks").get()
  );

  // Download
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("downloads").doc("dl_1").set({
      userId: ALICE_UID,
      pdfId: "pdf_1",
      title: "NCERT Class 10 Science Ch 1",
      category: "NCERT Books",
      subject: "Science",
      localFileName: "ncert_sci_ch1.txt",
      sizeKb: 42,
      createdAt: now,
    })
  );

  // Test Attempt
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("test_attempts").doc("att_1").set({
      userId: ALICE_UID,
      testId: "test_1",
      testTitle: "Daily Physics Sprint",
      subject: "Physics",
      totalQuestions: 5,
      attempted: 5,
      correct: 4,
      incorrect: 1,
      unanswered: 0,
      score: 15.0,
      maxScore: 20.0,
      accuracyPercent: 80.0,
      timeTakenSeconds: 180,
      weakTopics: ["Optics"],
      createdAt: now,
    })
  );

  // Chapter Progress create + update
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("progress").doc("prog_1").set({
      userId: ALICE_UID,
      subjectId: "math",
      subjectName: "Mathematics",
      chapterId: "ch_1",
      chapterTitle: "Real Numbers",
      progressPercent: 50,
      videoCompleted: true,
      notesRead: false,
      questionsPracticed: 4,
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("progress").doc("prog_1").update({
      progressPercent: 100,
      notesRead: true,
      questionsPracticed: 10,
      updatedAt: pastDate(500),
    })
  );

  // AI Session
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("ai_sessions").doc("ai_1").set({
      userId: ALICE_UID,
      subject: "Mathematics",
      academicLevel: "Class 10",
      language: "English",
      prompt: "Explain Euclid division lemma",
      response: "Given positive integers a and b...",
      createdAt: now,
    })
  );
});

test("Leaderboard: public opt-in query succeeds without PII; cross-user write blocked", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = pastDate();

  await assertSucceeds(
    aliceDb.collection("leaderboard").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice Sharma",
      selectedLevel: "Class 10",
      xp: 350,
      streakDays: 5,
      publicOptIn: true,
      createdAt: now,
      updatedAt: now,
    })
  );

  // Filtered query matching rule succeeds
  await assertSucceeds(
    bobDb.collection("leaderboard").where("publicOptIn", "==", true).get()
  );

  // Unfiltered query rejected
  await assertFails(bobDb.collection("leaderboard").get());

  // Cross-user write rejected
  await assertFails(
    bobDb.collection("leaderboard").doc(ALICE_UID).update({
      xp: 99999,
      updatedAt: pastDate(500),
    })
  );
});

test("Email spoofing & Admin catalog: unverified email spoof rejected; verified admin manages published catalog & audit logs", async () => {
  // Spoofed unverified admin email MUST fail
  const spoofedAdminDb = testEnv.authenticatedContext("spoof_uid", {
    email: ADMIN_EMAIL,
    email_verified: false,
  }).firestore();

  const now = pastDate();
  const questionPayload = {
    subject: "Physics",
    academicLevel: "Class 12",
    chapter: "Electrostatics",
    topic: "Gauss Law",
    questionType: "MCQ",
    difficulty: "Medium",
    marks: 4,
    year: "2024",
    source: "Practice Bank",
    statement: "What is the electric flux through a closed surface enclosing charge q?",
    options: ["q/epsilon_0", "q*epsilon_0", "0", "2q/epsilon_0"],
    correctAnswer: "q/epsilon_0",
    explanationEn: "By Gauss Law, total flux equals enclosed charge divided by permittivity.",
    explanationHi: "गॉस के नियम के अनुसार कुल फ्लक्स q / epsilon_0 होता है।",
    aiGenerated: false,
    repeatedTag: true,
    published: true,
    createdBy: "spoof_uid",
    createdAt: now,
    updatedAt: now,
  };

  await assertFails(
    spoofedAdminDb.collection("questions").doc("q_1").set(questionPayload)
  );

  // Verified admin succeeds
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: ADMIN_EMAIL,
    email_verified: true,
  }).firestore();

  await assertSucceeds(
    adminDb.collection("questions").doc("q_1").set({
      ...questionPayload,
      createdBy: ADMIN_UID,
    })
  );

  // Unpublished draft question cannot be read by student
  await assertSucceeds(
    adminDb.collection("questions").doc("q_draft").set({
      ...questionPayload,
      published: false,
      createdBy: ADMIN_UID,
    })
  );

  const studentDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  // Filtered published query succeeds
  await assertSucceeds(
    studentDb.collection("questions").where("published", "==", true).get()
  );

  // Student cannot read draft or write questions
  await assertFails(studentDb.collection("questions").doc("q_draft").get());
  await assertFails(studentDb.collection("questions").get());
  await assertFails(
    studentDb.collection("questions").doc("q_student").set({
      ...questionPayload,
      createdBy: ALICE_UID,
    })
  );

  // Admin audit log create succeeds, update/delete rejected
  await assertSucceeds(
    adminDb.collection("admin_audit_logs").doc("log_1").set({
      adminId: ADMIN_UID,
      adminEmail: ADMIN_EMAIL,
      action: "CREATE_QUESTION",
      targetCollection: "questions",
      targetId: "q_1",
      summary: "Added Physics Electrostatics MCQ",
      createdAt: now,
    })
  );
  await assertFails(
    adminDb.collection("admin_audit_logs").doc("log_1").update({ summary: "Tampered" })
  );
  await assertFails(adminDb.collection("admin_audit_logs").doc("log_1").delete());
});
