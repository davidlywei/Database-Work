import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

/******************************************************************************************
 Class Name:     populate

 Purpose:        Populates the DB with .dat files.
                 Uses the name of the .dat file as table names, and the column names as
                 table column names.

 Author:         David Wei
 Creation Date:  10/29/2016
 Last Modified:  Initial Revision
 IDE Used:       Intellij IDEA 2016.2.5
 ******************************************************************************************/

// Import JAVA packages for file reading

public class populate
{
    private static SQL_Manager sqlManager;
    private static final String PARAMFILE = "/home/rdness/IdeaProjects/Coen280_hw3/src/parameters.txt";

    public static void main(String[] args)
    {
        try
        {
            sqlManager = new SQL_Manager(new File(PARAMFILE));
        }  catch (FileNotFoundException e)
        {
            System.out.println("Parameter File '" + PARAMFILE + "' not found. \n\n" +
                    "Please ensure the 'PARAMFILE' constant " +
                    "in 'GUI_Elements.java' is linked correctly");

            System.exit(1);
        } catch (ClassNotFoundException e)
        {
            System.out.println("JDBC Driver not found");

            System.exit(1);
        } catch (SQLException e)
        {
            System.out.println("SQL Connection Failure");

            System.exit(1);
        }

        populateTable(args);
    }

    private static void populateTable(String [] filelist)
    {
        // Read through arguments and create maps
        for(String filename : filelist)
        {
            File datFile = new File(filename);

            if( datFile.exists())
            {
                SQLTable table = new SQLTable(datFile, sqlManager);

                table.insertData();
            }
            else
            {
                System.out.println(filename + " not found");
            }
        }
    }
}
