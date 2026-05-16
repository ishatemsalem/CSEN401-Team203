package game.gui;

import game.engine.Game;
import game.engine.cards.Card;
import game.engine.monsters.Monster;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class ActionPanel {

    private Pane    view;
    private Label   diceResultLabel;
    private Label   hintLabel;
    private Button  rollButton;
    private Button  powerUpButton;
    private boolean powerUpActivated  = false;
    private boolean hasRolledThisTurn = false;

    private GameView gameView;
    private Game     game;
    private Main     mainApp;

    public ActionPanel(GameView gameView, Game game, Main mainApp) {
        this.gameView = gameView;
        this.game     = game;
        this.mainApp  = mainApp;

        powerUpButton = new Button("Activate Power-Up (optional)");
        powerUpButton.setPrefWidth(210);
        powerUpButton.setStyle(buttonStyle("#6a1b9a"));
        powerUpButton.setOnAction(e -> togglePowerUp());

        rollButton = new Button("Roll dice");
        rollButton.setPrefWidth(210);
        rollButton.setStyle(buttonStyle("#1565c0"));
        rollButton.setOnAction(e -> onRollDice());

        diceResultLabel = new Label("Dice: —");
        diceResultLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-effect: dropshadow(gaussian, black, 4, 0.8, 1, 1);" // Added drop shadow for visibility over board
        );

        hintLabel = new Label("Turn power-up ON if you want it, then roll.\nPower-up only applies before the dice move.");
        hintLabel.setWrapText(true);
        hintLabel.setMaxWidth(210);
        hintLabel.setStyle(
            "-fx-text-fill: #b0bec5; " +
            "-fx-font-size: 11px; " +
            "-fx-effect: dropshadow(gaussian, black, 2, 0.8, 1, 1);" // Added drop shadow
        );

        view = new Pane();
        // CRITICAL: Allows clicks to pass through the empty space of this Pane to the board below
        view.setPickOnBounds(false); 
        view.getChildren().addAll(powerUpButton, rollButton, diceResultLabel, hintLabel);

        // =========================================================
        //  POSITION CONTROLS: Move each element independently here
        // =========================================================
        
        // Power-Up Button Position
        powerUpButton.setLayoutX(20);
        powerUpButton.setLayoutY(400);

        // Roll Button Position
        rollButton.setLayoutX(20);
        rollButton.setLayoutY(450);

        // Dice Result Label Position
        diceResultLabel.setLayoutX(20);
        diceResultLabel.setLayoutY(500);

        // Hint Label Position
        hintLabel.setLayoutX(20);
        hintLabel.setLayoutY(550);
        
        // =========================================================
    }

    private void togglePowerUp() {
        if (hasRolledThisTurn) {
            ExceptionHandler.showInvalidPowerUp();
            return;
        }
        powerUpActivated = !powerUpActivated;
        if (powerUpActivated) {
            powerUpButton.setText("Power-up: ON (uses when you roll)");
            powerUpButton.setStyle(buttonStyle("#2e7d32"));
        } else {
            powerUpButton.setText("Activate Power-Up (optional)");
            powerUpButton.setStyle(buttonStyle("#6a1b9a"));
        }
    }

    private void onRollDice() {
        try {
            onRollDiceImpl();
        } catch (RuntimeException ex) {
            ExceptionHandler.showGenericError(
                "Unexpected problem during your turn.\n" + ex.getClass().getSimpleName() + ": " + ex.getMessage()
            );
            try {
                hasRolledThisTurn = false;
                rollButton.setDisable(false);
                powerUpButton.setDisable(false);
            } catch (RuntimeException ignored) {}
        }
    }

    private void onRollDiceImpl() {
        if (hasRolledThisTurn) {
            ExceptionHandler.showAlreadyRolled();
            return;
        }

        if (game.getCurrent().isFrozen()) {
            gameView.getHUD().showFreezeAndHide();
            try {
                game.playTurn();
            } catch (InvalidMoveException e) {
                ExceptionHandler.showInvalidMove(e.getMessage());
            }
            diceResultLabel.setText("Dice: — (frozen)");
            gameView.refreshAll();
            gameView.getHUD().nextTurn();
            return;
        }

        if (powerUpActivated) {
            try {
                game.usePowerup();
            } catch (OutOfEnergyException e) {
                ExceptionHandler.showNotEnoughEnergy();
                powerUpActivated = false;
                powerUpButton.setText("Activate Power-Up (optional)");
                powerUpButton.setStyle(buttonStyle("#6a1b9a"));
                return;
            }
            powerUpActivated = false;
            powerUpButton.setText("Activate Power-Up (optional)");
            powerUpButton.setStyle(buttonStyle("#6a1b9a"));
            gameView.refreshAll();
        }

        hasRolledThisTurn = true;
        rollButton.setDisable(true);
        powerUpButton.setDisable(true);

        try {
            game.playTurn();
            diceResultLabel.setText("Dice: " + game.getLastRoll());

            Card drawn = game.getLastDrawnCard();
            if (drawn != null) {
                gameView.getHUD().getCardDisplay().showCard(
                    drawn.getName(),
                    drawn.getDescription(),
                    drawn.isLucky()
                );
                gameView.getHUD().setLastCardSummary(drawn.getName(), drawn.getDescription());
            }
        } catch (InvalidMoveException e) {
            ExceptionHandler.showInvalidMove(e.getMessage());
            hasRolledThisTurn = false;
            rollButton.setDisable(false);
            powerUpButton.setDisable(false);
            return;
        }

        gameView.refreshAll();
        gameView.getHUD().nextTurn();

        Monster winner = game.getWinner();
        if (winner != null) {
            mainApp.showWinScreen(
                winner.getName(),
                winner.getRole().toString(),
                winner.getEnergy()
            );
            return;
        }

        resetForNewTurn();
    }

    public void resetForNewTurn() {
        hasRolledThisTurn = false;
        powerUpActivated  = false;
        rollButton.setDisable(false);
        powerUpButton.setDisable(false);
        rollButton.setText("Roll dice");
        powerUpButton.setText("Activate Power-Up (optional)");
        powerUpButton.setStyle(buttonStyle("#6a1b9a"));
        diceResultLabel.setText("Dice: —");
    }

    public Pane getView() { return view; }

    private String buttonStyle(String color) {
        return  "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, black, 5, 0.5, 0, 2);"; // Shadow for visibility
    }
}