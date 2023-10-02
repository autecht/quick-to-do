import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

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
    static String newline = "" + (char) 13 + (char) 10; // windows carriage return line feed
    String line1; String line2; String line3; String line4; String line5; String line6;
    Task task1; Task task2; Task task3; Task task4; Task task5; Task task6;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Before
    public void setupTest() throws Exception {
        File toDelete = new File("tasks.txt");
        toDelete.delete();
        // label#date#time#priority#tag#descritpion
        // lines as they should appear in file
        // H = high, L = low, N = low, D = date, T = time, P = priority
        line1 = "HDHTHP#01/01/1001#00:00#10##";
        line2 = "HDHTNP#01/01/1001#00:00###";
        line3 = "HDLTHP#01/01/1001#20:00#10##";
        line4 = "HDNTHP#01/01/1001#23:59#10##";
        line5 = "LDHTHP#01/01/3001#00:00#10##";
        line6 = "NDNTNP####A tag#A description"; 

        task1 = Task.of(line1);
        task2 = Task.of(line2);
        task3 = Task.of(line3);
        task4 = Task.of(line4);
        task5 = Task.of(line5);
        task6 = Task.of(line6);

        // insert tasks in mixed order
        String[] label;
        String[] due;
        String priority;
        String[] tag;
        String[] description;

        label = new String[] {"HDLTHP"};
        due = new String[] {"01/01/1001", "20:00"};
        priority = "10";
        tag = null;
        description = null;
        writeTask(label, due, priority, tag, description); //line3 = "HDLTHP#01/01/1001#20:00#10##";

        label = new String[] {"LDHTHP"};
        due = new String[] {"01/01/3001", "00:00"};
        priority = "10";
        tag = null;
        description = null;
        writeTask(label, due, priority, tag, description); //line5 = "LDHTHP#01/01/3001#00:00#10##";

        label = new String[] {"HDHTHP"};
        due = new String[] {"01/01/1001", "00:00"};
        priority = "10";
        tag = null;
        description = null;
        writeTask(label, due, priority, tag, description); // line1 = "HDHTHP#01/01/1001#00:00#10##";

        label = new String[] {"HDHTNP"};
        due = new String[] {"01/01/1001", "00:00"};
        priority = null;
        tag = null;
        description = null;
        writeTask(label, due, priority, tag, description); // line2 = "HDHTNP#01/01/1001#00:00###";

        label = new String[] {"NDNTNP"};
        due = null;
        priority = null;
        tag = new String[] {"A", "tag"};
        description = new String[] {"A", "description"};
        writeTask(label, due, priority, tag, description); // line6 = "NDNTNP####A tag#A description"; 

        label = new String[] {"HDNTHP"};
        due = new String[] {"01/01/1001"};
        priority = "10";
        tag = null;
        description = null;
        writeTask(label, due, priority, tag, description); // line4 = "HDNTHP#01/01/1001#23:59#10##";
        
    }

    @Before
    public void setPrintStream() {
        System.setOut(new PrintStream(outputStream));
    
    }

    @Test
    public void testAdd() {
        String expected = line1 + "\n" + line2 + "\n" + line3 + "\n" 
                + line4 + "\n" + line5 + "\n" + line6 + "\n";
        String result = readFile("tasks.txt");
        assertEquals(expected, result);
    }

    @Test
    public void testList() throws Exception {
        String[] label;
        String[] due;
        String priority;
        String[] tag;

        label = null;
        due = null;
        priority = null;
        tag = null;
        listTasks(label, due, priority, tag);
        String result = outputStream.toString();
        String expected = "1) " + task1.toStringWithColor() + newline + "2) " + task2.toStringWithColor() + newline + "3) " + task3.toStringWithColor() + newline 
                +"4) " +  task4.toStringWithColor() + newline +"5) " +  task5.toStringWithColor() + newline + "6) " + task6.toStringWithColor() + newline;
        assertEquals(expected, result);
        //System.err.println(result);

        // test due, priority
        label = null;
        due = new String[] {"01/01/1001", "20:00"};
        priority = "10";
        tag = null;
        listTasks(label, due, priority, tag);
        result = outputStream.toString();
        expected += "1) " + task1.toStringWithColor() + newline + "3) " + task3.toStringWithColor() + newline;
        assertEquals(expected, result);

        // test tag
        label = null;
        due = null;
        priority = null;
        tag = new String[] {};
        listTasks(label, due, priority, tag);
        result = outputStream.toString();
        expected += "1) " + task1.toStringWithColor() + newline + "2) " + task2.toStringWithColor() + newline + "3) " + task3.toStringWithColor() + newline 
                + "4) " + task4.toStringWithColor() + newline + "5) " + task5.toStringWithColor() + newline;
        assertEquals(expected, result);

        // test label
        label = new String[] {"HDNTHP"};
        due = null;
        priority = null;
        tag = null;
        listTasks(label, due, priority, tag);
        result = outputStream.toString();
        expected += "4) " + task4.toStringWithColor() + newline;
        assertEquals(expected, result);
    }


    @Test
    public void testRemove() throws Exception {
        removeTask(new String[] {"HDHTHP"}); // first task
        removeTask(new String[] {"HDLTHP"}); // middle task
        removeTask(new String[] {"NDNTNP"}); // last task

        String result = readFile("tasks.txt");
        String expected = line2 + "\n" + line4 + "\n" + line5 + "\n";
        assertEquals(expected, result);
        
        this.setupTest();
    }

    @Test
    public void testModify() throws Exception {
        line1 = "HDHTHP#01/01/1001#00:00#10##";
        line2 = "HDHTNP#01/01/1001#00:00###";
        line3 = "HDLTHP#01/01/1001#20:00#10##";
        line4 = "HDNTHP#01/01/1001#23:59#10##";
        line5 = "LDHTHP#01/01/3001#00:00#10##";
        line6 = "NDNTNP####A tag#A description"; 


        String[] label;
        String[] newLabel;
        String[] due;
        String priority;
        String[] tag;
        String[] description;

        // test changing features, including removing and adding
        String newLine6 = "MDMTMP#01/01/1050#12:00#5##";
        label = new String[] {"NDNTNP"};
        newLabel = new String[]{"MDMTMP"};
        due = new String[]{"01/01/1050", "12:00"};
        priority = "5";
        tag = new String[]{};
        description = new String[] {};
        modifyTask(label, newLabel, due, priority, tag, description);

        String expected = line1 + "\n" + line2 + "\n" + line3 + "\n" 
                + line4 + "\n" + newLine6 + "\n" + line5  + "\n";
        String result = readFile("tasks.txt");
        assertEquals(expected, result);


        // test changing description, keeping other features the same
        String newLine1 = "HDHTHP#01/01/1001#00:00#10##Added description";

        label = new String[] {"HDHTHP"};
        newLabel = null;
        due = null;
        priority = null;
        tag = null;
        description = new String[] {"Added", "description"};
        modifyTask(label, newLabel, due, priority, tag, description);

        expected = newLine1 + "\n" + line2 + "\n" + line3 + "\n" 
                + line4 + "\n" + newLine6 + "\n" + line5  + "\n";
        result = readFile("tasks.txt");
        assertEquals(expected, result);

        
        this.setupTest();
    }

    @Test
    public void testToString() {
        Task filledTask = new Task("label", "01/02/2000", 
                "00:00", "0", "tag", "description");
        String result = filledTask.toString();
        String expected = "Task: label\n     Date: 01/02/2000\n     Time: 00:00" +
                "\n     Priority: 0\n     Tag: tag\n     Description: description";
        assertEquals(expected, result);
    }

    @Test
    public void testRemoveWithNum() throws Exception{
        Remove.removeTask(6);
        Remove.removeTask(3);
        Remove.removeTask(1);
        String result = readFile("tasks.txt");
        String expected = line2 + "\n" + line4 + "\n" + line5 + "\n";
        assertEquals(expected, result);
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

    public static void writeTask(String[] label, String[] due, String priority, String[] tag, String[] description) throws Exception {
        Add.writeTask(label, due, priority, tag, description);
    }

    public static void listTasks(String[] label, String[] due, String priority, String[] tag) throws Exception {
        List.listTasks(label, due, priority, tag);
    }

    public static void removeTask(String[] label) {
        Remove.removeTask(label);
    }

    public static void modifyTask(String[] label, String[] newLabel, String[] due, String priority, String[] tag, String[] description) throws Exception {
        Modify.modifyTask(label, newLabel, due, priority, tag, description);
    }



    
}


