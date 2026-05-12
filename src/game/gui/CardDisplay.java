package game.gui;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * CardDisplay — shows the card drawn when a monster lands on a Card Cell.
 *
 * Displays:
 *  - Card name (in yellow)
 *  - Card effect description (in white)
 *
 * Auto-hides after 3 seconds.
 * Hidden by default until showCard() is called.
 */
public class CardDisplay {

    private VBox  view;
    private Label nameLabel;
    private Label effectLabel;

    // ── Constructor ─────────────────────────────────────────────────────────
    public CardDisplay() {

        Label header = new Label("CARD DRAWN");
        header.setStyle(
            "-fx-text-fill: #aaaaaa;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;"
        );

        nameLabel = new Label("");
        nameLabel.setStyle(
            "-fx-text-fill: #ffe57f;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;"
        );

        effectLabel = new Label("");
        effectLabel.setStyle(
            "-fx-text-fill: #ffffff;" +
            "-fx-font-size: 11px;"
        );

        view = new VBox(3, header, nameLabel, effectLabel);
        view.setAlignment(Pos.CENTER_LEFT);
        view.setPadding(new Insets(7, 14, 7, 14));
        view.setStyle(
            "-fx-background-color: #2a2a3e;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #ffe57f;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;"
        );
        view.setPrefWidth(190);

        // Hidden until a card is drawn
        view.setVisible(false);
    }

    // ── PUBLIC API ───────────────────────────────────────────────────────────

    /**
     * Show the card box with the given name and effect.
     * Auto-hides after 3 seconds.
     *
     * @param cardName  the card's name (e.g. "SwapperCard")
     * @param effect    the card's description/effect text
     */
    public void showCard(String cardName, String effect) {
        nameLabel.setText(cardName);
        effectLabel.setText(effect);
        view.setVisible(true);

        // Auto-hide after 3 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> view.setVisible(false));
        pause.play();
    }

    /** Manually hide the card display. */
    public void hide() {
        view.setVisible(false);
    }

    /** Returns the VBox node to embed in HUDPanel. */
    public VBox getView() { return view; }
}
