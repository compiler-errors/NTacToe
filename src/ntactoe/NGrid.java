package ntactoe;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author michael
 */
public class NGrid {
    private static final int OFFSPACE = 10;
    private static final BasicStroke thickStroke = new BasicStroke(2), thinStroke = new BasicStroke(3), thinnestStroke = new BasicStroke(0.25f);
    private static class NThreePair { NGrid left, mid, right; }
    private static HashMap<Rectangle2D.Float, NGrid> crects = new HashMap<Rectangle2D.Float, NGrid>();
    private static boolean cleared = true;
    private static int oldx, oldy, oldwidth, oldheight;
    private static Font font = new Font("Calibri", Font.BOLD, 20);
    
    private static NGrid singleton;
    
    public static void prepare(int i) {
        singleton = prepareRecur(i);
    }
    
    public static void clear() {
        singleton.clearRecur();
    }
    
    public static boolean clickedAt(char c, int x, int y) {
        Point p = new Point(x, y);
        for (Entry<Rectangle2D.Float, NGrid> e : crects.entrySet()) {
            if (e.getKey().contains(p)) {
                if (e.getValue().data.equals(' ')) {
                    e.getValue().data = (Character) c;
                    return true;
                }
                return false;
            }
        }
        return false;
    }
    
    public static void draw(Graphics2D g, int x, int y, int width, int height) {
        if (cleared || x != oldx || y != oldy || width != oldwidth || height != oldheight) {
            System.out.println("Adjusting collision rectangles.");
            cleared = true;
            clearCollisionRectangles();
            oldx = x;
            oldy = y;
            oldwidth = width;
            oldheight = height;
        }
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
        singleton.drawRecur(g, x, y, width, height);
        cleared = false;
    }
    
    public static char getWinningChar() {
        if (singleton.level == 0) {
            System.out.println("Level <= 0.");
            return ' ';
        }
        NThreePair ntp = (NThreePair) singleton.data;
        return checkWinningCharRecur(singleton.level, ntp.left, ntp.mid, ntp.right);
    }
    
    private static void clearCollisionRectangles() {
        crects.clear();
    }

    private static void registerCollisionRectangle(int x, int y, int w, int h, NGrid g) {
        if (!cleared)
            return;
        Rectangle2D.Float r = new Rectangle2D.Float(x, y, w, h);
        crects.put(r, g);
    }
    
    private static char checkWinningCharRecur(int level, NGrid l, NGrid m, NGrid r) {
        if (level == 1) {
            char a = (Character) l.data, b = (Character) m.data, c = (Character) r.data;
            if (a == b && b == c)
                return a;
            else
                return ' ';
        } else {
            NThreePair top = (NThreePair) l.data, mid = (NThreePair) m.data, bot = (NThreePair) r.data;
            NGrid lt = top.left, lm = mid.left, lb = bot.left, mt = top.mid, mm = mid.mid, mb = bot.mid, rt = top.right, rm = mid.right, rb = bot.right;
            char winner;
            winner = checkWinningCharRecur(level - 1, lt, lm, lb);
            if (winner != ' ')
                return winner;
            winner = checkWinningCharRecur(level - 1, mt, mm, mb);
            if (winner != ' ')
                return winner;
            winner = checkWinningCharRecur(level - 1, rt, rm, rb);
            if (winner != ' ')
                return winner;
            winner = checkWinningCharRecur(level - 1, lt, mt, rt);
            if (winner != ' ')
                return winner;
            winner = checkWinningCharRecur(level - 1, lm, mm, rm);
            if (winner != ' ')
                return winner;
            winner = checkWinningCharRecur(level - 1, lb, mb, rb);
            if (winner != ' ')
                return winner;
            winner = checkWinningCharRecur(level - 1, lt, mm, rb);
            if (winner != ' ')
                return winner;
            winner = checkWinningCharRecur(level - 1, lb, mm, rt);
            if (winner != ' ')
                return winner;
            return ' ';
        }
    }
    
