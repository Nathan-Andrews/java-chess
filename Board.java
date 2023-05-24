import java.util.LinkedList;

public class Board {

    public int[] board;

    public int enPassant = -99; // tracks possible enPassent target
    public boolean[] hasMoved = {false,false,false,false,false,false}; // used to see if castling is possible
    public int turnColor = Piece.white;

    public boolean isGameOver = false;
    public boolean isCheckmate = false;

    Moves moves;

    public Board() {
        generateBoard();
        new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");

        moves = generateMoves(turnColor, board);
    }

    public Board(Board b) {
        // copy constructor
        for (int i = 0; i < 64; i++) {
            board[i] = b.board[i];
        }

        enPassant = b.enPassant;
        hasMoved = b.hasMoved;
        turnColor = b.turnColor;

        moves = generateMoves(turnColor, board);
    }

    public Board(int[] position) {
        for (int i = 0; i < 64; i++) {
            board[i] = position[i];
        }
    }

    public Board(String fen) {
        // fen string constructor
        generateBoard();

        int j = 0;
        for (int i = 0; i < fen.length(); i++) {
            char c = fen.charAt(i);
            
            if (Character.isDigit(c)) {
                j += Character.getNumericValue(c);
            }
            else if (Character.isLetter(c)) {
                board[j] = Piece.symbols.get(c);
                j++;
            }
        }

        updateCastlingRights();

        moves = generateMoves(turnColor, board);
    }

    public void generateBoard() {
        board = new int[64];
        for (int i = 0; i < 64; i++) {
            board[i] = Piece.none;
        }
    }

    public int at(int i) {
        return board[i];
    }

    public void movePiece(Move move) {
        if (isGameOver) return;

        if (!moveInMoveset(move.moveFrom,move.moveTo)) {
            return;
        }

        if (move.moveTo == move.moveFrom) return;

        manageEnPassent(move.moveFrom,move.moveTo);
        manageCastling(move.moveFrom,move.moveTo);

        board[move.moveTo] = board[move.moveFrom];
        board[move.moveFrom] = Piece.none;
        
        managePawnPromotion(move);

        turnColor = Piece.flipColor(turnColor);

        moves.clear();
        moves = generateMoves(turnColor, board);
    }

    public boolean moveInMoveset(int moveFrom, int moveTo) {
        return (moves != null && moves.get(moveFrom) != null && moves.get(moveFrom).contains(moveTo));
    }

    public void manageEnPassent(int moveFrom, int moveTo) {
        if (Piece.isPawn(board[moveFrom]) && Piece.isWhite(board[moveFrom]) && enPassant - 8 == moveTo) {
            board[enPassant] = Piece.none;
        }
        if (Piece.isPawn(board[moveFrom]) && Piece.isBlack(board[moveFrom]) && enPassant + 8 == moveTo) {
            board[enPassant] = Piece.none;
        }

        enPassant = -99;
        if (Piece.isPawn(board[moveFrom]) && (moveFrom - moveTo == 16 || moveFrom - moveTo == -16)) {
            enPassant = moveTo;
        }
    }

    public void manageCastling(int moveFrom, int moveTo) {
        updateCastlingRights();

        if (Piece.isKing(board[moveFrom]) && (moveFrom - moveTo == 2 || moveFrom - moveTo == -2)) {
            if (moveFrom - moveTo == -2) {
                board[moveTo-1] = board[moveTo+1];
                board[moveTo+1] = Piece.none;
            }
            else {
                board[moveTo+1] = board[moveTo-2];
                board[moveTo-2] = Piece.none;
            }
        }
    }

    public void updateCastlingRights() {
        if (!Piece.isKing(board[60])) hasMoved[0] = true;
        if (!Piece.isRook(board[56])) hasMoved[1] = true;
        if (!Piece.isRook(board[63])) hasMoved[2] = true;
        if (!Piece.isKing(board[4])) hasMoved[3] = true;
        if (!Piece.isRook(board[0])) hasMoved[4] = true;
        if (!Piece.isRook(board[7])) hasMoved[5] = true;
    }

    public void managePawnPromotion(Move move) {
        if (Piece.isPawn(board[move.moveTo])) {
            if (Piece.isWhite(board[move.moveTo]) && move.moveTo < 8) {
                board[move.moveTo] = Piece.queen | Piece.white;
            }
            else if (Piece.isBlack(board[move.moveTo]) && move.moveTo > 55) {
                board[move.moveTo] = Piece.queen | Piece.black;
            }
        }
    }

