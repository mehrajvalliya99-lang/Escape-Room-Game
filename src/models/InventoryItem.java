import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model representing an item in the player's inventory.
 * Contains identifier, display name, description, and item type
 * for procedural 2D illustration rendering.
 *
 * Fully integrated for: THE LAST EXPERIMENT — A Detective Mystery Adventure.
 */
public class InventoryItem {

    public enum ItemType {
        NEWSPAPER_FRAGMENT,
        RESEARCH_NOTE,
        MAGNIFYING_GLASS,
        DRAWER_KEY,
        CIPHER_MANUSCRIPT,
        LIBRARY_KEY,
        ANCIENT_MANUSCRIPT,
        LAB_CARD,
        CIRCUIT_COMPONENT,
        EXPERIMENT_REPORT,
        ANCIENT_ARTIFACT,
        VAULT_KEY,
        FINAL_RECORDING,

        // Backward compatibility mappings
        GOLDEN_KEY,
        OLD_PHOTOGRAPH,
        TORN_PAPER,
        MYSTERIOUS_BOOK,
        CANDLE,
        CLOCK_FRAGMENT,
        SECRET_NOTE,
        FINAL_KEY
    }

    private final String id;
    private final String name;
    private final String description;
    private final ItemType type;

    public InventoryItem(String id, String name, String description, ItemType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ItemType getType() {
        return type;
    }

    // =========================================================================
    // FACTORY METHODS FOR DETECTIVE MYSTERY ITEMS
    // =========================================================================

    public static InventoryItem createNewspaperFragment() {
        return new InventoryItem(
            "newspaper_fragment",
            "Newspaper Fragment",
            "A scorched newspaper clipping from 1947 reporting Dr. Vance's classified incident.",
            ItemType.NEWSPAPER_FRAGMENT
        );
    }

    public static InventoryItem createResearchNote() {
        return new InventoryItem(
            "research_note",
            "Torn Research Note",
            "A hastily scribbled formula from Dr. Vance detailing Project Aegis coordinates.",
            ItemType.RESEARCH_NOTE
        );
    }

    public static InventoryItem createMagnifyingGlass() {
        return new InventoryItem(
            "magnifying_glass",
            "Magnifying Glass",
            "An investigator's brass magnifying lens used to examine faint engravings and micro-scripts.",
            ItemType.MAGNIFYING_GLASS
        );
    }

    public static InventoryItem createDrawerKey() {
        return new InventoryItem(
            "drawer_key",
            "Hidden Drawer Key",
            "A small silver key recovered from the iron chest, cut for the study desk's locked drawer.",
            ItemType.DRAWER_KEY
        );
    }

    public static InventoryItem createCipherManuscript() {
        return new InventoryItem(
            "cipher_manuscript",
            "Cipher Manuscript",
            "An encoded cipher parchment revealing where Dr. Vance concealed the library passkey.",
            ItemType.CIPHER_MANUSCRIPT
        );
    }

    public static InventoryItem createLibraryKey() {
        return new InventoryItem(
            "library_key",
            "Library Access Key",
            "A heavy brass key stamped 'SECTOR 2'. Unlocks the Forgotten Library entrance.",
            ItemType.LIBRARY_KEY
        );
    }

    public static InventoryItem createAncientManuscript() {
        return new InventoryItem(
            "ancient_manuscript",
            "Ancient Manuscript",
            "A leather-bound treatise documenting an otherworldly artifact discovered in the excavation.",
            ItemType.ANCIENT_MANUSCRIPT
        );
    }

    public static InventoryItem createLabCard() {
        return new InventoryItem(
            "lab_card",
            "Laboratory Access Card",
            "An electronic magnetic card granting security clearance to the Abandoned Laboratory.",
            ItemType.LAB_CARD
        );
    }

    public static InventoryItem createCircuitComponent() {
        return new InventoryItem(
            "circuit_component",
            "Circuit Component",
            "A high-precision vacuum capacitor salvaged from the laboratory's main terminal.",
            ItemType.CIRCUIT_COMPONENT
        );
    }

    public static InventoryItem createExperimentReport() {
        return new InventoryItem(
            "experiment_report",
            "Experiment Report",
            "Dr. Vance's lab logbook recording Experiment 103's successful containment parameters.",
            ItemType.EXPERIMENT_REPORT
        );
    }

    public static InventoryItem createAncientArtifact() {
        return new InventoryItem(
            "ancient_artifact",
            "Ancient Artifact",
            "An enigmatic metallic matrix radiating a low hum. The heart of the experiment.",
            ItemType.ANCIENT_ARTIFACT
        );
    }

    public static InventoryItem createVaultKey() {
        return new InventoryItem(
            "vault_key",
            "Ancient Vault Key",
            "A massive obsidian-cut security key engineered to unlock the facility's Ancient Vault.",
            ItemType.VAULT_KEY
        );
    }

    public static InventoryItem createFinalRecording() {
        return new InventoryItem(
            "final_recording",
            "Scientist's Final Recording",
            "A magnetic tape containing Dr. Vance's last transmission and quarantine override phrase.",
            ItemType.FINAL_RECORDING
        );
    }

    // =========================================================================
    // COMPATIBILITY ALIASES FOR PREVIOUS TESTS & DEMOS
    // =========================================================================

    public static InventoryItem createGoldenKey() {
        return createLibraryKey();
    }

    public static InventoryItem createOldPhotograph() {
        return createExperimentReport();
    }

    public static InventoryItem createTornPaper() {
        return createNewspaperFragment();
    }

    public static InventoryItem createMysteriousBook() {
        return createAncientManuscript();
    }

    public static InventoryItem createCandle() {
        return createMagnifyingGlass();
    }

    public static InventoryItem createClockFragment() {
        return createCircuitComponent();
    }

    public static InventoryItem createSecretNote() {
        return createResearchNote();
    }

    public static InventoryItem createFinalKey() {
        return createVaultKey();
    }

    /**
     * Returns a list of sample items representing detective mystery tools and clues.
     */
    public static List<InventoryItem> getAllSampleItems() {
        List<InventoryItem> list = new ArrayList<>();
        list.add(createNewspaperFragment());
        list.add(createResearchNote());
        list.add(createMagnifyingGlass());
        list.add(createDrawerKey());
        list.add(createLibraryKey());
        list.add(createAncientManuscript());
        list.add(createLabCard());
        list.add(createCircuitComponent());
        list.add(createVaultKey());
        return Collections.unmodifiableList(list);
    }
}
