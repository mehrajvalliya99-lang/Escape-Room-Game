import java.util.HashMap;
import java.util.Map;

/**
 * Manages puzzle hints, progressive hint tiers, hint usage counts,
 * and hint score deductions.
 */
public class HintManager {

    public static final int HINT_PENALTY_POINTS = 25;

    // Tracks how many hints have been requested for each puzzle ID
    private final Map<String, Integer> hintUsagePerPuzzle;
    private int totalHintsUsed;

    // Progressive hints per puzzle ID
    private final Map<String, String[]> progressiveHints;

    public HintManager() {
        this.hintUsagePerPuzzle = new HashMap<>();
        this.progressiveHints = new HashMap<>();
        this.totalHintsUsed = 0;

        initProgressiveHints();
    }

    private void initProgressiveHints() {
        // LEVEL 1: THE STUDY
        progressiveHints.put("chest_code", new String[] {
            "Check the stopped wall clock and study painting.",
            "The hour hand points to 4 and the minute hand points towards 8.",
            "Combine the sequence: 4 - 8 - 1 - 5."
        });

        progressiveHints.put("painting_symbols", new String[] {
            "Look out the study window to see the night sky.",
            "The order follows celestial ascension: Moon rises first, then Sun.",
            "Order: MOON \u2192 SUN \u2192 STAR \u2192 HOURGLASS."
        });

        progressiveHints.put("bookshelf_cipher", new String[] {
            "The manuscript mentions shifting letters backward.",
            "Apply a Caesar shift of -1 to each character in 'E V F D S H'.",
            "The decrypted keyword is ESCAPE."
        });

        progressiveHints.put("desk_pattern", new String[] {
            "Inspect the alternating diamond tiles on the desk inlay.",
            "Notice the pattern: Filled, Hollow, Filled, Hollow.",
            "The missing symbol is the Hollow Diamond (\u25C7)."
        });

        progressiveHints.put("clock_combination", new String[] {
            "The roman numerals correspond to standard clock positions.",
            "Positions III, VI, and IX correspond to 3, 6, and 9.",
            "The combination is 3-6-9."
        });

        progressiveHints.put("globe_deduction", new String[] {
            "The coordinates point to prime meridian latitude markings.",
            "Zero meridian through Greenwich at 45 degrees North.",
            "The solution keyword is GREENWICH."
        });

        // LEVEL 2: THE SECRET LIBRARY
        progressiveHints.put("hidden_bookshelf", new String[] {
            "Look closely at the gold embossed diamond foil along the spines.",
            "The alternating sequence requires the next diamond shape.",
            "The missing symbol is the Filled Diamond (\u25C6)."
        });

        progressiveHints.put("manuscript", new String[] {
            "This is a classic Caesar cipher with a shift of -3.",
            "K \u2192 H, H \u2192 E, O \u2192 L, O \u2192 L, R \u2192 O.",
            "The translated greeting is HELLO."
        });

        progressiveHints.put("grandfather_clock", new String[] {
            "Look at the hour and minute indicators: XI and IX.",
            "XI is 11 o'clock and IX is 45 minutes.",
            "The time setting is 11:45."
        });

        progressiveHints.put("locked_cabinet", new String[] {
            "The astronomical charts display a 4-digit coordinate.",
            "Check the 4 constellation stars: 7, 3, 9, 2.",
            "Enter code: 7392."
        });

        progressiveHints.put("bookshelf", new String[] {
            "Chronological sequence according to Roman numeral tags.",
            "Arrange by order: I, IV, II, V, III.",
            "Enter sequence: 1-4-2-5-3."
        });

        // LEVEL 3: THE MYSTERY CHAMBER
        progressiveHints.put("pedestal", new String[] {
            "The planetary symbols represent celestial spheres in order of distance.",
            "Sun, Moon, Jupiter, Saturn, Uranus.",
            "Sequence: SUN MOON JUPITER SATURN URANUS."
        });

        progressiveHints.put("wall_mechanism", new String[] {
            "The outer and inner gear teeth engage at a resonant ratio.",
            "The ratio is inscribed near the teal core: 3 to 5 to 9.",
            "Enter gear ratio: 3:5:9."
        });

        progressiveHints.put("clock", new String[] {
            "The hands are locked on the third roman numeral.",
            "The ancient clock points to hour III.",
            "Answer is 3."
        });

        progressiveHints.put("floor_symbol", new String[] {
            "Look at the alternating geometric star vertices.",
            "The geometric shape completing the heptagram is the Hexagon.",
            "Enter: HEXAGON."
        });

        progressiveHints.put("scroll_table", new String[] {
            "The key word is engraved on the wax ribbon seal.",
            "The ancient Vigenere cipher key word is STONE.",
            "Enter: STONE."
        });

        progressiveHints.put("statue", new String[] {
            "Unscrew the golden pommel to read the engraved roman numerals.",
            "VII, IX, IV, I correspond to decimal numbers.",
            "Enter: 7941."
        });

        // LEVEL 4: THE FINAL ESCAPE
        progressiveHints.put("combination_lock", new String[] {
            "The sacred ratio is engraved on the archway lintel.",
            "Four concentric gold rings set to 3 - 7 - 9 - 1.",
            "Enter: 3791."
        });

        progressiveHints.put("pedestal_final", new String[] {
            "The ceremonial receptacle awaits the golden master artifact.",
            "Name the ancient relic described in the chamber scrolls.",
            "Enter: KEY OF OSIRIS."
        });

        progressiveHints.put("document_table", new String[] {
            "The architectural schematics reveal the sacred chamber name.",
            "The word reveals the final refuge.",
            "Enter: SANCTUARY."
        });

        progressiveHints.put("symbol_panels", new String[] {
            "Depress the relief plaques in mythological sequence.",
            "Celestial bodies followed by the ancient beast.",
            "Enter: SUN MOON STAR SERPENT."
        });

        progressiveHints.put("floor_pattern", new String[] {
            "The unbroken gold labyrinth ribbon leads to salvation.",
            "The Greek name of the ancient maze.",
            "Enter: LABYRINTHOS."
        });

        progressiveHints.put("wall_mechanisms", new String[] {
            "All differential gears must run in harmony.",
            "The keyword represents harmonic synchronization.",
            "Enter: SYNCHRONY."
        });
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
            hintText = "Look closely at the details and markings in this room.";
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
