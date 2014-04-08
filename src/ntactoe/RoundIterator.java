package ntactoe;

import java.util.Iterator;

/**
 *
 * @author s631127
 */
public class RoundIterator implements Iterator<Character> {
    char[] ar;
    int i = 0;

    public RoundIterator(char[] ar) {
        this.ar = ar;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Character next() {
        return ar[i];
    }

    @Override
    public void remove() {
    }

    public void forward() {
        i++;
        i %= ar.length;
    }

    public void clear() {
        i = 0;
    }
}
