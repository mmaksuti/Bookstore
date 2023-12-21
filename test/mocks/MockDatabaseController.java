package test.mocks;

import controllers.DatabaseController;
import org.mockito.Mock;

import java.io.IOException;

public class MockDatabaseController implements DatabaseController {
    private Object cannedDatabase = null;
    private String cannedFileContents = "";
    private String[] cannedDirectoryContents = {};
    boolean ensureDirectory = true;
    boolean deleteFile = true;

    public void setCannedDatabase(Object cannedDatabase) {
        this.cannedDatabase = cannedDatabase;
    }

    public void setCannedFileContents(String cannedFileContents) {
        this.cannedFileContents = cannedFileContents;
    }

    public void setCannedDirectoryContents(String[] cannedDirectoryContents) {
        this.cannedDirectoryContents = cannedDirectoryContents;
    }

    public void setEnsureDirectory(boolean ensureDirectory) {
        this.ensureDirectory = ensureDirectory;
    }

    public void setDeleteFile(boolean deleteFile) {
        this.deleteFile = deleteFile;
    }

    @Override
    public Object readObjectFromFile(String ignored) throws IOException, IllegalStateException, ClassNotFoundException {
        return cannedDatabase;
    }

    @Override
    public void writeObjectToFile(String ignored, Object obj) throws IOException {
        setCannedDatabase(obj);
    }

    @Override
    public boolean deleteFile(String ignored) {
        return deleteFile;
    }

    @Override
    public String readFileContents(String ignored) throws IOException {
        return cannedFileContents;
    }

    @Override
    public void writeFileContents(String ignored, String contents) throws IOException {
        cannedFileContents = contents;
    }

//    @Override
//    public boolean pathIsDirectory(String ignored) {
//        return true;
//    }
//
//    @Override
//    public boolean pathExists(String ignored) {
//        return true;
//    }
//
//    @Override
//    public boolean createDirectory(String ignored) {
//        return true;
//    }

    @Override
    public boolean ensureDirectory(String path) {
        return ensureDirectory;
    }

    @Override
    public String[] listDirectory(String ignored) {
        return cannedDirectoryContents;
    }
}
