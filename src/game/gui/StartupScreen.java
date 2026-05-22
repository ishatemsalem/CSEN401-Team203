package game.gui;

import java.io.File;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class StartupScreen {
    private StackPane view;
    private Runnable onBeatDrop;
    private MediaPlayer audioPlayer;
    
    private Text displayText;

    private final double[] timestamps = {
        0.350,  // 0:20
        0.667,  // 0:39
        0.983,  // 0:58
        1.617,  // 1:36
        1.933,  // 1:55
        2.250,  // 2:14
        2.883,  // 2:52
        3.183,  // 3:10
        3.517,  // 3:30
        4.150,  // 4:08
        4.467,  // 4:27
        4.767,  // 4:45
        5.083,  // 5:04
        5.400,  // 5:23
        5.717,  // 5:42
        6.033,  // 6:01
        6.667,  // 6:39
        6.983,  // 6:58
        7.300,  // 7:17
        8.333,  // 8:19
        8.550,  // 8:32
        10.133  // 10:07
    };
    
    private final String[] script = {
        "Disney,\n\n\n", "I mean\n\n\n", "Pixar,\n\n\n", 
        "Pixar,\nKindly,\n\n", "Pixar,\nKindly, don't\n\n", "Pixar,\nKindly, don't sue\n\n", 
        "Pixar,\nKindly, don't sue\n We\n", "Pixar,\nKindly, don't sue\n We are\n", 
        "Pixar,\nKindly, don't sue\n We are broke.\n", "Pixar,\nKindly, don't sue\n We are broke.\nThank", 
        "Pixar,\nKindly, don't sue\n We are broke.\nThank you.", "Pixar,\nKindly, don't sue\n We are broke.\nThank you. xoxo", 
        "", 
        "Game\n\n\n\n", "Game by:\n\n\n\n", "Game by:\nIslam\n\n\n", 
        "Game by:\nIslam\nJana\n\n", "Game by:\nIslam\nJana\nJudy\n", "Game by:\nIslam\nJana\nJudy\nRokaya", 
        "", "", ""
    };

    public StartupScreen(Runnable onBeatDrop, MediaPlayer audioPlayer) {
        this.onBeatDrop = onBeatDrop;
        this.audioPlayer = audioPlayer;
        
        view = new StackPane(); 
        view.setStyle("-fx-background-color: black;");

        displayText = new Text("");
        displayText.setFill(Color.WHITE);
        displayText.setTextAlignment(TextAlignment.CENTER);
        
        loadCustomFont();

        displayText.setText(script[0]);
        displayText.setTranslateY(80.0);
        displayText.setVisible(false);

        view.getChildren().add(displayText);
    }

    public StackPane getView() {
        return view;
    }

    private void loadCustomFont() {
        try {
            String fontPath = new File("assets/fonts/Jua-Regular.ttf").toURI().toString();
            Font customFont = Font.loadFont(fontPath, 40);
            
            if (customFont != null) {
                displayText.setFont(customFont);
            } else {
                throw new Exception("Font returned null");
            }
        } catch (Exception e) {
            System.out.println("Warning: Jua-Regular.ttf failed to load. Fallback to Courier New.");
            displayText.setFont(Font.font("Courier New", 40)); 
        }
    }

    public void startSequence() {
        if (audioPlayer == null) {
            onBeatDrop.run();
            return;
        }

        displayText.setVisible(true);

        AnimationTimer rhythmEngine = new AnimationTimer() {
            int currentIndex = 0;
            double lastReportedAudioTime = 0;
            long anchorNanoTime = System.nanoTime();

            @Override
            public void handle(long currentNanoTime) {
                double currentAudioTime = audioPlayer.getCurrentTime().toSeconds();
                
                if (currentAudioTime != lastReportedAudioTime) {
                    lastReportedAudioTime = currentAudioTime;
                    anchorNanoTime = currentNanoTime;
                }

                double exactSmoothTime = lastReportedAudioTime + ((currentNanoTime - anchorNanoTime) / 1_000_000_000.0);

                while (currentIndex < timestamps.length && exactSmoothTime >= timestamps[currentIndex]) {
                    if (currentIndex == timestamps.length - 1) { 
                        this.stop(); 
                        onBeatDrop.run(); 
                        return;
                    }
                    
                    String currentText = script[currentIndex];
                    displayText.setText(currentText);
                    
                    // Anim logic:
                    
                    // Disney, rising from center
                    if (currentIndex == 0) {
                        displayText.setTranslateY(80.0);
                    } else if (currentIndex == 1) {
                        displayText.setTranslateY(40.0);
                    } else if (currentIndex == 2) {
                        displayText.setTranslateY(0.0);
                    }    
                    else if (currentIndex > 2 && currentIndex <= 11) {
                        // Static rendering for the Kindly dont sue
                        displayText.setTranslateY(0);
                        
                    } else if (currentIndex >= 13 && currentIndex <= 18) {
                        // Dynamic rendering, for Game by
                        int emptyLines = 0;
                        if (currentText.endsWith("\n\n\n\n")) emptyLines = 4;
                        else if (currentText.endsWith("\n\n\n")) emptyLines = 3;
                        else if (currentText.endsWith("\n\n")) emptyLines = 2;
                        else if (currentText.endsWith("\n")) emptyLines = 1;

                        displayText.setTranslateY(emptyLines * 25.0);
                        
                    } else {
                        // reset translation for empty screen
                        displayText.setTranslateY(0);
                    }
                    
                    currentIndex++;
                }
            }
        };
        
        rhythmEngine.start();
    }
}