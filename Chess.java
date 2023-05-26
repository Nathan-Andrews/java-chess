import java.awt.*;  
import java.awt.event.*;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Chess extends JPanel implements MouseListener, MouseMotionListener {
    private JFrame frame = new JFrame("Chess");

    private int boardSize = 600;
    private Board board;
    private Color whiteColor = new Color(208, 181, 157);
    private Color blackColor = new Color(138, 87, 67);
    private Color selectedColor = new Color(242, 98, 85,200);
    private Color moveColor = new Color(150, 150, 150, 125);

    private BasicBot bot = new BasicBot();
    private boolean isComputing = false;

    private int mouseX, mouseY;
    private int selectedIndex;
    private boolean isDragging;
    
    public Chess() {
        // initalize window
        frame.setTitle("chess");
        frame.setSize(boardSize,boardSize + 25);

        // quit program when window closes
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(this);

        addMouseListener(this);
        addMouseMotionListener(this);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Timer timer = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repaint();

                if (!isComputing && !board.isGameOver && Piece.isSameColor(bot.color,board.turnColor)) {
                    isComputing = true;

                    executorService.submit(() -> {
                        board.movePiece(bot.playMove(board));
                        isComputing = false;
                    });
                }
        }});

        timer.start();

        board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        // board = new Board("k7/5Q2/1K6/8/8/8/8/8");
        // board = new Board("r1b1k1n1/p2p1p1P/n2B4/1p1NP2P/6P1/3P1Q2/P1P1K3/q5b1");

        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawBoard(g);
        drawPieces(g);
        if (isDragging) drawDragging(g);


        if (board.isGameOver) {
            g.setColor(new Color(0,0,0));
            g.fillRoundRect(boardSize / 4 - 5, boardSize * 5 / 16 - 5, boardSize / 2 + 10, boardSize / 4 + 10,20,20);
            g.setColor(new Color(255,255,255));
            g.fillRoundRect(boardSize / 4, boardSize * 5 / 16, boardSize / 2, boardSize / 4,20,20);

            Toolkit t=Toolkit.getDefaultToolkit();  
            Image image;

            if (!board.isCheckmate) image = t.getImage(Piece.texturePath + "stalemate.png");
            else if (board.turnColor == Piece.black) image = t.getImage(Piece.texturePath + "checkmateWhite.png");
            else image = t.getImage(Piece.texturePath + "checkmateBlack.png");
            
            g.drawImage(image,boardSize / 4 - 50, boardSize * 5 / 16 - 115, boardSize * 2 / 3, boardSize * 2 / 3,this);
        }
    }

    public void drawPieces(Graphics g) {
        // credit chess piece textures By Cburnett - Own work, CC BY-SA 3.0, https://commons.wikimedia.org/w/index.php?curid=1499803


        Toolkit t=Toolkit.getDefaultToolkit();  
        Image image;

        for (int i = 0; i < 64; i++) {
            if (board.at(i) % Piece.black == Piece.none || (isDragging && i == selectedIndex)) {continue;}

            image=t.getImage(Piece.texturePath + Piece.paths.get(board.at(i)));

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

                if (isDragging && board.moveInMoveset(selectedIndex,i + j * 8)) {
                    g.setColor(moveColor);
                    if (Piece.isEmpty(board.at(i + j * 8))) {
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

        if (board.at(selectedIndex) % Piece.black == Piece.none) {return;}

        image=t.getImage(Piece.texturePath + Piece.paths.get(board.at(selectedIndex)));

        g.drawImage(image, mouseX - (int)((double)boardSize / 16.667), mouseY - (int)((double)boardSize / 16.667),(int)((double)boardSize / 8.333),(int)((double)boardSize / 8.333),this);
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

        if (board.at(selectedIndex) % Piece.black == Piece.none) isDragging = false;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Handle mouse release event
        if (isDragging) {
            board.movePiece(new Move(selectedIndex,coordsToIndex(mouseX, mouseY)));
        }

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

    public static void main(String[] args) {
        new Chess();
    }
}