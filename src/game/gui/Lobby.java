package game.gui;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.media.AudioClip;

import java.io.File;

public class Lobby {

    private Pane view;
    private Main mainApp;

    private int selectedRole = 0;

    // Platform Nodes (Containers)
    private Pane scarerNode;
    private Pane laugherNode;
    
    // UI Elements
    private ImageView selectedSign;
    private Label scarerArrow;
    private Label laugherArrow;
    private ImageView startGameBtn;

    // Audio states
    private boolean musicOn = true;
    private boolean sfxOn = true;
    private AudioClip hoverSound;
    private AudioClip clickSound;

    public Lobby(Main mainApp) {
        this.mainApp = mainApp;
        this.view = new Pane(); 

        this.mainApp = mainApp;
        this.view = new Pane(); 

        // Load SFX once into memory to prevent freezing
        /*try {
            hoverSound = new AudioClip(new File("assets/audio/hover.mp3").toURI().toString());
            clickSound = new AudioClip(new File("assets/audio/click.mp3").toURI().toString());
        } catch (Exception e) {
            System.out.println("SFX files missing, buttons will be silent.");
        }*/

        // 1. Load Background (Crop to Fill)
        try {
            Image bgImage = new Image(new File("assets/lobby/lobby_bg.png").toURI().toString());
            BackgroundSize coverSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, false, true);
            BackgroundImage backgroundImage = new BackgroundImage(bgImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, coverSize);
            view.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            System.out.println("Missing background asset.");
            view.setStyle("-fx-background-color: #1a1a1a;");
        }

        // 2. Build the Platform Anchors (Monsters locked to platforms)
        scarerNode = createAnchorContainer(0.125, 0.145, 0.205);
        ImageView scarerPlatform = createAnchoredImage("assets/lobby/platform_scarer.png", scarerNode, 0.0, 0.65, 1.03);
        ImageView scarerMonster = createAnchoredImage("assets/lobby/monster_scarer.png", scarerNode, 0.1, 0.03, 0.7);
        scarerNode.getChildren().addAll(scarerPlatform, scarerMonster);

        laugherNode = createAnchorContainer(0.35, 0.23, 0.21);
        ImageView laugherPlatform = createAnchoredImage("assets/lobby/platform_laugher.png", laugherNode, 0.0, 0.4, 1.025);
        ImageView laugherMonster = createAnchoredImage("assets/lobby/monster_laugher.png", laugherNode, 0.1, 0.3, 0.71);
        laugherNode.getChildren().addAll(laugherPlatform, laugherMonster);

        // Apply distinct ColorAdjust to the ENTIRE container (Darkens platform AND monster together)
        scarerNode.setEffect(new ColorAdjust(0, 0, 0, 0));
        laugherNode.setEffect(new ColorAdjust(0, 0, 0, 0));

        // 3. Build the Main Board Anchor (Buttons locked to board)
        Pane boardNode = createAnchorContainer(0.625, 0.375, 0.265);
        ImageView mainBoard = createAnchoredImage("assets/lobby/main_board.png", boardNode, 0.0, 0.0, 1.0);
        
        // These placeholders (0.2, 0.3, etc.) are now % relative to the BOARD, not the screen
        ImageView instructionsBtn = createAnchoredImage("assets/lobby/btn_instructions.png", boardNode, 0.225, 0.45, 0.615);
        ImageView creditsBtn = createAnchoredImage("assets/lobby/btn_credits.png", boardNode, 0.28, 0.65, 0.49);
        ImageView musicBtn = createAnchoredImage("assets/lobby/btn_music_on.png", boardNode, 0.13, 0.155, 0.33);
        ImageView sfxBtn = createAnchoredImage("assets/lobby/btn_sfx_on.png", boardNode, 0.56, 0.16, 0.35);

        applyButtonHoverPress(instructionsBtn);
        applyButtonHoverPress(creditsBtn);
        applyButtonHoverPress(musicBtn);
        applyButtonHoverPress(sfxBtn);


