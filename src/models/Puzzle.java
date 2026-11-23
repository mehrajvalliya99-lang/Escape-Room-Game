import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model representing a puzzle challenge in "THE LAST EXPERIMENT — A Detective Mystery Adventure".
 *
 * Supports the 6 specified puzzle formats:
 * - Number code puzzles
 * - Symbol sequence puzzles
 * - Cipher puzzles
 * - Pattern puzzles
 * - Combination puzzles
 * - Logic and deduction puzzles
 */
public class Puzzle {

    public enum PuzzleType {
        NUMBER_CODE(
            "Number Code",
            "Enter the 4-digit numeric code to disengage the security mechanism.",
            "Inspect scattered logs, newspaper dates, and terminal displays for numeric clues."
        ),
        SYMBOL_SEQUENCE(
            "Symbol Sequence",
            "Arrange the operational components or scientific sequence in correct order.",
            "Check research schematics and procedure manuals for the protocol sequence."
        ),
        CIPHER(
            "Cipher & Cryptogram",
            "Decode the encrypted research note to reveal the hidden passkey.",
            "Examine cipher shift keys and scientist annotations for decoding instructions."
        ),
        PATTERN(
            "Pattern Recognition",
            "Identify the missing geometric or schematic element in the experimental array.",
            "Observe circuit continuity and symmetrical alignments."
        ),
        COMBINATION(
            "Combination Lock",
            "Align the mechanical tumblers or gear ratios to match the calibration record.",
            "Cross-reference the scientist's laboratory notes and instrument dials."
        ),
        LOGIC_DEDUCTION(
            "Logic & Deduction",
            "Deduce the true parameters from the conflicting experiment logs.",
            "Eliminate documented containment failures to find the successful experiment."
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

    public static Puzzle createTornNewspaperPuzzle() {
        return new Puzzle(
            "torn_newspaper",
            "Torn Newspaper Puzzle",
            "Reconstruct the torn newspaper scrap to recover the 4-digit incident date.",
            PuzzleType.NUMBER_CODE,
            "HEADLINE: 'VANCE LABS SEALED \u2014 DIRECTIVE 1947'",
            "The 4-digit year of Dr. Vance's incident is clearly printed in the headline."
        );
    }

    public static Puzzle createChemicalSequencePuzzle() {
        return new Puzzle(
            "chemical_sequence",
            "Chemical Sequence Puzzle",
            "Align the laboratory reaction reagents in order of molecular stabilization.",
            PuzzleType.SYMBOL_SEQUENCE,
            "SOLVENT \u2192 CATALYST \u2192 REAGENT \u2192 ISOTOPE",
            "Synthesize the stabilization compound in protocol order."
        );
    }

    public static Puzzle createSuspectCipherPuzzle() {
        return new Puzzle(
            "suspect_cipher",
            "Suspect's Cipher",
            "Decrypt the suspect's encoded note to reveal where the key is concealed.",
            PuzzleType.CIPHER,
            "CIPHER: 'C P P L T I F M G' -> Shift -1",
            "Shift each letter 1 step backward in the alphabet: C->B, P->O, P->O, L->K..."
        );
    }

    public static Puzzle createMissingManuscriptPuzzle() {
        return new Puzzle(
            "missing_manuscript",
            "Missing Manuscript Puzzle",
            "Reassemble the torn manuscript fragments into the secret keyword.",
            PuzzleType.PATTERN,
            "FRAGMENTS: [ EXP ] + [ ERI ] + [ MENT ]",
            "Assemble the fragments sequentially: EXP + ERI + MENT."
        );
    }

    public static Puzzle createCircuitPuzzle() {
        return new Puzzle(
            "circuit_puzzle",
            "Electrical Circuit Puzzle",
            "Connect the circuit nodes in the required impedance order.",
            PuzzleType.COMBINATION,
            "CIRCUIT NODES: [ ALPHA ] - [ BETA ] - [ GAMMA ] - [ DELTA ]",
            "Follow Greek alphabetical sequence from Alpha to Delta."
        );
    }

    public static Puzzle createExperimentLogicPuzzle() {
        return new Puzzle(
            "experiment_logic",
            "Experiment Logic Puzzle",
            "Deduce which experiment achieved stable containment from the scientist's log.",
            PuzzleType.LOGIC_DEDUCTION,
            "LOGS: Exp 101 failed (Overheating). Exp 102 failed (Voltage drop). Exp 103 Succeeded.",
            "Enter the code of the only successful experiment: EXP 103."
        );
    }

    // Compatibility methods
    public static Puzzle createChestNumberPuzzle() {
        return createTornNewspaperPuzzle();
    }

    public static Puzzle createLogicDeductionPuzzle() {
        return new Puzzle(
            "study_globe",
            "Terrestrial Deduction",
            "Deduce the prime meridian longitude coordinates location.",
            PuzzleType.LOGIC_DEDUCTION,
            "COORDINATES: 0° Longitude through historic observatory.",
            "Enter prime meridian location: GREENWICH."
        );
    }

    public static Puzzle createSymbolSequencePuzzle() {
        return new Puzzle(
            "study_portrait",
            "Scholar Identification",
            "Identify the ancient Greek mathematician pictured in the study portrait.",
            PuzzleType.SYMBOL_SEQUENCE,
            "PORTRAIT: 'Eureka! - Syracuse'",
            "Enter scholar name: ARCHIMEDES."
        );
    }

    public static Puzzle createCombinationPuzzle() {
        return new Puzzle(
            "study_clock",
            "Clock Combination",
            "Enter the 3-digit combination matching the stopped mantel clock.",
            PuzzleType.COMBINATION,
            "HOUR: 4 | MINUTE: 25",
            "Enter code: 425."
        );
    }

    public static Puzzle createCipherPuzzle() {
        return createSuspectCipherPuzzle();
    }

    public static Puzzle createPatternPuzzle() {
        return new Puzzle(
            "desk_drawer",
            "Desk Drawer Lock",
            "Enter the 3-digit combination code matching the stopped clock.",
            PuzzleType.PATTERN,
            "425",
            "Check the stopped mantel clock."
        );
    }

    /**
     * Returns a list of sample puzzles representing all 6 supported puzzle types.
     */
    public static List<Puzzle> getAllSamplePuzzles() {
        List<Puzzle> list = new ArrayList<>();
        list.add(createTornNewspaperPuzzle());
        list.add(createChemicalSequencePuzzle());
        list.add(createSuspectCipherPuzzle());
        list.add(createMissingManuscriptPuzzle());
        list.add(createCircuitPuzzle());
        list.add(createExperimentLogicPuzzle());
        return Collections.unmodifiableList(list);
    }
}
