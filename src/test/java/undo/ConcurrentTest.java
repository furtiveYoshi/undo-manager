package undo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import undo.change.Change;
import undo.document.Document;
import undo.document.DocumentImpl;
import undo.factory.ChangeFactory;
import undo.factory.ChangeFactoryImpl;
import undo.factory.UndoManagerFactory;
import undo.factory.UndoManagerFactoryImpl;
import undo.manager.UndoManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@RunWith(JUnit4.class)
public class ConcurrentTest {
    private UndoManagerFactory managerFactory = new UndoManagerFactoryImpl();
    private ChangeFactory changeFactory = new ChangeFactoryImpl();

    private class ChangeMakerTask implements Callable<String> {
        final Document document;
        final UndoManager undoManager;
        final List<Change> changes;

        ChangeMakerTask(Document document, UndoManager undoManager, List<Change> changes) {
            this.document = document;
            this.undoManager = undoManager;
            this.changes = new ArrayList<>(changes);
        }

        @Override
        public String call() throws Exception {
            Random rand = new Random();
            for (Change change : changes){
                synchronized (document) {
                    change.apply(document);
                    undoManager.registerChange(change);
                }
                Thread.sleep(rand.nextInt(10)+20);
            }
            return "Success";
        }
    }

    private class UndoChangeTask implements Callable<String> {
        final UndoManager undoManager;
        final int countOfUndo;

        UndoChangeTask(UndoManager undoManager, int countOfUndo) {
            this.undoManager = undoManager;
            this.countOfUndo = countOfUndo;
        }

        @Override
        public String call() throws Exception {
            Random rand = new Random();
            for (int i = 0; i < countOfUndo; i++){
                undoManager.undo();
                Thread.sleep(rand.nextInt(10)+20);
            }
            return "Success";
        }
    }

    private class RedoChangeTask implements Callable<String> {
        final UndoManager undoManager;
        final int countOfRedo;

        RedoChangeTask(UndoManager undoManager, int countOfRedo) {
            this.undoManager = undoManager;
            this.countOfRedo = countOfRedo;
        }

        @Override
        public String call() throws Exception {
            Random rand = new Random();
            for (int i = 0; i < countOfRedo; i++){
                undoManager.redo();
                Thread.sleep(rand.nextInt(10)+20);
            }
            return "Success";
        }
    }

    @Test
    public void ConcurrentTestCase() throws InterruptedException, ExecutionException {
        Document doc = new DocumentImpl();
        UndoManager manager = managerFactory.createUndoManager(doc, 20);

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        ChangeMakerTask changeMakerTask1 = new ChangeMakerTask(doc, manager, getListOfChangesFromString("a b c d e f g h i j k l m"));
        ChangeMakerTask changeMakerTask2 = new ChangeMakerTask(doc, manager, getListOfChangesFromString("n o p q r s t u v w x y z"));

        List<Future<String>> futures = executorService.invokeAll(Arrays.asList(changeMakerTask1, changeMakerTask2));

        Assert.assertEquals("Success", futures.get(0).get());
        Assert.assertEquals("Success", futures.get(1).get());

        String expectedDoc = doc.toString();
        UndoChangeTask undoChangeTask1 = new UndoChangeTask(manager, 10);
        UndoChangeTask undoChangeTask2 = new UndoChangeTask(manager, 10);

        RedoChangeTask redoChangeTask1 = new RedoChangeTask(manager, 10);
        RedoChangeTask redoChangeTask2 = new RedoChangeTask(manager, 10);

        futures = executorService.invokeAll(Arrays.asList(undoChangeTask1, undoChangeTask2));

        Assert.assertEquals("Success", futures.get(0).get());
        Assert.assertEquals("Success", futures.get(1).get());

        futures = executorService.invokeAll(Arrays.asList(redoChangeTask1, redoChangeTask2));

        Assert.assertEquals("Success", futures.get(0).get());
        Assert.assertEquals("Success", futures.get(1).get());

        Assert.assertEquals(expectedDoc, doc.toString());
        System.out.println(doc.toString());

        executorService.shutdown();
    }

    private List<Change> getListOfChangesFromString(String s) {
        List<Change> result = new ArrayList<>();
        for (String inserts : s.split(" ")) {
            Change change = changeFactory.createInsertion(0, inserts, 0, 0);
            Change space = changeFactory.createInsertion(0, " ", 0, 0);
            result.add(change);
            result.add(space);
        }
        return result;
    }
}
