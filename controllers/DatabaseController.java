package controllers;

import java.io.IOException;

public interface DatabaseController {
    public Object readFromFile(String file) throws IOException, IllegalStateException, ClassNotFoundException;
    public void writeToFile(String file, Object obj) throws IOException;
}
