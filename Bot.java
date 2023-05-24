import java.util.Random;


public class Bot {
    public int color = Piece.black;
    protected Random rand = new Random();
    

    public Bot() {

    }

    public Bot(int color) {
        this.color = color;
    }

    public int evaluatePosition(Board board, int color) {
        // positive if the selected color is winning
        int eval = 0;

        for (int i = 0; i < 64; i++) {
            if (board.at(i) != Piece.none) {
                if (Piece.isSameColor(board.at(i),color)) eval += Piece.value.get(board.at(i) % Piece.white);
                else eval -= Piece.value.get(board.at(i) % Piece.white);
            }
        }

        if (board.isCheckmate) {
            if (Piece.isSameColor(board.turnColor,color)) eval -= 100000;
            else eval += 100000;
        }

        return eval;
    }
}
