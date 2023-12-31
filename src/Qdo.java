// TODO: add options for list, increase date formats
// TODO: fix bugs - no label searching

import java.io.File;
import java.util.Scanner;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;



@Command(subcommands = {Add.class, List.class, Remove.class, Modify.class, Clear.class}, name = "qdo", version = "qdo 2.0", mixinStandardHelpOptions = true)
public class Qdo{
    static final String FILENAME = "tasks.txt";
    static final char DELIMETER_CHAR = '#';
    static final String DELIMETE_STRING = "#";
    static final String ANSI_RED = "\033[91m";
    static final String ANSI_GREEN = "\033[32m";
    static final String ANSI_RESET = "\033[0m";



    public static void main(String[] args) throws Exception {
        
        for (String argument: args) {
            for (int i = 0; i < argument.length(); i++) {
                if (argument.charAt(i) == DELIMETER_CHAR) {
                    throw new Exception(String.format(
                        "Argument %s: cannot use delimeter %c", argument, DELIMETER_CHAR));
                }
            }
        }
        int exitCode = new CommandLine(new Qdo()).execute(args);
        System.exit(exitCode);
    }


    /*
     * @param labelOption: label specified by flag, null if not specified
     * @param labelParem: label specified by positional parameters, null if not specified
     *
     * Checks that exactly one label specified. Returns this label.
     */
    public static String[] getRequiredLabel(String[] labelOption, String[] labelParam, String subcommand) throws Exception {
        boolean optionSpecified = labelOption != null;
        boolean paramSpecified = labelParam != null;

        if (optionSpecified && paramSpecified)
            throw new Exception(String.format("Usage: Qdo %s --label <task label> OR Qdo %s <task label>. " 
                    + "Cannot have both label flag and positional parameters.", subcommand, subcommand));
        if (!optionSpecified && !paramSpecified)
            throw new Exception(String.format("Usage: Qdo %s --label <task label> OR Qdo %s <task label>. " 
                    + "Must include --label flag or positional parameters.", subcommand, subcommand));

        if (optionSpecified)
            return labelOption;
        else
            return labelParam;
    }

    /*
     * @param labelOption: label specified by flag, null if not specified
     * @param labelParem: label specified by positional parameters, null if not specified
     *
     * Checks that up to one label specified. Returns this label (null if no label specified).
     */
    public static String[] getOptionalLabel(String[] labelOption, String[] labelParam, String subcommand) throws Exception {
        boolean optionSpecified = labelOption != null;
        boolean paramSpecified = labelParam != null;

        if (optionSpecified && paramSpecified)
            throw new Exception(String.format("Usage: Qdo %s --label <task label> OR Qdo %s <task label>. " 
                    + "Cannot have both label flag and positional parameters.", subcommand, subcommand));
        if (paramSpecified)
            return labelParam;
        else
            return labelOption;
    }
}

@Command(name = "add", mixinStandardHelpOptions = true, description = "Add task to-do list")
class Add implements Runnable {


    @Option(names={"--tag", "-t"}, arity = "1..*", 
            description = "Tag with which task is associated. Can search to-do list by tag")
    String[] tag;

    @Option(names = {"--description"}, arity = "1..*", description = "Description of task")
    String[] description;

    @Option(names = {"--priority", "-p"}, description = "Integer representing priority level of task, where higher integer is higher priority")
    String priority;

    @Option(names= {"--due"}, arity = "0..*", 
        description = "Day and, optionally, time task should be completed by. Earlier dates and times given higher precedence")
    String[] due;
    
    @Option(names = {"--label", "-l"}, arity="1..*", description = "Label representing task")
    String[] labelOption;

    @Parameters (paramLabel = "<task label>", arity = "0..*", description = "Label representing task (optional replacement for --label flag)")
    String[] labelParam;