    Moves generateMoves(int color, int[] position) {
        Moves allMoves = new Moves();

        for (int i = 0; i < 64; i++) {
            if (Piece.isSameColor(position[i],color)) {
                allMoves.put(i,generateLegalMovesForPiece(i, position, color));
            }
        }

        isGameOver = allMoves.size() == 0;
        isCheckmate = isGameOver && isInCheck(position,color);

        return allMoves;
    }

    public LinkedList<Integer> generatePseudoLegalMovesForPiece(int i, int color, int[] position) {
        LinkedList<Integer> plMoves = new LinkedList<Integer>();

        if (!Piece.isSameColor(color | Piece.pawn, position[i])) return plMoves;

        if (Piece.isPawn(position[i])) {
            // pawn
            if (Piece.isBlack(position[i])) {
                if (Piece.isEmpty(position[i + 8])) {
                    plMoves.add(i+8);
                    if (i < 16 && Piece.isEmpty(position[i + 16])) {
                        plMoves.add(i+16);
                    }
                }
                if (enPassant + 1 == i || (i % 8 > 0 && !Piece.isEmpty(position[i+7]) && !Piece.isSameColor(position[i],position[i + 7]))) {
                    plMoves.add(i + 7);
                }
                if (enPassant - 1 == i || (i % 8 < 7 && !Piece.isEmpty(position[i+9]) && !Piece.isSameColor(position[i], position[i + 9]))) {
                    plMoves.add(i+9);
                }
            }
            if (Piece.isWhite(position[i])) {
                if (Piece.isEmpty(position[i - 8])) {
                    plMoves.add(i-8);
                    if (i > 47 && Piece.isEmpty(position[i - 16])) {
                        plMoves.add(i-16);
                    }
                }
                if (enPassant - 1 == i || (i % 8 < 7 && !Piece.isEmpty(position[i-7]) && !Piece.isSameColor(position[i],position[i - 7]))) {
                    plMoves.add(i - 7);
                }
                if (enPassant + 1 == i || (i % 8 > 0 && !Piece.isEmpty(position[i-9]) && !Piece.isSameColor(position[i], position[i - 9]))) {
                    plMoves.add(i-9);
                }
            }
        }
        else if (Piece.isBishop(position[i])) {
            // bishop
            plMoves = generateBishopMoves(i,position,plMoves);
        }
        else if (Piece.isKnight(position[i])) {
            // knight
            if (i > 15) {
                if (i % 8 > 0 && !Piece.isSameColor(position[i], position[i-17])) plMoves.add(i-17);
                if (i % 8 < 7 && !Piece.isSameColor(position[i], position[i-15])) plMoves.add(i-15);
            }
            if (i < 48) {
                if (i % 8 > 0 && !Piece.isSameColor(position[i], position[i+15])) plMoves.add(i+15);
                if (i % 8 < 7 && !Piece.isSameColor(position[i], position[i+17])) plMoves.add(i+17);
            }
            if (i % 8 > 1) {
                if (i > 7 && !Piece.isSameColor(position[i],position[i-10])) plMoves.add(i-10);
                if (i < 56 && !Piece.isSameColor(position[i],position[i+6])) plMoves.add(i+6);
            }
            if (i % 8 < 6) {
                if (i > 7 && !Piece.isSameColor(position[i],position[i-6])) plMoves.add(i-6);
                if (i < 56 && !Piece.isSameColor(position[i],position[i+10])) plMoves.add(i+10);
            }
        }
        else if (Piece.isRook(position[i])) {
            // rook
            plMoves = generateRookMoves(i,position,plMoves);
        }
        else if (Piece.isKing(position[i])) {
            // king
            if (i > 7 && !Piece.isSameColor(position[i], position[i-8])) plMoves.add(i-8);
            if (i > 7 && i % 8 != 0 && !Piece.isSameColor(position[i], position[i-9])) plMoves.add(i-9);
            if (i > 7 && i % 8 != 7 && !Piece.isSameColor(position[i], position[i-7])) plMoves.add(i-7);
            if (i % 8 != 0 && !Piece.isSameColor(position[i], position[i-1])) plMoves.add(i-1);
            if (i % 8 != 7 && !Piece.isSameColor(position[i], position[i+1])) plMoves.add(i+1);
            if (i < 56 && !Piece.isSameColor(position[i], position[i+8])) plMoves.add(i+8);
            if (i < 56 && i % 8 != 7 && !Piece.isSameColor(position[i], position[i+9])) plMoves.add(i+9);
            if (i < 56 && i % 8 != 0 && !Piece.isSameColor(position[i], position[i+7])) plMoves.add(i+7);

            // castleing
            if (Piece.isWhite(position[i]) && !hasMoved[0] && !hasMoved[1] && Piece.isEmpty(position[i-1]) && Piece.isEmpty(position[i-2]) && Piece.isEmpty(position[i-3])) plMoves.add(i-2);
            if (Piece.isWhite(position[i]) && !hasMoved[0] && !hasMoved[2] && Piece.isEmpty(position[i+1]) && Piece.isEmpty(position[i+2])) plMoves.add(i+2);
            if (Piece.isBlack(position[i]) && !hasMoved[3] && !hasMoved[4] && Piece.isEmpty(position[i-1]) && Piece.isEmpty(position[i-2]) && Piece.isEmpty(position[i-3])) plMoves.add(i-2);
            if (Piece.isBlack(position[i]) && !hasMoved[3] && !hasMoved[5] && Piece.isEmpty(position[i+1]) && Piece.isEmpty(position[i+2])) plMoves.add(i+2);
        }
        else if (Piece.isQueen(position[i])) {
            // queen
            plMoves = generateBishopMoves(i,position,plMoves);
            plMoves = generateRookMoves(i,position,plMoves);
        }

        return plMoves;
    }

