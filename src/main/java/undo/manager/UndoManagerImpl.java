package undo.manager;

import undo.change.Change;
import undo.document.Document;

import java.util.ArrayList;

public class UndoManagerImpl implements UndoManager {
    private final ArrayList<Change> changeList;
    private final Document doc;
    private int currentChangeIndex = -1;
    private int lastChangeIndex = -1;
    private int oldestChangeIndex = 0;
    private final int bufferSize;
    private boolean isOverridden = false;

    public UndoManagerImpl(Document doc, int bufferSize) {
        this.doc = doc;
        this.bufferSize = bufferSize;
        this.changeList = new ArrayList<>(bufferSize);
        for (int i = 0; i < bufferSize; i++) {
            changeList.add(null);
        }
    }

    @Override
    public void registerChange(Change change) {
        if (currentChangeIndex + 1 < bufferSize) {
            changeList.set(currentChangeIndex + 1, change);
            lastChangeIndex = ++currentChangeIndex;
        } else {
            changeList.set(0, change);
            lastChangeIndex = currentChangeIndex = 0;
            isOverridden = true;
        }
        if (isOverridden && lastChangeIndex == oldestChangeIndex) {
            oldestChangeIndex = lastChangeIndex + 1 < bufferSize ? lastChangeIndex + 1 : 0;

        }
    }

    @Override
    public boolean canUndo() {
        return isOverridden || currentChangeIndex >= oldestChangeIndex;
    }

    @Override
    public void undo() {
        if (!canUndo()) {
            throw new IllegalStateException("Can not perform undo operation for document");
        }

        try {
            changeList.get(currentChangeIndex).revert(doc);
        } catch (Exception ex) {
            throw new IllegalStateException("Can not perform undo operation for document", ex);
        }

        if (isOverridden && currentChangeIndex == 0) {
            currentChangeIndex = bufferSize - 1;
            isOverridden = false;
        } else {
            currentChangeIndex = currentChangeIndex == oldestChangeIndex ? -1 : currentChangeIndex - 1;
        }
    }

    @Override
    public boolean canRedo() {
        return currentChangeIndex != lastChangeIndex;
    }

    @Override
    public void redo() {
        if (!canRedo()) {
            throw new IllegalStateException("Can not perform redo operation for document");
        }
        if (currentChangeIndex == -1) {
            currentChangeIndex = oldestChangeIndex - 1;
        }
        if (currentChangeIndex + 1 < bufferSize) {
            changeList.get(currentChangeIndex + 1).apply(doc);
            ++currentChangeIndex;
        } else if (lastChangeIndex < currentChangeIndex) {
            changeList.get(0).apply(doc);
            currentChangeIndex = 0;
            isOverridden = true;
        } else {
            throw new IllegalStateException("Can not perform redo operation for document. Internal state of manager is incorrect.");
        }
    }
}
