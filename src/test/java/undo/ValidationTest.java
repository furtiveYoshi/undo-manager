package undo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import undo.change.Change;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class ValidationTest extends BaseTest{

    @Test(expected = IllegalStateException.class)
    public void shouldFailUndoForManagerWithoutChanges() {
        manager.undo();
    }

    @Test
    public void shouldFailUndoForManagerWithUndoneAllBufferedChanges() {
        fillDocumentWithString(doc, TEST_STRING, manager);

        for (int i = 0; i < STANDARD_BUFFER_SIZE; i++) {
            manager.undo();
        }

        try {
            manager.undo();
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException ex){
            assertTrue(true);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailedRedoForManagerWithoutUndoneChanges(){
        Change change = changeFactory.createInsertion(0, "test", 0, 0);
        change.apply(doc);
        manager.registerChange(change);

        manager.redo();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailedApplyInsertChangeWithPositionUnderZero(){
        Change change = changeFactory.createInsertion(-1, "test", 0, 0);
        change.apply(doc);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailedApplyInsertChangeWithNullString(){
        Change change = changeFactory.createInsertion(0, null, 0, 0);
        change.apply(doc);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailedApplyInsertChangeWithPositionBiggerThanDocument(){
        Change change = changeFactory.createInsertion(3, "test", 0, 0);
        change.apply(doc);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailedApplyDeleteChangeWithPositionUnderZero(){
        Change change = changeFactory.createInsertion(0, "test", 0, 0);
        change.apply(doc);
        change = changeFactory.createDeletion(-1, "test", 0, 0);
        change.apply(doc);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailedApplyDeleteChangeWithNullString(){
        Change change = changeFactory.createInsertion(0, "test", 0, 0);
        change.apply(doc);
        change = changeFactory.createDeletion(0, null, 0, 0);
        change.apply(doc);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailedApplyDeleteChangeForEmptyDocument(){
        Change change = changeFactory.createDeletion(0, "test", 0, 0);
        change.apply(doc);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailedApplyDeleteChangeNotExistString(){
        Change change = changeFactory.createInsertion(0, "test", 0, 0);
        change.apply(doc);
        change = changeFactory.createDeletion(0, "tes1", 0, 0);
        change.apply(doc);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailedApplyDeleteChangeWithStringBiggerThanDocument(){
        Change change = changeFactory.createInsertion(0, "test", 0, 0);
        change.apply(doc);
        change = changeFactory.createDeletion(0, "test1", 0, 0);
        change.apply(doc);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailedSetPositionToDocumentWithPositionUnderZero(){
        doc.setDot(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailedSetPositionToDocumentWithPositionBiggerThanDocument(){
        doc.setDot(3);
    }
}
