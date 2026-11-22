import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages all game puzzles across the 4 escape room levels.
 * Tracks puzzle definitions, correct answers (including synonyms and normalizations),
 * solved puzzle states, and puzzle completion by level.
 */
public class PuzzleManager {

    private final Map<String, Puzzle> puzzlesById;
    private final Map<String, Integer> puzzleLevels;
    private final Map<String, List<String>> acceptedAnswers;
    private final Set<String> solvedPuzzleIds;

    public PuzzleManager() {
        this.puzzlesById = new HashMap<>();
        this.puzzleLevels = new HashMap<>();
        this.acceptedAnswers = new HashMap<>();
        this.solvedPuzzleIds = new HashSet<>();

        initAllPuzzles();
    }

    /**
     * Initializes puzzles for all 4 levels with solutions and clues.
     */
    private void initAllPuzzles() {
        // =====================================================================
        // LEVEL 1: THE STUDY
        // =====================================================================
        registerPuzzle(
            Puzzle.createChestNumberPuzzle(),
            1,
            "4815",
            "4-8-1-5", "4 8 1 5"
        );

        registerPuzzle(
            Puzzle.createSymbolSequencePuzzle(),
            1,
            "MOON SUN STAR HOURGLASS",
            "MOON-SUN-STAR-HOURGLASS", "MOON SUN STAR", "LUNAR SOLAR STAR", "\u263E \u2609 \u2727 \u231B"
        );

        registerPuzzle(
            Puzzle.createCipherPuzzle(),
            1,
            "ESCAPE",
            "DUECRG", "SECRET", "DECODER"
        );

        registerPuzzle(
            Puzzle.createPatternPuzzle(),
            1,
            "\u25C7",
            "HOLLOW DIAMOND", "WHITE DIAMOND", "DIAMOND", "HOLLOW"
        );

        registerPuzzle(
            Puzzle.createCombinationPuzzle(),
            1,
            "369",
            "3-6-9", "3 6 9", "III-VI-IX", "3:15", "03:15"
        );

        registerPuzzle(
            Puzzle.createLogicDeductionPuzzle(),
            1,
            "GREENWICH",
            "45N", "45 NORTH", "LONDON", "45"
        );

        // =====================================================================
        // LEVEL 2: THE SECRET LIBRARY
        // =====================================================================
        registerPuzzle(
            new Puzzle(
                "hidden_bookshelf",
                "Secret Pattern Puzzle",
                "Align the concealed book spines to release the hidden latch.",
                Puzzle.PuzzleType.PATTERN,
                "[ \u25C6 ] [ \u25C7 ] [ \u25C6 ] [ \u25C7 ]",
                "Look for recurring diamond foil patterns along the spines."
            ),
            2,
            "\u25C6",
            "FILLED DIAMOND", "BLACK DIAMOND", "DIAMOND"
        );

        registerPuzzle(
            new Puzzle(
                "manuscript",
                "Cipher Puzzle",
                "Decipher the handwritten celestial glyphs and symbols.",
                Puzzle.PuzzleType.CIPHER,
                "CIPHER: 'K H O O R' -> Shift -3",
                "Each glyph corresponds to an astrological symbol."
            ),
            2,
            "HELLO",
            "HELLOS"
        );

        registerPuzzle(
            new Puzzle(
                "grandfather_clock",
                "Clock Puzzle",
                "Deduce the true time setting from the pendulum and gear alignment.",
                Puzzle.PuzzleType.COMBINATION,
                "HOUR: XI | MINUTE: IX",
                "The clock hands stopped precisely at 11:45."
            ),
            2,
            "11:45",
            "1145", "11 45", "XI-IX"
        );

        registerPuzzle(
            new Puzzle(
                "locked_cabinet",
                "Combination Puzzle",
                "Enter the numeric sequence to unlock the heavy brass cabinet padlock.",
                Puzzle.PuzzleType.NUMBER_CODE,
                "7 - 3 - 9 - 2",
                "The code relates to the astronomical charts."
            ),
            2,
            "7392",
            "7-3-9-2", "7 3 9 2"
        );

        registerPuzzle(
            new Puzzle(
                "bookshelf",
                "Book Arrangement Puzzle",
                "Arrange the ancient library volumes in chronological sequence.",
                Puzzle.PuzzleType.LOGIC_DEDUCTION,
                "I - IV - II - V - III",
                "Inspect the publication dates and author monograms."
            ),
            2,
            "1-4-2-5-3",
            "14253", "I-IV-II-V-III", "I IV II V III", "1 4 2 5 3"
        );

        // =====================================================================
        // LEVEL 3: THE MYSTERY CHAMBER
        // =====================================================================
        registerPuzzle(
            new Puzzle(
                "pedestal",
                "Symbol Alignment Puzzle",
                "Align the arcane celestial symbols into harmonious resonance.",
                Puzzle.PuzzleType.SYMBOL_SEQUENCE,
                "\u2609 \u263D \u2643 \u2644 \u2645",
                "Observe the engraved star chart on the scroll table."
            ),
            3,
            "SUN MOON JUPITER SATURN URANUS",
            "SUN-MOON-JUPITER-SATURN-URANUS", "\u2609 \u263D \u2643 \u2644 \u2645", "12345"
        );

        registerPuzzle(
            new Puzzle(
                "wall_mechanism",
                "Mechanical Puzzle",
                "Rotate the concentric celestial gear rings into mechanical resonance.",
                Puzzle.PuzzleType.COMBINATION,
                "GEAR RATIO: 3:5:9 - TEAL CORE RESONANCE",
                "Match the outer gear teeth with the inner brass markers."
            ),
            3,
            "3:5:9",
            "359", "3-5-9", "3 5 9", "TEAL"
        );

        registerPuzzle(
            new Puzzle(
                "clock",
                "Time Deduction Puzzle",
                "Deduce the true celestial hour from the clock hands and escapement.",
                Puzzle.PuzzleType.LOGIC_DEDUCTION,
                "LOCKED AT III - MOON AT MERIDIAN",
                "Cross-reference the clock hand angle with the historical journals."
            ),
            3,
            "3",
            "III", "3:00", "03:00", "MERIDIAN"
        );

        registerPuzzle(
            new Puzzle(
                "floor_symbol",
                "Pattern Puzzle",
                "Trace the correct geometric path through the engraved floor heptagram.",
                Puzzle.PuzzleType.PATTERN,
                "\u2B21 \u2B22 \u2B21 \u2B22 \u2B21",
                "Follow the golden star vertex intersections in ascending sequence."
            ),
            3,
            "HEXAGON",
            "FILLED HEXAGON", "\u2B22", "HEPTAGRAM"
        );

        registerPuzzle(
            new Puzzle(
                "scroll_table",
                "Cipher Puzzle",
                "Decipher the astrological treatise and faded star chart cipher.",
                Puzzle.PuzzleType.CIPHER,
                "CIPHER: 'A L T A R' -> VIGENERE KEY: 'STONE'",
                "The ribbon seals hint at the key cipher alphabet."
            ),
            3,
            "STONE",
            "ALTAR", "TEMPLE", "SANCTUARY"
        );

        registerPuzzle(
            new Puzzle(
                "statue",
                "Hidden Clue",
                "Examine the guardian statue's ceremonial staff for concealed markings.",
                Puzzle.PuzzleType.NUMBER_CODE,
                "RUNES: VII - IX - IV - I",
                "The golden staff pommel unscrews to reveal an engraved number code."
            ),
            3,
            "7941",
            "7-9-4-1", "7 9 4 1", "VII-IX-IV-I"
        );

        // =====================================================================
        // LEVEL 4: THE FINAL ESCAPE
        // =====================================================================
        registerPuzzle(
            new Puzzle(
                "combination_lock",
                "Final Combination Puzzle",
                "Align the concentric gold and iron dials to unlock the grand escape door.",
                Puzzle.PuzzleType.COMBINATION,
                "3 - 7 - 9 - 1",
                "The dials align into the sacred ratio found on the chamber walls."
            ),
            4,
            "3791",
            "3-7-9-1", "3 7 9 1"
        );

        registerPuzzle(
            new Puzzle(
                "pedestal_final",
                "Multi-Part Puzzle",
                "The ceremonial receptacle demands the ancient triangular master key and elemental stones.",
                Puzzle.PuzzleType.PATTERN,
                "KEY OF OSIRIS",
                "Insert the golden master key gathered from the previous chambers."
            ),
            4,
            "KEY OF OSIRIS",
            "OSIRIS", "FINAL KEY", "GOLDEN KEY", "MASTER KEY"
        );

        registerPuzzle(
            new Puzzle(
                "document_table",
                "Final Clue Puzzle",
                "Decipher the faded notes describing the counterweight release sequence.",
                Puzzle.PuzzleType.CIPHER,
                "SANCTUARY",
                "The parchment reveals an architectural schematic of the final locking rods."
            ),
            4,
            "SANCTUARY",
            "SANCTUM"
        );

        registerPuzzle(
            new Puzzle(
                "symbol_panels",
                "Symbol Sequence Puzzle",
                "Depress the four relief plaques in astronomical sequence: Sun, Moon, Star, Serpent.",
                Puzzle.PuzzleType.SYMBOL_SEQUENCE,
                "SUN - MOON - STAR - SERPENT",
                "Each plaque clicks inward, releasing a heavy brass locking counterweight."
            ),
            4,
            "SUN MOON STAR SERPENT",
            "SUN-MOON-STAR-SERPENT", "SUN, MOON, STAR, SERPENT"
        );

        registerPuzzle(
            new Puzzle(
                "floor_pattern",
                "Pattern Puzzle",
                "Trace the unbroken gold ribbon from the perimeter into the golden pedestal.",
                Puzzle.PuzzleType.PATTERN,
                "LABYRINTHOS",
                "The inlaid celestial runes illuminate when traced in correct order."
            ),
            4,
            "LABYRINTHOS",
            "LABYRINTH"
        );

        registerPuzzle(
            new Puzzle(
                "wall_mechanisms",
                "Mechanism Puzzle",
                "Engage the differential brass gears to synchronize with the golden door bolts.",
                Puzzle.PuzzleType.COMBINATION,
                "SYNCHRONY",
                "The gear teeth lock into place with a resounding hydraulic click."
            ),
            4,
            "SYNCHRONY",
            "SYNCHRONIZE", "SYNCHRONOUS"
        );
    }

