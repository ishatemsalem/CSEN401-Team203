package game.gui;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.File;

public class Credits {
    private Pane view;
    private Main mainApp;
    private MediaPlayer creditsAudio;
    private Timeline mainSequence;
    private boolean isEnding = false;

    private Label title;
    private Pane leftContainer;
    private Pane rightContainer;


    private final String[] CREDITS_TEXT = {
        "Game By: - Islam",
        " - Jana",
        " - Judy",
        " - Rokaya",
        "",
        "Recording - Me",
        "Script writing - Me",
        "Mixing/mastering - 3 cups of coffee",
        "Being cool - Islam",
        "Not doing their voice lines - Judy, Jana, Rokaya",
        "Existing - Me",
        "Productivity - Other people",
        "My job - n/a",
        "Beauty - You",
        "Polymorphism - Sounds like a disease",
        "Uneven credits - CSS is hard",
        "Blocking - JavaFX",
        "Pain - My back",
        "Pulse - My heart",
        "8:15 AM Tutorials - A violation of human rights",
        "Steps walked today - Definitely not 10,000",
        "Mitochondria - The powerhouse of the cell",
        "Port Said commute - Send help",
        "Blender Add-on - Check my GitHub",
        "NullPointerException - Line 42",
        "[object Object] - [object Object]",
        "Laugh track - HollywoodLaughTracks",
        "Im typing all this - But why are you reading",
        "Git Commit Message - \"asdasdasd final real\"",
        "Bug fixes - What bugs?",
        "Feature - Not a bug",
        "Hotel - Trivago",
        "My sanity - 404 Not Found",
        "Is this a pigeon? - \uD83E\uDD8B", 
        "According to all known laws of aviation - there is no way a bee should be able to fly",
        "",
        "Best country - Egypt",
        "Your hopes and dreams - Unrealistic",
        "Light - Switch",
        "Best Insult - poopface",
        "Best Game Ever - DooR DasH: Scare vs Laugh TouchDown",
        "|   || - || | _",
        "The FitnessGram Pacer test is a multistage aerobic", 
        "World peace - lol",
        "Spiders - The Actual Worst",
        "Take me home - WEST VIRGINIA",
        "The Plane Has Landed - *claps*",
        "Why are you - Still reading this",
        "Nothing funny - Is happening",
        "Find Something Else -  To Do",
        "Anime - Great",
        "Fidget Spinners - The Cure To Cancer",
        "Despa - Cito 2",
        "Im Hungry - Ill go make some food",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "Im Back - Whatd I miss?",
        "Top o the mornin - to ya ladds",
        "Dogs - <Cats",
        "upside down - umop apisdn",
        "Car Salesman - *slap*",
        "Struggling to think of stuff - Alexander Scrambling",
        "Comedy - Our GPA",
        "3D Dupstep Outro - Atleast this isnt that",
        "*dab on the haters* - *haters dab back*",
        "Somebody once - Told Me",
        "The World Was Gonna - Roll Me",
        "Never Gonna Give You - Up",
        "Never Gonna Let You - Down",
        "Game of Thrones - More Like LAME of Thrones HA",
        "Marvel - Pretty Alright",
        "Midani - *dolphin sounds*",
        "Thanos - *snap*",
        "Hello Mine Turtle - Hello!",
        "Roblox - Oof",
        "Conveyor belts - Weeeeee",
        "Processing Foundation - Please reconsider",
        "GSoC 2026 - April 30th was a dark day",
        "GSoC - Maybe in 2027",
        "Big O Notation - O(no)",
        "SceneBuilder - (Not Responding)",
        "Laptop fans - Currently mimicking a Boeing 747",
        "Creeper - Aww man",
        "Clinical assessment of Nader - Failed",
        "Japanese pitch accent - Muzukashii desu",
        "To be fair - You have to have a very high IQ to understand Rick and Morty",
        "They did surgery - On a grape",
        "We live in - A society",
        "The cake - Is a lie",
        "I am once again asking - For a Bonus on this project",
        "The cake - Is a lie",
        "All your base - Are belong to us",
        "Do a barrel roll - *spins*",
        "Nader - Still needs a clinical assessment",
        "Font size - Too small?",
        "Ctrl+Z - CTRL+Z!!!",
        "Waluigi - Still not in Smash",
        "Wake me up - Inside",
        "“Two hours later…” - Read in the French Spongebob voice",
        "The Backrooms - I am currently trapped here",
        "What are those?! - These are my crocs",
        "Do you know da wae? - *clicking noises*",
        "E - E",
        "Are you grading this? - Please give us A+",
        "Dear TA - We love you, please have mercy",
        "Bottom of the list - Getting closer...",
        "Just kidding - There is no bottom",
        "My fingers hurt - From typing this",
        "Is the song over yet? - No",
        "Can I go home now? - Also no",
        "[REDACTED] - [DATA EXPUNGED]",
        "Error 418 - Im a teapot",
        "END OF FILE - Wait no I lied",
        "Please give us #1 rank - I will name my firstborn after you",
        "Thank you - Was fun"
    };

