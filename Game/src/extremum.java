import java.util.List;
import java.util.Random;

public class extremum
{
    private final Random rand = new Random();
    final char empty = '\u0000';
    private final int winC = 200;
    private long startTime;
    private boolean stop;

    // Returns a utility value
    public int getMinValue(state state, int alphaVal, int betaVal, int depth, long timeLimit)
    {
        long timeTracker = System.currentTimeMillis() - startTime;
        if (terminalTest(state) || depth == 0 || timeTracker >= timeLimit) {
            return state.utility();
        }
        // System.out.println(depth);
        List<state> successors = state.getSuccessors();
        for (state nextState : successors) {
            betaVal = Math.min(betaVal,
                    getMaxValue(nextState, alphaVal, betaVal, depth - 1, timeLimit));

            // Time is out, use whatever value the method is currently at
            if (Thread.interrupted()) {
                stop = true;
                return state.utility();
            }
            if (betaVal <= alphaVal) {
                break;
            }
        }
        return betaVal;
    }

    // Returns a utility value
    public int getMaxValue(state state, int alphaVal, int betaVal, int depth, long timeLimit)
    {
        // Time has run out, return whatever the current state holds
        long timeTracker = System.currentTimeMillis() - startTime;
        if (terminalTest(state) || depth == 0 || timeTracker >= timeLimit)
        {
            return state.utility();
        }
        // System.out.println(depth);
        List<state> successors = state.getSuccessors();
        for (state nextState : successors) {
            alphaVal = Math.max(alphaVal, getMinValue(nextState, alphaVal, betaVal, depth - 1, timeLimit));

            // Time is out, use whatever value the method is currently at
            if (Thread.interrupted()) {
                stop = true;
                return state.utility();
            }
            if (alphaVal >= betaVal) {
                break;
            }
        }
        return alphaVal;
    }

    //Gets the next state for the computer move
    public state getNextState(state state)
    {
        if (state.getMoveCounter() == 0) {
            //first turn in the game
            int middle = state.N / 2;
            int row = rand.ints(middle - 2, middle + 2).findFirst().getAsInt();
            int col = rand.ints(middle - 2, middle + 2).findFirst().getAsInt();
            state.move(new move(row, col, person.ai));
            return state;
        }

        Thread.interrupted();
        stop = false;
        state bestState = null;
        int bestPossible = Integer.MIN_VALUE;
        int currentScore = 0;
        List<state> successors = state.getSuccessors();
        long timeLimit = 7000;
        long searchTime = timeLimit / successors.size();

        //enhanced for loop to set current score and check if winner
        for (state successor : successors) {
            currentScore = iterative(successor, searchTime);
            if (currentScore > bestPossible) {
                bestPossible = currentScore;
                bestState = successor;
            }
            if (bestPossible >= winC || stop) {
                return bestState;
            }
        }
        return bestState;
    }

    public int iterative(state state, long searchTime)
    {
        int depth = 1;
        int best = 0;
        int alphaVal = Integer.MIN_VALUE;
        int betaVal = Integer.MAX_VALUE;
        startTime = System.currentTimeMillis();
        long endTime = startTime + searchTime;
        while (true) {
            long current = System.currentTimeMillis();
            if (current >= endTime) {
                break;
            }
            best = getMaxValue(state, alphaVal, betaVal, depth, endTime - current);
            // System.out.println(best);
            if (best >= winC || stop) {
                break;
            }

            depth++;

        }
        return best;
    }

    //If there is a winner, return true, else false
    public boolean terminalTest(state state)
    {

        char[][] board = state.getBoard();
        final int DIM = state.N;

        for (int r = 0; r < DIM; r++) {
            for (int c = 0; c < DIM; c++) {
                char player = board[r][c];
                if (player == empty)
                    continue;
                if (c + 3 < DIM && player == board[r][c + 1]
                        && player == board[r][c + 2]
                        && player == board[r][c + 3])
                    return true;
                if (r + 3 < DIM) {
                    if (player == board[r + 1][c] && player == board[r + 2][c]
                            && player == board[r + 3][c])
                        return true;
                }
            }
        }
        //in the off case there is no winner found even on this huge playing environment, return false
        return false;
    }
}
