package game.gui;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Top bar: turn, current player, energies/positions, card strip, freeze hint.
 */
public class HUDPanel {

    private HBox         view;
    private Label        turnLabel;
    private Label        playerLabel;
    private Label        scoresLabel;
    private Label        freezeLabel;
    private CardDisplay  cardDisplay;

    private int turnCount = 1;

    public HUDPanel() {
        turnLabel = styledLabel("Turn: 1");

        playerLabel = styledLabel("▶  Loading...");
        playerLabel.setStyle(
            "-fx-text-fill: #00e5ff;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;"
        );

        scoresLabel = styledLabel("Energy —  |  Pos —");
        scoresLabel.setStyle(
            "-fx-text-fill: #ffe57f;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;"
        );

        freezeLabel = new Label("❄  FROZEN");
        freezeLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-background-color: #0077b6;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 6 12 6 12;" +
            "-fx-background-radius: 6;"
        );
        freezeLabel.setVisible(false);

        cardDisplay = new CardDisplay();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        view = new HBox(16,
            turnLabel,
            playerLabel,
            scoresLabel,
            spacer,
            cardDisplay.getView(),
            freezeLabel
        );
        view.setAlignment(Pos.CENTER_LEFT);
        view.setPadding(new Insets(10, 16, 10, 16));
        view.setStyle("-fx-background-color: #1e1e2e; -fx-border-color: #333855; -fx-border-width: 0 0 2 0;");
        view.setMinHeight(72);
        view.setMaxHeight(72);
    }

    public void nextTurn() {
        turnCount++;
        turnLabel.setText("Turn: " + turnCount);
    }

    public void setCurrentPlayer(String name) {
        playerLabel.setText("▶  " + name + "'s Turn");
    }

    /**
     * Live score line: both monsters' energy and board index (0–99).
     */
    public void setScores(String pName, int pEnergy, int pPos,
                          String oName, int oEnergy, int oPos) {
        String shortP = shorten(pName, 14);
        String shortO = shorten(oName, 14);
        scoresLabel.setText(
            shortP + ": " + pEnergy + " (cell " + pPos + ")   |   " +
            shortO + ": " + oEnergy + " (cell " + oPos + ")"
        );
    }

    public void setFrozen(boolean frozen) {
        freezeLabel.setVisible(frozen);
    }

    public void showFreezeAndHide() {
        freezeLabel.setVisible(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> freezeLabel.setVisible(false));
        pause.play();
    }

    public CardDisplay getCardDisplay() { return cardDisplay; }
    public int getTurnCount() { return turnCount; }
    public HBox getView() { return view; }

    private static String shorten(String s, int max) {
        if (s == null) return "?";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private Label styledLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #eceff1; -fx-font-size: 14px;");
        return l;
    }
}