    private static NGrid prepareRecur(int i) {
        if (i == 0) {
            NGrid ret = new NGrid();
            ret.data = (Character) ' ';
            ret.level = i;
            return ret;
        } else {
            NGrid l = prepareRecur(i - 1), m = prepareRecur(i - 1), r = prepareRecur(i - 1);
            NThreePair ntp = new NThreePair();
            ntp.left = l;
            ntp.mid = m;
            ntp.right = r;
            NGrid ret = new NGrid();
            ret.data = ntp;
            ret.level = i;
            return ret;
        }
    }
    
    private Object data;
    private int level;
    
    private void drawRecur(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.setColor(Color.BLACK);
        if (level == 0) {
            if (!data.equals(' ')) {
                g.setFont(font);
                g.setColor(Color.BLACK);
                String s = "" + (Character) data;
                Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(s, g);
                g.setColor(Color.RED);
                g.drawString(s, (int)(x1 + (x2 - x1) / 2 - stringBounds.getWidth() / 2), (int)(y1 + (y2 - y1) / 2 + stringBounds.getHeight()/2));
            }
            registerCollisionRectangle(x1, y1, x2 - x1, y2 - y1, this);
        } else if (level % 2 == 0) {
            /* Squish window just a bit */
            x1 += OFFSPACE;
            x2 -= OFFSPACE;
            y1 += OFFSPACE;
            y2 -= OFFSPACE;
            
            int width = x2 - x1;
            int height = y2 - y1;
            
            g.setColor(Color.BLACK);
            if (level == 2) {
                g.setStroke(thinStroke);
                for (int i = 1; i < 3; i++) { //draw lines
                    g.drawLine(x1 + width * i / 3, y1, x1 + width * i / 3, y2);
                    g.drawLine(x1, y1 + height * i / 3, x2,  y1 + height * i / 3);
                }
            } else {
                g.setStroke(level == 2 ? thinStroke : thinnestStroke);
                g.drawRect(x1, y1, x2 - x1, y2 - y1);
            }
            
            NThreePair root = (NThreePair) data;
            for (int i = 0; i < 3; i++) {
                NThreePair sub = (NThreePair) (i == 0 ? root.left.data : (i == 1 ? root.mid.data : root.right.data));
                for (int j = 0; j < 3; j++) {
                    NGrid leaf = (j == 0 ? sub.left : (j == 1 ? sub.mid : sub.right));
                    leaf.drawRecur(g, x1 + width * i / 3, y1 + height * j / 3, x1 + width * (i + 1) / 3, y1 + height * (j + 1) / 3);
                }
            }
            
        } else { //extra horizontal
            x1 += OFFSPACE;
            x2 -= OFFSPACE;
            int width = x2 - x1;
            NThreePair triplet = (NThreePair) data;
            g.setStroke(thickStroke);
            //for (int i = 1; i < 3; i++) { //draw lines
            //    g.drawLine(x1 + width * i / 3, y1 + 10, x1 + width * i / 3, y2 - 10);
            //}
            for (int i = 0; i < 3; i++) {
                NGrid leaf = (i == 0 ? triplet.left : (i == 1 ? triplet.mid : triplet.right));
                leaf.drawRecur(g, x1 + width * i / 3, y1, x1 + width * (i + 1) / 3, y2);
            }
        }
    }
    
    private void setData(char c, int... coords) {
        NGrid n = this;
        for (int i : coords) {
            switch (i) {
                case 0:
                    n = ((NThreePair)(n.data)).left;
                    break;
                case 1:
                    n = ((NThreePair)(n.data)).mid;
                    break;
                case 2:
                    n = ((NThreePair)(n.data)).right;
                    break;
            }
        }
        n.data = (Character) c;
    }

    private void clearRecur() {
        if (level > 0) {
            NThreePair ntp = (NThreePair) data;
            ntp.left.clearRecur();
            ntp.mid.clearRecur();
            ntp.right.clearRecur();
        } else {
            data = (Character) ' ';
        }
    }
}
