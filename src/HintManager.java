import java.util.HashMap;
import java.util.Map;

/**
 * Manages puzzle hints, progressive hint tiers, hint usage counts,
 * and hint score deductions for "THE LAST EXPERIMENT — A Detective Mystery Adventure".
 */
public class HintManager {

    public static final int HINT_PENALTY_POINTS = 25;

    // Tracks how many hints have been requested for each puzzle ID
    private final Map<String, Integer> hintUsagePerPuzzle;
    private int totalHintsUsed;

    // Progressive hints per puzzle ID (3 progressive tiers per puzzle)
    private final Map<String, String[]> progressiveHints;

    public HintManager() {
        this.hintUsagePerPuzzle = new HashMap<>();
        this.progressiveHints = new HashMap<>();
        this.totalHintsUsed = 0;

        initProgressiveHints();
    }

    private void initProgressiveHints() {
        // =====================================================================
        // LEVEL 1: THE DETECTIVE'S STUDY
        // =====================================================================
        progressiveHints.put("torn_newspaper", new String[] {
            "Reconstruct the torn newspaper scrap found among Dr. Vance's belongings.",
            "The headline highlights the research facility's founding year.",
            "The circled 4-digit year is 1947."
        });

        progressiveHints.put("desk_drawer", new String[] {
            "The desk drawer lock requires a 3-digit numerical code.",
            "Cross-reference the stopped desk clock hands with the calendar markings.",
            "The code aligns with the stopped clock hour and minute: 4-2-5."
        });

        progressiveHints.put("suspect_cipher", new String[] {
            "Dr. Vance left an encrypted suspect note using a simple Caesar shift (-1).",
            "Each letter in 'C P P L T I F M G' shifted backward reveals the hiding place.",
            "The decrypted keyword is BOOKSHELF."
        });

        progressiveHints.put("study_clock", new String[] {
            "Examine the clock's stopped hands on the study mantelpiece.",
            "The hour hand points just past 4, and the minute hand rests exactly on the 5th interval (25 mins).",
            "Enter the time code: 425."
        });

        progressiveHints.put("study_portrait", new String[] {
            "The portrait depicts an ancient Greek mathematician with the inscription 'Eureka! - Syracuse'.",
            "The scientist is famous for hydrostatics and the lever principle.",
            "Enter the scholar's name: ARCHIMEDES."
        });

        progressiveHints.put("study_globe", new String[] {
            "The coordinates inscribed on the brass meridian ring point to zero longitude.",
            "The prime meridian passes through the historic observatory in England.",
            "Enter the location keyword: GREENWICH."
        });

        // Level 1 Legacy / Test Aliases
        progressiveHints.put("chest_code", progressiveHints.get("torn_newspaper"));
        progressiveHints.put("bookshelf_cipher", progressiveHints.get("suspect_cipher"));
        progressiveHints.put("desk_pattern", new String[] {
            "Inspect the alternating diamond tiles on the desk inlay.",
            "Notice the pattern: Filled, Hollow, Filled, Hollow.",
            "The missing symbol is the Hollow Diamond (\u25C7)."
        });
        progressiveHints.put("clock_combination", progressiveHints.get("study_clock"));
        progressiveHints.put("painting_symbols", new String[] {
            "Observe the security protocol sequence: Power, Cooling, Vacuum, Shield.",
            "Order: POWER \u2192 COOLING \u2192 VACUUM \u2192 SHIELD.",
            "Enter: POWER COOLING VACUUM SHIELD."
        });
        progressiveHints.put("globe_deduction", progressiveHints.get("study_globe"));

        // =====================================================================
        // LEVEL 2: THE FORGOTTEN LIBRARY
        // =====================================================================
        progressiveHints.put("book_arrangement", new String[] {
            "The research volumes on the library shelf are numbered in Roman numerals.",
            "Arrange the numbered volumes in standard ascending chronological sequence (1 through 5).",
            "Enter sequence: 12345 (or 1-2-3-4-5)."
        });

        progressiveHints.put("caesar_cipher", new String[] {
            "The manuscript cipher uses a Caesar shift of -3.",
            "D \u2192 A, U \u2192 R, W \u2192 T, L \u2192 I, I \u2192 F, D \u2192 A, F \u2192 C, W \u2192 T.",
            "The decoded keyword is ARTIFACT."
        });

        progressiveHints.put("missing_manuscript", new String[] {
            "Search the high archive shelf using the library ladder.",
            "The torn manuscript title refers to Dr. Vance's classified project.",
            "Enter the missing word: EXPERIMENT."
        });

        progressiveHints.put("hidden_shelf", new String[] {
            "Inspect the historical painting plaque and reading table notes.",
            "The archive catalog index is recorded as a 4-digit numerical code.",
            "Enter code: 4921."
        });

        progressiveHints.put("grandfather_clock", new String[] {
            "Examine the clock's stopped hands: hour hand on XI and minute hand on IX.",
            "XI corresponds to 11 o'clock and IX corresponds to 45 minutes.",
            "Enter time: 11:45."
        });

        progressiveHints.put("hidden_bookshelf", new String[] {
            "Look closely at the gold embossed diamond foil along the shelf spines.",
            "The alternating sequence requires the next diamond shape.",
            "The missing symbol is the Hollow Diamond (\u25C7)."
        });

        // Level 2 Legacy / Test Aliases
        progressiveHints.put("bookshelf", progressiveHints.get("book_arrangement"));
        progressiveHints.put("manuscript", progressiveHints.get("caesar_cipher"));
        progressiveHints.put("locked_cabinet", progressiveHints.get("hidden_shelf"));

        // =====================================================================
        // LEVEL 3: THE ABANDONED LABORATORY
        // =====================================================================
        progressiveHints.put("circuit_puzzle", new String[] {
            "Restore power across the 4 laboratory electrical breaker conduits in order.",
            "The high-voltage conduit columns list the bus markers: Alpha, Beta, Gamma, Delta.",
            "Enter sequence: ALPHA BETA GAMMA DELTA."
        });

        progressiveHints.put("chemical_sequence", new String[] {
            "The chemical reaction console requires reagent injection in proper order.",
            "Standard lab protocol: First dissolve solvent, add catalyst, introduce reagent, and stabilize with isotope.",
            "Enter sequence: SOLVENT CATALYST REAGENT ISOTOPE."
        });

        progressiveHints.put("experiment_logic", new String[] {
            "Dr. Vance logged his final breakthrough under a specific classified experiment number.",
            "Check the lab bench notes referencing Project Resonance.",
            "Enter experiment code: EXP 103 (or 103)."
        });

        progressiveHints.put("security_terminal", new String[] {
            "The facility lockdown terminal requires an administrative override passcode.",
            "The override code is noted in Experiment Report 103: 8 - 4 - 6 - 2.",
            "Enter override passcode: 8462."
        });

        progressiveHints.put("reactor_cipher", new String[] {
            "The resonance frequency dial is locked with a keyword.",
            "The keyword identifies the facility's central power core.",
            "Enter keyword: REACTOR."
        });

        progressiveHints.put("laser_grid", new String[] {
            "The security laser sensors form a geometric grid across the tiled floor.",
            "The 6-sided optical configuration completes the path.",
            "Enter geometric shape: HEXAGON."
        });

        // Level 3 Legacy / Test Aliases
        progressiveHints.put("wall_mechanism", progressiveHints.get("circuit_puzzle"));
        progressiveHints.put("pedestal", progressiveHints.get("chemical_sequence"));
        progressiveHints.put("scroll_table", progressiveHints.get("experiment_logic"));
        progressiveHints.put("statue", progressiveHints.get("security_terminal"));
        progressiveHints.put("clock", progressiveHints.get("reactor_cipher"));
        progressiveHints.put("floor_symbol", progressiveHints.get("laser_grid"));

        // =====================================================================
        // LEVEL 4: THE ANCIENT VAULT
        // =====================================================================
        progressiveHints.put("vault_combination", new String[] {
            "The heavy steel blast door is secured by four concentric dial tumblers.",
            "The code is stamped into the vault structural blueprint: 5 - 2 - 8 - 4.",
            "Enter combination code: 5284."
        });

        progressiveHints.put("containment_relays", new String[] {
            "Depress the four facility containment relays in critical safety order.",
            "Safety sequence: First establish Power, activate Cooling, draw Vacuum, and raise Shield.",
            "Enter sequence: POWER COOLING VACUUM SHIELD."
        });

        progressiveHints.put("artifact_arrangement", new String[] {
            "Place the recovered artifact components into the ancient altar receptacles.",
            "Alignment order from left to right: Relic, Core, Matrix, and Artifact.",
            "Enter sequence: RELIC CORE MATRIX ARTIFACT."
        });

        progressiveHints.put("final_message", new String[] {
            "Decode Dr. Vance's final transmission stored on the research terminal.",
            "The message confirms the outcome of the research project.",
            "Enter message: EXPERIMENT COMPLETE."
        });

        progressiveHints.put("superconductor_grid", new String[] {
            "The floor grid conducts power along an intricate geometric maze.",
            "The ancient word for the intricate maze path.",
            "Enter: LABYRINTH."
        });

        progressiveHints.put("hydraulic_valves", new String[] {
            "All hydraulic pressure release valves must operate in synchronized timing.",
            "Enter the keyword for perfect harmonic timing: SYNCHRONY."
        });

        // Level 4 Legacy / Test Aliases
        progressiveHints.put("combination_lock", progressiveHints.get("vault_combination"));
        progressiveHints.put("symbol_panels", progressiveHints.get("containment_relays"));
        progressiveHints.put("pedestal_final", progressiveHints.get("artifact_arrangement"));
        progressiveHints.put("document_table", progressiveHints.get("final_message"));
        progressiveHints.put("floor_pattern", progressiveHints.get("superconductor_grid"));
        progressiveHints.put("wall_mechanisms", progressiveHints.get("hydraulic_valves"));
    }

