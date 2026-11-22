import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Screen 6: Inventory Panel for "Escape the Room – Mystery Puzzle Adventure".
 *
 * Appears as a large centered ornate 2D window over Room 1 (The Study), keeping
 * the room softly visible but darkened in the background. Features a 3x3 grid
 * of nine item slots with rich gold/wood styling, procedural 2D illustrations for
 * the 8 placeholder mystery items, item inspection card with lore descriptions,
 * tactile hover/click effects, USE ITEM temporary message banner, and CLOSE button.
 *
 * // TODO: Member 2 - connect InventoryManager
 */
public class InventoryPanel extends JPanel {
    public static final int LOGICAL_WIDTH = 1280;
    public static final int LOGICAL_HEIGHT = 720;
    public static final int TOTAL_SLOTS = 9;

    private final GameController controller;
    private final Slot[] slots;
    private int selectedSlotIndex = -1;

    // Viewport scaling and letterboxing
    private double scale = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;

    // Window dimensions (Centered modal)
    private final Rectangle windowBounds = new Rectangle(210, 60, 860, 600);

    // Interactive UI controls
    private final MenuButton closeButton;
    private final MenuButton useItemButton;
    private final MenuButton closeXButton;
    private final MenuButton demoItemsButton;

    // Temporary notification message (for "USE ITEM" feedback)
    private String temporaryMessage = null;
    private int messageTicksRemaining = 0;
    private Timer feedbackTimer;

    // Ambient subtle sparkle/particle animation
    private float animPhase = 0.0f;
    private Timer animTimer;

    public InventoryPanel(GameController controller) {
        this.controller = controller;
        this.slots = new Slot[TOTAL_SLOTS];

        setPreferredSize(new Dimension(LOGICAL_WIDTH, LOGICAL_HEIGHT));
        setBackground(new Color(10, 12, 22));
        setDoubleBuffered(true);
        setFocusable(true);

        initSlots();

        // 1. CLOSE BUTTON (bottom right of window)
        closeButton = new MenuButton(
            "CLOSE",
            new Rectangle(825, 582, 205, 48),
            new Color(125, 30, 42),
            new Color(68, 14, 22),
            new Color(160, 42, 58),
            new Color(95, 20, 32),
            new Color(50, 10, 16),
            new Color(255, 215, 64),
            () -> controller.returnToCurrentRoom()
        );

        // 2. USE ITEM BUTTON (bottom center-right of window)
        useItemButton = new MenuButton(
            "USE ITEM",
            new Rectangle(590, 582, 205, 48),
            new Color(36, 110, 68),
            new Color(18, 62, 38),
            new Color(48, 145, 90),
            new Color(26, 88, 54),
            new Color(12, 45, 26),
            new Color(255, 215, 64),
            this::handleUseItem
        );

        // 3. CLOSE "X" BUTTON (top-right corner of window)
        closeXButton = new MenuButton(
            "X",
            new Rectangle(1022, 74, 34, 34),
            new Color(80, 24, 32),
            new Color(45, 12, 18),
            new Color(120, 36, 48),
            new Color(70, 20, 28),
            new Color(30, 8, 12),
            new Color(255, 215, 64),
            () -> controller.returnToCurrentRoom()
        );

        // 4. SAMPLE ITEMS TOGGLE BUTTON (top-left for testing & demonstration)
        demoItemsButton = new MenuButton(
            "Sample Items",
            new Rectangle(236, 76, 130, 30),
            new Color(72, 48, 110),
            new Color(42, 26, 68),
            new Color(95, 65, 145),
            new Color(58, 36, 92),
            new Color(30, 18, 50),
            new Color(212, 175, 55),
            this::toggleSampleItems
        );

        initMouseListeners();
        initTimers();
    }

