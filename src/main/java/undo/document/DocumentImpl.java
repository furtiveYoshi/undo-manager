package undo.document;

public class DocumentImpl implements Document {
    private StringBuffer document = new StringBuffer();
    private int currentPosition = 0;

    @Override
    public void delete(int pos, String s) {
        if (pos < 0) {
            throw new IllegalArgumentException("Position should be above or equal zero");
        }
        if (s == null) {
            throw new IllegalArgumentException("String should be not null");
        }

        synchronized (document) {
            if (document.length() == 0) {
                throw new IllegalStateException(String.format("Can not delete %s in position %s. Document is empty.", s, pos));
            }
            if ((pos + s.length()) >= document.length() ||
                    !document.substring(pos, pos + s.length()).equals(s)) {
                throw new IllegalStateException(String.format("Can not delete %s in position %s. Can not find such string in this position.", s, pos));
            }
            document.delete(pos, pos + s.length());
            currentPosition = pos;
        }
    }

    @Override
    public void insert(int pos, String s) {
        if (pos < 0) {
            throw new IllegalArgumentException("Position should be above or equal zero");
        }
        if (s == null) {
            throw new IllegalArgumentException("String should be not null");
        }
        synchronized (document) {
            if (!(pos == 0 && document.length() == 0) && pos >= document.length()) {
                throw new IllegalStateException(String.format("Can not insert '%s' in position %s. Position bigger than document length", s, pos));
            }
            document.insert(pos, s);
            currentPosition = pos + s.length();
        }
    }

    @Override
    public String toString() {
        synchronized (document){
            return document.toString();
        }
    }

    @Override
    public void setDot(int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("Position should be above or equal zero");
        }
        synchronized (document) {
            if (!(pos == 0 && document.length() == 0) && pos >= document.length()) {
                throw new IllegalArgumentException("Position should be smaller than document length");
            }
            currentPosition = pos;
        }
    }
}
