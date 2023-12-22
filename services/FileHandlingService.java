package services;

import java.io.*;

public class FileHandlingService {
    public Object readObjectFromFile(String file) throws IOException, IllegalStateException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

    public void writeObjectToFile(String file, Object obj) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);
        oos.close();
    }

    public boolean deleteFile(String file) {
        File f = new File(file);
        return f.delete();
    }

    public String readFileContents(String file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();
        StringBuilder contents = new StringBuilder();
        while (line != null) {
            contents.append(line).append("\n");
            line = br.readLine();
        }
        br.close();
        return contents.toString();
    }

    public void writeFileContents(String file, String contents) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(contents);
        bw.close();
    }

    public boolean ensureDirectory(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return f.mkdir();
        }
        else if (!f.isDirectory()) {
            return f.delete() && f.mkdir();
        }
        return true;
    }

//    public boolean pathIsDirectory(String path) {
//        File f = new File(path);
//        return f.isDirectory();
//    }
//
//    public boolean pathExists(String path) {
//        File f = new File(path);
//        return f.exists();
//    }
//
//    public boolean createDirectory(String path) {
//        File f = new File(path);
//        return f.mkdir();
//    }

    public String[] listDirectory(String path) {
        File f = new File(path);
        return f.list();
    }
}