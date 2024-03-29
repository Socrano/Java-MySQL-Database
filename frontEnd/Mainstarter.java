package javamysql;

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

  public static void main(String[] args) throws SQLException {

    // Useful constants throughout the code
    ArrayList<String> tableList = new ArrayList<String>(
        Arrays.asList("airline", "airport", "country", "plane", "planecompany", "route"));

    /*
     * when mainSwitch is true, the System continues running otherwise it will close
     * connection pending completion!!!
     */

    boolean superSwitch = true;
    
    bodyStructure(superSwitch, tableList);
  }

  
  static void bodyStructure(boolean superSwitch, ArrayList<String> tableList) throws SQLException {
    boolean mainSwitch = true;

    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();

    }
    catch (InstantiationException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    catch (IllegalAccessException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    catch (ClassNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    Scanner input = new Scanner(System.in);
    System.out.println("Please enter a username.");
    String username = input.nextLine();
    System.out.println("Please enter a password.");
    String password = input.nextLine();
    // change the DB address HERE!!!
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb"
        + "?characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
        username, password);

    while (mainSwitch) {
      try {

        // change the DB address HERE!!!
        connect(con);


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
            add(input, addswitch, tableList, con);
            continue;

          // deletion of data
          case "delete":
            boolean deleteswitch = true;
            delete(input, deleteswitch, tableList, con);
            continue;

          case "change":
            boolean changeSwitch = true;
            delete(input, changeSwitch, tableList, con);
            continue;

          default:
            System.out.println("Invalid input.");
            break;
          }

        case "search":
          // begin search
          System.out.println("Do you want to require procedure or just table?");
          ArrayList<String> procedureChoice = new ArrayList<String>(
              Arrays.asList("searchAirportRoute", "searchCountryAirport", "searchAirlinePlane",
                  "searchAirlineRoute", "searchCompanyPlane", "searchCountryAirline",
                  "searchDeparture_DestinationRoute"));

          String key3 = input.nextLine();
          switch (key3) {
          case "procedure":
            boolean procedureSwitch = true;
            procedure(procedureSwitch, procedureChoice, input, con);
            continue;

          case "table":
            boolean tableSwitch = true;
            table(tableSwitch, tableList, input, con);
            continue;
          }
        case "exit":
          System.out.println("Goodbye!");
          mainSwitch = false;
          break;

        default:
          System.out.println("Invalid value. Please try again.");
          continue;
        }
        return;
      }

      catch (Exception exp) {
        System.out.println("SQLException: " + exp.getMessage());
      }
      finally {
        try {
          con.close();
        }
        catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }
  
  
  static void connect(Connection con) throws SQLException {


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
  }
  
  static void add(Scanner input, boolean addswitch, ArrayList<String> tableList, Connection con) {
    while (addswitch) {
      System.out.println("Which table to you want to add data to?");
      String addTableName = input.nextLine();
      if (tableList.contains(addTableName)) {
        System.out
            .println("Please enter the tuple you want to add. Separated by ',' and space"
                + "in the following order.");
        System.out.println("For null value, please type null directly.");
        System.out.println(
            "Please make sure each non-null value is also surrounded by '' (single quotation mark)");

        System.out.println(new Mainstarter().returnColumnName(addTableName));
        String addTableData = input.nextLine();
        System.out.println(con);

        try {
          String addStatement = "INSERT INTO " + addTableName + " ("
              + new Mainstarter().returnColumnName(addTableName) + ") " + "VALUES("
              + addTableData + ")";
          Statement pstmtadd = con.createStatement();
          int rs1 = pstmtadd.executeUpdate(addStatement);

          System.out.println("The data has been successfully added!");
          if (rs1 == 1 || rs1 == 0) {
            System.out.println(rs1 + " row is affected.");
          }
          else {
            System.out.println(rs1 + " rows are affected.");
          }

        }
        catch (Exception e) {
          System.out.println(e.toString());
          System.out.println("Please try again.");
          continue;
        }

        System.out.println();
        System.out.println("Do you want to add another value? Y/N");
        System.out.println("Press N to go back to the main menu.");
        String inpans = input.nextLine();
        if (inpans.equals("Y") || inpans.equals("y")) {
          continue;
        }
        else {
          addswitch = false;
          break;
        }

      }
      else {
        System.out.println("Invalid table name. Please Try again.");
        continue;
      }

    }
  }
  
  static void delete(Scanner input, boolean deleteswitch, ArrayList<String> tableList, Connection con) {
    while (deleteswitch) {
      System.out.println("Which table do you want to delete data from?");
      String deleteTable = input.nextLine();
      if (tableList.contains(deleteTable)) {
        System.out.println(
            "Are there any keywords restrictions to limit the scope of deletion?");
        System.out.println(
            "Please type this in 'TableColumnName = 'xxx'' format, separated by 'OR' or 'AND'.");
        System.out.println("Type 'no' if you want to delete everything in this table.");
        String requirement = input.nextLine();
        System.out.println("Any other restrictions? Please type in SQL query format.");
        System.out.println("Leave this empty if there is no other restrictions.");
        String otherreq = input.nextLine();
        String deleteStatement;

        if (requirement.equals("no")) {
          deleteStatement = "DELETE FROM " + deleteTable;
        }
        else {
          deleteStatement = "DELETE FROM " + deleteTable + " WHERE " + requirement + " "
              + otherreq;
        }

        try {
          Statement deletestmt = con.createStatement();
          int resultnum2 = deletestmt.executeUpdate(deleteStatement);
          System.out.println("The data has been successfully deleted!");
          if (resultnum2 == 1 || resultnum2 == 0) {
            System.out.println(resultnum2 + " row is affected.");
          }
          else {
            System.out.println(resultnum2 + " rows are affected.");
          }
        }
        catch (Exception e) {
          System.out.println(e.toString());
          System.out.println("Please try again.");
          continue;
        }

        System.out.println();
        System.out.println("Do you want to delete another value? Y/N");
        System.out.println("Press N to go back to the main menu.");
        String inpans = input.nextLine();
        if (inpans.equals("Y") || inpans.equals("y")) {
          continue;
        }
        else {
          deleteswitch = false;
          break;
        }

      }
      else {
        System.out.println("Invalid table name. Please Try again.");
        continue;
      }
    }
  }
  
  static void change(Scanner input, boolean changeSwitch, ArrayList<String> tableList, Connection con) {
    while (changeSwitch) {
      System.out.println("Which table do you want to edit data from?");
      String tableName = input.nextLine();
      if (tableList.contains(tableName)) {
        System.out.println("Are there any limitation for the data you want to edit?");
        System.out.println(
            "Please enter in 'ColumnName = 'xxx'' form, separated by 'AND' or 'OR'");
        String editLimit = input.nextLine();
        System.out
            .println("What are you going to change? Please choose from the following.");
        System.out.println(new Mainstarter().returnColumnName(tableName));
        System.out
            .println("Please enter in 'ColumnName = 'xxx'' form, separated by comma.");
        String editTo = input.nextLine();

        try {
          String finalInput = "UPDATE " + tableName + " SET " + editTo + " WHERE "
              + editLimit;
          Statement editstmt = con.createStatement();
          int editnum3 = editstmt.executeUpdate(finalInput);
          System.out.println("The data has been successfully edited!");
          if (editnum3 == 1 || editnum3 == 0) {
            System.out.println(editnum3 + " row is affected.");
          }
          else {
            System.out.println(editnum3 + " rows are affected.");
          }

          System.out.println();
          System.out.println("Do you want to edit another value? Y/N");
          System.out.println("Press N to go back to the main menu.");
          String inpans2 = input.nextLine();
          if (inpans2.equals("Y") || inpans2.equals("y")) {
            continue;
          }
          else {
            changeSwitch = false;
            break;
          }

        }
        catch (Exception e) {
          System.out.println(e.toString());
          System.out.println("Please try again.");
          continue;
        }
      }
      else {
        System.out.println("Invalid table name. Please Try again.");
        continue;
      }

    }
  }
  
  static void procedure(boolean procedureSwitch, ArrayList<String> procedureChoice,
      Scanner input, Connection con) {
    while (procedureSwitch) {
      System.out.println("Please choose a procedure you want from the following list\n"
          + "Note: the name of procedure (searchAB) means search B which belongs to A");
      System.out.println(procedureChoice);

      ArrayList<String> airlineN = new ArrayList<String>(Arrays
          .asList("searchAirportRoute", "searchAirlinePlane", "searchAirlineRoute"));
      ArrayList<String> countryN = new ArrayList<String>(
          Arrays.asList("searchCountryAirport", "searchCountryAirline"));

      String procedure = input.nextLine();

      // check validity of procedure name.
      if (airlineN.contains(procedure)) {
        System.out.println("Please enter an airline name.");
      }
      else if (countryN.contains(procedure)) {
        System.out.println("Please enter a country name.");
      }
      else if (procedure.equals("searchCompanyPlane")) {
        System.out.println("Please enter a company name.");
      }
      else if (procedure.equals("searchDeparture_DestinationRoute")) {
        System.out.println(
            "Please enter the departure and destination airport name, separated by comma.");
      }
      else {
        System.out.println("Invalid procedure name, please try again.");
        continue;
      }

      String given = input.nextLine();

      try {
        String procedureStmt = "CALL " + procedure + "(\"" + given + "\");";
        System.out.println(procedureStmt);
        Statement pstmtadd = con.createStatement();
        ResultSet rs2 = pstmtadd.executeQuery(procedureStmt);
        ResultSetMetaData rs2md = rs2.getMetaData();
        int columnsNumber = rs2md.getColumnCount();
        String columnName[] = new String[columnsNumber];

        while (rs2.next()) {
          for (int i = 1; i < columnsNumber; i++) {
            columnName[i - 1] = rs2md.getColumnLabel(i);
            System.out.print(columnName[i - 1] + ": " + rs2.getString(i) + " ");
            System.out.println();
          }
        }

        System.out.println();
        System.out.println("Do you want to search for another procedure? Y/N");
        System.out.println("Press N to go back to the main menu.");
        String inpans = input.nextLine();
        if (inpans.equals("Y")) {
          continue;
        }
        else {
          procedureSwitch = false;
          break;
        }
      }
      catch (Exception e) {
        System.out.println(e.toString());
        System.out.println("Please try again.");
        continue;
      }
    }
  }
  
  static void table(boolean tableSwitch, ArrayList<String> tableList, Scanner input, Connection con)
      throws SQLException {
    while (tableSwitch) {
      System.out.println("Please choose a table from the following list:");
      System.out.println(tableList);
      String tableName = input.nextLine();

      if (tableList.contains(tableName)) {
        String procedureStmt = "SELECT * FROM " + tableName;
        Statement pstmtadd = con.createStatement();
        ResultSet rs3 = pstmtadd.executeQuery(procedureStmt);
        ArrayList<String> result = new ArrayList<String>();
        ResultSetMetaData rs3md = rs3.getMetaData();
        int columnsNumber = rs3md.getColumnCount();
        String columnName[] = new String[columnsNumber];

        while (rs3.next()) {
          for(int i = 1; i <= columnsNumber; i++) {
              System.out.print(rs3md.getColumnLabel(i) + ": " + rs3.getString(i) + " ");
          System.out.println();
      } 
        }

        System.out.println();
        System.out.println("Do you want to search for another table? Y/N");
        System.out.println("Press N to go back to the main menu.");
        String inpans = input.nextLine();
        if (inpans.equals("Y")) {
          continue;
        }
        else {
          tableSwitch = false;
          break;
        }
      }
      else {
        System.out.println("Invalid name. Please try again.");
        continue;
      }
    }
  }
  
  //give the user available column name according to the table they give
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
