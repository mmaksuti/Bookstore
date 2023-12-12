package controllers;

import java.io.*;

public class FileDatabaseController implements DatabaseController {
    public Object readFromFile(String file) throws IOException, IllegalStateException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

    public void writeToFile(String file, Object obj) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);
        oos.close();
    }
}
