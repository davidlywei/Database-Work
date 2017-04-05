import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.util.ArrayList;

/******************************************************************************************
 Class Name:     GUI_CheckboxList

 Purpose:        Creates a list of Checkboxes for the types of GUI interactions that
                 require it

 Author:         David Wei
 Creation Date:  11/05/2016
 Last Modified:  11/06/2016
                 - Added Paging             11/07/2016
                 - Cleaned up Code          11/06/2016

 IDE Used:       Intellij IDEA 2016.2.5
 ******************************************************************************************/
public class GUI_CheckboxList implements GUI_Filters
{
    // List containing selected items
    private ArrayList<String> filters;

    // List containing the set of all items to select from
    private ArrayList<String> options;

    // Alias for table
    private String tableAbbrev;

    // Parameter in table to compare to
    private String searchParam;

    // Boolean operator to compare by
    private String boolOP;

    // Name of SQL table
    private String tableName;

    // GUI_Elements reference
    private GUI_Elements elems;

    // Name of the to use for Filter Tab
    private String filterName;

    private int numPages;
    private int currPage;

    // Define the size of pages
    private final int PAGESIZE = 1000;

    public GUI_CheckboxList(String name, ArrayList<String> opts, String[] tcnst, GUI_Elements el)
    {
        // Get table constants for query construction
        tableName   = tcnst[0];
        tableAbbrev = tcnst[1];
        searchParam = tcnst[2];

        // Set default Where parameter boolean connector
        boolOP = "AND";

        filters = new ArrayList<>();

        elems = el;

        filterName = name;

        numPages = getNumPages();

        currPage = 0;

        // Set options to the list of filter options
        getList();
    }

