# KEYS OF ESCAPE ROOM — COMPLETE WALKTHROUGH & MASTER ANSWER GUIDE

**Game:** TRACE: Escape the Room – Mystery Puzzle Adventure  
**Platform:** Java Swing / AWT Procedural 2D  

---

## 1. GAME START & SETUP

1. **Home Screen (`HomePanel`):**
   - Click the gold-trimmed **`START GAME`** button.
2. **Player Details Screen (`PlayerDetailsPanel`):**
   - Click on the **"Detective Name"** text field and type your name (e.g., `Detective`).
   - Choose your preferred difficulty:
     - **EASY:** 20 Minutes (1,200 seconds) — *Recommended for a relaxed first playthrough*
     - **MEDIUM:** 15 Minutes (900 seconds)
     - **HARD:** 12 Minutes (720 seconds)
   - Click **`ENTER THE MANSION`**.
   - *Result:* State resets, the countdown timer starts, and you enter **Level 1 — The Study**.

---

## 2. LEVEL 1 — THE STUDY

### Room Overview & Interactive Objects
- **Potted Plant** (Far left foreground): Contains hidden item.
- **Desk Lamp** (Left side of desk): Conceals two hidden items.
- **Window** (Far left wall): Displays celestial lunar clue (`MOON → SUN → STAR → HOURGLASS`).
- **Wall Clock** (Center wall): Combination puzzle (`369`).
- **Painting** (Above desk): Symbol Sequence puzzle (`MOON SUN STAR HOURGLASS`).
- **Study Desk** (Center-left): Mosaic Pattern puzzle (`◇` / `HOLLOW DIAMOND`).
- **Bookshelf** (Center-right wall): Caesar Cipher puzzle (`ESCAPE`).
- **Globe** (Right side of desk): Logic Deduction puzzle (`GREENWICH`).
- **Iron Chest** (Floor in front of bookshelf): **Critical Objective** (`4815`).
- **Exit Door** (Right wall): Unlocks the route to Level 2.

### Step-by-Step Level 1 Completion
1. Click the **Potted Plant**:
   - Collects the **Torn Paper** (`+50 pts`). The scrap shows `4 - 8 - ...`.
2. Click the **Desk Lamp**:
   - Collects the **Candle** and the **Secret Note** (`+50 pts`).
3. Click the **Locked Chest**:
   - Opens the Number Code puzzle.
   - Type **`4815`** using keyboard or the on-screen keypad and click **`SUBMIT`**.
   - *Result:* Correct! You receive `+100 pts` and automatically collect both the **Golden Key** and the **Old Photograph** (`+50 pts`).
4. Click **`CLOSE`** (or the red **`X`**) to return to the study.
5. Click the **Exit Door** (on the right wall):
   - The game checks for the **Golden Key**. The lock clicks open, completing Level 1 (`+150 pts bonus`), and advances you to **Level 2 — The Secret Library**.

*(Optional study puzzles for extra score: Desk `◇`, Clock `369`, Painting `MOON SUN STAR HOURGLASS`, Bookshelf `ESCAPE`, Globe `GREENWICH` — each awards +100 pts).*

---

## 3. LEVEL 2 — THE SECRET LIBRARY

### Room Overview & Interactive Objects
- **Library Ladder** (Center-left): Reveals wall mechanism gear ratio clue (`3:5:9`).
- **Historical Painting** (Upper center wall): Reveals clock time plaque clue (`11:45`).
- **Window** (Upper right): Moonlight reading table clue.
- **Reading Table** (Center foreground): Clue notes reference cabinet coordinates (`7 - 3 - 9 - 2`).
- **Locked Cabinet** (Right foreground): **Critical Objective** Number Code puzzle (`7392`).
- **Old Manuscript** (On reading table): **Critical Objective** Cipher puzzle (`HELLO`).
- **Grandfather Clock** (Between ladder & painting): Clock Combination puzzle (`11:45`).
- **Hidden Bookshelf Section** (Left shelf insert): Pattern puzzle (`◆` / `FILLED DIAMOND`).
- **Main Bookshelf** (Left wall & tall right shelf): Book Arrangement puzzle (`1-4-2-5-3`).
- **Level 3 Button (`Level 3 →`)** (Top right HUD): Transition gate to Level 3.

### Step-by-Step Level 2 Completion
1. Click the **Locked Cabinet** (small wooden cabinet with brass padlock on lower right):
   - Opens the cabinet number code puzzle.
   - Type **`7392`** and click **`SUBMIT`**.
   - *Result:* Correct! You receive `+100 pts` and collect both the **Clock Fragment** and the **Mysterious Book** (`+50 pts`).
