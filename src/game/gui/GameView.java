package game.gui;

import game.engine.Game;
import game.engine.monsters.Monster;
import javafx.scene.layout.BorderPane;

/**
 * GameView — the main game screen.
 * Assembles HUDPanel (top), ActionPanel (left), and BoardView (center).
 * Holds the real Game engine object and refreshes all panels after every action.
 */
public class GameView {

    private BorderPane    root;
    private BoardView     boardView;
    private HUDPanel      hudPanel;
    private ActionPanel   actionPanel;
    private Game          game;

    // ── Constructor ─────────────────────────────────────────────────────────
    public GameView(Game game, Main mainApp) {
        this.game = game;

        boardView   = new BoardView();
        hudPanel    = new HUDPanel();
        actionPanel = new ActionPanel(this, game, mainApp);

        root = new BorderPane();
        root.setStyle("-fx-background-color: #111111;");
        root.setTop(hudPanel.getView());
        root.setCenter(boardView.getView());
        root.setLeft(actionPanel.getView());

        // First board render on startup
        refreshAll();
    }

    // ── PUBLIC API ───────────────────────────────────────────────────────────

    /**
     * Call this after EVERY action (roll, powerup, card drawn, etc.)
     * to sync the board and HUD with the current engine state.
     */
    public void refreshAll() {
        // Sync board visuals with engine state
       
        boardView.updateBoard(
            game.getBoard(),
            game.getPlayer(),
            game.getOpponent()
        );

        // Sync HUD with engine state
        Monster current = game.getCurrent();
        hudPanel.setCurrentPlayer(current.getName());
        hudPanel.setFrozen(current.isFrozen());
    }

    // ── GETTERS ──────────────────────────────────────────────────────────────
    public BorderPane getView()      { return root;        }
    public BoardView  getBoardView() { return boardView;   }
    public HUDPanel   getHUD()       { return hudPanel;    }
    public Game       getGame()      { return game;        }
}
