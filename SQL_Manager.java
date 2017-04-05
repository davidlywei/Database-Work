import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;

import java.sql.*;

/******************************************************************************************
 Class Name:     SQL_Manager

 Purpose:        Repackages SQL commands into a simple to use object

 Author:         David Wei
 Creation Date:  11/06/2016
 Last Modified:  Initial Revision
 IDE Used:       Intellij IDEA 2016.2.5
 ******************************************************************************************/
public class SQL_Manager
{
    HashMap<String, String> paramMap;
    Connection dbConn;

    public final int BATCHSIZE = 1000;

    public SQL_Manager(File f) throws FileNotFoundException, SQLException, ClassNotFoundException
    {
        paramMap = new HashMap<>();

        try
        {
            readFile(f);
            connect();
        } catch (FileNotFoundException | ClassNotFoundException | SQLException e)
        {
            throw e;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // Parse thorugh file for parameters
    private void readFile(File f) throws IOException
    {
        try
        {
            FileReader fr = new FileReader(f);
            BufferedReader fin = new BufferedReader(fr);

            String line = "";

            while ((line = fin.readLine()) != null)
            {
                String[] params = line.split(":");
                paramMap.put(params[0], params[1]);
            }

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            throw e;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void connect() throws SQLException, ClassNotFoundException
    {
        try
        {
            System.out.println("Loading jdbc_driver: " + paramMap.get("jdbc_driver"));
            Class.forName(paramMap.get("jdbc_driver"));
            System.out.println("Driver Loaded");

            String server = paramMap.get("host") + ":" +
                            paramMap.get("port") + ":"  +
                            paramMap.get("SID");

            System.out.println("Establishing connection with Server: " + server);
            dbConn = DriverManager.getConnection ("jdbc:oracle:thin:@" + server,
                    paramMap.get("username"), paramMap.get("password"));

        } catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        dbConn.close();
    }

    public void executeQuery(String prepStmt, ArrayList<String[]> entries, String tableName) throws SQLException
    {
        PreparedStatement pstmt = dbConn.prepareStatement(prepStmt);

        int batchNum = 0;

        int[] colTypes = checkTable(tableName);
        int numCols = colTypes.length;

        for(String[] entry : entries)
        {
            for(int i = 0; i < numCols; i++)
            {
                if(i < entry.length)
                {
                    if (colTypes[i] == Types.NUMERIC)
                    {
                        try
                        {
                            pstmt.setDouble(i + 1, Double.parseDouble(entry[i]));
                        }
                        catch (NumberFormatException e)
                        {
                            pstmt.setDouble(i + 1, 0);
                        }
                    }
                    if (colTypes[i] == Types.VARCHAR)
                    {
                        pstmt.setString(i + 1, entry[i]);
                    }
                }
                else
                {
                    if (colTypes[i] == Types.NUMERIC)
                    {
                        pstmt.setInt(i + 1, 0);
                    }
                    if (colTypes[i] == Types.VARCHAR)
                    {
                        pstmt.setString(i + 1, "");
                    }
                }
            }

            pstmt.addBatch();
            batchNum++;

            if((batchNum % BATCHSIZE) == 0)
            {
                pstmt.executeBatch();
                pstmt.clearBatch();
            }
        }

        pstmt.executeBatch();
        pstmt.close();
    }

    public int[] checkTable(String tableName)
    {
        try
        {
            Statement stmt = dbConn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);

            ResultSetMetaData rsmd = rs.getMetaData();

            int numCols = rsmd.getColumnCount();
            int[] types = new int[numCols];

            for(int i = 0; i < numCols; i++)
            {
                types[i] = rsmd.getColumnType(i + 1);
            }

            return types;

        } catch (SQLException e)
        {
            e.printStackTrace();

            return null;
        }

    }

    public void clearTable(String tableName) throws SQLException
    {
        Statement stmt = dbConn.createStatement();

        stmt.executeUpdate("DELETE FROM " + tableName);

        stmt.close();
    }

    public ArrayList<String[]> executeStatementMultiple(String query) throws SQLException
    {
        ArrayList<String[]> resultsList = new ArrayList<>();

        Statement stmt = dbConn.createStatement();

        ResultSet result = stmt.executeQuery(query);

        ResultSetMetaData rmd = result.getMetaData();

        int numCols = rmd.getColumnCount();

        while(result.next())
        {
            String[] entry = new String[numCols];

            for(int i = 0; i < numCols; i++)
            {
                entry[i] = result.getString(i + 1);
            }

            resultsList.add(entry);
        }

        result.close();
        stmt.close();

        return resultsList;
    }

    public ArrayList<String> executeStatementSingle(String query) throws SQLException
    {
        ArrayList<String> resultList = new ArrayList<>();

        Statement stmt = dbConn.createStatement();

        ResultSet result = stmt.executeQuery(query);

        while (result.next())
        {
            resultList.add(result.getString(1));
        }

        result.close();
        stmt.close();

        return resultList;
    }

    public ArrayList<String> getPage(String param, String tableName, int start, int finish)
    {
        ArrayList<String> list = new ArrayList<>();

        String prepStatment =   "SELECT " + param +
                                " FROM (SELECT a.*, ROWNUM rnum " +
                                        " FROM (SELECT UNIQUE " + param +
                                                " FROM " + tableName +
                                                " WHERE " + param + " IS NOT NULL " +
                                                " ORDER BY " + param + " ASC) a " +
                                        " WHERE ROWNUM <= ? ) " +
                                " WHERE rnum >= ? ";

        try
        {
            PreparedStatement ps = dbConn.prepareStatement(prepStatment);

            ps.setInt(2, start);
            ps.setInt(1, finish);

            ResultSet result = ps.executeQuery();

            while(result.next())
            {
                list.add(result.getString(1));
            }

            result.close();
            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return list;
    }
}