2. Click **`CLOSE`**.
3. Click the **Old Manuscript** (lying open on the central reading table):
   - Opens the Caesar cipher puzzle (`CIPHER: 'K H O O R' -> Shift -3`).
   - Type **`HELLO`** and click **`SUBMIT`**.
   - *Result:* Correct! Decoded successfully (`+100 pts`).
4. Click **`CLOSE`**.
5. Click the **`Level 3 →`** button in the top navigation bar:
   - The secret bookshelf swings open, completing Level 2 (`+150 pts bonus`), and transports you to **Level 3 — The Mystery Chamber**.

---

## 4. LEVEL 3 — THE MYSTERY CHAMBER

### Room Overview & Interactive Objects
- **Stone Pillars** (Left colonnade): Reveals guardian statue code clue (`VII - IX - IV - I`).
- **Wall Torches** (Left wall sconces): Flame reactions clue.
- **Wall Mechanism** (Upper center wall): **Critical Objective** Gear Combination puzzle (`3:5:9`).
- **Ancient Clock** (Left of wall mechanism): Time Deduction puzzle (`3`).
- **Floor Symbol** (Lower center stone floor): Geometric Pattern puzzle (`HEXAGON`).
- **Scroll Table** (Lower left table): Astrological Cipher puzzle (`STONE`).
- **Central Pedestal** (Center): Celestial Symbol Sequence puzzle (`SUN MOON JUPITER SATURN URANUS`).
- **Stone Statue** (Far right corner): **Critical Objective** Number Code puzzle (`7941`).
- **Locked Stone Door** (Right wall): **Critical Objective** Gateway to Level 4.

### Step-by-Step Level 3 Completion
1. Click the **Stone Statue** (guardian statue standing on the right):
   - Opens the Hidden Clue number code puzzle (`RUNES: VII - IX - IV - I`).
   - Type **`7941`** and click **`SUBMIT`**.
   - *Result:* Correct! You receive `+100 pts`, the pommel unscrews, and you collect the **Final Key** (`+50 pts`).
2. Click **`CLOSE`**.
3. Click the **Wall Mechanism** (concentric brass gears on the upper center wall):
   - Opens the Mechanical Combination puzzle (`GEAR RATIO: 3:5:9 - TEAL CORE RESONANCE`).
   - Type **`3:5:9`** (or `359`) and click **`SUBMIT`**.
   - *Result:* Correct! Gear teeth mesh into alignment (`+100 pts`).
4. Click **`CLOSE`**.
5. Click the **Locked Stone Door** (massive door on the right wall):
   - The game detects the **Final Key** in your inventory. Ancient counterweights grind open the door, completing Level 3 (`+150 pts bonus`), and you enter **Level 4 — The Final Escape**.

---

## 5. LEVEL 4 — THE FINAL ESCAPE

### Room Overview & Interactive Objectives
To disengage the final escape locks, three mechanisms must be satisfied:
1. **Combination Lock** (`combination_lock` mounted on the golden door)
2. **Symbol Panels** (`symbol_panels` on the wall)
3. **Central Pedestal** (`pedestal` / `pedestal_final` in front of the door)

Other objects:
- **Hourglass** (On document table): Clue inspection.
- **Document Table** (Left desk): Cipher puzzle (`SANCTUARY`).
- **Floor Pattern** (Labyrinth on floor): Pattern puzzle (`LABYRINTHOS`).
- **Wall Mechanisms** (Upper left wall gears): Combination puzzle (`SYNCHRONY`).
- **Stone Pillars** (Flanking columns): Inscribed with symbol sequence clue (`SUN - MOON - STAR - SERPENT`).
- **Golden Exit Door** (Center grand portal): Final victory trigger.

### Step-by-Step Level 4 Completion
1. Click the **Combination Lock** (bronze lock dial mounted directly on the Golden Exit Door):
   - Type **`3791`** and click **`SUBMIT`**.
   - *Result:* Correct! `+100 pts`. The lock dial clicks disengaged.
   - Click **`CLOSE`**.
2. Click the **Symbol Panels** (four carved plaques on the wall to the left of the door):
   - Type **`SUN MOON STAR SERPENT`** and click **`SUBMIT`**.
   - *Result:* Correct! `+100 pts`. Locking counterweights drop into place.
   - Click **`CLOSE`**.
3. Click the **Central Pedestal** (golden plinth on the floor directly in front of the door):
   - Type **`KEY OF OSIRIS`** (or `FINAL KEY`) and click **`SUBMIT`**.
   - *Result:* Correct! `+100 pts`. The obsidian Final Key locks firmly into the ceremonial receptacle.
   - Click **`CLOSE`**.
