/**
 * Automated Headless Test Suite for
 * "THE LAST EXPERIMENT — A Detective Mystery Adventure"
 */
public class GameTransformationTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("RUNNING HEADLESS GAME VERIFICATION TEST SUITE");
        System.out.println("==================================================");

        int totalTests = 0;
        int passedTests = 0;

        // 1. TEST PUZZLE MANAGER (Level 1)
        PuzzleManager pm = new PuzzleManager();
        totalTests++;
        if (pm.validateAnswer("torn_newspaper", "1947") &&
            pm.validateAnswer("desk_drawer", "425") &&
            pm.validateAnswer("suspect_cipher", "BOOKSHELF") &&
            pm.validateAnswer("study_clock", "425") &&
            pm.validateAnswer("study_portrait", "ARCHIMEDES") &&
            pm.validateAnswer("study_globe", "GREENWICH")) {
            System.out.println("[PASS] Level 1 Puzzles answer validation verified.");
            passedTests++;
        } else {
            System.out.println("[FAIL] Level 1 Puzzles answer validation failed.");
        }

        // 2. TEST PUZZLE MANAGER (Level 2)
        totalTests++;
        if (pm.validateAnswer("book_arrangement", "12345") &&
            pm.validateAnswer("caesar_cipher", "ARTIFACT") &&
            pm.validateAnswer("missing_manuscript", "EXPERIMENT") &&
            pm.validateAnswer("hidden_shelf", "4921") &&
            pm.validateAnswer("grandfather_clock", "11:45") &&
            pm.validateAnswer("hidden_bookshelf", "\u25C7")) {
            System.out.println("[PASS] Level 2 Puzzles answer validation verified.");
            passedTests++;
        } else {
            System.out.println("[FAIL] Level 2 Puzzles answer validation failed.");
        }

        // 3. TEST PUZZLE MANAGER (Level 3)
        totalTests++;
        if (pm.validateAnswer("circuit_puzzle", "ALPHA BETA GAMMA DELTA") &&
            pm.validateAnswer("chemical_sequence", "SOLVENT CATALYST REAGENT ISOTOPE") &&
            pm.validateAnswer("experiment_logic", "EXP 103") &&
            pm.validateAnswer("security_terminal", "8462") &&
            pm.validateAnswer("reactor_cipher", "REACTOR") &&
            pm.validateAnswer("laser_grid", "HEXAGON")) {
            System.out.println("[PASS] Level 3 Puzzles answer validation verified.");
            passedTests++;
        } else {
            System.out.println("[FAIL] Level 3 Puzzles answer validation failed.");
        }

        // 4. TEST PUZZLE MANAGER (Level 4)
        totalTests++;
        if (pm.validateAnswer("vault_combination", "5284") &&
            pm.validateAnswer("containment_relays", "POWER COOLING VACUUM SHIELD") &&
            pm.validateAnswer("artifact_arrangement", "RELIC CORE MATRIX ARTIFACT") &&
            pm.validateAnswer("final_message", "EXPERIMENT COMPLETE") &&
            pm.validateAnswer("superconductor_grid", "LABYRINTH") &&
            pm.validateAnswer("hydraulic_valves", "SYNCHRONY")) {
            System.out.println("[PASS] Level 4 Puzzles answer validation verified.");
            passedTests++;
        } else {
            System.out.println("[FAIL] Level 4 Puzzles answer validation failed.");
        }

        // 5. TEST COMPATIBILITY ALIASES
        totalTests++;
        if (pm.validateAnswer("chest_code", "1947") &&
            pm.validateAnswer("manuscript", "ARTIFACT") &&
            pm.validateAnswer("locked_cabinet", "4921") &&
            pm.validateAnswer("wall_mechanism", "ALPHA BETA GAMMA DELTA") &&
            pm.validateAnswer("statue", "8462") &&
            pm.validateAnswer("combination_lock", "5284") &&
            pm.validateAnswer("symbol_panels", "POWER COOLING VACUUM SHIELD") &&
            pm.validateAnswer("pedestal_final", "RELIC CORE MATRIX ARTIFACT")) {
            System.out.println("[PASS] Backwards compatibility aliases verified.");
            passedTests++;
        } else {
            System.out.println("[FAIL] Backwards compatibility aliases failed.");
        }

        // 6. TEST HINT MANAGER PROGRESSION & NO ASTROLOGY
        totalTests++;
        HintManager hm = new HintManager();
        ScoreManager sm = new ScoreManager();
        sm.addScore(500);
        String hint1 = hm.requestHint("torn_newspaper", sm);
        String hint2 = hm.requestHint("torn_newspaper", sm);
        String hint3 = hm.requestHint("torn_newspaper", sm);
        if (hint1 != null && hint2 != null && hint3 != null &&
            !hint1.equals(hint2) && sm.getScore() == 500 - 3 * HintManager.HINT_PENALTY_POINTS) {
            System.out.println("[PASS] HintManager progressive tiers & score deductions verified.");
            passedTests++;
        } else {
            System.out.println("[FAIL] HintManager progressive tiers failed.");
        }

        // 7. TEST INVENTORY MANAGER USAGE
        totalTests++;
        InventoryManager im = new InventoryManager();
        im.addItem(InventoryItem.createLibraryKey());
        im.addItem(InventoryItem.createLabCard());
        im.addItem(InventoryItem.createVaultKey());
        im.addItem(InventoryItem.createAncientArtifact());
        String useMsg = im.useItem(im.getItem("library_key"), GameState.PLAYING);
        if (im.getItemCount() == 4 && useMsg.contains("study exit lock")) {
            System.out.println("[PASS] InventoryManager items & contextual usage verified.");
            passedTests++;
        } else {
            System.out.println("[FAIL] InventoryManager items & contextual usage failed.");
        }

        // 8. TEST LEVEL MANAGER & GATING
        totalTests++;
        LevelManager lm = new LevelManager();
        if (lm.getCurrentLevel() == 1 && lm.isLevelUnlocked(1) && !lm.isLevelUnlocked(2)) {
            lm.completeLevel(1);
            if (lm.isLevelUnlocked(2)) {
                System.out.println("[PASS] LevelManager level unlocking & progression verified.");
                passedTests++;
            } else {
                System.out.println("[FAIL] LevelManager level progression failed.");
            }
        } else {
            System.out.println("[FAIL] LevelManager initial state failed.");
        }

        // 9. TEST FINAL ESCAPE EVALUATION
        totalTests++;
        pm.reset();
        boolean canEscapeBefore = lm.canEscapeFinalDoor(pm, im);
        pm.markSolved("vault_combination");
        pm.markSolved("containment_relays");
        pm.markSolved("artifact_arrangement");
        boolean canEscapeAfter = lm.canEscapeFinalDoor(pm, im);
        if (!canEscapeBefore && canEscapeAfter) {
            System.out.println("[PASS] LevelManager final escape condition gating verified.");
            passedTests++;
        } else {
            System.out.println("[FAIL] LevelManager final escape condition gating failed.");
        }

        // 10. FULL GAME CONTROLLER SIMULATION
        totalTests++;
        GameController gc = new GameController();
        Player player = new Player("Detective Holmes", Difficulty.EASY);
        gc.startNewGame(player);
        if (gc.getCurrentState() == GameState.PLAYING && gc.getLevelManager().getCurrentLevel() == 1) {
            // Solve Level 1
            gc.getPuzzleManager().markSolved("torn_newspaper");
            gc.getInventoryManager().addItem(InventoryItem.createLibraryKey());
            gc.getLevelManager().completeLevel(1);
            gc.changeState(GameState.LEVEL_2);

            // Solve Level 2
            gc.getPuzzleManager().markSolved("hidden_shelf");
            gc.getInventoryManager().addItem(InventoryItem.createLabCard());
            gc.getLevelManager().completeLevel(2);
            gc.changeState(GameState.LEVEL_3);

            // Solve Level 3
            gc.getPuzzleManager().markSolved("circuit_puzzle");
            gc.getPuzzleManager().markSolved("security_terminal");
            gc.getInventoryManager().addItem(InventoryItem.createVaultKey());
            gc.getLevelManager().completeLevel(3);
            gc.changeState(GameState.LEVEL_4);

            // Solve Level 4 & Escape
            gc.getPuzzleManager().markSolved("vault_combination");
            gc.getPuzzleManager().markSolved("containment_relays");
            gc.getPuzzleManager().markSolved("artifact_arrangement");
            boolean escapeReady = gc.getLevelManager().canEscapeFinalDoor(gc.getPuzzleManager(), gc.getInventoryManager());

            if (escapeReady && gc.getLevelManager().getCurrentLevel() == 4) {
                gc.triggerWin(1500, "02:45", "4/4");
                if (gc.getCurrentState() == GameState.WIN) {
                    System.out.println("[PASS] Complete GameController simulation (Start -> Win) verified.");
                    passedTests++;
                } else {
                    System.out.println("[FAIL] Win state transition failed.");
                }
            } else {
                System.out.println("[FAIL] Level 4 escape evaluation failed in controller.");
            }
        } else {
            System.out.println("[FAIL] GameController initialization failed.");
        }

        System.out.println("==================================================");
        System.out.println("RESULTS: " + passedTests + "/" + totalTests + " TESTS PASSED");
        System.out.println("==================================================");

        if (passedTests != totalTests) {
            System.exit(1);
        }
    }
}
