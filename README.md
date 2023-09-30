# Quick To-Do
Quick To-Do is a easily understandable command-line application designed to allow the user to add, remove, and modify tasks on a to-do list with the details they choose to include.
With multiple organizational flags, Quick To-Do allows the user to customize their experience. The application uses picocli to process commands and provide helpful feedback. 
A local file stores tasks in the to-do list when the task is not running. Qdo uses the date, time, and 
priority of tasks to organize them, search for them, and check whether they are due. It uses tags to search for them, 
and can store a longer description of the task.
## Installation
To install Quick To-Do, simply download the executable `qdo.exe` in the src directory.
Place the executable in any folder. From there, you can use the executable from within
the folder using the command `./qdo`. To use the command from anywhere on your device,
edit the PATH environment variable to include the directory of `qdo.exe` on your machine.
## Usage
### Add
Use the `add` subcommand to add a task to the file. A task label must be specified, but other flags are optional. 
Use `--label <task label>` to specify the label of the task to be added. Or, you can specify the task label with positional parameters. 
Use `--due <date> <time>`  to specify the date and, optionally, the time the task should be completed in.
Use `--priority <priority-level>` to specify the priority level of the task.
Use `--tag <tag>` to specify the tag of the task and `--description <description of task>` to specify the description of the task.
### List
Use the `list` subcommand to list the tasks in the to-do list in a readable format. By default, `list` lists all tasks in the to-do list. 
Use `--label <task label>` to only list the task with the given label. Or, you can specify the task label with positional parameters. 
Use `--due <date> <time>` to only list tasks due by the given date and time.
Use `--priority <priority level>` to only lists tasks with a priority level equal to or greater than the given priority level.
Use `--tag <tag of task>` to only list tasks with a tag matching the tag argument.
Use `--no-tag` to only list tasks without a tag.
### Remove
Use `remove --label <task label>` to remove a task with the given label from the to-do list. Or, you can specify the task label with positional parameters.

### Modify
Use the `modify` subcommand with a label flag to modify any features a task with the given label.
Use `--label <task label>` to specify the label of the task to be modified. Or, you can specify the task label with positional parameters. 
Use `--new-label <new label>` flag to change the label of the task to the new label.
Use `--due <date> <time>` flag to change the date and, optionally, time of the task.
Use `--priority <new priority>` to change the priority level of the task.
Use `--tag <new tag>` to change the tag of the task.
Use `--description <new description>` to change the description of the task.
## Future Features
I plan on:
- Allowing the user to input the date with more formats
- Implementing an autocomplete or searching function so that the user does not need to type the entire label for the `list` and `remove` subcommands.
- Improving help messages