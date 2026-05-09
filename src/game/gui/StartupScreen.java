package game.gui;

import java.io.File;

import javafx.animation.AnimationTimer;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;

public class StartupScreen {
    private StackPane view;
    private Runnable onBeatDrop;
    private MediaPlayer audioPlayer;
    
    private Canvas displayScreen;
    private GraphicsContext gc;
    
    // Array holding our pre-baked VRAM textures
    private Image[] textureFrames;

    private final double[] timestamps = {
0.333, // 0.08
        0.667, // 0.16
        0.958, // 0.23
        1.583, // 1.14
        1.917, // 1.22
        2.167, // 2.04
        2.833, // 2.20
        3.167, // 3.04
        3.458, // 3.11
        4.125, // 4.03
        4.417, // 4.10
        4.750, // 4.18
        5.042, // 5.01
        5.333, // 5.08
        5.667, // 5.16
        6.000, // 6.00
        6.625, // 6.15
        6.958, // 6.23
        7.250, // 7.06
        8.458, // 8.11
        9.900 // 10.03
    };
    
    private final String[] script = {
        "Disney,\n ", "Disney,\n \n ", "Disney,\n \n \n ", 
        "Disney,\nKindly,\n\n", "Disney,\nKindly, don't\n\n", "Disney,\nKindly, don't sue\n\n", 
        "Disney,\nKindly, don't sue\n We\n", "Disney,\nKindly, don't sue\n We are\n", 
        "Disney,\nKindly, don't sue\n We are broke.\n", "Disney,\nKindly, don't sue\n We are broke.\nThank", 
        "Disney,\nKindly, don't sue\n We are broke.\nThank you.", "Disney,\nKindly, don't sue\n We are broke.\nThank you. xoxo", 
        "", 
        "Game\n\n\n\n", "Game by:\n\n\n\n", "Game by:\nIslam\n\n\n", 
        "Game by:\nIslam\nJana\n\n", "Game by:\nIslam\nJana\nJudy\n", "Game by:\nIslam\nJana\nJudy\nRokaya", 
        "", "DROP"
    };

    public StartupScreen(Runnable onBeatDrop, MediaPlayer audioPlayer) {
        this.onBeatDrop = onBeatDrop;
        this.audioPlayer = audioPlayer;
        
        view = new StackPane(); 
        view.setStyle("-fx-background-color: black;");

        displayScreen = new Canvas(1280, 720);
        gc = displayScreen.getGraphicsContext2D();
        view.getChildren().add(displayScreen);

        bakeTexturesToVRAM();
    }

    public StackPane getView() {
        return view;
    }

    /**
     * Converts all text into static Images at startup.
     * gc.drawImage() is hardware accelerated and bypasses font rendering entirely.
     */
private void bakeTexturesToVRAM() {
        textureFrames = new Image[script.length];
        
        Canvas offscreenCanvas = new Canvas(1280, 720);
        GraphicsContext offscreenGc = offscreenCanvas.getGraphicsContext2D();
        
        // ==========================================
        // 1. THE FONT FIX
        // ==========================================
        // Make sure you imported java.io.File at the top of your class!
        String fontPath = new File("assets/fonts/Jua-Regular.ttf").toURI().toString();
        Font customFont = Font.loadFont(fontPath, 40);
        
        if (customFont == null) {
            // If you see this in your terminal, the folder structure doesn't match the path
            System.out.println("CRITICAL WARNING: Jua-Regular.ttf failed to load! Check your folder path.");
            offscreenGc.setFont(Font.font("Courier New", 40)); 
        } else {
            offscreenGc.setFont(customFont);
        }
        
        offscreenGc.setTextAlign(TextAlignment.CENTER);
        
        // Change baseline to TOP so our manual math is absolute
        offscreenGc.setTextBaseline(VPos.TOP); 

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.BLACK); 

        // The exact pixel distance the text will jump up for each \n
        double lineHeight = 45.0; 

        for (int i = 0; i < script.length; i++) {
            offscreenGc.clearRect(0, 0, 1280, 720);
            offscreenGc.setFill(Color.BLACK);
            offscreenGc.fillRect(0, 0, 1280, 720);
            offscreenGc.setFill(Color.WHITE);
            
            // ==========================================
            // 2. THE ABSOLUTE LIFT FIX
            // ==========================================
            // First, count exactly how many '\n' characters are in this specific frame
            int newlineCount = 0;
            for (char c : script[i].toCharArray()) {
                if (c == '\n') {
                    newlineCount++;
                }
            }
            
            // Base starting position (360 is the exact center of the screen).
            // For every \n we detect, we subtract lineHeight, shoving the starting point higher.
            double startY = 360 - (newlineCount * lineHeight);
            
            // Split the string and draw each line
            String[] lines = script[i].split("\n", -1);
            for (int j = 0; j < lines.length; j++) {
                // The first line prints at startY.
                // Subsequent lines print exactly one lineHeight below the previous line.
                offscreenGc.fillText(lines[j], 640, startY + (j * lineHeight));
            }
            
            textureFrames[i] = offscreenCanvas.snapshot(params, null); 
        }
    }

    public void startSequence() {
        if (audioPlayer == null) return;

        AnimationTimer rhythmEngine = new AnimationTimer() {
            int currentIndex = 0;
            
            // Variables for our Custom High-Resolution Interpolation Clock
            double lastReportedAudioTime = 0;
            long anchorNanoTime = System.nanoTime();

            @Override
            public void handle(long currentNanoTime) {
                // THE MAGIC: Ask the audio player for the time.
                double currentAudioTime = audioPlayer.getCurrentTime().toSeconds();
                
                // If the audio buffer ticked, re-anchor our hyper-accurate stopwatch
                if (currentAudioTime != lastReportedAudioTime) {
                    lastReportedAudioTime = currentAudioTime;
                    anchorNanoTime = currentNanoTime;
                }

                // Extrapolate the exact time down to the microsecond, bypassing audio buffer lag
                double exactSmoothTime = lastReportedAudioTime + ((currentNanoTime - anchorNanoTime) / 1_000_000_000.0);

                while (currentIndex < timestamps.length && exactSmoothTime >= timestamps[currentIndex]) {
                    if (currentIndex == timestamps.length - 1) { 
                        this.stop(); 
                        onBeatDrop.run(); 
                        return;
                    }
                    
                    // Hardware accelerated blit. Overdraw = 1.
                    gc.drawImage(textureFrames[currentIndex], 0, 0);
                    
                    currentIndex++;
                }
            }
        };
        
        rhythmEngine.start();
    }
}