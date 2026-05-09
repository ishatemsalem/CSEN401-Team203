package game.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import java.io.File;

public class Main extends Application {
    private Stage window;
    private MediaPlayer backgroundMusic;
    private Scene mainScene;
    private StackPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        this.window = primaryStage;
        this.window.setTitle("DooR DasH: Scare vs Laugh Touchdown");
        
        rootLayout = new StackPane();
        mainScene = new Scene(rootLayout, 1280, 720);
        
        // Initialize Audio 
        initAudio("assets/audio/lobby_theme.mp3");

        // Load the Intro Screen first, PASSING IN THE AUDIO PLAYER
        StartupScreen intro = new StartupScreen(this::switchToLobby, backgroundMusic);
        rootLayout.getChildren().add(intro.getView());

        window.setScene(mainScene);
        window.show();
        
        // Start the music and the intro sequence cleanly
        if (backgroundMusic != null) {
            backgroundMusic.setOnPlaying(() -> {
                // This will only fire exactly when the audio hits the speakers
                intro.startSequence(); 
            });
            backgroundMusic.play();
        } else {
            // Fallback just in case the audio file is missing
            intro.startSequence();
        }
    }

    private void initAudio(String path) {
        try {
            Media media = new Media(new File(path).toURI().toString());
            backgroundMusic = new MediaPlayer(media);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); // Loop lobby music
        } catch (Exception e) {
            System.out.println("Audio file not found, running silently.");
        }
    }

    // Callbacks for Scene Switching
    public void switchToLobby() {
        Lobby menu = new Lobby(this);
        rootLayout.getChildren().setAll(menu.getView());
    }

    public void switchToInstructions() {
        Instructions instructions = new Instructions(this);
        rootLayout.getChildren().setAll(instructions.getView());
    }

    public void startGame(String selectedSide) {
        System.out.println("Starting game as: " + selectedSide);
    }
    
    public void showWinScreen(String winnerName, String role, int finalEnergy) {
        WinScreen win = new WinScreen(this, winnerName, role, finalEnergy);
        rootLayout.getChildren().setAll(win.getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}