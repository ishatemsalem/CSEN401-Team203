package game.gui;

import game.engine.Game;
import game.engine.monsters.Monster;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * ActionPanel — the left-side control panel the player interacts with each turn.
 *
 * Contains:
 *  - Power-Up toggle button (must be pressed BEFORE rolling)
 *  - Roll Dice button
 *  - Dice result label
 *
 * Guards:
 *  - Cannot roll twice in one turn
 *  - Cannot activate power-up after rolling
 *  - Cannot roll if frozen (engine skips turn automatically)
 *  - Power-up throws OutOfEnergyException if not enough energy
 *
 * All invalid actions show a popup via ExceptionHandler.
 * No popup closes the game.
 */
public class ActionPanel {

    private VBox    view;
    private Label   diceResultLabel;
    private Button  rollButton;
    private Button  powerUpButton;
    private boolean powerUpActivated  = false;
    private boolean hasRolledThisTurn = false;

    private GameView gameView;
    private Game     game;
    private Main     mainApp;

    // ── Constructor ─────────────────────────────────────────────────────────
    public ActionPanel(GameView gameView, Game game, Main mainApp) {
        this.gameView = gameView;
        this.game     = game;
        this.mainApp  = mainApp;

        // ── Power-Up Button ──────────────────────────────────────────────────
        powerUpButton = new Button("Activate Power-Up");
        powerUpButton.setPrefWidth(190);
        powerUpButton.setStyle(buttonStyle("#6a1b9a"));
        powerUpButton.setOnAction(e -> togglePowerUp());

        // ── Roll Dice Button ─────────────────────────────────────────────────
        rollButton = new Button("🎲  Roll Dice");
        rollButton.setPrefWidth(190);
        rollButton.setStyle(buttonStyle("#1565c0"));
        rollButton.setOnAction(e -> onRollDice());

        // ── Dice Result Label ────────────────────────────────────────────────
        diceResultLabel = new Label("Dice: —");
        diceResultLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;"
        );

        // ── Layout ───────────────────────────────────────────────────────────
        view = new VBox(20, powerUpButton, rollButton, diceResultLabel);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(24));
        view.setStyle("-fx-background-color: #1a1a2e;");
        view.setPrefWidth(210);
    }

    // ── PRIVATE: POWER-UP TOGGLE ─────────────────────────────────────────────

    private void togglePowerUp() {

        // Guard — cannot activate after rolling
        if (hasRolledThisTurn) {
            ExceptionHandler.showInvalidPowerUp();
            return;
        }

        if (!powerUpActivated) {
            // Try to activate via engine — throws OutOfEnergyException if < 500
            try {
                game.usePowerup();
                powerUpActivated = true;
                powerUpButton.setText("✔  Power-Up ON");
                powerUpButton.setStyle(buttonStyle("#2e7d32")); // green = active
                gameView.refreshAll(); // HUD energies after spending / effect
            } catch (OutOfEnergyException e) {
                ExceptionHandler.showNotEnoughEnergy();
            }
        } else {
            // Deactivate (player changed their mind before rolling)
            powerUpActivated = false;
            powerUpButton.setText("Activate Power-Up");
            powerUpButton.setStyle(buttonStyle("#6a1b9a")); // purple = inactive
            gameView.refreshAll();
        }
    }

    // ── PRIVATE: ROLL DICE ───────────────────────────────────────────────────

    private void onRollDice() {

        // Guard — cannot roll twice
        if (hasRolledThisTurn) {
            ExceptionHandler.showAlreadyRolled();
            return;
        }

        // ── Frozen case ──────────────────────────────────────────────────────
        // If current monster is frozen, engine skips the turn automatically
        if (game.getCurrent().isFrozen()) {
            gameView.getHUD().showFreezeAndHide();
            try {
                game.playTurn(); // engine detects frozen → skips + unfreezes
            } catch (InvalidMoveException e) {
                ExceptionHandler.showInvalidMove(e.getMessage());
            }
            diceResultLabel.setText("Dice: — (frozen skip)");
            gameView.refreshAll();
            gameView.getHUD().nextTurn();
            gameView.getHUD().setCurrentPlayer(game.getCurrent().getName());
            // No need to disable buttons — turn already switched
            return;
        }

        // ── Normal roll ──────────────────────────────────────────────────────
        hasRolledThisTurn = true;
        rollButton.setDisable(true);
        powerUpButton.setDisable(true); // lock power-up after rolling

        try {
            game.playTurn(); // engine: rolls dice + moves + triggers cell effects

            diceResultLabel.setText("Dice: " + game.getLastRoll());

            // ── Check if a card was drawn ────────────────────────────────────
            // TODO: ask engine team for game.getLastDrawnCard() getter
            // Card drawn = game.getLastDrawnCard();
            // if (drawn != null) {
            //     gameView.getHUD().getCardDisplay()
            //             .showCard(drawn.getName(), drawn.getDescription());
            // }

        } catch (InvalidMoveException e) {
            // Engine reverts move automatically — just show popup
            ExceptionHandler.showInvalidMove(e.getMessage());
            // Re-enable buttons so player can try again
            hasRolledThisTurn = false;
            rollButton.setDisable(false);
            powerUpButton.setDisable(false);
            return;
        }

        // ── Sync board and HUD ───────────────────────────────────────────────
        gameView.refreshAll();
        gameView.getHUD().nextTurn();
        gameView.getHUD().setCurrentPlayer(game.getCurrent().getName());

        // ── Check win condition ──────────────────────────────────────────────
        Monster winner = game.getWinner();
        if (winner != null) {
            mainApp.showWinScreen(
                winner.getName(),
                winner.getRole().toString(),
                winner.getEnergy()
            );
            return;
        }

        // Reset for the next player's turn
        resetForNewTurn();
    }

    // ── PUBLIC: RESET BETWEEN TURNS ─────────────────────────────────────────

    /**
     * Call this at the start of each new turn to re-enable buttons
     * and reset all per-turn state.
     */
    public void resetForNewTurn() {
        hasRolledThisTurn = false;
        powerUpActivated  = false;
        rollButton.setDisable(false);
        powerUpButton.setDisable(false);
        rollButton.setText("🎲  Roll Dice");
        powerUpButton.setText("Activate Power-Up");
        powerUpButton.setStyle(buttonStyle("#6a1b9a"));
        diceResultLabel.setText("Dice: —");
    }

    /** Returns the VBox node to embed in GameView. */
    public VBox getView() { return view; }

    // ── PRIVATE HELPERS ──────────────────────────────────────────────────────
    private String buttonStyle(String color) {
        return  "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;";
    }
}
