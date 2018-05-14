
/*
    This program provides a way for the user of this resteraunt interface
    to interact with the system with the roles Manager, Employee, and Customer.
    A menu interface allows the user to navigate using the displayed options
        (numbers or c - commit, r - rollback)
 */

import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Databases
{
    // Initialize input reader for later
    static BufferedReader keyboard = new BufferedReader(new InputStreamReader (System.in));;
    static Connection conn; // Connection
    static Statement stmt;  // Statement
    static String loginName; // Customer's logged in name
    
    public static void Dashes(int num){
        // Pretty dashed line
        for(int i=0;i<(num);i++)
                System.out.print("-");
        System.out.print("\n");
    }
    
    public static void runQuery(String query) throws SQLException{
        // Simple query execution and table printing method
        ResultSet rset = stmt.executeQuery(query);
        printTable(rset);
        rset.close();
    }
    
    public static void getOrderTotal(String orderID) throws SQLException{
        /*
        Calculates the total price with the total cost of each item in the order
            multiplied by the quantity of that item. This will return an int
            representing the total order price.
        */
        String q = "SELECT SUM(ItemCost * Quantity)"
                + " FROM HasItem,MenuItem,Orders"
                + " WHERE Orders.OrderID = HasItem.OrderID"
                + " and HasItem.ItemID = MenuItem.ItemID"
                + " and Orders.OrderID = " + orderID;
        ResultSet rset = stmt.executeQuery(q);
        String sumCost = "";
        while(rset.next())
            sumCost = rset.getString(1);
        rset.close();
        System.out.println("TOTAL: $" + sumCost);
    }
    
    public static void managerMenu(int selection) throws SQLException, IOException{
        //Handles the different menu options and queries the manager can perform.
        String input;
        switch(selection){
            case 1:
                // Displays the employees table with optional employee name
                System.out.print("(Optional) Enter an Employee Name:");
                input = keyboard.readLine();
                if(input.isEmpty())
                     runQuery("SELECT * FROM Employee ");
                else
                     runQuery("SELECT *"
                            + " FROM Employee"
                            + " WHERE EmployeeName = '" + input + "'");
                input = "";
                break;
            case 2:
                // Displays Orders table w/ optional orderID
                System.out.print("(Optional) Enter an OrderID:");
                input = keyboard.readLine();
                if(input.isEmpty())
                     runQuery("SELECT * FROM Orders ");
                else{
                     runQuery("SELECT *"
                            + " FROM Orders"
                            + " WHERE OrderID = " + input);
                    if(input.isEmpty())
                        getOrderTotal(input);
                }
                input = "";
                break;
             case 3:
                // Displays Customers table w/ optional CustomerName
                System.out.print("(Optional) Enter a Customer Name:");
                input = keyboard.readLine();
                if(input.isEmpty())
                     runQuery("SELECT * FROM Customers ");
                else
                     runQuery("SELECT *"
                            + " FROM Customers"
                            + " WHERE CustomerName = '" + input + "'");
                input = "";
                break;
             case 4:
                // Displays MenuItem table w/ optional ItemName
                System.out.print("(Optional) Enter a Menu Item Name:");
                input = keyboard.readLine();
                if(input.isEmpty())
                     runQuery("SELECT * FROM MenuItem ");
                else
                     runQuery("SELECT *"
                            + " FROM MenuItem"
                            + " WHERE ItemName = '" + input + "'");
                input = "";
                break;
             case 5:
                // Displays Ingredient table w/ optional ingredientName
                System.out.print("(Optional) Enter an Ingredient Name:");
                input = keyboard.readLine();
                if(input.isEmpty())
                     runQuery("SELECT * FROM Ingredient ");
                else
                     runQuery("SELECT *"
                            + " FROM Ingredient"
                            + " WHERE IngredientName = '" + input + "'");
                input = "";
                break;
             case 6:
                // Displays vendor table w/ optional vendorName
                System.out.print("(Optional) Enter a Vendor Name:");
                input = keyboard.readLine();
                if(input.isEmpty())
                     runQuery("SELECT * FROM Vendor ");
                else
                     runQuery("SELECT *"
                            + " FROM Vendor"
                            + " WHERE VendorName = '" + input + "'");
                input = "";
                break;
             case 7:
                // Displays Vendor and their ingredient based on ingredientName 
                System.out.print("Enter an Ingredient Name:");
                input = keyboard.readLine();
                if(input.isEmpty())
                     return;
                else
                     runQuery("SELECT *"
                             + " FROM Ingredient, Vendor"
                             + " WHERE"
                             + " Ingredient.IngredientID = Vendor.IngredientID"
                             + " and IngredientName = '" + input + "'");
                input = "";
                break;
             case 8:
                 // Deletes orders where the deliverydt is over 6 months ago (using sysdate as current time)
                 System.out.print("Clear orders > 6 months old? [y/n]:");
                 input = keyboard.readLine();
                 if(input.equals("y")){
                     stmt.executeUpdate("DELETE FROM ORDERS "
                             + "WHERE ADD_MONTHS(SYSDATE, -6) > DeliveryDateTime");
                     System.out.println("Command Successful!");
                 }
                 break;
             case 9:
                // Inserts new employee with their details. Cannot be null.
                System.out.print("Enter an Employee Name:");
                String eName = keyboard.readLine();
                if(eName.isEmpty())
                    return;
                System.out.print("Enter an Employee Position:");
                String ePos = keyboard.readLine();
                if(ePos.isEmpty())
                    return;
                System.out.print("Enter an Employee ID:");
                String eID = keyboard.readLine();
                if(eID.isEmpty())
                    return;
                 stmt.executeUpdate("INSERT INTO EMPLOYEE"
                         + " VALUES(" + eID + ",'" + eName + "','" + ePos + "')");
                 System.out.println("New employee created!");
                 break;
             case 10:
                 // Deletes a vendor based on VendorName
                 System.out.print("Enter a Vendor Name:");
                 String vName = keyboard.readLine();
                 if(vName.isEmpty())
                     return;
                 stmt.executeUpdate("DELETE FROM"
                         + " Vendor WHERE VendorName = '" + vName +"'");
                 System.out.println("Command Successful!");
                 break;
            case 11:
                // Displays how many of each item have been sold
                runQuery("SELECT MenuItem.ItemID, MenuItem.ItemName, "
                        + "SUM(HasItem.Quantity)\"Total Sold\" " 
                        + "FROM HasItem JOIN MenuItem "
                        + "ON HasItem.ItemID = MenuItem.ItemID "
                        + "GROUP BY MenuItem.ItemID, MenuItem.ItemName");                  
                break;
        }
    }
    
    public static void employeeMenu(int selection) throws SQLException, IOException, SQLException, SQLException{
        //Handles the different menu options and queries the employee can perform.
        String input;
        switch(selection){
            case 1:
                // Displays order based on orderID
                System.out.print("(Optional) Enter an OrderID:");
                input = keyboard.readLine();
                if(input.isEmpty())
                     runQuery("SELECT * FROM Orders ");
                else{
                     runQuery("SELECT *"
                            + " FROM Orders"
                            + " WHERE OrderID = " + input);
                    if(input.isEmpty())
                        getOrderTotal(input);
                }
                input = "";
                break;
             case 2:
                //Displays customer based on customerName
                System.out.print("(Optional) Enter a Customer Name:");
                input = keyboard.readLine();
                if(input.isEmpty())
                     runQuery("SELECT * FROM EmployeeCustomer ");
                else
                     runQuery("SELECT *"
                            + " FROM EmployeeCustomer"
                            + " WHERE CustomerName = '" + input + "'");
                input = "";
                break;
             case 3:
                // Displays a MenuItem based on ItemName 
                System.out.print("(Optional) Enter a Menu Item Name:");
                input = keyboard.readLine();
                if(input.isEmpty())
                     runQuery("SELECT * FROM MenuItem ");
                else
                     runQuery("SELECT * "
                            + " FROM MenuItem"
                            + " WHERE ItemName = '" + input + "'");
                input = "";
                break;
             case 4:
                // Displays an ingredient based on IngredientName
                System.out.print("(Optional) Enter an Ingredient Name:");
                input = keyboard.readLine();
                if(input.isEmpty())
                     runQuery("SELECT * FROM Ingredient ");
                else
                     runQuery("SELECT *"
                            + " FROM Ingredient"
                            + " WHERE IngredientName = '" + input + "'");
                input = "";
                break;
             case 5:
                 // Displays information about all orders with customer, quantity, and order item
                 runQuery("SELECT"
                         + " Orders.OrderID, CustomerName,"
                         + " ItemName, Quantity,"
                         + " DeliveryDateTime, DeliveryMethod,"
                         + " CustomerPhone"
                         + " FROM"
                         + " Customers, Orders, HasItem, MenuItem"
                         + " WHERE"
                         + " Customers.OrderID = Orders.OrderID and"
                         + " HasItem.OrderID = Orders.OrderID and"
                         + " HasItem.ItemID = MenuItem.ItemID");
                 break;
        }
    }
    
    public static void customerMenu(int selection) throws SQLException, IOException{
    //Handles the different menu options and queries the customer can perform.
        String input;
        switch(selection){
            case 1:
               // Displays information for this customer and their order based on customerName
               runQuery("SELECT"
                       + " CustomerID, CustomerName,"
                       + " OrderDateTime, DeliveryDateTime, DeliveryMethod,"
                       + " OrderLocation, Quantity, ItemName"
                       + " FROM Orders, Customers, HasItem, MenuItem"
                       + " WHERE Orders.OrderID = Customers.OrderID"
                       + " and Orders.OrderID = HasItem.OrderID"
                       + " and HasItem.ItemID = MenuItem.ItemID"
                       + " and CustomerName = '" + loginName + "'");
               break;
            case 2:
               // Displays the customer's information based on their customerName
               runQuery("SELECT *"
                       + " FROM Customers"
                       + " WHERE CustomerName = '" + loginName + "'");
               break;
            case 3:
               // Displays information about all items in MenuItem
               runQuery("SELECT"
                       + " ItemName, ItemCost, ItemDescription"
                       + " FROM MenuItem ");
               break;   
            case 4:
                // Allows a user to update their information. Must have info entered in all fields.
                // Updates loginName so it reflects the currently logged in customerName
                System.out.print("Enter New Name:");
                String newName = keyboard.readLine();
                if(newName.isEmpty())
                    return;
                System.out.print("Enter New Phone:");
                String newPhone = keyboard.readLine();
                if(newPhone.isEmpty())
                    return;
                System.out.print("Enter New Email:");
                String newEmail = keyboard.readLine();
                if(newEmail.isEmpty())
                    return;
                String query = "UPDATE Customers"
                    + " SET CustomerName = '" + newName + "',"
                        + " CustomerPhone = '" + newPhone + "',"
                        + " CustomerEmail = '" + newEmail + "'"
                        + " WHERE CustomerName = '" + loginName + "'";
               int result = stmt.executeUpdate(query);
               loginName = newName;
               System.out.println("User updated.");
               break;
         }
    }

    public static void mainMenu(int selection) throws IOException, SQLException{
        List<String> mm = new ArrayList();
        switch(selection){ // Menu input number
         case 1:
             mm.add("View all Employees.");
             mm.add("View all Orders.");
             mm.add("View all Customers.");
             mm.add("View all MenuItems.");
             mm.add("View all Ingredients.");
             mm.add("View all Vendors.");
             mm.add("View Ingredient and its Vendor.");
             mm.add("Delete orders older than 6 months.");
             mm.add("Add a new Employee.");
             mm.add("Delete a vendor.");
             mm.add("Display total sold.");
             menu(mm,1);
             break;
         case 2:
             mm.add("View all Orders.");
             mm.add("View Customers.");
             mm.add("View all MenuItems.");
             mm.add("View all Ingredients.");
             mm.add("View Order and Customer Details.");
             menu(mm,2);
             break;
         case 3:
             mm.add("View my Order.");
             mm.add("View my Information.");
             mm.add("View all MenuItems.");
             mm.add("Update my Information.");
             System.out.print("Enter Customer Login Name:");
             loginName = keyboard.readLine();
             if(loginName.isEmpty()){
                 System.out.println("No user! Try 'Billy Bob'.");
                 return;
             }
             menu(mm,3);
             break;
        }
    }
    
    public static void menuAction(int selection, int selectionModifier) throws IOException, SQLException{
        // Top level menu - transitions user into a submenu
        // selectionModifier - refers to which topmenu was selected
        // selection - which submenu option was selected
        switch(selectionModifier){
           case 0:
               mainMenu(selection); 
               return;
           case 1:
               managerMenu(selection);
               return;
            case 2:
               employeeMenu(selection);
               return;
            case 3:
                customerMenu(selection);
                return;
        }
    }

    public static void menu(List<String> menuItems, int selectionModifier) throws IOException, SQLException{
        // menu
        // Handles display of menu and user input
        // selectionModifier - submenu selected, 0 = toplevel
        // choice - menu option within a submenu selected
        // menuItems = list of menu entries, must be in order
        // options: c - prompted to commit, r - prompted to rollback
        int choice;
        while(true){
                Dashes(40);
                if(selectionModifier != 0)
                    System.out.println("[0] - return.");
                for(int i=0;i<menuItems.size();i++){
                    System.out.println("[" + Integer.toString(i+1) + "] - " + menuItems.get(i));
                }
                System.out.println("[" + Integer.toString(menuItems.size() + 1) + "] - exit.");
                System.out.println("[c] - save/commit.");
                System.out.println("[r] - rollback.");
                Dashes(40);
                System.out.print(">>>");
                try{ // Valid user input
                        String input = keyboard.readLine();     
                        String yn;
                        if(input.equals("c")){ 
                            System.out.print("Commit? [y/n]:");
                            yn = keyboard.readLine();
                            if(yn.equals("y"))
                                conn.commit();
                            System.out.println("Command successful.");
                            yn = "";
                        }
                        else if(input.equals("r")){
                            System.out.print("Rollback? [y/n]:");
                            yn = keyboard.readLine();
                            if(yn.equals("y")){
                                conn.rollback();
                                return;
                            }
                            System.out.println("Command successful.");
                            yn = "";
                        }
                        choice = Integer.parseInt(input);
                        if(choice == menuItems.size() + 1)
                            System.exit(0);
                        else if(choice == 0 && selectionModifier != 0)
                            return;
                        else if(choice > menuItems.size() || choice < 1)
                            throw new NumberFormatException();
                        else
                            menuAction(choice, selectionModifier);
                }
                catch(NumberFormatException e){
                        Dashes(40);
                        System.out.println("Invalid input.");
                        continue;
                }
        }
    }

    public static void printTable(ResultSet rset){
        // Method for printing table pretty-likeish
        // Prints col names with decent spacing
        // Prints column content with less than decent spacing
        try{
           ResultSetMetaData metadata = rset.getMetaData();
           int ColCount = metadata.getColumnCount(); // Num of columns
           Dashes(ColCount * 40);
           for(int i=0;i<ColCount;i++) // Print column name with nice formatting
                System.out.printf("%-20s | ",metadata.getColumnName(i+1));
           System.out.print("\n");
           Dashes(ColCount * 40);
           while (rset.next()) { // Print row content
                for(int i=0;i<ColCount;i++)
                        System.out.printf("%-20s | ", rset.getString(i+1));
                System.out.print("\n");
           }
           Dashes(ColCount * 40);
        }
        catch(SQLException e){
                System.out.println(e);
        }
    }
    
    public static void driverSetup() throws SQLException{
        // main oracle setup stuff
        String username="", password = ""; //creds
        try {
           DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            Dashes(40); // Decoration
                System.out.println("Registered the driver...");
            conn = DriverManager.getConnection (
                       "jdbc:oracle:thin:@oracle1.wiu.edu:1521/toolman.wiu.edu",
                username, password); // Connect & authenticate to DB
            conn.setAutoCommit(false);
                System.out.println("Logged into Oracle as " + username);
            stmt = conn.createStatement(); // Initial statement
        }
        catch(SQLException e)
        {          
            System.out.println("Caught SQL Exception: \n     " + e);
        }
    }
    
    public static void main (String args []) throws IOException, SQLException
    {
        // Run driver setup and spawn top level main menu
            driverSetup();
            while(true){ 
                List<String> mainMenu = new ArrayList();
                mainMenu.add("Login as Manager");
                mainMenu.add("Login as Employee");
                mainMenu.add("Login as Customer");
                menu(mainMenu,0); // Display menu
            }
    }
}