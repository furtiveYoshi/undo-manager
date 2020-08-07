package undo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import undo.change.Change;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class GeneralCasesTest extends BaseTest{

    @Test
    public void shouldSuccessRedoAndUndoInsertChanges() {
        fillDocumentWithString(doc, TEST_STRING, manager);

        Change change = changeFactory.createInsertion(7, "test ", 0, 0);
        change.apply(doc);
        manager.registerChange(change);

        manager.undo();
        manager.undo();
        manager.undo();
        manager.redo();

        assertEquals(REVERSED_TEST_STRING, doc.toString());
    }

    @Test
    public void shouldSuccessRedoAndUndoDeleteChanges() {
        fillDocumentWithString(doc, TEST_STRING, manager);

        Change change = changeFactory.createDeletion(0, " ", 0, 0);
        change.apply(doc);
        manager.registerChange(change);

        change = changeFactory.createDeletion(9, "into ", 0, 0);
        change.apply(doc);
        manager.registerChange(change);

        manager.undo();
        manager.undo();
        manager.redo();

        assertEquals(REVERSED_TEST_STRING, doc.toString());
    }

    @Test
    public void shouldSuccessOverrideBufferFewTimes(){
        manager = managerFactory.createUndoManager(doc, SHORT_BUFFER_SIZE);
        fillDocumentWithString(doc, TEST_STRING, manager);

        for (int i =0; i< SHORT_BUFFER_SIZE; i++){
            manager.undo();
        }

        for (int i =0; i< SHORT_BUFFER_SIZE-1; i++){
            manager.redo();
        }

        assertEquals(REVERSED_TEST_STRING, doc.toString());
    }

    @Test
    public void shouldSuccessRegisterNewChangeAfterUndoWithoutOverrideBuffer(){
        manager = managerFactory.createUndoManager(doc, BIG_BUFFER_SIZE);
        fillDocumentWithString(doc, TEST_STRING, manager);

        manager.undo();
        manager.undo();
        manager.redo();

        assertEquals(REVERSED_TEST_STRING, doc.toString());

        Change change = changeFactory.createInsertion(0, "test ", 0, 0);
        change.apply(doc);
        manager.registerChange(change);

        manager.undo();

        assertEquals(REVERSED_TEST_STRING, doc.toString());
    }

    @Test
    public void shouldSuccessRegisterNewChangeAfterUndoWithOverrideBuffer(){
        fillDocumentWithString(doc, TEST_STRING, manager);

        manager.undo();
        manager.undo();
        manager.redo();

        assertEquals(REVERSED_TEST_STRING, doc.toString());

        Change change = changeFactory.createInsertion(0, "test ", 0, 0);
        change.apply(doc);
        manager.registerChange(change);

        manager.undo();

        assertEquals(REVERSED_TEST_STRING, doc.toString());
    }

    @Test
    public void shouldSuccessRegisterNewChangeAfterUndoWithCrossingAgeOfBuffer(){
        fillDocumentWithString(doc, TEST_STRING, manager);

        manager.undo();
        manager.undo();
        manager.undo();
        manager.undo();

        assertEquals(" changes some type", doc.toString());

        Change change = changeFactory.createInsertion(0, "test ", 0, 0);
        change.apply(doc);
        manager.registerChange(change);

        manager.undo();

        assertEquals(" changes some type", doc.toString());
    }

    @Test
    public void shouldSuccessRedoChangeAfterUndoWithCrossingAgeOfBuffer(){
        fillDocumentWithString(doc, TEST_STRING, manager);

        manager.undo();
        manager.undo();
        manager.undo();
        manager.undo();

        manager.redo();
        manager.redo();
        manager.redo();

        assertEquals(REVERSED_TEST_STRING, doc.toString());
    }
}
