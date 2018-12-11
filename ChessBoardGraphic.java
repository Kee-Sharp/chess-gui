import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

/**
 * Creates a chessboard node to be used by the View Game alert
 * @author James_D and skee8
 * @version 1.1
 */
public class ChessBoardGraphic {

    /**
     * Creates the board
     * @return the board
     */
    public static GridPane board() {
        GridPane root = new GridPane();
        final int size = 8;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                StackPane square = new StackPane();
                String color;
                if ((row + col) % 2 == 0) {
                    color = "peru";
                } else {
                    color = "peachpuff";
                }
                square.setStyle("-fx-background-color: " + color + ";");
                root.add(square, col, row);
            }
        }
        for (int i = 0; i < size; i++) {
            root.getColumnConstraints().add(new ColumnConstraints(19, 19, 19,
                Priority.ALWAYS, HPos.CENTER, true));
            root.getRowConstraints().add(new RowConstraints(19, 19, 19,
                Priority.ALWAYS, VPos.CENTER, true));
        }
        return root;
    }
}