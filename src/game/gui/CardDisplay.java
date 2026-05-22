package game.gui;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.File;

public class CardDisplay {

    // ========================================================================
    // --- CARD ALIGNMENT & SIZING CONTROLS (%) ---
    // Change these values to resize or move the deck relative to the screen.
    // 1.0 = 100%, 0.5 = 50%, 0.15 = 15%, etc.
    // ========================================================================
    
    private static final double CARD_WIDTH_PCT = 0.14;  // Card takes 12% of screen width
    private static final double CARD_POS_X_PCT = 0.85;  // Deck is placed 85% across the screen (right side)
    private static final double CARD_POS_Y_PCT = 0.15;  // Deck is placed 5% down from the top
    private static final double SLIDE_DOWN_PCT = 1.20;  // Animated card slides down 120% of screen height

    // ========================================================================

    private StackPane view;
    private StackPane animatedGroup;
    private ImageView activeCardImage;
    private ReadOnlyDoubleProperty screenHeightProp;

    public CardDisplay(ReadOnlyDoubleProperty screenWidthProp, ReadOnlyDoubleProperty screenHeightProp) {
        this.screenHeightProp = screenHeightProp;

        // 1. Static Deck Image (The top card of the deck)
        ImageView deckImage = new ImageView();
        try {
            deckImage.setImage(new Image(new File("assets/tex/cards/back.png").toURI().toString()));
        } catch (Exception e) {}
        deckImage.setPreserveRatio(true);
        deckImage.fitWidthProperty().bind(screenWidthProp.multiply(CARD_WIDTH_PCT));

        // 2. The Animated Card (Starts off matching the deck)
        activeCardImage = new ImageView();
        try {
            activeCardImage.setImage(new Image(new File("assets/tex/cards/back.png").toURI().toString()));
        } catch (Exception e) {}
        activeCardImage.setPreserveRatio(true);
        activeCardImage.fitWidthProperty().bind(screenWidthProp.multiply(CARD_WIDTH_PCT));

        animatedGroup = new StackPane(activeCardImage);
        animatedGroup.setVisible(false);

        view = new StackPane(deckImage, animatedGroup);
        view.setAlignment(Pos.TOP_CENTER);

        // Bind the exact position of the entire deck container to screen percentages
        view.translateXProperty().bind(screenWidthProp.multiply(CARD_POS_X_PCT));
        view.translateYProperty().bind(screenHeightProp.multiply(CARD_POS_Y_PCT));
    }

    private String getCardImagePath(String name) {
        if (name == null) return "assets/tex/cards/back.png";
        String n = name.toLowerCase();
        if (n.contains("2319") || n.contains("alert")) return "assets/tex/cards/2319_alert.png";
        if (n.contains("contam")) return "assets/tex/cards/contam_code.png";
        if (n.contains("mega")) return "assets/tex/cards/mega_drain.png";
        if (n.contains("mind") || n.contains("scramble")) return "assets/tex/cards/mind_scramble.png";
        if (n.contains("swap")) return "assets/tex/cards/pos_swap.png";
        if (n.contains("small") || n.contains("snatch")) return "assets/tex/cards/small_snatch.png";
        if (n.contains("sneaky") || n.contains("thief")) return "assets/tex/cards/sneaky_thief.png";
        if (n.contains("shield")) return "assets/tex/cards/super_shield.png";
        if (n.contains("total") || n.contains("confusion")) return "assets/tex/cards/total_confusion.png";
        return "assets/tex/cards/back.png";
    }

    public void showCard(String cardName, String effect, boolean lucky) {
        try {
            activeCardImage.setImage(new Image(new File("assets/tex/cards/back.png").toURI().toString()));
        } catch (Exception e) {}
        
        animatedGroup.setTranslateY(0);
        animatedGroup.setVisible(true);

        // Calculate dynamic slide distance based on current window height
        double slideDistance = screenHeightProp.get() * SLIDE_DOWN_PCT;

        // 1. Slide face-down card down out of the screen
        TranslateTransition slideDown = new TranslateTransition(Duration.seconds(0.4), animatedGroup);
        slideDown.setByY(slideDistance); 

        slideDown.setOnFinished(e -> {
            // 2. Swap texture to the actual drawn face-up card
            try {
                activeCardImage.setImage(new Image(new File(getCardImagePath(cardName)).toURI().toString()));
            } catch (Exception ex) {}

            // 3. Slide the face-up card back up into view
            TranslateTransition slideUp = new TranslateTransition(Duration.seconds(0.4), animatedGroup);
            slideUp.setByY(-slideDistance); 
            
            slideUp.setOnFinished(ev -> {
                PauseTransition pause = new PauseTransition(Duration.seconds(4));
                pause.setOnFinished(event -> animatedGroup.setVisible(false));
                pause.play();
            });
            slideUp.play();
        });
        slideDown.play();
    }

    public void hide() {
        animatedGroup.setVisible(false);
    }

    public StackPane getView() { return view; }
}