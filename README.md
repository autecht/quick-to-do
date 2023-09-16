# Quick To-Do
Quick To-Do is a easily understandable command-line application designed to allow the user to add, remove, and modify tasks on a to-do list with the details they choose to include.
With multiple organizational flags, Quick To-Do allows the user to customize their experience. The application uses picocli to process commands and provide helpful feedback. 
A local file named "tasks.txt" stores data when the program is not running.
## Installation
To install Quick To-Do, simply download the executable `qdo.exe` in the src directory.
Place the executable in any folder. From there, you can use the executable from within
the folder using the command `./qdo`. To use the command from anywhere on your device,
edit the PATH environment variable to include the directory of `qdo.exe` on your machine.
## Usage
Use the `add` subcommand to add a task to the file. A label must be specified, but you can
choose whether to specify a date with the `--due` flag, a priority level with the `priority` flag, 
a tag with the `tag` flag, or a description of the task with the `description` flag.\\
Use the `list` subcommand to list all of the tasks in the to-do list in a readable format.\\
Use the `remove` subcommand with a label argument to remove a task with the given label from the to-do list.\\
Use the `modify` subcommand with a label argument to modify a task with the given label. Use the `--label` flag to change the label of the task, the `--due` flag to change the date of the task, 
the `--priority` flag to change the priority level of the task, the `--tag` flag to change the tag 
of the task, and the `--description` flag to change the description of the task.
## Future Features
I plan on:
    - Expanding the `list` subcommand flags to allow for listing of only tasks with specific features
    - Allowing the user to input the date with more formats
    - Implementing an autocomplete function so that the user does not need to type the entire label for the 
    `list` and `remove` subcommands.
