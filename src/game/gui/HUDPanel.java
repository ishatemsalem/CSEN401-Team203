package game.gui;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * HUDPanel — the top info bar displayed across the game screen.
 *
 * Displays:
 *  - Current turn number
 *  - Current player name (whose turn it is)
 *  - Card drawn (name + effect) — auto-hides after 3 seconds
 *  - Freeze indicator — auto-hides after 2 seconds
 */
public class HUDPanel {

    private HBox         view;
    private Label        turnLabel;
    private Label        playerLabel;
    private Label        freezeLabel;
    private CardDisplay  cardDisplay;

    private int turnCount = 1;

    // ── Constructor ─────────────────────────────────────────────────────────
    public HUDPanel() {

        // Turn counter label
        turnLabel = styledLabel("Turn: 1");

        // Current player label
        playerLabel = styledLabel("▶  Loading...");
        playerLabel.setStyle(
            "-fx-text-fill: #00e5ff;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;"
        );

        // Freeze indicator — hidden by default
        freezeLabel = new Label("❄  FROZEN — TURN SKIPPED");
        freezeLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-background-color: #0077b6;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 6 16 6 16;" +
            "-fx-background-radius: 6;"
        );
        freezeLabel.setVisible(false);

        // Card display box
        cardDisplay = new CardDisplay();

        // Assemble into a horizontal bar
        view = new HBox(30,
            turnLabel,
            playerLabel,
            cardDisplay.getView(),
            freezeLabel
        );
        view.setAlignment(Pos.CENTER_LEFT);
        view.setPadding(new Insets(10, 24, 10, 24));
        view.setStyle("-fx-background-color: #1e1e2e;");
        view.setMinHeight(60);
    }

    // ── PUBLIC UPDATE METHODS ─────────────────────────────────────────────────

    /** Increment turn counter and update the label. */
    public void nextTurn() {
        turnCount++;
        turnLabel.setText("Turn: " + turnCount);
    }

    /** Show whose turn it currently is. */
    public void setCurrentPlayer(String name) {
        playerLabel.setText("▶  " + name + "'s Turn");
    }

    /**
     * Show or hide the frozen indicator.
     * When shown, auto-hides after 2 seconds.
     */
    public void setFrozen(boolean frozen) {
        freezeLabel.setVisible(frozen);
    }

    /**
     * Flash the ❄ FROZEN banner for 2 seconds then hide it.
     * Call this when a frozen monster's turn is skipped.
     */
    public void showFreezeAndHide() {
        freezeLabel.setVisible(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> freezeLabel.setVisible(false));
        pause.play();
    }

    /** Returns the CardDisplay so ActionPanel can trigger it after a card is drawn. */
    public CardDisplay getCardDisplay() { return cardDisplay; }

    /** Returns the current turn number. */
    public int getTurnCount() { return turnCount; }

    /** Returns the root HBox node to embed in the scene. */
    public HBox getView() { return view; }

    // ── PRIVATE HELPERS ──────────────────────────────────────────────────────
    private Label styledLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        return l;
    }
}
