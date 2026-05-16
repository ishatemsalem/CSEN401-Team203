package game.gui;

import game.engine.Board;
import game.engine.cells.*;
import game.engine.monsters.Monster;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

public class BoardView {

    private static final int GRID_COLS = 10;
    private static final int GRID_ROWS = 10;
 
    private static final String COLOR_PLAYER_TOKEN   = "#00e5ff"; 
    private static final String COLOR_OPPONENT_TOKEN = "#ff1744"; 
 
    private final StackPane wrapper;  
    private final StackPane boardAnchor; // Locks the grid and image together
    private final GridPane gridPane;
    private final CellView[][] cellViews = new CellView[GRID_ROWS][GRID_COLS];
 
    private Circle playerToken;
    private Circle opponentToken;
 
    public BoardView() {
        // 1. The outermost wrapper (Full screen area given by GameView)
        wrapper = new StackPane();
        wrapper.setAlignment(Pos.CENTER);
        
        // Load the room background (Fallback to dark gray if missing)
        try {
            Image bgImage = new Image(new File("assets/lobby/lobby_bg.png").toURI().toString());
            BackgroundSize coverSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, false, true);
            wrapper.setBackground(new Background(new BackgroundImage(bgImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, coverSize)));
        } catch (Exception e) {
            wrapper.setStyle("-fx-background-color: #121212;");
        }

        // 2. The Anchor Container (Maintains 5:4 ratio, 90% of screen height)
        boardAnchor = new StackPane();
        boardAnchor.setAlignment(Pos.CENTER);
        
        // Bind the anchor's height to 90% of the wrapper (leaving 5% top and bottom)
        boardAnchor.maxHeightProperty().bind(wrapper.heightProperty().multiply(0.9));
        // Force the 5:4 Width-to-Height ratio (5 / 4 = 1.25)
        boardAnchor.maxWidthProperty().bind(boardAnchor.maxHeightProperty().multiply(1.25));

        // 3. The Board Image (Matches the Anchor exactly)
        ImageView boardImage = new ImageView();
        try {
            boardImage.setImage(new Image(new File("assets/tex/board.png").toURI().toString()));
        } catch (Exception e) {
            System.out.println("Board texture missing.");
        }

        // FIX: Force JavaFX to respect the original image pixels
        boardImage.setPreserveRatio(true); 

        // FIX: Only bind the height. Let preserveRatio handle the width naturally!
        boardImage.fitHeightProperty().bind(boardAnchor.maxHeightProperty().multiply(0.9));
        
        // 4. The Grid (Transparent, overlaying the image perfectly)
        gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: transparent;");
        
        // Force cells to be exactly 10% of the board's width and height
        for (int i = 0; i < GRID_COLS; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(10);
            gridPane.getColumnConstraints().add(col);
            
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(10);
            gridPane.getRowConstraints().add(row);
        }

        buildEmptyGrid();
        initMonsterTokens();

        // Stack the image, then the invisible grid of cells on top
        boardAnchor.getChildren().addAll(boardImage, gridPane);
        wrapper.getChildren().add(boardAnchor);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(wrapper.widthProperty());
        clip.heightProperty().bind(wrapper.heightProperty());
        wrapper.setClip(clip);



// Push the entire table down by 20 pixels
boardAnchor.setTranslateY(-25); 
// Push the entire table to the right by 10 pixels
boardAnchor.setTranslateX(0);
// Nudge the invisible cell grid UP by 5 pixels (relative to the image)
gridPane.setTranslateY(0);
// Nudge the invisible cell grid LEFT by 8 pixels
gridPane.setTranslateX(0);
// Shift the underlying image down by 10 pixels
boardImage.setTranslateY(0);
// Shift the underlying image right by 5 pixels
boardImage.setTranslateX(0);

// Default was 0.9 (90% of screen height). 
// Change to 0.7 for a smaller table, or 1.0 to fill the height completely.
boardAnchor.maxHeightProperty().bind(wrapper.heightProperty().multiply(0.7));

    }
 
    public void updateBoard(Board board, Monster player, Monster opponent) {
        for (int index = 0; index < 100; index++) {
            Cell cell = board.getCell(index);             
            int[] rc  = displayRowCol(index);
            CellView cv = cellViews[rc[0]][rc[1]];
            cv.setCell(cell, index);
        }
        updateMonsterTokens(player, opponent);
    }
 
    public void showEnergyChange(int cellIndex, int delta) {
        int[] rc = displayRowCol(cellIndex);
        CellView cv = cellViews[rc[0]][rc[1]];
 
        String sign  = (delta >= 0) ? "+" : "";
        String color = (delta >= 0) ? "#00e676" : "#ff5252";
        cv.flashLabel(sign + delta, color);
    }
 
    public void showShieldBlock(int cellIndex) {
        int[] rc = displayRowCol(cellIndex);
        cellViews[rc[0]][rc[1]].flashLabel("⛨ BLOCKED", "#ffe57f");
    }
 
    public void markDoorExhausted(int cellIndex) {
        int[] rc = displayRowCol(cellIndex);
        cellViews[rc[0]][rc[1]].setExhausted(true);
    }
 
    public StackPane getView() { return wrapper; }  
 
    private void buildEmptyGrid() {
        for (int index = 0; index < 100; index++) {
            int[] rc = displayRowCol(index);
            // Size doesn't matter here anymore since ColumnConstraints forces the dimensions
            CellView cv = new CellView(index, 10); 
            
            // Make individual CellView panes transparent to see the board beneath them
            cv.getPane().setStyle("-fx-background-color: transparent;"); 
            
            cellViews[rc[0]][rc[1]] = cv;
            gridPane.add(cv.getPane(), rc[1], rc[0]);  
        }
    }
 
    private void initMonsterTokens() {
        playerToken   = makeToken(COLOR_PLAYER_TOKEN);
        opponentToken = makeToken(COLOR_OPPONENT_TOKEN);
    }
 
    private Circle makeToken(String hexColor) {
        Circle c = new Circle(15); // Slightly larger tokens to fit the scaling layout
        c.setFill(Color.web(hexColor));
        c.setStroke(Color.WHITE);
        c.setStrokeWidth(2);
        c.setMouseTransparent(true);
        return c;
    }
 
    private void updateMonsterTokens(Monster player, Monster opponent) {
        for (int r = 0; r < GRID_ROWS; r++)
            for (int c = 0; c < GRID_COLS; c++)
                cellViews[r][c].clearTokens();
 
        int[] pRC = displayRowCol(player.getPosition());
        cellViews[pRC[0]][pRC[1]].addToken(playerToken, "P");
 
        int[] oRC = displayRowCol(opponent.getPosition());
        cellViews[oRC[0]][oRC[1]].addToken(opponentToken, "O");
    }
 
    private int[] displayRowCol(int index) {
        int boardRow = index / 10;
        int boardCol = index % 10;
 
        int displayRow = (GRID_ROWS - 1) - boardRow;          
        int displayCol = (boardRow % 2 == 0) ? boardCol       
                                              : (9 - boardCol); 
        return new int[]{displayRow, displayCol};
    }
}