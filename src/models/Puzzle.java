import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model representing a puzzle challenge in "Escape the Room – Mystery Puzzle Adventure".
 *
 * Designed as a clean data layer that can be dynamically populated and extended
 * by Member 2 via PuzzleManager and HintManager.
 *
 * Supports the 6 specified puzzle formats:
 * - Number code puzzles
 * - Symbol sequence puzzles
 * - Cipher puzzles
 * - Pattern puzzles
 * - Combination puzzles
 * - Logic and deduction puzzles
 *
 * // TODO: Member 2 - connect PuzzleManager
 * // TODO: Member 2 - connect HintManager
 * // TODO: Member 2 - implement answer validation
 */
public class Puzzle {

    public enum PuzzleType {
        NUMBER_CODE(
            "Number Code",
            "Enter the 4-digit code to disengage the antique iron lock.",
            "Inspect the stopped wall clock and the study painting for numerical clues."
        ),
        SYMBOL_SEQUENCE(
            "Symbol Sequence",
            "Decipher the celestial glyph sequence to align the constellation lock.",
            "Observe the lunar phase visible through the study window."
        ),
        CIPHER(
            "Cipher & Cryptogram",
            "Decode the encrypted parchment scrap to discover the secret keyword.",
            "Shift each letter backward according to the roman numeral on the clock."
        ),
        PATTERN(
            "Pattern Recognition",
            "Identify the missing geometric emblem in the study desk mosaic.",
            "Look for symmetry in the diagonal tiles."
        ),
        COMBINATION(
            "Combination Lock",
            "Align the rotating bronze tumbler rings to the secret coordinate.",
            "The globe on the study desk bears matching latitude markings."
        ),
        LOGIC_DEDUCTION(
            "Logic & Deduction",
            "Deduce the true order of events from the mansion owner's notes.",
            "Eliminate conflicting timestamps found in the journal."
        );

        private final String displayName;
        private final String defaultDescription;
        private final String defaultHint;

        PuzzleType(String displayName, String defaultDescription, String defaultHint) {
            this.displayName = displayName;
            this.defaultDescription = defaultDescription;
            this.defaultHint = defaultHint;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDefaultDescription() {
            return defaultDescription;
        }

        public String getDefaultHint() {
            return defaultHint;
        }
    }

    private final String id;
    private final String title;
    private final String description;
    private final PuzzleType type;
    private final String visualClue;
    private final String defaultHint;

    public Puzzle(String id, String title, String description, PuzzleType type, String visualClue, String defaultHint) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type != null ? type : PuzzleType.NUMBER_CODE;
        this.visualClue = visualClue;
        this.defaultHint = defaultHint;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public PuzzleType getType() {
        return type;
    }

    public String getVisualClue() {
        return visualClue;
    }

    public String getDefaultHint() {
        return defaultHint;
    }

    // =========================================================================
    // SAMPLE PUZZLES FOR THE 6 SUPPORTED FORMATS
    // =========================================================================

    public static Puzzle createChestNumberPuzzle() {
        return new Puzzle(
            "chest_code",
            "The Iron Chest Keypad",
            "Enter the 4-digit numeric code to disengage the heavy iron lock.",
            PuzzleType.NUMBER_CODE,
            "4 - 8 - 1 - 5",
            "Check the stopped grandfather clock: hour 4, minute 8."
        );
    }

    public static Puzzle createSymbolSequencePuzzle() {
        return new Puzzle(
            "painting_symbols",
            "The Celestial Glyphs",
            "Arrange the sacred celestial symbols in the correct astronomical order.",
            PuzzleType.SYMBOL_SEQUENCE,
            "\u263E \u2192 \u2609 \u2192 \u2727 \u2192 \u231B",
            "Moon rises first, followed by the sun, guided by the northern star."
        );
    }

    public static Puzzle createCipherPuzzle() {
        return new Puzzle(
            "bookshelf_cipher",
            "The Scholar's Cryptogram",
            "Decrypt the ancient manuscript fragment found between dusty volumes.",
            PuzzleType.CIPHER,
            "CIPHER: 'E V F D S H' -> Key: +1",
            "Reverse the Caesar shift by 1 position backward in the alphabet."
        );
    }

    public static Puzzle createPatternPuzzle() {
        return new Puzzle(
            "desk_pattern",
            "The Mosaic Tiles",
            "Determine the missing emblem required to complete the study desk inlay.",
            PuzzleType.PATTERN,
            "Pattern: [ \u25C6 ] [ \u25C7 ] [ \u25C6 ] [ ? ]",
            "Alternate filled and hollow diamond formations."
        );
    }

    public static Puzzle createCombinationPuzzle() {
        return new Puzzle(
            "clock_combination",
            "The Pendulum Tumbler",
            "Align the three rotating brass tumblers to the grandfather clock settings.",
            PuzzleType.COMBINATION,
            "DIAL SETTINGS: [ III ] - [ VI ] - [ IX ]",
            "The clock stopped precisely at a quarter past the third hour."
        );
    }

    public static Puzzle createLogicDeductionPuzzle() {
        return new Puzzle(
            "globe_deduction",
            "The Explorer's Deduction",
            "Deduce which hidden compartment holds the obsidian master key.",
            PuzzleType.LOGIC_DEDUCTION,
            "CLUES: 1. Not in water. 2. At 45\u00B0 North. 3. Near Greenwich meridian.",
            "Cross-reference the latitude markings painted on the desk globe."
        );
    }

    /**
     * Returns a list of sample puzzles representing all 6 supported puzzle types.
     */
    public static List<Puzzle> getAllSamplePuzzles() {
        List<Puzzle> list = new ArrayList<>();
        list.add(createChestNumberPuzzle());
        list.add(createSymbolSequencePuzzle());
        list.add(createCipherPuzzle());
        list.add(createPatternPuzzle());
        list.add(createCombinationPuzzle());
        list.add(createLogicDeductionPuzzle());
        return Collections.unmodifiableList(list);
    }
}
