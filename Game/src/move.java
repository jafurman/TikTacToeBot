public class move
    {
    private final int row;
    private final int col;
    private final person player;

    public move(int row, int col, person player)
    {
        this.row = row;
        this.col = col;
        this.player = player;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public person getPlayer() { return player; }

    public static move actuallyMove(String theMove)
    {
        int row = 0, col = 0;
        for (int i = 0; i < theMove.length(); i++) {
            char charToCheck = theMove.charAt(i);
            if (Character.isLetter(charToCheck)) {
                row = (int) charToCheck - 'a';
            } else
            {
                col = charToCheck - '1';
            }
        }
        return new move(row, col, person.player);
    }

    //for printing purposes
    @Override
    public String toString() { return String.valueOf((char) (row + 'a')) + String.valueOf((char) (col + '1')); }
    }