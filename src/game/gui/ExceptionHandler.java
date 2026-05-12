package game.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * ExceptionHandler — central class for ALL error/warning popups in the game.
 *
 * Rules (from Milestone 3):
 *  - Every invalid action must notify the player with a reason
 *  - The game must NEVER stop/terminate on an exception
 *  - Closing the popup must NOT close the game
 *
 * How it works:
 *  - Uses Alert.showAndWait() → game pauses until player clicks OK
 *  - After OK is clicked, game continues exactly where it left off
 *  - The window X button still works (closes game) — popups don't affect that
 *
 * Usage from anywhere:
 *   ExceptionHandler.showInvalidMove("Cannot land on opponent.");
 *   ExceptionHandler.showNotEnoughEnergy();
 */
public class ExceptionHandler {

    // ── SPECIFIC POPUP METHODS ───────────────────────────────────────────────

    /** Shown when engine throws InvalidMoveException (e.g. landing on opponent). */
    public static void showInvalidMove(String reason) {
        showPopup(
            AlertType.WARNING,
            "Invalid Move",
            reason.isEmpty() ? "That move is not allowed." : reason
        );
    }

    /** Shown when player tries to roll dice a second time in the same turn. */
    public static void showAlreadyRolled() {
        showPopup(
            AlertType.WARNING,
            "Already Rolled",
            "You have already rolled the dice this turn!\nWait for your next turn."
        );
    }

    /**
     * Shown when player tries to activate power-up AFTER rolling.
     * Rule: power-up must be activated BEFORE the dice roll.
     */
    public static void showInvalidPowerUp() {
        showPopup(
            AlertType.WARNING,
            "Too Late for Power-Up",
            "You can only activate your power-up BEFORE rolling the dice!"
        );
    }

    /**
     * Shown when engine throws OutOfEnergyException.
     * Power-up costs 500 energy — player doesn't have enough.
     */
    public static void showNotEnoughEnergy() {
        showPopup(
            AlertType.WARNING,
            "Not Enough Energy",
            "You need at least 500 energy to activate your power-up!\n" +
            "Collect more energy by landing on doors."
        );
    }

    /**
     * Shown when a frozen monster tries to act.
     * (In normal flow the engine handles this — this is a fallback display.)
     */
    public static void showFrozenWarning() {
        showPopup(
            AlertType.INFORMATION,
            "Monster Frozen",
            "Your monster is frozen!\nYour turn will be automatically skipped."
        );
    }

    /** General-purpose invalid action popup with a custom message. */
    public static void showInvalidAction(String message) {
        showPopup(
            AlertType.WARNING,
            "Invalid Action",
            message
        );
    }

    /** Shown for unexpected errors (should rarely appear). */
    public static void showGenericError(String details) {
        showPopup(
            AlertType.ERROR,
            "Something Went Wrong",
            "An unexpected error occurred:\n" + details +
            "\n\nThe game will continue."
        );
    }

    // ── CORE POPUP BUILDER ───────────────────────────────────────────────────

    /**
     * Builds and shows an Alert dialog.
     *
     * showAndWait() means:
     *  - Game rendering is paused while popup is open
     *  - When player clicks OK or closes popup, game resumes
     *  - The game window itself is NOT closed
     */
    private static void showPopup(AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);   // no secondary header — cleaner look
        alert.showAndWait();         // blocks until player dismisses — safe, won't crash
    }
}
