package game.gui;

import game.engine.Role;
import game.engine.cells.*;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * CellView — the visual for a single cell on the 10×10 board.
 *
 * Each CellView wraps a StackPane containing:
 *   ┌─────────────────────┐
 *   │ [idx]               │   ← top bar (index number )
 *   │                     │
 *   │    [centre label]   │   ← door energy / card symbol / transport arrow
 *   │                     │
 *   │  [token] [token]    │   ← monster tokens (circles with initials)
 *   │  [flash overlay]    │   ← energy +/- or shield pop-up (fades out)
 *   └─────────────────────┘
 *
 * Call setCell() to fully re-render when the board state changes.
 * Call addToken() / clearTokens() to move monster markers.
 * Call flashLabel() to show transient energy or shield messages.
 * Call setExhausted() to grey-out a used door.
 */
public class CellView {

    // ── Sizing ───────────────────────────────────────────────────────────────
    private final int size;   // cell width = cell height in px

    // ── Colour palette ───────────────────────────────────────────────────────
    // (Placeholders — swap these for your team's art/theme)
    private static final String C_SCARER_DOOR    = "#1565c0"; // deep blue
    private static final String C_LAUGHER_DOOR   = "#e65100"; // deep orange
    private static final String C_DOOR_EXHAUSTED = "#424242"; // dark grey
    private static final String C_CARD           = "#6a1b9a"; // deep purple
    private static final String C_CONVEYOR       = "#2e7d32"; // dark green
    private static final String C_CONTAMINATION  = "#b71c1c"; // dark red
    private static final String C_MONSTER        = "#ff6f00"; // amber
    private static final String C_NORMAL         = "#263238"; // blue-grey dark
    private static final String C_BORDER         = "#546e7a"; // muted blue-grey

    private final StackPane   root;          // the node added to the GridPane
    private final BorderPane  layout;        // organises top-bar / centre / bottom
    private final Label       indexLabel;    // small index number top-left
    private final Label       centreLabel;   // door energy or transport symbol
    private final HBox        tokenRow;      // holds monster tokens at the bottom
    private final Label       flashLabel;    // transient energy / shield message

