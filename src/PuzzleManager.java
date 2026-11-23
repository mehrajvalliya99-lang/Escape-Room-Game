import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages all game puzzles across the 4 escape room levels:
 * LEVEL 1: The Detective's Study
 * LEVEL 2: The Forgotten Library
 * LEVEL 3: The Abandoned Laboratory
 * LEVEL 4: The Ancient Vault
 *
 * Story theme: THE LAST EXPERIMENT — A Detective Mystery Adventure.
 * Handles puzzle registration, answer normalization, validation,
 * double-solve protection, and progress tracking.
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
     * Initializes all puzzles across the four storyline levels.
     */
    private void initAllPuzzles() {
        // =====================================================================
        // LEVEL 1: THE DETECTIVE'S STUDY
        // =====================================================================
        // 1. Torn Newspaper Puzzle (discovers incident date 1947)
        registerPuzzle(
            new Puzzle(
                "torn_newspaper",
                "Torn Newspaper Puzzle",
                "Reconstruct the torn newspaper scrap to recover the 4-digit incident date.",
                Puzzle.PuzzleType.NUMBER_CODE,
                "HEADLINE: 'VANCE LABS SEALED \u2014 DIRECTIVE 1947'",
                "The 4-digit year of Dr. Vance's incident is printed in the headline."
            ),
            1,
            "1947",
            "1-9-4-7", "1 9 4 7", "4815", "4-8-1-5" // compatibility with chest_code
        );
        // Register chest_code alias pointing to same puzzle
        registerPuzzleAlias("chest_code", "torn_newspaper");

        // 2. Hidden Desk Drawer Puzzle (formula from wall clock / notes: 425)
        registerPuzzle(
            new Puzzle(
                "desk_drawer",
                "Hidden Desk Drawer",
                "Calculate the lock code from Dr. Vance's desk formula.",
                Puzzle.PuzzleType.NUMBER_CODE,
                "FORMULA: HOUR (4) \u00D7 100 + MINUTE (25) = [ ? ]",
                "Cross-reference the stopped wall clock at 4:25 with the drawer formula."
            ),
            1,
            "425",
            "0425", "4 2 5", "\u25C7", "HOLLOW DIAMOND", "DIAMOND"
        );
        registerPuzzleAlias("desk_pattern", "desk_drawer");

        // 3. Suspect's Cipher (reverse/shift cipher revealing BOOKSHELF)
        registerPuzzle(
            new Puzzle(
                "suspect_cipher",
                "Suspect's Cipher",
                "Decrypt the suspect's encoded note to reveal where the key is concealed.",
                Puzzle.PuzzleType.CIPHER,
                "CIPHER: 'C P P L T I F M G' -> Shift -1",
                "Shift each letter 1 step backward in the alphabet: C->B, P->O, P->O, L->K..."
            ),
            1,
            "BOOKSHELF",
            "BOOK SHELF", "ESCAPE", "SECRET", "DUECRG"
        );
        registerPuzzleAlias("bookshelf_cipher", "suspect_cipher");

        // 4. Study Wall Clock (combination puzzle: 4:25)
        registerPuzzle(
            new Puzzle(
                "study_clock",
                "Antique Wall Clock",
                "Align the clock tumblers to the hour when the facility power was severed.",
                Puzzle.PuzzleType.COMBINATION,
                "DIAL SETTING: [ 04 ] - [ 25 ]",
                "The clock hands are stopped firmly at 4:25."
            ),
            1,
            "425",
            "0425", "4:25", "369", "3-6-9"
        );
        registerPuzzleAlias("clock_combination", "study_clock");

        // 5. Scientist's Portrait (Archimedes plaque)
        registerPuzzle(
            new Puzzle(
                "study_portrait",
                "Scientist's Portrait",
                "Enter the historical thinker named on the brass dedication plaque.",
                Puzzle.PuzzleType.LOGIC_DEDUCTION,
                "PLAQUE: 'DEDICATED TO ARCHIMEDES \u2014 FATHER OF MECHANICS'",
                "The dedication honors Archimedes."
            ),
            1,
            "ARCHIMEDES",
            "MOON SUN STAR HOURGLASS", "MOON SUN STAR"
        );
        registerPuzzleAlias("painting_symbols", "study_portrait");

        // 6. Expedition Globe (Greenwich coordinates)
        registerPuzzle(
            new Puzzle(
                "study_globe",
                "Expedition Globe",
                "Determine the excavation baseline meridian from the desk globe.",
                Puzzle.PuzzleType.LOGIC_DEDUCTION,
                "COORDINATES: Prime meridian intersection at 45\u00B0 North.",
                "The reference meridian passes through Greenwich."
            ),
            1,
            "GREENWICH",
            "45N", "45 NORTH", "LONDON", "45"
        );
        registerPuzzleAlias("globe_deduction", "study_globe");

        // =====================================================================
        // LEVEL 2: THE FORGOTTEN LIBRARY
        // =====================================================================
        // 1. Book Arrangement Puzzle (chronological publication: 12345)
        registerPuzzle(
            new Puzzle(
                "book_arrangement",
                "Book Arrangement Puzzle",
                "Arrange the ancient library volumes in chronological sequence.",
                Puzzle.PuzzleType.LOGIC_DEDUCTION,
                "VOLUMES: I (1905), II (1912), III (1920), IV (1935), V (1942)",
                "Arrange volumes from earliest to latest: 1, 2, 3, 4, 5."
            ),
            2,
            "12345",
            "1-2-3-4-5", "1 2 3 4 5", "1-4-2-5-3", "14253"
        );
        registerPuzzleAlias("bookshelf", "book_arrangement");

        // 2. Caesar Cipher Puzzle (manuscript: ARTIFACT)
        registerPuzzle(
            new Puzzle(
                "caesar_cipher",
                "Caesar Cipher Puzzle",
                "Decode the encrypted manuscript fragment describing the excavation relic.",
                Puzzle.PuzzleType.CIPHER,
                "CIPHER: 'D U W L I D F W' -> Shift -3",
                "Shift each letter 3 steps backward in the alphabet: D->A, U->R, W->T..."
            ),
            2,
            "ARTIFACT",
            "HELLO", "HELLOS"
        );
        registerPuzzleAlias("manuscript", "caesar_cipher");

        // 3. Missing Manuscript Puzzle (EXP + ERI + MENT)
        registerPuzzle(
            new Puzzle(
                "missing_manuscript",
                "Missing Manuscript Puzzle",
                "Reconstruct the torn research notes into the classified operation keyword.",
                Puzzle.PuzzleType.PATTERN,
                "FRAGMENTS: [ EXP ] + [ ERI ] + [ MENT ]",
                "Concatenate the three paper fragments sequentially."
            ),
            2,
            "EXPERIMENT",
            "THE EXPERIMENT"
        );

        // 4. Hidden Shelf Puzzle (locked cabinet protocol: 4921)
        registerPuzzle(
            new Puzzle(
                "hidden_shelf",
                "Hidden Shelf Puzzle",
                "Enter the laboratory protocol code to disengage the concealed shelf latch.",
                Puzzle.PuzzleType.NUMBER_CODE,
                "SECURITY LATCH: 4 - 9 - 2 - 1",
                "Enter the four security digits displayed on the reading table."
            ),
            2,
            "4921",
            "4-9-2-1", "4 9 2 1", "7392", "7-3-9-2"
        );
        registerPuzzleAlias("locked_cabinet", "hidden_shelf");

        // 5. Grandfather Clock (11:45)
        registerPuzzle(
            new Puzzle(
                "grandfather_clock",
                "Library Grandfather Clock",
                "Deduce the true time setting from the pendulum and gear alignment.",
                Puzzle.PuzzleType.COMBINATION,
                "HOUR: XI | MINUTE: IX",
                "The clock hands stopped precisely at 11:45."
            ),
            2,
            "11:45",
            "1145", "11 45", "XI-IX"
        );

        // 6. Hidden Bookshelf Section (Diamond pattern)
        registerPuzzle(
            new Puzzle(
                "hidden_bookshelf",
                "Secret Pattern Latch",
                "Align the concealed book spines to release the hidden latch.",
                Puzzle.PuzzleType.PATTERN,
                "[ \u25C6 ] [ \u25C7 ] [ \u25C6 ] [ \u25C7 ]",
                "Look for recurring diamond foil patterns along the spines."
            ),
            2,
            "\u25C6",
            "FILLED DIAMOND", "BLACK DIAMOND", "DIAMOND", "\u25C7"
        );

        // =====================================================================
        // LEVEL 3: THE ABANDONED LABORATORY
        // =====================================================================
        // 1. Electrical Circuit Puzzle (connect nodes ALPHA BETA GAMMA DELTA)
        registerPuzzle(
            new Puzzle(
                "circuit_puzzle",
                "Electrical Circuit Puzzle",
                "Connect the circuit nodes in the required impedance order.",
                Puzzle.PuzzleType.COMBINATION,
                "CIRCUIT NODES: [ ALPHA ] - [ BETA ] - [ GAMMA ] - [ DELTA ]",
                "Follow Greek alphabetical sequence: Alpha, Beta, Gamma, Delta."
            ),
            3,
            "ALPHA BETA GAMMA DELTA",
            "ALPHA-BETA-GAMMA-DELTA", "ABCD", "A B C D", "3:5:9", "359", "3-5-9", "TEAL"
        );
        registerPuzzleAlias("wall_mechanism", "circuit_puzzle");

        // 2. Chemical Sequence Puzzle (SOLVENT CATALYST REAGENT ISOTOPE)
        registerPuzzle(
            new Puzzle(
                "chemical_sequence",
                "Chemical Sequence Puzzle",
                "Align the laboratory reaction reagents in order of molecular stabilization.",
                Puzzle.PuzzleType.SYMBOL_SEQUENCE,
                "STAGES: SOLVENT \u2192 CATALYST \u2192 REAGENT \u2192 ISOTOPE",
                "Follow the reaction protocol from base solvent to radioactive isotope."
            ),
            3,
            "SOLVENT CATALYST REAGENT ISOTOPE",
            "SOLVENT-CATALYST-REAGENT-ISOTOPE", "SUN MOON JUPITER SATURN URANUS", "12345"
        );
        registerPuzzleAlias("pedestal", "chemical_sequence");

        // 3. Experiment Logic Puzzle (Exp 103 succeeded)
        registerPuzzle(
            new Puzzle(
                "experiment_logic",
                "Experiment Logic Puzzle",
                "Deduce which experiment achieved stable containment from the scientist's log.",
                Puzzle.PuzzleType.LOGIC_DEDUCTION,
                "LOGS: Exp 101 failed (Thermal runaway). Exp 102 failed (Field collapse). Exp 103 Succeeded.",
                "Enter the code of the only successful containment test."
            ),
            3,
            "EXP 103",
            "103", "EXP103", "3", "III"
        );
        registerPuzzleAlias("clock", "experiment_logic");

        // 4. Security Terminal Puzzle (statue / override: 8462)
        registerPuzzle(
            new Puzzle(
                "security_terminal",
                "Security Terminal Puzzle",
                "Enter the restricted terminal override authorization code.",
                Puzzle.PuzzleType.NUMBER_CODE,
                "RESTRICTED TERMINAL: 8 - 4 - 6 - 2",
                "Enter the 4-digit terminal override code."
            ),
            3,
            "8462",
            "8-4-6-2", "8 4 6 2", "7941", "7-9-4-1"
        );
        registerPuzzleAlias("statue", "security_terminal");

        // 5. Reactor Coolant Cipher
        registerPuzzle(
            new Puzzle(
                "reactor_cipher",
                "Reactor Coolant Cipher",
                "Decode the chemical stabilizer keyword on the scroll table.",
                Puzzle.PuzzleType.CIPHER,
                "CIPHER: 'R E A C T O R'",
                "The decoded keyword is REACTOR."
            ),
            3,
            "REACTOR",
            "STONE", "ALTAR", "TEMPLE"
        );
        registerPuzzleAlias("scroll_table", "reactor_cipher");

        // 6. Laser Diffraction Grid (Hexagon)
        registerPuzzle(
            new Puzzle(
                "laser_grid",
                "Laser Reflection Grid",
                "Identify the polygon required to focus the laser diffraction beam.",
                Puzzle.PuzzleType.PATTERN,
                "DIFFRACTION CORE: 6-SIDED POLYGON [ \u2B22 ]",
                "A regular six-sided geometric polygon is a Hexagon."
            ),
            3,
            "HEXAGON",
            "FILLED HEXAGON", "\u2B22"
        );
        registerPuzzleAlias("floor_symbol", "laser_grid");

        // =====================================================================
        // LEVEL 4: THE ANCIENT VAULT
        // =====================================================================
        // 1. Multi-Stage Combination Lock (5284)
        registerPuzzle(
            new Puzzle(
                "vault_combination",
                "Multi-Stage Combination Lock",
                "Align the 4 rotating vault tumbler dials to the master lockdown override code.",
                Puzzle.PuzzleType.COMBINATION,
                "VAULT COMBINATION: 5 - 2 - 8 - 4",
                "The 4 tumbler dials are calibrated to 5, 2, 8, 4."
            ),
            4,
            "5284",
            "5-2-8-4", "5 2 8 4", "3791", "3-7-9-1"
        );
        registerPuzzleAlias("combination_lock", "vault_combination");

        // 2. Ancient Artifact Arrangement (Relic Core Matrix Artifact)
        registerPuzzle(
            new Puzzle(
                "artifact_arrangement",
                "Ancient Artifact Arrangement",
                "Place the artifacts on the central pedestal in chronological excavation order.",
                Puzzle.PuzzleType.PATTERN,
                "ALIGNMENT: RELIC \u2192 CORE \u2192 MATRIX \u2192 ARTIFACT",
                "Arrange the artifacts: Relic, Core, Matrix, Artifact."
            ),
            4,
            "RELIC CORE MATRIX ARTIFACT",
            "RELIC-CORE-MATRIX-ARTIFACT", "ARTIFACT MATRIX", "KEY OF OSIRIS", "OSIRIS", "ANCIENT ARTIFACT", "FINAL KEY", "VAULT KEY"
        );
        registerPuzzleAlias("pedestal_final", "artifact_arrangement");

        // 3. Final Message Decoding (EXPERIMENT COMPLETE)
        registerPuzzle(
            new Puzzle(
                "final_message",
                "Final Message Decoding",
                "Decode Dr. Vance's final recording to discover the shutdown override phrase.",
                Puzzle.PuzzleType.CIPHER,
                "AUDIO TRANSCRIPTION: 'E X P E R I M E N T   C O M P L E T E'",
                "The override phrase signals that the experiment is complete."
            ),
            4,
            "EXPERIMENT COMPLETE",
            "EXPERIMENTCOMPLETE", "SANCTUARY", "OVERRIDE"
        );
        registerPuzzleAlias("document_table", "final_message");

        // 4. Containment Release Sequence (POWER COOLING VACUUM SHIELD)
        registerPuzzle(
            new Puzzle(
                "containment_relays",
                "Containment Release Sequence",
                "Depress the four containment release relays in safe stabilization order.",
                Puzzle.PuzzleType.SYMBOL_SEQUENCE,
                "STABILIZATION RELAYS: POWER \u2192 COOLING \u2192 VACUUM \u2192 SHIELD",
                "Safely release containment: Power, Cooling, Vacuum, Shield."
            ),
            4,
            "POWER COOLING VACUUM SHIELD",
            "POWER-COOLING-VACUUM-SHIELD", "SUN MOON STAR SERPENT", "SUN-MOON-STAR-SERPENT"
        );
        registerPuzzleAlias("symbol_panels", "containment_relays");

        // 5. Superconductor Grid
        registerPuzzle(
            new Puzzle(
                "superconductor_grid",
                "Superconductor Labyrinth",
                "Trace the unbroken superconducting magnetic track across the vault floor.",
                Puzzle.PuzzleType.PATTERN,
                "TRACK PATTERN: LABYRINTH",
                "The magnetic conduit forms an intricate Labyrinth."
            ),
            4,
            "LABYRINTH",
            "LABYRINTHOS"
        );
        registerPuzzleAlias("floor_pattern", "superconductor_grid");

        // 6. Hydraulic Synchronization
        registerPuzzle(
            new Puzzle(
                "hydraulic_valves",
                "Hydraulic Synchronization",
                "Engage the hydraulic pressure valves to balance the vault atmosphere.",
                Puzzle.PuzzleType.COMBINATION,
                "SYNCHRONIZATION CODE: SYNCHRONY",
                "The valve protocol keyword is SYNCHRONY."
            ),
            4,
            "SYNCHRONY",
            "SYNCHRONIZE", "SYNCHRONOUS"
        );
        registerPuzzleAlias("wall_mechanisms", "hydraulic_valves");
    }

    /**
     * Registers a puzzle and stores its primary and alternate answers.
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
     * Registers an alias ID pointing to an existing target puzzle.
     */
    public void registerPuzzleAlias(String aliasId, String targetId) {
        if (aliasId == null || targetId == null) return;
        Puzzle target = puzzlesById.get(targetId);
        if (target != null) {
            puzzlesById.put(aliasId, target);
            puzzleLevels.put(aliasId, puzzleLevels.get(targetId));
            acceptedAnswers.put(aliasId, acceptedAnswers.get(targetId));
        }
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

    public Puzzle getPuzzle(String id) {
        return puzzlesById.get(id);
    }

    public boolean isSolved(String id) {
        return id != null && solvedPuzzleIds.contains(id);
    }

    public void markSolved(String id) {
        if (id != null) {
            solvedPuzzleIds.add(id);
            // If this is an alias or target, mark both
            for (Map.Entry<String, Puzzle> entry : puzzlesById.entrySet()) {
                if (entry.getValue().getId().equals(id) || entry.getKey().equals(id)) {
                    solvedPuzzleIds.add(entry.getKey());
                    solvedPuzzleIds.add(entry.getValue().getId());
                }
            }
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
            return "Correct! The security mechanism unlocks.";
        } else {
            return "Incorrect answer. Inspect clues or request a hint.";
        }
    }

    public int getSolvedCountForLevel(int level) {
        Set<String> uniqueSolved = new HashSet<>();
        for (String id : solvedPuzzleIds) {
            Puzzle p = puzzlesById.get(id);
            if (p != null) {
                Integer lvl = puzzleLevels.get(p.getId());
                if (lvl != null && lvl == level) {
                    uniqueSolved.add(p.getId());
                }
            }
        }
        return uniqueSolved.size();
    }

    public int getTotalCountForLevel(int level) {
        Set<String> uniquePuzzles = new HashSet<>();
        for (Map.Entry<String, Puzzle> entry : puzzlesById.entrySet()) {
            Integer lvl = puzzleLevels.get(entry.getKey());
            if (lvl != null && lvl == level) {
                uniquePuzzles.add(entry.getValue().getId());
            }
        }
        return uniquePuzzles.size();
    }

    public int getTotalSolvedCount() {
        Set<String> uniqueSolved = new HashSet<>();
        for (String id : solvedPuzzleIds) {
            Puzzle p = puzzlesById.get(id);
            if (p != null) {
                uniqueSolved.add(p.getId());
            }
        }
        return uniqueSolved.size();
    }

    public int getSolvedCount() {
        return getTotalSolvedCount();
    }

    public void reset() {
        solvedPuzzleIds.clear();
    }

    public Set<String> getSolvedPuzzleIds() {
        return Collections.unmodifiableSet(solvedPuzzleIds);
    }
}
