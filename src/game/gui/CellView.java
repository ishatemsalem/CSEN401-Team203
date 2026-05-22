package game.gui;

import game.engine.Role;
import game.engine.cells.*;
import game.engine.monsters.Monster;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class CellView {

    private final StackPane   root;          
    private final BorderPane  layout;        
    private final Label       indexLabel;    
    private final Label       centreLabel;   
    private final Label       flashLabel;    
    private final VBox        occupantsBox;

    private boolean exhausted = false;

    public CellView(int index, int size) {
        indexLabel = new Label(String.valueOf(index));
        indexLabel.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.6);" +
                "-fx-font-size: 10px;" +
                "-fx-padding: 2 0 0 4;"
        );

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getChildren().addAll(indexLabel);

        centreLabel = new Label("");
        centreLabel.setAlignment(Pos.CENTER);

        occupantsBox = new VBox(2);
        occupantsBox.setAlignment(Pos.CENTER);
        occupantsBox.setStyle("-fx-padding: 0 0 2 0;");

        flashLabel = new Label("");
        flashLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 4 8 4 8;" +
                "-fx-background-color: rgba(0,0,0,0.85);" +
                "-fx-background-radius: 4;"
        );
        flashLabel.setOpacity(0);   
        flashLabel.setMouseTransparent(true);
        flashLabel.setTranslateY(-15);

        layout = new BorderPane();
        layout.setTop(topBar);
        layout.setCenter(centreLabel);
        layout.setBottom(occupantsBox);
        layout.setPrefSize(size, size);

        root = new StackPane(layout, flashLabel);
        root.setPrefSize(size, size);

        applyStyle(null);
    }

    public void setCell(Cell cell, int index) {
        indexLabel.setText(String.valueOf(index));
        
        centreLabel.setText("");
        centreLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-family: 'Jua', sans-serif;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );
        exhausted = false;

        if (cell == null) {
            applyStyle(null);
            return;
        }

        if (cell instanceof DoorCell) {
            DoorCell dc = (DoorCell) cell;                     
            if (dc.isActivated()) {
                centreLabel.setText("USED");
                exhausted = true;
            } else {
                centreLabel.setText(String.valueOf(dc.getEnergy()));
            }
        } else if (cell instanceof ConveyorBelt) {
            ConveyorBelt cb = (ConveyorBelt) cell;             
            centreLabel.setText("+" + cb.getEffect());
        } else if (cell instanceof ContaminationSock) {
            ContaminationSock cs = (ContaminationSock) cell;   
            centreLabel.setText("−" + Math.abs(cs.getEffect()));
        } else if (cell instanceof MonsterCell) {
            MonsterCell mc = (MonsterCell) cell;               
            if (mc.getCellMonster() != null) {                 
                Monster m = mc.getCellMonster();
                centreLabel.setText(m.getName());
                // Darkened colors for stationed board monsters
                String color = (m.getRole() == Role.SCARER) ? "#c60000" : "#5bba5b";
                centreLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-family: 'Jua', sans-serif; -fx-font-size: 13px; -fx-font-weight: bold;");
            }
        }
        
        applyStyle(cell);
    }

public void setOccupants(Monster player, Monster opponent, int cellIndex) {
        occupantsBox.getChildren().clear();
        boolean isConfusedHere = false;

        if (player != null && player.getPosition() == cellIndex) {
            occupantsBox.getChildren().add(createMonsterLabel(player));
            if (player.isConfused()) isConfusedHere = true;
        }
        if (opponent != null && opponent.getPosition() == cellIndex) {
            occupantsBox.getChildren().add(createMonsterLabel(opponent));
            if (opponent.isConfused()) isConfusedHere = true;
        }

        // Apply light red background if any stationed occupant is confused
        if (isConfusedHere) {
            // Appends to the base style applied by setCell()
            layout.setStyle(layout.getStyle() + "-fx-background-color: rgba(255, 100, 100, 0.4);");
        }
    }

    private Label createMonsterLabel(Monster m) {
        Label l = new Label(m.getName());
        String bg = (m.getRole() == Role.SCARER) ? "#c60000" : "#5bba5b"; 
        String fg = (m.getRole() == Role.SCARER) ? "#dddddd" : "#1a1a1a"; 
        
        // Add vivid blue outline if frozen
        String border = m.isFrozen() ? "-fx-border-color: #00bfff; -fx-border-width: 2; -fx-border-radius: 3; " : "";
        
        l.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-family: 'Jua', sans-serif; -fx-font-size: 11px; -fx-padding: 1 4 1 4; -fx-background-radius: 3; -fx-font-weight: bold; " + border);
        return l;
    }

    public void setExhausted(boolean exhausted) {
        this.exhausted = exhausted;
        if (exhausted) {
            centreLabel.setText("USED");
            centreLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Jua', sans-serif; -fx-font-size: 16px; -fx-font-weight: bold;");
        }
    }

    public void clearTokens() {
        occupantsBox.getChildren().clear();
    }

    public void flashLabel(String text, String hexColor) {
        flashLabel.setText(text);
        flashLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + hexColor + ";" +
                "-fx-padding: 4 8 4 8;" +
                "-fx-background-color: rgba(0,0,0,0.85);" +
                "-fx-background-radius: 4;"
        );
        flashLabel.setOpacity(1);

        PauseTransition pause = new PauseTransition(Duration.millis(1200));
        FadeTransition fade   = new FadeTransition(Duration.millis(500), flashLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        SequentialTransition seq = new SequentialTransition(pause, fade);
        seq.play();
    }

    public StackPane getPane() { return root; }

    private void applyStyle(Cell cell) {
        String baseBorder = "-fx-border-color: #546e7a; -fx-border-width: 1; -fx-border-radius: 3; -fx-background-radius: 3; ";
        if (cell instanceof CardCell) {
            layout.setStyle(baseBorder + "-fx-background-color: rgba(0, 0, 139, 0.4);"); // Dark Blue
        } else if (cell instanceof MonsterCell) {
            layout.setStyle(baseBorder + "-fx-background-color: rgba(173, 216, 230, 0.5);"); // Light Blue
        } else {
            layout.setStyle(baseBorder + "-fx-background-color: transparent;");
        }
    }
}