package game.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

public class StartupScreen {
    private VBox view;
    private Label syncText;
    private Runnable onBeatDrop;


    public StartupScreen(Runnable onBeatDrop) {
        this.onBeatDrop = onBeatDrop;
        
        view = new VBox();
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: black;");

        syncText = new Label("");
        syncText.setTextFill(Color.WHITE);
        syncText.setFont(Font.font("Courier New", 40)); 
        
        // THE FIX: Lock the size and center the text internally
        syncText.setPrefSize(1280, 720); 
        syncText.setAlignment(Pos.CENTER);
        syncText.setTextAlignment(TextAlignment.CENTER);

        view.getChildren().add(syncText);
    }

    public VBox getView() {
        return view;
    }

    public void startSequence() {
        Timeline timeline = new Timeline(
            
            new KeyFrame(Duration.seconds(0.08), e -> syncText.setText("Disney,\n")),
            new KeyFrame(Duration.seconds(0.16), e -> syncText.setText("Disney,\n\n")),
            new KeyFrame(Duration.seconds(0.23), e -> syncText.setText("Disney,\n\n\n")),
            new KeyFrame(Duration.seconds(1.14), e -> syncText.setText("Disney,\nKindly,\n\n")),
            new KeyFrame(Duration.seconds(1.22), e -> syncText.setText("Disney,\nKindly, don't\n\n")),
            new KeyFrame(Duration.seconds(2.04), e -> syncText.setText("Disney,\nKindly, don't sue\n\n")),
            new KeyFrame(Duration.seconds(2.20), e -> syncText.setText("Disney,\nKindly, don't sue\n We\n")),
            new KeyFrame(Duration.seconds(3.04), e -> syncText.setText("Disney,\nKindly, don't sue\n We are\n")),
            new KeyFrame(Duration.seconds(3.11), e -> syncText.setText("Disney,\nKindly, don't sue\n We are broke.\n")),
            new KeyFrame(Duration.seconds(4.03), e -> syncText.setText("Disney,\nKindly, don't sue\n We are broke.\nThank")),
            new KeyFrame(Duration.seconds(4.10), e -> syncText.setText("Disney,\nKindly, don't sue\n We are broke.\nThank you.")),
            new KeyFrame(Duration.seconds(4.18), e -> syncText.setText("Disney,\nKindly, don't sue\n We are broke.\nThank you. xoxo")),
            
            new KeyFrame(Duration.seconds(5.01), e -> syncText.setText("")),

            new KeyFrame(Duration.seconds(5.08), e -> syncText.setText("Game\n\n\n\n")),
            new KeyFrame(Duration.seconds(5.16), e -> syncText.setText("Game by:\n\n\n\n")),
            new KeyFrame(Duration.seconds(6.00), e -> syncText.setText("Game by:\nIslam\n\n\n")),
            new KeyFrame(Duration.seconds(6.15), e -> syncText.setText("Game by:\nIslam\nJana\n\n")),
            new KeyFrame(Duration.seconds(6.23), e -> syncText.setText("Game by:\nIslam\nJana\nJudy\n")),
            new KeyFrame(Duration.seconds(7.06), e -> syncText.setText("Game by:\nIslam\nJana\nJudy\nRokaya")),

            new KeyFrame(Duration.seconds(8.11), e -> syncText.setText("")),
            
            new KeyFrame(Duration.seconds(10.03), e -> onBeatDrop.run()) 
        );
        timeline.play();
    }
}