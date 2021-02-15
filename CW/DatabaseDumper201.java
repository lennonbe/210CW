import java.sql.*;
import java.util.*;

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

        System.out.println(result);

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
                System.out.println("hi");
                result.add(rs.getString(1));
            }
        } 
        catch (Exception e) 
        {
            //TODO: handle exception
            System.out.println("exception");
        }
        
        System.out.println("Views are:");
        System.out.println(result);

        return result;
    }

    /**
     * Method used to get the view names of the database.
     */
    /*public List<String> getViewNames() 
    {
        List<String> result = new ArrayList<>();
        String query = "SELECT name FROM sqlite_master WHERE type = 'view' AND sql LIKE '% FROM %tablename% WHERE %';";
        System.out.println("Showing the views");

        try 
        {
            Statement stmt = super.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("Views in the current database: ");
            System.out.println(rs);
            
            int i = 0;
            while (rs.next()) 
            {
                System.out.println(i);
                System.out.print(rs.getString(1));
                result.add(rs.getString(1));
                System.out.println(); 
                i++;
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return result;
    }*/

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
                    System.out.println(name + "=" + input);

                    Statement stmt = super.getConnection().createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM " + input);

                    System.out.println(rs);
                    
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnsNumber = rsmd.getColumnCount();
                    
                    for (int i = 1; i <= columnsNumber; i++) 
                    {
                        
                        returnString += rsmd.getColumnName(i) + " " + JDBCType.valueOf(rsmd.getColumnType(i));
                        
                        if (i == columnsNumber)
                        {
                            returnString += ")";
                        }
                        else
                        {
                            returnString += ",";
                        }
                            
                    }

                    System.out.println(returnString);
                }
            }            
        } 
        catch (Exception e) 
        {
            //TODO: handle exception
        }

        return returnString;
    }

    /**
     * Get the inserts needed to build the table based of a string input which represents the table name
     */
    @Override
    public String getInsertsForTable(String input) 
    {
        try 
        {
            List<String> namesList = this.getTableNames();
            DatabaseMetaData md = this.getConnection().getMetaData();
            String insertInto = "INSERT INTO " + input + " (";
            String values = " VALUES (";
            String returnString = "";
            String columnNames = "";
            boolean gotColumnNames = false;

            for (String name : namesList) 
            {
                if(name.equals(input))
                {
                    System.out.println(name + "=" + input);

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
                        //System.out.println(insertInto);
                    }
                    //Finished getting column names and no. and will no longer repeat this process in the loop

                    
                    //Use a second result set of the same data to loop thorugh
                    ResultSet rs2 = stmt.executeQuery("SELECT * FROM " + input);
                    ResultSetMetaData rsmd2 = rs2.getMetaData();
                    int columnsNumber2 = rsmd2.getColumnCount();
                    while (rs2.next()) 
                    {
                        returnString += insertInto; 
                        //returnString += values;
                        for (int i = 1; i <= columnsNumber2; i++) 
                        {
                            
                            String columnValue = rs2.getString(i);
                            values += "'" + columnValue + "'";
                            
                            if (i == columnsNumber2)
                            {
                                values += ");\n";
                                //insertInto += values;
                            }
                            else
                            {
                                values += ",";
                            }
                        }

                        returnString += values;
                        values = " VALUES (";
                    }

                    System.out.println(returnString);
                }
            }            
        } 
        catch (Exception e) 
        {
            //TODO: handle exception
        }

        return null;
    }

    @Override
    public String getDDLForView(String viewName) 
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDumpString() 
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void dumpToFileName(String fileName) 
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void dumpToSystemOut() 
    {
        // TODO Auto-generated method stub
        //this.getTableNames();
        //this.getViewNames();
        //this.getDDLForTable("heroes");
        this.getInsertsForTable("heroes");
        this.getInsertsForTable("planets");
        this.getInsertsForTable("powers");
        this.getInsertsForTable("missions");
        this.getDDLForTable("heroes");
        this.getDDLForTable("planets");
        this.getViewNames();
    }

    @Override
    public String getDatabaseIndexes() 
    {
        // TODO Auto-generated method stub
        return null;
    }

}
