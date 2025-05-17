import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// this class is responsible for managing the connection with our database
class DBConnection
{
    // storing/initializing URL, USER, PASS as constant variables
    private static final String URL = "jdbc:mysql://localhost:3306/DiarySystem?serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASS = "";
    
    // returning a database connection object so that we can use in our program where we need data from the database
    
    public static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

// this class is responsible for managing passwords, where it holds, updates passwords
class PasswordManager
{
    private String password;
    // class constructor for initializing attributes
    public PasswordManager(String password) { this.password = password; }
    // getter for the password attribute
    public String getPassword() { return password; }
    // setter for setting the password for the user
    public void setPassword(String password) { this.password = password; }
}

// User class will be responsible for managing, holding user's data
class User
{
    // we assign attributes as "final" to prevent accidental modification of values after initializing them
    private final int id;
    private final String username;
    private final PasswordManager passwordManager;

    // class constructor for initializing attributes
    public User(int id, String username, String password)
    {
        this.id = id;
        this.username = username;
        this.passwordManager = new PasswordManager(password);
    }

    // getters and setters for class attributes
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return passwordManager.getPassword(); }
    public void setPassword(String password) { this.passwordManager.setPassword(password); }
}

// Diary class will be responsible for managing user's diaries
class Diary
{
    // defining class attributes
    private final int id;
    private String name;
    private String duration;
    private String address;
    private String date;
    private String time;
    private String details;
    private final int userId;

    // using the class constructor for initializing attributes
    public Diary(int id, String name, String duration, String address, String date, String time, String details, int userId)
    {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.address = address;
        this.date = date;
        this.time = time;
        this.details = details;
        this.userId = userId;
    }

