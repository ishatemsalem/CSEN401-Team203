package game.gui;

import game.engine.Board;
import game.engine.cells.*;
import game.engine.monsters.Monster;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;


public class BoardView {

    // ── Visual sizing constants (tweak freely) ──────────────────────────────
    /** Sized so 10×10 grid + HUD fits in 720px height without overlapping side/top bars. */
    private static final int CELL_SIZE   = 56;
    private static final int GRID_COLS   = 10;
    private static final int GRID_ROWS   = 10;
 
 
    // ── Monster token colours ───────────────────────────────────────────────
    private static final String COLOR_PLAYER_TOKEN   = "#00e5ff"; // cyan
    private static final String COLOR_OPPONENT_TOKEN = "#ff1744"; // hot-red
 
    // ── Internal state ──────────────────────────────────────────────────────
    private final StackPane wrapper;  
    private final GridPane gridPane;
    private final CellView[][] cellViews = new CellView[GRID_ROWS][GRID_COLS];
 
    // Keep references to the two monster tokens so we can move them around
    private Circle playerToken;
    private Circle opponentToken;
 
    // ── Constructor ─────────────────────────────────────────────────────────
    public BoardView() {
        gridPane = new GridPane();
        gridPane.setHgap(2);
        gridPane.setVgap(2);
        gridPane.setStyle("-fx-background-color: #1c1c1c; -fx-padding: 8;");
 
        buildEmptyGrid();
        initMonsterTokens();
        
     // wrap the grid so it centers inside whatever scene it's placed in
        wrapper = new StackPane(gridPane);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle("-fx-background-color: #121212;");
        // Keep board paint inside the BorderPane center slot (was overflowing and covering HUD / actions).
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(wrapper.widthProperty());
        clip.heightProperty().bind(wrapper.heightProperty());
        wrapper.setClip(clip);
    }
 
    // ────────────────────────────────────────────────────────────────────────
    //  PUBLIC API  (called by GameView / a controller after each turn)
    // ────────────────────────────────────────────────────────────────────────
 
    /**
     * Full board refresh – call this once after the Board is initialised
     * AND after every turn to sync visuals with engine state.
     *
     * @param board   the live Board object from the engine
     * @param player  the current player's Monster
     * @param opponent the opponent's Monster
     */
    public void updateBoard(Board board, Monster player, Monster opponent) {
        for (int index = 0; index < 100; index++) {
            Cell cell = board.getCell(index);             // NOTE: getCell must be made package-visible      -----> i changed it to public im not sure whether i can do that or not, need a fix for this
            int[] rc  = displayRowCol(index);
            CellView cv = cellViews[rc[0]][rc[1]];
            cv.setCell(cell, index);
        }
        updateMonsterTokens(player, opponent);
    }
 
    /**
     * Show a floating "+X" or "-X" energy label over a cell, then fade out.
     * Call this whenever any energy change happens to monsters on the board.
     *
     * @param cellIndex board index (0–99) of the affected cell
     * @param delta     signed energy change (positive = gain, negative = loss)
     */
    public void showEnergyChange(int cellIndex, int delta) {
        int[] rc = displayRowCol(cellIndex);
        CellView cv = cellViews[rc[0]][rc[1]];
 
        String sign  = (delta >= 0) ? "+" : "";
        String color = (delta >= 0) ? "#00e676" : "#ff5252";
        cv.flashLabel(sign + delta, color);
    }
 
    /**
     * Flash a ⛨ shield-block indicator over a cell.
     * Call when a shield absorbs an energy loss.
     */
    public void showShieldBlock(int cellIndex) {
        int[] rc = displayRowCol(cellIndex);
        cellViews[rc[0]][rc[1]].flashLabel("⛨ BLOCKED", "#ffe57f");
    }
 
    /**
     * Mark a door cell as activated/exhausted visually.
     *
     * @param cellIndex board index of the door
     */
    public void markDoorExhausted(int cellIndex) {
        int[] rc = displayRowCol(cellIndex);
        cellViews[rc[0]][rc[1]].setExhausted(true);
    }
 
    /** Returns the GridPane so GameView can embed it in the scene. */
    public StackPane getView() { return wrapper; }  // was: return gridPane
 
    // ────────────────────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ────────────────────────────────────────────────────────────────────────
 
    /** Build all 100 CellView placeholders and add them to the GridPane. */
    private void buildEmptyGrid() {
        for (int index = 0; index < 100; index++) {
            int[] rc = displayRowCol(index);
            CellView cv = new CellView(index, CELL_SIZE);
            cellViews[rc[0]][rc[1]] = cv;
            gridPane.add(cv.getPane(), rc[1], rc[0]);  // GridPane.add(node, col, row)
        }
    }
 
    /** Create the two circular monster tokens (not yet placed on the grid). */
    private void initMonsterTokens() {
        playerToken   = makeToken(COLOR_PLAYER_TOKEN);
        opponentToken = makeToken(COLOR_OPPONENT_TOKEN);
    }
 
    private Circle makeToken(String hexColor) {
        Circle c = new Circle(10);
        c.setFill(Color.web(hexColor));
        c.setStroke(Color.WHITE);
        c.setStrokeWidth(2);
        c.setMouseTransparent(true);
        return c;
    }
 
    /**
     * Move monster tokens to their current board positions.
     * Removes old tokens from whatever cell they were in, then re-adds them.
     */
    private void updateMonsterTokens(Monster player, Monster opponent) {
        // Clear all token overlays first
        for (int r = 0; r < GRID_ROWS; r++)
            for (int c = 0; c < GRID_COLS; c++)
                cellViews[r][c].clearTokens();
 
        // Place player token
        int[] pRC = displayRowCol(player.getPosition());
        cellViews[pRC[0]][pRC[1]].addToken(playerToken, "P");
 
        // Place opponent token
        int[] oRC = displayRowCol(opponent.getPosition());
        cellViews[oRC[0]][oRC[1]].addToken(opponentToken, "O");
    }
 
    /**
     * Convert a linear board index (0–99) into [displayRow, displayCol]
     * for use with JavaFX GridPane.
     *
     * Board row 0 (cells 0–9) is shown at the BOTTOM of the grid (GridPane row 9).
     * Even board-rows go left→right; odd board-rows go right→left.
     */
    private int[] displayRowCol(int index) {
        int boardRow = index / 10;
        int boardCol = index % 10;
 
        int displayRow = (GRID_ROWS - 1) - boardRow;          // flip: row 0 = bottom
        int displayCol = (boardRow % 2 == 0) ? boardCol       // even row: L→R
                                              : (9 - boardCol); // odd row:  R→L
        return new int[]{displayRow, displayCol};
    }
}
