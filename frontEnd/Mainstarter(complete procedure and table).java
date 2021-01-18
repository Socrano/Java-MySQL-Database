
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import java.sql.Connection;

public class Mainstarter {

  public static void main(String[] args) {

    // Useful constants throughout the code
    Connection con = null;
    ArrayList<String> tableList = new ArrayList<String>(
        Arrays.asList("airline", "airport", "country", "plane", "planecompany", "route"));

    /*
     * when mainSwitch is true, the System continues running otherwise it will close
     * connection pending completion!!!
     */

    boolean mainSwitch = true;

    while (mainSwitch) {
      try {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Scanner input = new Scanner(System.in);
        System.out.println("Please enter a username.");
        String username = input.nextLine();
        System.out.println("Please enter a password.");
        String password = input.nextLine();

        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb"
            + "?characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            username, password);

        System.out.println("Connection: " + con);
        System.out.println("Welcome to the Flight Database System!");
        System.out.println();
        System.out.println(
            "Please use the following keys to view, modify or search within the database.");
        System.out.println("To perform task, simply type the key and follow the instruction.");
        System.out.println();
        System.out.println("Do you want to modify or search the table?");
        System.out.println("Type 'modify' for making changes to the database");
        System.out.println("Type 'search' for viewing data");
        System.out.println("Type 'exit' for exiting the database.");

        String key1 = input.nextLine();

        switch (key1) {
        case "modify":
          // the beginning of the modify data
          System.out.println("Do you want to add/delete/change the data?");
          String key2 = input.nextLine();

          switch (key2) {
          // addition of data
          case "add":
            // temp switch, loop when data is false (true)
            boolean addswitch = true;
            while (addswitch) {
              System.out.println("Which table to you want to add data to?");
              String addTableName = input.nextLine();
              if (tableList.contains(addTableName)) {
                System.out
                    .println("Please enter the tuple you want to add. Separated by ',' and space"
                        + "in the following order.");
                System.out.println(new Mainstarter().returnColumnName(addTableName));
                String addTableData = input.nextLine();
                String addStatement = "INSERT INTO " + addTableName + " ("
                    + new Mainstarter().returnColumnName(addTableName) + ") " + "VALUES(\""
                    + addTableData + "\")";

                Statement pstmtadd = con.createStatement();
                int rs1 = pstmtadd.executeUpdate(addStatement);

                System.out.println("The data has been successfully added!");
                if (rs1 == 1 || rs1 == 0) {
                  System.out.println(rs1 + " row is affected.");
                }
                else {
                  System.out.println(rs1 + " rows are affected.");
                }

                addswitch = false;
                break;
              }
              else {
                System.out.println("Invalid table name. Please Try again.");
                continue;
              }
            }
            break;

          // deletion of data
          case "delete":
            boolean deleteswitch = true;
            while (deleteswitch) {
              System.out.println("Which table do you want to delete data from?");
              String deleteTable = input.nextLine();
              if (tableList.contains(deleteTable)) {
                System.out
                    .println("Are there any keywords restrictions to limit the scope of deletion?");
                System.out.println(
                    "Please type this in 'TableColumnName = xxx' format, separated by 'OR' or 'AND'.");
                System.out.println("Type 'no' if you want to delete everything in this table.");
                String requirement = input.nextLine();
                System.out.println("Any other restrictions? Please type in SQL query format.");
                String otherreq = input.nextLine();
                String deleteStatement;

                if (requirement.equals("no")) {
                  deleteStatement = "DELETE FROM " + deleteTable;
                }
                else {
                  deleteStatement = "DELETE FROM " + deleteTable + " WHERE " + requirement + " "
                      + otherreq;
                }

                Statement deletestmt = con.createStatement();
                int resultnum2 = deletestmt.executeUpdate(deleteStatement);
                System.out.println("The data has been successfully deleted!");
                if (resultnum2 == 1 || resultnum2 == 0) {
                  System.out.println(resultnum2 + " row is affected.");
                }
                else {
                  System.out.println(resultnum2 + " rows are affected.");
                }
                deleteswitch = false;
              }
              else {
                System.out.println("Invalid table name. Please Try again.");
                continue;
              }
            }
            break;

          case "change" :
            boolean changeSwitch = true;
            while (changeSwitch) {
              System.out.println("Which table do you want to edit data from?");
              String tableName = input.nextLine();
              if(tableList.contains(tableName)) {
                System.out.println("Are there any limitation for the data you want to edit?");
                System.out.println("Please enter in 'ColumnName = 'xxx'' form, separated by 'AND' or 'OR'");
                String editLimit = input.nextLine();
                System.out.println("What are you going to change?");
                System.out.println(new Mainstarter().returnColumnName(tableName));
                System.out.println("Please enter in 'ColumnName = 'xxx'' form, separated by comma.");
                String editTo = input.nextLine();
                
                String finalInput = "UPDATE " + tableName + " SET " + editTo + " WHERE " + editLimit;
                Statement editstmt = con.createStatement();
                int editnum3 = editstmt.executeUpdate(finalInput);
                System.out.println("The data has been successfully edited!");
                if (editnum3 == 1 || editnum3 == 0) {
                  System.out.println(editnum3 + " row is affected.");
                }
                else {
                  System.out.println(editnum3 + " rows are affected.");
                }
                changeSwitch = false;
                
              } else {
                System.out.println("Invalid table name. Please Try again.");
                continue;
              }
              
            }
          }
        

        
        case "search":
          //begin search
          System.out.println("Do you want to require procedure or just table?");
          ArrayList<String> procedureChoice = new ArrayList<String>(Arrays.asList("searchAirportRoute",
              "searchCountryAirport", "searchAirlinePlane", "searchAirlineRoute",
              "searchCompanyPlane", "searchCountryAirline", "searchDeparture_DestinationRoute"));
          String key3 = input.nextLine();
          switch (key3) {
          case "procedure":
            boolean procudureSwitch = true;
            while (procudureSwitch) {
              System.out.println("Please choose a procedure you want from the following list\n"
                  + "Note: the name of procedure (searchAB) means search B which belongs to A");
              System.out.println(procedureChoice);
              System.out.println("And the input for each procedure should be:\n"
                  + "searchAirportRoute(airline_name), " + "searchCountryAirport(country_name), \n"
                      + "searchAirlinePlane(airline_name), " + "searchAirlineRoute(airline_name), \n"
                          + "searchCompanyPlane(company_name), " + "searchCountryAirline(country_name), \n"
                              + "searchDeparture_DestinationRoute(departure_airport_name, "
                              + "destination_airport_name)");
              String procedure = input.nextLine();
              System.out.println("Please give the procedure the input it needs"
                  + "\nNote: It is better to require table before require procedure"
                  + " to make sure your input really exist"
                  + "\nAnd please cite your arguments with '.");
              String given = input.nextLine();
              String procedureStmt = "CALL " + procedure + "(" + given + ");";
              Statement pstmtadd = con.createStatement();
              ResultSet rs2 = pstmtadd.executeQuery(procedureStmt);
              ArrayList<String> result = new ArrayList<String>();
              ResultSetMetaData rs2md = rs2.getMetaData();
              int columnsNumber = rs2md.getColumnCount();
              String columnName[] = new String[columnsNumber];
              
              while (rs2.next()) {
                for(int i = 1; i < columnsNumber; i++) {
                  columnName[i-1] = rs2md.getColumnLabel(i);
                    System.out.print(columnName[i-1] + ": " + rs2.getString(i) + " ");
                System.out.println();
                System.out.println("This object finish;\n");
            } 
              }
            }
          case "table":
            boolean tableSwitch = true;
            while (tableSwitch) {
              System.out.println("Please choose a table from the following list:");
              System.out.println(tableList);
              String tableName = input.nextLine();
              String procedureStmt = "SELECT * FROM " + tableName;
              Statement pstmtadd = con.createStatement();
              ResultSet rs3 = pstmtadd.executeQuery(procedureStmt);
              ArrayList<String> result = new ArrayList<String>();
              ResultSetMetaData rs3md = rs3.getMetaData();
              int columnsNumber = rs3md.getColumnCount();
              String columnName[] = new String[columnsNumber];

              while (rs3.next()) {
                for(int i = 1; i < columnsNumber; i++) {
                  columnName[i-1] = rs3md.getColumnLabel(i);
                    System.out.print(columnName[i-1] + ": " + rs3.getString(i) + " ");
                System.out.println();
            } 
              }
            }
          }
        }
        return;
      
      }
      catch (Exception exp) {
        System.out.println("SQLException: " + exp.getMessage());
      }
    }

  }

  String returnColumnName(String giventablename) {
    if (giventablename.equalsIgnoreCase("airline")) {
      return "ID, airline_name, alias, IATA, icao, callsign, country, Airlinecol, Active";
    }
    else if (giventablename.equalsIgnoreCase("airport")) {
      return "id, airport_name, city, country, IATA, ICAO, LATITUDE, LONGITUDE, attitude, Timezone";
    }
    else if (giventablename.equalsIgnoreCase("country")) {
      return "country_name";
    }
    else if (giventablename.equalsIgnoreCase("plane")) {
      return "plane_name, IATA, ICAO, AirlineID, Plane_Company";
    }
    else if (giventablename.equalsIgnoreCase("planecompany")) {
      return "id, CompanyName, modelNumber";
    }
    else if (giventablename.equalsIgnoreCase("route")) {
      return "route_id, airline_id, departure_airport_id, destination_airport_id, "
          + "codeshare, stopnum, equipment";
    }
    else {
      return "Invalid table name! Please try again";
    }
  }
}

class Modify {
  public void addTuple(String addTableName, String addTableData, Connection con) {
    String addStatement = "INSERT INTO" + addTableName
        + new Mainstarter().returnColumnName(addTableName) + "VALUES(" + addTableData + ")";

  }

}
