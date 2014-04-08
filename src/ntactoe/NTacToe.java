package ntactoe;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author michael
 */
public class NTacToe {
    public static void main(String[] args) {
        final RoundIterator roundIterator = new RoundIterator(new char[] {'M', 'V', 'n', 'u'});
        NGrid.prepare(4);
        JFrame jf = new JFrame();
        final JPanel j = new JPanel() {
            @Override
            public void paint(Graphics g) {
                NGrid.draw((Graphics2D) g, 0, 0, getWidth(), getHeight());
            }
        };
        jf.add(j);
        j.setVisible(true);
        jf.setVisible(true);
        jf.setSize(500, 500);
        j.setSize(500, 500);
        j.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (NGrid.clickedAt(roundIterator.next(), e.getX(), e.getY())) {
                    roundIterator.forward();
                    j.repaint();
                }
                
                char winner = NGrid.getWinningChar();
                if (winner != ' ') {
                    System.out.println(winner+" won.");
                    NGrid.clear();
                    roundIterator.clear();
                }
            }
        });
    }
}