    @Override
    // Returns the node to add to Accordion
    public Node getNode()
    {
        // Create main VBox
        VBox cvbox = new VBox();

        // Grids for placing the Search bar and operator selectors
        GridPane searchGP = new GridPane();
        GridPane opGrid = new GridPane();

        Text pageDisc = new Text((currPage + 1) + " of " + numPages);

        // Create Searchbar grid

        // --- Create searchbar TextField, and description text
        TextField searchBar = new TextField();
        Text searchText = new Text("Search: ");

        // --- Add searchbar listner
        searchBar.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.isEmpty())
            {
                currPage = 0;
                numPages = getNumPages();

                getList();
                pageDisc.setText((currPage + 1) + " of " + numPages);

                cvbox.getChildren().remove(cvbox.getChildren().size() - 1);
                cvbox.getChildren().add(createCBoxListView(options));
            }
            else
            {
                numPages = 0;

                getSearchList(newValue);
                pageDisc.setText("1 of 1");

                cvbox.getChildren().remove(cvbox.getChildren().size() - 1);
                cvbox.getChildren().add(createCBoxListView(options));
            }

        });

        // --- Package all parts of searchbar into gridpane
        searchGP.add(searchText, 0, 0);
        searchGP.add(searchBar, 1, 0);

        // Create Operation Slider

        // --- Create labels for Operation Slider
        Text opText = new Text("Combination Op: ");
        Text andOp = new Text("AND");
        Text orOp = new Text("OR");

        // --- Create slider
        Slider opSlider = new Slider();
        opSlider.setMaxWidth(20);
        opSlider.setMin(0);
        opSlider.setMax(2);
        opSlider.setPadding(new Insets(0, 5, 0, 5));

        // --- Map slider listner to set boolop
        opSlider.valueProperty().addListener(((observable, oldValue, newValue) -> {
            // Force double position value to 0 or 2 based on it's position relative to pos 1
            // Map boolop accordingly
            if(newValue.intValue() < 1)
            {
                opSlider.setValue(0);

                boolOP = "AND";
            }
            else
            {
                opSlider.setValue(2);

                boolOP = "OR";
            }

            elems.setQueryDisplayText();
        }));

        // --- Package all operation slider pieces together in opGrid
        opGrid.add(opText, 0, 0);
        opGrid.add(andOp, 1, 0);
        opGrid.add(opSlider, 2, 0);
        opGrid.add(orOp, 3, 0);
        opGrid.setPadding(new Insets(GUI_Utils.MARGIN, 0, GUI_Utils.MARGIN, 0));

        // Create ListView of CheckBoxes
        ListView itemListView = createCBoxListView(options);

        // Package everything into main VBox
        cvbox.setAlignment(Pos.CENTER);
        cvbox.getChildren().add(searchGP);
        cvbox.getChildren().add(opGrid);

        if(numPages > 1)
        {
            HBox pageHBox = new HBox(10);
            pageHBox.setAlignment(Pos.CENTER);
            pageHBox.setPadding(new Insets(0, 0, 10, 0));

            Button prevPage = new Button("<");
            prevPage.setOnAction(event -> {
                if(currPage > 0)
                {
                    currPage--;
                    getList();

                    pageDisc.setText((currPage + 1) + " of " + numPages);

                    cvbox.getChildren().remove(cvbox.getChildren().size() - 1);
                    cvbox.getChildren().add(createCBoxListView(options));
                }
            });

            Button nextPage = new Button(">");
            nextPage.setOnAction(event -> {
                if((currPage +1) < numPages)
                {
                    currPage++;
                    getList();

                    pageDisc.setText((currPage + 1) + " of " + numPages);

                    cvbox.getChildren().remove(cvbox.getChildren().size() - 1);
                    cvbox.getChildren().add(createCBoxListView(options));
                }
            });

            pageHBox.getChildren().add(prevPage);
            pageHBox.getChildren().add(pageDisc);
            pageHBox.getChildren().add(nextPage);

            cvbox.getChildren().add(pageHBox);
        }

        cvbox.getChildren().add(itemListView);

        return cvbox;
    }

    private ListView<CheckBox> createCBoxListView(ArrayList<String> list)
    {
        // Create main ListView
        ListView<CheckBox> listview = new ListView<>();

        // Define observable list to hold all checkboxes
        ObservableList itemList = FXCollections.observableArrayList();

        // Create checkboxes, and add it to itemlist
        for (String item : list)
        {
            CheckBox cbox = new CheckBox(item);

            if(filters.contains(item))
                cbox.setSelected(true);

            // Add item listner
            cbox.selectedProperty().addListener((observable, oldVal, newVal) -> {
                if(newVal)
                {
                    filters.add(item);
                }
                else
                {
                    filters.remove(item);
                }

                elems.setQueryDisplayText();
            });

            itemList.add(cbox);
        }

        // Set all items into listview
        listview.setItems(itemList);

        return listview;
    }


    @Override
    // Returns the list of parameters to add to FROM list
    public String getFromList()
    {
        return tableName + " " + tableAbbrev;
    }

    @Override
    // Returns the list of parameters to add to WHERE list
    public String getWhereList()
    {
        // Define string to return
        String whereParams = "";

        // Check if filter is used
        if (filters.size() > 0)
        {
            // If filter is used, add SQL join constraint with movie
            whereParams += "m.id = " + tableAbbrev + ".movieID AND (";

            // Create new parameter list
            ArrayList<String> paramList = new ArrayList<>();

            // Create a string for each filter, and add to param list
            for (String option : filters)
            {
                paramList.add( tableAbbrev + "."
                                + searchParam + " = '"
                                + option + "'");
            }

            // Combine all items in paramlist connected with boolop
            whereParams += GUI_Utils.getSeparatedList(paramList, boolOP);

            // add closing parenthesis
            whereParams += ")";
        }

        return whereParams;
    }

    @Override
    // Returns a bool to signify if this filter is used
    public Boolean isUsed()
    {
        return !(filters.size() == 0);
    }

    @Override
    // Returns the name of the filter
    public String getName()
    {return filterName;}

    private int getNumPages()
    {
        int numEntries = 0;

        try
        {
            SQL_Manager sm = elems.getSQLManager();

            ArrayList<String> pageList = sm.executeStatementSingle( "SELECT COUNT(*) " +
                                                                    "FROM ( SELECT UNIQUE " + searchParam +
                                                                            " FROM " + tableName +
                                                                            " WHERE " + searchParam +
                                                                            " IS NOT NULL)");

            numEntries = Integer.parseInt(pageList.get(0));
        }
        catch (SQLException e)
        {
            return 0;
        }

        return (int) Math.ceil(numEntries / (double) PAGESIZE);
    }

    private void getList()
    {
        SQL_Manager sm = elems.getSQLManager();

        options = sm.getPage(searchParam, tableName,
                ((currPage * PAGESIZE) + 1), ((currPage + 1) * PAGESIZE));
    }

    private void getSearchList(String searchStr)
    {
        SQL_Manager sm = elems.getSQLManager();

        String query =  "SELECT UNIQUE " + searchParam +
                        " FROM " + tableName +
                        " WHERE " + searchParam + " IS NOT NULL " +
                            " AND " + searchParam + " LIKE '%" + searchStr + "%'" +
                        " ORDER BY " + searchParam + " ASC";

        try
        {
            options = sm.executeStatementSingle(query);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
