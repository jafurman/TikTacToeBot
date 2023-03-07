//Joshua Furman
//CS4200 Winter Intermission
//Project 3


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {
    private static final List<move> act = new ArrayList<>();

    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static void main(String[] args)
    {
        Scanner scan = new Scanner(System.in);
        extremum searcher = new extremum();
        state state = new state();
        System.out.println("Welcome to the large tik tac toe!! You can change the number of Xs or Os needed in a row in the move class.");
        System.out.println("[1] You Start");
        System.out.println("[2] Computer Starts");
        int user= scan.nextInt();
        run(state, searcher, user);
    }

    private static void run(state gameState, extremum gameSearcher, int currentPlayer) {
        move opponentMove;
        Scanner keyboard = new Scanner(System.in);
        time gameTimer = new time(Thread.currentThread());
        Thread timerThread;
        while (true)
        {
            if (currentPlayer==1) {
                System.out.print("Next Move? (answer in a1, b6, c4, etc format. Numbers go to 8 and letters go to H): ");
                opponentMove = move.actuallyMove(keyboard.nextLine());
                act.add(opponentMove);
                gameState.move(opponentMove);
                if (gameSearcher.terminalTest(gameState)) {
                    System.out.println("You beat the computer, good job! ");
                    System.exit(0);
                }
                // Start the 30-second timer
                timerThread = new Thread(gameTimer);
                timerThread.start(); // Start the timer
                gameState = gameSearcher.getNextState(gameState); // Search
                timerThread.interrupt(); // Cancel the timer
                act.add(gameState.getLastAction());
                printBoard(gameState, currentPlayer);
                // If terminal state, declare it and exit
                if (gameSearcher.terminalTest(gameState)) {
                    System.out.println("Aww, the computer beat you. Computer Wins!");
                    System.exit(0);
                }
            }
            if (currentPlayer==2) {
                // Start the 30-second timer to terminate after 30 seconds
                timerThread = new Thread(gameTimer);
                timerThread.start(); // Start the timer
                gameState = gameSearcher.getNextState(gameState); // Search
                timerThread.interrupt(); // Cancel the timer
                act.add(gameState.getLastAction());
                printBoard(gameState, currentPlayer);
                // If terminal state, declare it and exit
                if (gameSearcher.terminalTest(gameState)) {
                    //player wins
                    System.out.println("The ai wins!");
                    System.exit(0);
                }
                System.out.print("Next Move? (answer in a1, b6, c4, etc format): ");
                opponentMove = move.actuallyMove(keyboard.nextLine());
                gameState.move(opponentMove);
                act.add(opponentMove);
                if (gameSearcher.terminalTest(gameState)) {
                    System.out.println("The player wins!"); // opponent wins
                    System.exit(0);
                }
            }
        }
    }

    private static void printBoard(state gameState, int currentPlayer) {
        System.out.println();
        System.out.print("   ");
        for (int i = 1; i <= state.N; i++) {
            if (currentPlayer==1) { System.out.print(BLUE); }
            if (currentPlayer==2) { System.out.print(PURPLE); }
            System.out.printf("%2d ", i);
        }
        System.out.print("    ");
        if (currentPlayer==1) {
            System.out.print(BLUE);
            System.out.print("AI vs Player");
        }
        if(currentPlayer==2)
        {
            System.out.print(PURPLE);
            System.out.print("Player vs AI");
        }
        char[][] board = gameState.getBoard();
        char currentRow = 65;
        final char DEFAULT = '\u0000';
        for (int i = 0; i < state.N; i++, currentRow++) {
            System.out.printf("%n%c  ", currentRow);
            for (int j = 0; j < state.N; j++) {
                if (board[i][j] == DEFAULT) {
                    System.out.print(" - ");
                } else {
                    System.out.printf(" %c ", board[i][j]);
                }
            }
        }
        System.out.println();
    }


}