import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model representing an item in the player's inventory.
 * Contains identifier, display name, description, and item type
 * for procedural 2D illustration rendering.
 *
 * Provides factory methods for the 8 placeholder items specified for Room 1
 * and future mystery puzzle steps.
 */
public class InventoryItem {

    public enum ItemType {
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
    // FACTORY METHODS FOR THE 8 SPECIFIED PLACEHOLDER ITEMS
    // =========================================================================

    public static InventoryItem createGoldenKey() {
        return new InventoryItem(
            "golden_key",
            "Golden Key",
            "An ornate antique key cast in solid brass. Its teeth are cut in a peculiar geometric pattern.",
            ItemType.GOLDEN_KEY
        );
    }

    public static InventoryItem createOldPhotograph() {
        return new InventoryItem(
            "old_photo",
            "Old Photograph",
            "A sepia photograph showing the mansion's original owner holding a pocket watch stopped at 3:15.",
            ItemType.OLD_PHOTOGRAPH
        );
    }

    public static InventoryItem createTornPaper() {
        return new InventoryItem(
            "torn_paper",
            "Torn Paper",
            "A scorched scrap of parchment containing half of an encoded sequence: '4 - 8 - ...'",
            ItemType.TORN_PAPER
        );
    }

    public static InventoryItem createMysteriousBook() {
        return new InventoryItem(
            "mysterious_book",
            "Mysterious Book",
            "A heavy leather tome bound with an iron latch. The gilded cover depicts an eclipse.",
            ItemType.MYSTERIOUS_BOOK
        );
    }

    public static InventoryItem createCandle() {
        return new InventoryItem(
            "candle",
            "Candle",
            "A brass candleholder carrying a half-melted wax taper. The wick still smells of brimstone.",
            ItemType.CANDLE
        );
    }

    public static InventoryItem createClockFragment() {
        return new InventoryItem(
            "clock_fragment",
            "Clock Fragment",
            "A tarnished bronze escapement wheel with broken gear teeth, salvaged from a wall clock.",
            ItemType.CLOCK_FRAGMENT
        );
    }

    public static InventoryItem createSecretNote() {
        return new InventoryItem(
            "secret_note",
            "Secret Note",
            "A tightly folded note sealed with red wax. It whispers: 'Look closely where shadows converge.'",
            ItemType.SECRET_NOTE
        );
    }

    public static InventoryItem createFinalKey() {
        return new InventoryItem(
            "final_key",
            "Final Key",
            "A large skeleton key adorned with obsidian facets. It appears designed for the study's exit door.",
            ItemType.FINAL_KEY
        );
    }

    /**
     * Returns an unmodifiable list of all 8 placeholder items for testing and demonstration.
     */
    public static List<InventoryItem> getAllSampleItems() {
        List<InventoryItem> list = new ArrayList<>();
        list.add(createGoldenKey());
        list.add(createOldPhotograph());
        list.add(createTornPaper());
        list.add(createMysteriousBook());
        list.add(createCandle());
        list.add(createClockFragment());
        list.add(createSecretNote());
        list.add(createFinalKey());
        return Collections.unmodifiableList(list);
    }
}
