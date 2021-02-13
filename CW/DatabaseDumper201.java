import java.sql.*;
import java.util.*;

/**
 * Class which needs to be implemented.  ONLY this class should be modified
 */
public class DatabaseDumper201 extends DatabaseDumper {
  
    /**
     * 
     * @param c connection which the dumper should use
     * @param type a string naming the type of database being connected to e.g. sqlite
     */
    public DatabaseDumper201(Connection c,String type) {
        super(c,type);
    }
    /**
     * 
     * @param c connection to a database which will have a sql dump create for
     */
    public DatabaseDumper201(Connection c) {
        super(c,c.getClass().getCanonicalName());
    }

    List<String> tableNames = new ArrayList<>();
    public List<String> getTableNames()
    {
        List<String> result = new ArrayList<>();
        
        try 
        {
            String[] VIEW_TYPES = {"TABLE"};
            DatabaseMetaData md = this.getConnection().getMetaData();
            ResultSet rs = md.getTables(null, null, "%", VIEW_TYPES);

            /*Statement stmt = getConnection().createStatement();
            ResultSet rs1 = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA='LSH");*/
            
            while (rs.next()) 
            {
                /*result.add(rs1.getString(1));
                tableNames.add(rs1.getString(1));*/
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

    /*@Override
    public List<String> getViewNames() 
    {
        List<String> result = new ArrayList<>();

        try 
        {
            String[] VIEW_TYPES = {"VIEW"};
            DatabaseMetaData md = this.getConnection().getMetaData();
            ResultSet rs = md.getTables(null, null, null, new String[]{"VIEW"});

            Statement stmt = getConnection().createStatement();
            //ResultSet rs1 = stmt.executeQuery("SELECT TABLE_SCHEMA,TABLE_NAME FROM INFORMATION_SCHEMA.VIEWS");
            //ResultSet rs1 = stmt.executeQuery("SELECT * FROM sqlite_master WHERE type = 'view'");
            ResultSet rs1 = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type = 'view' AND sql LIKE '% FROM %tablename% WHERE %';");
            

            System.out.println("hello there");
            while (rs1.next()) 
            {
                System.out.println("hi");
                result.add(rs1.getString(1));
            }
            System.out.println(result);
        } 
        catch (Exception e) 
        {
            //TODO: handle exception
            System.out.println("exception");
        }


        return result;
    }*/

    public List<String> getViewNames() {
        List<String> result = new ArrayList<>();
        String query = "SELECT name FROM sqlite_master WHERE type = 'view' AND sql LIKE '% FROM %tablename% WHERE %';";
        System.out.println("Showing the views");
        try {
            Statement stmt = super.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("Views in the current database: ");
            System.out.println(rs);
            while (rs.next()) {
                System.out.print(rs.getString(1));
                result.add(rs.getString(1));
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String getDDLForTable(String input) 
    {
        try 
        {
            DatabaseMetaData md = this.getConnection().getMetaData();
            for (String name : tableNames) 
            {
                System.out.println(name + "=" + input);
                if(name.equals(input))
                {
                    //get that tables attributes and create the table
                    ResultSet rs = md.getTables(input, null, null, null);
                    System.out.println(rs);
                    
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnsNumber = rsmd.getColumnCount();
                    while (rs.next()) {
                        for (int i = 1; i <= columnsNumber; i++) {
                            if (i > 1) System.out.print(",  ");
                            String columnValue = rs.getString(i);
                            System.out.print(columnValue + " " + rsmd.getColumnName(i));
                        }
                        System.out.println("");
                    }
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
    public String getInsertsForTable(String tableName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDDLForView(String viewName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDumpString() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void dumpToFileName(String fileName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dumpToSystemOut() 
    {
        // TODO Auto-generated method stub
        //this.getTableNames();
        this.getViewNames();
        //this.getDDLForTable("heroes");
    }

    @Override
    public String getDatabaseIndexes() {
        // TODO Auto-generated method stub
        return null;
    }

}
