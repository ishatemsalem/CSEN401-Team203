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

        // Initialize with text overlaying the wooden plank part
        powerUpButton = new Button("Activate Power-Up");
        powerUpButton.setPrefSize(420, 90); 

        // Load your new custom image asset using relative project locations safely
        String powerUpImagePath = new java.io.File("assets/buttons/powerup_button.png").toURI().toString();

        // Apply JavaFX styles using your new image background
        powerUpButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background-image: url('" + powerUpImagePath + "');" + 
            "-fx-background-size: 100% 100%;" +
            "-fx-background-repeat: no-repeat;" +
            "-fx-background-position: center;" +
            "-fx-font-family: 'Jua';" +
            "-fx-font-size: 24px;" + 
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #3B2414;" + // Your matching dark brown text color
            "-fx-cursor: hand;" +
            "-fx-padding: 0 0 0 100;"   // Shifts text to the right so it doesn't overlap the "Optional" paper flap
        );

        // Click compression animations
        powerUpButton.setOnMousePressed(e -> {
            powerUpButton.setScaleX(0.95);
            powerUpButton.setScaleY(0.95);
        });

        powerUpButton.setOnMouseReleased(e -> {
            powerUpButton.setScaleX(1.0);
            powerUpButton.setScaleY(1.0);
        });
        powerUpButton.setOnAction(e -> togglePowerUp());

        rollButton = new Button("ROLL DICE");
        rollButton.setPrefSize(260, 90);
        String imagePath = new java.io.File("assets/buttons/button_base.png").toURI().toString();
        
        // Apply the styles
        rollButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background-image: url('" + imagePath + "');" + 
            "-fx-background-size: 100% 100%;" +
            "-fx-background-repeat: no-repeat;" +
            "-fx-background-position: center;" +
            "-fx-font-family: 'Jua';" +
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #3B2414;" + 
            "-fx-cursor: hand;" +
            "-fx-padding: 0;" 
        );

        // Press and release animations
        rollButton.setOnMousePressed(e -> {
            rollButton.setScaleX(0.95);
            rollButton.setScaleY(0.95);
        });

        rollButton.setOnMouseReleased(e -> {
            rollButton.setScaleX(1.0);
            rollButton.setScaleY(1.0);
        });

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
        if (gameView.getHUD().getCardDisplay().isWaitingForDraw()) {
            return; 
        }
        if (hasRolledThisTurn) {
            ExceptionHandler.showInvalidPowerUp();
            return;
        }
        
        powerUpActivated = !powerUpActivated;
        
        String powerUpImagePath = new java.io.File("assets/buttons/powerup_button.png").toURI().toString();
        
        if (powerUpActivated) {
            powerUpButton.setText("Power-Up: ON");
            powerUpButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-image: url('" + powerUpImagePath + "');" + 
                "-fx-background-size: 100% 100%;" +
                "-fx-background-repeat: no-repeat;" +
                "-fx-background-position: center;" +
                "-fx-font-family: 'Jua';" +
                "-fx-font-size: 24px;" + 
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1b5e20;" + // Dark forest green text color
                "-fx-cursor: hand;" +
                "-fx-padding: 0 0 0 100;"
            );
        } else {
            powerUpButton.setText("Activate Power-Up");
            powerUpButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-image: url('" + powerUpImagePath + "');" + 
                "-fx-background-size: 100% 100%;" +
                "-fx-background-repeat: no-repeat;" +
                "-fx-background-position: center;" +
                "-fx-font-family: 'Jua';" +
                "-fx-font-size: 24px;" + 
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #3B2414;" + // Default brown text color
                "-fx-cursor: hand;" +
                "-fx-padding: 0 0 0 100;"
            );
        }
    }

    private void onRollDice() {
        if (gameView.getHUD().getCardDisplay().isWaitingForDraw()) {
            ExceptionHandler.showInvalidAction("You must pick a card by pressing on the deck first!");
            return;
        }
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
            try {
                game.playTurn();
            } catch (InvalidMoveException e) {
                ExceptionHandler.showInvalidMove(e.getMessage());
            }
            diceResultLabel.setText("Dice: — (frozen)");
            
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
            gameView.refreshAll(true, null);
        }

        hasRolledThisTurn = true;
        rollButton.setDisable(true);
        powerUpButton.setDisable(true);

        Monster activeMonster = game.getCurrent();
        int startPos = activeMonster.getPosition();

        try {
            game.playTurn();
            int roll = game.getLastRoll();
            int intermediatePos = (startPos + roll) % 100;
            int endPos = activeMonster.getPosition();

            diceResultLabel.setText("Dice: " + roll);

            Card drawn = game.getLastDrawnCard();

            Runnable finaliseTurnProcess = () -> {
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
            };

            Runnable onFinishedMovementHop = () -> {
                if (drawn != null) {
                    gameView.getHUD().setLastCardSummary(drawn.getName(), drawn.getDescription());
                    gameView.getHUD().getCardDisplay().prepareDeckForDraw(drawn.getName(), finaliseTurnProcess);
                } else {
                    finaliseTurnProcess.run();
                }
            };

            if (!skipAnimations) {
                gameView.animateAndRefreshTurn(startPos, roll, intermediatePos, endPos, activeMonster, onFinishedMovementHop);
            } else {
                gameView.refreshAll(true, onFinishedMovementHop);
            }
        } catch (InvalidMoveException e) {
            ExceptionHandler.showInvalidMove(e.getMessage());
            resetForNewTurn();
        }
    }

    public void resetForNewTurn() {
        hasRolledThisTurn = false;
        powerUpActivated  = false;
        rollButton.setDisable(false);
        powerUpButton.setDisable(false);
        rollButton.setText("ROLL DICE");
        powerUpButton.setText("Activate Power-Up (optional)");
        
        // Maintain image backing when resetting state
        String powerUpImagePath = new java.io.File("assets/buttons/powerup_base.png").toURI().toString();
        powerUpButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background-image: url('" + powerUpImagePath + "');" + 
            "-fx-background-size: 100% 100%;" +
            "-fx-background-repeat: no-repeat;" +
            "-fx-background-position: center;" +
            "-fx-font-family: 'Jua';" +
            "-fx-font-size: 24px;" + 
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #3B2414;" + 
            "-fx-cursor: hand;" +
            "-fx-padding: 0 0 0 100;"
        );
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