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

    public static Map<Integer, Integer> value = new Hashtable<Integer,Integer>() {{
        put(pawn, 100);
        put(bishop, 300);
        put(knight,300);
        put(rook,500);
        put(queen,900);
        put(king,1000);
    }};

    // bonus squares give extra eval points to encourage the bot to position its pieces where they are strongest
    public static int[] pawnBonusWhite = 
    {3,3,3,3,3,3,3,3,
    10,10,10,10,10,10,10,10,
    5,5,7,8,8,7,5,5,
    4,4,5,8,8,5,4,4,
    3,3,3,7,7,3,3,3,
    3,2,1,3,3,1,2,3,
    3,4,4,-1,-1,4,4,3,
    3,3,3,3,3,3,3,3};
    public static int[] pawnBonusBlack = 
    {3,3,3,3,3,3,3,3,
    3,4,4,-1,-1,4,4,3,
    3,2,1,3,3,1,2,3,
    3,3,3,7,7,3,3,3,
    4,4,5,8,8,5,4,4,
    5,5,7,8,8,7,5,5,
    10,10,10,10,10,10,10,10,
    3,3,3,3,3,3,3,3};
    public static int[] knightBonus = 
    {0,1,2,2,2,2,1,0,
    1,3,4,5,5,4,3,1,
    2,4,6,7,7,6,4,2,
    2,5,8,9,9,8,5,2,
    2,5,8,9,9,8,5,2,
    2,4,6,7,7,6,4,2,
    1,3,4,5,5,4,3,1,
    0,1,2,2,2,2,1,0};
    public static int[] bishopBonusWhite = 
    {0,3,3,3,3,3,3,0,
    3,6,6,6,6,6,6,3,
    3,6,7,8,8,7,6,3,
    3,7,7,8,8,7,7,3,
    3,4,9,8,8,9,4,3,
    3,8,8,8,8,8,8,3,
    3,8,4,4,4,4,8,3,
    4,2,2,2,2,2,2,4};
    public static int[] bishopBonusBlack = 
    {4,2,2,2,2,2,2,4,
    3,8,4,4,4,4,8,3,
    3,8,8,8,8,8,8,3,
    3,4,9,8,8,9,4,3,
    3,7,7,8,8,7,7,3,
    3,6,7,8,8,7,6,3,
    3,6,6,6,6,6,6,3,
    0,3,3,3,3,3,3,0};
    public static int[] rookBonusWhite = 
    {6,6,6,6,6,6,6,6,
    7,9,9,9,9,9,9,7,
    0,4,4,4,4,4,4,0,
    0,4,4,4,4,4,4,0,
    0,4,4,4,4,4,4,0,
    0,4,4,4,4,4,4,0,
    0,4,4,4,4,4,4,0,
    4,4,7,8,8,7,4,4};
    public static int[] rookBonusBlack = 
    {4,4,7,8,8,7,4,4,
    0,4,4,4,4,4,4,0,
    0,4,4,4,4,4,4,0,
    0,4,4,4,4,4,4,0,
    0,4,4,4,4,4,4,0,
    0,4,4,4,4,4,4,0,
    7,9,9,9,9,9,9,7,
    6,6,6,6,6,6,6,6};
    public static int[] kingBonusWhite = 
    {2,1,1,0,0,1,1,2,
    2,1,1,0,0,1,1,2,
    3,2,2,0,0,2,2,3,
    3,2,2,0,0,2,2,3,
    5,3,3,2,2,3,3,5,
    6,4,4,4,4,4,4,6,
    8,7,5,5,5,5,7,8,
    9,10,7,6,6,7,10,9};
    public static int[] kingBonusBlack = 
    {9,10,7,6,6,7,10,9,
    8,7,5,5,5,5,7,8,
    6,4,4,4,4,4,4,6,
    5,3,3,2,2,3,3,5,
    3,2,2,0,0,2,2,3,
    3,2,2,0,0,2,2,3,
    2,1,1,0,0,1,1,2,
    2,1,1,0,0,1,1,2};
    public static int[] queenBonus = 
    {0,2,2,3,3,2,2,0,
    2,5,5,5,5,5,5,2,
    2,5,9,9,9,9,5,2,
    3,5,9,9,9,9,5,3,
    5,5,9,9,9,9,5,3,
    2,6,9,9,9,9,5,2,
    2,5,9,9,9,5,5,2,
    0,2,2,3,3,2,2,0};

    public static Map<Integer,int[]>  squareBonuses;

    static {
        squareBonuses = new Hashtable<Integer,int[]>();
        squareBonuses.put(pawn | white, pawnBonusWhite);
        squareBonuses.put(pawn | black, pawnBonusBlack);
        squareBonuses.put(bishop | white, bishopBonusWhite);
        squareBonuses.put(bishop | black, bishopBonusBlack);
        squareBonuses.put(knight | white, knightBonus);
        squareBonuses.put(knight | black, knightBonus);
        squareBonuses.put(rook | white, rookBonusWhite);
        squareBonuses.put(rook | black, rookBonusBlack);
        squareBonuses.put(king | white, kingBonusWhite);
        squareBonuses.put(king | black, kingBonusBlack);
        squareBonuses.put(queen | white, queenBonus);
        squareBonuses.put(queen | black, queenBonus);
    }


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