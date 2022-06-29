package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.Minesweeper;
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

    private Minesweeper mineField;
    private JLabel lNumBombs;

    public MainWindow() {
        super("Minesweeper");
    }

    private void newGame(JComboBox<Difficulty> cbDifficulty) {
        Difficulty diff = (Difficulty) cbDifficulty.getSelectedItem();
        int rows = diff.getRows();
        int cols = diff.getCols();
        int bombs = diff.getBombs();
        if (this.boardPanel != null) {
            this.boardPanel.removeAll();
            this.boardPanel.setLayout(new GridLayout(rows, cols));
        } else {
            this.boardPanel = new JPanel(new GridLayout(rows, cols));
            this.add(boardPanel);
        }

        this.mineField = new Minesweeper(rows, cols, bombs);
        Square[][] squares = new Square[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Square b = new Square();
                b.setBackground(Color.DARK_GRAY);
                b.setSize(new Dimension(32, 32));
                b.setFont(new Font("Arial", Font.PLAIN, 12));
                b.setMargin(new Insets(0, 0, 0, 0));
                squares[i][j] = b;
                this.boardPanel.add(b);
                this.mineField.addObserver(b, i, j);
                final int x = i, y = j;
                b.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        State state = State.SHOW;
                        if (e.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) {
                            state = State.FLAG;
                        }
                        Move m = mineField.play(x, y, state);
                        switch (m) {
                            case WIN:
                                showMessage("You win!");
                                break;
                            case LOSE:
                                showMessage("You lose!");
                                break;
                            default:
                                break;
                        }
                        setNumOfBombs(mineField.getNumOfFlags());
                    }
                });
            }
        }
        this.boardPanel.revalidate();
        this.boardPanel.setPreferredSize(new Dimension(32 * cols, 32 * rows));

        this.setNumOfBombs(bombs);

        this.setDimension();
    }

    protected void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
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

    private void setNumOfBombs(long numOfBombs) {
        if (this.lNumBombs != null) {
            this.lNumBombs.setText("" + numOfBombs);
        }
    }

    public static void main(String[] args) {
        MainWindow mw = new MainWindow();
        mw.createWindow();
    }
}
