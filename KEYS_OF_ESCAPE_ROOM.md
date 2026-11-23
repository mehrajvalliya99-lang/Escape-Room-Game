# KEYS OF ESCAPE ROOM — COMPLETE WALKTHROUGH & MASTER ANSWER GUIDE

**Game:** THE LAST EXPERIMENT — A Detective Mystery Adventure  
**Genre:** 2D Detective Escape Room / Scientific Mystery Adventure  
**Platform:** Java Swing / AWT / Graphics2D MVC Architecture  

---

## 1. STORY & MISSION BRIEFING

You are an elite detective investigating the sudden disappearance of **Dr. Alistair Vance**, a brilliant researcher who vanished from an abandoned underground research facility. Dr. Vance was conducting classified research into an ancient energy relic known as **Project Resonance**.

Upon entering the facility, an automated quarantine protocol engages, locking down all blast doors and security bulkheads. To uncover the truth of Dr. Vance's fate and escape alive, you must investigate four interconnected facility sectors, solve scientific and cryptographic puzzles, gather critical access items, disengage containment relays, and breach the Ancient Vault before the facility seals permanently.

### Facility Sectors:
1. **Level 1 — The Detective's Study:** Dr. Vance's abandoned office filled with scattered files, locked drawers, and evidence.
2. **Level 2 — The Forgotten Library:** The facility's high-security archive housing classified manuscripts and schematics.
3. **Level 3 — The Abandoned Laboratory:** High-voltage circuit breaker arrays, chemical reaction consoles, and security terminals.
4. **Level 4 — The Ancient Vault:** The subterranean containment chamber housing the ancient relic and the facility escape portal.

---

## 2. MASTER ANSWER SHEET

Every puzzle implemented in the source code is cataloged below with its exact accepted answer:

| Level | Puzzle ID | Display Name | Exact Accepted Answer | Required Item | Outcome / Reward |
|---|---|---|---|---|---|
| **Level 1** | `torn_newspaper` | Torn Newspaper Archive | **`1947`** | None | Awards **`Library Access Key`** (`+100 pts`). Unlocks Level 1 Exit Door. |
| **Level 1** | `desk_drawer` | Desk Drawer Lock | **`425`** | None | Awards **`Drawer Key`** & **`Magnifying Glass`** (`+100 pts`). |
| **Level 1** | `suspect_cipher` | Suspect's Cipher Note | **`BOOKSHELF`** | None | Decodes cipher; awards **`Cipher Manuscript`** (`+100 pts`). |
| **Level 1** | `study_clock` | Mantel Clock Time | **`425`** | None | Solves clock combination (`+100 pts`). |
| **Level 1** | `study_portrait` | Scholar Portrait Plaque | **`ARCHIMEDES`** | None | Identifies the ancient mathematician (`+100 pts`). |
| **Level 1** | `study_globe` | Terrestrial Coordinates | **`GREENWICH`** | None | Decodes prime meridian location (`+100 pts`). |
| **Level 2** | `book_arrangement` | Book Arrangement | **`12345`** *(or `1-2-3-4-5`)* | None | Aligns archive volumes in chronological order (`+100 pts`). |
| **Level 2** | `caesar_cipher` | Encrypted Manuscript | **`ARTIFACT`** | None | Awards **`Ancient Manuscript`** (`+100 pts`). |
| **Level 2** | `missing_manuscript` | High Shelf Manuscript | **`EXPERIMENT`** | None | Identifies Dr. Vance's classified project (`+100 pts`). |
| **Level 2** | `hidden_shelf` | Archive Security Cabinet | **`4921`** | None | Awards **`Lab Access Card`** & **`Circuit Component`** (`+100 pts`). |
| **Level 2** | `grandfather_clock` | Library Grandfather Clock | **`11:45`** | None | Solves stopped time setting (`+100 pts`). |
| **Level 2** | `hidden_bookshelf` | Secret Shelf Pattern | **`◇`** *(or `Hollow` / `Hollow Diamond`)* | None | Disengages hidden bookshelf latch (`+100 pts`). |
| **Level 3** | `circuit_puzzle` | Electrical Circuit Breakers | **`ALPHA BETA GAMMA DELTA`** | `Circuit Component` | Restores main facility electrical grid (`+100 pts`). |
| **Level 3** | `chemical_sequence` | Chemical Reaction Sequence | **`SOLVENT CATALYST REAGENT ISOTOPE`** | None | Stabilizes chemical reaction console (`+100 pts`). |
| **Level 3** | `experiment_logic` | Research Workstation | **`EXP 103`** *(or `103`)* | None | Awards **`Experiment Report 103`** (`+100 pts`). |
| **Level 3** | `security_terminal` | Security Override Terminal | **`8462`** | None | Overrides lockdown; dispenses **`Ancient Vault Key`** (`+100 pts`). |
| **Level 3** | `reactor_cipher` | Resonance Dial | **`REACTOR`** | None | Harmonizes resonance frequency (`+100 pts`). |
| **Level 3** | `laser_grid` | Optical Sensor Grid | **`HEXAGON`** | None | Completes safety perimeter grid (`+100 pts`). |
| **Level 4** | `vault_combination` | Vault Combination Lock | **`5284`** | None | Disengages 4 concentric tumbler rings (`+100 pts`). |
| **Level 4** | `containment_relays` | Containment Relays | **`POWER COOLING VACUUM SHIELD`** | None | Releases magnetic safety counterweights (`+100 pts`). |
| **Level 4** | `artifact_arrangement` | Artifact Altar Alignment | **`RELIC CORE MATRIX ARTIFACT`** | `Vault Key` | Locks ancient artifact into pedestal; awards `Ancient Artifact` (`+100 pts`). |
| **Level 4** | `final_message` | Final Decryption Puzzle | **`EXPERIMENT COMPLETE`** | None | Recovers **`Final Recording`** (`+100 pts`). |
| **Level 4** | `superconductor_grid` | Superconductor Grid | **`LABYRINTH`** | None | Illuminates superconducting ribbon floor maze (`+100 pts`). |
| **Level 4** | `hydraulic_valves` | Hydraulic Release Valves | **`SYNCHRONY`** | None | Synchronizes pressure release valves (`+100 pts`). |

