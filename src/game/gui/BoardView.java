package game.gui;

import game.engine.Board;
import game.engine.cells.*;
import game.engine.monsters.Monster;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.Transition;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.File;

public class BoardView {

    // --- ALIGNMENT CONTROLS ---
    private static final double OVERALL_BOARD_HEIGHT_PCT = 0.8; 
    private static final double ENTIRE_BOARD_OFFSET_X    = 0.0;  
    private static final double ENTIRE_BOARD_OFFSET_Y    = -0.03; 
    
    private static final double IMAGE_SCALE_PCT      = 1.08;  
    private static final double IMAGE_OFFSET_X_PCT   = 0.0;  
    private static final double IMAGE_OFFSET_Y_PCT   = 0.0;  

    private static final double GRID_WIDTH_PCT       = 1.01; 
    private static final double GRID_HEIGHT_PCT      = 0.90; 
    private static final double GRID_OFFSET_X_PCT    = 0.0;  
    private static final double GRID_OFFSET_Y_PCT    = 0.03;  

    private static final int GRID_COLS = 10;
    private static final int GRID_ROWS = 10;
 
    private static final String COLOR_PLAYER_TOKEN   = "#00e5ff"; 
    private static final String COLOR_OPPONENT_TOKEN = "#ff1744"; 
 
    private final StackPane wrapper;  
    private final StackPane boardAnchor; 
    private final GridPane gridPane;
    private final Pane tokenLayer; // New independent layer for animated tokens
    private final CellView[][] cellViews = new CellView[GRID_ROWS][GRID_COLS];
 
    private Circle playerToken;
    private Circle opponentToken;
    
    // Track previous positions to trigger animation when they change
    private int lastPlayerPos = 0;
    private int lastOpponentPos = 0;
 
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

        // 5. The Token Layer (Must perfectly mirror the Grid's coordinates and size)
        tokenLayer = new Pane();
        tokenLayer.setPickOnBounds(false);
        tokenLayer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        tokenLayer.prefWidthProperty().bind(gridPane.prefWidthProperty());
        tokenLayer.prefHeightProperty().bind(gridPane.prefHeightProperty());
        tokenLayer.translateXProperty().bind(gridPane.translateXProperty());
        tokenLayer.translateYProperty().bind(gridPane.translateYProperty());

        initMonsterTokens();

        // Stack order: Image -> Invisible Grid -> Token Layer
        boardAnchor.getChildren().addAll(boardImage, gridPane, tokenLayer);
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
        }
        updateMonsterTokens(player, opponent, skipAnimation, onFinish);
    }
 
    private void updateMonsterTokens(Monster player, Monster opponent, boolean skipAnimation, Runnable onFinish) {
        int newP = player.getPosition();
        int newO = opponent.getPosition();

        // Clear out old static bindings from cells just in case
        for (int r = 0; r < GRID_ROWS; r++) {
            for (int c = 0; c < GRID_COLS; c++) {
                cellViews[r][c].clearTokens();
            }
        }

        // If skipped or no positions changed, snap them instantly
        if (skipAnimation || (newP == lastPlayerPos && newO == lastOpponentPos)) {
            placeTokenInstantly(playerToken, newP, -5);
            placeTokenInstantly(opponentToken, newO, 5);
            lastPlayerPos = newP;
            lastOpponentPos = newO;
            if (onFinish != null) onFinish.run();
            return;
        }

        // Otherwise, animate the changes
        ParallelTransition pt = new ParallelTransition();

        if (newP != lastPlayerPos) {
            pt.getChildren().add(createBobAnimation(playerToken, lastPlayerPos, newP, -5));
            lastPlayerPos = newP;
        } else {
            placeTokenInstantly(playerToken, newP, -5);
        }

        if (newO != lastOpponentPos) {
            pt.getChildren().add(createBobAnimation(opponentToken, lastOpponentPos, newO, 5));
            lastOpponentPos = newO;
        } else {
            placeTokenInstantly(opponentToken, newO, 5);
        }

        pt.setOnFinished(e -> {
            if (onFinish != null) onFinish.run();
        });
        pt.play();
    }

    private void placeTokenInstantly(Circle token, int index, double offsetX) {
        token.translateXProperty().unbind();
        token.translateYProperty().unbind();
        int[] rc = displayRowCol(index);
        
        // Dynamically bind to token layer width/height to support resizing
        token.translateXProperty().bind(tokenLayer.widthProperty().divide(GRID_COLS).multiply(rc[1] + 0.5).add(offsetX));
        token.translateYProperty().bind(tokenLayer.heightProperty().divide(GRID_ROWS).multiply(rc[0] + 0.5));
    }

    private Transition createBobAnimation(Circle token, int startIdx, int endIdx, double offsetX) {
        // Unbind so PathTransition can take control
        token.translateXProperty().unbind();
        token.translateYProperty().unbind();

        double startX = getCellX(startIdx, offsetX);
        double startY = getCellY(startIdx);
        double endX = getCellX(endIdx, offsetX);
        double endY = getCellY(endIdx);

        token.setTranslateX(startX);
        token.setTranslateY(startY);

        Path path = new Path();
        path.getElements().add(new MoveTo(startX, startY));

        // Create an arc height based on travel distance
        double distance = Math.abs(endX - startX) + Math.abs(endY - startY);
        double controlX = (startX + endX) / 2;
        double controlY = Math.min(startY, endY) - (distance * 0.15) - 30; // Jump upwards

        path.getElements().add(new QuadCurveTo(controlX, controlY, endX, endY));

        PathTransition pt = new PathTransition(Duration.seconds(0.7), path, token);
        
        // When finished, re-bind to exact cell layout so it resizes correctly
        pt.setOnFinished(e -> placeTokenInstantly(token, endIdx, offsetX));
        return pt;
    }

    private double getCellX(int index, double offsetX) {
        int[] rc = displayRowCol(index);
        return (tokenLayer.getWidth() / GRID_COLS) * (rc[1] + 0.5) + offsetX;
    }

    private double getCellY(int index) {
        int[] rc = displayRowCol(index);
        return (tokenLayer.getHeight() / GRID_ROWS) * (rc[0] + 0.5);
    }
 
    private void buildEmptyGrid() {
        for (int index = 0; index < 100; index++) {
            int[] rc = displayRowCol(index);
            CellView cv = new CellView(index, 10); 
            cv.getPane().setStyle("-fx-background-color: transparent;"); 
            cellViews[rc[0]][rc[1]] = cv;
            gridPane.add(cv.getPane(), rc[1], rc[0]);  
        }
    }
 
    private void initMonsterTokens() {
        playerToken   = makeToken(COLOR_PLAYER_TOKEN);
        opponentToken = makeToken(COLOR_OPPONENT_TOKEN);
        tokenLayer.getChildren().addAll(playerToken, opponentToken);
    }
 
    private Circle makeToken(String hexColor) {
        Circle c = new Circle(15); 
        c.setFill(Color.web(hexColor));
        c.setStroke(Color.WHITE);
        c.setStrokeWidth(2);
        c.setMouseTransparent(true);
        return c;
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