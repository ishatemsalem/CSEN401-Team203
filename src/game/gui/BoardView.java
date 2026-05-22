package game.gui;

import game.engine.Board;
import game.engine.cells.*;
import game.engine.monsters.Monster;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.File;

public class BoardView {

    private static final double OVERALL_BOARD_HEIGHT_PCT = 0.8; 
    private static final double ENTIRE_BOARD_OFFSET_X    = 0.0;  
    private static final double ENTIRE_BOARD_OFFSET_Y    = -0.03; 
    
    private static final double IMAGE_SCALE_PCT      = 1.08;  
    private static final double IMAGE_OFFSET_X_PCT   = 0.0;  
    private static final double IMAGE_OFFSET_Y_PCT   = 0.03;  

    private static final double GRID_WIDTH_PCT       = 1.01; 
    private static final double GRID_HEIGHT_PCT      = 0.90; 
    private static final double GRID_OFFSET_X_PCT    = 0.0;  
    private static final double GRID_OFFSET_Y_PCT    = 0.03;  

    private static final int GRID_COLS = 10;
    private static final int GRID_ROWS = 10;
 
    private final StackPane wrapper;  
    private final StackPane boardAnchor; 
    private final GridPane gridPane;
    private final CellView[][] cellViews = new CellView[GRID_ROWS][GRID_COLS];
 
    public BoardView() {
        wrapper = new StackPane();
        wrapper.setAlignment(Pos.CENTER);
        
        try {
            Image bgImage = new Image(new File("assets/lobby/lobby_bg.png").toURI().toString());
            BackgroundSize coverSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, false, true);
            wrapper.setBackground(new Background(new BackgroundImage(bgImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, coverSize)));
        } catch (Exception e) {
            wrapper.setStyle("-fx-background-color: #121212;");
        }

        boardAnchor = new StackPane();
        boardAnchor.setAlignment(Pos.CENTER);
        
        boardAnchor.maxHeightProperty().bind(wrapper.heightProperty().multiply(OVERALL_BOARD_HEIGHT_PCT));
        boardAnchor.maxWidthProperty().bind(boardAnchor.maxHeightProperty().multiply(1.25)); 

        boardAnchor.translateXProperty().bind(wrapper.widthProperty().multiply(ENTIRE_BOARD_OFFSET_X));
        boardAnchor.translateYProperty().bind(wrapper.heightProperty().multiply(ENTIRE_BOARD_OFFSET_Y));

        ImageView boardImage = new ImageView();
        try {
            boardImage.setImage(new Image(new File("assets/tex/board.png").toURI().toString()));
        } catch (Exception e) {}
        boardImage.setPreserveRatio(false); 
        boardImage.fitWidthProperty().bind(boardAnchor.maxWidthProperty().multiply(IMAGE_SCALE_PCT));
        boardImage.fitHeightProperty().bind(boardAnchor.maxHeightProperty().multiply(IMAGE_SCALE_PCT));
        boardImage.translateXProperty().bind(boardAnchor.maxWidthProperty().multiply(IMAGE_OFFSET_X_PCT));
        boardImage.translateYProperty().bind(boardAnchor.maxHeightProperty().multiply(IMAGE_OFFSET_Y_PCT));
        
        gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: transparent;");
        gridPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        gridPane.prefWidthProperty().bind(boardAnchor.maxWidthProperty().multiply(GRID_WIDTH_PCT)); 
        gridPane.prefHeightProperty().bind(boardAnchor.maxHeightProperty().multiply(GRID_HEIGHT_PCT));
        gridPane.translateXProperty().bind(boardAnchor.maxWidthProperty().multiply(GRID_OFFSET_X_PCT));
        gridPane.translateYProperty().bind(boardAnchor.maxHeightProperty().multiply(GRID_OFFSET_Y_PCT));
        
        for (int i = 0; i < GRID_COLS; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(10);
            gridPane.getColumnConstraints().add(col);
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(10);
            gridPane.getRowConstraints().add(row);
        }

        buildEmptyGrid();

        boardAnchor.getChildren().addAll(boardImage, gridPane);
        wrapper.getChildren().add(boardAnchor);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(wrapper.widthProperty());
        clip.heightProperty().bind(wrapper.heightProperty());
        wrapper.setClip(clip);
    }
 
    public void updateBoard(Board board, Monster player, Monster opponent, boolean skipAnimation, Runnable onFinish) {
        for (int index = 0; index < 100; index++) {
            Cell cell = board.getCell(index);             
            int[] rc  = displayRowCol(index);
            cellViews[rc[0]][rc[1]].setCell(cell, index);
            cellViews[rc[0]][rc[1]].setOccupants(player, opponent, index);
        }
        
        if (skipAnimation) {
            if (onFinish != null) onFinish.run();
        } else {
            // Replaces the volatile PathTransition with a reliable pause buffer to prevent instant turn snapping
            PauseTransition pt = new PauseTransition(Duration.seconds(0.3));
            pt.setOnFinished(e -> {
                if (onFinish != null) onFinish.run();
            });
            pt.play();
        }
    }
 
    private void buildEmptyGrid() {
        for (int index = 0; index < 100; index++) {
            int[] rc = displayRowCol(index);
            CellView cv = new CellView(index, (int)Region.USE_COMPUTED_SIZE); 
            cv.getPane().setStyle("-fx-background-color: transparent;"); 
            cellViews[rc[0]][rc[1]] = cv;
            gridPane.add(cv.getPane(), rc[1], rc[0]);  
        }
    }
 
    private int[] displayRowCol(int index) {
        int boardRow = index / 10;
        int boardCol = index % 10;
        int displayRow = (GRID_ROWS - 1) - boardRow;          
        int displayCol = (boardRow % 2 == 0) ? boardCol : (9 - boardCol); 
        return new int[]{displayRow, displayCol};
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
}