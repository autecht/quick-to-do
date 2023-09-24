/* 
 * Represents task. Includes helper functions to compare and work with tasks and
 * parts of tasks.
 */
public class Task {
    String label;
    TaskDateTime due;
    String priority;
    String tag;
    String description;
    Task(String label, String date, String time, String priority, String tag, String description) {
        this.label = label;
        this.due = TaskDateTime.of(date, time);
        this.priority = priority;
        this.tag = tag;
        this.description = description;
    }

    /*
     * creates new Task represented by arguments
     */
    Task(String label, String[] dateTime, String priority, String tag, String description) throws Exception {
        this.label = label;
        this.due = TaskDateTime.of(dateTime);
        this.priority = priority;
        this.tag = tag;
        this.description = description;
    }

    /*
     * creates new task from line representing entire task in format
     * label#date#time#priority#tag#description
     */
    public static Task of(String taskLine) {
        String[] sections = getSections(taskLine);
        return new Task(sections[0], sections[1], sections[2], sections[3], sections[4], sections[5]);
    }

    

    /*
     * param arr: array of Strings
     * returns empty string if arr is null, elements of arr joined with space otherwise
     */
    static String convertToString(String[] arr) {
        if (arr == null) return "";
        else return String.join(" ", arr);
    }

    /*
     * Determines whether this Task has higher priority than task represented by String task.
     * Determines prioirity first with whether date is more recent, then by whether
     * priority is greater, then by whether label comes last lexicographically.
     * 
     * returns integer greater than 1 if this Task has higher prioirity, integer
     * less than 1 if String task has higher priority, 0 otherwise
      */
    public int compareTo(String task) {
        return this.compareTo(Task.of(task));
    }

    public int compareTo(Task other) {
        int dateTimeCompare = this.due.compareTo(other.due);
        if (dateTimeCompare != 0) return dateTimeCompare;

        int prioirtyCompare = comparePriorities(this.priority, other.priority);
        if (prioirtyCompare != 0) return prioirtyCompare;

        return -this.label.compareTo(other.label);
    }

    static int comparePriorities(String p1, String p2) {
        boolean firstEmpty = p1.equals("");
        boolean secondEmpty = p2 == null || p2.equals("");
        if (firstEmpty) {
            if (secondEmpty) return 0;
            else return -1;
        }
        if (secondEmpty) return 1;

        return Integer.parseInt(p1) - Integer.parseInt(p2);
    }

    /*
     * returns String that is representation of task in format label#due#priority#tag#description
     */
    public String toLine() {
        return this.label + "#" + this.due.getDate() + "#" + this.due.getTime() 
                + "#" + this.priority + "#" + this.tag + "#" + this.description;
    }

    /*
     * returns the 0-indexed sectionNumberth section of task represente by line, where line is
     * formatted as label#due#priority#tag#description
     */
    static String getSection(String line, int sectionNumber) {
        return getSections(line)[sectionNumber];
    }

    /*
     * Splits line formatted as label#due#time#priority#tag#description into String array
     * of length 4 
     *
     * returns String {label, date, time, priority, tag, description}
     */
    static String[] getSections(String line) {
        String[] result = {"", "", "", "", "", ""};
        int pos = 0;
        for (int i = 0; i < 6; i++) {
            char c;
            while (pos < line.length() && (c = line.charAt(pos)) != '#') {
                result[i] += c;
                pos++;
            }
            pos++;
        }

        return result;
    }



    // returns substring that is priority portion from line
    static String getPriority(String line) {
        return getSection(line, 3);
    }

    static String getDate(String line) {
        return getSection(line, 1);
    }

    static String getLabel(String line) {
        return getSection(line, 0);
    }

    @Override
    public String toString() {
        String whitespace = "     ";
        String newLine = "\n";
        String result = String.format("Task: %s", this.label);
        String date = this.due.getDate();
        String time = this.due.getTime();

        if (!date.equals("")) result += newLine + whitespace + "Date: " + date;
        if (!time.equals("")) result += newLine + whitespace + "Time: " + time;
        if (!this.priority.equals("")) result += newLine + whitespace + "Priority: " + this.priority;
        if (!this.tag.equals("")) result += newLine + whitespace + "Tag: " + this.tag;
        if (!this.description.equals("")) result += newLine + whitespace + "Description: " + this.description;
        return result;
    }

    /*
     * return String representation of Task with color from ANSI escape code
     * red represents task due date and time have passed
     * green represents task due date is today
     */
    public String toStringWithColor() {
        String escapeSequence = "";
        TaskDateTime today = TaskDateTime.now();
        if (today.compareTo(this) < 0) {
            escapeSequence = Qdo.ANSI_RED;
        }
        else {
            boolean sameDay = today.getDateInt() == this.due.getDateInt();
            if (sameDay) {
                escapeSequence = Qdo.ANSI_GREEN;
            }
        }
        return escapeSequence + this.toString() + Qdo.ANSI_RESET;

    }
    
}
