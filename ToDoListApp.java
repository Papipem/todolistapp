import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ToDoListApp extends JFrame {
    private TaskManager manager;
    private DefaultListModel<String> listModel;
    private JList<String> taskDisplay;
    private JTextField nameField, dueDateField, priorityField, searchField;

    public ToDoListApp() {
        manager = new TaskManager();
        setTitle("To-Do List App");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 🧾 Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        nameField = new JTextField();
        dueDateField = new JTextField();
        priorityField = new JTextField();
        JButton addButton = new JButton("Add Task");

        inputPanel.add(new JLabel("Task Name:"));
        inputPanel.add(new JLabel("Due Date:"));
        inputPanel.add(new JLabel("Priority:"));
        inputPanel.add(new JLabel(""));
        inputPanel.add(nameField);
        inputPanel.add(dueDateField);
        inputPanel.add(priorityField);
        inputPanel.add(addButton);

        add(inputPanel, BorderLayout.NORTH);

        // 📝 Task List
        listModel = new DefaultListModel<>();
        taskDisplay = new JList<>(listModel);
        add(new JScrollPane(taskDisplay), BorderLayout.CENTER);

        // ⚙️ Buttons Panel
        JPanel buttonPanel = new JPanel();
        JButton deleteButton = new JButton("Delete");
        JButton editButton = new JButton("Edit");
        searchField = new JTextField(10);
        JButton searchButton = new JButton("Search");

        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(new JLabel("Search:"));
        buttonPanel.add(searchField);
        buttonPanel.add(searchButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // ➕ Add Task
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String due = dueDateField.getText().trim();
                String priority = priorityField.getText().trim();

                if (name.isEmpty() || due.isEmpty() || priority.isEmpty()) {
                    throw new IllegalArgumentException("Please fill out all fields.");
                }

                if (!priority.equalsIgnoreCase("High") &&
                        !priority.equalsIgnoreCase("Medium") &&
                        !priority.equalsIgnoreCase("Low")) {
                    throw new IllegalArgumentException("Priority must be High, Medium, or Low.");
                }

                manager.addTask(new Task(name, due, priority));
                refreshList(manager.getAllTasks());
                clearFields();

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 🗑️ Delete Task
        deleteButton.addActionListener(e -> {
            try {
                int selectedIndex = taskDisplay.getSelectedIndex();
                if (selectedIndex < 0) {
                    throw new IllegalStateException("Please select a task to delete.");
                }
                manager.deleteTask(selectedIndex);
                refreshList(manager.getAllTasks());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ✏️ Edit Task
        editButton.addActionListener(e -> {
            try {
                int selectedIndex = taskDisplay.getSelectedIndex();
                if (selectedIndex < 0) {
                    throw new IllegalStateException("Please select a task to edit.");
                }

                String newName = JOptionPane.showInputDialog("Enter new task name:");
                if (newName == null || newName.trim().isEmpty()) throw new IllegalArgumentException("Task name cannot be empty.");

                String newDueDate = JOptionPane.showInputDialog("Enter new due date:");
                if (newDueDate == null || newDueDate.trim().isEmpty()) throw new IllegalArgumentException("Due date cannot be empty.");

                String newPriority = JOptionPane.showInputDialog("Enter new priority (High/Medium/Low):");
                if (newPriority == null || newPriority.trim().isEmpty()) throw new IllegalArgumentException("Priority cannot be empty.");
                if (!newPriority.equalsIgnoreCase("High") &&
                        !newPriority.equalsIgnoreCase("Medium") &&
                        !newPriority.equalsIgnoreCase("Low")) {
                    throw new IllegalArgumentException("Priority must be High, Medium, or Low.");
                }

                manager.editTask(selectedIndex, newName, newDueDate, newPriority);
                refreshList(manager.getAllTasks());

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 🔍 Search Task
        searchButton.addActionListener(e -> {
            try {
                String keyword = searchField.getText().trim();
                List<Task> results;

                if (keyword.isEmpty()) {
                    results = manager.getAllTasks();
                } else {
                    results = manager.searchTasks(keyword);
                    if (results.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No tasks found matching your search.", "Search", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                refreshList(results);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error during search: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void refreshList(List<Task> tasks) {
        listModel.clear();
        for (Task task : tasks) {
            listModel.addElement(task.toString());
        }
    }

    private void clearFields() {
        nameField.setText("");
        dueDateField.setText("");
        priorityField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ToDoListApp().setVisible(true);
        });
    }
}
