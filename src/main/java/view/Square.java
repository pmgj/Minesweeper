package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import model.Field;
import model.FieldValue;
import model.State;

public class Square extends JButton implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Field f = (Field) evt.getSource();
        FieldValue value = f.getValue();
        State state = f.getState();
        String text = "";
        switch (state) {
            case FLAG:
                URL resource = getClass().getClassLoader().getResource("flag_icon.png");
                ImageIcon icon = new ImageIcon(resource);
                Image img = icon.getImage();
                Image newimg = img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
                icon = new ImageIcon(newimg);
                this.setIcon(icon);
                this.setForeground(Color.WHITE);
                f.setState(State.FLAG);
                break;
            case SHOW:
                switch (value) {
                    case BOMB:
                        resource = getClass().getClassLoader().getResource("bomb_icon.png");
                        icon = new ImageIcon(resource);
                        img = icon.getImage();
                        newimg = img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
                        icon = new ImageIcon(newimg);
                        this.setIcon(icon);
                        this.setForeground(Color.black);
                        this.setBackground(Color.lightGray);

                        /*
                         * text = "B";
                         * this.setForeground(Color.BLACK);
                         * this.setBackground(Color.LIGHT_GRAY);
                         */
                        break;
                    case NONE:
                        text = " ";
                        this.setBackground(Color.WHITE);
                        break;
                    default:
                        text = "" + value.ordinal();
                        this.setBackground(Color.WHITE);
                        Color[] colors = { Color.BLUE, Color.GREEN, Color.RED, Color.ORANGE, Color.MAGENTA, Color.PINK,
                                Color.CYAN, Color.DARK_GRAY };
                        this.setForeground(colors[value.ordinal() - 1]);
                        break;
                }
                break;
            default:
                break;
        }
        this.setFont(this.getFont().deriveFont(Font.BOLD));
        this.setText(text);
        this.setFocusPainted(false);
    }
}
