package undo.factory;

import undo.document.Document;
import undo.manager.UndoManager;
import undo.manager.UndoManagerImpl;

public class UndoManagerFactoryImpl implements UndoManagerFactory {
    @Override
    public UndoManager createUndoManager(Document doc, int bufferSize) {
        return new UndoManagerImpl(doc, bufferSize);
    }
}
