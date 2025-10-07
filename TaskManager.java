import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> taskList;

    public TaskManager() {
        taskList = new ArrayList<>();
    }

    public void addTask(Task task) {
        taskList.add(task);
    }

    public void deleteTask(int index) {
        if (index >= 0 && index < taskList.size()) {
            taskList.remove(index);
        }
    }

    public void editTask(int index, String newName, String newDueDate, String newPriority) {
        if (index >= 0 && index < taskList.size()) {
            taskList.set(index, new Task(newName, newDueDate, newPriority));
        }
    }

    public List<Task> getAllTasks() {
        return taskList;
    }

    // 🔍 Search by name or due date
    public List<Task> searchTasks(String keyword) {
        List<Task> results = new ArrayList<>();
        for (Task task : taskList) {
            if (task.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                    task.getDueDate().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(task);
            }
        }
        return results;
    }
}
