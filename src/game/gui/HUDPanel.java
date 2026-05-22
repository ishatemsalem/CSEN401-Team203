package game.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.CacheHint;
import game.engine.monsters.Monster;

import java.io.File;

public class HUDPanel {

    // =========================================================
    // --- MARQUEE POSITIONING CONSTANTS ---
    // MARQUEE_WIDTH_PCT    : sign width as % of screen width.
    //                        Narrower = shorter sign height = easier to fit above the board.
    // MARQUEE_Y_PCT        : distance from top of screen (1% = almost flush with top edge).
    // MARQUEE_ASPECT_RATIO : height ÷ width of your marquee_sign.png.
    //                        Wrong value = text drifts vertically inside the sign.
    //                        Measure your PNG: ratio = imageHeight / imageWidth.
    // =========================================================
    private static final double MARQUEE_WIDTH_PCT      = 0.50;   // sign width as % of screen
    private static final double MARQUEE_Y_PCT          = 0.005;   // distance from top of screen
    // Aspect ratio of marquee_sign.png (height ÷ width).
    // Measure your actual image and adjust if text drifts up or down.
    private static final double MARQUEE_ASPECT_RATIO   = 0.157;  // ≈ 238 / 1513 px

    private Pane         root;

    private StackPane    marqueePane;
    private ImageView    marqueeImage;
    private Label        marqueeLabel;   // replaces the old plain turnLabel

    private VBox         playerBox;
    private StackPane    playerFreezeOverlay;
    private VBox         opponentBox;
    private StackPane    opponentFreezeOverlay;

    private Label pName, pRole, pType, pEnergy, pPos, pStatus;
    private Label oName, oRole, oType, oEnergy, oPos, oStatus;

    private Label        lastCardSummary;
    private CardDisplay  cardDisplay;

    private int turnCount = 1;

    public HUDPanel() {
        root = new Pane();
        root.setPickOnBounds(false);
        root.setCache(true);
        root.setCacheHint(CacheHint.SPEED);

        // ── Marquee sign ─────────────────────────────────────────────
        marqueeImage = new ImageView();
        try {
            marqueeImage.setImage(
                new Image(new File("assets/tex/marquee_sign.png").toURI().toString())
            );
        } catch (Exception e) {
            System.out.println("Could not load marquee_sign.png – check assets/tex/");
        }
        marqueeImage.setPreserveRatio(true);
        marqueeImage.fitWidthProperty().bind(root.widthProperty().multiply(MARQUEE_WIDTH_PCT));

        // Text rendered INSIDE the sign — pure Pos.CENTER, no manual offset needed
        marqueeLabel = new Label("Turn 1  ·  —'s Turn  ·  Last Roll: —");
        marqueeLabel.setMouseTransparent(true);
        marqueeLabel.setStyle(
            "-fx-font-family: 'Jua', sans-serif;" +
            "-fx-font-size: 22px;" +
            "-fx-text-fill: #3b1a00;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,220,150,0.45), 2, 0.5, 0, 1);"
        );

        // Stack image + label; prefHeightProperty binding below makes Pos.CENTER work correctly
        marqueePane = new StackPane(marqueeImage, marqueeLabel);
        marqueePane.setAlignment(Pos.CENTER);
        marqueePane.setPickOnBounds(false);
        marqueePane.setMouseTransparent(true);

        // Bind pref size so the StackPane knows exactly how tall it is —
        // without this, Pos.CENTER has no height to centre within.
        marqueePane.prefWidthProperty().bind(root.widthProperty().multiply(MARQUEE_WIDTH_PCT));
        marqueePane.prefHeightProperty().bind(
            root.widthProperty().multiply(MARQUEE_WIDTH_PCT).multiply(MARQUEE_ASPECT_RATIO)
        );
        marqueePane.layoutXProperty().bind(
            root.widthProperty().divide(2)
                .subtract(marqueePane.prefWidthProperty().divide(2))
        );
        marqueePane.layoutYProperty().bind(root.heightProperty().multiply(MARQUEE_Y_PCT));

        // ── Player Side (Top Left) ────────────────────────────────────
        playerBox = new VBox(5);
        playerBox.setAlignment(Pos.TOP_LEFT);
        playerBox.setLayoutX(20);
        playerBox.setLayoutY(60);

        pName   = styledLabel("Name: -");
        pRole   = styledLabel("Role: -");
        pType   = styledLabel("Type: -");
        pEnergy = styledLabel("Energy: -");
        pPos    = styledLabel("Position: -");
        pStatus = styledLabel("Status: Normal");

        playerBox.getChildren().addAll(pName, pRole, pType, pEnergy, pPos, pStatus);

        playerFreezeOverlay = new StackPane();
        playerFreezeOverlay.setStyle(
            "-fx-background-color: rgba(173,216,230,0.4);" +
            "-fx-background-radius: 8;"
        );
        playerFreezeOverlay.setVisible(false);
        playerFreezeOverlay.layoutXProperty().bind(playerBox.layoutXProperty().subtract(10));
        playerFreezeOverlay.layoutYProperty().bind(playerBox.layoutYProperty().subtract(10));
        playerFreezeOverlay.prefWidthProperty().bind(playerBox.widthProperty().add(20));
        playerFreezeOverlay.prefHeightProperty().bind(playerBox.heightProperty().add(20));

        // ── Opponent Side (Top Right) ─────────────────────────────────
        opponentBox = new VBox(5);
        opponentBox.setAlignment(Pos.TOP_RIGHT);
        opponentBox.layoutXProperty().bind(root.widthProperty().subtract(220));
        opponentBox.setLayoutY(60);

        oName   = styledLabel("Name: -");
        oRole   = styledLabel("Role: -");
        oType   = styledLabel("Type: -");
        oEnergy = styledLabel("Energy: -");
        oPos    = styledLabel("Position: -");
        oStatus = styledLabel("Status: Normal");

        opponentBox.getChildren().addAll(oName, oRole, oType, oEnergy, oPos, oStatus);

        opponentFreezeOverlay = new StackPane();
        opponentFreezeOverlay.setStyle(
            "-fx-background-color: rgba(173,216,230,0.4);" +
            "-fx-background-radius: 8;"
        );
        opponentFreezeOverlay.setVisible(false);
        opponentFreezeOverlay.layoutXProperty().bind(opponentBox.layoutXProperty().subtract(10));
        opponentFreezeOverlay.layoutYProperty().bind(opponentBox.layoutYProperty().subtract(10));
        opponentFreezeOverlay.prefWidthProperty().bind(opponentBox.widthProperty().add(20));
        opponentFreezeOverlay.prefHeightProperty().bind(opponentBox.heightProperty().add(20));

        // ── Last Card Summary ─────────────────────────────────────────
        lastCardSummary = styledLabel("Last card drawn: —");
        lastCardSummary.setWrapText(true);
        lastCardSummary.setMaxWidth(400);
        lastCardSummary.setLayoutX(20);
        lastCardSummary.setLayoutY(220);

        // ── Card Display ──────────────────────────────────────────────
        cardDisplay = new CardDisplay(root.widthProperty(), root.heightProperty());
        cardDisplay.getView().setLayoutY(260);

        root.getChildren().addAll(
            marqueePane,                                    // <-- replaces old turnLabel
            playerBox,     playerFreezeOverlay,
            opponentBox,   opponentFreezeOverlay,
            lastCardSummary,
            cardDisplay.getView()
        );
    }

