package game.gui;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Card drawn on a card cell: name, description, and a short effect / lucky indication.
 */
public class CardDisplay {

    private VBox  view;
    private Label nameLabel;
    private Label effectLabel;
    private Label indicationLabel;

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
        effectLabel.setWrapText(true);
        effectLabel.setMaxWidth(260);
        effectLabel.setStyle(
            "-fx-text-fill: #ffffff;" +
            "-fx-font-size: 11px;"
        );

        indicationLabel = new Label("");
        indicationLabel.setWrapText(true);
        indicationLabel.setMaxWidth(260);
        indicationLabel.setStyle(
            "-fx-text-fill: #b0bec5;" +
            "-fx-font-size: 10px;" +
            "-fx-font-style: italic;"
        );

        view = new VBox(4, header, nameLabel, effectLabel, indicationLabel);
        view.setAlignment(Pos.CENTER_LEFT);
        view.setPadding(new Insets(8, 14, 8, 14));
        view.setStyle(
            "-fx-background-color: #2a2a3e;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #ffe57f;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;"
        );
        view.setPrefWidth(280);
        view.setMinWidth(200);
        view.setVisible(false);
    }

    /** @param lucky when {@code true}, shows a “lucky card” hint (from engine {@code Card#isLucky()}). */
    public void showCard(String cardName, String effect, boolean lucky) {
        nameLabel.setText(cardName != null ? cardName : "Card");
        effectLabel.setText(effect != null && !effect.isEmpty() ? effect : "(No description)");
        if (lucky) {
            indicationLabel.setText("✦ Lucky card — higher rarity weight in the deck.");
            indicationLabel.setStyle(
                "-fx-text-fill: #ffd740;" +
                "-fx-font-size: 10px;" +
                "-fx-font-style: italic;"
            );
        } else {
            indicationLabel.setText("Effect applied on landing this cell.");
            indicationLabel.setStyle(
                "-fx-text-fill: #90caf9;" +
                "-fx-font-size: 10px;" +
                "-fx-font-style: italic;"
            );
        }
        view.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> view.setVisible(false));
        pause.play();
    }

    public void hide() {
        view.setVisible(false);
    }

    public VBox getView() { return view; }
}
