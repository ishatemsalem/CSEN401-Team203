package game.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.CacheHint;
import game.engine.monsters.Monster;

public class HUDPanel {

    private Pane         root;
    private Label        turnLabel;
    
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

        turnLabel = styledLabel("Turn: 1");
        turnLabel.setLayoutX(20);
        turnLabel.setLayoutY(20);

        // -- Player Side (Top Left) --
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
        playerFreezeOverlay.setStyle("-fx-background-color: rgba(173, 216, 230, 0.4); -fx-background-radius: 8;");
        playerFreezeOverlay.setVisible(false);
        playerFreezeOverlay.layoutXProperty().bind(playerBox.layoutXProperty().subtract(10));
        playerFreezeOverlay.layoutYProperty().bind(playerBox.layoutYProperty().subtract(10));
        playerFreezeOverlay.prefWidthProperty().bind(playerBox.widthProperty().add(20));
        playerFreezeOverlay.prefHeightProperty().bind(playerBox.heightProperty().add(20));

        // -- Opponent Side (Top Right) --
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
        opponentFreezeOverlay.setStyle("-fx-background-color: rgba(173, 216, 230, 0.4); -fx-background-radius: 8;");
        opponentFreezeOverlay.setVisible(false);
        opponentFreezeOverlay.layoutXProperty().bind(opponentBox.layoutXProperty().subtract(10));
        opponentFreezeOverlay.layoutYProperty().bind(opponentBox.layoutYProperty().subtract(10));
        opponentFreezeOverlay.prefWidthProperty().bind(opponentBox.widthProperty().add(20));
        opponentFreezeOverlay.prefHeightProperty().bind(opponentBox.heightProperty().add(20));

        // -- Last Card Summary --
        lastCardSummary = styledLabel("Last card drawn: —");
        lastCardSummary.setWrapText(true);
        lastCardSummary.setMaxWidth(400); 
        lastCardSummary.setLayoutX(20);
        lastCardSummary.setLayoutY(220); // Moved up

        cardDisplay = new CardDisplay(root.widthProperty(), root.heightProperty());
        cardDisplay.getView().setLayoutY(260);  // Moved up 15%

        root.getChildren().addAll(
            turnLabel, 
            playerBox, playerFreezeOverlay,
            opponentBox, opponentFreezeOverlay,
            lastCardSummary, cardDisplay.getView()
        );
    }

    public void nextTurn() {
        turnCount++;
    }

    public void updateInfo(Monster current, Monster humanPlayer, Monster opponent, int lastDice) {
        turnLabel.setText("Turn: " + turnCount + "   |   Current Turn: " + current.getName() + "   |   Last Dice: " + (lastDice >= 1 ? lastDice : "—"));

        pName.setText("Name: " + humanPlayer.getName());
        pRole.setText("Role: " + humanPlayer.getRole() + (humanPlayer.isConfused() ? " (CONFUSED)" : ""));
        pType.setText("Type: " + humanPlayer.getClass().getSimpleName());
        pEnergy.setText("Energy: " + humanPlayer.getEnergy());
        pPos.setText("Position: " + humanPlayer.getPosition());
        pStatus.setText("Status: " + buildStatusString(humanPlayer));
        
        playerFreezeOverlay.setVisible(humanPlayer.isFrozen());

        oName.setText("Name: " + opponent.getName());
        oRole.setText("Role: " + opponent.getRole() + (opponent.isConfused() ? " (CONFUSED)" : ""));
        oType.setText("Type: " + opponent.getClass().getSimpleName());
        oEnergy.setText("Energy: " + opponent.getEnergy());
        oPos.setText("Position: " + opponent.getPosition());
        oStatus.setText("Status: " + buildStatusString(opponent));
        
        opponentFreezeOverlay.setVisible(opponent.isFrozen());
    }

    private String buildStatusString(Monster m) {
        String s = "";
        if (m.isFrozen()) s += "Frozen ";
        if (m.isConfused()) s += "Confused ";
        return s.isEmpty() ? "Normal" : s;
    }

    public void setLastCardSummary(String cardName, String effectText) {
        String name = cardName != null ? cardName : "—";
        String eff = effectText != null ? effectText : "";
        if (eff.length() > 72) {
            eff = eff.substring(0, 71) + "…";
        }
        lastCardSummary.setText("Last card drawn: " + name + " — " + (eff.isEmpty() ? "(no description)" : eff));
    }

    public CardDisplay getCardDisplay() { return cardDisplay; }
    public int getTurnCount() { return turnCount; }
    public Pane getView() { return root; } 

    private Label styledLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #eceff1; -fx-font-family: 'Jua', sans-serif; -fx-font-size: 16px; -fx-effect: dropshadow(gaussian, black, 3, 0.8, 1, 1);");
        return l;
    }
}