---

## 3. COMPLETE STEP-BY-STEP PLAYTHROUGH

Follow these numbered steps to finish the complete game with maximum score from start to victory:

### PHASE 1: GAME START & REGISTRATION
1. On the **Home Screen**, click **`START GAME`**.
2. In the **Player Details Screen**:
   - Type your detective name in the text box (e.g., `Detective`).
   - Select your preferred difficulty:
     - **EASY:** 20 Minutes (1,200 s) — *Recommended*
     - **MEDIUM:** 15 Minutes (900 s)
     - **HARD:** 12 Minutes (720 s)
   - Click the orange **`ENTER THE FACILITY`** button.
   - *Outcome:* The game timer starts and you are placed inside **Level 1 — The Detective's Study**.

---

### PHASE 2: LEVEL 1 — THE DETECTIVE'S STUDY
3. **Collect Clue scrap:** Click the **Potted Plant** (far left foreground).
   - *Outcome:* You find the **Torn Newspaper Fragment** (`+50 pts`). The circled date is **`1947`**.
4. **Collect Research Evidence:** Click the **Desk Lamp** (on the left side of the study desk).
   - *Outcome:* You collect Dr. Vance's **Research Note** and a **Magnifying Glass** (`+50 pts`).
5. **Unlock the Archive Chest (Critical Objective):** Click the **Archive Chest** (on the floor in front of the bookshelf).
   - A 4-digit keypad modal opens.
   - Type **`1947`** and click **`SUBMIT`** (or press Enter).
   - *Outcome:* Correct! (`+100 pts`). The chest pops open, awarding the **`Library Access Key`**.
   - Click **`CLOSE`** (or the **`X`** button) to return to the study.
