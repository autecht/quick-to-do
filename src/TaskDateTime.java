import java.time.LocalDateTime;

/*
 * Class to represent, compare, and 
 */
public class TaskDateTime {
    LocalDateTime dateTime; // date and time of TaskDateTime, null if no date or time associated

    TaskDateTime (int year, int month, int dayOfMonth, int hour, int minute) {
        this.dateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
    }

    TaskDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /*
     * represents task with no time associated
     */
    public TaskDateTime() {
        this.dateTime = null;
    }

    /*
     * return new TaskDateTime where date is due[0] and time is due[1]
     * Assumes date is formatted as mm/dd/yyyy or is empty String and time is formatted as hh:mm
     */
    public static  TaskDateTime of(String[] due) throws Exception {
        due = checkFormat(due);
        String date = due[0];
        String time = due[1];
        
        return TaskDateTime.of(date, time);
    }

    /*
     * return new TaskDateTime where date is given by date argument and time given by time argument
     * Assumes date is formatted as mm/dd/yyyy and time is formatted as hh:mm
     */
    public static  TaskDateTime of(String date, String time) {
        boolean noDate = (date == null) || (date.equals(""));
        boolean noTime = (time == null) || (time.equals(""));
        if (noDate && noTime) return new TaskDateTime(); // no date or time associated

        int year;
        int day;
        int month;
        if (noDate) { // time with no date
            LocalDateTime today = LocalDateTime.now();
            year = today.getYear();
            month = today.getMonthValue();
            day = today.getDayOfMonth();
        }
        else {
            year = Integer.parseInt(date.substring(6, 10));
            day = Integer.parseInt(date.substring(3,5));
            month = Integer.parseInt(date.substring(0,2));
        }

        if (noTime) time = "23:59";
        int hour = Integer.parseInt(time.substring(0,2));
        int minute = Integer.parseInt(time.substring(3,5));
        return new TaskDateTime(year, month, day, hour, minute);
    }

    /*
     * Compare TaskDateTimes based on which has date and time occuring earlier
     */
    public int compareTo(TaskDateTime other) {
        if (this.dateTime == null) {
            if (other.dateTime == null) return 0;
            else return -1;
        }
        if (other.dateTime == null) return 1;
        return -this.dateTime.compareTo(other.dateTime);
    }

    /*
     * If properly formatted, returns String array with 0th element being date 
     * in mm/dd/yyyy format and 1st element being time in hh:mm military time format
     */
    public static String[] checkFormat(String[] due) throws Exception {
        if (due == null) 
            return new String[] {null, null}; // no date or time
        String[] dateAndTime = new String[2];
        if (due.length > 2) 
            throw new Exception("Usage: --due <date> <time>");
        dateAndTime[0] = convertToDate(due[0]);

        if (due.length == 2) dateAndTime[1] = convertToTime(due[1]);
        else dateAndTime[1] = convertToTime("");


        return dateAndTime;
    }

    /*
     * Retruns date in format mm/dd/yyyy, where empty String represents no date
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
            else if (!isNumber(curr)) throwDateException();
        }
        return date;
    }

    static void throwDateException() {
        //throw new Exception("due must be date in format mm/dd/yyyy");
        System.out.println("date must be date in format mm/dd/yyyy");
        System.exit(1);
    }

    static String convertToTime(String time) throws Exception {
        if (time.equals("") || time == null) return "";
        if (time.length() != 5) throwTimeException();
        for (int i = 0; i < time.length(); i++) {
            if (i == 2) {
                if (time.charAt(i) != ':') throwTimeException();
            }
            else if (!isNumber(time.charAt(i))) throwTimeException();
        }
        return time;
    }

    private static void throwTimeException() throws Exception {
        throw new Exception("time must be in format hh:mm");
    }

    static boolean isNumber(char c) {
        return (c >= 48) && (c <= 57);
    }

    static TaskDateTime now() {
        return new TaskDateTime(LocalDateTime.now());
    }

    /*
     * return integer encoding of date
     */
    public int getDateInt() {
        if (this.dateTime == null) return -1;
        int year = this.dateTime.getYear();
        int day = this.dateTime.getDayOfYear();
        return year * 366 + day;
    }

    /*
     * returns array representation of TaskDateTime, where sections[0] is date and sections[1] is time
     */
    public String[] toArr() {
        String date = this.getDate();
        String time = this.getTime();
        String[] arr = {date, time};
        return arr;
    }

    /* 
     * returns date of TaskDateTime, in format mm/dd/yyyy
    */
    public String getDate() {
        if (this.dateTime == null) {
            return "";
        }
        int day = dateTime.getDayOfMonth();
        String dayString = String.format("%d", day);
        if (dayString.length() == 1) dayString = "0" + dayString;
        int month = dateTime.getMonthValue();
        String monthString = String.format("%d", month);
        if (monthString.length() == 1) monthString = "0" + monthString;
        int year = dateTime.getYear();
        String yearString = String.format("%d", year);
        return String.format("%s/%s/%s", monthString, dayString, yearString);
    }

    /*
     * return time of TaskDateTime, in format hh/mm
     */
    public String getTime() {
        if (this.dateTime == null) return "";
        int hour = dateTime.getHour();
        String hourString = String.format("%d", hour);
        if (hourString.length() == 1) hourString = "0" + hourString;

        int minute = dateTime.getMinute();
        String minuteString = String.format("%d", minute);
        if (minuteString.length() == 1) minuteString = "0" + minuteString;

        return String.format("%s:%s", hourString, minuteString);
    }

    public boolean equals(Object other) {
        if (other.getClass() != TaskDateTime.class) return false;
        TaskDateTime otherTDT = (TaskDateTime) other;
        LocalDateTime thisLDT = this.dateTime;
        LocalDateTime otherLDT = otherTDT.dateTime;
        return thisLDT.equals(otherLDT);
    }

    public boolean hasNoDateTime() {
        return this.dateTime == null;
    }

    public int compareTo(Task t) {
        return this.compareTo(t.due);
    }

}
