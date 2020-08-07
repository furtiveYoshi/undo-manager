package undo.factory;

import undo.change.Change;
import undo.change.DeleteChange;
import undo.change.InsertChange;

public class ChangeFactoryImpl implements ChangeFactory {
    @Override
    public Change createDeletion(int pos, String s, int oldDot, int newDot) {
        return new DeleteChange(s, pos, oldDot, newDot);
    }

    @Override
    public Change createInsertion(int pos, String s, int oldDot, int newDot) {
        return new InsertChange(s, pos, oldDot, newDot);
    }
}
