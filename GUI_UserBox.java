import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.ZoneId;
import java.util.ArrayList;

/******************************************************************************************
 Class Name:     GUI_UserBox

 Purpose:        Allows the user to Filter by User parameters

 Author:         David Wei
 Creation Date:  11/05/2016
 Last Modified:  11/06/2016
                 - Cleaned up Code              11/06/2016

 IDE Used:       Intellij IDEA 2016.2.5
 ******************************************************************************************/
public class GUI_UserBox implements GUI_Filters
{
    // Define name of filter used in Accordion
    private String filterName;

    // Add reference to GUI_Elements
    private GUI_Elements elems;

    // Need to define a ZoneID for date
    private ZoneId zid;

    // Define Strings to hold filters
    private String usernameFilter;
    private String fromFilter;
    private String toFilter;
    private String userRFilter;

    public GUI_UserBox(String name, GUI_Elements el)
    {
        // Set needed ZoneID
        zid = ZoneId.systemDefault();

        // Set Filter name
        filterName = name;

        // Initialize Filters
        usernameFilter = "";
        fromFilter = "";
        toFilter = "";
        userRFilter = "";

        // Store Gui Elems ref
        elems = el;
    }

    @Override
    // Returns Node for Accordion
    public Node getNode()
    {
        // Create main VBox to hold all elements
        VBox vbox = new VBox(10);

        // Create Grid to set spacing
        GridPane grid = new GridPane();
        // Create inner grid for spacing purposes
        GridPane innerGrid = new GridPane();

        // Set UserID filter

        // --- Define userID discription text and textfield
        Text userID = new Text("User ID: ");
        TextField userIDTF = new TextField();

        // ----- Define userID Textfield listner
        userIDTF.textProperty().addListener((observable, oldVal, newVal) -> {
            usernameFilter = "u.userID = " + userIDTF.getText();

            elems.setQueryDisplayText();
        });

        // Setup To and From filters

        // --- Create To and From Description text
        Text fromT = new Text("From: ");
        Text toT = new Text("To: ");

        // --- Create Date pickers for To and From filter
        DatePicker fromDP = new DatePicker();
        DatePicker toDP = new DatePicker();

        // ----- Setup To / From action listner
        fromDP.setOnAction(event -> {
            fromFilter = "u.timestamp >= "
                            + fromDP.getValue().atStartOfDay(zid).toEpochSecond();

            elems.setQueryDisplayText();
        });
        toDP.setOnAction(event -> {
            toFilter = "u.timestamp >= "
                            + toDP.getValue().atStartOfDay(zid).toEpochSecond();

            elems.setQueryDisplayText();
        });

        // Create UserRating filter

        // --- Define userRating description text, equality choice box, and textfield
        Text userRDiscT = new Text("User Rating: ");
        ChoiceBox ratingCB = new ChoiceBox();
        TextField userRTF = new TextField();

        // ----- Define listners for the three elements
        ratingCB.setItems(FXCollections.observableArrayList(GUI_Utils.EQUALITIES_LIST));
        ratingCB.getSelectionModel().selectedIndexProperty().addListener(((observable, oldValue, newValue) -> {
            if(!userRTF.getText().isEmpty())
            {
                userRFilter = "u.rating"
                                    + " " + ratingCB.getItems().get(newValue.intValue())
                                    + " " + userRTF.getText();

                elems.setQueryDisplayText();
            }

        }));
        userRTF.textProperty().addListener(((observable, oldValue, newValue) -> {
            if(ratingCB.getSelectionModel().getSelectedIndex() != -1 && !newValue.isEmpty())
            {
                userRFilter = "u.rating"
                        + " " + ratingCB.getSelectionModel().getSelectedItem()
                        + " " + newValue;

                elems.setQueryDisplayText();
            }

        }));

        // Define clear button

        // --- Create Clear Button
        Button clrBtn = new Button("Clear");

        // ----- Add clear button listner
        clrBtn.setOnAction(event ->
        {
            userIDTF.setText("");
            usernameFilter = "";

            fromDP.setValue(null);
            toDP.setValue(null);
            fromFilter = "";
            toFilter = "";

            ratingCB.getSelectionModel().clearSelection();
            userRTF.setText("");
            userRFilter = "";

            elems.setQueryDisplayText();
        });

        // Create inner grid to for fitting purposes
        innerGrid.add(ratingCB, 0, 0);
        innerGrid.add(userRTF, 1, 0);

        // Define separators for aesthetics
        Separator sep1 = new Separator(Orientation.HORIZONTAL);
        sep1.setPadding(new Insets(10, 0, 10, 0));
        Separator sep2 = new Separator(Orientation.HORIZONTAL);
        sep2.setPadding(new Insets(10, 0, 10, 0));
        Separator sep3 = new Separator(Orientation.HORIZONTAL);
        sep3.setPadding(new Insets(10, 0, 10, 0));
        Separator sep4 = new Separator(Orientation.HORIZONTAL);
        sep4.setPadding(new Insets(10, 0, 10, 0));

        // Package everything into a Grid
        grid.add(userID, 0, 0);
        grid.add(userIDTF, 1, 0);
        grid.add(sep1, 0, 1);
        grid.add(sep2, 1, 1);
        grid.add(fromT, 0, 2);
        grid.add(fromDP, 1, 2);
        grid.add(toT, 0, 3);
        grid.add(toDP, 1, 3);
        grid.add(sep3, 0, 4);
        grid.add(sep4, 1, 4);
        grid.add(userRDiscT, 0, 5);
        grid.add(innerGrid,1,5);

        // Add all items to main VBox
        vbox.getChildren().add(grid);
        vbox.getChildren().add(clrBtn);
        vbox.setAlignment(Pos.TOP_CENTER);

        return vbox;
    }

    @Override
    // Returns the parameters for a FROM list
    public String getFromList()
    {
        return "";
    }

    @Override
    // Returns the parameters for a WHERE list
    public String getWhereList()
    {
        // Creates array list to use for concatenation later
        ArrayList<String> whereList = new ArrayList<>();


        // Checks if we need to add movie / user join
        if(!( usernameFilter.isEmpty()
                && fromFilter.isEmpty()
                && toFilter.isEmpty()
                && userRFilter.isEmpty()))
            whereList.add("m.id = u.movieID");

        if(!usernameFilter.isEmpty())
            whereList.add(usernameFilter);

        if(!fromFilter.isEmpty())
            whereList.add(fromFilter);

        if(!toFilter.isEmpty())
            whereList.add(toFilter);

        if(!userRFilter.isEmpty())
            whereList.add(userRFilter);

        return GUI_Utils.getSeparatedList(whereList, "AND");
    }

    @Override
    // Returns if filter is used or not
    public Boolean isUsed()
    {
        return (!( usernameFilter.isEmpty()
                && fromFilter.isEmpty()
                && toFilter.isEmpty()
                && userRFilter.isEmpty()));
    }

    @Override
    // Returns name of filter
    public String getName()
    {
        return filterName;
    }
}
