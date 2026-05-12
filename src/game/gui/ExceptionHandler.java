package game.gui;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * In-game feedback: small corner toasts on the {@link GameView} layer.
 * Falls back to blocking {@link Alert} only when no game layer is attached (e.g. CSV load in {@link Main}).
 */
public final class ExceptionHandler {

    private static VBox toastColumn;

    private ExceptionHandler() {}

    /** Call once per {@link GameView}; adds a non-interactive toast stack on top of the game. */
    public static void attachToGameLayer(StackPane gameLayerRoot) {
        detach();
        toastColumn = new VBox(8);
        toastColumn.setAlignment(Pos.BOTTOM_RIGHT);
        toastColumn.setPickOnBounds(false);
        toastColumn.setMouseTransparent(true);
        toastColumn.setPadding(new Insets(0, 12, 12, 0));
        toastColumn.setMaxWidth(420);
        StackPane.setAlignment(toastColumn, Pos.BOTTOM_RIGHT);
        gameLayerRoot.getChildren().add(toastColumn);
    }

    public static void detach() {
        if (toastColumn != null && toastColumn.getParent() != null) {
            ((StackPane) toastColumn.getParent()).getChildren().remove(toastColumn);
        }
        toastColumn = null;
    }

    private static boolean hasToastHost() {
        return toastColumn != null && toastColumn.getParent() != null;
    }

    public static void showInvalidMove(String reason) {
        pushToast("warning", "Invalid move", reason.isEmpty() ? "That move is not allowed." : reason);
    }

    public static void showAlreadyRolled() {
        pushToast("warning", "Already rolled", "You already rolled this turn. Wait for your next turn.");
    }

    public static void showInvalidPowerUp() {
        pushToast("warning", "Power-up", "Activate power-up only before rolling the dice.");
    }

    public static void showNotEnoughEnergy() {
        pushToast("warning", "Not enough energy", "You need at least 500 energy to use your power-up.");
    }

    public static void showFrozenWarning() {
        pushToast("info", "Frozen", "This monster is frozen — turn will be skipped.");
    }

    public static void showInvalidAction(String message) {
        pushToast("warning", "Invalid action", message);
    }

    public static void showGenericError(String details) {
        if (hasToastHost()) {
            pushToast("error", "Error", details);
        } else {
            showBlockingAlert(AlertType.ERROR, "Something went wrong", details + "\n\nThe game will continue.");
        }
    }

    private static void pushToast(String kind, String title, String body) {
        String full = title + ": " + body;
        if (!hasToastHost()) {
            showBlockingAlert(
                "error".equals(kind) ? AlertType.ERROR : AlertType.WARNING,
                title,
                body
            );
            return;
        }

        Label line = new Label(full);
        line.setWrapText(true);
        line.setMaxWidth(400);
        String bg = switch (kind) {
            case "error" -> "#8b0000";
            case "info" -> "#1565c0";
            default -> "#bf6f00";
        };
        line.setStyle(
            "-fx-text-fill: #ffffff;" +
            "-fx-padding: 10 14;" +
            "-fx-background-color: " + bg + ";" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 13px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 8, 0.2, 0, 2);"
        );

        toastColumn.getChildren().add(0, line);
        while (toastColumn.getChildren().size() > 4) {
            toastColumn.getChildren().remove(toastColumn.getChildren().size() - 1);
        }

        PauseTransition hide = new PauseTransition(Duration.seconds(4.2));
        hide.setOnFinished(e -> {
            if (toastColumn != null) {
                toastColumn.getChildren().remove(line);
            }
        });
        hide.play();
    }

    private static void showBlockingAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