4. Click the **Golden Exit Door** (the grand illuminated portal):
   - The game verifies all 3 final conditions:
     1. Combination Lock solved: **YES**
     2. Symbol Panels sequence solved: **YES**
     3. Pedestal Master Key inserted: **YES**
   - The countdown timer stops immediately.
   - Victory points and remaining-time bonuses are tallied.
   - The game triggers the **Win Screen (`WinPanel`)** displaying:
     - Golden trophy illustration and celebration confetti
     - **FINAL SCORE** (Base score + time bonus)
     - **TIME TAKEN** (Actual elapsed time `MM:SS`)
     - **ROOMS CLEARED: `4/4`**

---

## 6. COMPLETE MASTER ANSWER SHEET

| Level | Puzzle Name | Puzzle ID | Object to Click | Exact Accepted Answer(s) | Required Item | Outcome / Reward |
|:---:|:---|:---|:---|:---|:---:|:---|
| **1** | The Iron Chest Keypad | `chest_code` | Locked Chest | `4815` *(or `4-8-1-5`)* | None | `+100 pts`, Unlocks **Golden Key** & **Old Photo** |
| **1** | The Celestial Glyphs | `painting_symbols` | Painting | `MOON SUN STAR HOURGLASS` | None | `+100 pts` |
| **1** | The Scholar's Cryptogram | `bookshelf_cipher` | Bookshelf | `ESCAPE` *(or `SECRET`)* | None | `+100 pts` |
| **1** | The Mosaic Tiles | `desk_pattern` | Study Desk | `◇` *(or `HOLLOW DIAMOND`)* | None | `+100 pts` |
| **1** | The Pendulum Tumbler | `clock_combination` | Wall Clock | `369` *(or `3-6-9`, `3:15`)* | None | `+100 pts` |
| **1** | The Explorer's Deduction | `globe_deduction` | Globe | `GREENWICH` *(or `45N`)* | None | `+100 pts` |
| **1** | Exit Door Transition | `door` | Exit Door | *None (Click Door)* | **Golden Key** | Completes Level 1 (`+150 pts`), Unlocks Level 2 |
| **2** | Combination Puzzle | `locked_cabinet` | Locked Cabinet | `7392` *(or `7-3-9-2`)* | None | `+100 pts`, Unlocks **Clock Fragment** & **Book** |
| **2** | Cipher Puzzle | `manuscript` | Old Manuscript | `HELLO` | None | `+100 pts` |
| **2** | Clock Puzzle | `grandfather_clock` | Grandfather Clock | `11:45` *(or `1145`)* | None | `+100 pts` |
| **2** | Secret Pattern Puzzle | `hidden_bookshelf` | Hidden Bookshelf | `◆` *(or `FILLED DIAMOND`)* | None | `+100 pts` |
| **2** | Book Arrangement | `bookshelf` | Main Bookshelf | `1-4-2-5-3` *(or `14253`)* | None | `+100 pts` |
| **2** | Level 3 Transition | `level3Button` | Top HUD Button | *None (Click Button)* | Objectives Solved | Completes Level 2 (`+150 pts`), Unlocks Level 3 |
| **3** | Hidden Clue | `statue` | Stone Statue | `7941` *(or `7-9-4-1`)* | None | `+100 pts`, Unlocks **Final Key** |
| **3** | Mechanical Puzzle | `wall_mechanism` | Wall Mechanism | `3:5:9` *(or `359`)* | None | `+100 pts` |
| **3** | Time Deduction | `clock` | Ancient Clock | `3` *(or `III`, `3:00`)* | None | `+100 pts` |
| **3** | Pattern Puzzle | `floor_symbol` | Floor Symbol | `HEXAGON` *(or `⬢`)* | None | `+100 pts` |
| **3** | Cipher Puzzle | `scroll_table` | Scroll Table | `STONE` *(or `ALTAR`)* | None | `+100 pts` |
| **3** | Symbol Alignment | `pedestal` | Central Pedestal | `SUN MOON JUPITER SATURN URANUS` | None | `+100 pts` |
| **3** | Stone Door Transition | `door` | Locked Stone Door | *None (Click Door)* | **Final Key** | Completes Level 3 (`+150 pts`), Unlocks Level 4 |
| **4** | Final Combination | `combination_lock` | Combination Lock | `3791` *(or `3-7-9-1`)* | None | `+100 pts`, Disengages door lock dial |
| **4** | Symbol Sequence | `symbol_panels` | Symbol Panels | `SUN MOON STAR SERPENT` | None | `+100 pts`, Releases door counterweights |
| **4** | Multi-Part Master Key | `pedestal_final` | Central Pedestal | `KEY OF OSIRIS` *(or `FINAL KEY`)* | **Final Key** | `+100 pts`, Receptacle lock engages |
| **4** | Final Clue Puzzle | `document_table` | Document Table | `SANCTUARY` | None | `+100 pts` (Optional) |
| **4** | Floor Pattern Puzzle | `floor_pattern` | Floor Pattern | `LABYRINTHOS` | None | `+100 pts` (Optional) |
| **4** | Mechanism Puzzle | `wall_mechanisms` | Wall Mechanisms | `SYNCHRONY` | None | `+100 pts` (Optional) |
| **4** | Grand Escape Portal | `golden_door` | Golden Exit Door | *None (Click Door)* | All 3 Solved | **VICTORY! Triggers Win Screen** |

