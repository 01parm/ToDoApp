import javax.swing.*;   // Imports the Swing library used to create GUI components like buttons, labels, panels, etc.
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;  // Helps format and parse dates in a readable form.
import java.text.ParseException;  // Used to handle errors that occur while parsing dates.
import java.util.*;   // Imports utility classes like ArrayList, Date, Comparator, etc.


public class SimpleToDoList extends JFrame {

    // GUI Components
    DefaultListModel<Task> model = new DefaultListModel<>(); // create objects that helps to manage a list of item. it is generic class by swing , to store data and model is name of variable
    JList<Task> taskList = new JList<>(model); //creates a graphical list component (JList) to display a list of Task. It creates a list component named taskList, and links it to a data model (model) that stores all the tasks.
    JLabel statusLabel = new JLabel("Ready"); //t creates a small text area at the bottom of the window (called the status bar), that initially shows the word "Ready".

    // Data
    ArrayList<Task> tasks = new ArrayList<>();   //ArrayList is a class in Java that stores a list of items.It's part of the Java Collections Framework.
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");// A Java class used to format and parse dates.

    public SimpleToDoList() {
        super("Simple To-Do List");  //used to call a constructor or method from the parent class

        // Buttons
        JButton addBtn = new JButton("Add");  //Jbutton is swing class that represent button in class.
        JButton editBtn = new JButton("Edit");
        JButton removeBtn = new JButton("Remove");
        JButton doneBtn = new JButton("Mark Done");
        JButton sortByDateBtn = new JButton("Sort by Deadline");
        JButton sortByPriorityBtn = new JButton("Sort by Priority");
        JButton exitBtn = new JButton("Exit");

        // Set button colors
        addBtn.setBackground(Color.GREEN); // setbackground belong to swing and color belong to java class 
        addBtn.setForeground(Color.WHITE);
        editBtn.setBackground(Color.CYAN);
        editBtn.setForeground(Color.BLACK);
        removeBtn.setBackground(Color.RED);
        removeBtn.setForeground(Color.WHITE);
        doneBtn.setBackground(Color.YELLOW);
        doneBtn.setForeground(Color.BLACK);
        sortByDateBtn.setBackground(Color.ORANGE);
        sortByDateBtn.setForeground(Color.WHITE);
        sortByPriorityBtn.setBackground(Color.PINK);
        sortByPriorityBtn.setForeground(Color.BLACK);
        exitBtn.setBackground(Color.DARK_GRAY);
        exitBtn.setForeground(Color.WHITE);

        // Button panel with single column layout
        JPanel buttonPanel = new JPanel(); // creates a panel (a container) that will hold and organize other GUI components. jpanel is swing class used to organise and group components like button,label etc.
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Vertical arrangement
        // setLayout - This is a method used to change how components are arranged inside a panel.Java GUI layouts decide the position, alignment, and spacing of component
         // boxlayout-creating a BoxLayout manager.This manager arranges components in a single row or column ."Y" axis → Vertical direction


        // Adding buttons with spacing ,used to add components (like buttons) into a container (like buttonPanel).
        buttonPanel.add(addBtn);
        buttonPanel.add(Box.createVerticalStrut(10)); // Box is a utility class in Java’s Swing library. createVerticalStrut() creates a non-visible, invisible space of the specified height.
        buttonPanel.add(editBtn);                                        // to create horizontal gap - Box.createHorizontalStrut(width)
        buttonPanel.add(Box.createVerticalStrut(10)); // 10px space
        buttonPanel.add(removeBtn);
        buttonPanel.add(Box.createVerticalStrut(10)); // 10px space
        buttonPanel.add(doneBtn);
        buttonPanel.add(Box.createVerticalStrut(10)); // 10px space
        buttonPanel.add(sortByDateBtn);
        buttonPanel.add(Box.createVerticalStrut(10)); // 10px space
        buttonPanel.add(sortByPriorityBtn);
        buttonPanel.add(Box.createVerticalStrut(10)); // 10px space
        buttonPanel.add(exitBtn);

        // Task list renderer
        taskList.setCellRenderer(new TaskRenderer());  /*  TaskRenderer class is a custom class that you've defined to determine
                                                        how each task in the list should look. 
                                                       tells Java how to render each item in the list, like changing colors
                                                        based on task priority or applying strike-through for completed tasks.*/

        // Status bar
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));

        // Add to frame
        add(new JScrollPane(taskList), BorderLayout.CENTER);    // A JList typically shows a large number of items, and when the list is too long, you can't see all the items at once. To fix this, we wrap it in a JScrollPane which adds scrollbars (vertical and/or horizontal) when needed.
        add(buttonPanel, BorderLayout.WEST); // Buttons will be on the left side
        add(statusLabel, BorderLayout.SOUTH);

        // Button actions
        addBtn.addActionListener(e -> showTaskDialog(null));  //When the button is clicked, execute showTaskDialog(null), which opens the dialog to add a task.
        editBtn.addActionListener(e -> {
            int i = taskList.getSelectedIndex();
            if (i != -1) showTaskDialog(tasks.get(i));   //adding an action listener to the editBtn button. 
                                                          // When the button is clicked, it checks if a task is selected in the list (taskList). 
                                                       //f a task is selected, it opens the task in the edit dialog so that the user can modify the task's details.
        });

        removeBtn.addActionListener(e -> {     //when remove button is click
            int i = taskList.getSelectedIndex();
            if (i != -1) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this task?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    tasks.remove(i);
                    refreshList();
                }
            }
        });

        doneBtn.addActionListener(e -> {    // complete the task
            int i = taskList.getSelectedIndex();
            if (i != -1) {
                Task task = tasks.get(i);
                task.completed = true;
                // Move completed task to the end of the list
                tasks.remove(i);
                tasks.add(task);
                refreshList();
            }
        });

        sortByDateBtn.addActionListener(e -> {   // sort the list by date 
            tasks.sort(Comparator.comparing(t -> t.deadline));
            refreshList();
        });
  
        sortByPriorityBtn.addActionListener(e -> {    // sort by priority of task
            tasks.sort(Comparator.comparingInt(Task::getPriorityValue));
            refreshList();
        });

        exitBtn.addActionListener(e -> {    // exit from aap
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Do you want to save and exit?",
                    "Exit Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                saveTasks();
                System.exit(0);
            }
        });

        // Confirm before closing window   
        addWindowListener(new WindowAdapter() {   //WindowListener interface defines several methods (like windowOpened(), windowClosing(), windowClosed(), etc.) to respond to different window events.
            public void windowClosing(WindowEvent e) {
                exitBtn.doClick();
            }
        });

        // Load tasks from file
        loadTasks();

        // Final frame settings
        setSize(750, 450);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Add or edit a task with a dialog box.
     */
    void showTaskDialog(Task editTask) {
        JTextField nameField = new JTextField(editTask != null ? editTask.name : "");
        String[] priorities = {"High", "Medium", "Low"};
        JComboBox<String> priorityBox = new JComboBox<>(priorities);
        JTextField deadlineField = new JTextField(editTask != null ?
                dateFormat.format(editTask.deadline) : "dd-MM-yyyy");

        if (editTask != null) {
            priorityBox.setSelectedItem(editTask.priority);
        }

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Task:")); panel.add(nameField);
        panel.add(new JLabel("Priority:")); panel.add(priorityBox);
        panel.add(new JLabel("Deadline:")); panel.add(deadlineField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                editTask == null ? "Add Task" : "Edit Task", JOptionPane.OK_CANCEL_OPTION);

/*This code block is checking whether the user clicked "OK" in a dialog box, and if so, 
it retrieves the values entered in the dialog (name, priority, and deadline), processes them, and stores them in variables.
 The try-catch block is used to handle any potential errors (like incorrect date formats). */
        if (result == JOptionPane.OK_OPTION) { 
            try {
                String name = nameField.getText().trim();
                String priority = (String) priorityBox.getSelectedItem();
                String deadlineText = deadlineField.getText().trim();
                
                // Validate deadline format
                if (!isValidDate(deadlineText)) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Please use dd-MM-yyyy.");
                    return;
                }

                Date deadline = dateFormat.parse(deadlineText);  // convert string to date
                Date today = new Date();
                if (deadline.before(today)) {
                    JOptionPane.showMessageDialog(this, "Deadline has already passed. Please enter a future date.");
                    return;
                }

                // ✅ Duplicate check 
                if (editTask == null) {
                    for (Task t : tasks) {
                        if (t.name.equalsIgnoreCase(name)) {
                            JOptionPane.showMessageDialog(this,
                                    "Task with this name already exists!");
                            return;
                        }
                    }
                    tasks.add(new Task(name, priority, false, deadline));
                } else {
                    editTask.name = name;
                    editTask.priority = priority;
                    editTask.deadline = deadline;
                }

                // Sort tasks so completed tasks go to the end after editing or adding
                tasks.sort(Comparator.comparing((Task t) -> t.completed).thenComparing(t -> t.deadline));
                refreshList();
                saveTasks();
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Error parsing the date.");
            }
        }
    }

    /**
     * Validates the date format (dd-MM-yyyy).
     */
    private boolean isValidDate(String date) {
        try {
            dateFormat.setLenient(false); // Disable leniency , dont allowed incorrect date format
            dateFormat.parse(date); // Try to parse the date
            return true;
        } catch (ParseException e) {
            return false; // Invalid date format
        }
    }

    /**
     * Refresh list and status bar.
     */
    void refreshList() {
        model.clear();
        for (Task t : tasks) model.addElement(t);

        long completed = tasks.stream().filter(t -> t.completed).count();
        statusLabel.setText("Total Tasks: " + tasks.size() + " | Completed: " + completed); // represent total task and completed task
    }

    /**
     * Save tasks to local file.
     */
    void saveTasks() {
        System.out.println(" saveTasks() called");
        System.out.println("Saving to: " + new File("tasks.txt").getAbsolutePath());

        try (BufferedWriter w = new BufferedWriter(new FileWriter("tasks.txt"))) {
            for (Task t : tasks) {
                System.out.println("Saving task: " + t.name); 
                w.write(t.name + "|" + t.priority + "|" + t.completed + "|" + dateFormat.format(t.deadline));
                w.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving tasks.");
        }
    }
    

    /**
     * Load tasks from local file.create folder if not exist
     */
    void loadTasks() {
        File file = new File("D:\\mca\\sem2\\java\\practice_questions\\tasks.txt");
        if (!file.exists()) return;

        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    tasks.add(new Task(parts[0], parts[1],
                            Boolean.parseBoolean(parts[2]),
                            dateFormat.parse(parts[3])));
                }
            }
            refreshList();
        } catch (IOException | ParseException e) {
            JOptionPane.showMessageDialog(this, "Error loading tasks.");
        }
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleToDoList::new);
    }

    /**
     * Task model class
     */
    static class Task { //static: Makes the class nested and accessible without an instance of the outer class (SimpleToDoList).
        String name;
        String priority;
        boolean completed;
        Date deadline;

        public Task(String name, String priority, boolean completed, Date deadline) {
            this.name = name;
            this.priority = priority;
            this.completed = completed;
            this.deadline = deadline;
        }

        int getPriorityValue() {
            return switch (priority) {
                case "High" -> 1;
                case "Medium" -> 2;
                default -> 3;
            };
        }

        public String toString() {
            return (completed ? "[✔] " : "[ ] ") + name + " | " + priority + " | " +
                    new SimpleDateFormat("dd-MM-yyyy").format(deadline);
        }
    }

    /**
     * Task list renderer class
     */
    static class TaskRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            // Get the default cell renderer component
            Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Task t) {
                // If the task is not completed, apply color and bold based on priority
                if (!t.completed) {
                    // Bold and apply color based on priority
                    comp.setFont(comp.getFont().deriveFont(Font.BOLD, 16)); // Bold and larger font size

                    // Apply color based on priority
                    switch (t.priority) {
                        case "High" -> comp.setForeground(Color.RED);  // Red for high priority
                        case "Medium" -> comp.setForeground(Color.ORANGE);  // Orange for medium priority
                        case "Low" -> comp.setForeground(new Color(0, 128, 0));  // Green for low priority
                        default -> comp.setForeground(Color.BLACK);  // Default to black
                    }
                } else {
                    // For completed tasks, set the font as normal (no bold) and strike-through text
                    comp.setFont(comp.getFont().deriveFont(Font.PLAIN));  // Normal font
                    ((JLabel) comp).setText("<html><strike>" + t.name + "</strike></html>");  // Strike-through text
                    comp.setForeground(Color.BLACK);  // Completed tasks in black
                }
            }

            return comp;  // Return the customized component
        }
    }
}     