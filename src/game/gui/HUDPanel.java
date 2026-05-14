package game.gui;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import game.engine.monsters.Monster;

/**
 * HUD: turn counter, whose turn (you vs opponent), energies/positions/dice,
 * drawn-card strip + last-card summary, freeze badge.
 */
public class HUDPanel {

    private VBox         root;
    private HBox         bar;
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
            "-fx-font-weight: bold;"
        );

        scoresLabel = styledLabel("You / Opponent —");
        scoresLabel.setStyle(
            "-fx-text-fill: #ffe57f;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;"
        );

        lastCardSummary = new Label("Last card drawn: —");
        lastCardSummary.setWrapText(true);
        lastCardSummary.setMaxWidth(Double.MAX_VALUE);
        lastCardSummary.setStyle(
            "-fx-text-fill: #cfd8dc;" +
            "-fx-font-size: 11px;" +
            "-fx-padding: 0 0 0 2;"
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

        bar = new HBox(14,
            turnLabel,
            playerLabel,
            scoresLabel,
            spacer,
            cardDisplay.getView(),
            freezeLabel
        );
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(8, 14, 4, 14));

        root = new VBox(2, bar, lastCardSummary);
        root.setPadding(new Insets(4, 0, 6, 0));
        root.setStyle("-fx-background-color: #1e1e2e; -fx-border-color: #333855; -fx-border-width: 0 0 2 0;");
        root.setMinHeight(104);
        root.setPrefHeight(104);
    }

    public void nextTurn() {
        turnCount++;
        turnLabel.setText("Turn: " + turnCount);
    }

    /**
     * Shows who is acting now and whether that actor is the human's monster or the opponent's.
     */
    public void setTurnContext(Monster current, Monster humanPlayer, Monster opponent) {
        boolean yourTurn = current == humanPlayer;
        String tag = yourTurn ? "Your turn" : "Opponent's turn";
        playerLabel.setText("▶  " + tag + ": " + current.getName());
    }

    /** Energies, cell indices, last dice — first monster is always "You" from {@link Game#getPlayer()}. */
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

    /** Stays visible and is overwritten every time a new card is drawn (milestone: track each card). */
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
    public VBox getView() { return root; }

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
