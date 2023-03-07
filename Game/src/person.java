public enum person
    {
    //If it was up to me, I'd be the X's and the computer would be the O's
    player('O'), ai('X');
    private final char ID;
    person(char value) { this.ID = value; }
    public char value()
    {
        return ID;
    }
    }
