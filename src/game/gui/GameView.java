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

    public GameView(Game game, Main mainApp) {
        this.game = game;

        boardView   = new BoardView();
        hudPanel    = new HUDPanel();
        actionPanel = new ActionPanel(this, game, mainApp);

        gamePane = new BorderPane();
        gamePane.setStyle("-fx-background-color: transparent;");

        gamePane.setCenter(boardView.getView());

        layerRoot = new StackPane();
        
        layerRoot.getChildren().addAll(gamePane, actionPanel.getView(), hudPanel.getView());
        ExceptionHandler.attachToGameLayer(layerRoot);

        // Initial setup instantly
        refreshAll(true, null);
    }

    public void refreshAll() {
        refreshAll(true, null);
    }

    public void refreshAll(boolean skipAnimation, Runnable onAnimationComplete) {
        try {
            Monster current = game.getCurrent();
            Monster player = game.getPlayer();
            Monster opponent = game.getOpponent();
            hudPanel.setTurnContext(current, player, opponent);
            hudPanel.setFrozen(current.isFrozen());
            hudPanel.setScores(
                player.getName(), player.getEnergy(), player.getPosition(),
                opponent.getName(), opponent.getEnergy(), opponent.getPosition(),
                game.getLastRoll()
            );

            // Update board triggers the animations and runs the callback when finished
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