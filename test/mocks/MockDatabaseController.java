package test.mocks;

import controllers.DatabaseController;

import java.io.IOException;

public class MockDatabaseController implements DatabaseController {
    Object cannedDatabase;

    public MockDatabaseController(Object cannedDatabase) {
        this.cannedDatabase = cannedDatabase;
    }

    @Override
    public Object readFromFile(String ignored) throws IOException, IllegalStateException, ClassNotFoundException {
        return cannedDatabase;
    }

    @Override
    public void writeToFile(String ignored, Object obj) throws IOException {
        cannedDatabase = obj;
    }
}