    /**
     * Registers a puzzle with its level and accepted answers.
     */
    public void registerPuzzle(Puzzle puzzle, int level, String primaryAnswer, String... alternates) {
        if (puzzle == null) return;
        String id = puzzle.getId();
        puzzlesById.put(id, puzzle);
        puzzleLevels.put(id, level);

        List<String> list = new ArrayList<>();
        list.add(normalize(primaryAnswer));
        if (alternates != null) {
            for (String alt : alternates) {
                if (alt != null) {
                    list.add(normalize(alt));
                }
            }
        }
        acceptedAnswers.put(id, list);
    }

    /**
     * Normalizes an answer string: strips spaces, punctuation, hyphens, and converts to uppercase.
     */
    public static String normalize(String input) {
        if (input == null) return "";
        return input.trim()
            .toUpperCase()
            .replaceAll("[-_:,.]", "")
            .replaceAll("\\s+", "");
    }

    /**
     * Retrieves a puzzle by ID.
     */
    public Puzzle getPuzzle(String id) {
        return puzzlesById.get(id);
    }

    /**
     * Checks if a puzzle has already been solved.
     */
    public boolean isSolved(String id) {
        return id != null && solvedPuzzleIds.contains(id);
    }

    /**
     * Marks a puzzle as solved.
     */
    public void markSolved(String id) {
        if (id != null) {
            solvedPuzzleIds.add(id);
        }
    }

