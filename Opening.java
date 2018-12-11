import java.util.List;
/**
 * Convenient class for helping to define an opening
 * @author skee8
 * @version 1.0
 */
public class Opening {
    private String name;
    private List<String> moves;
    private int size;
    /**
     * Creates an Opening with the name and moves
     * @param  name  the name of the Opening
     * @param  moves the list of moves that define the opening
     */
    public Opening(String name, List<String> moves) {
        this.name = name;
        this.moves = moves;
        size = moves.size();
    }
    /**
     * name getter method
     * @return the name
     */
    public String name() {
        return name;
    }
    /**
     * moves getter method
     * @return the list of moves
     */
    public List<String> moves() {
        return moves;
    }
    /**
     * size getter method
     * @return the size
     */
    public int size() {
        return size;
    }
}