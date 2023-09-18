/* 
 * Represents task. Includes helper functions to compare and work with tasks and
 * parts of tasks.
 */
public class Task {
    String label;
    String due;
    String priority;
    String tag;
    String description;
    Task(String label, String due, String priority, String tag, String description) {
        this.label = label;
        this.due = due;
        this.priority = priority;
        this.tag = tag;
        this.description = description;
    }

    /*
     * creates new task from line representing entire task in format
     * label#due#priority#tag#description
     */
    Task(String taskLine) {
        String[] sections = getSections(taskLine);
        this.label = sections[0];
        this.due = sections[1];
        this.priority = sections[2];
        this.tag = sections[3];
        this.description = sections[4];
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
     * Retruns date in format mm/dd/yyyy
     * throws error if date cannot be read
     */
    static String convertToDate(String date) {
        if (date.equals("") || date == null) return "";
        if (date.length() != 10) throwDateException();
        for (int i = 0; i < 10; i++) {
            char curr = date.charAt(i);
            if ((i == 2) || (i == 5)) {
                if (curr != '/') throwDateException();
            }
            else if ((curr < 48) || (curr > 57)) throwDateException();
        }
        return date;
    }

    static void throwDateException() {
        //throw new Exception("due must be date in format mm/dd/yyyy");
        System.out.println("due flag must be date in format mm/dd/yyyy");
        System.exit(1);
    }

    /*
     * assumes date1 and date2 are in mm/dd/yyyy format or are empty string
     * returns 0 if date1 same as date2, positive integer if date1 is before
     * than date2 (where empty string always considered less recent), 
     * or negative integer if date2 is more recent than date1
     */
    static int compareDates(String date1, String date2) {
        boolean firstEmpty = date1.equals("");
        boolean secondEmpty = date2.equals("");
        if (firstEmpty) {
            if (secondEmpty) return 0;
            else return -1;
        }
        if (secondEmpty) return 1;

        int date1year = Integer.parseInt(date1.substring(6, 10));
        int date2year = Integer.parseInt(date2.substring(6, 10));
        int diff = date2year - date1year;
        if (diff != 0) return diff;

        int date1month = Integer.parseInt(date1.substring(0, 2));
        int date2month = Integer.parseInt(date2.substring(0, 2));
        diff = date2month - date1month;
        if (diff != 0) return diff;

        int date1day = Integer.parseInt(date1.substring(3, 5));
        int date2day = Integer.parseInt(date2.substring(3, 5));
        diff = date2day - date1day;
        return diff;
    }

    /*
     * Determines whether this Task has higher priority than task represented by String task.
     * Determines prioirity first with whether date is more recent, then by whether
     * priority is greater, then by whether label comes last lexicographically.
     * 
     * returns integer greater than 1 if this Task has higher prioirity, integer
     * less than 1 if String task has higher priority, 0 otherwise
      */
    public int compareTask(String task) {
        String otherDate = getDate(task);
        int dateCompare = compareDates(this.due, otherDate);
        if (dateCompare != 0) return dateCompare;

        int prioirtyCompare = comparePriorities(this.priority, getPriority(task));
        if (prioirtyCompare != 0) return prioirtyCompare;

        return -this.label.compareTo(getLabel(task));
    }

    static int comparePriorities(String p1, String p2) {
        boolean firstEmpty = p1.equals("");
        boolean secondEmpty = p2.equals("");
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
        return this.label + "#" + this.due + "#" + this.priority + "#" + this.tag + "#" + this.description;
    }

    /*
     * returns the 0-indexed sectionNumberth section of task represente by line, where line is
     * formatted as label#due#priority#tag#description
     */
    static String getSection(String line, int sectionNumber) {
        return getSections(line)[sectionNumber];
    }

    /*
     * Splits line formatted as label#due#priority#tag#description into String array
     * of length 4 
     *
     * returns String {label, due, priority, tag, description}
     */
    static String[] getSections(String line) {
        String[] result = {"", "", "", "", ""};
        int pos = 0;
        for (int i = 0; i < 5; i++) {
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
        return getSection(line, 2);
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
        if (!this.due.equals("")) result += newLine + whitespace + "Due: " + this.due;
        if (!this.priority.equals("")) result += newLine + whitespace + "Priority: " + this.priority;
        if (!this.tag.equals("")) result += newLine + whitespace + "Tag: " + this.tag;
        if (!this.description.equals("")) result += newLine + whitespace + "Description: " + this.description; // TODO: should include newlines in description
        return result;
    }
    
}