    // ── State ────────────────────────────────────────────────────────────────
    private boolean exhausted = false;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * @param index the 0-based board index (0–99), shown on the cell
     * @param size  pixel size (width and height) of the cell
     */
    public CellView(int index, int size) {
        this.size = size;

        // ── Index label (top-left) ──────────────────────────────────────────
        indexLabel = new Label(String.valueOf(index));
        indexLabel.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.6);" +
                "-fx-font-size: 8px;" +
                "-fx-padding: 1 0 0 2;"
        );

        

        // ── Top bar ─────────────────────────────────────────────────────────
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getChildren().addAll(indexLabel);

        // ── Centre label (door energy / transport arrow / etc.) ─────────────
        centreLabel = new Label("");
        centreLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );
        centreLabel.setAlignment(Pos.CENTER);

        // ── Token row (monster circles at the bottom) ────────────────────────
        tokenRow = new HBox(3);
        tokenRow.setAlignment(Pos.CENTER);
        tokenRow.setPadding(new Insets(0, 0, 2, 0));

        // ── Flash overlay (energy change / shield block) ─────────────────────
        flashLabel = new Label("");
        flashLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 2 4 2 4;" +
                "-fx-background-color: rgba(0,0,0,0.75);" +
                "-fx-background-radius: 4;"
        );
        flashLabel.setOpacity(0);   // hidden by default
        flashLabel.setMouseTransparent(true);

        // ── Layout assembly ──────────────────────────────────────────────────
        layout = new BorderPane();
        layout.setTop(topBar);
        layout.setCenter(centreLabel);
        layout.setBottom(tokenRow);
        layout.setPrefSize(size, size);
        layout.setMaxSize(size, size);
        layout.setMinSize(size, size);

        // ── Root ─────────────────────────────────────────────────────────────
        root = new StackPane(layout, flashLabel);
        root.setPrefSize(size, size);
        root.setMaxSize(size, size);
        root.setMinSize(size, size);

        // Default appearance (normal cell)
        applyStyle(C_NORMAL);
    }

    // ── PUBLIC API ───────────────────────────────────────────────────────────

    /**
     * Fully re-renders this cell to match the current engine Cell object.
     * Call this inside BoardView.updateBoard() for every cell each turn.
     *
     * @param cell  the engine Cell at this position (may be null for index 0)
     * @param index the board index (needed to re-display if cell is null)
     */
    public void setCell(Cell cell, int index) {
        indexLabel.setText(String.valueOf(index));
        centreLabel.setText("");
        exhausted = false;

        if (cell == null) {
            applyStyle(C_NORMAL);
            return;
        }

        if (cell instanceof DoorCell) {
            DoorCell dc = (DoorCell) cell;                     // ← old-style cast
            boolean isScarer = (dc.getRole() == Role.SCARER);
            String bg   = isScarer ? C_SCARER_DOOR : C_LAUGHER_DOOR;

            if (dc.isActivated()) {
                applyStyle(C_DOOR_EXHAUSTED);
                centreLabel.setText("✗");
                exhausted = true;
            } else {
                applyStyle(bg);
                centreLabel.setText(String.valueOf(dc.getEnergy()));
            }

        } else if (cell instanceof CardCell) {
            applyStyle(C_CARD);

        } else if (cell instanceof ConveyorBelt) {
            ConveyorBelt cb = (ConveyorBelt) cell;             // ← old-style cast
            applyStyle(C_CONVEYOR);
            centreLabel.setText("+" + cb.getEffect());

        } else if (cell instanceof ContaminationSock) {
            ContaminationSock cs = (ContaminationSock) cell;   // ← old-style cast
            applyStyle(C_CONTAMINATION);
            centreLabel.setText("−" + Math.abs(cs.getEffect()));

        } else if (cell instanceof MonsterCell) {
            MonsterCell mc = (MonsterCell) cell;               // ← old-style cast
            applyStyle(C_MONSTER);

            if (mc.getCellMonster() != null) {                 // ← use whichever exists
                String name = mc.getCellMonster().getName();
                centreLabel.setText(name.length() > 5 ? name.substring(0, 5) : name);
            }

        } else {
            applyStyle(C_NORMAL);
        }
    }

    /**
     * Override just the exhausted state of a door (e.g. after a monster
     * lands and activates it mid-turn, without doing a full board refresh).
     */
    public void setExhausted(boolean exhausted) {
        this.exhausted = exhausted;
        if (exhausted) {
            layout.setStyle(
                    "-fx-background-color: " + C_DOOR_EXHAUSTED + ";" +
                    "-fx-border-color: " + C_BORDER + ";" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 3;" +
                    "-fx-background-radius: 3;"
            );
            centreLabel.setText("✗");
        }
    }

    /**
     * Add a monster token (circle + initial) to the bottom of this cell.
     * Pass the pre-built Circle from BoardView and an initial string ("P"/"O").
     */
    public void addToken(Circle token, String initial) {
        // Build a small labelled token stack
        Label lbl = new Label(initial);
        lbl.setStyle(
                "-fx-font-size: 7px;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;"
        );
        StackPane tokenStack = new StackPane(token, lbl);
        tokenStack.setMaxSize(22, 22);
        tokenRow.getChildren().add(tokenStack);
    }

    /** Remove all monster tokens from this cell. */
    public void clearTokens() {
        tokenRow.getChildren().clear();
    }

    /**
     * Show a floating transient label ("+200", "−50", "⛨ BLOCKED", etc.)
     * centred over the cell that fades out automatically after ~1.2 s.
     *
     * @param text     the message to display
     * @param hexColor text colour (e.g. "#00e676" for green, "#ff5252" for red)
     */
    public void flashLabel(String text, String hexColor) {
        flashLabel.setText(text);
        flashLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + hexColor + ";" +
                "-fx-padding: 2 5 2 5;" +
                "-fx-background-color: rgba(0,0,0,0.75);" +
                "-fx-background-radius: 4;"
        );
        flashLabel.setOpacity(1);

        // Pause then fade out
        PauseTransition pause = new PauseTransition(Duration.millis(800));
        FadeTransition fade   = new FadeTransition(Duration.millis(400), flashLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        SequentialTransition seq = new SequentialTransition(pause, fade);
        seq.play();
    }

    /** Returns the StackPane node to embed in the GridPane. */
    public StackPane getPane() { return root; }

    // ── PRIVATE HELPERS ──────────────────────────────────────────────────────

    //Apply background colour
    private void applyStyle(String bgColor) {   
        layout.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 3;" +
            "-fx-background-radius: 3;"
        );
    }
}