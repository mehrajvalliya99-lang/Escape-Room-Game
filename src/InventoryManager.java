import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the player's collected items, item capacity, item consumption,
 * and contextual item usage in rooms and puzzles for
 * "THE LAST EXPERIMENT — A Detective Mystery Adventure".
 */
public class InventoryManager {

    public static final int MAX_CAPACITY = 9; // 3x3 grid
    private final List<InventoryItem> items;

    public InventoryManager() {
        this.items = new ArrayList<>();
    }

    /**
     * Adds an item to inventory if not full and not duplicate.
     * @return true if successfully added, false otherwise.
     */
    public boolean addItem(InventoryItem item) {
        if (item == null || isFull()) {
            return false;
        }
        if (hasItem(item.getId())) {
            return false; // Prevent duplicate
        }
        items.add(item);
        return true;
    }

    /**
     * Removes an item by its ID.
     */
    public boolean removeItem(String itemId) {
        if (itemId == null) return false;
        return items.removeIf(item -> itemId.equals(item.getId()));
    }

    /**
     * Checks if the inventory contains an item with the given ID.
     */
    public boolean hasItem(String itemId) {
        if (itemId == null) return false;
        for (InventoryItem item : items) {
            if (itemId.equals(item.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the inventory contains an item of the given type.
     */
    public boolean hasItemType(InventoryItem.ItemType type) {
        if (type == null) return false;
        for (InventoryItem item : items) {
            if (type == item.getType()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves an item by ID.
     */
    public InventoryItem getItem(String itemId) {
        if (itemId == null) return null;
        for (InventoryItem item : items) {
            if (itemId.equals(item.getId())) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns an unmodifiable view of collected items.
     */
    public List<InventoryItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int getItemCount() {
        return items.size();
    }

    public boolean isFull() {
        return items.size() >= MAX_CAPACITY;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Performs contextual action when an item is used.
     */
    public String useItem(InventoryItem item, GameState currentRoom) {
        if (item == null) {
            return "No item selected.";
        }

        switch (item.getType()) {
            case NEWSPAPER_FRAGMENT:
            case TORN_PAPER:
                return "Reading the torn headline: '...FACILITY BREAKTHROUGH: 1947...' The year is circled in red ink!";

            case RESEARCH_NOTE:
            case SECRET_NOTE:
                return "Inspecting Dr. Vance's lab notebook: 'Containment relay sequence: POWER -> COOLING -> VACUUM -> SHIELD.'";

            case MAGNIFYING_GLASS:
                return "Using the magnifying glass: Tiny micro-inscriptions become visible on the documents and dials!";

            case DRAWER_KEY:
                if (currentRoom == GameState.PLAYING) {
                    return "The small brass key fits the locked desk drawer. The drawer slides open, revealing secret files!";
                }
                return "A small brass desk drawer key engraved with the facility emblem.";

            case CIPHER_MANUSCRIPT:
                return "Studying the decoded manuscript: 'The missing archive keyword is EXPERIMENT.'";

            case LIBRARY_KEY:
            case GOLDEN_KEY:
                if (currentRoom == GameState.PLAYING) {
                    return "The heavy brass key fits the study exit lock! Turn it to unlock the Forgotten Library.";
                } else if (currentRoom == GameState.LEVEL_2) {
                    return "The key clicks inside the library cabinet, releasing its brass latch!";
                }
                return "A heavy brass key engraved with 'LIBRARY WING'. Fits antique mortise locks.";

            case ANCIENT_MANUSCRIPT:
            case MYSTERIOUS_BOOK:
                return "Reading the ancient manuscript: It details a mysterious energy relic uncovered deep beneath the facility.";

            case LAB_CARD:
                if (currentRoom == GameState.LEVEL_2) {
                    return "You swipe the research access card into the terminal reader. The electronic maglock disengages!";
                } else if (currentRoom == GameState.LEVEL_3) {
                    return "The access card grants Level 3 laboratory security clearance.";
                }
                return "A magnetic stripe security card labeled: 'DR. A. VANCE \u2014 LEVEL 3 ACCESS'.";

            case CIRCUIT_COMPONENT:
            case CLOCK_FRAGMENT:
                if (currentRoom == GameState.LEVEL_3) {
                    return "You insert the circuit component into the breaker slot. Main electrical power hums back to life!";
                }
                return "A precision electrical relay component. Essential for restoring power to the laboratory grid.";

            case EXPERIMENT_REPORT:
                return "Reviewing Experiment 103 logs: 'Terminal override security code: 8 - 4 - 6 - 2.'";

            case ANCIENT_ARTIFACT:
                if (currentRoom == GameState.LEVEL_4) {
                    return "The glowing ancient artifact pulses with energy! Slot it into the vault pedestal socket.";
                }
                return "A metallic artifact with intricate geometric etchings. It resonates with a faint turquoise hum.";

            case VAULT_KEY:
            case FINAL_KEY:
                if (currentRoom == GameState.LEVEL_4) {
                    return "The obsidian-faceted Vault Key engages the locking pins! Turn it to disengage the vault mechanism.";
                } else if (currentRoom == GameState.LEVEL_3) {
                    return "The Vault Key was retrieved from the security terminal. Take it to the vault entrance!";
                }
                return "The heavy obsidian-faceted Vault Key. Required to breach the final containment vault.";

            case FINAL_RECORDING:
                return "Listening to Dr. Vance's final recording: 'The experiment succeeded... but quarantine engaged. Escape before the facility seals permanently!'";

            case OLD_PHOTOGRAPH:
                return "Inspecting the archival photograph: The clock in Dr. Vance's study is stopped at 4:25.";

            case CANDLE:
                return "Lighting the laboratory burner: The flame illuminates hidden heat-sensitive ink on the documents.";

            default:
                return "You examine the " + item.getName() + ", but cannot use it here.";
        }
    }

    /**
     * Clears inventory.
     */
    public void reset() {
        items.clear();
    }
}
