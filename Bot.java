import java.util.Random;


public class Bot {
    public int color = Piece.black;
    private Random rand = new Random();
    

    public Bot() {

    }

    public Move playMove(Board board) {
        int r = rand.nextInt(board.moves.size());
        return board.moves.atMove(r);
    }
}
