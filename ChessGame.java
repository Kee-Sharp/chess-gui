import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class representing a game of chess
 * @author Instructors and skee8
 * @version 1.0
 */
public class ChessGame {

    private StringProperty event = new SimpleStringProperty(this, "NA");
    private StringProperty site = new SimpleStringProperty(this, "NA");
    private StringProperty date = new SimpleStringProperty(this, "NA");
    private StringProperty white = new SimpleStringProperty(this, "NA");
    private StringProperty black = new SimpleStringProperty(this, "NA");
    private StringProperty result = new SimpleStringProperty(this, "NA");
    private StringProperty opening = new SimpleStringProperty(this, "NA");
    private List<String> finalPosition;
    private List<Opening> openings;
    private List<String> moves;

    /**
     * constructor taking in all of the string values associated with the fields
     * @param  event <-
     * @param  site <-
     * @param  date <-
     * @param  white <-
     * @param  black <-
     * @param  result <-
     */
    public ChessGame(String event, String site, String date,
                     String white, String black, String result) {
        this.event.set(event);
        this.site.set(site);
        this.date.set(date);
        this.white.set(white);
        this.black.set(black);
        this.result.set(result);
        this.opening.set("NA");
        openings = new ArrayList<>();
        openings.add(new Opening("Guioco Piano",
            new ArrayList<>(Arrays.asList("e4 e5", "Nf3 Nc6", "Bc4 Bc5"))));
        openings.add(new Opening("Ruy Lopez",
            new ArrayList<>(Arrays.asList("e4 e5", "Nf3 Nc6", "Bb5"))));
        openings.add(new Opening("Sicilian Defence",
            new ArrayList<>(Arrays.asList("e4 c5"))));
        openings.add(new Opening("Queen's Gambit",
            new ArrayList<>(Arrays.asList("d4 d5", "c4"))));
        openings.add(new Opening("Philidor Defence",
            new ArrayList<>(Arrays.asList("e4 e5", "Nf3 d6"))));
        openings.add(new Opening("Indian Defence",
            new ArrayList<>(Arrays.asList("d4 Nf6"))));
        openings.add(new Opening("Italian Game",
            new ArrayList<>(Arrays.asList("e4 e5", "Nf3 Nc6", "Bc4"))));
        moves = new ArrayList<>();
    }
    /**
     * adds a move to the list of moves, then checks to see if the list of moves
     * now matches an opening
     * @param move the move to be added
     */
    public void addMove(String move) {
        moves.add(move);
        for (Opening o : openings) {
            boolean equals = true;
            if (moves.size() >= o.size()) {
                for (int i = 0; i < o.size(); i++) {
                    if (!moves.get(i).contains(o.moves().get(i))) {
                        equals = false;
                    }
                }
                if (equals) {
                    opening.set(o.name());
                }
            }
        }
    }
    /**
     * returns a move from the list of moves
     * @param  n the index of the move to be returned
     * @return   the particular move
     */
    public String getMove(int n) {
        return moves.get(n - 1);
    }
    /**
     * event getter method
     * @return a string representation of the event
     */
    public String getEvent() {
        return event.get();
    }
    /**
     * site getter method
     * @return a string representation of the site
     */
    public String getSite() {
        return site.get();
    }
    /**
     * date getter method
     * @return a string representation of the date
     */
    public String getDate() {
        return date.get();
    }
    /**
     * white player getter method
     * @return a string representation of the white player
     */
    public String getWhite() {
        return white.get();
    }
    /**
     * black player getter method
     * @return a string representation of the black player
     */
    public String getBlack() {
        return black.get();
    }
    /**
     * result getter method
     * @return a string representation of the result
     */
    public String getResult() {
        return result.get();
    }
    /**
     * opening getter method
     * @return a string representation of the opening
     */
    public String getOpening() {
        return opening.get();
    }
    /**
     * finalPosition getter method
     * @return [description]
     */
    public List<String> getFinalPosition() {
        return finalPosition;
    }
    /**
     * finalPosition setter method
     * @param finalPosition what the finalPosition will be set to
     */
    public void setFinalPosition(List<String> finalPosition) {
        this.finalPosition = finalPosition;
    }
}