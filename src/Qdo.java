// TODO: add options for list,  increase date formats
// TODO: fix bugs - no label searching, weird things will happen if $ used

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;



@Command(subcommands = {Add.class, List.class, Remove.class, Modify.class}, name = "qdo", version = "qdo 1.0", mixinStandardHelpOptions = true)
public class Qdo{
    static final String fileName = "tasks.txt";
    static final char delimeter = '$';
    static final String delimeterString = "$";

    public static void main(String[] args) throws Exception {
        
        for (String argument: args) {
            for (int i = 0; i < argument.length(); i++) {
                if (argument.charAt(i) == delimeter) {
                    throw new Exception(String.format(
                        "Argument %s: cannot use delimeter %c", argument, delimeter));
                }
            }
        }
        int exitCode = new CommandLine(new Qdo()).execute(args);
        System.exit(exitCode);
    }
}

@Command(name = "add", mixinStandardHelpOptions = true, description = "Add task to-do list")
class Add implements Runnable {


    @Option(names={"--tag, -t"}, arity = "1..*", 
            description = "Tag with which task is associated. Can search to-do list by tag")
    String[] tag;

    @Option(names = {"--description"}, arity = "1..*", description = "Description of task")
    String[] description;

    @Option(names = {"--priority, -p"}, description = "Integer representing priority level of task, where higher integer is higher priority")
    String priority;

    @Option(names= {"--due"}, description = "Day task should be finished, in mm/dd/yyyy format. Earlier dates given higher precedence")
    String due;

    @Parameters(paramLabel = "<label>", arity="1..*", description = "Label representing task")
    String[] label;

    @Override
    public void run() {
        // deal with nulls and check date
        if (priority == null) priority = "";
        if (due == null) due = "";
        Task.checkDate(due);
        writeTask(Task.convertToString(label), due, priority,
            Task.convertToString(tag), 
            Task.convertToString(description));
    }

    /*
     * Inserts task represented by parameters given into file in position based on task priority.
     * Points lineNumber to point to line after task.
     */
    static void writeTask(String label, String due, String priority, String tag, String description) {
        FileNavigator fn = new FileNavigator(Qdo.fileName);
        Task task = new Task(label, due, priority, tag, description);
        if (fn.findTask(task)) {
            System.err.println("Duplicate task");
            System.exit(1);
        }

        String taskLine = null;
        while ((taskLine = fn.readLine()) != null) {
            if (task.compareTask(taskLine) > 0) break;
        }
        if (taskLine == null) fn.insert(task);
        else fn.insert(fn.getLine() - 1, task);
        fn.close();
    }

    
}

@Command(name = "list", mixinStandardHelpOptions= true, description = "List tasks currently on to-do list")
class List implements Runnable {
	
	@Override
    public void run() {
        listAll();
    }

    /*
     * List all tasks in file
     */
    static void listAll() {
        FileNavigator fn = new FileNavigator(Qdo.fileName);
        for (String line: fn.lines) {
            Task t = new Task(line);
            System.out.println(t);
        }
    }

}

@Command(name = "remove", mixinStandardHelpOptions = true, description = "Remove/check off task from to-do list")
class Remove implements Runnable {

    @Parameters (paramLabel = "<label>", arity = "1..*", description = "Label of task to be removed")
    String[] label;

    @Override
    public void run() {
        removeTask(Task.convertToString(label));
    }

    static void removeTask(String label) {
        FileNavigator fn = new FileNavigator(Qdo.fileName);
        if (!fn.removeTask(label)) {
            System.err.println("No task with label " + label);
            fn.close();
            System.exit(1);
        }
        fn.close();
    }
}

@Command(name = "modify", mixinStandardHelpOptions = true, description = "Modify aspects of task on to-do list")
class Modify implements Runnable {

    @Parameters(paramLabel = "<label>", arity = "1..*", description = "Label of task to be modified")
    String[] label;

    @Option(names = {"--label", "-l"}, arity = "1..*", description = "New label to be changed to")
    String[] newLabel;

    @Option(names={"--tag"}, description = "Tag of modified task")
    String[] tag;

    @Option(names = {"--description"}, arity = "0..*", description = "Description of modified task")
    String[] description;

    @Option(names = {"--priority"}, description = "Priority of modified task")
    String priority;

    @Option(names= {"--due"}, description = "Date modified task should be finished, in mm/dd/yyyy format")
    String due;

    @Override
    public void run() {
        modifyTask(label, newLabel, due,
                priority, tag, description);
    }

    static void modifyTask(String[] originalLabel, String[] newLabel, String due, 
            String priority, String[] tag, String[] description) {
        
        // Don't change if null input
        String labelArgument;
        String dueArgument;
        String priorityArgument;
        String tagArgument;
        String descriptionArguement;
        // Check newLabel not already in file  
        FileNavigator fn = new FileNavigator(Qdo.fileName);
        if (newLabel != null){
            labelArgument = Task.convertToString(newLabel);
            if (fn.findTask(labelArgument)) {
                FileNavigator.duplicateLabelExit(labelArgument);
            }
        } else labelArgument = Task.convertToString(originalLabel);
        if (!fn.findTask(Task.convertToString(originalLabel))) {
            FileNavigator.noSuchLabelExit(Task.convertToString(originalLabel));
        }
        Task taskToModify = new Task(fn.readLine());
        fn.close();

        if (due == null) dueArgument = taskToModify.due;
        else dueArgument = due;
        if (priority == null) priorityArgument = taskToModify.priority;
        else priorityArgument = priority;
        if (tag == null) tagArgument = taskToModify.tag;
        else tagArgument = Task.convertToString(tag);
        if (description == null) descriptionArguement = taskToModify.description;
        else descriptionArguement = Task.convertToString(description);
        
        // Modify task
        Remove.removeTask(Task.convertToString(originalLabel));
        Add.writeTask(labelArgument, dueArgument, priorityArgument, tagArgument, descriptionArguement);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Modify()).execute(args);
        System.exit(exitCode);
    }
}




/*
qdo add -tag -description -due -priority label
qdo list
qdo check label
qdo modify label -tag -description -due -priority
 */