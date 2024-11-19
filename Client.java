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
        System.out.println("Please enter the port:");
        String port = scan.nextLine();

        String URL = "jdbc:mysql://" + host + ":" + port + "/" + DBname;


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


                //1: Display all digital displays
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

                //2: Find digital displays by scheduler system
                if(choice == 2){
                    System.out.println("Enter the scheduler system, enter N in none:");
                    scan.nextLine();
                    String schedSys = scan.nextLine(); 

                    if(schedSys.equals("N")) break;
                    
                    PreparedStatement stmt = connection.prepareStatement("SELECT * FROM DigitalDisplay " +
                                                                         "WHERE DigitalDisplay.schedulerSystem = ?");
                    stmt.setString(1, schedSys);
                    resultSet = stmt.executeQuery();
                    System.out.println();
                    while (resultSet.next()) {
                        System.out.printf("SerialNo: %s, ModelNo: %s, SchedulerSystem: %s%n",
                                resultSet.getString("serialNo"),
                                resultSet.getString("modelNo"),
                                resultSet.getString("schedulerSystem"));
                    }
                    System.out.println();
                }

                //3: Insert new digital display
                if (choice == 3) {
                    System.out.println("Enter serial number, if none enter N:");
                    String serialNo = scan.nextLine();
                    if(serialNo.equals("N")) break;

                    scan.nextLine();
                    System.out.println("Enter scheduler system:");
                    String schedSys = scan.nextLine();
                    System.out.println("Enter model number:");
                    String modelNo = scan.nextLine();

                    PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Model WHERE modelNo = ?");
                    stmt.setString(1, modelNo);
                    resultSet = stmt.executeQuery();
                    if (!resultSet.next()) {
                        System.out.println("Model does not exist. Enter model details:");
                        System.out.println("Width:");
                        double width = scan.nextDouble();
                        System.out.println("Height:");
                        double height = scan.nextDouble();
                        System.out.println("Weight:");
                        double weight = scan.nextDouble();
                        System.out.println("Depth:");
                        double depth = scan.nextDouble();
                        System.out.println("Screen size:");
                        double screenSize = scan.nextDouble();
                        scan.nextLine(); // Consume newline

                        PreparedStatement insertModelStmt = connection.prepareStatement(
                                "INSERT INTO Model (modelNo, width, height, weight, depth, screenSize) VALUES (?, ?, ?, ?, ?, ?)");
                        insertModelStmt.setString(1, modelNo);
                        insertModelStmt.setDouble(2, width);
                        insertModelStmt.setDouble(3, height);
                        insertModelStmt.setDouble(4, weight);
                        insertModelStmt.setDouble(5, depth);
                        insertModelStmt.setDouble(6, screenSize);
                        insertModelStmt.executeUpdate();
                    }

                    PreparedStatement insertDisplayStmt = connection.prepareStatement(
                            "INSERT INTO DigitalDisplay (serialNo, schedulerSystem, modelNo) VALUES (?, ?, ?)");
                    insertDisplayStmt.setString(1, serialNo);
                    insertDisplayStmt.setString(2, schedSys);
                    insertDisplayStmt.setString(3, modelNo);
                    insertDisplayStmt.executeUpdate();
                    System.out.println("Digital display inserted successfully.");
                }

                //4: delete digital display
                if (choice == 4) {
                    //display all digital displays
                    PreparedStatement baseState = connection.prepareStatement("SELECT * FROM DigitalDisplay");
                    resultSet = baseState.executeQuery();

                    System.out.println("Enter the serial number of the digital display to delete, enter N if none:");
                    scan.nextLine();
                    String serialNo = scan.nextLine();
                    if(serialNo.equals("N")) break;

                    //get modelNo for the chosen digital display
                    PreparedStatement getModel = connection.prepareStatement(
                        "SELECT modelNo FROM DigitalDisplay WHERE serialNo = ?");
                    getModel.setString(1, serialNo);
                    ResultSet modelNoResult = getModel.executeQuery();

                    if (modelNoResult.next()) {
                        String modelNotDelete = modelNoResult.getString("modelNo");

                        //check for more modelNo
                        PreparedStatement checkModel = connection.prepareStatement(
                            "SELECT COUNT(*) FROM DigitalDisplay WHERE modelNo = ?");
                        checkModel.setString(1, modelNotDelete);
                        ResultSet countResult = checkModel.executeQuery();

                        if (countResult.next() && countResult.getInt(1) == 1) {
                            //no other displays use modelNo, so delete this model
                            PreparedStatement deleteModelStmt = connection.prepareStatement(
                                "DELETE FROM model WHERE modelNo = ?");
                            deleteModelStmt.setString(1, modelNotDelete);
                            deleteModelStmt.executeUpdate();
                            System.out.println("Model deleted successfully as no other displays use it.");
                        }

                        //delete the selected digital display
                        PreparedStatement deleteDisplayStmt = connection.prepareStatement(
                            "DELETE FROM DigitalDisplay WHERE serialNo = ?");
                        deleteDisplayStmt.setString(1, serialNo);
                        deleteDisplayStmt.executeUpdate();
                        System.out.println("Digital display deleted successfully.");
                    } 
                    else {
                        System.out.println("No digital display found with the given serial number.");
                    }

                    //display all remaining digital displays
                    PreparedStatement postState = connection.prepareStatement("SELECT * FROM DigitalDisplay");
                    resultSet = postState.executeQuery();
                    System.out.println("Remaining Digital Displays:");
                    while (resultSet.next()) {
                        System.out.println("SerialNo: " + resultSet.getString("serialNo") +
                                        ", SchedulerSystem: " + resultSet.getString("schedulersystem") +
                                        ", ModelNo: " + resultSet.getString("modelNo"));
                    }

                    //display models
                    PreparedStatement postModelsState = connection.prepareStatement("SELECT * FROM model");
                    resultSet = postModelsState.executeQuery();
                    System.out.println("Remaining Models:");
                    while (resultSet.next()) {
                        System.out.println("ModelNo: " + resultSet.getString("modelNo"));
                    }
                }

                //5: Update digital display 
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
