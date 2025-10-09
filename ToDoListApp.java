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

        JFrame frame = new JFrame("🗒 To-Do List Manager");
        frame.setSize(750, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 🎨 DARK THEME COLORS
        Color bgColor = new Color(33, 33, 36);
        Color cardColor = new Color(45, 45, 50);
        Color buttonColor = new Color(86, 156, 214);
        Color buttonHover = new Color(96, 170, 230);
        Color textColor = new Color(240, 240, 240);
        Color listBg = new Color(40, 40, 45);
        Color listText = Color.WHITE;

        Font baseFont = new Font("Segoe UI Semibold", Font.PLAIN, 14);
        Font labelFont = new Font("Segoe UI Semibold", Font.BOLD, 13);

        frame.getContentPane().setBackground(bgColor);

        // 🔹 TOP PANEL
        JPanel topPanel = new JPanel(new GridLayout(2, 4, 8, 8));
        topPanel.setBackground(cardColor);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nameField = createStyledField(baseFont, textColor, listBg);
        dueField = createStyledField(baseFont, textColor, listBg);
        priorityField = createStyledField(baseFont, textColor, listBg);
        JButton addButton = new JButton(" Add Task");
        styleButton(addButton, buttonColor, textColor, buttonHover, baseFont);

        topPanel.add(createStyledLabel("Task Name:", labelFont, textColor));
        topPanel.add(createStyledLabel("Due Date (YYYY-MM-DD):", labelFont, textColor));
        topPanel.add(createStyledLabel("Priority:", labelFont, textColor));
        topPanel.add(new JLabel(""));
        topPanel.add(nameField);
        topPanel.add(dueField);
        topPanel.add(priorityField);
        topPanel.add(addButton);
        frame.add(topPanel, BorderLayout.NORTH);

        // 🔹 SEARCH PANEL
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(cardColor);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        searchField = createStyledField(baseFont, textColor, listBg);
        JButton searchButton = new JButton(" Search");
        JButton undoButton = new JButton(" Undo");
        JButton peekButton = new JButton(" Peek");
        JButton dequeueButton = new JButton(" Dequeue Completed");
        JButton viewCompletedButton = new JButton(" View Completed");

        JButton[] buttons = {searchButton, undoButton, peekButton, dequeueButton, viewCompletedButton};
        for (JButton b : buttons)
            styleButton(b, buttonColor, textColor, buttonHover, baseFont);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(cardColor);
        for (JButton b : buttons) buttonPanel.add(b);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(buttonPanel, BorderLayout.EAST);
        frame.add(searchPanel, BorderLayout.SOUTH);

        // 🔹 TASK LIST
        taskList.setBackground(listBg);
        taskList.setForeground(listText);
        taskList.setFont(baseFont);
        taskList.setSelectionBackground(new Color(70, 130, 180));
        taskList.setSelectionForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(scrollPane, BorderLayout.CENTER);

        // 🔹 CONTEXT MENU
        JPopupMenu menu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem(" Edit Task");
        JMenuItem deleteItem = new JMenuItem(" Delete Task");
        JMenuItem completeItem = new JMenuItem(" Mark Complete");
        editItem.setFont(baseFont);
        deleteItem.setFont(baseFont);
        completeItem.setFont(baseFont);
        menu.add(editItem);
        menu.add(deleteItem);
        menu.add(completeItem);
        taskList.setComponentPopupMenu(menu);

        // 🧠 LOGIC (same as before)
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String due = dueField.getText().trim();
            String priority = priorityField.getText().trim();

            if (name.isEmpty() || due.isEmpty() || priority.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!taskManager.isValidDate(due)) {
                JOptionPane.showMessageDialog(frame, "Invalid date format! Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!priority.equalsIgnoreCase("High") &&
                    !priority.equalsIgnoreCase("Medium") &&
                    !priority.equalsIgnoreCase("Low")) {
                JOptionPane.showMessageDialog(frame,
                        "Priority must be: High, Medium, or Low.",
                        "Invalid Priority",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String formattedPriority = priority.substring(0, 1).toUpperCase() + priority.substring(1).toLowerCase();
            Task t = new Task(name, due, formattedPriority);
            taskManager.addTask(t);
            listModel.addElement(t);

            nameField.setText("");
            dueField.setText("");
            priorityField.setText("");
        });

        // 🔹 Edit
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

        // 🔹 Delete
        deleteItem.addActionListener(e -> {
            Task selected = taskList.getSelectedValue();
            if (selected == null) return;
            taskManager.deleteTask(selected);
            listModel.removeElement(selected);
        });

        // 🔹 Complete
        completeItem.addActionListener(e -> {
            Task selected = taskList.getSelectedValue();
            if (selected == null) return;
            taskManager.completeTask(selected);
            listModel.removeElement(selected);
            JOptionPane.showMessageDialog(frame, "Task completed and added to queue!");
        });

        // 🔹 Undo
        undoButton.addActionListener(e -> {
            taskManager.undoLastAction();
            listModel.clear();
            for (Task t : taskManager.getTasks()) listModel.addElement(t);
        });

        // 🔹 Peek
        peekButton.addActionListener(e -> {
            Task top = taskManager.peekLastAction();
            JOptionPane.showMessageDialog(frame,
                    top != null ? "Last Action (Top of Stack):\n" + top : "Stack is empty.");
        });

        // 🔹 Dequeue
        dequeueButton.addActionListener(e -> {
            Task t = taskManager.dequeueCompleted();
            JOptionPane.showMessageDialog(frame,
                    t != null ? "Removed oldest completed: " + t : "No completed tasks to dequeue.");
        });

        // 🔹 View completed
        viewCompletedButton.addActionListener(e -> {
            java.util.Queue<Task> completed = taskManager.getCompletedQueue();
            if (completed.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No completed tasks yet!");
            } else {
                StringBuilder sb = new StringBuilder(" Completed Tasks:\n\n");
                for (Task t : completed) sb.append("- ").append(t).append("\n");
                JOptionPane.showMessageDialog(frame, sb.toString());
            }
        });

        // 🔹 Search
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) return;
            Task found = taskManager.searchTask(keyword);
            JOptionPane.showMessageDialog(frame,
                    found != null ? "Found: " + found : "No matching task.");
            if (found != null) taskList.setSelectedValue(found, true);
        });

        frame.setVisible(true);
    }

    private JTextField createStyledField(Font font, Color text, Color bg) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.setForeground(text);
        field.setBackground(bg);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        return field;
    }

    private JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(font);
        return label;
    }

    private void styleButton(JButton button, Color bg, Color fg, Color hover, Font font) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setFont(font);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hover);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bg);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoListApp::new);
    }
}
