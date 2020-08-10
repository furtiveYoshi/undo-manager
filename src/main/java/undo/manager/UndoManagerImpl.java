package undo.manager;

import undo.change.Change;
import undo.document.Document;

public class UndoManagerImpl implements UndoManager {
    private final Change[] changeList;
    private final Document doc;
    private volatile int currentChangeIndex = -1;
    private volatile int lastChangeIndex = -1;
    private volatile int oldestChangeIndex = 0;
    private final int bufferSize;
    private volatile boolean isOverridden = false;

    public UndoManagerImpl(Document doc, int bufferSize) {
        this.changeList = new Change[bufferSize];
        this.doc = doc;
        this.bufferSize = bufferSize;
    }

    @Override
    public synchronized void registerChange(Change change) {
        if (currentChangeIndex + 1 < bufferSize) {
            changeList[currentChangeIndex + 1] = change;
            lastChangeIndex = ++currentChangeIndex;
        } else {
            changeList[0] = change;
            lastChangeIndex = currentChangeIndex = 0;
            isOverridden = true;
        }
        if (isOverridden && lastChangeIndex == oldestChangeIndex) {
            oldestChangeIndex = lastChangeIndex + 1 < bufferSize ? lastChangeIndex + 1 : 0;
        }
    }

    @Override
    public synchronized boolean canUndo() {
        return isOverridden || currentChangeIndex >= oldestChangeIndex;
    }

    @Override
    public synchronized void undo() {
        if (!canUndo()) {
            throw new IllegalStateException("Can not perform undo operation for document");
        }

        try {
            changeList[currentChangeIndex].revert(doc);
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
    public synchronized boolean canRedo() {
        return currentChangeIndex != lastChangeIndex;
    }

    @Override
    public synchronized void redo() {
        if (!canRedo()) {
            throw new IllegalStateException("Can not perform redo operation for document");
        }
        if (currentChangeIndex == -1) {
            currentChangeIndex = oldestChangeIndex - 1;
        }
        if (currentChangeIndex + 1 < bufferSize) {
            changeList[currentChangeIndex + 1].apply(doc);
            ++currentChangeIndex;
        } else if (lastChangeIndex < currentChangeIndex) {
            changeList[0].apply(doc);
            currentChangeIndex = 0;
            isOverridden = true;
        } else {
            throw new IllegalStateException("Can not perform redo operation for document. Internal state of manager is incorrect.");
        }
    }
}
