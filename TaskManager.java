import java.util.*;
import java.text.SimpleDateFormat;

public class TaskManager {
    private Stack<Task> actionStack;       // for push, pop, peek
    private Queue<Task> completedQueue;    // for enqueue, dequeue
    private List<Task> tasks;              // main list of tasks

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

    // ---- Date validation ----
    public boolean isValidDate(String date) {
        try {
            new SimpleDateFormat("yyyy-MM-dd").parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
