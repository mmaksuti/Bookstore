package controllers;

import java.io.IOException;

public interface DatabaseController {
    public Object readObjectFromFile(String file) throws IOException, IllegalStateException, ClassNotFoundException;
    public void writeObjectToFile(String file, Object obj) throws IOException;
    public boolean deleteFile(String file);
    public String readFileContents(String file) throws IOException;
    public void writeFileContents(String file, String contents) throws IOException;
//    public boolean pathIsDirectory(String path);
//    public boolean pathExists(String path);
//    public boolean createDirectory(String path);
    public boolean ensureDirectory(String path);
    public String[] listDirectory(String path);
}
