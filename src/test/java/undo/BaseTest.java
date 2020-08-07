package undo;

import org.junit.Before;
import undo.change.Change;
import undo.document.Document;
import undo.document.DocumentImpl;
import undo.factory.ChangeFactory;
import undo.factory.ChangeFactoryImpl;
import undo.factory.UndoManagerFactory;
import undo.factory.UndoManagerFactoryImpl;
import undo.manager.UndoManager;

public abstract class BaseTest {
    UndoManagerFactory managerFactory = new UndoManagerFactoryImpl();
    ChangeFactory changeFactory = new ChangeFactoryImpl();
    Document doc;
    UndoManager manager;

    static final int BIG_BUFFER_SIZE = 15;
    static final int STANDARD_BUFFER_SIZE = 7;
    static final int SHORT_BUFFER_SIZE = 3;
    static final String TEST_STRING = "type some changes into document";
    static final String REVERSED_TEST_STRING = "document into changes some type";

    @Before
    public void setUp(){
        doc = new DocumentImpl();
        manager = managerFactory.createUndoManager(doc, STANDARD_BUFFER_SIZE);
    }

    void fillDocumentWithString(Document doc, String s, UndoManager manager) {
        for (String inserts : s.split(" ")) {
            Change change = changeFactory.createInsertion(0, inserts, 0, 0);
            change.apply(doc);
            manager.registerChange(change);
            Change space = changeFactory.createInsertion(0, " ", 0, 0);
            space.apply(doc);
            manager.registerChange(space);
        }
    }
}
