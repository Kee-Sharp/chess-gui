import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Creates a table for all games in a ChessDB
 * @author skee8
 * @version  1.2
 */
public class ChessGui extends Application {

    private final TableView<ChessGame> table = new TableView<>();

    /**
     * makes a TableView of ChessGames and their metadata, creates an alert with
     * the meta data, moves, and final position when a game is viewed
     * @param stage [description]
     */
    public void start(Stage stage) {
        ChessDb chessDatabase = new ChessDb();
        ObservableList<ChessGame> data = FXCollections.observableArrayList(
            chessDatabase.getGames());
        data.addAll(PgnReader.getChessGames());
        makeTable(data);

        TextField textField = new TextField();
        textField.setPromptText("Search By Any Category");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
                String input = textField.getText();
                table.getSelectionModel().clearSelection();
                search(input, data);
                textField.clear();
            });
        searchButton.defaultButtonProperty().bind(Bindings.isNotEmpty(textField
            .textProperty()));
        Button switchSearch = new Button("Advanced Search");
        VBox searches = new VBox();
        searches.getChildren().addAll(searchButton, switchSearch);
        searches.setAlignment(Pos.CENTER);
        Button viewButton = new Button("View Game");
        viewButton.disableProperty().bind(Bindings.isEmpty(table.
            getSelectionModel().getSelectedItems()));
        viewButton.setOnAction(e -> {
                ObservableList<ChessGame> games = table.getSelectionModel()
                    .getSelectedItems();
                for (ChessGame c : games) {
                    viewGameData(c, true);
                }
            });
        viewButton.defaultButtonProperty().bind(Bindings.isNotEmpty(table
            .getSelectionModel().getSelectedItems()));
        viewButton.defaultButtonProperty().bind(Bindings.not(searchButton
            .defaultButtonProperty()));

        Button clear = new Button("Clear Selection");
        clear.setOnAction(e -> table.getSelectionModel().clearSelection());
        clear.disableProperty().bind(viewButton.disableProperty());

        Button dismissButton = new Button("Dismiss");
        dismissButton.setOnAction(e -> Platform.exit());
        dismissButton.setCancelButton(true);

        HBox hbox = new HBox();
        table.setOnMousePressed(e -> hbox.requestFocus());
        hbox.setSpacing(40);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(textField, searches, viewButton, clear,
            dismissButton);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(table, hbox);

        HBox alternateSearch = makeAlternateSearch(data, hbox,
            vbox);
        switchSearch.setOnAction(e -> {
                vbox.getChildren().setAll(table, alternateSearch);
            });
        for (Node child : vbox.getChildren()) {
            child.setOnMouseEntered(e -> {
                    textField.requestFocus();
                });
        }
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.setTitle("Chess Database");
        stage.setWidth(768);
        stage.show();
    }
    /**
     * initialize the Table
     * @param list the list of ChessGames to start out with
     */
    public void makeTable(ObservableList<ChessGame> list) {
        TableColumn<ChessGame, String> event = new TableColumn<>("Event");
        TableColumn<ChessGame, String> site = new TableColumn<>("Site");
        TableColumn<ChessGame, String> date = new TableColumn<>("Date");
        TableColumn<ChessGame, String> white = new TableColumn<>("White");
        TableColumn<ChessGame, String> black = new TableColumn<>("Black");
        TableColumn<ChessGame, String> result = new TableColumn<>("Result");
        TableColumn<ChessGame, String> opening = new TableColumn<>("Opening");

        event.setCellValueFactory(new PropertyValueFactory<>("event"));
        site.setCellValueFactory(new PropertyValueFactory<>("site"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        white.setCellValueFactory(new PropertyValueFactory<>("white"));
        black.setCellValueFactory(new PropertyValueFactory<>("black"));
        result.setCellValueFactory(new PropertyValueFactory<>("result"));
        opening.setCellValueFactory(new PropertyValueFactory<>("opening"));

        event.setPrefWidth(160);
        site.setPrefWidth(130);
        date.setPrefWidth(70);
        white.setPrefWidth(120);
        black.setPrefWidth(120);
        result.setPrefWidth(50);
        opening.setPrefWidth(100);

        table.setPrefHeight(410);
        table.setItems(list);
        table.getColumns().setAll(event, site, date, white, black, result,
            opening);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    /**
     * looks for and selects a particular ChessGame based on a keyword
     * @param s    the user's input to be matched to a game
     * @param list the list of ChessGames to be searched
     */
    public void search(String s, ObservableList<ChessGame> list) {
        for (ChessGame c : list) {
            if (s.equals(c.getEvent()) || s.equals(c.getSite()) || s.equals(c
                .getDate()) || s.equals(c.getWhite()) || s.equals(c.getBlack())
                 || s.equals(c.getResult()) || s.equals(c.getOpening())) {
                table.getSelectionModel().select(c);
            }
        }
    }
    /**
     * a more complicated search with the option to switch back to the default
     * search method
     * @param  list          the list of ChessGames to be searched
     * @param  other         the previous HBox containing the normal buttons
     * @param  vb            the VBox is added to reset the other HBox as a
     * child
     * @return               HBox containing the alternate search
     */
    public HBox makeAlternateSearch(ObservableList<ChessGame> list, HBox other,
        VBox vb) {
        HBox hbox = new HBox();
        TextField event = new TextField();
        event.setPromptText("Search Event");
        event.setPrefWidth(85);
        TextField site = new TextField();
        site.setPromptText("Search Site");
        site.setPrefWidth(75);
        TextField date = new TextField();
        date.setPromptText("Search Date");
        date.setPrefWidth(80);
        TextField white = new TextField();
        white.setPromptText("Search White");
        white.setPrefWidth(85);
        TextField black = new TextField();
        black.setPromptText("Search Black");
        black.setPrefWidth(80);
        TextField result = new TextField();
        result.setPromptText("Search Result");
        result.setPrefWidth(85);
        TextField opening = new TextField();
        opening.setPromptText("Search Opening");
        opening.setPrefWidth(100);
        Button search = new Button("Search");
        search.setDefaultButton(true);
        Button back = new Button("Back");
        List<TextField> fields = new ArrayList<>(Arrays.asList(event, site, date
            , white, black, result, opening));
        search.setOnAction(ev -> {
                table.getSelectionModel().clearSelection();
                for (ChessGame c : list) {
                    String e = event.getText();
                    String s = site.getText();
                    String d = date.getText();
                    String w = white.getText();
                    String b = black.getText();
                    String r = result.getText();
                    String o = opening.getText();
                    if ((e.isEmpty() || e.equals(c.getEvent()))
                        && (s.isEmpty() || s.equals(c.getSite()))
                        && (d.isEmpty() || d.equals(c.getDate()))
                        && (w.isEmpty() || w.equals(c.getWhite()))
                        && (b.isEmpty() || b.equals(c.getBlack()))
                        && (r.isEmpty() || r.equals(c.getResult()))
                        && (o.isEmpty() || o.equals(c.getOpening()))) {
                        table.getSelectionModel().select(c);
                    }
                }
                for (TextField t : fields) {
                    t.clear();
                }
                search.setDefaultButton(false);
                vb.getChildren().setAll(table, other);
            });
        back.setOnAction(e -> {
                for (TextField t : fields) {
                    t.clear();
                }
                vb.getChildren().setAll(table, other);
            });
        hbox.getChildren().addAll(event, site, date, white, black, result,
            opening, search, back);
        return hbox;
    }
    /**
     * creates an Alert with the metadata, moves, and board showing the final
     * position
     * @param c the ChessGame to be displayed
     */
    public void viewGameData(ChessGame c, boolean white) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(c.getEvent());
        alert.setHeaderText(String.format(
            "Event: %s%nSite: %s%nDate: %s%nWhite: %s%nBlack: %s%nResult: %s%n"
            + "Opening: %s", c.getEvent(), c.getSite(), c.getDate(),
            c.getWhite(), c.getBlack(), c.getResult(), c.getOpening()));
        List<Image> pieces = new ArrayList<>(Arrays.asList(new Image("wking.png"
            ), new Image("wqueen.png"), new Image("wbishop.png"),
            new Image("wrook.png"), new Image("wknight.png"),
            new Image("wpawn.png"), new Image("bking.png"),
            new Image("bqueen.png"), new Image("bbishop.png"),
            new Image("brook.png"), new Image("bknight.png"),
            new Image("bpawn.png")));
        MyImageView wK = new MyImageView(pieces.get(0));
        MyImageView wQ = new MyImageView(pieces.get(1));
        MyImageView wB = new MyImageView(pieces.get(2));
        MyImageView wR = new MyImageView(pieces.get(3));
        MyImageView wN = new MyImageView(pieces.get(4));
        MyImageView wP = new MyImageView(pieces.get(5));
        MyImageView bK = new MyImageView(pieces.get(6));
        MyImageView bQ = new MyImageView(pieces.get(7));
        MyImageView bB = new MyImageView(pieces.get(8));
        MyImageView bR = new MyImageView(pieces.get(9));
        MyImageView bN = new MyImageView(pieces.get(10));
        MyImageView bP = new MyImageView(pieces.get(11));
        List<MyImageView> pieceImages = new ArrayList<>(Arrays.asList(wK, wQ, wB
            , wR, wN, wP, bK, bQ, bB, bR, bN, bP));
        GridPane board = ChessBoardGraphic.board();
        List<String> fenNames = new ArrayList<>(Arrays.asList("K", "Q", "B", "R"
            , "N", "P", "k", "q", "b", "r", "n", "p"));
        List<String> fP = c.getFinalPosition();
        List<String> copy = new ArrayList<>();
        for (int m = 0; m < 8; m++) {
            copy.add(fP.get(m));
        }
        int wPawnCounter = 0;
        int bPawnCounter = 0;
        if (!white) {
            Collections.reverse(copy);
            for (int l = 0; l < 8; l++) {
                copy.set(l, reverseString(copy.get(l)));
            }
        }
        for (int i = 0; i < 8; i++) {
                String row = copy.get(i);
                for (int j = 0; j < 8; j++) {
                    String space = PgnReader.getStringLetter(row, j);
                    for (int k = 0; k < fenNames.size(); k++) {
                        if (space.equals(fenNames.get(k))) {
                            board.add(pieceImages.get(k).use(), j, i);
                        }
                    }
                }
            }
        board.setPrefWidth(150);
        alert.setGraphic(board);
        String moves = "";
        boolean moremoves = true;
        int i = 1;
        while (moremoves) {
            try {
                moves += c.getMove(i) + " ";
                i++;
            } catch (Exception e) {
                moremoves = false;
            }
        }
        alert.setContentText(moves);

        ButtonType switchView = new ButtonType(white ? "Black Perspective" : "White Perspective");
        ButtonType ok = new ButtonType("OK", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(switchView, ok);

        Optional<ButtonType> option = alert.showAndWait();

        if (option.get() == switchView) {
            viewGameData(c, !white);
        } else if (option.get() == null) {
            Button cancelButton = ( Button ) alert.getDialogPane().lookupButton( ok );
            cancelButton.fire();
        }
    }
    public String reverseString(String s) {
        if (s.length() <= 1) {
            return s;
        } else {
            int size = s.length();
            return s.substring(size - 1, size) + reverseString(s.substring(1, size - 1)) + s.substring(0, 1);
        }
    }
}