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

        // 🎨 COLORS
        Color bgColor = new Color(245, 247, 250);
        Color buttonColor = new Color(52, 152, 219);
        Color buttonText = Color.WHITE;
        Color listBg = Color.WHITE;
        Color listText = Color.DARK_GRAY;

        frame.getContentPane().setBackground(bgColor);

        // 🔹 TOP PANEL (no blue background)
        JPanel topPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        topPanel.setBackground(bgColor);

        nameField = new JTextField();
        dueField = new JTextField();
        priorityField = new JTextField();
        JButton addButton = new JButton("Add Task");

        styleButton(addButton, buttonColor, buttonText);

        topPanel.add(createStyledLabel("Task Name:", Color.BLACK));
        topPanel.add(createStyledLabel("Due Date (YYYY-MM-DD):", Color.BLACK));
        topPanel.add(createStyledLabel("Priority:", Color.BLACK));
        topPanel.add(new JLabel(""));
        topPanel.add(nameField);
        topPanel.add(dueField);
        topPanel.add(priorityField);
        topPanel.add(addButton);
        frame.add(topPanel, BorderLayout.NORTH);

        // 🔹 SEARCH PANEL
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(bgColor);
        searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        JButton undoButton = new JButton("Undo");
        JButton dequeueButton = new JButton("Dequeue Completed");
        JButton peekButton = new JButton("Peek Last Action");
        JButton viewCompletedButton = new JButton("View Completed");

        styleButton(searchButton, buttonColor, buttonText);
        styleButton(undoButton, buttonColor, buttonText);
        styleButton(dequeueButton, buttonColor, buttonText);
        styleButton(peekButton, buttonColor, buttonText);
        styleButton(viewCompletedButton, buttonColor, buttonText);

        JPanel searchButtons = new JPanel();
        searchButtons.setBackground(bgColor);
        searchButtons.add(searchButton);
        searchButtons.add(undoButton);
        searchButtons.add(peekButton);
        searchButtons.add(dequeueButton);
        searchButtons.add(viewCompletedButton);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButtons, BorderLayout.EAST);
        frame.add(searchPanel, BorderLayout.SOUTH);

        // 🔹 TASK LIST DESIGN
        taskList.setBackground(listBg);
        taskList.setForeground(listText);
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskList.setSelectionBackground(new Color(100, 149, 237));
        taskList.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(scrollPane, BorderLayout.CENTER);

        // 🔹 CONTEXT MENU
        JPopupMenu menu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Edit Task");
        JMenuItem deleteItem = new JMenuItem("Delete Task");
        JMenuItem completeItem = new JMenuItem("Mark Complete");
        menu.add(editItem);
        menu.add(deleteItem);
        menu.add(completeItem);
        taskList.setComponentPopupMenu(menu);

        // 🎯 LOGIC (same)
// add
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String due = dueField.getText().trim();
            String priority = priorityField.getText().trim();

            // Empty fields check
            if (name.isEmpty() || due.isEmpty() || priority.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Date validation
            if (!taskManager.isValidDate(due)) {
                JOptionPane.showMessageDialog(frame, "Invalid date format! Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ✅ Priority validation (this is the fix)
            if (!priority.equalsIgnoreCase("High") &&
                    !priority.equalsIgnoreCase("Medium") &&
                    !priority.equalsIgnoreCase("Low")) {
                JOptionPane.showMessageDialog(frame,
                        "Priority must be one of the following: High, Medium, or Low.",
                        "Invalid Priority",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

// Format the priority properly (capitalize first letter)
            String formattedPriority = priority.substring(0, 1).toUpperCase() + priority.substring(1).toLowerCase();

// Create the task using the formatted priority
            Task t = new Task(name, due, formattedPriority);
            taskManager.addTask(t); // push
            listModel.addElement(t);


            // Clear input fields
            nameField.setText("");
            dueField.setText("");
            priorityField.setText("");
        });



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

            String formattedPriority = newPriority.substring(0, 1).toUpperCase() + newPriority.substring(1).toLowerCase();
            Task newTask = new Task(newName, newDue, formattedPriority);
            taskManager.editTask(selected, newTask);
            listModel.setElementAt(newTask, taskList.getSelectedIndex());
        });

        deleteItem.addActionListener(e -> {
            Task selected = taskList.getSelectedValue();
            if (selected == null) return;
            taskManager.deleteTask(selected);
            listModel.removeElement(selected);
        });

        completeItem.addActionListener(e -> {
            Task selected = taskList.getSelectedValue();
            if (selected == null) return;
            taskManager.completeTask(selected);
            listModel.removeElement(selected);
            JOptionPane.showMessageDialog(frame, "Task completed (enqueued).");
        });

        undoButton.addActionListener(e -> {
            taskManager.undoLastAction();
            listModel.clear();
            for (Task t : taskManager.getTasks()) listModel.addElement(t);
        });

        peekButton.addActionListener(e -> {
            Task top = taskManager.peekLastAction();
            if (top != null)
                JOptionPane.showMessageDialog(frame, "Top of Stack (Last Action): " + top);
            else
                JOptionPane.showMessageDialog(frame, "Stack is empty.");
        });

        dequeueButton.addActionListener(e -> {
            Task t = taskManager.dequeueCompleted();
            if (t == null)
                JOptionPane.showMessageDialog(frame, "No completed tasks to dequeue.");
            else
                JOptionPane.showMessageDialog(frame, "Removed oldest completed: " + t);
        });

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

    // 🔹 Helper method for label styling
    private JLabel createStyledLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    // 🔹 Helper method for button styling
    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoListApp::new);
    }
}