    /**
     * Validates player input for a given puzzle ID.
     */
    public boolean validateAnswer(String id, String playerInput) {
        if (id == null || playerInput == null) return false;
        if (isSolved(id)) return true;

        List<String> validList = acceptedAnswers.get(id);
        if (validList == null) return false;

        String normInput = normalize(playerInput);
        if (normInput.isEmpty()) return false;

        for (String valid : validList) {
            if (valid.equals(normInput)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a user-friendly validation message.
     */
    public String getValidationFeedback(String id, String playerInput) {
        if (playerInput == null || playerInput.trim().isEmpty()) {
            return "Please enter an answer before submitting.";
        }
        if (isSolved(id)) {
            return "This puzzle has already been solved!";
        }
        if (validateAnswer(id, playerInput)) {
            return "Correct! The mechanism clicks and unlocks.";
        } else {
            return "Incorrect answer. Inspect clues or use a hint.";
        }
    }

    /**
     * Returns how many puzzles have been solved in a given level.
     */
    public int getSolvedCountForLevel(int level) {
        int count = 0;
        for (String id : solvedPuzzleIds) {
            Integer lvl = puzzleLevels.get(id);
            if (lvl != null && lvl == level) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns total registered puzzles for a given level.
     */
    public int getTotalCountForLevel(int level) {
        int count = 0;
        for (Map.Entry<String, Integer> entry : puzzleLevels.entrySet()) {
            if (entry.getValue() == level) {
                count++;
            }
        }
        return count;
    }

    /**
     * Resets puzzle solved status for a new game session.
     */
    public void reset() {
        solvedPuzzleIds.clear();
    }

    public Set<String> getSolvedPuzzleIds() {
        return Collections.unmodifiableSet(solvedPuzzleIds);
    }
}