    public void nextTurn() {
        turnCount++;
    }

    public void updateInfo(Monster current, Monster humanPlayer, Monster opponent, int lastDice) {
        // ── Marquee text ──────────────────────────────────────────────
        String diceStr  = (lastDice >= 1) ? String.valueOf(lastDice) : "—";
        String turnStr  = "Turn " + turnCount
                        + "  ·  " + current.getName() + "'s Turn"
                        + "  ·  Last Roll: " + diceStr;
        marqueeLabel.setText(turnStr);

        // ── Left panel ────────────────────────────────────────────────
        pName.setText("Name: "     + humanPlayer.getName());
        pRole.setText("Role: "     + humanPlayer.getRole()
                        + (humanPlayer.isConfused() ? " (CONFUSED)" : ""));
        pType.setText("Type: "     + humanPlayer.getClass().getSimpleName());
        pEnergy.setText("Energy: " + humanPlayer.getEnergy());
        pPos.setText("Position: "  + humanPlayer.getPosition());
        pStatus.setText("Status: " + buildStatusString(humanPlayer));
        playerFreezeOverlay.setVisible(humanPlayer.isFrozen());

        // ── Right panel ───────────────────────────────────────────────
        oName.setText("Name: "     + opponent.getName());
        oRole.setText("Role: "     + opponent.getRole()
                        + (opponent.isConfused() ? " (CONFUSED)" : ""));
        oType.setText("Type: "     + opponent.getClass().getSimpleName());
        oEnergy.setText("Energy: " + opponent.getEnergy());
        oPos.setText("Position: "  + opponent.getPosition());
        oStatus.setText("Status: " + buildStatusString(opponent));
        opponentFreezeOverlay.setVisible(opponent.isFrozen());
    }

    private String buildStatusString(Monster m) {
        String s = "";
        if (m.isFrozen())   s += "Frozen ";
        if (m.isConfused()) s += "Confused ";
        return s.isEmpty() ? "Normal" : s;
    }

    public void setLastCardSummary(String cardName, String effectText) {
        String name = (cardName != null)  ? cardName  : "—";
        String eff  = (effectText != null) ? effectText : "";
        if (eff.length() > 72) eff = eff.substring(0, 71) + "…";
        lastCardSummary.setText(
            "Last card drawn: " + name + " — " +
            (eff.isEmpty() ? "(no description)" : eff)
        );
    }

    public CardDisplay getCardDisplay() { return cardDisplay; }
    public int         getTurnCount()   { return turnCount; }
    public Pane        getView()        { return root; }

    private Label styledLabel(String text) {
        Label l = new Label(text);
        l.setStyle(
            "-fx-text-fill: #eceff1;" +
            "-fx-font-family: 'Jua', sans-serif;" +
            "-fx-font-size: 16px;" +
            "-fx-effect: dropshadow(gaussian, black, 3, 0.8, 1, 1);"
        );
        return l;
    }
}