    /**
     * Initializes the 3x3 grid of nine item slots.
     * Initially, all slots are empty as specified.
     */
    private void initSlots() {
        int gridStartX = 248;
        int gridStartY = 196;
        int slotSize = 96;
        int gap = 14;

        int index = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int sx = gridStartX + col * (slotSize + gap);
                int sy = gridStartY + row * (slotSize + gap);
                slots[index] = new Slot(index, new Rectangle(sx, sy, slotSize, slotSize));
                index++;
            }
        }
    }

    private void initTimers() {
        // Animation loop for subtle glow and shine
        animTimer = new Timer(50, e -> {
            animPhase += 0.08f;
            if (animPhase > 1000.0f) animPhase = 0.0f;

            if (messageTicksRemaining > 0) {
                messageTicksRemaining--;
                if (messageTicksRemaining == 0) {
                    temporaryMessage = null;
                }
            }

            repaint();
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (animTimer != null && !animTimer.isRunning()) {
            animTimer.start();
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (animTimer != null && animTimer.isRunning()) {
            animTimer.stop();
        }
    }

    // =========================================================================
    // ITEM DATA & SAMPLE TOGGLING
    // =========================================================================

    /**
     * Populates slots with sample placeholder items for preview and verification.
     * // TODO: Member 2 - connect InventoryManager to sync with real player items.
     */
    public void populateSampleItems() {
        List<InventoryItem> samples = InventoryItem.getAllSampleItems();
        for (int i = 0; i < slots.length; i++) {
            if (i < samples.size()) {
                slots[i].setItem(samples.get(i));
            } else {
                slots[i].setItem(null);
            }
        }
        if (selectedSlotIndex < 0 || slots[selectedSlotIndex].isEmpty()) {
            selectedSlotIndex = 0; // select Golden Key by default
        }
        repaint();
    }

    /**
     * Clears all item slots back to empty state.
     */
    public void clearSlots() {
        for (Slot s : slots) {
            s.setItem(null);
        }
        selectedSlotIndex = -1;
        repaint();
    }

    /**
     * Checks if any slot currently holds an item.
     */
    public boolean hasAnyItem() {
        for (Slot s : slots) {
            if (!s.isEmpty()) return true;
        }
        return false;
    }

    private void toggleSampleItems() {
        if (hasAnyItem()) {
            clearSlots();
            temporaryMessage = "Inventory compartments cleared.";
        } else {
            populateSampleItems();
            temporaryMessage = "Sample items loaded for testing.";
        }
        messageTicksRemaining = 60; // ~3 seconds
        repaint();
    }

    private void handleUseItem() {
        if (selectedSlotIndex < 0 || slots[selectedSlotIndex].isEmpty()) {
            temporaryMessage = "Select an item from your inventory first.";
        } else {
            InventoryItem item = slots[selectedSlotIndex].getItem();
            // TODO: Member 2 - inventory logic
            // TODO: Member 2 - connect InventoryManager / item usage logic
            temporaryMessage = "Item interaction will be implemented in future puzzle steps.";
        }
        messageTicksRemaining = 70; // ~3.5 seconds
        repaint();
    }

    // =========================================================================
    // MOUSE INTERACTION & HIT TESTING
    // =========================================================================

    private void initMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point lp = toLogicalCoordinates(e.getPoint());
                boolean anyHover = false;

                // Slots hover
                for (Slot s : slots) {
                    boolean h = s.contains(lp);
                    s.setHovered(h);
                    if (h) anyHover = true;
                }

                // Buttons hover
                boolean bClose = closeButton.contains(lp);
                closeButton.setHovered(bClose);
                if (bClose) anyHover = true;

                boolean bUse = useItemButton.contains(lp);
                useItemButton.setHovered(bUse);
                if (bUse) anyHover = true;

                boolean bCloseX = closeXButton.contains(lp);
                closeXButton.setHovered(bCloseX);
                if (bCloseX) anyHover = true;

                boolean bDemo = demoItemsButton.contains(lp);
                demoItemsButton.setHovered(bDemo);
                if (bDemo) anyHover = true;

                setCursor(anyHover ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());

                // Slot selection click
                for (int i = 0; i < slots.length; i++) {
                    if (slots[i].contains(lp)) {
                        selectedSlotIndex = i;
                        // Clear temporary message on item selection
                        temporaryMessage = null;
                        messageTicksRemaining = 0;
                        repaint();
                        return;
                    }
                }

                // Buttons press
                if (closeButton.contains(lp)) closeButton.setPressed(true);
                if (useItemButton.contains(lp)) useItemButton.setPressed(true);
                if (closeXButton.contains(lp)) closeXButton.setPressed(true);
                if (demoItemsButton.contains(lp)) demoItemsButton.setPressed(true);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());

                if (closeButton.isPressed()) {
                    closeButton.setPressed(false);
                    if (closeButton.contains(lp)) closeButton.trigger();
                }
                if (useItemButton.isPressed()) {
                    useItemButton.setPressed(false);
                    if (useItemButton.contains(lp)) useItemButton.trigger();
                }
                if (closeXButton.isPressed()) {
                    closeXButton.setPressed(false);
                    if (closeXButton.contains(lp)) closeXButton.trigger();
                }
                if (demoItemsButton.isPressed()) {
                    demoItemsButton.setPressed(false);
                    if (demoItemsButton.contains(lp)) demoItemsButton.trigger();
                }
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                for (Slot s : slots) s.setHovered(false);
                closeButton.setHovered(false);
                closeButton.setPressed(false);
                useItemButton.setHovered(false);
                useItemButton.setPressed(false);
                closeXButton.setHovered(false);
                closeXButton.setPressed(false);
                demoItemsButton.setHovered(false);
                demoItemsButton.setPressed(false);
                setCursor(Cursor.getDefaultCursor());
                repaint();
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private void updateScaleAndOffset() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        double targetRatio = (double) LOGICAL_WIDTH / LOGICAL_HEIGHT;
        double currentRatio = (double) w / h;

        if (currentRatio > targetRatio) {
            scale = (double) h / LOGICAL_HEIGHT;
            offsetX = (int) ((w - LOGICAL_WIDTH * scale) / 2.0);
            offsetY = 0;
        } else {
            scale = (double) w / LOGICAL_WIDTH;
            offsetX = 0;
            offsetY = (int) ((h - LOGICAL_HEIGHT * scale) / 2.0);
        }
    }

    private Point toLogicalCoordinates(Point p) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return p;

        double targetRatio = (double) LOGICAL_WIDTH / LOGICAL_HEIGHT;
        double currentRatio = (double) w / h;
        double currentScale;
        int currentOffsetX = 0;
        int currentOffsetY = 0;

        if (currentRatio > targetRatio) {
            currentScale = (double) h / LOGICAL_HEIGHT;
            currentOffsetX = (int) ((w - LOGICAL_WIDTH * currentScale) / 2.0);
        } else {
            currentScale = (double) w / LOGICAL_WIDTH;
            currentOffsetY = (int) ((h - LOGICAL_HEIGHT * currentScale) / 2.0);
        }

        if (currentScale <= 0) return p;
        int lx = (int) ((p.x - currentOffsetX) / currentScale);
        int ly = (int) ((p.y - currentOffsetY) / currentScale);
        return new Point(lx, ly);
    }

    // =========================================================================
    // RENDERING PIPELINE
    // =========================================================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        updateScaleAndOffset();

        // 1. Fill letterbox margin with deep dark void tone
        g2d.setColor(new Color(6, 7, 14));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 2. Viewport transformation to logical 1280x720
        g2d.translate(offsetX, offsetY);
        g2d.scale(scale, scale);
        g2d.clipRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);

        // Rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 3. Draw Room 1 background (dimmed)
        drawDimmedRoomBackground(g2d);

        // 4. Draw Centered Ornate Inventory Window
        drawInventoryWindow(g2d);

        // 5. Draw Header (Title & Decorative Key)
        drawHeader(g2d);

        // 6. Draw 3x3 Grid of 9 Item Slots
        drawSlotsGrid(g2d);

        // 7. Draw Item Detail / Inspection Card
        drawInspectionPanel(g2d);

        // 8. Draw Action Buttons
        drawButton(g2d, useItemButton);
        drawButton(g2d, closeButton);
        drawButton(g2d, closeXButton);
        drawButton(g2d, demoItemsButton);

        g2d.dispose();
    }

    /**
     * Renders Room 1 softly in the background with a darkening translucent scrim,
     * maintaining the atmosphere of inspecting an inventory while inside the study.
     */
    private void drawDimmedRoomBackground(Graphics2D g) {
        // Attempt to paint the active room panel (Room 1 or Level 2 Library) if available
        boolean paintedRoom = false;
        if (controller != null && controller.getGameFrame() != null) {
            GameState roomState = controller.getCurrentRoomState();
            if (roomState == null) roomState = GameState.PLAYING;
            JPanel room = controller.getGameFrame().getScreen(roomState);
            if (room != null) {
                try {
                    room.paint(g);
                    paintedRoom = true;
                } catch (Exception ignored) {
                    paintedRoom = false;
                }
            }
        }

        // Fallback procedural study backdrop if room not yet attached
        if (!paintedRoom) {
            drawProceduralStudyBackdrop(g);
        }

        // Dark translucent scrim overlay (so Room 1 remains visible but darkened)
        g.setColor(new Color(8, 10, 20, 215));
        g.fillRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);

        // Subtle dark purple vignette
        RadialGradientPaint vig = new RadialGradientPaint(
            LOGICAL_WIDTH / 2.0f, LOGICAL_HEIGHT / 2.0f, 750.0f,
            new float[]{0.0f, 0.65f, 1.0f},
            new Color[]{new Color(0, 0, 0, 0), new Color(10, 6, 22, 120), new Color(4, 3, 10, 240)}
        );
        g.setPaint(vig);
        g.fillRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);
    }

    private void drawProceduralStudyBackdrop(Graphics2D g) {
        // Dark wall
        g.setColor(new Color(24, 22, 34));
        g.fillRect(0, 0, LOGICAL_WIDTH, 500);

        // Wooden floor
        LinearGradientPaint floor = new LinearGradientPaint(
            0, 500, 0, 720,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(55, 34, 18), new Color(25, 14, 8)}
        );
        g.setPaint(floor);
        g.fillRect(0, 500, LOGICAL_WIDTH, 220);

        // Window moonlight beam
        GeneralPath beam = new GeneralPath();
        beam.moveTo(110, 150);
        beam.lineTo(240, 150);
        beam.lineTo(390, 620);
        beam.lineTo(190, 620);
        beam.closePath();
        g.setColor(new Color(135, 185, 255, 25));
        g.fill(beam);
    }

    /**
     * Draws the large centered ornate inventory chest/window.
     */
    private void drawInventoryWindow(Graphics2D g) {
        int x = windowBounds.x;
        int y = windowBounds.y;
        int w = windowBounds.width;
        int h = windowBounds.height;
        int arc = 24;

        // 1. Soft Outer Drop Shadow
        for (int i = 12; i >= 1; i--) {
            g.setColor(new Color(2, 3, 8, 18 - i));
            g.fillRoundRect(x - i, y - i + 6, w + i * 2, h + i * 2, arc + i, arc + i);
        }

        // 2. Outer Dark Wooden Frame
        LinearGradientPaint woodGrad = new LinearGradientPaint(
            x, y, x, y + h,
            new float[]{0.0f, 0.45f, 1.0f},
            new Color[]{new Color(58, 36, 22), new Color(38, 22, 14), new Color(22, 12, 8)}
        );
        g.setPaint(woodGrad);
        g.fillRoundRect(x, y, w, h, arc, arc);

        // Wooden frame outer gold rim
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(2.4f));
        g.drawRoundRect(x, y, w, h, arc, arc);

        // 3. Inner Parchment / Antiqued Leather Field
        int pad = 12;
        int ix = x + pad;
        int iy = y + pad;
        int iw = w - pad * 2;
        int ih = h - pad * 2;
        int iarc = arc - 6;

        LinearGradientPaint innerParchment = new LinearGradientPaint(
            ix, iy, ix, iy + ih,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(28, 20, 24), new Color(20, 14, 18), new Color(14, 10, 14)}
        );
        g.setPaint(innerParchment);
        g.fillRoundRect(ix, iy, iw, ih, iarc, iarc);

        // Inner golden pinstripe border
        g.setColor(new Color(180, 145, 45, 180));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(ix + 4, iy + 4, iw - 8, ih - 8, iarc - 4, iarc - 4);

        // 4. Ornate Corner Metal Brackets
        drawCornerBracket(g, ix + 6, iy + 6, 0);
        drawCornerBracket(g, ix + iw - 26, iy + 6, 90);
        drawCornerBracket(g, ix + iw - 26, iy + ih - 26, 180);
        drawCornerBracket(g, ix + 6, iy + ih - 26, 270);

        // 5. Header Separator Line
        int sepY = y + 104;
        LinearGradientPaint sepGrad = new LinearGradientPaint(
            x + 40, sepY, x + w - 40, sepY,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(212, 175, 55, 0), new Color(255, 215, 64, 190), new Color(212, 175, 55, 0)}
        );
        g.setPaint(sepGrad);
        g.setStroke(new BasicStroke(1.6f));
        g.drawLine(x + 40, sepY, x + w - 40, sepY);

        // Small decorative diamond on separator center
        drawDiamond(g, x + w / 2, sepY, 5);
    }

    private void drawCornerBracket(Graphics2D g, int cx, int cy, int angleDeg) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(cx + 10, cy + 10);
        g2.rotate(Math.toRadians(angleDeg));

        g2.setColor(new Color(212, 175, 55));
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawLine(-10, -10, 8, -10);
        g2.drawLine(-10, -10, -10, 8);

        // Rivet
        g2.setColor(new Color(255, 235, 140));
        g2.fillOval(-8, -8, 4, 4);

        g2.dispose();
    }

    /**
     * Draws the elegant heading, subtitle, and decorative antique key icon.
     */
    private void drawHeader(Graphics2D g) {
        int cx = windowBounds.x + windowBounds.width / 2;
        int topY = windowBounds.y + 14;

        // Small decorative key illustration above title
        drawKeyIconHeader(g, cx, topY + 12);

        // Main Title: INVENTORY
        Font titleFont = new Font("Georgia", Font.BOLD, 30);
        g.setFont(titleFont);
        FontMetrics tfm = g.getFontMetrics(titleFont);
        String title = "INVENTORY";
        int tx = cx - tfm.stringWidth(title) / 2;
        int ty = topY + 52;

        // Title drop shadow
        g.setColor(new Color(8, 4, 12, 220));
        g.drawString(title, tx + 2, ty + 2);

        // Title golden gradient
        LinearGradientPaint titleGrad = new LinearGradientPaint(
            tx, ty - 22, tx, ty,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(255, 238, 140), new Color(212, 165, 45)}
        );
        g.setPaint(titleGrad);
        g.drawString(title, tx, ty);

        // Subtitle: Satchel Compartments
        Font subFont = new Font("Georgia", Font.ITALIC, 14);
        g.setFont(subFont);
        FontMetrics sfm = g.getFontMetrics(subFont);
        String sub = "Satchel Compartments \u2014 Select an item to inspect its details";
        int sx = cx - sfm.stringWidth(sub) / 2;
        int sy = ty + 22;

        g.setColor(new Color(225, 210, 185));
        g.drawString(sub, sx, sy);
    }

    private void drawKeyIconHeader(Graphics2D g, int cx, int cy) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(cx, cy);

        // Key bow (circle)
        g2.setColor(new Color(255, 215, 64));
        g2.setStroke(new BasicStroke(2.2f));
        g2.drawOval(-16, -6, 12, 12);

        // Key shaft
        g2.drawLine(-4, 0, 16, 0);

        // Key bit teeth
        g2.drawLine(10, 0, 10, 6);
        g2.drawLine(15, 0, 15, 5);

        // Central gem glint
        g2.setColor(new Color(255, 250, 220));
        g2.fillOval(-11, -1, 3, 3);

        g2.dispose();
    }

    // =========================================================================
    // LAYER: 3x3 GRID OF NINE ITEM SLOTS
    // =========================================================================

    private void drawSlotsGrid(Graphics2D g) {
        // Section Header: COMPARTMENTS
        Font sectionFont = new Font("Georgia", Font.BOLD, 15);
        g.setFont(sectionFont);
        g.setColor(new Color(245, 215, 95));
        g.drawString("COMPARTMENTS (3 \u00D7 3)", 248, 182);

        // Draw each of the 9 slots
        for (Slot s : slots) {
            drawSlot(g, s, s.index == selectedSlotIndex);
        }

        // Slot occupancy count hint below grid
        int count = 0;
        for (Slot s : slots) if (!s.isEmpty()) count++;

        Font countFont = new Font("Arial", Font.PLAIN, 13);
        g.setFont(countFont);
        g.setColor(new Color(190, 175, 155));
        String countStr = "Items Collected: " + count + " / " + TOTAL_SLOTS;
        g.drawString(countStr, 248, 536);

        // Small note about Member 2 integration
        Font hintFont = new Font("Arial", Font.ITALIC, 11);
        g.setFont(hintFont);
        g.setColor(new Color(145, 135, 120));
        g.drawString("Click a slot to inspect details or test item interactions.", 248, 554);
    }

    private void drawSlot(Graphics2D g, Slot s, boolean isSelected) {
        Rectangle r = s.bounds;
        int x = r.x;
        int y = r.y;
        int w = r.width;
        int h = r.height;
        int arc = 14;

        // 1. Hover Glow
        if (s.isHovered()) {
            RadialGradientPaint hGlow = new RadialGradientPaint(
                x + w / 2.0f, y + h / 2.0f, (float) (w * 0.75),
                new float[]{0.0f, 0.7f, 1.0f},
                new Color[]{
                    new Color(255, 225, 75, 90),
                    new Color(255, 205, 50, 35),
                    new Color(255, 185, 20, 0)
                }
            );
            g.setPaint(hGlow);
            g.fillRoundRect(x - 6, y - 6, w + 12, h + 12, arc + 6, arc + 6);
        }

        // 2. Selection Radiant Aura
        if (isSelected) {
            g.setColor(new Color(255, 220, 80, 50));
            g.fillRoundRect(x - 4, y - 4, w + 8, h + 8, arc + 4, arc + 4);
        }

        // 3. Slot Drop Shadow
        g.setColor(new Color(4, 5, 10, 190));
        g.fillRoundRect(x + 2, y + 3, w, h, arc, arc);

        // 4. Dark Brown Slot Background
        Color topSlot = isSelected ? new Color(48, 30, 22) : (s.isHovered() ? new Color(42, 26, 18) : new Color(30, 18, 14));
        Color botSlot = isSelected ? new Color(28, 16, 10) : (s.isHovered() ? new Color(24, 14, 10) : new Color(18, 10, 8));

        LinearGradientPaint slotGrad = new LinearGradientPaint(
            x, y, x, y + h,
            new float[]{0.0f, 1.0f},
            new Color[]{topSlot, botSlot}
        );
        g.setPaint(slotGrad);
        g.fillRoundRect(x, y, w, h, arc, arc);

        // Subtle inner bevel / highlight
        g.setColor(new Color(255, 255, 255, 18));
        g.drawRoundRect(x + 1, y + 1, w - 2, h / 2, arc - 2, arc - 2);

        // 5. Golden Border
        if (isSelected) {
            g.setColor(new Color(255, 225, 80));
            g.setStroke(new BasicStroke(2.4f));
            g.drawRoundRect(x, y, w, h, arc, arc);

            // Double inner selection line
            g.setColor(new Color(212, 175, 55, 180));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x + 3, y + 3, w - 6, h - 6, arc - 2, arc - 2);
        } else if (s.isHovered()) {
            g.setColor(new Color(255, 215, 64));
            g.setStroke(new BasicStroke(1.8f));
            g.drawRoundRect(x, y, w, h, arc, arc);
        } else {
            g.setColor(new Color(155, 120, 45));
            g.setStroke(new BasicStroke(1.4f));
            g.drawRoundRect(x, y, w, h, arc, arc);
        }

        // Slot number indicator in top-left
        Font numFont = new Font("Arial", Font.BOLD, 10);
        g.setFont(numFont);
        g.setColor(new Color(140, 110, 60));
        g.drawString(String.valueOf(s.index + 1), x + 8, y + 14);

        // 6. Slot Content
        if (s.isEmpty()) {
            // Empty slot indicator: soft dashed cross/gem
            drawEmptySlotIndicator(g, x + w / 2, y + h / 2);
        } else {
            // Draw Item 2D Illustration
            InventoryItem item = s.getItem();
            drawItemIllustration(g, item.getType(), x + w / 2, y + h / 2 - 6, 52);

            // Item Name beneath icon
            Font nameFont = new Font("Arial", Font.BOLD, 10);
            g.setFont(nameFont);
            FontMetrics nfm = g.getFontMetrics(nameFont);
            String shortName = item.getName();
            if (nfm.stringWidth(shortName) > w - 10) {
                // Shorten if too long for slot width
                if (shortName.contains(" ")) {
                    shortName = shortName.substring(0, shortName.indexOf(" "));
                }
            }
            int nx = x + (w - nfm.stringWidth(shortName)) / 2;
            int ny = y + h - 8;

            g.setColor(new Color(10, 8, 14, 200));
            g.drawString(shortName, nx + 1, ny + 1);

            g.setColor(new Color(255, 235, 180));
            g.drawString(shortName, nx, ny);
        }
    }

    private void drawEmptySlotIndicator(Graphics2D g, int cx, int cy) {
        g.setColor(new Color(85, 65, 45, 90));
        g.setStroke(new BasicStroke(1.2f));
        g.drawOval(cx - 14, cy - 14, 28, 28);

        g.drawLine(cx - 6, cy, cx + 6, cy);
        g.drawLine(cx, cy - 6, cx, cy + 6);
    }

    // =========================================================================
    // LAYER: ITEM INSPECTION PANEL (RIGHT SIDE)
    // =========================================================================

    private void drawInspectionPanel(Graphics2D g) {
        int px = 590;
        int py = 176;
        int pw = 440;
        int ph = 384;
        int arc = 18;

        // Panel drop shadow
        g.setColor(new Color(4, 5, 10, 180));
        g.fillRoundRect(px + 2, py + 4, pw, ph, arc, arc);

        // Panel background (rich dark parchment/leather)
        LinearGradientPaint bg = new LinearGradientPaint(
            px, py, px, py + ph,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(32, 22, 20), new Color(24, 16, 16), new Color(16, 11, 14)}
        );
        g.setPaint(bg);
        g.fillRoundRect(px, py, pw, ph, arc, arc);

        // Golden panel border
        g.setColor(new Color(195, 155, 55));
        g.setStroke(new BasicStroke(1.6f));
        g.drawRoundRect(px, py, pw, ph, arc, arc);

        // Inner pinstripe
        g.setColor(new Color(140, 105, 35, 120));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRoundRect(px + 4, py + 4, pw - 8, ph - 8, arc - 4, arc - 4);

        // Header ribbon: ITEM DETAILS
        Font hdrFont = new Font("Georgia", Font.BOLD, 15);
        g.setFont(hdrFont);
        g.setColor(new Color(245, 215, 95));
        g.drawString("ITEM INSPECTION", px + 18, py + 26);

        // Divider
        g.setColor(new Color(180, 140, 45, 130));
        g.drawLine(px + 18, py + 34, px + pw - 18, py + 34);

        // Check if an occupied slot is currently selected
        Slot selectedSlot = (selectedSlotIndex >= 0 && selectedSlotIndex < slots.length)
            ? slots[selectedSlotIndex]
            : null;

        if (selectedSlot != null && !selectedSlot.isEmpty()) {
            drawOccupiedItemInspection(g, selectedSlot.getItem(), px, py, pw, ph);
        } else {
            drawEmptySlotInspection(g, px, py, pw, ph);
        }

        // Draw temporary feedback banner if active
        if (temporaryMessage != null && messageTicksRemaining > 0) {
            drawFeedbackBanner(g, px + 16, py + ph - 58, pw - 32, 44);
        }
    }

    private void drawOccupiedItemInspection(Graphics2D g, InventoryItem item, int px, int py, int pw, int ph) {
        // 1. Pedestal / Exhibition Display Circle
        int pedCX = px + 80;
        int pedCY = py + 105;
        int pedR = 46;

        // Radiant halo behind illustration
        RadialGradientPaint halo = new RadialGradientPaint(
            pedCX, pedCY, pedR + 15,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{
                new Color(255, 220, 80, 75),
                new Color(212, 160, 40, 25),
                new Color(180, 120, 20, 0)
            }
        );
        g.setPaint(halo);
        g.fillOval(pedCX - pedR - 12, pedCY - pedR - 12, (pedR + 12) * 2, (pedR + 12) * 2);

        // Pedestal disk
        g.setColor(new Color(18, 12, 14));
        g.fillOval(pedCX - pedR, pedCY - pedR, pedR * 2, pedR * 2);

        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(2.0f));
        g.drawOval(pedCX - pedR, pedCY - pedR, pedR * 2, pedR * 2);

        // Large 2D Item Illustration (size 74px)
        drawItemIllustration(g, item.getType(), pedCX, pedCY, 74);

        // 2. Item Name & Classification
        int textX = px + 145;
        int textY = py + 80;

        Font nameFont = new Font("Georgia", Font.BOLD, 22);
        g.setFont(nameFont);

        // Name drop shadow
        g.setColor(new Color(8, 4, 8, 220));
        g.drawString(item.getName(), textX + 1, textY + 1);

        g.setColor(new Color(255, 230, 110));
        g.drawString(item.getName(), textX, textY);

        // Type Tag / Category Badge
        int badgeY = textY + 12;
        g.setColor(new Color(60, 38, 22));
        g.fillRoundRect(textX, badgeY, 110, 22, 8, 8);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRoundRect(textX, badgeY, 110, 22, 8, 8);

        Font badgeFont = new Font("Arial", Font.BOLD, 10);
        g.setFont(badgeFont);
        g.setColor(new Color(255, 225, 140));
        g.drawString("KEY ARTIFACT", textX + 12, badgeY + 15);

        // 3. Description Header & Text
        int descY = py + 180;
        Font descHdrFont = new Font("Georgia", Font.BOLD, 14);
        g.setFont(descHdrFont);
        g.setColor(new Color(245, 215, 95));
        g.drawString("DESCRIPTION & CLUES", px + 22, descY);

        g.setColor(new Color(150, 115, 45, 120));
        g.drawLine(px + 22, descY + 6, px + pw - 22, descY + 6);

        // Description Body (multi-line wrapped)
        Font descFont = new Font("Georgia", Font.PLAIN, 15);
        g.setFont(descFont);
        g.setColor(new Color(255, 245, 225));

        int descStartX = px + 22;
        int descCurrentY = descY + 28;
        int maxLineWidth = pw - 44;

        List<String> lines = wrapText(item.getDescription(), g.getFontMetrics(descFont), maxLineWidth);
        for (String line : lines) {
            g.drawString(line, descStartX, descCurrentY);
            descCurrentY += 22;
        }

        // Subnote regarding potential use
        descCurrentY += 8;
        drawDiamond(g, descStartX + 4, descCurrentY - 4, 4);
        Font noteFont = new Font("Arial", Font.ITALIC, 13);
        g.setFont(noteFont);
        g.setColor(new Color(210, 190, 155));
        g.drawString("Click [USE ITEM] to test using this item on Room 1 puzzle locks.", descStartX + 14, descCurrentY);
    }

    private void drawEmptySlotInspection(Graphics2D g, int px, int py, int pw, int ph) {
        int cx = px + pw / 2;
        int cy = py + ph / 2 - 20;

        // Faint outline of item pedestal
        g.setColor(new Color(80, 60, 45, 70));
        g.setStroke(new BasicStroke(1.8f));
        g.drawOval(cx - 40, cy - 40, 80, 80);

        // Faint satchel icon
        drawSatchelOutline(g, cx, cy, 32);

        Font emptyHdr = new Font("Georgia", Font.BOLD, 20);
        g.setFont(emptyHdr);
        FontMetrics fm = g.getFontMetrics(emptyHdr);
        String msg = "Compartment Empty";
        int mx = cx - fm.stringWidth(msg) / 2;
        int my = cy + 65;

        g.setColor(new Color(212, 175, 55, 180));
        g.drawString(msg, mx, my);

        Font subFont = new Font("Georgia", Font.PLAIN, 14);
        g.setFont(subFont);
        FontMetrics sfm = g.getFontMetrics(subFont);
        String sub1 = "Select an occupied slot on the left to inspect";
        String sub2 = "collected items, keys, and parchment clues.";
        String sub3 = "(Or click [Sample Items] above to test with sample items)";

        g.setColor(new Color(210, 195, 175));
        g.drawString(sub1, cx - sfm.stringWidth(sub1) / 2, my + 26);
        g.drawString(sub2, cx - sfm.stringWidth(sub2) / 2, my + 46);

        Font subFont3 = new Font("Arial", Font.ITALIC, 12);
        g.setFont(subFont3);
        FontMetrics sfm3 = g.getFontMetrics(subFont3);
        g.setColor(new Color(255, 215, 64, 190));
        g.drawString(sub3, cx - sfm3.stringWidth(sub3) / 2, my + 72);
    }

    private void drawSatchelOutline(Graphics2D g, int cx, int cy, int size) {
        int half = size / 2;
        g.setColor(new Color(140, 110, 65, 90));
        g.setStroke(new BasicStroke(1.8f));
        g.drawRoundRect(cx - half, cy - half + 4, size, size - 8, 8, 8);
        g.drawArc(cx - half / 2, cy - half - 4, half, half, 0, 180);
    }

    /**
     * Draws the temporary message banner when USE ITEM or sample items are triggered.
     */
    private void drawFeedbackBanner(Graphics2D g, int x, int y, int w, int h) {
        int arc = 12;

        // Banner shadow
        g.setColor(new Color(4, 5, 10, 200));
        g.fillRoundRect(x + 1, y + 2, w, h, arc, arc);

        // Banner background (deep purple/amber glow)
        LinearGradientPaint bg = new LinearGradientPaint(
            x, y, x, y + h,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(75, 45, 15), new Color(42, 24, 8)}
        );
        g.setPaint(bg);
        g.fillRoundRect(x, y, w, h, arc, arc);

        // Bright gold border
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(1.8f));
        g.drawRoundRect(x, y, w, h, arc, arc);

        // Banner text
        Font bFont = new Font("Georgia", Font.BOLD, 13);
        g.setFont(bFont);
        FontMetrics fm = g.getFontMetrics(bFont);
        int tx = x + (w - fm.stringWidth(temporaryMessage)) / 2;
        int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();

        // Decorative diamond before message
        drawDiamond(g, tx - 14, ty - 4, 4);

        g.setColor(new Color(255, 248, 220));
        g.drawString(temporaryMessage, tx, ty);
    }

    // =========================================================================
    // LAYER: 2D PROCEDURAL ITEM ILLUSTRATIONS (ALL 8 REQUIRED PLACEHOLDERS)
    // =========================================================================

    /**
     * Renders attractive 2D procedural illustrations for each of the 8 placeholder items.
     * All graphics are drawn mathematically with Graphics2D using gradients, shadows,
     * highlights, and fine details.
     */
    private void drawItemIllustration(Graphics2D g, InventoryItem.ItemType type, int cx, int cy, int size) {
        if (type == null) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(cx, cy);

        float scaleFactor = size / 70.0f;
        g2.scale(scaleFactor, scaleFactor);

        switch (type) {
            case GOLDEN_KEY -> drawGoldenKeyArt(g2);
            case OLD_PHOTOGRAPH -> drawOldPhotographArt(g2);
            case TORN_PAPER -> drawTornPaperArt(g2);
            case MYSTERIOUS_BOOK -> drawMysteriousBookArt(g2);
            case CANDLE -> drawCandleArt(g2);
            case CLOCK_FRAGMENT -> drawClockFragmentArt(g2);
            case SECRET_NOTE -> drawSecretNoteArt(g2);
            case FINAL_KEY -> drawFinalKeyArt(g2);
        }

        g2.dispose();
    }

    // 1. GOLDEN KEY ART
    private void drawGoldenKeyArt(Graphics2D g) {
        // Drop shadow
        g.setColor(new Color(10, 8, 14, 150));
        g.drawOval(-24, -13, 20, 20);
        g.drawLine(-4, -3, 24, -3);

        // Ornate Bow (Head)
        LinearGradientPaint gold = new LinearGradientPaint(
            -26, -16, -6, 4,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(255, 235, 140), new Color(212, 175, 55), new Color(150, 110, 30)}
        );
        g.setPaint(gold);
        g.setStroke(new BasicStroke(4.0f));
        g.drawOval(-26, -15, 20, 20);

        // Heart/clover cutout in bow
        g.setColor(new Color(255, 245, 180));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(-22, -11, 12, 12);

        // Collar ring
        g.setColor(new Color(255, 220, 90));
        g.fillRect(-7, -4, 4, 8);

        // Stem / Shaft
        g.setPaint(gold);
        g.setStroke(new BasicStroke(4.0f));
        g.drawLine(-6, 0, 24, 0);

        // Key Bit Teeth
        g.setStroke(new BasicStroke(3.0f));
        g.drawLine(14, 0, 14, 11);
        g.drawLine(20, 0, 20, 8);
        g.drawLine(24, 0, 24, 14);

        // Highlight glint
        g.setColor(Color.WHITE);
        g.fillOval(-23, -13, 3, 3);
        g.drawLine(0, -1, 10, -1);
    }

    // 2. OLD PHOTOGRAPH ART
    private void drawOldPhotographArt(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.rotate(Math.toRadians(-6));

        // Photo card drop shadow
        g2.setColor(new Color(10, 8, 14, 170));
        g2.fillRect(-21, -27, 44, 56);

        // Antique cream photo frame border
        LinearGradientPaint cardParchment = new LinearGradientPaint(
            -23, -29, 23, 29,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(240, 230, 205), new Color(205, 190, 160)}
        );
        g2.setPaint(cardParchment);
        g2.fillRect(-23, -29, 46, 58);

        g2.setColor(new Color(150, 130, 100));
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRect(-23, -29, 46, 58);

        // Inner Sepia Photo Area
        LinearGradientPaint sepia = new LinearGradientPaint(
            -18, -24, 18, 14,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(85, 60, 40), new Color(55, 36, 24), new Color(30, 18, 12)}
        );
        g2.setPaint(sepia);
        g2.fillRect(-18, -24, 36, 38);

        // Silhouette of mansion & moon in sepia
        g2.setColor(new Color(230, 210, 170, 160));
        g2.fillOval(4, -20, 8, 8); // moon

        g2.setColor(new Color(25, 16, 10));
        GeneralPath mansion = new GeneralPath();
        mansion.moveTo(-16, 14);
        mansion.lineTo(-16, -2);
        mansion.lineTo(-8, -12); // roof peak
        mansion.lineTo(0, -4);
        mansion.lineTo(10, -8);
        mansion.lineTo(16, 14);
        mansion.closePath();
        g2.fill(mansion);

        // Sepia aged tape tabs on corners
        g2.setColor(new Color(210, 190, 140, 190));
        g2.fillRect(-24, -30, 8, 4);
        g2.fillRect(16, 25, 8, 4);

        g2.dispose();
    }

    // 3. TORN PAPER ART
    private void drawTornPaperArt(Graphics2D g) {
        // Shadow
        g.setColor(new Color(10, 8, 14, 160));
        g.fillRoundRect(-20, -22, 42, 48, 4, 4);

        // Jagged torn polygon
        GeneralPath paper = new GeneralPath();
        paper.moveTo(-22, -26);
        paper.lineTo(18, -24);
        paper.lineTo(24, -14);
        paper.lineTo(20, 8);
        paper.lineTo(25, 22);
        paper.lineTo(8, 26);
        paper.lineTo(-6, 20);
        paper.lineTo(-14, 25);
        paper.lineTo(-24, 14);
        paper.lineTo(-19, -4);
        paper.closePath();

        LinearGradientPaint paperGrad = new LinearGradientPaint(
            -22, -26, 25, 26,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(245, 230, 190), new Color(225, 205, 160), new Color(195, 170, 125)}
        );
        g.setPaint(paperGrad);
        g.fill(paper);

        // Burnt/torn edges stroke
        g.setColor(new Color(130, 95, 55));
        g.setStroke(new BasicStroke(1.2f));
        g.draw(paper);

        // Faded calligraphy script lines
        g.setColor(new Color(75, 55, 38, 180));
        g.setStroke(new BasicStroke(1.4f));
        g.drawLine(-14, -14, 12, -14);
        g.drawLine(-16, -6, 14, -6);
        g.drawLine(-13, 2, 8, 2);
        g.drawLine(-15, 10, 6, 10);

        // Mysterious red cipher number: '4 - 8'
        Font cipherFont = new Font("Georgia", Font.BOLD, 10);
        g.setFont(cipherFont);
        g.setColor(new Color(180, 40, 35));
        g.drawString("4 \u2013 8 \u2026", -10, 18);
    }

    // 4. MYSTERIOUS BOOK ART
    private void drawMysteriousBookArt(Graphics2D g) {
        // Book shadow
        g.setColor(new Color(10, 8, 14, 170));
        g.fillRoundRect(-20, -22, 42, 48, 6, 6);

        // Leather Cover (Deep wine/crimson gradient)
        LinearGradientPaint cover = new LinearGradientPaint(
            -22, -24, 22, 24,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(125, 25, 38), new Color(85, 15, 25), new Color(50, 8, 14)}
        );
        g.setPaint(cover);
        g.fillRoundRect(-22, -24, 42, 48, 6, 6);

        // Gold embossed cover border
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.4f));
        g.drawRoundRect(-20, -22, 38, 44, 4, 4);

        // Golden spine ribs on left
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(-22, -14, -18, -14);
        g.drawLine(-22, 0, -18, 0);
        g.drawLine(-22, 14, -18, 14);

        // Pages side edge on right
        g.setColor(new Color(235, 220, 185));
        g.fillRect(17, -20, 4, 40);
        g.setColor(new Color(160, 140, 105));
        g.drawRect(17, -20, 4, 40);

        // Golden Arcane Star Emblem on center
        drawDiamond(g, -1, 0, 7);

        // Golden bookmark ribbon extending from bottom
        g.setColor(new Color(212, 175, 55));
        GeneralPath ribbon = new GeneralPath();
        ribbon.moveTo(2, 24);
        ribbon.lineTo(8, 32);
        ribbon.lineTo(4, 30);
        ribbon.lineTo(0, 32);
        ribbon.closePath();
        g.fill(ribbon);
    }

    // 5. CANDLE ART
    private void drawCandleArt(Graphics2D g) {
        // Brass base saucer
        LinearGradientPaint brass = new LinearGradientPaint(
            -18, 14, 18, 24,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(255, 225, 120), new Color(205, 160, 45), new Color(130, 95, 25)}
        );
        g.setPaint(brass);
        g.fillOval(-18, 14, 36, 10);
        g.setColor(new Color(110, 80, 20));
        g.setStroke(new BasicStroke(1.2f));
        g.drawOval(-18, 14, 36, 10);

        // Brass handle thumb loop on right
        g.setPaint(brass);
        g.setStroke(new BasicStroke(2.4f));
        g.drawArc(12, 10, 10, 12, 270, 270);

        // Wax candle column
        LinearGradientPaint wax = new LinearGradientPaint(
            -7, -10, 7, 16,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(255, 250, 235), new Color(240, 230, 210), new Color(205, 190, 165)}
        );
        g.setPaint(wax);
        g.fillRect(-7, -10, 14, 26);

        // Wax drips
        g.setColor(new Color(250, 245, 230));
        g.fillOval(-8, -4, 4, 8);
        g.fillOval(4, 2, 4, 10);

        // Candle wick
        g.setColor(new Color(40, 30, 20));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(0, -10, 0, -16);

        // Candle flame halo glow
        RadialGradientPaint glow = new RadialGradientPaint(
            0, -23, 20,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(255, 220, 80, 140), new Color(255, 140, 20, 60), new Color(255, 100, 0, 0)}
        );
        g.setPaint(glow);
        g.fillOval(-20, -43, 40, 40);

        // Flame teardrop
        GeneralPath flame = new GeneralPath();
        flame.moveTo(0, -32);
        flame.curveTo(6, -26, 6, -18, 0, -17);
        flame.curveTo(-6, -18, -6, -26, 0, -32);
        flame.closePath();

        LinearGradientPaint flameGrad = new LinearGradientPaint(
            0, -32, 0, -17,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(255, 255, 220), new Color(255, 195, 40), new Color(235, 80, 15)}
        );
        g.setPaint(flameGrad);
        g.fill(flame);
    }

    // 6. CLOCK FRAGMENT ART
    private void drawClockFragmentArt(Graphics2D g) {
        // Drop shadow
        g.setColor(new Color(10, 8, 14, 160));
        g.fillOval(-20, -20, 40, 40);

        // Antique broken brass gear rim
        LinearGradientPaint bronze = new LinearGradientPaint(
            -22, -22, 22, 22,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(225, 185, 95), new Color(175, 130, 45), new Color(110, 75, 20)}
        );
        g.setPaint(bronze);
        g.setStroke(new BasicStroke(3.5f));
        g.drawOval(-20, -20, 40, 40);

        // Perimeter Cog teeth
        for (int i = 0; i < 12; i++) {
            if (i >= 7 && i <= 9) continue; // broken section!
            double rad = Math.toRadians(i * 30);
            int x1 = (int) (Math.cos(rad) * 19);
            int y1 = (int) (Math.sin(rad) * 19);
            int x2 = (int) (Math.cos(rad) * 24);
            int y2 = (int) (Math.sin(rad) * 24);
            g.drawLine(x1, y1, x2, y2);
        }

        // Inner dial plate (partial cracked porcelain)
        g.setColor(new Color(245, 235, 215, 210));
        g.fillOval(-15, -15, 30, 30);

        // Crack line across clock
        g.setColor(new Color(90, 65, 35));
        g.setStroke(new BasicStroke(1.2f));
        g.drawLine(-12, -4, 2, 2);
        g.drawLine(2, 2, 14, -8);

        // Roman numerals 'III' and 'VI'
        Font romFont = new Font("Georgia", Font.BOLD, 8);
        g.setFont(romFont);
        g.setColor(new Color(50, 35, 20));
        g.drawString("III", 6, 3);
        g.drawString("VI", -4, 12);

        // Broken ornate clock hand
        g.setColor(new Color(30, 20, 10));
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(0, 0, -8, -12);
        g.fillOval(-2, -2, 5, 5); // center arbor
    }

    // 7. SECRET NOTE ART
    private void drawSecretNoteArt(Graphics2D g) {
        // Shadow
        g.setColor(new Color(10, 8, 14, 170));
        g.fillRoundRect(-21, -16, 44, 34, 4, 4);

        // Folded parchment envelope / document
        LinearGradientPaint noteParchment = new LinearGradientPaint(
            -22, -18, 22, 18,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(245, 235, 205), new Color(225, 210, 170), new Color(195, 175, 130)}
        );
        g.setPaint(noteParchment);
        g.fillRoundRect(-22, -18, 44, 36, 4, 4);

        g.setColor(new Color(145, 115, 65));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(-22, -18, 44, 36, 4, 4);

        // Envelope fold lines
        g.setColor(new Color(165, 135, 85));
        g.setStroke(new BasicStroke(1.0f));
        g.drawLine(-22, -18, 0, 2);
        g.drawLine(22, -18, 0, 2);
        g.drawLine(-22, 18, -4, 0);
        g.drawLine(22, 18, 4, 0);

        // Crimson Wax Seal on center
        g.setColor(new Color(185, 30, 42));
        g.fillOval(-8, -6, 16, 16);

        // Seal stamp ring
        g.setColor(new Color(125, 18, 26));
        g.setStroke(new BasicStroke(1.2f));
        g.drawOval(-6, -4, 12, 12);

        // Key/crest impression in wax
        g.setColor(new Color(240, 110, 120));
        g.fillOval(-2, -1, 4, 4);
        g.drawLine(0, 1, 0, 5);
    }

    // 8. FINAL KEY ART
    private void drawFinalKeyArt(Graphics2D g) {
        // Dark Master Obsidian Key
        // Drop shadow
        g.setColor(new Color(10, 8, 14, 180));
        g.drawOval(-24, -15, 22, 22);
        g.drawLine(-4, -4, 25, -4);

        // Gothic Winged Bow (Obsidian & Antique Gold)
        LinearGradientPaint obsidian = new LinearGradientPaint(
            -26, -18, -4, 6,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(80, 50, 110), new Color(38, 22, 60), new Color(18, 10, 30)}
        );
        g.setPaint(obsidian);
        g.fillOval(-26, -18, 24, 24);

        // Gold rim around obsidian bow
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(2.4f));
        g.drawOval(-26, -18, 24, 24);

        // Sapphire Gem in Bow Center
        RadialGradientPaint sapphire = new RadialGradientPaint(
            -14, -6, 7,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{new Color(180, 225, 255), new Color(45, 120, 240), new Color(15, 40, 120)}
        );
        g.setPaint(sapphire);
        g.fillOval(-18, -10, 9, 9);
        g.setColor(new Color(255, 255, 255, 220));
        g.fillOval(-16, -8, 3, 3); // sparkle

        // Master Stem (dual gold & dark titanium)
        LinearGradientPaint goldStem = new LinearGradientPaint(
            -2, 0, 26, 0,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(255, 225, 110), new Color(212, 175, 55), new Color(160, 115, 25)}
        );
        g.setPaint(goldStem);
        g.setStroke(new BasicStroke(4.5f));
        g.drawLine(-2, -4, 26, -4);

        // Double Skeleton Key Bits
        g.setStroke(new BasicStroke(3.2f));
        g.drawLine(14, -4, 14, 8);
        g.drawLine(20, -4, 20, 6);
        g.drawLine(26, -4, 26, 12);

        // Cut notches on bits
        g.setColor(new Color(18, 10, 30));
        g.drawLine(14, 2, 17, 2);
        g.drawLine(20, 1, 23, 1);
    }

    // =========================================================================
    // LAYER: POLISHED BUTTONS & HELPERS
    // =========================================================================

    private void drawButton(Graphics2D g, MenuButton btn) {
        Rectangle r = btn.bounds;
        int x = r.x;
        int y = r.y + (btn.pressed ? 2 : 0);
        int w = r.width;
        int h = r.height;
        int arc = 18;

        // Hover glow
        if (btn.hovered) {
            RadialGradientPaint glow = new RadialGradientPaint(
                x + w / 2.0f, y + h / 2.0f, (float) (w * 0.7),
                new float[]{0.0f, 0.65f, 1.0f},
                new Color[]{
                    new Color(255, 225, 75, 115),
                    new Color(255, 205, 50, 40),
                    new Color(255, 185, 20, 0)
                }
            );
            g.setPaint(glow);
            g.fillRoundRect(x - 6, y - 6, w + 12, h + 12, arc + 6, arc + 6);
        }

        // Drop shadow
        g.setColor(new Color(4, 5, 12, 170));
        g.fillRoundRect(x + 2, y + 4, w, h, arc, arc);

        // Gradient
        Color topC = btn.pressed ? btn.pressedColor : (btn.hovered ? btn.hoverTopColor : btn.topColor);
        Color botC = btn.pressed ? btn.pressedColor.darker() : (btn.hovered ? btn.hoverBottomColor : btn.bottomColor);

        LinearGradientPaint btnGrad = new LinearGradientPaint(
            x, y, x, y + h,
            new float[]{0.0f, 1.0f},
            new Color[]{topC, botC}
        );
        g.setPaint(btnGrad);
        g.fillRoundRect(x, y, w, h, arc, arc);

        // Top gloss reflection
        Area glossArea = new Area(new RoundRectangle2D.Double(x, y, w, h, arc, arc));
        glossArea.intersect(new Area(new Rectangle(x, y, w, h / 2)));
        LinearGradientPaint glossGrad = new LinearGradientPaint(
            x, y, x, y + h / 2,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(255, 255, 255, 50), new Color(255, 255, 255, 10)}
        );
        g.setPaint(glossGrad);
        g.fill(glossArea);

        // Golden border
        g.setColor(btn.borderColor);
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(x, y, w, h, arc, arc);

        // Text or Icon
        if ("X".equals(btn.text)) {
            // Crisp vector crossed diagonal lines for Close 'X'
            g.setColor(new Color(4, 5, 12, 220));
            g.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x + 10, y + 10, x + w - 10, y + h - 10);
            g.drawLine(x + w - 10, y + 10, x + 10, y + h - 10);

            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x + 9, y + 9, x + w - 11, y + h - 11);
            g.drawLine(x + w - 11, y + 9, x + 9, y + h - 11);
            return;
        }

        Font font = (w < 40) ? new Font("Arial", Font.BOLD, 16) : new Font("Arial", Font.BOLD, 16);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        int textX = x + (w - fm.stringWidth(btn.text)) / 2;
        int textY = y + (h - fm.getHeight()) / 2 + fm.getAscent();

        // Text drop shadow & fill
        g.setColor(new Color(4, 5, 12, 220));
        g.drawString(btn.text, textX + 1, textY + 2);

        g.setColor(Color.WHITE);
        g.drawString(btn.text, textX, textY);
    }

    private void drawDiamond(Graphics2D g, int cx, int cy, int size) {
        GeneralPath diamond = new GeneralPath();
        diamond.moveTo(cx, cy - size);
        diamond.lineTo(cx + size, cy);
        diamond.lineTo(cx, cy + size);
        diamond.lineTo(cx - size, cy);
        diamond.closePath();

        g.setColor(new Color(255, 215, 64));
        g.fill(diamond);
        g.setColor(new Color(150, 105, 30));
        g.setStroke(new BasicStroke(1.0f));
        g.draw(diamond);
    }

    private List<String> wrapText(String text, FontMetrics fm, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) return lines;

        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String candidate = currentLine.isEmpty() ? word : currentLine + " " + word;
            if (fm.stringWidth(candidate) <= maxWidth) {
                currentLine.append(currentLine.isEmpty() ? "" : " ").append(word);
            } else {
                if (!currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                }
                currentLine = new StringBuilder(word);
            }
        }
        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    // =========================================================================
    // GETTERS & TESTING METHODS
    // =========================================================================

    public Slot[] getSlots() {
        return slots;
    }

    public int getSelectedSlotIndex() {
        return selectedSlotIndex;
    }

    public void setSelectedSlotIndex(int selectedSlotIndex) {
        this.selectedSlotIndex = selectedSlotIndex;
        repaint();
    }

    public MenuButton getCloseButton() {
        return closeButton;
    }

    public MenuButton getUseItemButton() {
        return useItemButton;
    }

    public String getTemporaryMessage() {
        return temporaryMessage;
    }

    // =========================================================================
    // INNER CLASSES: Slot & MenuButton
    // =========================================================================

    public static class Slot {
        public final int index;
        public final Rectangle bounds;
        private InventoryItem item;
        private boolean hovered;

        public Slot(int index, Rectangle bounds) {
            this.index = index;
            this.bounds = bounds;
            this.item = null;
            this.hovered = false;
        }

        public boolean contains(Point p) {
            return bounds != null && bounds.contains(p);
        }

        public boolean isEmpty() {
            return item == null;
        }

        public InventoryItem getItem() {
            return item;
        }

        public void setItem(InventoryItem item) {
            this.item = item;
        }

        public boolean isHovered() {
            return hovered;
        }

        public void setHovered(boolean hovered) {
            this.hovered = hovered;
        }
    }

    public static class MenuButton {
        public final String text;
        public final Rectangle bounds;
        public final Color topColor;
        public final Color bottomColor;
        public final Color hoverTopColor;
        public final Color hoverBottomColor;
        public final Color pressedColor;
        public final Color borderColor;
        public final Runnable action;

        private boolean hovered;
        private boolean pressed;

        public MenuButton(String text, Rectangle bounds,
                          Color topColor, Color bottomColor,
                          Color hoverTopColor, Color hoverBottomColor,
                          Color pressedColor, Color borderColor,
                          Runnable action) {
            this.text = text;
            this.bounds = bounds;
            this.topColor = topColor;
            this.bottomColor = bottomColor;
            this.hoverTopColor = hoverTopColor;
            this.hoverBottomColor = hoverBottomColor;
            this.pressedColor = pressedColor;
            this.borderColor = borderColor;
            this.action = action;
        }

        public boolean contains(Point p) {
            return bounds.contains(p);
        }

        public void trigger() {
            if (action != null) action.run();
        }

        public boolean isHovered() { return hovered; }
        public void setHovered(boolean hovered) { this.hovered = hovered; }

        public boolean isPressed() { return pressed; }
        public void setPressed(boolean pressed) { this.pressed = pressed; }
    }
}
