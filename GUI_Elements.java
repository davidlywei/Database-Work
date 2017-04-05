import javafx.scene.text.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;

/******************************************************************************************
 Class Name:     GUI_Elements

 Purpose:        Gets and Stores variables common to the GUI

 Author:         David Wei
 Date:           11/05/2016
 Last Modified:  11/06/2016
                 - Added error dialog if paramfile not found    11/06/2016
                 - Added SQL_Manager                            11/06/2016

 IDE Used:       Intellij IDEA 2016.2.5
 ******************************************************************************************/
public class GUI_Elements
{
    private SQL_Manager sqlManager;

    private final String PARAMFILE = "/home/rdness/IdeaProjects/Coen280_hw3/src/parameters.txt";

    private Text queryDisplay;
    private ArrayList<GUI_Filters> tableList;

    public GUI_Elements(ArrayList<GUI_Filters> tl, Text qt)
    {
        tableList = tl;
        queryDisplay = qt;

        setupSQLManager();
    }

    private String getQuery()
    {
        String query= "";

        query +=   "SELECT    UNIQUE m.ID";
        query += "\nFROM      " + getFromList();
        query += "\nWHERE     " + getWhereList();

        return query;
    }

    private String getFromList()
    {
        ArrayList<String> fromList = new ArrayList<>();

        fromList.add("movies m");

        for(GUI_Filters table : tableList)
        {
            if(table.isUsed() && !table.getFromList().isEmpty())
            {
                fromList.add(table.getFromList());
            }
        }

        return GUI_Utils.getSeparatedList(fromList, ",");
    }

    private String getWhereList()
    {
        ArrayList<String> whereList = new ArrayList<>();

        for(GUI_Filters table : tableList)
        {
            if(table.isUsed())
                whereList.add(table.getWhereList());
        }

        return GUI_Utils.getSeparatedList(whereList, "AND");
    }

    public void setQueryDisplayText()
    {
        Boolean used = false;

        for(GUI_Filters table : tableList)
        {
            if(table.isUsed())
            {
                queryDisplay.setText(getQuery());
                used = true;
                break;
            }
        }

        if(!used)
            queryDisplay.setText("");
    }

    public Text getQueryDisplay()
    { return queryDisplay;}

    private void setupSQLManager()
    {
        try
        {
            File f = new File(PARAMFILE);
            sqlManager = new SQL_Manager(f);
        } catch (FileNotFoundException e)
        {
            GUI_Utils.error("Parameter File '" + PARAMFILE + "' not found. \n\n" +
                    "Please ensure the 'PARAMFILE' constant " +
                    "in 'GUI_Elements.java' is linked correctly");

            System.exit(1);
        } catch (ClassNotFoundException e)
        {
            GUI_Utils.error("JDBC Driver not found");

            System.exit(1);
        } catch (SQLException e)
        {
            GUI_Utils.error("SQL Connection Failure");

            System.exit(1);
        }
    }

    public SQL_Manager getSQLManager()
    { return sqlManager; }
}
