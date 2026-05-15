package game.gui;
import game.engine.monsters.Monster;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MonsterInfo {
private VBox view;
    private Label nameLabel;
    private Label detailsLabel; 
    private Label energyLabel;
    private ProgressBar energyBar;
    private Label posLabel;
    private Label statusLabel;

    public MonsterInfo(String title, String color) {
        Label header = new Label(title);
        header.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px; -fx-font-weight: bold;");

        nameLabel = new Label("---");
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        detailsLabel = new Label("Type: --- | Role: ---");
        detailsLabel.setStyle("-fx-text-fill: #7dbdde; -fx-font-size: 12px;");

        energyLabel = new Label("Energy: 0/1000");
        energyLabel.setStyle("-fx-text-fill: #ffe57f; -fx-font-size: 12px;");

        energyBar = new ProgressBar(0);
        energyBar.setPrefWidth(200);
        energyBar.setStyle("-fx-accent: " + color + ";");

        posLabel = new Label("Position: Cell 0");
        posLabel.setStyle("-fx-text-fill: #cfd8dc; -fx-font-size: 12px;");

        statusLabel = new Label("STATUS: READY");
        statusLabel.setStyle("-fx-text-fill: #00e676; -fx-font-size: 11px; -fx-font-weight: bold;");

        view = new VBox(8, header, nameLabel, detailsLabel, energyLabel, energyBar, posLabel, statusLabel);
        view.setPadding(new Insets(12));
        view.setStyle("-fx-background-color: #2a2a3e; -fx-background-radius: 10; -fx-border-color: #444466; -fx-border-width: 1;");
    }

    public void refresh(Monster m) {
        if (m == null) return;

        nameLabel.setText(m.getName().toUpperCase());
        detailsLabel.setText("TYPE: " + m.getClass().getSimpleName() + " | ROLE: " + m.getRole());
        
        energyLabel.setText("Energy: " + m.getEnergy() + " / 1000");
        energyBar.setProgress(m.getEnergy() / 1000.0);
        posLabel.setText("Position: Cell " + m.getPosition());

        if (m.isConfused()) {
            statusLabel.setText("STATUS: CONFUSED 😵");
            statusLabel.setTextFill(Color.web("#ff3636")); 
        } 
        
        else if (m.isFrozen()) {
            statusLabel.setText("STATUS: FROZEN ❄️");
            statusLabel.setTextFill(Color.web("#82eefa")); 
        } 
       
        else {
            statusLabel.setText("STATUS: ACTIVE ✅");
            statusLabel.setTextFill(Color.web("#00e676")); 
        }
    }

    public VBox getView() { return view; }
}
