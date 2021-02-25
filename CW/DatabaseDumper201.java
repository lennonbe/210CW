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
    public List<String> getTableNames()
    {
        //Empty list
        List<String> result = new ArrayList<>();
        try 
        {
            //Get a result set where we can access the table names for the tables
            String[] VIEW_TYPES = {"TABLE"};
            DatabaseMetaData md = this.getConnection().getMetaData();
            ResultSet rs = md.getTables(null, null, "%", VIEW_TYPES);

            //Iterate through the tables inside the result set adding their names to the list
            while (rs.next()) 
            {
                result.add(rs.getString("TABLE_NAME"));
            }

        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        //Return the list
        return result;
    }

    @Override
    public List<String> getViewNames() 
    {
        //Empty array list
        List<String> result = new ArrayList<>();
        try 
        {
            //Get a result set where we can access the table names for the views
            DatabaseMetaData md = this.getConnection().getMetaData();
            ResultSet rs = md.getTables(null, null, "%", new String[]{"VIEW"});

            //Iterate through the views inside the result set adding their names to the list
            while (rs.next()) 
            {
                result.add(rs.getString("TABLE_NAME"));
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        //Return the list
        return result;
    }

    /**
     * Cleans up the corner case in which there are primes in an attribute or column name.
     * @param input
     * @return
     */
    public String cleanUpPrimes(String input)
    {
        String returnString = input;

        //Cleans up the quotes
        if(returnString.indexOf("'") != -1)
        {
            returnString = returnString.replace("'", "''");
        }

        //Returns a string with the corrected escaping of quotes
        return returnString;
    }

    //Method for getting the drop statements to delete tables if they already exist.
    public String getDropsForTable()
    {
        String returnString = "";
        try 
        {
            List<String> namesList = this.getTableNames();
            
            for(String name : namesList)
            {
                returnString += "DROP TABLE IF EXISTS ";
                returnString += name + ";";
                returnString += "\n--\n";
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return returnString;
    }

    //Method for getting the drop statements to delete tables if they already exist.
    public String getDropsForView()
    {
        String returnString = "";
        try 
        {
            List<String> namesList = this.getViewNames();
            
            for(String name : namesList)
            {
                returnString += "DROP TABLE IF EXISTS ";
                returnString += "view_" + name + ";";
                returnString += "\n--\n";
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return returnString;
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
            returnString = "CREATE TABLE ";
            for (String name : namesList) 
            {
                if(name.equals(input)  && !name.contains("sqlite_"))
                {
                    returnString += "'" + input + "'" + " (";
                    
                    DatabaseMetaData dbmd = super.getConnection().getMetaData();
                    ResultSet rs2 = dbmd.getColumns(null, null, name, null);
                    
                    //Fetching needed column data and types
                    while(rs2.next())
                    {
                        if(rs2.getInt("NULLABLE") == dbmd.columnNoNulls)
                        {
                            returnString += "'" + rs2.getString("COLUMN_NAME") + "'" + " " + rs2.getString("TYPE_NAME") + " NOT NULL";
                            returnString += ",";
                        }
                        else
                        {
                            returnString += "'" + rs2.getString("COLUMN_NAME") + "'" + " " + rs2.getString("TYPE_NAME");
                            returnString += ",";
                        }
                    }
                    
                    //Building the PRIMARY KEY string
                    ResultSet pk = dbmd.getPrimaryKeys(null, null, name);
                    String temp = "";
                    while(pk.next())
                    {
                        temp += "'" + pk.getString("COLUMN_NAME") + "'" + ", ";
                    }
                    
                    //Building the FOREIGN KEYS string
                    ResultSet fk = dbmd.getImportedKeys(null, null, name);
                    String temp2 = "";
                    while(fk.next())
                    {
                        temp2 += " FOREIGN KEY ("  + "'" +fk.getString("FKCOLUMN_NAME") + "'" +") REFERENCES " + "'" +fk.getString("PKTABLE_NAME") + "'" + "(" + "'" + fk.getString("PKCOLUMN_NAME") +"'),";
                    } 
                    
                    //Final string to be returned trimmed
                    if(temp != "" && temp2 != "")
                    {
                        returnString = returnString.substring(0, returnString.length() - 1);
                        temp = temp.substring(0, temp.length() - 2); 
                        temp2 = temp2.substring(0, temp2.length() - 1); 
                        returnString += ", PRIMARY KEY(" + temp +")," + temp2 +");";
                    }
                    else if(temp == "" && temp2 == "")
                    {
                        returnString = returnString.substring(0, returnString.length() - 1);
                        returnString += ");";
                    }
                    else if(temp == "" && temp2 != "") 
                    {
                        returnString = returnString.substring(0, returnString.length() - 1);
                        temp2 = temp2.substring(0, temp2.length() - 1); 
                        returnString += ", PRIMARY KEY(" + temp +"));";
                    }
                    else if(temp != "" && temp2 == "")
                    {
                        returnString = returnString.substring(0, returnString.length() - 1);
                        temp = temp.substring(0, temp.length() - 2); 
                        returnString += ", PRIMARY KEY(" + temp +"));";
                    }       
                    
                    returnString += "\n--\n";
                }
                else
                {
                    returnString += "";
                }
            }            
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return returnString;
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
            String insertInto = "INSERT INTO " + input + " (";
            String values = " VALUES (";
            String columnNames = "";
            boolean gotColumnNames = false;

            for (String name : namesList) 
            {
                if(name.equals(input) && !name.contains("sqlite_"))
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
                        for (int i = 1; i <= columnsNumber2; i++) 
                        {
                            int typeInt = rsmd.getColumnType(i); 
                            
                            if(typeInt == java.sql.Types.VARCHAR 
                            || typeInt == java.sql.Types.LONGNVARCHAR
                            || typeInt == java.sql.Types.LONGVARCHAR
                            || typeInt == java.sql.Types.NCHAR || typeInt == java.sql.Types.NVARCHAR)
                            {
                                String columnValue = rs2.getString(i);
                                values += "'" + this.cleanUpPrimes(columnValue) + "'";
                            }
                            else if(typeInt == java.sql.Types.BOOLEAN
                            || typeInt == java.sql.Types.BINARY
                            || typeInt == java.sql.Types.LONGVARBINARY
                            || typeInt == java.sql.Types.DATE || typeInt == java.sql.Types.TIME
                            || typeInt == java.sql.Types.TIMESTAMP || typeInt == java.sql.Types.TIMESTAMP_WITH_TIMEZONE
                            || typeInt == java.sql.Types.TIME_WITH_TIMEZONE)
                            {
                                String columnValue = rs2.getString(i);
                                values += "'" + columnValue + "'";
                            }
                            else if(typeInt == java.sql.Types.INTEGER)
                            {
                                int value = rs2.getInt(i);
                                values += value;
                            }
                            else if(typeInt == java.sql.Types.FLOAT)
                            {
                                float value = rs2.getFloat(i);
                                values += value;
                            }
                            else if(typeInt == java.sql.Types.DOUBLE)
                            {
                                double value = rs2.getDouble(i);
                                values += value;
                            }
                            else if(typeInt == java.sql.Types.BOOLEAN)
                            {
                                boolean value = rs2.getBoolean(i);
                                values += "'" + value + "'";
                            }
                            else if(typeInt == java.sql.Types.NULL)
                            {
                                String value = "NULL";
                                values += "'" + value + "'";
                            }
                            else
                            {
                                String value = rs2.getString(i);
                                values += "'" + value + "'";
                            }
                            
                            //If its the final column add a closing brace and semi-colon plus new line and comment dash for easier reading
                            if (i == columnsNumber2)
                            {
                                values += ");\n--\n";
                            }
                            //Else, just add a comma
                            else
                            {
                                values += ",";
                            }                            
                        }

                        returnString += insertInto; 
                        returnString += values;                            
                        values = " VALUES (";
                        
                    }

                }
                else
                {
                    returnString += "";
                }
            }            
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return returnString;
    }

    public String getInsertsForView(String input) 
    {
        String returnString = "";
        try 
        {
            List<String> namesList = this.getViewNames();
            DatabaseMetaData md = this.getConnection().getMetaData();
            String insertInto = "INSERT INTO view_" + input + " (";
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
                        for (int i = 1; i <= columnsNumber2; i++) 
                        {
                            int typeInt = rsmd.getColumnType(i); 
                            
                            if(typeInt == java.sql.Types.VARCHAR 
                            || typeInt == java.sql.Types.LONGNVARCHAR
                            || typeInt == java.sql.Types.LONGVARCHAR
                            || typeInt == java.sql.Types.NCHAR || typeInt == java.sql.Types.NVARCHAR
                            )
                            {
                                String columnValue = rs2.getString(i);
                                values += "'" + this.cleanUpPrimes(columnValue) + "'";
                            }
                            else if(typeInt == java.sql.Types.BOOLEAN
                            || typeInt == java.sql.Types.BINARY
                            || typeInt == java.sql.Types.LONGVARBINARY
                            || typeInt == java.sql.Types.DATE || typeInt == java.sql.Types.TIME
                            || typeInt == java.sql.Types.TIMESTAMP || typeInt == java.sql.Types.TIMESTAMP_WITH_TIMEZONE
                            || typeInt == java.sql.Types.TIME_WITH_TIMEZONE)
                            {
                                String columnValue = rs2.getString(i);
                                values += "'" + columnValue + "'";
                            }
                            else if(typeInt == java.sql.Types.INTEGER)
                            {
                                int value = rs2.getInt(i);
                                values += value;
                            }
                            else if(typeInt == java.sql.Types.FLOAT)
                            {
                                float value = rs2.getFloat(i);
                                values += value;
                            }
                            else if(typeInt == java.sql.Types.DOUBLE)
                            {
                                double value = rs2.getDouble(i);
                                values += value;
                            }
                            else if(typeInt == java.sql.Types.BOOLEAN)
                            {
                                boolean value = rs2.getBoolean(i);
                                values += "'" + value + "'";
                            }
                            else if(typeInt == java.sql.Types.NULL)
                            {
                                String value = "NULL";
                                values += "'" + value + "'";
                            }
                            else
                            {
                                String value = rs2.getString(i);
                                values += "'" + value + "'";
                            }
                            
                            //If its the final column add a closing brace and semi-colon plus new line and comment dash for easier reading
                            if (i == columnsNumber2)
                            {
                                values += ");\n--\n";
                            }
                            //Else, just add a comma
                            else
                            {
                                values += ",";
                            }                            
                        }

                        returnString += insertInto; 
                        returnString += values;                            
                        values = " VALUES (";
                        
                    }
                }
            }            
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
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
            returnString = "CREATE TABLE ";
            for (String name : namesList) 
            {
                if(name.equals(input))
                {
                    returnString += "view_" + input + " (";
                    
                    DatabaseMetaData dbmd = super.getConnection().getMetaData();
                    ResultSet rs2 = dbmd.getColumns(null, null, name, null);

                    while(rs2.next())
                    {
                        returnString += "'" + rs2.getString("COLUMN_NAME") + "' " + rs2.getString("TYPE_NAME");
                        returnString += ",";
                    }
                    
                    returnString = returnString.substring(0, returnString.length() - 1); 
                    returnString += ");\n--\n";
                }
            }            
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return returnString;
    }

    public String getDatabaseAndDriverVersion()
    {
        String returnString = "";
        try 
        {
            DatabaseMetaData dbmd = super.getConnection().getMetaData();
            returnString = "--JDBC version " + dbmd.getJDBCMajorVersion() + "." + dbmd.getJDBCMinorVersion() + "\n"; 
            returnString += "--Database version " + dbmd.getDatabaseMajorVersion() + "." + dbmd.getDatabaseMinorVersion();        
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return returnString;
    }

    @Override
    public String getDumpString() 
    {
        //Sorting the list containing the table names, this allows them to be in correct order
        List<String> namesList = this.getTableNames();
        this.listSorter(namesList);

        //Printing out a comment with the database and driver version
        String str = "\n" + getDatabaseAndDriverVersion() + "\n";

        //Printing out the drop statements to delete any pre-existing tables that we may be re-writing, as if we did not do this it would cause errors
        str += "\n--Tables drop statements: \n";
        str += getDropsForTable();

        //Since views are also tables, perform a drop on these as well
        str += "\n--Views drop statements: \n";
        str += getDropsForView();
        
        //Tables create and insert statements
        str += "\n--Tables create and inserts: \n";
        for(String name : namesList)
        {
            str += this.getDDLForTable(name);
            str += this.getInsertsForTable(name);
        }

        //Views create and insert statements
        str += "\n--Views create and inserts: \n";
        List<String> viewsList = this.getViewNames();
        for(String name : viewsList)
        {
            str += this.getDDLForView(name);
            str += this.getInsertsForView(name);
        }

        //Indexes of the DB
        str += "\n--Indexes of DB: \n";
        str += this.getDatabaseIndexes();

        //Final string with everything added to it
        return str;
    }

    public List<String> listSorter(List<String> input)
    {
        //Getting list consisting of table names
        List<String> namesList = this.getTableNames();

        //Sorting algorithm which replaces the location of the list element based on references
        int index = 0;
        for(String name : namesList)
        {
            String str = this.getDDLForTable(name);

            int index2 = 0;
            for(String name2 : namesList)
            {
                //Use of String.contains() method to check for references
                if(str.contains(name2) && !name.equals(name2) && index2 > index)
                {
                    namesList.set(index, name2);
                    namesList.set(index2, name);
                }

                index2++;
            }

            index++;
        }

        return namesList;
    }

    @Override
    public void dumpToFileName(String fileName) 
    {
        //Method which dumps the string containing the information needed to build a DB into a .sql file
        String file = fileName + ".sql";
        try (PrintWriter out = new PrintWriter(file)) 
        {
            out.println(getDumpString());
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    @Override
    public void dumpToSystemOut() 
    {
        //Dumping to system out
        System.out.println(this.getDumpString());
        this.dumpToFileName("testFile1");
    }

    @Override
    public String getDatabaseIndexes() 
    {
        String returnString = "";
        try 
        {
            DatabaseMetaData md = this.getConnection().getMetaData();
            List<String> namesList = this.getTableNames();
            
            for (String name : namesList)
            {
                //Getting index info
                ResultSet rs = md.getIndexInfo(null, null, name, true, false);
                
                //Iterating through result set which contains index info
                while(rs.next())
                {
                    String tempString = "";
                    String temp = rs.getString("ASC_OR_DESC");

                    if(!rs.getString("INDEX_NAME").contains("sqlite_"))
                    {
                        //Handling of different ordering of indexes, per spec
                        //Adding to a returnString the create index statements
                        if(temp == "A")
                        {
                            tempString = "CREATE INDEX '" + rs.getString("INDEX_NAME") + "' ON '" + rs.getString("TABLE_NAME") + "' ('"  + rs.getString("COLUMN_NAME") + "' ASC);";   
                        }
                        else if(temp == "D")
                        {
                            tempString = "CREATE INDEX '" + rs.getString("INDEX_NAME") + "' ON '" + rs.getString("TABLE_NAME") + "' ('"  + rs.getString("COLUMN_NAME") + "' DESC);";   
                        }
                        else if(temp == null)
                        {
                            tempString = "CREATE INDEX '" + rs.getString("INDEX_NAME") + "' ON '" + rs.getString("TABLE_NAME") + "' ('"  + rs.getString("COLUMN_NAME") + "');";   
                        }
                        
                        //Slitting statements with -- for formatting
                        returnString += tempString + "\n--\n";
                    }
                    
                }
                
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return returnString;
    }

}
