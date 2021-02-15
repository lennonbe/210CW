import java.sql.*;
import java.util.*;
import java.io.*;
import java.io.FileNotFoundException;

/**
 * Class which needs to be implemented.  ONLY this class should be modified
 */
public class DatabaseDumper201 extends DatabaseDumper 
{  
    /**
     * 
     * @param c connection which the dumper should use
     * @param type a string naming the type of database being connected to e.g. sqlite
     */
    public DatabaseDumper201(Connection c,String type) 
    {
        super(c,type);
    }

    /**
     * 
     * @param c connection to a database which will have a sql dump create for
     */
    public DatabaseDumper201(Connection c) 
    {
        super(c,c.getClass().getCanonicalName());
    }

    /**
     * Method used to get the names of the different tables in the database.
     */
    List<String> tableNames = new ArrayList<>();
    public List<String> getTableNames()
    {
        List<String> result = new ArrayList<>();
        
        try 
        {
            String[] VIEW_TYPES = {"TABLE"};
            DatabaseMetaData md = this.getConnection().getMetaData();
            ResultSet rs = md.getTables(null, null, "%", VIEW_TYPES);

            while (rs.next()) 
            {
                result.add(rs.getString("TABLE_NAME"));
                tableNames.add(rs.getString("TABLE_NAME"));
            }

        }
        catch (Exception e) 
        {
            //TODO: handle exception
        }

        return result;
    }

    @Override
    public List<String> getViewNames() 
    {
        List<String> result = new ArrayList<>();

        try 
        {
            DatabaseMetaData md = this.getConnection().getMetaData();
            ResultSet rs = md.getTables(null, null, "%", new String[]{"VIEW"});

            while (rs.next()) 
            {
                result.add(rs.getString("TABLE_NAME"));
            }
        } 
        catch (Exception e) 
        {
            //TODO: handle exception
        }
        

        return result;
    }

    /**
     * get the DDL which creates a table given a string as input which represents the table name.
     */
    @Override
    public String getDDLForTable(String input) 
    {
        String returnString = "";
        try 
        {
            List<String> namesList = this.getTableNames();
            DatabaseMetaData md = this.getConnection().getMetaData();
            returnString = "CREATE TABLE ";
            for (String name : namesList) 
            {
                
                if(name.equals(input))
                {
                    returnString += input + " (";

                    Statement stmt = super.getConnection().createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM " + input);
                    
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnsNumber = rsmd.getColumnCount();
                    
                    for (int i = 1; i <= columnsNumber; i++) 
                    {
                        
                        returnString += rsmd.getColumnName(i) + " " + JDBCType.valueOf(rsmd.getColumnType(i)) + " (" + rsmd.getColumnDisplaySize(i) + ")";

                        if (i == columnsNumber)
                        {
                            returnString += ");";
                        }
                        else
                        {
                            returnString += ",";
                        }
                            
                    }
                }
            }            
        } 
        catch (Exception e) 
        {
            //TODO: handle exception
        }

        return returnString;
    }

    public boolean isNumeric(String str) 
    { 
        try 
        {  
          Integer.parseInt(str);  
          return true;
        } 
        catch(NumberFormatException e)
        {  
          return false;  
        }  

      }

