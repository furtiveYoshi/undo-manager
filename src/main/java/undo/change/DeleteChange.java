package undo.change;

import undo.document.Document;

public final class DeleteChange implements Change {
    private static final String TYPE = "delete";

    private final String string;
    private final int position;
    private final int oldPos;
    private final int newPos;

    public DeleteChange(String string, int position, int oldPos, int newPos) {
        this.string = string;
        this.position = position;
        this.oldPos = oldPos;
        this.newPos = newPos;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Document doc) {
        doc.delete(position, string);
        doc.setDot(newPos);
    }

    @Override
    public void revert(Document doc) {
        doc.insert(position, string);
        doc.setDot(oldPos);
    }
}
