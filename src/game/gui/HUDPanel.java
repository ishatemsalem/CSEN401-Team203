package game.gui;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.CacheHint;
import javafx.util.Duration;

import game.engine.monsters.Monster;

public class HUDPanel {

    private Pane         root;
    private Label        turnLabel;
    private Label        playerLabel;
    private Label        scoresLabel;
    private Label        lastCardSummary;
    private Label        freezeLabel;
    private CardDisplay  cardDisplay;

    private int turnCount = 1;

    public HUDPanel() {
        turnLabel = styledLabel("Turn: 1");

        playerLabel = styledLabel("▶  Loading...");
        playerLabel.setStyle(
            "-fx-text-fill: #00e5ff;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-effect: dropshadow(gaussian, black, 3, 0.8, 1, 1);"
        );

        scoresLabel = styledLabel("You / Opponent —");
        scoresLabel.setStyle(
            "-fx-text-fill: #ffe57f;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-effect: dropshadow(gaussian, black, 3, 0.8, 1, 1);"
        );

        lastCardSummary = new Label("Last card drawn: —");
        lastCardSummary.setWrapText(true);
        lastCardSummary.setMaxWidth(400); // Prevent text from running off-screen
        lastCardSummary.setStyle(
            "-fx-text-fill: #cfd8dc;" +
            "-fx-font-size: 11px;" +
            "-fx-effect: dropshadow(gaussian, black, 3, 0.8, 1, 1);"
        );

        freezeLabel = new Label("❄  FROZEN");
        freezeLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-background-color: #0077b6;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 6 12 6 12;" +
            "-fx-background-radius: 6;" +
            "-fx-effect: dropshadow(gaussian, black, 4, 0.5, 0, 2);"
        );
        freezeLabel.setVisible(false);

        cardDisplay = new CardDisplay();

        root = new Pane();
        root.setPickOnBounds(false); // Clicks pass through empty space to the board
        
        // FIX FOR START-GAME LAG: Cache the complex text and shadows as a hardware bitmap
        root.setCache(true);
        root.setCacheHint(CacheHint.SPEED);

        root.getChildren().addAll(
            turnLabel, playerLabel, scoresLabel, 
            lastCardSummary, freezeLabel, cardDisplay.getView()
        );

        // =========================================================
        //  POSITION CONTROLS: Move each element independently here
        // =========================================================
        
        turnLabel.setLayoutX(20);
        turnLabel.setLayoutY(20);

        playerLabel.setLayoutX(20);
        playerLabel.setLayoutY(50);

        scoresLabel.setLayoutX(20);
        scoresLabel.setLayoutY(80);

        lastCardSummary.setLayoutX(20);
        lastCardSummary.setLayoutY(110);

        freezeLabel.setLayoutX(20);
        freezeLabel.setLayoutY(140);

        cardDisplay.getView().setLayoutX(900);
        cardDisplay.getView().setLayoutY(20);
        
        // =========================================================
    }

    public void nextTurn() {
        turnCount++;
        turnLabel.setText("Turn: " + turnCount);
    }

    public void setTurnContext(Monster current, Monster humanPlayer, Monster opponent) {
        boolean yourTurn = current == humanPlayer;
        String tag = yourTurn ? "Your turn" : "Opponent's turn";
        playerLabel.setText("▶  " + tag + ": " + current.getName());
    }

    public void setScores(String youName, int youEnergy, int youPos,
                          String oppName, int oppEnergy, int oppPos, int lastDice) {
        String y = shorten(youName, 12);
        String o = shorten(oppName, 12);
        String diceTxt = (lastDice >= 1 && lastDice <= 6) ? String.valueOf(lastDice) : "—";
        scoresLabel.setText(
            "You " + y + ": " + youEnergy + " @cell " + youPos +
            "  |  Opponent " + o + ": " + oppEnergy + " @cell " + oppPos +
            "  |  Last dice: " + diceTxt
        );
    }

    public void setLastCardSummary(String cardName, String effectText) {
        String name = cardName != null ? cardName : "—";
        String eff = effectText != null ? effectText : "";
        if (eff.length() > 72) {
            eff = eff.substring(0, 71) + "…";
        }
        lastCardSummary.setText("Last card drawn: " + name + " — " + (eff.isEmpty() ? "(no description)" : eff));
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
    public Pane getView() { return root; } // Changed return type to Pane

    private static String shorten(String s, int max) {
        if (s == null) return "?";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private Label styledLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #eceff1; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, black, 3, 0.8, 1, 1);");
        return l;
    }
}