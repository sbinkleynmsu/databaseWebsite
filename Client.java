import java.io.IOException;
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
                    if(args.length != 2){
                        throw new IOException("INCORRECT USAGE ARGUMENTS SHOULD BE: \"1 <street name> \" ");
                    }else{
                        String streetName = args[1].replace("'", "").replace("\"", "");
                        PreparedStatement prepState = connection.prepareStatement("SELECT * FROM Site WHERE address LIKE ?");
                        prepState.setString(1, "%" + streetName + "%");
                        resultSet = prepState.executeQuery();

                        while (resultSet.next()) {
                            emptySet = 1;
                            System.out.printf("Site Code: %d, Type: %s, Address: %s, Phone: %s%n",
                            resultSet.getInt("siteCode"),
                            resultSet.getString("type"),
                            resultSet.getString("address"),
                            resultSet.getString("phone"));
                        }
                    }
                }

                //Question 2: Find digital displays by scheduler system
                if(choice == 2){
                    if(args.length != 2){
                        throw new IOException("INCORRECT USAGE ARGUMENTS SHOULD BE: \"2 <scheduler system> \" ");
                    }else{
                        String schedSys = args[1].replace("'", "");
                        PreparedStatement prepState = connection.prepareStatement("SELECT DigitalDisplay.serialNo, DigitalDisplay.modelNo, Model.screenSize " +
                                                                    "FROM DigitalDisplay " +
                                                                                    "JOIN Model ON DigitalDisplay.modelNo = Model.modelNo " +
                                                                                    "WHERE DigitalDisplay.schedulerSystem = ?");
                        prepState.setString(1, schedSys);
                        resultSet = prepState.executeQuery();

                        while (resultSet.next()) {
                            emptySet = 1;
                            System.out.printf("Serial No: %s, Model No: %s, Screen Size: %.2f%n",
                            resultSet.getString("serialNo"),
                            resultSet.getString("modelNo"),
                            resultSet.getDouble("screenSize"));
                        }
                    }
                }

                //Question 3: List distinct salesmen and their count
                if(choice == 3){
                    String selectQuery = "SELECT name, COUNT(*) as cnt FROM Salesman GROUP BY name ORDER BY name ASC";
                    resultSet = statement.executeQuery(selectQuery);

                    while (resultSet.next()) {
                        emptySet = 1;
                        System.out.printf("Name: %s, Count: %d%n",
                        resultSet.getString("name"),
                        resultSet.getInt("cnt"));
                    }
                }

                if(choice == 4){
                    if(args.length != 2){
                        throw new IOException("INCORRECT USAGE ARGUMENTS SHOULD BE: \"4 <phone no> \" ");
                    }else{
                        String phoneNo = args[1].replace("'", "");
                        PreparedStatement prepState = connection.prepareStatement("SELECT * FROM Client WHERE phone = ?");
                        prepState.setString(1, phoneNo);
                        resultSet = prepState.executeQuery();

                        while (resultSet.next()) {
                            emptySet = 1;
                            System.out.printf("Client ID: %d, Name: %s, Phone: %s, Address: %s%n",
                            resultSet.getInt("clientId"),
                            resultSet.getString("name"),
                            resultSet.getString("phone"),
                            resultSet.getString("address"));
                        }
                    }
                }

                if(choice == 5){
                    String selectQuery = "With TempTable(AdId, THours) AS (SELECT empId, SUM(hours) FROM AdmWorkHours GROUP BY empId) SELECT " +
                                        "Administrator.empId, Administrator.name, Temptable.THours FROM Administrator, TempTable WHERE Administrator.empId = TempTable.AdId ORDER BY TempTable.THours ASC";
                    resultSet = statement.executeQuery(selectQuery);

                    while (resultSet.next()) {
                        emptySet = 1;
                        int id = resultSet.getInt("empId");
                        String name = resultSet.getString("name");
                        double hours = resultSet.getDouble("Thours");

                        System.out.println("ID: " + id + ", Name: " + name +
                                        ", Total Hours: " + hours);
                    }
                }


                if(emptySet == 0){
                    System.out.println("No Results Found");
                }
            }while(choice != 6);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e){
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