    // getters for class attributes
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDuration() { return duration; }
    public String getAddress() { return address; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getDetails() { return details; }
    public int getUserId() { return userId; }

    // setters for class attributes
    public void setName(String name) { this.name = name; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setAddress(String address) { this.address = address; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setDetails(String details) { this.details = details; }
}

// DatabaseHandler class is responsible for: - performing queries on database to retrieve data
//                                           - handling SQLExceptions and connections
//                                           - performing the CRUD operations:
//                                              - Create
//                                              - Read
//                                              - Update
//                                              - Delete
class DatabaseHandler
{

    public User getUserByUsername(String username) throws SQLException
    {
        String sql = "SELECT * FROM user WHERE username=?";         // sql query to handle user login 
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
        }
        return null;
    }

    public void addUser(String username, String password) throws SQLException
    {
        String sql = "INSERT INTO user (username, password) VALUES (?, ?)";     //sql query handle user registeration
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
        }
    }

    public void updatePassword(String username, String password) throws SQLException {
        String sql = "UPDATE user SET password=? WHERE username=?";         // sql query handle updating password
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, password);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }

  
    public List<Diary> getUserDiaries(int userId) throws SQLException
    {
        List<Diary> list = new ArrayList<>();
        String sql = "SELECT * FROM diary WHERE user_id=?";         // sql query retrive all added diaries
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                list.add(new Diary (
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("duration"),
                        rs.getString("address"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("details"),
                        rs.getInt("user_id")
                ));
            }
        }
        return list;
    }

    public void addDiary(Diary d) throws SQLException
    {
        String sql = "INSERT INTO diary (name, duration, address, date, time, details, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, d.getName());
            stmt.setString(2, d.getDuration());
            stmt.setString(3, d.getAddress());
            stmt.setString(4, d.getDate());
            stmt.setString(5, d.getTime());
            stmt.setString(6, d.getDetails());
            stmt.setInt(7, d.getUserId());
            stmt.executeUpdate();
        }
    }

    public void updateDiary(Diary d) throws SQLException
    {
        String sql = "UPDATE diary SET name=?, duration=?, address=?, date=?, time=?, details=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, d.getName());
            stmt.setString(2, d.getDuration());
            stmt.setString(3, d.getAddress());
            stmt.setString(4, d.getDate());
            stmt.setString(5, d.getTime());
            stmt.setString(6, d.getDetails());
            stmt.setInt(7, d.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteDiary(int id) throws SQLException
    {
        String sql = "DELETE FROM diary WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}

class LoginManager {// the relation between "LoginManager"& "DatabaseHandler" -> aggregation
    private final DatabaseHandler dbHandler = new DatabaseHandler();                         // creating an instance from class DatabaseHandler to do database operation to manage users account :

    public User login(String username, String password) throws Exception                     // 1- login()-> handle user login process
    {
        User user = dbHandler.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password))
            return user;
        else 
            return null;
    }

    public boolean register(String username, String password) throws Exception {           //register()-> handle user registeration process
        if (dbHandler.getUserByUsername(username) != null)           //handle the case if username is already used"registered" by another user
            return false; 
        else{
        dbHandler.addUser(username, password);
        return true;
        }
    }

    public void changePassword(String username, String newPassword) throws Exception {    // changePassword()-> handle user changing password process 
        dbHandler.updatePassword(username, newPassword);
    }
}



class RecordManager { // the relation between "RecordManager"& "DatabaseHandler" -> aggregation                      
    private final DatabaseHandler dbHandler = new DatabaseHandler();                // creating an instance from class DatabaseHandler to do database operations on user diaries:                                                                       
    public List<Diary> viewRecord(int userId) throws Exception {             //      1-viewRecord()-> retrive user diaries from database for logged in user
        return dbHandler.getUserDiaries(userId);                                   
    }                                                                              
                                                                                    
    public void addRecord(Diary d) throws Exception {                                //      2- addRecord()-> add a new diary record 
        dbHandler.addDiary(d);
    }

    public void updateRecord(Diary d) throws Exception {       //      3- updateRecord()-> edit an existing record
        dbHandler.updateDiary(d);            
    }

    public void deleteRecord(int id) throws Exception {                              //      4- deleteRecord()-> delete an existiing record
        dbHandler.deleteDiary(id);
    }
}

class DiaryGUI {
    private final LoginManager loginManager = new LoginManager();               // creating an instance from class LoginManager to be able to use login operations on user account:(register- login -change password)
    private final RecordManager recordManager = new RecordManager();           // creating an instance from class RecordManager to be able to manage user dairies :(add diary-update diary -delete diary)
    private User currentUser;

    // --- entry page ---
    public void showLogin() {
        new LoginFrame();
    }

    // --- LOGIN / REGISTRATION page ---
    class LoginFrame extends JFrame {
        JTextField usernameField = new JTextField(15);          
        JPasswordField passwordField = new JPasswordField(15);

        public LoginFrame() {
            setTitle("Login - Diary System");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(350,200);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridLayout(3,2,5,5));
            panel.add(new JLabel("Username:"));         
            panel.add(usernameField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);

            JButton loginButton = new JButton("Login");         //login button
            JButton regButton = new JButton("Register");       // register button
            panel.add(loginButton);
            panel.add(regButton);

            add(panel);

            loginButton.addActionListener(e -> doLogin());
            regButton.addActionListener(e -> doRegister());

            setVisible(true);
        }

        private boolean validateLoginFields(String username, String password)       // boolean function validate user credintials"username,"password":
        {      // trim()->  ignore accidental spaces                                                     //                 1- username or password-> null
            if(username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {//          2-username or password-> empty
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty.");         //         --->>   show error message
                return false;                                                                                 //                  --return false--
            }
            else
                return true;
        }

        private void doLogin() {
            String username = usernameField.getText().trim();               // getText()-> retrieve username from usernameField
            String password = new String(passwordField.getPassword());
            if (!validateLoginFields(username, password)) return;          // calling validateLoginFields to validate user credintials
            try {
                User u = loginManager.login(username, password);
                if (u != null) {
                    currentUser = u;
                    JOptionPane.showMessageDialog(this, "Welcome, " + username + "!");
                    dispose();          // --> close login frame
                    new MainMenuFrame();
                } else {
                    JOptionPane.showMessageDialog(this, "Login failed! Wrong credentials.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }

        private void doRegister() {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (!validateLoginFields(username, password)) return;
            try {
                if (loginManager.register(username, password)) {
                    JOptionPane.showMessageDialog(this, "Registration successful! Login now.");
                } else {
                    JOptionPane.showMessageDialog(this, "Username already exists.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // --- MAIN MENU page ---
    class MainMenuFrame extends JFrame {
        private final JTextField tfTaskName = new JTextField();                               // text fields
        private final JTextField tfAddress = new JTextField();                               //
        private final JTextField tfDuration = new JTextField();                             //      
        private final JTextField tfDate = new JTextField();                                //
        private final JTextField tfTime = new JTextField();                               //  
        private final JTextArea taDetails = new JTextArea(3, 20);           //

        private final JTable table;
        private final DefaultTableModel tableModel;

        private final JButton btnAdd = new JButton("Add");                              //BUTTONS
        private final JButton btnEdit = new JButton("Edit");                           //
        private final JButton btnDelete = new JButton("Delete");                      //    
        private final JButton btnLogout = new JButton("Logout");                     //
        private final JButton btnPwd = new JButton("Change Password");              //


        private void colorButtons() {
            btnAdd.setOpaque(true);
            btnAdd.setBackground(new Color(76, 175, 80));                             // BUTTON COLOR
            btnAdd.setForeground(Color.WHITE);
            btnAdd.setFocusPainted(false);
            btnAdd.setBorderPainted(false);

            btnEdit.setOpaque(true);
            btnEdit.setBackground(new Color(33, 150, 243));                          // BUTTON COLOR
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setFocusPainted(false);
            btnEdit.setBorderPainted(false);

            btnDelete.setOpaque(true);
            btnDelete.setBackground(new Color(244, 67, 54));                         // BUTTON COLOR
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setFocusPainted(false);
            btnDelete.setBorderPainted(false);

            btnPwd.setOpaque(true);
            btnPwd.setBackground(new Color(255, 193, 7));                            // BUTTON COLOR
            btnPwd.setForeground(Color.BLACK);
            btnPwd.setFocusPainted(false);
            btnPwd.setBorderPainted(false);

            btnLogout.setOpaque(true);
            btnLogout.setBackground(new Color(158, 158, 158));                       // BUTTON COLOR
            btnLogout.setForeground(Color.WHITE);
            btnLogout.setFocusPainted(false);
            btnLogout.setBorderPainted(false);
        }


        private List<Diary> loadedDiaries; 

        public MainMenuFrame() {
            setTitle("Diary Management System - User: " + currentUser.getUsername());
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(900, 600);
            setLocationRelativeTo(null);        //--> center page on screen

            JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
            formPanel.add(new JLabel("Task Name:")); formPanel.add(tfTaskName);
            formPanel.add(new JLabel("Address:"));   formPanel.add(tfAddress);
            formPanel.add(new JLabel("Duration:"));  formPanel.add(tfDuration);
            formPanel.add(new JLabel("Date (YYYY-MM-DD):")); formPanel.add(tfDate);
            formPanel.add(new JLabel("Time (HH:MM:SS):"));  formPanel.add(tfTime);
            formPanel.add(new JLabel("Details:"));   formPanel.add(new JScrollPane(taDetails));

            colorButtons();

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(btnAdd);
            buttonPanel.add(btnEdit);
            buttonPanel.add(btnDelete);
            buttonPanel.add(btnPwd);
            buttonPanel.add(btnLogout);

            // table display added diaries
            String[] columns = {"Task Name", "Address", "Duration", "Date", "Time", "Details"};
            tableModel = new DefaultTableModel(columns, 0) { 
                public boolean isCellEditable(int r, int c){            //isCellEditable(rows, columns)--> prevent editing directly from table[]
                    return false;                                       // u must use "edit button " to edit 
                } 
            };
            table = new JTable(tableModel);         // creating a new table from tableModel
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);        // selection mode is single to be sure that user cannot select more than 1 row
                                                                                // which help in edit and delete operations
            JScrollPane tableScroll = new JScrollPane(table);   //adding scrollbars to the table when needed allowing users to navigate through a large number of added diaries

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(formPanel, BorderLayout.CENTER);
            topPanel.add(buttonPanel, BorderLayout.SOUTH);

            setLayout(new BorderLayout(10,10));
            add(topPanel, BorderLayout.NORTH);
            add(tableScroll, BorderLayout.CENTER);

            // Button Actions
            btnAdd.addActionListener(e -> addEntry());
            btnEdit.addActionListener(e -> editEntry());
            btnDelete.addActionListener(e -> deleteEntry());
            btnLogout.addActionListener(e -> {
                dispose();
                currentUser = null;
                showLogin();
            });
            btnPwd.addActionListener(e -> {
                String newPwd = JOptionPane.showInputDialog(this, "Enter new password:");
                if (newPwd != null && !newPwd.trim().isEmpty()) {
                    try {
                        loginManager.changePassword(currentUser.getUsername(), newPwd);
                        JOptionPane.showMessageDialog(this, "Password changed.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage());
                    }
                }
            });

            // Table select: load into fields
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int row = table.getSelectedRow();
                    if (row >= 0 && loadedDiaries != null && row < loadedDiaries.size()) {
                        Diary d = loadedDiaries.get(row);
                        tfTaskName.setText(d.getName());
                        tfAddress.setText(d.getAddress());
                        tfDuration.setText(d.getDuration());
                        tfDate.setText(d.getDate());
                        tfTime.setText(d.getTime());
                        taDetails.setText(d.getDetails());
                    }
                }
            });

            loadEntries();
            setVisible(true);
        }

        private void loadEntries() { //   loadEntries()--> retrive added diaries from the database and displays them in table in GUI.
            try {
                loadedDiaries = recordManager.viewRecord(currentUser.getId());
                tableModel.setRowCount(0);
                for (Diary d : loadedDiaries) {
                    tableModel.addRow(new Object[]{
                            d.getName(), d.getAddress(), d.getDuration(),
                            d.getDate(), d.getTime(), d.getDetails()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load entries:\n" + ex.getMessage());
            }
        }

        private void clearForm() {      // clearForm()--> it clear text fields ,help me in operations :- adding new record
            tfTaskName.setText("");   //                                                             - editing existing record                                                             
            tfAddress.setText("");    //                                                             - deleting existing record    
            tfDuration.setText("");   //  
            tfDate.setText("");
            tfTime.setText("");
            taDetails.setText("");
        }

        private boolean validateEntryFields() {                 // validateEntryFields()-> verify that required fields are filled in.
            if (tfTaskName.getText().trim().isEmpty() || tfDate.getText().trim().isEmpty() || tfTime.getText().trim().isEmpty()) { //checks if any of the required fields (task name, date, or time) are empty
                JOptionPane.showMessageDialog(this, "Task Name, Date, and Time are required.");                            //if empty dsiplay mess.
                return false;                                                                                                      
            }
            return true;
        }

        private void addEntry() {
            if(!validateEntryFields()) return;
            try {
                Diary d = new Diary(0, // id is 0 because it is defined by database AUTO INCREMENT
                        tfTaskName.getText(), tfDuration.getText(), tfAddress.getText(),
                        tfDate.getText(), tfTime.getText(), taDetails.getText(), currentUser.getId());
                recordManager.addRecord(d);
                JOptionPane.showMessageDialog(this, "Entry added!");
                clearForm();
                loadEntries();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to add entry:\n" + ex.getMessage());
            }
        }

        private void editEntry() {
            int row = table.getSelectedRow();       //getSelectedRow()-->>Gets the index of the currently selected row in the table.
            if (row < 0 || loadedDiaries == null || row >= loadedDiaries.size()) {      // If row is negative--> there is no selection.  
                JOptionPane.showMessageDialog(this, "Select a row to edit.");   //If loadedDiaries is null or row is out of bounds.there is problem in  data
                return;                                                                 
            }
            if(!validateEntryFields()) return;
            try {
                Diary d = loadedDiaries.get(row);
                d.setName(tfTaskName.getText());
                d.setAddress(tfAddress.getText());
                d.setDuration(tfDuration.getText());
                d.setDate(tfDate.getText());
                d.setTime(tfTime.getText());
                d.setDetails(taDetails.getText());
                recordManager.updateRecord(d);
                JOptionPane.showMessageDialog(this, "Entry updated!");
                clearForm();
                loadEntries();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to edit entry:\n" + ex.getMessage());
            }
        }

        private void deleteEntry() {
            int row = table.getSelectedRow();
            if (row < 0 || loadedDiaries == null || row >= loadedDiaries.size()) {
                JOptionPane.showMessageDialog(this, "Select a row to delete.");
                return;
            }
            try {
                Diary d = loadedDiaries.get(row);
                recordManager.deleteRecord(d.getId());
                JOptionPane.showMessageDialog(this, "Entry deleted!");
                clearForm();
                loadEntries();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to delete entry:\n" + ex.getMessage());
            }
        }
    }


}



public class Main
{
    public static void main(String[] args) {
        // Set Look and Feel (optional)
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch(Exception ignored){}
        SwingUtilities.invokeLater(() -> new DiaryGUI().showLogin());
    }
}