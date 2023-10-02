import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
/*
 * Class to insert into file using ArrayList as buffer.
 */
public class FileNavigator {

    String fileName;
    ArrayList<String> lines; // representation of file, where each element is a line of the file
    int lineNumber; // current line number

    FileNavigator(String fileName) {
        this.fileName = fileName;
        File f = new File(fileName);
        
        
        try {
            if (!f.exists()) f.createNewFile();
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
        

            lines = new ArrayList<String>();
            String nextLine;
            while ((nextLine = br.readLine()) != null) {
                lines.add(nextLine);
            }

            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }  
    }

    public int getLine() {
        return this.lineNumber;
    }

    // returns number of lines in FileNavigator
    public int size() {
        return this.lines.size();
    }

    /*
     * @param lineNumber: line number to point to
     * sets pointer so that current line number is lineNumber
     */
    public void setLine(int lineNumber) {
        if (lineNumber < 0 || lineNumber > this.size()) {
            System.err.println("Line number out of bounds");
            System.exit(1);
        }
        this.lineNumber = lineNumber;
    }

    /*
     * returns one line without line break character and updates lineNumber to 
     * point to next line.
     */
    public String readLine() {
        if (lineNumber >= this.lines.size()) return null;
        String toReturn = this.lines.get(lineNumber);
        lineNumber++;
        return toReturn;
    }

    /*
     * @param sequence: String to be inserted into file representation
     * inserts line into file representation at current line, updates line number
     */
    public void insert(String line) {
        this.lines.add(this.lineNumber, line);
        this.lineNumber++;
    }

    public void insert(Task task) {
        this.insert(task.toLine());
    }

    /*
     * @param sequence: String to be inserted into file representation
     * inserts line into file representation at line pos, updates line number to point after position
     */
    public void insert(int pos, String line) {
        this.setLine(pos);
        this.insert(line);
    }

    public void insert (int pos, Task task) {
        this.setLine(pos);
        this.insert(task.toLine());
        this.lineNumber++;
    }

    /*
     * searches for task with identical label in FileNavigator. 
     * Points lineNumber to task if found, or to 0th position otherwise
     * 
     * return true if identical task found, false otherwise
     */
    public boolean findTask (Task task) {
        this.setLine(0);
        String nextLine;
        while ((nextLine = this.readLine()) != null) {
            if (Task.getLabel(nextLine).equals(task.label)) {
                this.setLine(this.getLine() - 1);
                return true;
            }
        }
        this.setLine(0);
        return false;
    }

    /*
     * searches for task with identical label in FileNavigator. 
     * Points lineNumber to task if found, or to 0th position otherwise
     * 
     * return true if identical task found, false otherwise
     */
    public boolean findTask (String task) {
        this.setLine(0);
        String nextLine;
        while ((nextLine = this.readLine()) != null) {
            if (Task.getLabel(nextLine).equals(task)) {
                this.setLine(this.getLine() - 1);
                return true;
            }
        }
        this.setLine(0);
        return false;
    }

    /*
     * @param task: label of task to be removed
     * Removes task from FileNavigator if found. Points FileNavigator to where 
     * task was if found, 0th position otherwise
     *
     */
    public boolean removeTask(String task) {
        if (findTask(task)) {
            this.lines.remove(this.getLine());
            this.lineNumber--;
            return true;
        }
        return false; 
    }

    /*
     * @param task: line number of task to be removed
     * Assumes there are at least lineNumber tasks and lineNumber is positive.
     * Removes task in 1-indexed position lineNumber from fileNavigator. Points lineNumber
     * at beginning of FileNavigator.
     *
     */
    public void removeTask(int lineNumber) {
        this.lines.remove(lineNumber - 1);
    }


    static void duplicateLabelExit(String label) {
        System.err.println(String.format("Duplicate label: %s already exists in to-do list", label));
        System.exit(1);
    }

    static void noSuchLabelExit(String label) {
        System.err.println(String.format("No such label: %s not in to-do list", label));
        System.exit(1);
    }


    public void close() {
        try{
            BufferedWriter br = new BufferedWriter(new FileWriter(new File(fileName)));
            for (int i = 0; i < lines.size(); i++) {
                br.write(lines.get(i) + "\n");
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}

