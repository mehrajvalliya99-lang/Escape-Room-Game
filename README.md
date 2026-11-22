# Escape the Room – Mystery Puzzle Adventure

An atmospheric 2D mystery puzzle adventure game developed in Java. The player takes on the role of a detective investigating an enigmatic ancestral mansion, solving arcane riddles, unlocking ancient mechanisms, and ultimately finding their way through the grand golden portal in the final escape chamber.

---

## 🛠 Technology Stack

- **Language:** Java SE (JDK 17+)
- **GUI Framework:** Java Swing (`JFrame`, `JPanel`, `CardLayout`, `Timer`)
- **Graphics & Rendering:** Java AWT & `Graphics2D`
  - 100% procedural 2D visual environments (no external image assets required)
  - Layered rendering with `GradientPaint`, `LinearGradientPaint`, and `RadialGradientPaint`
  - High-precision antialiasing (`RenderingHints`) and alpha compositing (`AlphaComposite`)
  - Resolution: 1280 × 720 logical canvas with dynamic aspect-ratio letterboxing
- **Architecture:** Decoupled Model-View-Controller (MVC) architecture with unified state management via `GameController` and `GameState`.

---

## 🚀 How to Run the Project

### Prerequisites
- JDK 17 or later installed and added to your system `PATH`.

### Compile
From the project root directory, run:
```bash
javac -d bin src/*.java src/models/*.java src/screens/*.java
```

### Run
Launch the application:
```bash
java -cp bin Main
```

---

## 🎮 Completed Features (Member 1 — Frontend & 2D Environments)

### 1. Screen & Navigation Architecture
- **State Management:** Clean `GameState` enum and `GameController` orchestrating screen transitions via `CardLayout`.
- **Player Details Persistence:** Player name and chosen difficulty level (`EASY`, `MEDIUM`, `HARD`) persist across all levels, HUD components, and dialogs.
- **Top HUD & Message Bar:** Reusable top HUD displaying player badge, current room title, score placeholder, dynamic timer format, and inventory button. Bottom message bar displays object hover names and click inspection descriptions.

### 2. Implemented Game Screens
1. **Home Screen (`HomePanel`):** Title typography with drop shadow, atmospheric mansion backdrop, and interactive gold-trimmed menu buttons (`START GAME`, `HOW TO PLAY`, `EXIT`).
2. **Instructions Screen (`InstructionsPanel`):** Tabbed / paginated guide cards with custom glyph icons and navigation controls.
3. **Player Details Screen (`PlayerDetailsPanel`):** Text input for detective name, difficulty mode selector with time limits, and adventure launch button.
4. **Level 1 — The Study (`RoomPanel`):** 10 interactive objects (Bookshelf, Desk, Desk Drawer, Wall Clock, Painting, Chest, Lamp, Globe, Plant Pot, Exit Door), fireplace glow, and window rain animations.
5. **Level 2 — The Secret Library (`LibraryPanel`):** 9 interactive objects (Bookshelf, Reading Table, Old Manuscript, Library Ladder, Grandfather Clock, Historical Painting, Locked Cabinet, Window, Hidden Bookshelf Section) with warm golden lighting.
6. **Level 3 — The Mystery Chamber (`MysteryChamberPanel`):** 9 interactive objects (Central Pedestal, Stone Pillars, Wall Mechanism, Ancient Clock, Locked Stone Door, Wall Torches, Floor Symbol, Scroll Table, Stone Statue) with flickering torchlight and teal rune glows.
7. **Level 4 — The Final Escape (`FinalEscapePanel`):** 10 interactive objects (Golden Exit Door, Combination Lock, Stone Pillars, Central Pedestal, Four Symbol Panels, Hourglass, Document Table, Torches, Floor Pattern, Wall Mechanisms), grand golden radiance, fluted columns, and sacred geometric floor labyrinth.
8. **Inventory Modal (`InventoryPanel`):** 3×3 item grid with selection states, item inspection panel, inspect/use action buttons, sample items loader, and background darkening scrim.
9. **Puzzle Modal (`PuzzlePanel`):** Universal puzzle UI supporting 6 puzzle formats:
   - Number Code
   - Symbol Sequence
   - Cipher & Cryptogram
   - Pattern Recognition
   - Combination Lock
   - Logic & Deduction
10. **Win Screen (`WinPanel`):** Celebratory golden trophy illustration, results statistics, and replay/exit controls.
11. **Game Over Screen (`GameOverPanel`):** Ominous antique hourglass illustration, results dossier, and try-again controls.

---

## 🧩 Member 2 — Remaining Backend & Gameplay Tasks

The following gameplay systems and integration points are clearly marked with `// TODO: Member 2` comments across the codebase:

1. **Puzzle Logic (`// TODO: Member 2 - puzzle logic`):**
   - Implement `PuzzleManager` to supply room-specific puzzle data and solutions.
   - Implement answer validation logic for puzzle input fields and symbols.
2. **Hint System (`// TODO: Member 2 - hint system`):**
   - Implement `HintManager` to provide progressive clues, hint penalties, and cooldowns.
3. **Inventory Logic (`// TODO: Member 2 - inventory logic`):**
   - Implement `InventoryManager` to handle item collection, real inventory state, and item-on-object usage.
4. **Timer System (`// TODO: Member 2 - timer`):**
   - Connect active countdown timer thread / Swing `Timer` based on chosen difficulty (15, 20, or 25 mins).
   - Trigger `GameOverPanel` upon timer expiration.
5. **Score Calculation (`// TODO: Member 2 - scoring`):**
   - Implement `ScoreManager` to compute score from solved puzzles, time remaining, and hint deductions.
6. **Level Progression (`// TODO: Member 2 - level progression`):**
   - Implement door unlocking conditions and puzzle-gate transitions between rooms.
7. **Final Escape Condition (`// TODO: Member 2 - final escape condition`):**
   - Connect the final combination lock and four symbol releases on Level 4 to trigger the `WinPanel` upon escaping.

---

## 📁 Project Structure

```
EscapeRoomGame/
├── README.md
├── bin/                       # Compiled bytecode
└── src/
    ├── GameController.java    # Central state & screen controller
    ├── GameFrame.java         # Main window (CardLayout)
    ├── GameState.java         # Screen & application states
    ├── Main.java              # Application entry point
    ├── models/
    │   ├── Difficulty.java    # Difficulty enum & duration settings
    │   ├── GameObject.java    # Interactive object model (hitbox, hover, click)
    │   ├── InventoryItem.java # Item data model & item types
    │   ├── Player.java        # Player profile model
    │   └── Puzzle.java        # Puzzle model & 6 puzzle category types
    └── screens/
        ├── FinalEscapePanel.java     # Level 4 — The Final Escape
        ├── GameOverPanel.java        # Game Over Screen
        ├── HomePanel.java            # Home / Title Screen
        ├── InstructionsPanel.java    # How to Play Screen
        ├── InventoryPanel.java       # Inventory Modal
        ├── LibraryPanel.java         # Level 2 — The Secret Library
        ├── MysteryChamberPanel.java  # Level 3 — The Mystery Chamber
        ├── PlayerDetailsPanel.java   # Player details & difficulty setup
        ├── PuzzlePanel.java          # Puzzle Modal UI
        ├── RoomPanel.java            # Level 1 — The Study
        └── WinPanel.java             # Win Screen
```
