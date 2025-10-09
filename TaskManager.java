import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TaskManager {
    private Stack<Task> actionStack = new Stack<>();
    private Queue<Task> completedQueue = new LinkedList<>();
    private List<Task> tasks = new ArrayList<>();


    public TaskManager() {
        actionStack = new Stack<>();
        completedQueue = new LinkedList<>();
        tasks = new ArrayList<>();
    }

    // ---- Add (push) ----
    public void addTask(Task task) {
        actionStack.push(task);
        tasks.add(task);
    }

    // ---- Edit ----
    public void editTask(Task oldTask, Task newTask) {
        int index = tasks.indexOf(oldTask);
        if (index != -1) {
            tasks.set(index, newTask);
            actionStack.push(newTask);
        }
    }

    // ---- Delete (pop) ----
    public void deleteTask(Task task) {
        tasks.remove(task);
        actionStack.push(task);
    }

    // ---- Complete (enqueue) ----
    public void completeTask(Task task) {
        tasks.remove(task);
        completedQueue.offer(task);
    }

    // ---- Undo last action (pop) ----
    public void undoLastAction() {
        if (!actionStack.isEmpty()) {
            Task last = actionStack.pop();
            if (!tasks.contains(last)) tasks.add(last);
        }
    }

    // ---- Peek ----
    public Task peekLastAction() {
        if (!actionStack.isEmpty()) {
            return actionStack.peek();
        }
        return null;
    }

    // ---- Dequeue ----
    public Task dequeueCompleted() {
        return completedQueue.poll();
    }

    // ---- Search by keyword ----
    public Task searchTask(String keyword) {
        for (Task t : tasks) {
            if (t.getName().equalsIgnoreCase(keyword) ||
                    t.getDueDate().equalsIgnoreCase(keyword))
                return t;
        }
        return null;
    }

    // ---- Get all tasks ----
    public List<Task> getTasks() {
        return tasks;
    }

    // ---- Get completed queue ----
    public Queue<Task> getCompletedQueue() {
        return completedQueue;
    }

    public boolean isValidDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateStr, formatter);

            // ✅ Allow all valid dates (no restriction on past dates)
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
