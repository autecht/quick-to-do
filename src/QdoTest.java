import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class QdoTest {
    String message;
    String fileName;

    @Before
    public void setupTest() {
        this.message = "Line 1\nLine 2\nLine 3\nLine 4\n";
        this.fileName = "test.txt";
        writeMessage("test.txt", message);
    }
    @Test
    public void testCompareDates() {
        int sameResult = Task.compareDates("11/22/2022", "11/22/2022");
        assertEquals(0, sameResult);

        int negativeResult = Task.compareDates("01/12/2003", "01/13/2003" );
        assertEquals(true, negativeResult > 0);

        int postitiveResult = Task.compareDates("01/13/2003", "01/12/2003" );
        assertEquals(true, postitiveResult < 0);

        // Month Tests
        assertEquals(true, Task.compareDates("08/12/2024", "03/12/2024") < 0);
        assertEquals(true, Task.compareDates("03/12/2024", "08/12/2024") > 0);
        // Year Tests
        assertEquals(true, Task.compareDates("03/12/2025", "03/12/2024") < 0);
        assertEquals(true, Task.compareDates("03/12/2020", "03/12/2024") > 0);
    }

    @Test
    public void testCompareTask() {
        Task task1 = new Task("Task1", "02/12/2003", "5", "imp", ":SLkdjfsdlkfj");
        String normalTask = "Feed dogs$02/29/2003$2$less$sldfkj";

        Task similarTask = new Task("Feed doggo$02/29/2003$2$less$sldfkj");
        String recentYear = "Feed dogo$02/29/2030$2$less$sldfkj";
        String lowPriority = "Task$02/12/2003$0$less$sldfkj";

        assertEquals(true, task1.compareTask(lowPriority) > 0);
        assertEquals(true, task1.compareTask(normalTask) > 0);
        assertEquals(true, similarTask.compareTask(normalTask) > 0);
        assertEquals(true, similarTask.compareTask(recentYear) > 0);
        


        
    }

    @Test
    public void testRead() {
        String fileName = "exp.txt";
        try{
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName)));
        bw.write("Well this is rather interesting\nand I newlinenext\nokay\n");
        bw.close();}catch(Exception e) {e.printStackTrace();}
        System.out.println(readFile(fileName));
        assertEquals("Well this is rather interesting\nand I newlinenext\nokay\n", readFile(fileName));
    }

    @Test
    public void testNavigator() {
        writeMessage(this.fileName, this.message);
        FileNavigator fn = new FileNavigator(this.fileName);
        fn.close();
        assertEquals(this.message, readFile(this.fileName));
    }

    @Test
    public void testReadLine() {
        FileNavigator fn = new FileNavigator(this.fileName);
        assertEquals("Line 1", fn.readLine());
        assertEquals("Line 2", fn.readLine());
        assertEquals("Line 3", fn.readLine());
        assertEquals("Line 4", fn.readLine());
        assertEquals(null, fn.readLine());
        fn.close();
    }

    @Test
    public void testGetSection() {
        String line = "someLabel$01/12/2003$lij$$";
        FileNavigator fn = new FileNavigator(this.fileName);
        String result = Task.getDate(line);
        String expected = "01/12/2003";
        assertEquals(expected, result);

        line = "someLabel$$$$";
        result = Task.getDate(line);
        expected = "";
        assertEquals(expected, result);
        fn.close();
    }

    @Test
    public void testAdd() {
        File testFile = new File("tasks.txt");
        testFile.delete();

        String task1 = "RecentDate$01/12/2002$10$productivity$sldkfjlk;sdfj";
        String task2 = "LowerPriority$01/12/2002$5$productivity$sjfkhsdfjkl";
        String task3 = "NoPriority$01/12/2002$$$";
        String task4 = "Howdy$01/24/2002$10$productivity$lsd";
        String task5 = "Go to Dentist$09/30/2023$5$necessary$On Champlain and Perrin, 3:00pm, should pick up dinner after";
        String task6 = "Hello$09/24/2024$1$$";
        String task7 = "NoDate$$30$$";
        String task8 = "OnlyLabel$$$$";

        writeTask(new Task(task6));
        writeTask(new Task(task4));
        writeTask(new Task(task1));
        writeTask(new Task(task7));
        writeTask(new Task(task3));
        writeTask(new Task(task5));
        writeTask(new Task(task8));
        writeTask(new Task(task2));
        List.listAll();

        String result = readFile("tasks.txt");
        String expected = task1 + "\n" + task2 + "\n" + task3 + "\n" + task4
                + "\n" + task5 + "\n" + task6 + "\n" + task7 + "\n" + task8 + "\n";
        assertEquals(expected, result);

        Remove.removeTask(Task.getLabel(task3));
        Remove.removeTask(Task.getLabel(task6));
        result = readFile("tasks.txt");
        expected = task1 + "\n" + task2  + "\n" + task4
                + "\n" + task5 +  "\n" + task7 + "\n" + task8 + "\n";
        assertEquals(expected, result);

        writeTask(new Task(task3)); writeTask(new Task(task6));

        Task mt = new Task ("FarOffDate", "04/29/4033", 
                "3000", "modified", "This task was modified");
        
        Modify.modifyTask("RecentDate".split(" "), mt.label.split(" "), mt.due, mt.priority, mt.tag.split(" "), mt.description.split(" "));
        expected =  task2  + "\n" + task3 + "\n" + task4
                + "\n" + task5 +  "\n" + task6 + "\n" + mt.toLine() + "\n" + task7 + "\n" + task8 + "\n";
        result = readFile("tasks.txt");
        assertEquals(expected, result);
        
        Modify.modifyTask(new String[] {"LowerPriority"}, null, null, null, null, null);
        result = readFile("tasks.txt");
        assertEquals(expected, result);
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