6. **Optional Study Bonus Puzzles:**
   - Click the **Desk**: Enter **`425`** $\rightarrow$ Unlocks Drawer Key (`+100 pts`).
   - Click the **Bookshelf**: Enter **`BOOKSHELF`** $\rightarrow$ Decodes cipher note (`+100 pts`).
   - Click the **Mantel Clock**: Enter **`425`** $\rightarrow$ Solves clock combination (`+100 pts`).
   - Click the **Archimedes Portrait**: Enter **`ARCHIMEDES`** $\rightarrow$ Solves scholar puzzle (`+100 pts`).
   - Click the **Globe**: Enter **`GREENWICH`** $\rightarrow$ Solves navigation coordinates (`+100 pts`).
7. **Transition to Level 2:** Click the **Exit Door** (far right wall) or click **`Level 2 →`** in the top navigation bar.
   - *Outcome:* The Library Access Key turns with a mechanical clunk! Level 1 complete (`+150 pts level completion bonus`), and you advance to **Level 2 — The Forgotten Library**.

---

### PHASE 3: LEVEL 2 — THE FORGOTTEN LIBRARY
8. **Collect Schematics:** Click the **Library Ladder** (center-left tall ladder).
   - *Outcome:* You climb up and discover a folder titled `EXPERIMENT` with breaker circuit schematics, collecting the **Cipher Manuscript** (`+50 pts`).
9. **Examine Founding Portrait:** Click the **Historical Painting** (upper center wall).
   - *Outcome:* Inspecting the founding team plaque reveals the catalog index code: **`4921`**.
10. **Unlock the Archive Cabinet (Critical Objective):** Click the **Locked Cabinet** (far right floor).
    - A 4-digit code modal opens.
    - Type **`4921`** and click **`SUBMIT`**.
    - *Outcome:* Correct! (`+100 pts`). The steel cabinet swings open, awarding both the **`Lab Access Card`** and the **`Circuit Component`**!
    - Click **`CLOSE`** to return to the library.
11. **Optional Library Bonus Puzzles:**
    - Click the **Central Reading Table Manuscript**: Enter **`ARTIFACT`** $\rightarrow$ Decodes Caesar cipher, awards `Ancient Manuscript` (`+100 pts`).
    - Click the **Grandfather Clock**: Enter **`11:45`** $\rightarrow$ Solves stopped clock (`+100 pts`).
    - Click the **Main Bookshelf**: Enter **`12345`** $\rightarrow$ Chronologically sorts archive volumes (`+100 pts`).
    - Click the **Hidden Bookshelf Section**: Enter **`◇`** *(or `Hollow`)* $\rightarrow$ Realigns diamond spines (`+100 pts`).
12. **Transition to Level 3:** Click **`Level 3 →`** in the top navigation bar.
    - *Outcome:* The electronic maglock verifies your Lab Access Card! Level 2 complete (`+150 pts level completion bonus`), and you advance to **Level 3 — The Abandoned Laboratory**.

---

### PHASE 4: LEVEL 3 — THE ABANDONED LABORATORY
13. **Inspect Conduit Markers:** Click the **Power Conduits** (three glowing teal stone pillars on the left).
    - *Outcome:* The ionized conduits display the power distribution bus sequence: **`ALPHA - BETA - GAMMA - DELTA`**.
14. **Restore Facility Power (Critical Objective 1):** Click the **Circuit Breaker Board** (`Wall Mechanism`, upper center wall).
    - A combination modal opens.
    - Type **`ALPHA BETA GAMMA DELTA`** and click **`SUBMIT`**.
    - *Outcome:* Correct! (`+100 pts`). Circuit breakers engage and primary facility power hums back to life.
    - Click **`CLOSE`** to return to the laboratory.
15. **Inspect Experiment Workstation:** Click the **Research Workstation** (`Scroll Table`, lower left bench).
    - Type **`EXP 103`** *(or `103`)* and click **`SUBMIT`**.
    - *Outcome:* Correct! (`+100 pts`). Awards **`Experiment Report 103`**, revealing lockdown override code **`8462`**.
    - Click **`CLOSE`** to return to the laboratory.
