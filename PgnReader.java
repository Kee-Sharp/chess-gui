import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates ChessGames from all pgn files within a folder
 * @author skee8
 * @version 2.0
 */
public class PgnReader {
    /**
     * Gets the data for a specific category
     * @param  tagName the category to look for
     * @param  game    the string representing the file to be searched
     * @return         the associated data if it exists
     */
    public static String tagValue(String tagName, String game) {
        String[] gamelines = game.split("\n");
        for (int i = 0; i < gamelines.length; i++) {
            String gameline = gamelines[i];
            if (gameline.regionMatches(1, tagName, 0, tagName.length())) {
                int first = gameline.indexOf("\"");
                int last = gameline.lastIndexOf("\"");
                return gameline.substring(first + 1, last);
            }
        }
        return "NOT GIVEN";
    }
    /**
     * creates a list of moves from a game
     * @param  game the string holding all of the moves concatenated
     * @return      list of separated moves
     */
    public static List<String> getMoves(String game) {
        List<String> allMoves = new ArrayList<String>();
        String[] gamelines = game.split("\n");
        String moveline = "";
        boolean start = false;
        for (int i = 0; i < gamelines.length; i++) {
            int moveslength = moveline.length();
            if (start
                && (getStringLetter(moveline, moveslength - 1).equals(" ")
                    || getStringLetter(gamelines[i], 0).equals("\\."))) {
                moveline += gamelines[i];
            } else if (start) {
                moveline += " " + gamelines[i];
            }
            if (gamelines[i].startsWith("1.")) {
                moveline = gamelines[i];
                start = true;
            }
        }
        String[] rounds = moveline.split("\\.");
        /*String[] moves = new String[m1.length - 1];
        for (int i = 1; i < m1.length; i++) {
            String s = m1[i].replaceAll("[0-9]\\.", "");
            moves[i - 1] = s;
        }
        return moves;*/
        if (rounds.length > 1) {
            for (int i = 1; i < rounds.length; i++) {
                String[] moves = rounds[i].split(" ");
                String result = "";
                int whiteindex = moves.length - 3;
                int blackindex = moves.length - 2;
                String endmove = moves[blackindex + 1];
                if ((!getStringLetter(endmove, 0).matches("\\d"))
                    || endmove.indexOf("-") > -1) {
                    whiteindex = moves.length - 2;
                    blackindex = moves.length - 1;
                }
                if (moves.length > 2
                    && !moves[blackindex].equals(tagValue("Result", game))) {
                    String whitemove = moves[whiteindex];
                    String blackmove = moves[blackindex];
                    result += whitemove + " " + blackmove;
                } else if (moves.length <= 2) {
                    String whitemove = moves[moves.length - 1];
                    result += whitemove;
                } else {
                    String whitemove = moves[whiteindex];
                    result += whitemove;
                }
                allMoves.add(result);
            }
        }
        return allMoves;

    }
    /**
     * Creates a modified version of the FEN Notation
     * @param  game String containing all of the moves
     * @return      list of strings representing the FEN for each row
     */
    public static List<String> finalPosition(String game) {
        String[][] board = new String[8][8];
        boardInitialize(board);
        List<String> rowPositions = new ArrayList<>();
        List<String> moves = getMoves(game);
        for (int m = 0; m < moves.size(); m++) {
            String move = moves.get(m);
            if (move.contains(" ")) {
                String[] plies = move.split(" ");
                boardMove(board, plies[0], true);
                boardMove(board, plies[1], false);
            } else {
                boardMove(board, move, true);
            }
        }
        for (int i = 0; i < board.length; i++) {
            String result = "";
            for (int j = 0; j < board[0].length; j++) {
                String c = board[i][j];
                result += (c.equals("0")) ? "1" : c;
            }
            rowPositions.add(result);
        }
        return rowPositions;
    }
    /**
     * creates the chess board at the starting position
     * @param board the String array to be initiated
     */
    public static void boardInitialize(String[][] board) {
        board[0] = new String[] {"r", "n", "b", "q", "k", "b", "n", "r"};
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == 1) {
                    board[i][j] = "p";
                } else if (i > 1 && i < 6) {
                    board[i][j] = "0";
                } else if (i == 6) {
                    board[i][j] = "P";
                }
            }
        }
        board[7] = new String[] {"R", "N", "B", "Q", "K", "B", "N", "R"};
    }
    /**
     * gets a particular letter
     * @param  str the larger string
     * @param  pos the position to be accessed
     * @return     the letter at that position
     */
    public static String getStringLetter(String str, int pos) {
        return String.valueOf(str.charAt(pos));
    }
    /**
     * moves the pieces into place
     * @param board the board containing the pieces
     * @param move  String representation of the move
     * @param white whether or not the move was a white one or not
     */
    public static void boardMove(String[][] board, String move, boolean white) {
        if (move.length() == 0) {
            return;
        }
        String piece = "";
        String overridepiece = null;
        int file = 0;
        int rank = 0;
        boolean capture = false;
        int dfile = -1;
        int drank = -1;
        String firstletter = getStringLetter(move, 0);
        String secondletter = getStringLetter(move, 1);
        String stlast = getStringLetter(move, move.length() - 2);
        String lastletter = getStringLetter(move, move.length() - 1);
        String special = "?!#+";
        String lower = "abcdefgh";
        int fileindex;
        int rankindex;
        int movesize = move.length();
        int lowerletters = 0;
        int numbers = 0;
        for (int i = 1; i < move.length(); i++) {
            if (lower.indexOf(getStringLetter(move, i)) > -1) {
                lowerletters++;
            }
            if (getStringLetter(move, i).matches("\\d")) {
                numbers++;
            }
        }
        if (special.indexOf(lastletter) > -1 && special.indexOf(stlast) > -1) {
            movesize = movesize - 2;
        } else if (special.indexOf(lastletter) > -1) {
            movesize--;
        }
        if (move.indexOf("=") > -1) {
            overridepiece = getStringLetter(move, movesize - 1);
            movesize = movesize - 2;
        }
        fileindex = movesize - 2;
        rankindex = movesize - 1;
        if (lowerletters >= 2 && numbers >= 2) {
            dfile = move.charAt(1) - 'a';
            drank = 56 - move.charAt(2);
            movesize = movesize - 2;
        } else if (lowerletters >= 2) {
            dfile = move.charAt(1) - 'a';
            movesize--;
        } else if (numbers >= 2) {
            drank = 56 - move.charAt(1);
            movesize--;
        }
        if (movesize == 2) {
            piece = "P";
            file = move.charAt(fileindex) - 'a';
            rank = 56 - move.charAt(rankindex);
        } else if (movesize == 3 && firstletter.equals("O")) {
            piece = "K";
            file = 6;
            rank = 7;
            String otherpiece = "KC";
            if (!white) {
                otherpiece = otherpiece.toLowerCase();
                rank = 0;
            }
            removePiece(board, otherpiece, 0, 0, false, dfile, drank);
            if (white) {
                board[7][5] = "R";
            } else {
                board[0][5] = "r";
            }
        } else if (movesize == 3) {
            piece = firstletter;
            file = move.charAt(fileindex) - 'a';
            rank = 56 - move.charAt(rankindex);
        } else if (movesize == 4
            && getStringLetter(move, fileindex - 1).equals("x")) {
            capture = true;
            piece = (lower.indexOf(firstletter) == -1) ? firstletter : "P";
            if (lower.indexOf(firstletter) > -1) {
                dfile = move.charAt(0) - 'a';
            }
            file = move.charAt(fileindex) - 'a';
            rank = 56 - move.charAt(rankindex);
        } else if (movesize == 5 && firstletter.equals("O")) {
            piece = "K";
            file = 2;
            rank = 7;
            String otherpiece = "QC";
            if (!white) {
                otherpiece = otherpiece.toLowerCase();
                rank = 0;
            }
            removePiece(board, otherpiece, 0, 0, false, dfile, drank);
            if (white) {
                board[7][3] = "R";
            } else {
                board[0][3] = "r";
            }
        }
        if (!white && overridepiece != null) {
            piece = piece.toLowerCase();
            overridepiece = overridepiece.toLowerCase();
        } else if (!white) {
            piece = piece.toLowerCase();
        }
        removePiece(board, piece, file, rank, capture, dfile, drank);
        board[rank][file] = (overridepiece != null) ? overridepiece : piece;
    }
    /**
     * removes the pieces from where the were
     * @param board   the board containing the pieces
     * @param piece   piece to be removed
     * @param file    the file the piece moves to
     * @param rank    the rank the piece moves to
     * @param capture a boolean used for en passant
     * @param dfile   originating file, if disambiguation
     * @param drank   originating rank, if disambiguation
     */
    public static void removePiece(String[][] board, String piece, int file,
        int rank, boolean capture, int dfile, int drank) {
        if (dfile > -1 && drank > -1) {
            board[drank][dfile] = "0";
        } else if (dfile > -1) {
            for (int i = 0; i < board.length; i++) {
                if (board[i][dfile].equals(piece)) {
                    board[i][dfile] = "0";
                }
            }
        } else if (drank > -1) {
            for (int i = 0; i < board.length; i++) {
                if (board[drank][i].equals(piece)) {
                    board[drank][i] = "0";
                }
            }
        } else {
            if (piece.equalsIgnoreCase("P") && !capture) {
                if (piece.equals("P") && board[rank + 2][file].equals("P")
                    && board[rank + 1][file].equals("0")) {
                    board[rank + 2][file] = "0";
                } else if (piece.equals("P")
                    && board[rank + 1][file].equals("P")) {
                    board[rank + 1][file] = "0";
                } else if (piece.equals("p")
                    && board[rank - 2][file].equals("p")
                    && board[rank - 1][file].equals("0")) {
                    board[rank - 2][file] = "0";
                } else if (piece.equals("p")
                    && board[rank - 1][file].equals("p")) {
                    board[rank - 1][file] = "0";
                }
            } else if (piece.equalsIgnoreCase("P")) {
                if (board[rank][file].equals("0") && piece.equals("P")) {
                    board[rank + 1][file] = "0";
                } else if (board[rank][file].equals("0") && piece.equals("p")) {
                    board[rank - 1][file] = "0";
                }
                for (int i = 0; i < board.length; i++) {
                    for (int j = 0; j < board[0].length; j++) {
                        if ((((i - rank) == 1 && piece.equals("P"))
                            || (((rank - i) == 1) && piece.equals("p")))
                            && Math.abs(j - file) == 1) {
                            if (board[i][j].equals(piece)) {
                                board[i][j] = "0";
                            }
                        }
                    }
                }
            } else if (piece.equalsIgnoreCase("B")
                || piece.equalsIgnoreCase("Q")) {
                for (int i = 0; i < board.length; i++) {
                    for (int j = 0; j < board[0].length; j++) {
                        if (i - j == rank - file || i + j == rank + file) {
                            if (board[i][j].equals(piece)
                                && diClear(board, i, j, rank, file)) {
                                board[i][j] = "0";
                            }
                        }
                    }
                }
            } else if (piece.equalsIgnoreCase("N")) {
                for (int i = 0; i < board.length; i++) {
                    for (int j = 0; j < board[0].length; j++) {
                        if (((Math.abs(i - rank) == 2
                            && Math.abs(j - file) == 1)
                            || (Math.abs(i - rank) == 1
                                && Math.abs(j - file) == 2))
                            && board[i][j].equals(piece)) {
                            board[i][j] = "0";
                        }
                    }
                }
            } else if (piece.equalsIgnoreCase("R")) {
                int incrememnt = 1;
                for (int i = 0; i < board.length; i++) {
                    if (board[rank][i].equals(piece)
                        && rowClear(board, rank, i, file)) {
                        board[rank][i] = "0";
                    }
                    if (board[i][file].equals(piece)
                        && colClear(board, i, rank, file)) {
                        board[i][file] = "0";
                    }
                }
            } else if (piece.equalsIgnoreCase("K")) {
                for (int i = 0; i <= board.length; i++) {
                    int row = (i / 3) + rank - 1;
                    int col = (i % 3) + file - 1;
                    row = Math.abs(row % 8);
                    col = Math.abs(col % 8);
                    if (board[row][col].equals(piece)) {
                        board[row][col] = "0";
                    }
                }
            } else if (piece.length() == 2
                && piece.substring(1, 2).equalsIgnoreCase("C")) {
                int krow = 0;
                int kcol = 4;
                int rrow = 0;
                int rcol = 0;
                if (piece.substring(1, 2).equals("C")) {
                    krow = 7;
                    rrow = 7;
                }
                if (piece.substring(0, 1).equalsIgnoreCase("K")) {
                    rcol = 7;
                }
                board[krow][kcol] = "0";
                board[rrow][rcol] = "0";
            }
            if (piece.equalsIgnoreCase("Q")) {
                for (int i = 0; i < board.length; i++) {
                    if (board[rank][i].equals(piece)
                        && rowClear(board, rank, i, file)) {
                        board[rank][i] = "0";
                    }
                    if (board[i][file].equals(piece)
                        && colClear(board, i, rank, file)) {
                        board[i][file] = "0";

                    }
                }
            }
        }
    }
    /**
     * checks if a row is clear between two places
     * @param  board the board containing the pieces
     * @param  rank  the row to be check
     * @param  f1    column one
     * @param  f2    column two
     * @return       whether or not the row is clear
     */
    public static boolean rowClear(String[][] board, int rank, int f1, int f2) {
        boolean result = true;
        if (f1 < f2) {
            for (int i = f1 + 1; i < f2; i++) {
                if (!board[rank][i].equals("0")) {
                    result = false;
                }
            }
        } else if (f1 > f2) {
            for (int i = f2 + 1; i < f1; i++) {
                if (!board[rank][i].equals("0")) {
                    result = false;
                }
            }
        }
        return result;
    }
    /**
     * checks if a row is clear between two places
     * @param  board the board containing the pieces
     * @param  r1  row one
     * @param  r2    row two
     * @param  file    the column to be checked
     * @return     whether or not the column is clear
     */
    public static boolean colClear(String[][] board, int r1, int r2, int file) {
        boolean result = true;
        if (r1 < r2) {
            for (int i = r1 + 1; i < r2; i++) {
                if (!board[i][file].equals("0")) {
                    result = false;
                }
            }
        } else if (r1 > r2) {
            for (int i = r2 + 1; i < r1; i++) {
                if (!board[i][file].equals("0")) {
                    result = false;
                }
            }
        }
        return result;
    }
    /**
     * checks if a row is clear between two places
     * @param  board the board containing the pieces
     * @param  r1    row one
     * @param  f1    row one
     * @param  r2    row two
     * @param  f2    file two
     * @return       whether or not the diagonal is clear
     */
    public static boolean diClear(String[][] board, int r1, int f1, int r2,
                                int f2) {
        boolean result = true;
        if (r1 < r2 && f1 < f2) {
            for (int x = f1 + 1; x < f2; x++) {
                //y-x = r1-f1
                if (!board[x + r1 - f1][x].equals("0")) {
                    result = false;
                }
            }
        } else if (r1 < r2) {
            for (int x = f2 + 1; x < f1; x++) {
                //y+x = r1+f1
                if (!board[r1 + f1 - x][x].equals("0")) {
                    result = false;
                }
            }
        } else if (r1 > r2 && f1 < f2) {
            for (int x = f1 + 1; x < f2; x++) {
                if (!board[r1 + f1 - x][x].equals("0")) {
                    result = false;
                }
            }
        } else {
            for (int x = f2 + 1; x < f1; x++) {
                if (!board[x + r1 - f1][x].equals("0")) {
                    result = false;
                }
            }
        }
        return result;
    }
    /**
     * converts a pgn file to a string
     * @param  path the pgn file path
     * @return      a String with all of the content of the file
     */
    public static String fileContent(String path) {
        System.out.println(path);
        Path file = Paths.get(path);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                // Add the \n that's removed by readline()
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
            System.exit(1);
        }
        return sb.toString();
    }
    /**
     * creates ChessGames with all of the data from the pgn files
     * @return a list of those games
     */
    public static List<ChessGame> getChessGames() {
        List<ChessGame> games = new ArrayList<ChessGame>();
        File hw6 = new File(new File("./pgnGames").getAbsolutePath());
        File[] pgnFiles = hw6.listFiles(pathname -> {
                return pathname.getName().contains(".pgn");
            });
        for (File f : pgnFiles) {
            String data = fileContent("pgnGames/" + f.getName());
            ChessGame game = new ChessGame(tagValue("Event", data),
                tagValue("Site", data), tagValue("Date", data),
                tagValue("White", data), tagValue("Black", data),
                tagValue("Result", data));
            game.setFinalPosition(finalPosition(data));
            List<String> moves = getMoves(data);
            for (String s : moves) {

                game.addMove(s);
            }
            games.add(game);
        }
        return games;
    }
}
