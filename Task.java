public class Task {
    private String name;
    private String dueDate;
    private String priority;

    public Task(String name, String dueDate, String priority) {
        this.name = name;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return name + " | Due: " + dueDate + " | Priority: " + priority;
    }
}