        creditsBtn.setOnMouseClicked(e -> mainApp.switchToCredits());
        instructionsBtn.setOnMouseClicked(e -> mainApp.switchToInstructions());

        musicBtn.setOnMouseClicked(e -> toggleAudio(musicBtn, "music"));
        sfxBtn.setOnMouseClicked(e -> toggleAudio(sfxBtn, "sfx"));

        boardNode.getChildren().addAll(mainBoard, instructionsBtn, creditsBtn, musicBtn, sfxBtn);

        // 4. Independent Elements
        ImageView topSign = createBoundImage("assets/lobby/top_sign.png", 0.25, 0.05, 0.47);
        
        // START button is explicitly bound to the viewport, fully separated from the board
        startGameBtn = createBoundImage("assets/lobby/btn_start.png", 0.765, 0.1, 0.145);
        applyButtonHoverPress(startGameBtn);
        // Now Grey it out and disable it initially
        ColorAdjust startBtnEffect = (ColorAdjust) startGameBtn.getEffect();
        startBtnEffect.setSaturation(-0.1);  // Makes it grayscale
        startBtnEffect.setBrightness(-0.5);  // Darkens it
        startGameBtn.setDisable(true);       // Prevents clicks and hover animations

        // Add the action to switch to the GameView
        startGameBtn.setOnMouseClicked(e -> {
            if (selectedRole != 0) {
                String side = (selectedRole == 1) ? "Scarer" : "Laugher";
                mainApp.startGame(side);
            }
        });


        selectedSign = createBoundImage("assets/lobby/sign_selected.png", 0, 0, 0.1);
        selectedSign.setVisible(false);

        // Apply independent floating animations
        applyFloatingAnimation(boardNode, 8.0, 4); 
        applyFloatingAnimation(topSign, 3.5, 5);
        applyFloatingAnimation(startGameBtn, 5.0, 3); // Gives the separate start button a slight breath

        view.getChildren().addAll(scarerNode, laugherNode, boardNode, topSign, startGameBtn, selectedSign);

        // 5. Load BM Jua Font & Arrows
        Font juaFont30 = null;
        Font juaFont40 = null;
        try {
            String fontPath = new File("assets/fonts/Jua-Regular.ttf").toURI().toString();
            juaFont30 = Font.loadFont(fontPath, 30);
            juaFont40 = Font.loadFont(fontPath, 40);
        } catch (Exception e) {
            System.out.println("Font not found, using default.");
            juaFont30 = Font.font(30);
            juaFont40 = Font.font(40);
        }

        scarerArrow = createBouncingArrow(scarerNode, juaFont40);
        laugherArrow = createBouncingArrow(laugherNode, juaFont40);
        view.getChildren().addAll(scarerArrow, laugherArrow);

        // 6. Platform Interaction Logic
        setupPlatformInteractions(scarerNode, 1);
        setupPlatformInteractions(laugherNode, 2);

        // 7. Bottom Flashing Text
        Label bottomText = new Label("select one of the platforms to select a role, then start the game!");
        if (juaFont30 != null) bottomText.setFont(juaFont30);
        bottomText.setTextFill(Color.WHITE);
        bottomText.setAlignment(Pos.CENTER);
        
        bottomText.layoutXProperty().bind(view.widthProperty().subtract(bottomText.widthProperty()).divide(2));
        bottomText.layoutYProperty().bind(view.heightProperty().multiply(0.85));

