import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ToDoListApp {
    private TaskManager taskManager;
    private DefaultListModel<Task> listModel;
    private JList<Task> taskList;
    private JTextField nameField, dueField, priorityField, searchField;

    public ToDoListApp() {
        taskManager = new TaskManager();
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);

        JFrame frame = new JFrame("To-Do List App");
        frame.setSize(700, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // top panel
        JPanel topPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        nameField = new JTextField();
        dueField = new JTextField();
        priorityField = new JTextField();
        JButton addButton = new JButton("Add Task");

        topPanel.add(new JLabel("Task Name:"));
        topPanel.add(new JLabel("Due Date (YYYY-MM-DD):"));
        topPanel.add(new JLabel("Priority:"));
        topPanel.add(new JLabel(""));
        topPanel.add(nameField);
        topPanel.add(dueField);
        topPanel.add(priorityField);
        topPanel.add(addButton);
        frame.add(topPanel, BorderLayout.NORTH);

        // search bar
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        JButton undoButton = new JButton("Undo");
        JButton dequeueButton = new JButton("Dequeue Completed");
        JButton peekButton = new JButton("Peek Last Action");

        // 🔹 New button for viewing completed tasks
        JButton viewCompletedButton = new JButton("View Completed");

        JPanel searchButtons = new JPanel();
        searchButtons.add(searchButton);
        searchButtons.add(undoButton);
        searchButtons.add(peekButton);
        searchButtons.add(dequeueButton);
        searchButtons.add(viewCompletedButton); // 🔹 added here

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButtons, BorderLayout.EAST);
        frame.add(searchPanel, BorderLayout.SOUTH);

        frame.add(new JScrollPane(taskList), BorderLayout.CENTER);

        // context menu
        JPopupMenu menu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Edit Task");
        JMenuItem deleteItem = new JMenuItem("Delete Task");
        JMenuItem completeItem = new JMenuItem("Mark Complete");
        menu.add(editItem);
        menu.add(deleteItem);
        menu.add(completeItem);
        taskList.setComponentPopupMenu(menu);

        // add
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String due = dueField.getText().trim();
            String priority = priorityField.getText().trim();

            if (name.isEmpty() || due.isEmpty() || priority.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields required!");
                return;
            }
            if (!taskManager.isValidDate(due)) {
                JOptionPane.showMessageDialog(frame, "Invalid date format! Use YYYY-MM-DD.");
                return;
            }

            Task t = new Task(name, due, priority);
            taskManager.addTask(t); // push
            listModel.addElement(t);
            nameField.setText("");
            dueField.setText("");
            priorityField.setText("");
        });

        // edit
        editItem.addActionListener(e -> {
            Task selected = taskList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(frame, "Select a task to edit.");
                return;
            }

            String newName = JOptionPane.showInputDialog("Edit Name:", selected.getName());
            String newDue = JOptionPane.showInputDialog("Edit Due Date (YYYY-MM-DD):", selected.getDueDate());
            String newPriority = JOptionPane.showInputDialog("Edit Priority:", selected.getPriority());
            if (newName == null || newDue == null || newPriority == null) return;
            if (!taskManager.isValidDate(newDue)) {
                JOptionPane.showMessageDialog(frame, "Invalid date format!");
                return;
            }

            Task newTask = new Task(newName, newDue, newPriority);
            taskManager.editTask(selected, newTask); // push edited
            listModel.setElementAt(newTask, taskList.getSelectedIndex());
        });

        // delete
        deleteItem.addActionListener(e -> {
            Task selected = taskList.getSelectedValue();
            if (selected == null) return;
            taskManager.deleteTask(selected); // push deleted
            listModel.removeElement(selected);
        });

        // complete
        completeItem.addActionListener(e -> {
            Task selected = taskList.getSelectedValue();
            if (selected == null) return;
            taskManager.completeTask(selected); // enqueue
            listModel.removeElement(selected);
            JOptionPane.showMessageDialog(frame, "Task completed (enqueued).");
        });

        // undo
        undoButton.addActionListener(e -> {
            taskManager.undoLastAction(); // pop
            listModel.clear();
            for (Task t : taskManager.getTasks()) listModel.addElement(t);
        });

        // peek
        peekButton.addActionListener(e -> {
            Task top = taskManager.peekLastAction(); // peek
            if (top != null)
                JOptionPane.showMessageDialog(frame, "Top of Stack (Last Action): " + top);
            else
                JOptionPane.showMessageDialog(frame, "Stack is empty.");
        });

        // dequeue completed
        dequeueButton.addActionListener(e -> {
            Task t = taskManager.dequeueCompleted(); // dequeue
            if (t == null)
                JOptionPane.showMessageDialog(frame, "No completed tasks to dequeue.");
            else
                JOptionPane.showMessageDialog(frame, "Removed oldest completed: " + t);
        });

        // 🔹 view completed
        viewCompletedButton.addActionListener(e -> {
            java.util.Queue<Task> completed = taskManager.getCompletedQueue();
            if (completed.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No completed tasks yet!");
            } else {
                StringBuilder sb = new StringBuilder("Completed Tasks:\n\n");
                for (Task t : completed) {
                    sb.append("- ").append(t).append("\n");
                }
                JOptionPane.showMessageDialog(frame, sb.toString());
            }
        });

        // search
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) return;
            Task found = taskManager.searchTask(keyword);
            if (found != null) {
                taskList.setSelectedValue(found, true);
                JOptionPane.showMessageDialog(frame, "Task found: " + found);
            } else JOptionPane.showMessageDialog(frame, "No matching task.");
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoListApp::new);
    }
}
