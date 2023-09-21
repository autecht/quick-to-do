import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QdoTest {
    String message;
    String fileName;
    static String newline = "" + 13 + 10;
    String line1; String line2; String line3; String line4; String line5; String line6;
    Task task1; Task task2; Task task3; Task task4; Task task5; Task task6;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Before
    public void setupTest() {
        File toDelete = new File("tasks.txt");
        toDelete.delete();
        // label#date#time#priority#tag#descritpion
        line1 = "HDHTHP#01/01/1001#00:00#10##";
        line2 = "HDLTHP#01/01/1001#20:00#10##";
        line3 = "HDNTHP#01/01/1001#23:59#10##";
    }

    @Before
    public void setupTasks() {
        File testFile = new File("tasks.txt");
        testFile.delete();

        

        writeTask(new Task(task6));
        writeTask(new Task(task4));
        writeTask(new Task(task1));
        writeTask(new Task(task7));
        writeTask(new Task(task3));
        writeTask(new Task(task5));
        writeTask(new Task(task8));
        writeTask(new Task(task2));
    }

    @Before
    public void setPrintStream() {
        System.setOut(new PrintStream(outputStream));
    
    }

    

    @After
    public void close() throws IOException {
        outputStream.close();
    }
    
    /*
     * Puts contents of file with path fileName into String
     * Assumes file ends in newline
     */
    public static String readFile(String fileName) {
        String contents = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
            
            String nextLine;
            while ((nextLine = br.readLine()) != null) {
                contents += nextLine + "\n";
            }
            //contents = contents.substring(contents.length() - 2);
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return contents;
    }

    public static void writeMessage(String fileName, String message) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName)));
            bw.write(message);
            bw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeTask(Task t) {
        Add.writeTask(t.label, t.due, t.priority, t.tag, t.description);
    }

    
}