        setupSemiBlinkAnimation(bottomText);
        view.getChildren().add(bottomText);
    }

    public Pane getView() {
        return view;
    }

    /**
     * Creates an invisible container bound to the main view's percentages.
     */
    private Pane createAnchorContainer(double xPct, double yPct, double widthPct) {
        Pane container = new Pane();
        container.setPickOnBounds(false); // Let clicks pass through empty space to elements behind
        container.layoutXProperty().bind(view.widthProperty().multiply(xPct));
        container.layoutYProperty().bind(view.heightProperty().multiply(yPct));
        container.prefWidthProperty().bind(view.widthProperty().multiply(widthPct));
        return container;
    }

    /**
     * Anchors an image entirely relative to its parent container's WIDTH.
     * This ensures the layout never breaks or separates when resizing.
     */
    private ImageView createAnchoredImage(String relativePath, Pane parentContainer, double relX, double relY, double relScale) {
        ImageView img = loadImageView(relativePath);
        img.layoutXProperty().bind(parentContainer.prefWidthProperty().multiply(relX));
        // Using widthProperty for Y is intentional. It maintains perfect aspect ratio positioning.
        img.layoutYProperty().bind(parentContainer.prefWidthProperty().multiply(relY));
        img.fitWidthProperty().bind(parentContainer.prefWidthProperty().multiply(relScale));
        return img;
    }

    /**
     * Standard view-bound image for elements like the independent Start Button.
     */
    private ImageView createBoundImage(String relativePath, double xPct, double yPct, double widthPct) {
        ImageView img = loadImageView(relativePath);
        img.layoutXProperty().bind(view.widthProperty().multiply(xPct));
        img.layoutYProperty().bind(view.heightProperty().multiply(yPct));
        img.fitWidthProperty().bind(view.widthProperty().multiply(widthPct));
        return img;
    }

    private ImageView loadImageView(String relativePath) {
        Image imgResource = null;
        try {
            imgResource = new Image(new File(relativePath).toURI().toString());
        } catch (Exception e) {
            System.out.println("Missing asset: " + relativePath);
        }
        ImageView img = new ImageView(imgResource);
        img.setPreserveRatio(true);
        return img;
    }

    /**
     * Standard buttons scale to 0.95 when pressed.
     */
    private void applyButtonHoverPress(ImageView btn) {
        ColorAdjust colorAdjust = new ColorAdjust();
        btn.setEffect(colorAdjust);

        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
            /*if (sfxOn && hoverSound != null) {
                hoverSound.play();
            }*/
        });
        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
        btn.setOnMousePressed(e -> {
            btn.setScaleX(0.95);
            btn.setScaleY(0.95);
            colorAdjust.setBrightness(-0.2);
            /*if (sfxOn && clickSound != null) {
                clickSound.play();
            }*/
        });
        btn.setOnMouseReleased(e -> {
            btn.setScaleX(1.05); 
            btn.setScaleY(1.05);
            colorAdjust.setBrightness(0.0);
        });
    }

    private void applyFloatingAnimation(javafx.scene.Node node, double durationSeconds, double distanceY) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(durationSeconds), node);
        transition.setByY(distanceY);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.setAutoReverse(true);
        transition.play();
    }

    /**
     * Platform Sequence: Hover (n) -> Click (0.9n) -> Release (1.0 original)
     * Because we are scaling the container Node, the monster and platform scale from the exact same central origin.
     */
    private void setupPlatformInteractions(Pane platformNode, int roleId) {
        double n = 1.05;
        double pressScale = n * 0.9;

        platformNode.setOnMouseEntered(e -> {
            platformNode.setScaleX(n);
            platformNode.setScaleY(n);
        });

        platformNode.setOnMouseExited(e -> {
            platformNode.setScaleX(1.0);
            platformNode.setScaleY(1.0);
        });

        platformNode.setOnMousePressed(e -> {
            platformNode.setScaleX(pressScale);
            platformNode.setScaleY(pressScale);
        });

        platformNode.setOnMouseReleased(e -> {
            platformNode.setScaleX(1.0);
            platformNode.setScaleY(1.0);
            selectRole(roleId);
        });
    }

    private void selectRole(int roleId) {
        selectedRole = roleId;

        startGameBtn.setDisable(false);
        ColorAdjust startBtnEffect = (ColorAdjust) startGameBtn.getEffect();
        startBtnEffect.setSaturation(0.0);
        startBtnEffect.setBrightness(0.0);

        scarerArrow.setVisible(false);
        laugherArrow.setVisible(false);

        if (roleId == 1) { 
            // Brighten Scarer container, darken Laugher container
            ((ColorAdjust) scarerNode.getEffect()).setBrightness(0.0);
            ((ColorAdjust) laugherNode.getEffect()).setBrightness(-0.4);
            
            scarerArrow.setVisible(true);
            scarerArrow.setScaleX(1.5); scarerArrow.setScaleY(1.5);

            selectedSign.setVisible(true);
            selectedSign.layoutXProperty().bind(scarerNode.layoutXProperty().add(scarerNode.prefWidthProperty().divide(3)));
            selectedSign.layoutYProperty().bind(scarerNode.layoutYProperty().subtract(60));

        } else if (roleId == 2) { 
            ((ColorAdjust) laugherNode.getEffect()).setBrightness(0.0);
            ((ColorAdjust) scarerNode.getEffect()).setBrightness(-0.4);
            
            laugherArrow.setVisible(true);
            laugherArrow.setScaleX(1.5); laugherArrow.setScaleY(1.5);

            selectedSign.setVisible(true);
            selectedSign.layoutXProperty().bind(laugherNode.layoutXProperty().add(laugherNode.prefWidthProperty().divide(3)));
            selectedSign.layoutYProperty().bind(laugherNode.layoutYProperty().subtract(60));
        }
    }

    private Label createBouncingArrow(Pane targetNode, Font font) {
        Label arrow = new Label(">");
        if (font != null) arrow.setFont(font);
        arrow.setTextFill(Color.WHITE);
        arrow.setRotate(90);
        
        // Anchor the arrow dynamically above the respective container
        arrow.layoutXProperty().bind(targetNode.layoutXProperty().add(targetNode.prefWidthProperty().divide(2.5)));
        arrow.layoutYProperty().bind(targetNode.layoutYProperty().subtract(25));

        TranslateTransition bounce = new TranslateTransition(Duration.seconds(0.8), arrow);
        bounce.setByY(-15);
        bounce.setCycleCount(Animation.INDEFINITE);
        bounce.setAutoReverse(true);
        bounce.play();

        return arrow;
    }

    private void setupSemiBlinkAnimation(Label textNode) {
        Timeline blinkText = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(textNode.opacityProperty(), 1.0)),
            new KeyFrame(Duration.seconds(5), new KeyValue(textNode.opacityProperty(), 1.0)),
            new KeyFrame(Duration.seconds(5.5), new KeyValue(textNode.opacityProperty(), 0.3)),
            new KeyFrame(Duration.seconds(6), new KeyValue(textNode.opacityProperty(), 1.0))
        );
        blinkText.setCycleCount(Animation.INDEFINITE);
        blinkText.play();
    }

    private void toggleAudio(ImageView btn, String type) {
    if (type.equals("music")) {
        musicOn = !musicOn;
        mainApp.setMusicMuted(!musicOn); // <--- Tell Main to mute/unmute
        String path = musicOn ? "assets/lobby/btn_music_on.png" : "assets/lobby/btn_music_off.png";
        try { btn.setImage(new Image(new File(path).toURI().toString())); } catch (Exception e) {}
    } else {
        sfxOn = !sfxOn;
        mainApp.setSfxMuted(!sfxOn);     // <--- Tell Main to mute/unmute SFX globally
        String path = sfxOn ? "assets/lobby/btn_sfx_on.png" : "assets/lobby/btn_sfx_off.png";
        try { btn.setImage(new Image(new File(path).toURI().toString())); } catch (Exception e) {}
    }
}

    private void playSFX(String relativePath) {
    if (!sfxOn) return; // Don't play if muted
    try {
        AudioClip clip = new AudioClip(new File(relativePath).toURI().toString());
        clip.play();
    } catch (Exception e) {
        // Silently fail if placeholder audio files aren't created yet
    }
}
    

}