---

## 7. FASTEST ROUTE TO WIN (SPEEDRUN GUIDE)

1. **Start Game:** Name: `Detective`, Difficulty: `EASY` $\rightarrow$ Click **`ENTER THE MANSION`**.
2. **Level 1 (The Study):**
   - Click **Locked Chest** $\rightarrow$ Type **`4815`** $\rightarrow$ Click **`SUBMIT`** $\rightarrow$ Click **`CLOSE`** (Obtain Golden Key).
   - Click **Exit Door** $\rightarrow$ Enters **Level 2**.
3. **Level 2 (The Secret Library):**
   - Click **Locked Cabinet** $\rightarrow$ Type **`7392`** $\rightarrow$ Click **`SUBMIT`** $\rightarrow$ Click **`CLOSE`**.
   - Click **Old Manuscript** $\rightarrow$ Type **`HELLO`** $\rightarrow$ Click **`SUBMIT`** $\rightarrow$ Click **`CLOSE`**.
   - Click **`Level 3 →`** button in top HUD $\rightarrow$ Enters **Level 3**.
4. **Level 3 (The Mystery Chamber):**
   - Click **Stone Statue** $\rightarrow$ Type **`7941`** $\rightarrow$ Click **`SUBMIT`** $\rightarrow$ Click **`CLOSE`** (Obtain Final Key).
   - Click **Wall Mechanism** $\rightarrow$ Type **`3:5:9`** $\rightarrow$ Click **`SUBMIT`** $\rightarrow$ Click **`CLOSE`**.
   - Click **Locked Stone Door** $\rightarrow$ Enters **Level 4**.
5. **Level 4 (The Final Escape):**
   - Click **Combination Lock** $\rightarrow$ Type **`3791`** $\rightarrow$ Click **`SUBMIT`** $\rightarrow$ Click **`CLOSE`**.
   - Click **Symbol Panels** $\rightarrow$ Type **`SUN MOON STAR SERPENT`** $\rightarrow$ Click **`SUBMIT`** $\rightarrow$ Click **`CLOSE`**.
   - Click **Central Pedestal** $\rightarrow$ Type **`KEY OF OSIRIS`** $\rightarrow$ Click **`SUBMIT`** $\rightarrow$ Click **`CLOSE`**.
   - Click **Golden Exit Door** $\rightarrow$ **YOU WIN!**

---

## 8. TROUBLESHOOTING GUIDE

| Issue | Cause | Solution |
|---|---|---|
| **Answer is rejected** | Typo or extra characters. | Answers are case-insensitive and ignore spaces/hyphens. Refer to the Master Answer Sheet above. |
| **"Please enter an answer before submitting"** | The text box is empty. | Click inside the text box or use the keypad, enter your answer, then click `SUBMIT` or press `Enter`. |
| **"This puzzle has already been solved!"** | Puzzle already solved earlier. | Solved puzzles cannot be resubmitted. Click `CLOSE` or `X` and proceed to your next objective. |
| **Door in Level 1 remains locked** | Iron Chest not solved. | Click the Locked Chest on the floor, enter `4815`, and close the modal. Once the Golden Key is in your inventory, click the door again. |
| **Level 3 button says locked in Library** | Cabinet or Manuscript not solved. | Solve both the Locked Cabinet (`7392`) and the Manuscript Cipher (`HELLO`). Once solved, clicking `Level 3 →` immediately opens the staircase. |
| **Stone Door in Level 3 remains locked** | Final Key not collected from statue. | Click the Stone Statue on the right, enter `7941`, and collect the Obsidian Final Key. Then click the stone door. |
| **Golden Door in Level 4 won't open** | One of the 3 required locks is incomplete. | Inspect the three key spots: 1) Combination Lock (`3791`), 2) Symbol Panels (`SUN MOON STAR SERPENT`), and 3) Pedestal (`KEY OF OSIRIS`). Once all three are solved, click the door to trigger victory. |
| **Timer is running low** | Normal countdown. | The timer runs across the entire playthrough. If time expires, click `TRY AGAIN` to restart with full time. Playing on `EASY` grants 20 full minutes. |
| **Inventory slot item cannot be used** | Item clicked inside modal without an active target. | Items are contextual. Keys automatically unlock their corresponding doors upon clicking the door object. Using items inside the inventory modal reveals lore inspection text. |
