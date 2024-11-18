import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Client {

    // Database credentials
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Connection connection = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        System.out.println("Please enter the database host:");
        String host = scan.nextLine();
        System.out.println("Please enter the database username:");
        String username = scan.nextLine();
        System.out.println("Please enter the database password:");
        String password = scan.nextLine();
        System.out.println("Please enter the database name:");
        String DBname = scan.nextLine();

        String URL = "jdbc:mysql://" + host + ":3306/" + DBname;


        try {
            // Establish the connection
            connection = DriverManager.getConnection(URL, username, password);
            System.out.println("Connected to the MySQL/MariaDB server successfully.");

            // // Create a statement object for executing SQL queries
            statement = connection.createStatement();

            //Flag for empty Sets
            int emptySet = 0;

            //Function option
            int choice = 0;
            
            do {
                System.out.println("Please choose a function by its number:");
                System.out.println("1. Display all digital displays");
                System.out.println("2. Search digital displays given a scheduler system");
                System.out.println("3. Insert a new digital display");
                System.out.println("4. Delete a digital display");
                System.out.println("5. Update a digital display");
                System.out.println("6. Logout");

                choice = scan.nextInt();


                //Question 1: Find sites by street
                if(choice == 1){
                    PreparedStatement baseState = connection.prepareStatement("SELECT * FROM DigitalDisplay");
                    resultSet = baseState.executeQuery();

                    System.out.println();
                    while (resultSet.next()) {
                        emptySet = 1;
                        System.out.printf("SerialNo: %s, SchedulerSystem: %s, ModelNo: %s%n",
                        resultSet.getString("serialNo"),
                        resultSet.getString("schedulerSystem"),
                        resultSet.getString("modelNo"));
                    }
                    System.out.println();
                    
                    System.out.println("Choose a model number, if none type N: ");
                    scan.nextLine();
                    String modelNo = scan.nextLine();

                    if(modelNo.equals("N")) break;
                    PreparedStatement prepState = connection.prepareStatement("SELECT * FROM Model WHERE modelNo = ?");
                    prepState.setString(1, modelNo);
                    resultSet = prepState.executeQuery();

                    System.out.println();
                    while (resultSet.next()) {
                        emptySet = 1;
                        System.out.printf("ModelNo: %s, width: %.2f, height: %.2f, weight: %.2f, depth: %.2f, screenSize: %.2f%n",
                        resultSet.getString("ModelNo"),
                        resultSet.getDouble("width"),
                        resultSet.getDouble("height"),
                        resultSet.getDouble("weight"),
                        resultSet.getDouble("depth"),
                        resultSet.getDouble("screenSize"));
                    }
                    System.out.println();
                }

                if(choice == 5){
                    PreparedStatement baseState = connection.prepareStatement("SELECT * FROM DigitalDisplay");
                    resultSet = baseState.executeQuery();

                    System.out.println();
                    while (resultSet.next()) {
                        emptySet = 1;
                        System.out.printf("SerialNo: %s, SchedulerSystem: %s, ModelNo: %s%n",
                        resultSet.getString("serialNo"),
                        resultSet.getString("schedulerSystem"),
                        resultSet.getString("modelNo"));
                    }
                    System.out.println();
                    
                    System.out.println("Choose a serial number to update");
                    scan.nextLine();
                    String serialNo = scan.nextLine();

                    System.out.println("Type new scheduler system");
                    String schedSys = scan.nextLine();

                    System.out.println("Type new modelNo");
                    String modelNo = scan.nextLine();


                    PreparedStatement prepState = connection.prepareStatement("UPDATE DigitalDisplay SET SchedulerSystem = ?, ModelNo = ? WHERE serialNo = ?");
                    prepState.setString(1, schedSys);
                    prepState.setString(2, modelNo);
                    prepState.setString(3, serialNo);
                    prepState.executeUpdate();

                    PreparedStatement lastState = connection.prepareStatement("SELECT * FROM DigitalDisplay");
                    resultSet = lastState.executeQuery();

                    System.out.println();
                    while (resultSet.next()) {
                        emptySet = 1;
                        System.out.printf("SerialNo: %s, SchedulerSystem: %s, ModelNo: %s%n",
                        resultSet.getString("serialNo"),
                        resultSet.getString("schedulerSystem"),
                        resultSet.getString("modelNo"));
                    }
                    System.out.println();
                }


                if(emptySet == 0){
                    System.out.println("No Results Found");
                }

            }while(choice != 6);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            // Close resources
            try {
                scan.close();
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
