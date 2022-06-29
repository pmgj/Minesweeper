package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.FieldValue;
import model.Move;
import model.State;
import model.difficulty.Advanced;
import model.difficulty.Beginner;
import model.difficulty.Difficulty;
import model.difficulty.Intermediate;

public class MainWindow extends JFrame {

    private JPanel mainPanel;
    private JPanel optionsPanel;
    private JPanel boardPanel;
    private JPanel bottomPanel;

    private Square[][] squares;
    private JLabel lNumBombs;

    private int rows;
    private int cols;
    private int bombs;
    private Move lastMove;

    public MainWindow() {
        super("Minesweeper");
    }

    private void newGame(JComboBox<Difficulty> cbDifficulty) {
        Difficulty diff = (Difficulty) cbDifficulty.getSelectedItem();
        this.rows = diff.getRows();
        this.cols = diff.getCols();
        this.bombs = diff.getBombs();
        this.lastMove = Move.VALID;
        if (this.boardPanel != null) {
            this.boardPanel.removeAll();
            this.boardPanel.setLayout(new GridLayout(rows, cols));
        } else {
            this.boardPanel = new JPanel(new GridLayout(rows, cols));
            this.add(boardPanel);
        }

        this.squares = new Square[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Square b = new Square();
                b.setBackground(Color.darkGray);
                b.setSize(new Dimension(32, 32));
                b.setFont(new Font("Verdana", Font.PLAIN, 12));
                b.setMargin(new Insets(0, 0, 0, 0));
                this.squares[i][j] = b;
                this.boardPanel.add(b);
                final int row = i, col = j;
                b.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (lastMove == Move.LOSE || lastMove == Move.WIN) {
                            return;
                        }
                        State state = b.getState();
                        if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {
                            if (state != State.FLAG) {
                                FieldValue value = b.getValue();
                                Move ret;
                                switch (value) {
                                    case BOMB:
                                        ret = Move.LOSE;
                                        break;
                                    case NONE:
                                        openCell(row, col);
                                        ret = endOfGame();
                                        break;
                                    default:
                                        showCell(b);
                                        ret = endOfGame();
                                        break;
                                }
                                if (ret == Move.LOSE) {
                                    showBombs();
                                    showMessage("You lose!");
                                }
                                if (ret == Move.WIN) {
                                    showBombs();
                                    showMessage("You win!");
                                }
                                lastMove = ret;
                            }
                        } else if (e.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) {
                            if (state == State.FLAG) {
                                b.setIcon(null);
                                b.setState(State.HIDE);
                            } else if (state == State.HIDE) {
                                URL resource = getClass().getClassLoader().getResource("flag_icon.png");
                                ImageIcon icon = new ImageIcon(resource);
                                Image img = icon.getImage();
                                Image newimg = img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
                                icon = new ImageIcon(newimg);
                                b.setIcon(icon);
                                b.setForeground(Color.WHITE);
                                b.setState(State.FLAG);
                            }
                            setNumOfBombs(getNumOfFlags());
                        }
                    }
                });
            }
        }
        this.boardPanel.revalidate();

        this.boardPanel.setPreferredSize(new Dimension(32 * cols, 32 * rows));

        this.fillBombs();
        this.setNumbers();
        this.setNumOfBombs(this.bombs);

        this.setDimension();
    }

    private long getNumOfFlags() {
        return this.bombs - Arrays.stream(this.squares).flatMap(x -> Arrays.stream(x))
                .filter(x -> x.getState() == State.FLAG).count();
    }

    private void fillBombs() {
        for (int i = 0; i < bombs;) {
            int row = (int) Math.floor((Math.random() * rows));
            int col = (int) Math.floor((Math.random() * cols));
            if (squares[row][col].getValue() == FieldValue.NONE) {
                squares[row][col].setValue(FieldValue.BOMB);
                i++;
            }
        }
    }

    private void setNumbers() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (squares[i][j].getValue() == FieldValue.NONE) {
                    squares[i][j].setValue(FieldValue.values()[countPosition(i, j)]);
                }
            }
        }
    }

    private int countPosition(int row, int col) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < rows && j >= 0 && j < cols && squares[i][j].getValue() == FieldValue.BOMB) {
                    count++;
                }
            }
        }
        return count;
    }

    protected void openCell(int row, int col) {
        Square cell = this.squares[row][col];
        if (cell.getValue() != FieldValue.BOMB) {
            if (cell.getValue() == FieldValue.NONE && cell.getState() == State.HIDE) {
                showCell(cell);
                openCells(row, col);
            } else {
                showCell(cell);
            }
        }
    }

    private void openCells(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < rows && j >= 0 && j < cols) {
                    openCell(i, j);
                }
            }
        }
    }

    protected Move endOfGame() {
        long blocked = Arrays.stream(squares).flatMap(x -> Arrays.stream(x)).filter(x -> x.getState() != State.SHOW)
                .count();
        if (blocked == this.bombs) {
            return Move.WIN;
        } else {
            return Move.VALID;
        }
    }

    protected void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    private void showCell(Square s) {
        FieldValue value = s.getValue();
        String text;
        s.setBackground(Color.white);
        switch (value) {
            case BOMB:
                URL resource = getClass().getClassLoader().getResource("bomb_icon.png");
                ImageIcon icon = new ImageIcon(resource);
                Image img = icon.getImage();
                Image newimg = img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
                icon = new ImageIcon(newimg);
                s.setIcon(icon);
                s.setForeground(Color.black);
                s.setBackground(Color.lightGray);
                break;
            case NONE:
                text = " ";
                s.setText(text);
                break;
            default:
                text = "" + value.ordinal();
                Color[] colors = { Color.blue, Color.green, Color.red, Color.orange, Color.magenta, Color.PINK,
                        Color.CYAN, Color.DARK_GRAY };
                s.setForeground(colors[value.ordinal() - 1]);
                s.setText(text);
                break;
        }
        s.setFont(s.getFont().deriveFont(Font.BOLD));
        s.setState(State.SHOW);
        s.setFocusPainted(false);
    }

    protected void showBombs() {
        Arrays.stream(squares).flatMap(x -> Arrays.stream(x)).forEach(s -> {
            if (s.getValue() == FieldValue.BOMB) {
                this.showCell(s);
            }
        });
    }

    private void createWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JComboBox<Difficulty> cbDifficulty = new JComboBox<>(
                new Difficulty[] { new Beginner(), new Intermediate(), new Advanced() });
        optionsPanel.add(cbDifficulty);
        JButton bStart = new JButton("Start");
        bStart.addActionListener(ae -> this.newGame(cbDifficulty));
        optionsPanel.add(bStart);
        this.add(optionsPanel);

        this.newGame(cbDifficulty);

        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lBombs = new JLabel("# Bombs: ");
        bottomPanel.add(lBombs);
        Difficulty diff = (Difficulty) cbDifficulty.getSelectedItem();
        lNumBombs = new JLabel("" + diff.getBombs());
        bottomPanel.add(lNumBombs);
        this.add(bottomPanel);

        mainPanel.add(optionsPanel);
        mainPanel.add(boardPanel);
        mainPanel.add(bottomPanel);

        this.setContentPane(mainPanel);

        this.setDimension();
        this.setResizable(false);
        this.setVisible(true);
    }

    private void setDimension() {
        if (bottomPanel != null) {
            Dimension d = new Dimension((int) boardPanel.getPreferredSize().getWidth(), optionsPanel.getHeight()
                    + (int) boardPanel.getPreferredSize().getHeight() + bottomPanel.getHeight());
            this.setSize(d);
            this.pack();
        }
    }

    public void setNumOfBombs(long numBombs) {
        if (this.lNumBombs != null) {
            this.lNumBombs.setText("" + numBombs);
        }
    }

    public static void main(String[] args) {
        MainWindow mw = new MainWindow();
        mw.createWindow();
    }
}
