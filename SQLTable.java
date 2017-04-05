import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;

/******************************************************************************************
 Class Name:     SQLTable

 Purpose:        Class to parse and hold information for a SQLTable

 Author:         David Wei
 Creation Date:  10/29/2016
 Last Modified:  Initial Revision
 IDE Used:       Intellij IDEA 2016.2.5
 ******************************************************************************************/

public class SQLTable
{
    private File file;
    private BufferedReader fin;
    private String tableName;
    private String tableCols;
    private SQL_Manager sqlManager;
    private String preparedStatement;
    private int numCols;

    private final int BATCHSIZE = 1000;

    public SQLTable(File f, SQL_Manager sqlm)
    {
        file = f;

        tableName = "";
        tableCols = "";
        preparedStatement = "";
        numCols = 0;

        try
        {
            FileReader fr = new FileReader(f);
            fin = new BufferedReader(fr);

            createTable();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        sqlManager = sqlm;
    }

    private void createTable()
    {
        // Remove extension from Filename
        tableName = file.getName();
        tableName = tableName.substring(0, tableName.lastIndexOf('.'));
        tableName = tableName.replace('-','_');

        // Get columns of table
        try
        {
            String columnNames = fin.readLine();
            tableCols = "(" + columnNames.replaceAll("\t", ", ") + ")";

            // Count number of columns
            numCols = tableCols.length() - tableCols.replace(",", "").length();

            String colVars = "";

            for(int i = 0; i < numCols; i++)
            {
                colVars += "?, ";
            }

            colVars += "?";

            preparedStatement = "INSERT INTO " + tableName + tableCols +
                    " VALUES (" + colVars + ")";

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void insertData()
    {
        try
        {
            String data;
            ArrayList<String[]> table = new ArrayList<>();

            System.out.println("Clearing items from Table: " + tableName);
            sqlManager.clearTable(tableName);

            System.out.println("Adding items to Table: " + tableName);

            while((data = fin.readLine()) != null)
            {
                table.add(data.split("\t"));
            }

            sqlManager.executeQuery(preparedStatement, table, tableName);

            System.out.println("Finished adding items to Table: " + tableName);
        } catch (IOException | SQLException e)
        {
            e.printStackTrace();
        }
    }
}