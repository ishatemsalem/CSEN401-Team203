package game.gui;

import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
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
    // ========================================================================
    private static final double CARD_WIDTH_PCT = 0.12;  // Standard size on deck
    private static final double DECK1_X_PCT = 0.80;     // Main deck placement
    private static final double DECK2_X_PCT = 0.90;     // Target secondary deck placement
    private static final double CARD_POS_Y_PCT = 0.15;  // Deck top offset positioning
    private static final double SLIDE_DOWN_PCT = 1.20;  // Initial drop screen clearance scale

    // ========================================================================

    private StackPane view;
    private StackPane animatedGroup;
    private ImageView activeCardImage;
    private ImageView deck1Image;
    private ImageView deck2Image;
    
    private ReadOnlyDoubleProperty screenWidthProp;
    private ReadOnlyDoubleProperty screenHeightProp;
    private Runnable onDismissCallback;
    private boolean waitingForDraw = false;

    public CardDisplay(ReadOnlyDoubleProperty screenWidthProp, ReadOnlyDoubleProperty screenHeightProp) {
        this.screenWidthProp = screenWidthProp;
        this.screenHeightProp = screenHeightProp;

        // 1. Deck 1 (Main deck to pull cards from)
        deck1Image = new ImageView();
        loadLocalImage(deck1Image, "assets/tex/cards/back.png");
        deck1Image.setPreserveRatio(true);
        deck1Image.fitWidthProperty().bind(screenWidthProp.multiply(CARD_WIDTH_PCT));

        // 2. Deck 2 (The discard / destination pile)
        deck2Image = new ImageView();
        loadLocalImage(deck2Image, "assets/tex/cards/back.png");
        deck2Image.setPreserveRatio(true);
        deck2Image.fitWidthProperty().bind(screenWidthProp.multiply(CARD_WIDTH_PCT));
        deck2Image.setVisible(false); // Hidden until a card lands on it

        // 3. Active Animation Layer
        activeCardImage = new ImageView();
        loadLocalImage(activeCardImage, "assets/tex/cards/back.png");
        activeCardImage.setPreserveRatio(true);
        activeCardImage.fitWidthProperty().bind(screenWidthProp.multiply(CARD_WIDTH_PCT));

        animatedGroup = new StackPane(activeCardImage);
        animatedGroup.setVisible(false);

        // Build container layout
        view = new StackPane();
        view.setAlignment(Pos.TOP_LEFT); // Top-left origin to absolute bind relative positions
        view.setPickOnBounds(false);    // Clicking empty space won't block lower UI unless expanded

        // Position components manually based on screen space dimensions
        deck1Image.translateXProperty().bind(screenWidthProp.multiply(DECK1_X_PCT));
        deck1Image.translateYProperty().bind(screenHeightProp.multiply(CARD_POS_Y_PCT));

        deck2Image.translateXProperty().bind(screenWidthProp.multiply(DECK2_X_PCT));
        deck2Image.translateYProperty().bind(screenHeightProp.multiply(CARD_POS_Y_PCT));

        view.getChildren().addAll(deck2Image, deck1Image, animatedGroup);
    }

    private void loadLocalImage(ImageView iv, String path) {
        try {
            iv.setImage(new Image(new File(path).toURI().toString()));
        } catch (Exception e) {
            System.out.println("Failed to load texture asset: " + path);
        }
    }

    /**
     Map UI interaction hooks directly to this status check.
     */
    public boolean isWaitingForDraw() {
        return waitingForDraw;
    }

    /**
     Sets up the deck container to await an explicit user interaction click event.
     */
    public void prepareDeckForDraw(String cardName, Runnable onDismissed) {
        this.waitingForDraw = true;
        this.onDismissCallback = onDismissed;

        // Highlight or hook action into Deck 1
        deck1Image.setStyle("-fx-effect: dropshadow(three-pass-box, #00ffcc, 15, 0.5, 0, 0); -fx-cursor: hand;");
        deck1Image.setOnMouseClicked(e -> {
            deck1Image.setOnMouseClicked(null);
            deck1Image.setStyle("");
            waitingForDraw = false;
            executeDrawSequence(cardName);
        });
    }

    private void executeDrawSequence(String cardName) {
        // Prepare the active card asset layer
        try {
            activeCardImage.setImage(new Image(new File("assets/tex/cards/back.png").toURI().toString()));
        } catch (Exception e) {}
        
        // Start state match coordinates: Top Left of Deck 1 base position
        animatedGroup.translateXProperty().unbind();
        animatedGroup.translateYProperty().unbind();
        animatedGroup.setTranslateX(screenWidthProp.get() * DECK1_X_PCT);
        animatedGroup.setTranslateY(screenHeightProp.get() * CARD_POS_Y_PCT);
        animatedGroup.setScaleX(1.0);
        animatedGroup.setScaleY(1.0);
        animatedGroup.setVisible(true);

        // Sequence 1: Slide card down out of the frame from current deck base position
        double slideDistance = screenHeightProp.get() * SLIDE_DOWN_PCT;
        TranslateTransition slideDown = new TranslateTransition(Duration.seconds(0.4), animatedGroup);
        slideDown.setByY(slideDistance);

        slideDown.setOnFinished(e -> {
            // -- Sequence State Context change: Face card, fullscreen mode, centering focus --

            // 1. Swap texture to the real card face
            try {
                activeCardImage.setImage(new Image(new File(getCardImagePath(cardName)).toURI().toString()));
            } catch (Exception ex) {}

            // 2. Perform centering calculations with dynamic scaling constraints (80% window height max size)
            double actualCardHeight = activeCardImage.getBoundsInLocal().getHeight();
            double actualCardWidth = activeCardImage.getBoundsInLocal().getWidth();
            double winHeight = screenHeightProp.get();
            double winWidth = screenWidthProp.get();

            // Calculate exact fit scale constraint based strictly on the screen's vertical boundary limit
            double centerFillTargetHeight = winHeight * 0.80; // Total height card should occupy (leaving some border padding)
            double targetScaleFactor = centerFillTargetHeight / actualCardHeight;

            // Define clean centered coordinates, overriding parent container alignment handles
            // Must account for origin shift from top-left to centered during scale transitions
            double newWidthOnScreen = actualCardWidth * targetScaleFactor;
            double centerX_TL_base = (winWidth / 2.0) - (actualCardWidth / 2.0); 
            double centerY_Center_Focus = (winHeight / 2.0) - (actualCardHeight / 2.0) - 260.0;

            // Snap positioning down below view frame with correct centering offset alignment 
            animatedGroup.setTranslateX(centerX_TL_base);
            animatedGroup.setTranslateY(winHeight + 50); // Position just out of frame bottom base

            // Sequence 2: Centralized slide up and focus scale change
            TranslateTransition slideCenter = new TranslateTransition(Duration.seconds(0.4), animatedGroup);
            slideCenter.setToY(centerY_Center_Focus);

            ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(0.4), animatedGroup);
            scaleUp.setToX(targetScaleFactor); // Scaled based strictly to fit the screen height limit
            scaleUp.setToY(targetScaleFactor);

            ParallelTransition displayAll = new ParallelTransition(slideCenter, scaleUp);
            displayAll.setOnFinished(ev -> setupDismissOverlay());
            displayAll.play();
        });
        
        slideDown.play();
    }

    private void setupDismissOverlay() {
        // Enforce full-screen interaction zone on the parent container stack
        view.setPickOnBounds(true);
        view.setOnMouseClicked(event -> {
            view.setOnMouseClicked(null);
            view.setPickOnBounds(false); // Pass background events back down cleanly immediately on click

            // Pre-calculate target return baseline destination coordinates (Deck 2 base position)
            double targetX = screenWidthProp.get() * DECK2_X_PCT;
            double targetY = screenHeightProp.get() * CARD_POS_Y_PCT;

            // Slide 3: Fly from focused center point back down onto Deck 2 pile context
            TranslateTransition slideToDeck2 = new TranslateTransition(Duration.seconds(0.4), animatedGroup);
            slideToDeck2.setToX(targetX);
            slideToDeck2.setToY(targetY);

            // Rescale 2: Shrink item back down to baseline structural dimensions
            ScaleTransition scaleDown = new ScaleTransition(Duration.seconds(0.4), animatedGroup);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);

            ParallelTransition returnToPile = new ParallelTransition(slideToDeck2, scaleDown);
            returnToPile.setOnFinished(e -> {
                // Finalize cleanup pipeline
                animatedGroup.setVisible(false);
                deck2Image.setVisible(true); // Persist a pile visibility layout target state base texture
                
                // Signal turn pipeline that the card interaction block context is resolved
                if (onDismissCallback != null) {
                    onDismissCallback.run();
                }
            });
            returnToPile.play();
        });
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

    public StackPane getView() { return view; }
}