16. **Override Security Lockdown (Critical Objective 2):** Click the **Security Terminal** (`Stone Statue`, right wall).
    - A 4-digit numeric keypad opens.
    - Type **`8462`** and click **`SUBMIT`**.
    - *Outcome:* Correct! (`+100 pts`). Facility lockdown overrides and the terminal dispenser ejects the **`Ancient Vault Key`**!
    - Click **`CLOSE`** to return to the laboratory.
17. **Optional Laboratory Bonus Puzzles:**
    - Click the **Reaction Console** (`Pedestal`): Enter **`SOLVENT CATALYST REAGENT ISOTOPE`** $\rightarrow$ Stabilizes chemical console (`+100 pts`).
    - Click the **Resonance Dial** (`Clock`): Enter **`REACTOR`** $\rightarrow$ Locks core resonance (`+100 pts`).
    - Click the **Laser Sensor Grid** (`Floor Symbol`): Enter **`HEXAGON`** $\rightarrow$ Calibrates optical sensor path (`+100 pts`).
18. **Transition to Level 4:** Click the **Vault Blast Door** (right wall) or click **`Level 4 →`** in the top bar.
    - *Outcome:* The hydraulic counterweights hiss open! Level 3 complete (`+150 pts level completion bonus`), and you breach **Level 4 — The Ancient Vault**.

---

### PHASE 5: LEVEL 4 — THE ANCIENT VAULT
19. **Disengage Vault Combination Lock (Escape Requirement 1):** Click the **Vault Combination Lock** (mounted on the center golden door).
    - A combination modal opens.
    - Type **`5284`** and click **`SUBMIT`**.
    - *Outcome:* Correct! (`+100 pts`). Four concentric tumbler dials disengage.
    - Click **`CLOSE`** to return to the vault.
20. **Stabilize Containment Relays (Escape Requirement 2):** Click the **Containment Relays** (`Symbol Panels`, left wall).
    - A sequence modal opens.
    - Type **`POWER COOLING VACUUM SHIELD`** and click **`SUBMIT`**.
    - *Outcome:* Correct! (`+100 pts`). Electromagnetic containment relays stabilize in safety order.
    - Click **`CLOSE`** to return to the vault.
21. **Align Ancient Relic on Altar (Escape Requirement 3):** Click the **Artifact Altar** (`Central Pedestal`, foreground in front of the door).
    - A multi-part puzzle modal opens.
    - Type **`RELIC CORE MATRIX ARTIFACT`** and click **`SUBMIT`**.
    - *Outcome:* Correct! (`+100 pts`). The Ancient Vault Key and energy relic lock securely into the altar socket, awarding the **`Ancient Artifact`**!
    - Click **`CLOSE`** to return to the vault.
22. **Optional Vault Bonus Puzzles:**
    - Click the **Research Terminal** (`Document Table`): Enter **`EXPERIMENT COMPLETE`** $\rightarrow$ Decodes final transmission and awards Dr. Vance's `Final Recording` (`+100 pts`).
    - Click the **Superconductor Grid** (`Floor Pattern`): Enter **`LABYRINTH`** $\rightarrow$ Completes floor ribbon circuit (`+100 pts`).
    - Click the **Hydraulic Valves** (`Wall Mechanisms`): Enter **`SYNCHRONY`** $\rightarrow$ Synchronizes pressure release valves (`+100 pts`).
23. **Breach the Final Exit Portal & Escape to Victory:** Click the grand **Vault Exit Portal** (`Golden Door`, center).
    - *Outcome:* The lockdown field completely disengages, the massive vault portals slide open to the surface escape tunnel, and Level 4 is completed!
    - The **Victory Screen (`WinPanel`)** appears celebrating your achievement with:
      - **"YOU ESCAPED!"** gold ribbon banner.
      - **"Congratulations, Detective! The Last Experiment mystery is solved."**
      - Full **Mission Dossier**: Final Score, Time Taken, Rooms Cleared (4/4), Puzzles Solved, and Hints Used.
      - Options to **`PLAY AGAIN`**, return to **`MAIN MENU`**, or **`EXIT`**.