    @Override
    public void run() {
        try {
            String[] label = Qdo.getRequiredLabel(labelOption, labelParam, "add");
            writeTask(label, due, priority, tag, description);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Inserts task represented by parameters given into file in position based on task precedence.
     * Points lineNumber to point to line after task.
     */
    static void writeTask(String[] label, String[] due, String priority, String[] tag, String[] description) throws Exception{
        // convert arguments to Strings
        String labelArgument = Task.convertToString(label);
        String priorityArgument;
        if (priority == null) priorityArgument = "";
        else priorityArgument = priority;
        String tagArgument = Task.convertToString(tag);
        String descriptionArgument = Task.convertToString(description);
        
        FileNavigator fn = new FileNavigator(Qdo.FILENAME);
        Task task = new Task(labelArgument, due, priorityArgument, tagArgument, descriptionArgument);
        if (fn.findTask(task)) {
            throw new Exception("Duplicate task");
        }

        String taskLine = null;
        while ((taskLine = fn.readLine()) != null) {
            if (task.compareTo(taskLine) > 0) break;
        }
        if (taskLine == null) fn.insert(task); // insert at end of file
        else fn.insert(fn.getLine() - 1, task); // insert before task with lower precedence
        fn.close();
    }

    
}

@Command(name = "list", mixinStandardHelpOptions= true, description = "List tasks currently on to-do list")
class List implements Runnable {

	@Option(names = {"--due", "--due-by"}, arity = "0..*",
            description = "Date and, optionally, time by which tasks listed are due, in mm/dd/yyyy format")
    String[] due;

    @Option(names = {"--priority", "-p"}, description = "Minimum priority of tasks listed")
    String priority;

    @Option(names = {"--tag", "-t"}, arity = "0..*", description = "Tag of tasks listed")
    String[] tag;

    @Option(names = {"--no-tag"}, arity = "0", description = "List only tasks with no tag. Equivalent to not specifying tag option argument")
    boolean noTag;

    @Option(names = {"--label", "-l"}, arity="1..*", description = "Label representing task")
    String[] labelOption;

    @Parameters (paramLabel = "<task label>", arity = "0..*", description = "Label representing task (optional replacement for --label flag)")
    String[] labelParam;


	@Override
    public void run() {
        String[] tagArgument = tag;
        if (noTag) tagArgument = null;
        try {  
            String[]  label = Qdo.getOptionalLabel(labelOption, labelParam, "list");
            listTasks(label, due, priority, tagArgument);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

    /*
     * @param label: If label is not null, only list task with identical label (or all tasks if empty)
     * @param date: Only list tasks with a due date before or equal to the given date (all tasks if null)
     * @param priority: Only list tasks with a priority greater than or equal to the given priority (all tasks if empty or null)
     * @param tag: If not null, only list tasks with the given tag
     *
     * Lists tasks in "tasks.txt" based on arguments given
     */
    static void listTasks(String[] label, String[] due, String priority, String[] tag) throws Exception {
        FileNavigator fn = new FileNavigator(Qdo.FILENAME);
        String line = null;
        while ((line = fn.readLine()) != null) {
            Task currentTask = Task.of(line);
            if (label != null) {
                if (!currentTask.label.equals(Task.convertToString(label))) continue;
            }

            TaskDateTime dateTimeCutoff = TaskDateTime.of(due);
            if (currentTask.due.compareTo(dateTimeCutoff) < 0) break;
            if (Task.comparePriorities(currentTask.priority, priority) < 0) continue;
            if (tag != null){
                String match = Task.convertToString(tag);
                if (match.equals(currentTask.tag)) {
                    System.out.println(String.format("%d) %s", fn.lineNumber, currentTask.toStringWithColor()));
                }
            }
            else 
                System.out.println(String.format("%d) %s", fn.lineNumber, currentTask.toStringWithColor()));
            
        }
        fn.close();
    }

}

@Command(name = "remove", mixinStandardHelpOptions = true, description = "Remove/check off task from to-do list")
class Remove implements Runnable {

    @Option(names = {"--label", "-l"}, arity="1..*", description = "Label representing task")
    String[] labelOption;

    @Parameters (paramLabel = "<task label>", arity = "0..*", description = "Label representing task (optional replacement for --label flag)")
    String[] labelParam;

    @Option (names = {"--number", "-n", "--num", "--task-number"}, description = "Number of task to be removed. For example, qdo remove --number 5 will remove the 5th task from the list")
    int taskNum;

    @Override
    public void run() {
        try{
        String[] label = Qdo.getOptionalLabel(labelOption, labelParam, "remove");
        if (label == null) {
            removeTask(taskNum);
        }
        else
            removeTask(label); } catch (Exception e) {e.printStackTrace();}
    }

    static void removeTask(String[] label) {
        String labelString = Task.convertToString(label);
        FileNavigator fn = new FileNavigator(Qdo.FILENAME);
        if (!fn.removeTask(labelString)) {
            FileNavigator.noSuchLabelExit(labelString);
        }
        fn.close();
    }

    /*
     * remove 1-indexed lineNumberth task from to-do list
     * throw exception if outside of range
     */
    static void removeTask(int lineNumber) throws Exception{
        if (lineNumber == 0)
            throw new Exception("Must specify label or line number of task to remove");
        
        FileNavigator fn = new FileNavigator(Qdo.FILENAME);
        if (lineNumber < 0 || lineNumber > fn.size()) {
            throw new Exception(String.format("Task number out of range. No task at postion %d", lineNumber));
        }
        fn.removeTask(lineNumber);
        fn.close();
    }
}

@Command(name = "modify", mixinStandardHelpOptions = true, description = "Modify aspects of task on to-do list")
class Modify implements Runnable {

    @Option(names = {"--label", "-l"}, arity="1..*", description = "Label representing task")
    String[] labelOption;

    @Parameters (paramLabel = "<task label>", arity = "0..*", description = "Label representing task (optional replacement for --label flag)")
    String[] labelParam;

    @Option(names = {"-n", "--new-label"}, arity = "1..*", description = "New label to be changed to")
    String[] newLabel;

    @Option(names={"--tag", "-t"}, description = "Tag of modified task")
    String[] tag;

    @Option(names = {"--description"}, arity = "0..*", description = "Description of modified task")
    String[] description;

    @Option(names = {"--priority", "-p"}, description = "Priority of modified task")
    String priority;

    @Option(names= {"--due"}, arity = "0..*", description = "Date modified task should be finished, in mm/dd/yyyy format")
    String[] due;

    @Override
    public void run() {
        try {
            String[] label = Qdo.getRequiredLabel(labelOption, labelParam, "modify");
            modifyTask(label, newLabel, due, priority, tag, description);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void modifyTask(String[] originalLabel, String[] newLabel, String[] due, 
            String priority, String[] tag, String[] description) throws Exception {
        
        // Don't change if null input
        String[] labelArgument; // label that will be added
        String[] dueArgument;
        String priorityArgument;
        String[] tagArgument;
        String[] descriptionArguement;
         
        FileNavigator fn = new FileNavigator(Qdo.FILENAME);
        if (newLabel != null){
            labelArgument = newLabel;
            if (fn.findTask(Task.convertToString(labelArgument))) { // Check newLabel not already in file 
                FileNavigator.duplicateLabelExit(Task.convertToString(labelArgument));
            }
        } else labelArgument = originalLabel;

        Task taskToModify = Task.of(fn.readLine());
        fn.close();

        if (due == null) dueArgument = taskToModify.due.toArr();
        else dueArgument = due;
        if (priority == null) priorityArgument = taskToModify.priority;
        else priorityArgument = priority;
        if (tag == null) tagArgument = new String[] {taskToModify.tag};
        else tagArgument = tag;
        if (description == null) descriptionArguement = new String[] {taskToModify.description};
        else descriptionArguement = description;
        
        // Modify task
        Remove.removeTask(originalLabel);
        Add.writeTask(labelArgument, dueArgument, priorityArgument, tagArgument, descriptionArguement);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Modify()).execute(args);
        System.exit(exitCode);
    }
}


@Command(name = "clear", mixinStandardHelpOptions= true, description = "Remove all tasks from to-do list")
class Clear implements Runnable {

    @Override
    public void run() {

        Scanner inputScanner = new Scanner(System.in);
        String input = "";
        do {
            System.out.println("Are you sure you want to remove all tasks from to-do list? Change cannot be reverted. Type y/n");
            if (input.equals("n")) {
                inputScanner.close();
                System.exit(0);
            }
        }
        while (!(input = inputScanner.nextLine()).equals("y"));

        inputScanner.close();
        File toDelete = new File(Qdo.FILENAME);
        toDelete.delete();
    }


}