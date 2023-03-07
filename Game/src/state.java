import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class state
{
    public static final int N = 8;
    public static final int toWin = 4;
    private static final int aiUtilWeight = 10;
    private static final int playerUtilWeight = 10;
    private final char[][] board;
    private int moveCounter = 0;
    private move previousMove;

    public state createState(int row, int col, state currentState)
    {
        state aState = new state();
        char[][] newBoard = aState.getBoard();
        char[][] currentBoard = currentState.getBoard();
        for (int i = 0; i < N; i++) {
            System.arraycopy(currentBoard[i], 0, newBoard[i], 0, N);
        }
        if (currentState.getLastAction().getPlayer() == person.ai) {
            aState.move(new move(row, col, person.player));
        } else if (currentState.getLastAction().getPlayer() == person.player) {
            aState.move(new move(row, col, person.ai));
        }

        return aState;
    }

    private enum Direction
    {
        up, down, left, right
    }

    public void move(move action)
    {
        board[action.getRow()][action.getCol()] = action.getPlayer().value();
        previousMove = action;
        moveCounter++;
    }
    state()
    {
        board = new char[N][N];
    }

    //returns a list of possible actions for the ai to take
    public List<state> getSuccessors()
    {
        List<state> successors = new ArrayList<>();
        final char def = '\u0000';
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == def) {
                    successors.add(createState(i, j, this));
                }
            }
        }
        Collections.shuffle(successors);
        return successors;
    }

    public int utility()
    {
        //if board is filled, return 0 and game end
        if (N * N== moveCounter)
        { return 0; }
        int utility = 0;
        // integer represents the longest chain of the ai. Utility val is the weight, etc
        int longestChain = longLink(person.ai);
        utility += longestChain * aiUtilWeight;
        longestChain = longLink(person.player);
        utility -= longestChain * playerUtilWeight;

        // we don't want the ai making moves in the corner because it generally isn't optimal in a connection game
        // these for loops center the moves towards the middle of the board
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == person.ai.value()) {
                    utility = Math.abs(4 - j)-utility;
                    utility = Math.abs(4 - i)-utility;
                } else if (board[i][j] == person.player.value()) {
                    utility = Math.abs(4 - j)+utility;
                    utility = Math.abs(4 - i)+utility;
                }
            }
        }
        int[] topNumCharsRowCol = TopNumCharacters();
        //least num of pieces ai and player have to place to win
        int[] numCharsToWin = getNumCharsToWin(topNumCharsRowCol);
        int topCharacters = (topNumCharsRowCol[0] + topNumCharsRowCol[1]) - (topNumCharsRowCol[2] + topNumCharsRowCol[3]);
        int numCharsLeft = (numCharsToWin[0] + numCharsToWin[1]) - (numCharsToWin[2] + numCharsToWin[3]);
        //add to utility weight
        utility += (topCharacters) + (numCharsLeft);
        return utility;
    }

    private int longLink(person player)
    {
        int longest = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                longest = Math.max(longest, lengthFromCell(i, j, player));
            }
        }
        return longest;
    }

    private int lengthFromCell(int i, int j, person player)
    {
        return Math.max(
                longLinkWDir(i, j, Direction.up, player) + longLinkWDir(i, j, Direction.down, player),
                longLinkWDir(i, j, Direction.left, player) + longLinkWDir(i, j, Direction.right, player));
    }

    private int longLinkWDir(int row, int col, Direction dir, person player)
    {
        int l = 0;
        int i = row;
        int j = col;
        if (dir == Direction.up && i == 0 || dir == Direction.down
                && i == N - 1 || dir == Direction.left && j == 0
                || dir == Direction.right && j == N - 1) {
            return l;
        }
        while (i >= 0 && i < N && j >= 0 && j < N && board[i][j] == player.value())
        {
            if (dir == Direction.up) { i--; }
            else if (dir == Direction.down) { i++; }
            else if (dir == Direction.left) { j--; }
            else if (dir == Direction.right) { j++; }
            l++;
        }
        if (linkCompletion(row, col, dir, player)) { return l - 1; }
        return 0;
    }

    private boolean linkCompletion(int row, int col, Direction dir, person player)
    {
        person opp = player == person.ai ? person.player : person.ai;
        int possL = 0;
        switch (dir) {
            case up, down -> {
                for (int i = row; i < N; i++) {
                    if (board[i][col] != opp.value()) {
                        possL++;
                        if (possL == 4) {
                            return true;
                        }
                    }
                }
                for (int i = row - 1; i >= 0; i--) {
                    if (board[i][col] != opp.value()) {
                        possL++;
                        if (possL == 4) {
                            return true;
                        }
                    }
                }
            }
            case left, right -> {
                for (int i = col; i < N; i++) {
                    if (board[row][i] != opp.value()) {
                        possL++;
                        if (possL == 4) {
                            return true;
                        }
                    }
                }
                for (int i = col - 1; i >= 0; i--) {
                    if (board[row][i] != opp.value()) {
                        possL++;
                        if (possL == 4) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public int[] TopNumCharacters()
    {
        int[] topNumCharsRowCol = new int[4];
        int aiNumCharsRow = 0;
        int aiNumCharsCol = 0;
        int playerNumCharsRow = 0;
        int playerNumCharsCol = 0;
        // top num of characters for rows for ai and player
        for (int row = 0; row < N; row++) {
            int aiNum = 0;
            int playerNum = 0;
            for (int col = 0; col < N; col++) {
                if (getBoard()[row][col] == person.ai.value()) {
                    aiNum++;
                }
                if (getBoard()[row][col] == person.player.value()) {
                    playerNum++;
                }
            }
            if (aiNum > aiNumCharsRow) {
                aiNumCharsRow = aiNum;
            }
            if (playerNum > playerNumCharsRow) {
                playerNumCharsRow = playerNum;
            }
        }
        // top num of characters for cols for ai and player
        for (int c = 0; c < N; c++) {
            int aiNum = 0;
            int playerNum = 0;
            for (int r = 0; r < N; r++) {
                if (getBoard()[r][c] == person.ai.value()) {
                    aiNum++;
                }
                if (getBoard()[r][c] == person.player.value()) {
                    playerNum++;
                }
            }
            if (aiNum > aiNumCharsCol) {
                aiNumCharsCol = aiNum;
            }
            if (playerNum > playerNumCharsCol) {
                playerNumCharsCol = playerNum;
            }
        }
        topNumCharsRowCol[0] = aiNumCharsRow;
        topNumCharsRowCol[1] = aiNumCharsCol;
        topNumCharsRowCol[2] = playerNumCharsRow;
        topNumCharsRowCol[3] = playerNumCharsCol;
        return topNumCharsRowCol;
    }

    public int[] getNumCharsToWin(int[] topNumCharsRowCol)
    {
        int[] numCharsToWin = new int[4];
        numCharsToWin[0] = toWin - topNumCharsRowCol[0];//Comp Row Chars to Win
        numCharsToWin[1] = toWin - topNumCharsRowCol[1];//Comp Col Chars to Win
        numCharsToWin[2] = toWin - topNumCharsRowCol[2];//Opp Row Chars to Win
        numCharsToWin[3] = toWin - topNumCharsRowCol[3];//Opp Col Chars to Win
        return numCharsToWin;
    }
    public char[][] getBoard() {
        return this.board;
    }
    public int getMoveCounter() {
        return moveCounter;
    }
    public move getLastAction() {
        return previousMove;
    }
    public void setLastAction(move lastAction)
    {
        this.previousMove = lastAction;
    }
    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == '\u0000') {
                    s.append("-");
                } else {
                    s.append(board[i][j]);
                }
            }
            s.append("\n");
        }
        return s.toString();
    }

}