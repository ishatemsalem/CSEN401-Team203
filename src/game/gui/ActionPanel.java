package game.gui;

import game.engine.Game;
import game.engine.cards.Card;
import game.engine.monsters.Monster;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class ActionPanel {

    private Pane     view;
    private Label    diceResultLabel;
    private Label    hintLabel;
    private Button   rollButton;
    private Button   powerUpButton;
    private CheckBox skipAnimBox;
    
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
            "-fx-font-family: 'Jua', sans-serif;" +
            "-fx-font-size: 26px;" +
            "-fx-effect: dropshadow(gaussian, black, 4, 0.8, 1, 1);" 
        );

        hintLabel = new Label("Turn power-up ON if you want it, then roll.\nPower-up only applies before the dice move.");
        hintLabel.setWrapText(true);
        hintLabel.setMaxWidth(210);
        hintLabel.setStyle(
            "-fx-text-fill: #b0bec5; " +
            "-fx-font-family: 'Jua', sans-serif;" +
            "-fx-font-size: 12px; " +
            "-fx-effect: dropshadow(gaussian, black, 2, 0.8, 1, 1);" 
        );

        skipAnimBox = new CheckBox("Skip Animations");
        skipAnimBox.setStyle(
            "-fx-text-fill: #b0bec5; " +
            "-fx-font-family: 'Jua', sans-serif; " +
            "-fx-font-size: 15px; " +
            "-fx-effect: dropshadow(gaussian, black, 2, 0.8, 1, 1);"
        );

        view = new Pane();
        view.setPickOnBounds(false); 
        view.getChildren().addAll(powerUpButton, rollButton, diceResultLabel, hintLabel, skipAnimBox);
        
        powerUpButton.setLayoutX(20);
        powerUpButton.setLayoutY(400);

        rollButton.setLayoutX(20);
        rollButton.setLayoutY(450);

        diceResultLabel.setLayoutX(20);
        diceResultLabel.setLayoutY(500);

        hintLabel.setLayoutX(20);
        hintLabel.setLayoutY(550);

        // Pin checkbox dynamically to the bottom right of the screen
        skipAnimBox.layoutXProperty().bind(view.widthProperty().subtract(180));
        skipAnimBox.layoutYProperty().bind(view.heightProperty().subtract(40));
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
            resetForNewTurn();
        }
    }

    private void onRollDiceImpl() {
        if (hasRolledThisTurn) {
            ExceptionHandler.showAlreadyRolled();
            return;
        }

        boolean skipAnimations = skipAnimBox.isSelected();

        if (game.getCurrent().isFrozen()) {
            // REMOVED: gameView.getHUD().showFreezeAndHide(); 
            // The HUD now handles the freeze overlay directly via gameView.refreshAll()
            try {
                game.playTurn();
            } catch (InvalidMoveException e) {
                ExceptionHandler.showInvalidMove(e.getMessage());
            }
            diceResultLabel.setText("Dice: — (frozen)");
            
            // Sync freeze with board instantly
            gameView.refreshAll(skipAnimations, () -> {
                gameView.getHUD().nextTurn();
                resetForNewTurn();
            });
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
            // Power-ups hit instantly before roll, refresh instantly (skip animation)
            gameView.refreshAll(true, null);
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
            resetForNewTurn();
            return;
        }

        // Apply visual updates with bobbing animation. 
        // When the animation is FULLY COMPLETE, reset the UI and next turn.
        gameView.refreshAll(skipAnimations, () -> {
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
        });
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
                "-fx-font-family: 'Jua', sans-serif;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, black, 5, 0.5, 0, 2);";
    }
}