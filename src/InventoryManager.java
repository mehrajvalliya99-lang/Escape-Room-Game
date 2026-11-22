import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the player's collected items, item capacity, item consumption,
 * and contextual item usage in rooms and puzzles.
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
            case GOLDEN_KEY:
                if (currentRoom == GameState.PLAYING) {
                    return "The Golden Key fits the study's exit door lock! Turn it to unlock the way forward.";
                } else if (currentRoom == GameState.LEVEL_2) {
                    return "The Golden Key clicks inside the locked library cabinet, releasing its brass latch!";
                }
                return "The Golden Key gleams in the light. It looks suited for an antique brass lock.";

            case OLD_PHOTOGRAPH:
                return "Inspecting the photograph: The pocket watch in the portrait is stopped precisely at 3:15.";

            case TORN_PAPER:
                return "Reading the torn parchment scrap: '...4 - 8 - 1 - 5...' The sequence matches the iron chest!";

            case MYSTERIOUS_BOOK:
                return "Opening the leather tome: Celestial charts reveal the alignment of the planetary spheres.";

            case CANDLE:
                return "You light the candle: The flickering warm flame dispels the dark shadows, illuminating hidden inscriptions.";

            case CLOCK_FRAGMENT:
                if (currentRoom == GameState.LEVEL_3) {
                    return "You insert the clock fragment into the wall mechanism. The gear teeth mesh into alignment!";
                }
                return "The bronze escapement wheel is intact. It belongs to an ancient gear mechanism.";

            case SECRET_NOTE:
                return "Unfolding the sealed secret note: 'Look closely where shadows converge near the fireplace.'";

            case FINAL_KEY:
                if (currentRoom == GameState.LEVEL_4) {
                    return "The obsidian-faceted Final Key resonates with power! Slot it into the Central Pedestal.";
                }
                return "The heavy Master Skeleton Key is adorned with obsidian facets. It opens the ultimate gateway.";

            default:
                return "You examine the " + item.getName() + ", but cannot use it here.";
        }
    }

    /**
     * Clears inventory.
     */
    public void clear() {
        items.clear();
    }

    public void reset() {
        items.clear();
    }
}