    public Credits(Main mainApp) {
        this.mainApp = mainApp;
        view = new Pane();
        view.setPrefSize(1280, 720);

        // 1. Solid Black Background
        Rectangle pitchBlack = new Rectangle(1280, 720, Color.BLACK);

        // 2 Audio Setup
        try {
            Media media = new Media(new File("assets/audio/EndCreditsAudio.mp3").toURI().toString());
            creditsAudio = new MediaPlayer(media);
        } catch (Exception e) {
            System.out.println("EndCreditsAudio.mp3 missing or path incorrect.");
        }

        // 3. dFonts
        Font hoefler = null;
        try { hoefler = Font.loadFont(new File("assets/fonts/Hoefler Text Regular.ttf").toURI().toString(), 80); } catch (Exception e) {}
        if (hoefler == null) hoefler = Font.font("Serif", 160);

        Font jua = null;
        try { jua = Font.loadFont(new File("assets/fonts/Jua-Regular.ttf").toURI().toString(), 28); } catch (Exception e) {}
        if (jua == null) jua = Font.font("SansSerif", 28);

        // 4. Decoupled Title
        title = new Label("CREDITS");
        title.setFont(hoefler);
        title.setTextFill(Color.WHITE);
        title.setPrefWidth(1280);
        title.setAlignment(Pos.CENTER);
        title.setLayoutY(320); 
        title.setOpacity(0);   

        // 5. Decoupled Left Column
        leftContainer = new Pane();
        leftContainer.setLayoutX(50);
        leftContainer.setLayoutY(750);
        leftContainer.setPrefWidth(550);

        // 6. Decoupled Right Column
        rightContainer = new Pane();
        rightContainer.setLayoutX(680);
        rightContainer.setLayoutY(750); // Starts offscreen bottom
        rightContainer.setPrefWidth(550);

        // Build the text layout
        for (int i = 0; i < CREDITS_TEXT.length; i++) {
            String line = CREDITS_TEXT[i];
            String lText = "";
            String rText = "";

            if (line != null && !line.trim().isEmpty()) {
                int dashIndex = line.indexOf("-");
                if (dashIndex != -1) {
                    lText = line.substring(0, dashIndex).trim();
                    rText = line.substring(dashIndex + 1).trim();
                } else {
                    lText = line.trim();
                }
            }

            Label lLabel = new Label(lText);
            lLabel.setFont(jua);
            lLabel.setTextFill(Color.WHITE);
            lLabel.setPrefWidth(550);
            lLabel.setAlignment(Pos.CENTER_LEFT); 
            lLabel.setLayoutY(i * 45);

            Label rLabel = new Label(rText);
            rLabel.setFont(jua);
            rLabel.setTextFill(Color.WHITE);
            rLabel.setPrefWidth(550);
            rLabel.setAlignment(Pos.CENTER_RIGHT); 
            rLabel.setLayoutY(i * 45);

            leftContainer.getChildren().add(lLabel);
            rightContainer.getChildren().add(rLabel);
        }

        // 7. Skip Button 
        Button skipBtn = new Button("Skip");
        skipBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 24px; -fx-cursor: hand;");
        skipBtn.setLayoutX(1150);
        skipBtn.setLayoutY(30);
        skipBtn.setOnAction(e -> endCredits());

        view.getChildren().addAll(pitchBlack, title, leftContainer, rightContainer, skipBtn);
    }

    public void startAnimation() {
        if (creditsAudio != null) {
            creditsAudio.play();
        }

        mainSequence = new Timeline(
            new KeyFrame(Duration.seconds(0.3), e -> {
                FadeTransition ft = new FadeTransition(Duration.seconds(0.3), title);
                ft.setToValue(1.0);
                ft.play();
            }),
            new KeyFrame(Duration.seconds(2.3), e -> {
                double distance = -(750 + (CREDITS_TEXT.length * 45) + 100); 
                Duration scrollDuration = Duration.seconds(215); 

                TranslateTransition moveT = new TranslateTransition(scrollDuration, title);
                moveT.setByY(distance);
                moveT.setInterpolator(Interpolator.LINEAR); 

                TranslateTransition moveL = new TranslateTransition(scrollDuration, leftContainer);
                moveL.setByY(distance);
                moveL.setInterpolator(Interpolator.LINEAR); 

                TranslateTransition moveR = new TranslateTransition(scrollDuration, rightContainer);
                moveR.setByY(distance);
                moveR.setInterpolator(Interpolator.LINEAR); 

                moveT.play();
                moveL.play();
                moveR.play();
            }),
            new KeyFrame(Duration.seconds(217.3), e -> endCredits())
        );
        mainSequence.play();
    }

    private void endCredits() {
        if (isEnding) return; 
        isEnding = true;

        if (mainSequence != null) mainSequence.stop();
        if (creditsAudio != null) creditsAudio.stop();
        
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), view);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> mainApp.switchToLobby());
        fadeOut.play();
    }

    public Pane getView() {
        return view;
    }
}