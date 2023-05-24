public class StupidBot extends Bot {

    public StupidBot() {
        super();
    }

    public StupidBot(int color) {
        super(color);
    }

    public Move playMove(Board board) {
        int r = rand.nextInt(board.moves.size());
        return board.moves.atMove(r);
    }
    
}