    /**
     * Get the inserts needed to build the table based of a string input which represents the table name
     */
    @Override
    public String getInsertsForTable(String input) 
    {
        String returnString = "";
        try 
        {
            List<String> namesList = this.getTableNames();
            DatabaseMetaData md = this.getConnection().getMetaData();
            String insertInto = "INSERT INTO " + input + " (";
            String values = " VALUES (";
            String columnNames = "";
            boolean gotColumnNames = false;

            for (String name : namesList) 
            {
                if(name.equals(input))
                {
                    Statement stmt = super.getConnection().createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM " + input);
                    
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnsNumber = rsmd.getColumnCount();

                    if(gotColumnNames == false)
                    {
                        //Get column names
                        for (int i = 1; i <= columnsNumber; i++) 
                        {
                            columnNames += rsmd.getColumnName(i);
                            
                            if (i == columnsNumber)
                            {
                                columnNames += ")";
                            }
                            else
                            {
                                columnNames += ", ";
                            }
                                
                        }
    
                        insertInto += columnNames;
                        gotColumnNames = true;
                    }
                    //Finished getting column names and no. and will no longer repeat this process in the loop

                    
                    //Use a second result set of the same data to loop thorugh
                    ResultSet rs2 = stmt.executeQuery("SELECT * FROM " + input);
                    ResultSetMetaData rsmd2 = rs2.getMetaData();
                    int columnsNumber2 = rsmd2.getColumnCount();
                    while (rs2.next()) 
                    {
                        returnString += insertInto; 
                        for (int i = 1; i <= columnsNumber2; i++) 
                        {
                            
                            String columnValue = rs2.getString(i);

                            if(this.isNumeric(columnValue) == true)
                            {
                                values += columnValue;
                            }
                            else
                            {
                                values += "'" + columnValue + "'";
                            }
                            
                            if (i == columnsNumber2)
                            {
                                values += ");\n";
                            }
                            else
                            {
                                values += ",";
                            }
                        }

                        returnString += values;
                        values = " VALUES (";
                    }
                }
            }            
        } 
        catch (Exception e) 
        {
            //TODO: handle exception
        }

        return returnString;
    }

    @Override
    public String getDDLForView(String input) 
    {
        String returnString = "";
        try 
        {
            List<String> namesList = this.getViewNames();
            DatabaseMetaData md = this.getConnection().getMetaData();
            returnString = "CREATE TABLE ";
            for (String name : namesList) 
            {
                if(name.equals(input))
                {
                    returnString += input + "_view" + " (";

                    Statement stmt = super.getConnection().createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM " + input);
                    
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnsNumber = rsmd.getColumnCount();
                    
                    for (int i = 1; i <= columnsNumber; i++) 
                    {
                        
                        returnString += rsmd.getColumnName(i) + " " + JDBCType.valueOf(rsmd.getColumnType(i)) + " (" + rsmd.getColumnDisplaySize(i) + ")";
                        
                        if (i == columnsNumber)
                        {
                            returnString += ");";
                        }
                        else
                        {
                            returnString += ",";
                        }
                            
                    }
                }
            }            
        } 
        catch (Exception e) 
        {
            //TODO: handle exception
        }

        return returnString;
    }

    @Override
    public String getDumpString() 
    {
        // TODO Auto-generated method stub

        String str = "Tables create and inserts: \n";

        List<String> namesList = this.getTableNames();
        for(String name : namesList)
        {
            str += this.getDDLForTable(name);
            str += "\n";
            str += this.getInsertsForTable(name);
        }

        str += "\nViews create and inserts: \n";
        List<String> viewsList = this.getViewNames();
        for(String name : viewsList)
        {
            str += this.getDDLForView(name);
            str += "\n";
            str += this.getInsertsForTable(name);
        }

        return str;
    }

    @Override
    public void dumpToFileName(String fileName) 
    {
        // TODO Auto-generated method stub

        String file = fileName + ".txt";
        try (PrintWriter out = new PrintWriter(file)) 
        {
            out.println(getDumpString());
        }
        catch (Exception e) 
        {
            System.out.println("EXCEPTION OCCURED IN: dumpToFileName()");
        }
    }

    @Override
    public void dumpToSystemOut() 
    {
        /*this.getInsertsForTable("heroes");
        this.getInsertsForTable("planets");
        this.getInsertsForTable("powers");
        this.getInsertsForTable("missions");
        this.getDDLForTable("heroes");
        this.getDDLForTable("planets");
        this.getViewNames();
        this.getDDLForView("hw");*/

        System.out.println(this.getDumpString());
        this.dumpToFileName("testFile1");
    }

    @Override
    public String getDatabaseIndexes() 
    {
        // TODO Auto-generated method stub
        return null;
    }

}