    /**
     * Requests a progressive hint for the given puzzle ID.
     * Deducts points from scoreManager and returns the hint text.
     */
    public String requestHint(String puzzleId, ScoreManager scoreManager) {
        if (puzzleId == null) {
            return "Inspect your surroundings carefully for clues.";
        }

        int currentUsage = hintUsagePerPuzzle.getOrDefault(puzzleId, 0);
        String[] hints = progressiveHints.get(puzzleId);

        String hintText;
        if (hints != null && hints.length > 0) {
            int index = Math.min(currentUsage, hints.length - 1);
            hintText = hints[index];
        } else {
            hintText = "Look closely at the documents and scientific apparatus in this room.";
        }

        // Deduct score penalty
        if (scoreManager != null) {
            scoreManager.deductScore(HINT_PENALTY_POINTS);
        }

        hintUsagePerPuzzle.put(puzzleId, currentUsage + 1);
        totalHintsUsed++;

        return hintText;
    }

    public int getHintsUsedForPuzzle(String puzzleId) {
        return hintUsagePerPuzzle.getOrDefault(puzzleId, 0);
    }

    public int getTotalHintsUsed() {
        return totalHintsUsed;
    }

    public int getHintPenalty() {
        return HINT_PENALTY_POINTS;
    }

    public void reset() {
        hintUsagePerPuzzle.clear();
        totalHintsUsed = 0;
    }
}
