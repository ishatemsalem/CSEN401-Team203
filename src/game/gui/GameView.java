package game.gui;

import game.engine.Game;
import game.engine.monsters.Monster;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * GameView — full play screen: HUD (top), actions (left), board (center),
 * plus an overlay layer for inline messages (see {@link ExceptionHandler}).
 */
public class GameView {

    private final StackPane layerRoot;
    private final BorderPane gamePane;
    private final BoardView  boardView;
    private final HUDPanel   hudPanel;
    private final ActionPanel actionPanel;
    private final Game       game;
    private final MonsterInfo Info1;
    private final MonsterInfo Info2;

    public GameView(Game game, Main mainApp) {
        this.game = game;

        boardView   = new BoardView();
        hudPanel    = new HUDPanel();
        actionPanel = new ActionPanel(this, game, mainApp);

        gamePane = new BorderPane();
        gamePane.setStyle("-fx-background-color: #111111;");

        gamePane.setTop(hudPanel.getView());
        gamePane.setLeft(actionPanel.getView());
        gamePane.setCenter(boardView.getView());
        BorderPane.setMargin(boardView.getView(), new Insets(0, 6, 8, 6));

        hudPanel.getView().prefWidthProperty().bind(gamePane.widthProperty());

        actionPanel.getView().setMinWidth(220);
        actionPanel.getView().setPrefWidth(230);

        layerRoot = new StackPane();
        layerRoot.getChildren().add(gamePane);
        ExceptionHandler.attachToGameLayer(layerRoot);

        Info1 = new MonsterInfo("YOU", "#00e5ff");
        Info2 = new MonsterInfo("OPPONENT", "#ff1744");
        VBox rightSidebar = new VBox(20, Info1.getView(), Info2.getView());
        rightSidebar.setPadding(new Insets(10));
        gamePane.setRight(rightSidebar);
        
        refreshAll();
    }

    public void refreshAll() {
        try {
            boardView.updateBoard(
                game.getBoard(),
                game.getPlayer(),
                game.getOpponent()
            );

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
            Info1.refresh(game.getPlayer());
            Info2.refresh(game.getOpponent());
            
        } catch (RuntimeException ex) {
            ExceptionHandler.showGenericError(
                "Could not refresh the board or HUD.\n" + ex.getClass().getSimpleName() + ": " + ex.getMessage()
            );
        }
    }

    public StackPane getView()      { return layerRoot; }
    public BoardView getBoardView() { return boardView; }
    public HUDPanel  getHUD()       { return hudPanel; }
    public Game      getGame()      { return game; }
}