---

## 4. OPTIMAL SPEEDRUN ROUTE (< 3 MINUTES)

If your goal is to finish the game as fast as possible:

1. **Start:** Click `START GAME` $\rightarrow$ Name `Detective` $\rightarrow$ `ENTER THE FACILITY`.
2. **Level 1 (Study):** Click **Archive Chest** $\rightarrow$ Type `1947` $\rightarrow$ Click `SUBMIT` $\rightarrow$ Click `CLOSE` $\rightarrow$ Click `Exit Door` $\rightarrow$ Enter Level 2.
3. **Level 2 (Library):** Click **Locked Cabinet** $\rightarrow$ Type `4921` $\rightarrow$ Click `SUBMIT` $\rightarrow$ Click `CLOSE` $\rightarrow$ Click `Level 3 →` $\rightarrow$ Enter Level 3.
4. **Level 3 (Laboratory):**
   - Click **Circuit Breaker Board** (`Wall Mechanism`) $\rightarrow$ Type `ALPHA BETA GAMMA DELTA` $\rightarrow$ `SUBMIT` $\rightarrow$ `CLOSE`.
   - Click **Security Terminal** (`Statue`) $\rightarrow$ Type `8462` $\rightarrow$ `SUBMIT` $\rightarrow$ `CLOSE`.
   - Click `Vault Blast Door` $\rightarrow$ Enter Level 4.
5. **Level 4 (Vault):**
   - Click **Vault Combination Lock** $\rightarrow$ Type `5284` $\rightarrow$ `SUBMIT` $\rightarrow$ `CLOSE`.
   - Click **Containment Relays** (`Symbol Panels`) $\rightarrow$ Type `POWER COOLING VACUUM SHIELD` $\rightarrow$ `SUBMIT` $\rightarrow$ `CLOSE`.
   - Click **Artifact Altar** (`Pedestal`) $\rightarrow$ Type `RELIC CORE MATRIX ARTIFACT` $\rightarrow$ `SUBMIT` $\rightarrow$ `CLOSE`.
   - Click **Vault Exit Portal** (`Golden Door`) $\rightarrow$ **VICTORY SCREEN**!

---

## 5. TROUBLESHOOTING & COMMON QUESTIONS

### Q1: Why is my puzzle answer rejected?
- Answers are case-insensitive and whitespace-trimmed.
- Do not add punctuation unless specified. For example, enter `1947` (not `1947.`), `ALPHA BETA GAMMA DELTA` (single spaces between words), or `12345`.
- If stuck, click the blue **`HINT`** button. HintManager will provide up to 3 progressive hints with a small score penalty (-25 pts).

### Q2: Why does an exit door say it's locked?
- **Level 1 Door:** Requires unlocking the Archive Chest (`1947`) to get the Library Access Key.
- **Level 2 Door (`Level 3 →`):** Requires unlocking Cabinet (`4921`) to get the Lab Access Card.
- **Level 3 Door (`Level 4 →`):** Requires restoring power at the Circuit Breakers (`ALPHA BETA GAMMA DELTA`) and overriding the Security Terminal (`8462`) to get the Ancient Vault Key.
- **Level 4 Portal (`Golden Door`):** Requires all three escape objectives: Combination Lock (`5284`), Containment Relays (`POWER COOLING VACUUM SHIELD`), and Artifact Altar (`RELIC CORE MATRIX ARTIFACT`).

### Q3: How do I use collected inventory items?
- Click the purple **`INVENTORY`** button in the lower right corner of any room.
- Click on any occupied inventory slot to inspect its 2D illustration, classified dossier notes, and item type.
- Click **`USE ITEM`** to trigger contextual interactions in the active room.
- Click **`CLOSE`** to return to your current room.

### Q4: What happens if the countdown timer expires?
- The quarantine protocol seals the facility, triggering the **`GameOverPanel`** ("FACILITY LOCKDOWN").
- You can click **`TRY AGAIN`** to immediately start a new run or **`MAIN MENU`** to return to the home screen.
