import java.awt.*;  
import java.awt.event.*;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Map;
import java.util.Hashtable;

public class Chess extends JPanel implements MouseListener, MouseMotionListener {
    private JFrame frame = new JFrame("Chess");

    private int boardSize = 600;
    private int[] board;
    private Color whiteColor = new Color(208, 181, 157);
    private Color blackColor = new Color(138, 87, 67);
    private Color selectedColor = new Color(242, 98, 85,200);
    private Color moveColor = new Color(150, 150, 150, 125);

    private int mouseX, mouseY;
    private int selectedIndex;
    private boolean isDragging;

    // LinkedList<Integer> moves = new LinkedList<Integer>();
    Map<Integer,LinkedList<Integer>> moves;

    int enPassant = -99; // tracks possible enPassent target
    boolean[] hasMoved = {false,false,false,false,false,false}; // used to see if castling is possible
    int turnColor = Piece.white;
    
    public Chess() {
        // initalize window
        frame.setTitle("chess");
        frame.setSize(boardSize,boardSize + 25);

        // quit program when window closes
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(this);

        addMouseListener(this);
        addMouseMotionListener(this);

        Timer timer = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repaint();
        }});

        timer.start();

        generateBoard();
        convertFenString("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        // convertFenString("r1b1k1nr/p2p1pNp/n2B4/1p1NP2P/6P1/3P1Q2/P1P1K3/q5b1");

        moves = generateMoves(turnColor, board);

        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawBoard(g);
        drawPieces(g);
        if (isDragging) drawDragging(g);
    }

    public void drawPieces(Graphics g) {
        // credit chess piece textures By Cburnett - Own work, CC BY-SA 3.0, https://commons.wikimedia.org/w/index.php?curid=1499803


        Toolkit t=Toolkit.getDefaultToolkit();  
        Image image;

        for (int i = 0; i < 64; i++) {
            if (board[i] % Piece.black == Piece.none || (isDragging && i == selectedIndex)) {continue;}

            image=t.getImage(Piece.texturePath + Piece.paths.get(board[i]));

            g.drawImage(image, ((i % 8) * boardSize / 8) + 1, (i / 8) * boardSize / 8,(int)((double)boardSize / 8.333),(int)((double)boardSize / 8.333),this); 
        }
    }

    public void drawBoard(Graphics g) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                g.setColor(whiteColor);
                // g.setColor(new Color(224, 146, 197));
                if ((i + j) % 2 == 1) {
                    g.setColor(blackColor);
                    // g.setColor(new Color(245, 213, 234));
                }

                g.fillRect(i * boardSize / 8,j * boardSize / 8,boardSize / 8 + 1,boardSize / 8 + 1);

                if (isDragging && i + j * 8 == selectedIndex) {
                    g.setColor(selectedColor);
                    g.fillRect(i * boardSize / 8,j * boardSize / 8,boardSize / 8 + 1,boardSize / 8 + 1);
                }

                if (isDragging && moveInMoveset(i + j * 8)) {
                    g.setColor(moveColor);
                    if (Piece.isEmpty(board[i + j * 8])) {
                        g.fillOval((i * boardSize / 8) + (boardSize / 25),(j * boardSize / 8) + (boardSize / 25), boardSize / 25, boardSize / 25);
                    }
                    else {
                        g.setColor(selectedColor);
                        g.fillRect(i * boardSize / 8,j * boardSize / 8,boardSize / 8 + 1,boardSize / 8 + 1);
                    }
                }
            }
        }
    }

    public void drawDragging(Graphics g) {
        Toolkit t=Toolkit.getDefaultToolkit();  
        Image image;

        if (board[selectedIndex] % Piece.black == Piece.none) {return;}

        image=t.getImage(Piece.texturePath + Piece.paths.get(board[selectedIndex]));

        g.drawImage(image, mouseX - (int)((double)boardSize / 16.667), mouseY - (int)((double)boardSize / 16.667),(int)((double)boardSize / 8.333),(int)((double)boardSize / 8.333),this);
    }

    public void generateBoard() {
        board = new int[64];
        for (int i = 0; i < 64; i++) {
            board[i] = Piece.none;
        }
    }

    public void movePiece(int i) {
        if (!moveInMoveset(i)) {
            // moves = new LinkedList<Integer>();
            return;
        }

        // moves = new LinkedList<Integer>(); 

        if (i == selectedIndex) return;

        manageEnPassent(i);
        manageCastling(i);

        board[i] = board[selectedIndex];
        board[selectedIndex] = Piece.none;

        turnColor = Piece.flipColor(turnColor);

        moves.clear();
        moves = generateMoves(turnColor, board);
    }

    public boolean moveInMoveset(int i) {
        // if (moves.get(selectedIndex) == null) System.out.println("a");
        // else System.out.println("b");
        return (moves != null && moves.get(selectedIndex) != null && moves.get(selectedIndex).contains(i));
    }

    public void manageEnPassent(int i) {
        if (Piece.isPawn(board[selectedIndex]) && Piece.isWhite(board[selectedIndex]) && enPassant - 8 == i) {
            board[enPassant] = Piece.none;
        }
        if (Piece.isPawn(board[selectedIndex]) && Piece.isBlack(board[selectedIndex]) && enPassant + 8 == i) {
            board[enPassant] = Piece.none;
        }

        enPassant = -99;
        if (Piece.isPawn(board[selectedIndex]) && (selectedIndex - i == 16 || selectedIndex - i == -16)) {
            enPassant = i;
        }
    }

    public void manageCastling(int i) {
        if (!Piece.isKing(board[60])) hasMoved[0] = true;
        if (!Piece.isRook(board[56])) hasMoved[1] = true;
        if (!Piece.isRook(board[63])) hasMoved[2] = true;
        if (!Piece.isKing(board[4])) hasMoved[3] = true;
        if (!Piece.isRook(board[0])) hasMoved[4] = true;
        if (!Piece.isRook(board[7])) hasMoved[5] = true;

        if (Piece.isKing(board[selectedIndex]) && (selectedIndex - i == 2 || selectedIndex - i == -2)) {
            if (selectedIndex - i == -2) {
                board[i-1] = board[i+1];
                board[i+1] = Piece.none;
            }
            else {
                board[i+1] = board[i-2];
                board[i-2] = Piece.none;
            }
        }
    }

    public void convertFenString(String fen) {
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
    }

    public int coordsToIndex(int x, int y) {
        return (x * 8 / boardSize) + 8 * (y * 8 / boardSize);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Handle mouse click event
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Handle mouse press event
        mouseX = e.getX();
        mouseY = e.getY();
        isDragging = true;
        selectedIndex = coordsToIndex(mouseX, mouseY);

        // generateLegalMovesForPiece(selectedIndex);

        if (board[selectedIndex] % Piece.black == Piece.none) isDragging = false;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Handle mouse release event
        if (isDragging) movePiece(coordsToIndex(mouseX, mouseY));

        isDragging = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Handle mouse drag event
        if (isDragging) {
            mouseX = e.getX();
            mouseY = e.getY();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Handle mouse move event
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        // moves = new LinkedList<Integer>();
        isDragging = false;
    }
    
    public void mouseEntered(MouseEvent e) {
        // TODO generated method stub
    }

    public Map<Integer,LinkedList<Integer>> generateMoves(int color, int[] position) {
        Map<Integer,LinkedList<Integer>> allMoves = new Hashtable<Integer,LinkedList<Integer>>();

        for (int i = 0; i < 64; i++) {
            if (Piece.isSameColor(position[i],color)) {
                // System.out.println(i);
                allMoves.put(i,generateLegalMovesForPiece(i, position, color));
            }
        }
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
                if (enPassant + 1 == i || (i % 8 < 7 && !Piece.isEmpty(position[i+7]) && !Piece.isSameColor(position[i],position[i + 7]))) {
                    plMoves.add(i + 7);
                }
                if (enPassant - 1 == i || (i % 8 > 0 && !Piece.isEmpty(position[i+9]) && !Piece.isSameColor(position[i], position[i + 9]))) {
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

        for (int move : legalMoves) {
            int temp = position[move];
            position[move] = position[i];
            position[i] = Piece.none;

            if (isInCheck(position,color)) legalMoves.remove(move);

            position[i] = position[move];
            position[move] = temp;
        }

        return legalMoves;
    }

    public boolean isInCheck(int[] position, int color) {
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

    public static void main(String[] args) {
        new Chess();
    }
}