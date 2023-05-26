public class BasicBot extends Bot {
    public BasicBot() {
        super();
    }

    public BasicBot(int color) {
        super(color);
    }

    public Move playMove(Board board) {
        int movesVisited = 0;

        Move bestMove = board.moves.atMove(0);
        int bestScore = -1000000;

        for (int i = 0; i < board.moves.size(); i++) {
            Board newBoard = new Board(board);
            newBoard.movePiece(board.moves.atMove(i));

            int minScore = 1000000; // score of the best move for white
            for (int j = 0; j < newBoard.moves.size(); j++) {
                movesVisited++;

                Board newBoard2 = new Board(newBoard);
                newBoard2.movePiece(newBoard.moves.atMove(j));

                minScore = Math.min(evaluatePosition(newBoard2, this.color),minScore);
            }

            if (minScore > bestScore) {
                bestScore = minScore;
                bestMove = board.moves.atMove(i);
            }
        }
        System.out.println(movesVisited);

        return bestMove;
    }
}
                         