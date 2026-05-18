package game.gui;

import game.engine.Game;
import game.engine.monsters.Monster;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class GameView {

    private final StackPane layerRoot;
    private final BorderPane gamePane;
    private final BoardView  boardView;
    private final HUDPanel   hudPanel;
    private final ActionPanel actionPanel;
    private final Game       game;
    private final Main       mainApp;

    public GameView(Game game, Main mainApp) {
        this.game = game;
        this.mainApp = mainApp;

        boardView   = new BoardView();
        hudPanel    = new HUDPanel();
        actionPanel = new ActionPanel(this, game, mainApp);

        gamePane = new BorderPane();
        gamePane.setStyle("-fx-background-color: transparent;");
        gamePane.setCenter(boardView.getView());

        layerRoot = new StackPane();
        layerRoot.getChildren().addAll(gamePane, actionPanel.getView(), hudPanel.getView());
        ExceptionHandler.attachToGameLayer(layerRoot);

        // CHEAT KYEYS
        layerRoot.setFocusTraversable(true);
        layerRoot.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.W) {
                game.getPlayer().setPosition(99);
                refreshAll(true, this::checkWin);
            } else if (e.getCode() == javafx.scene.input.KeyCode.E) {
                game.getPlayer().setEnergy(game.getPlayer().getEnergy() + 500);
                refreshAll(true, this::checkWin);
            }
        });

        // Request focus so key events are caught immediately
        javafx.application.Platform.runLater(layerRoot::requestFocus);

        refreshAll(true, null);
    }

    private void checkWin() {
        Monster winner = game.getWinner();
        if (winner != null) {
            mainApp.showWinScreen(winner.getName(), winner.getRole().toString(), winner.getEnergy());
        }
    }

    public void refreshAll() {
        refreshAll(true, null);
    }

    public void refreshAll(boolean skipAnimation, Runnable onAnimationComplete) {
        try {
            Monster current = game.getCurrent();
            Monster player = game.getPlayer();
            Monster opponent = game.getOpponent();
            
            hudPanel.updateInfo(current, player, opponent, game.getLastRoll());

            boardView.updateBoard(
                game.getBoard(),
                player,
                opponent,
                skipAnimation,
                onAnimationComplete
            );

        } catch (RuntimeException ex) {
            ExceptionHandler.showGenericError(
                "Could not refresh the board or HUD.\n" + ex.getClass().getSimpleName() + ": " + ex.getMessage()
            );
            if (onAnimationComplete != null) onAnimationComplete.run();
        }
    }

    public StackPane getView()      { return layerRoot; }
    public BoardView getBoardView() { return boardView; }
    public HUDPanel  getHUD()       { return hudPanel; }
    public Game      getGame()      { return game; }
}