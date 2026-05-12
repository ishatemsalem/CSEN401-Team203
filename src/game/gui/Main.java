package game.gui;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import javafx.util.Duration;

import game.engine.Game;
import game.engine.Role;

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
        
        initAudio("assets/audio/lobby_theme.mp3");

        // Load intro screen first, PASSING IN AUDIO PLAYER
        StartupScreen intro = new StartupScreen(this::triggerFlashbangTransition, backgroundMusic);
        rootLayout.getChildren().add(intro.getView());
        
        window.setScene(mainScene);
        window.setMinWidth(1100);
        window.setMinHeight(700);
        window.show();
        
        // Start the music and the intro sequence
        if (backgroundMusic != null) {
            backgroundMusic.setOnPlaying(() -> {
                // for sync purposes
                intro.startSequence(); 
            });
            backgroundMusic.play();
        } else {
            // justincase the audio file is missing
            intro.startSequence();
        }
    }

    private void triggerFlashbangTransition() {
        //whitebox
        Rectangle flashOverlay = new Rectangle(1280, 720, Color.WHITE);
        flashOverlay.setMouseTransparent(true); // mouseclicks pass through
        
        flashOverlay.setBlendMode(BlendMode.ADD);
        
        rootLayout.getChildren().add(flashOverlay);

        // Timing
        Duration fadeInTime = Duration.seconds(2.0 / 60.0);
        Duration fadeOutTime = Duration.seconds(53.0 / 60.0);

        FadeTransition phaseIn = new FadeTransition(fadeInTime, flashOverlay);
        phaseIn.setFromValue(0.0);
        phaseIn.setToValue(1.0);

        FadeTransition phaseOut = new FadeTransition(fadeOutTime, flashOverlay);
        phaseOut.setFromValue(1.0);
        phaseOut.setToValue(0.0);

        // when fully white:
        phaseIn.setOnFinished(e -> {
            switchToLobby();
            
            // since switchToLobby() clears everything in rootLayout, put flash overlay on top again
            rootLayout.getChildren().add(flashOverlay);
            
            phaseOut.play(); //fadeout
        });

        // cleanup
        phaseOut.setOnFinished(e -> rootLayout.getChildren().remove(flashOverlay));

        // Ignite the flash
        phaseIn.play();
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
        Role playerRole = roleFromLobbyChoice(selectedSide);
        try {
            Game game = new Game(playerRole);
            GameView gameView = new GameView(game, this);
            rootLayout.getChildren().setAll(gameView.getView());
            window.setScene(mainScene);
        } catch (IOException e) {
            ExceptionHandler.showGenericError(
                "Could not load game data (CSV files).\n" + e.getMessage()
            );
        }
    }

    /** Maps lobby combo text to engine {@link Role}. */
    private static Role roleFromLobbyChoice(String selectedSide) {
        if (selectedSide != null && selectedSide.toLowerCase().contains("laugh")) {
            return Role.LAUGHER;
        }
        return Role.SCARER;
    }
    
    public void showWinScreen(String winnerName, String role, int finalEnergy) {
        WinScreen win = new WinScreen(this, winnerName, role, finalEnergy);
        rootLayout.getChildren().setAll(win.getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}