    public LinkedList<Integer> generateLegalMovesForPiece(int i, int[] position, int color) {
        LinkedList<Integer> legalMoves = generatePseudoLegalMovesForPiece(i,color,position);
        // LinkedList<Integer> plMoves = (LinkedList<Integer>) legalMoves.clone();

        // for (int move : legalMoves) {
        //     int temp = position[move];
        //     position[move] = position[i];
        //     position[i] = Piece.none;

        //     // if (isInCheck(position,color)) legalMoves.remove(legalMoves.indexOf(move));
        //     legalMoves.removeIf(c -> )

        //     position[i] = position[move];
        //     position[move] = temp;
        // }

        legalMoves.removeIf(c -> {
            int temp = position[c];
            position[c] = position[i];
            position[i] = Piece.none;

            // if (isInCheck(position,color)) legalMoves.remove(legalMoves.indexOf(move));
            Boolean b = isInCheck(position,color);

            position[i] = position[c];
            position[c] = temp;
            return b;
        });

        return legalMoves;
    }

    public boolean isInCheck(int[] position, int color) {
        int king = 0;
        for (int i = 0; i < 64; i++) {
            if (position[i] == (Piece.king | color)) {
                king = i;
                break;
            }
        }

        for (int i = 0; i < 64; i++) {
            if (!Piece.isSameColor(position[i],color)) {
                if (generatePseudoLegalMovesForPiece(i, Piece.flipColor(color), position).contains(king)) return true;
            }
        }

        return false;
    }

    public LinkedList<Integer> generateRookMoves(int i, int[] position,LinkedList<Integer> plMoves) {
        int index = i;
        while (index % 8 != 0) {
            index -= 1;
            if (Piece.isSameColor(position[index],position[i])) break;
            plMoves.add(index);
            if (!Piece.isEmpty(position[index])) break;
        }
        index = i;
        while (index % 8 < 7) {
            index += 1;
            if (Piece.isSameColor(position[index],position[i])) break;
            plMoves.add(index);
            if (!Piece.isEmpty(position[index])) break;
        }
        index = i;
        while (index > 7) {
            index -= 8;
            if (Piece.isSameColor(position[index],position[i])) break;
            plMoves.add(index);
            if (!Piece.isEmpty(position[index])) break;
        }
        index = i;
        while (index < 56) {
            index += 8;
            if (Piece.isSameColor(position[index],position[i])) break;
            plMoves.add(index);
            if (!Piece.isEmpty(position[index])) break;
        }

        return plMoves;
    }

    public LinkedList<Integer> generateBishopMoves(int i, int[] position, LinkedList<Integer> plMoves) {
        int index = i;
        while (index % 8 < 7 && index > 7) {
            index -= 7;
            if (Piece.isSameColor(position[index],position[i])) break;
            plMoves.add(index);
            if (!Piece.isEmpty(position[index])) break;
        }
        index = i;
        while (index % 8 != 0 && index > 7) {
            index -= 9;
            if (Piece.isSameColor(position[index],position[i])) break;
            plMoves.add(index);
            if (!Piece.isEmpty(position[index])) break;
        }
        index = i;
        while (index < 56 && index % 8 != 0) {
            index += 7;
            if (Piece.isSameColor(position[index],position[i])) break;
            plMoves.add(index);
            if (!Piece.isEmpty(position[index])) break;
        }
        index = i;
        while (index < 56 && index % 8 < 7) {
            index += 9;
            if (Piece.isSameColor(position[index],position[i])) break;
            plMoves.add(index);
            if (!Piece.isEmpty(position[index])) break;
        }

        return plMoves;
    }
}
