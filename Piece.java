import java.util.Map;
import java.util.Hashtable;

public final class Piece {
    public static int none = 0;
    public static int pawn = 1;
    public static int bishop = 2;
    public static int knight = 3;
    public static int rook = 4;
    public static int king = 6;
    public static int queen = 7;

    public static int white = 8;
    public static int black = 16;

    public static String texturePath = "textures/";

    public static Map<Character, Integer> symbols = new Hashtable<Character, Integer>() {{
        put('p', pawn | black);
        put('b', bishop | black);
        put('n', knight | black);
        put('r', rook | black);
        put('k', king | black);
        put('q', queen | black);
        put('P', pawn | white);
        put('B', bishop | white);
        put('N', knight | white);
        put('R', rook | white);
        put('K', king | white);
        put('Q', queen | white);
    }};

    public static Map<Integer, String> paths = new Hashtable<Integer, String>() {{
        put(pawn | white,"Wpawn.png");
        put(bishop | white,"Wbishop.png");
        put(knight | white,"Wknight.png");
        put(rook | white,"Wrook.png");
        put(king | white,"Wking.png");
        put(queen | white,"Wqueen.png");

        put(pawn | black,"Bpawn.png");
        put(bishop | black,"Bbishop.png");
        put(knight | black,"Bknight.png");
        put(rook | black,"Brook.png");
        put(king | black,"Bking.png");
        put(queen | black,"Bqueen.png");
    }};

    public static boolean isPawn(int i) {
        return i % white == pawn;
    }
    public static boolean isBishop(int i) {
        return i % white == bishop;
    }
    public static boolean isKnight(int i) {
        return i % white == knight;
    }
    public static boolean isRook(int i) {
        return i % white == rook;
    }
    public static boolean isKing(int i) {
        return i % white == king;
    }
    public static boolean isQueen(int i) {
        return i % white == queen;
    }
    public static boolean isEmpty(int i) {
        return i == none;
    }
    public static boolean isWhite(int i) {
        return !isBlack(i) && i != none;
    }
    public static boolean isBlack(int i) {
        return i > black;
    }

    public static boolean isSameColor(int x,int y) {
        if (isEmpty(x) || isEmpty(y)) return false;
        return !(x >= black ^ y >= black);
    }

    public static int flipColor(int i) {
        if (i >= Piece.black) return (i % Piece.black) | Piece.white;
        return (i % Piece.white) | Piece.black;
    }

    private Piece() {};
}