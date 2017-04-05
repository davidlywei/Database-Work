import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/******************************************************************************************
 Class Name:     GUI_RatingBox

 Purpose:        Creates a Node to allow users to Filter by Rating

 Author:         David Wei
 Creation Date:  11/05/2016
 Last Modified:  11/06/2016
                 - Cleaned up code

 IDE Used:       Intellij IDEA 2016.2.5
 ******************************************************************************************/
public class GUI_RatingBox implements GUI_Filters
{
    // Name to use for Filter Tab
    private String filterName;

    // Strings to hold Filter constraints for ratings, and numRatings
    private String ratingFilter;
    private String numRatingFilter;

    // Reference to GUI_Elements
    private GUI_Elements elems;

    public GUI_RatingBox(String name, GUI_Elements el)
    {
        // Set filterName
        filterName = name;

        // Initialize filter
        ratingFilter = "";
        numRatingFilter = "";

        // Store elements reference
        elems = el;
    }

    @Override
    // Create node to use in Accordion
    public Node getNode()
    {
        // Create main VBox
        VBox vbox = new VBox(10);

        // Define grid to hold ratings and numRatings selection
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_RIGHT);

        // Create ratings Filter
        ChoiceBox ratingCB = new ChoiceBox();
        TextField ratingTF = new TextField();
        Text ratingT = new Text("Rating ");

        // --- Set Ratings Equality ChoiceBox
        ratingCB.setItems(FXCollections.observableArrayList(GUI_Utils.EQUALITIES_LIST));
        ratingCB.getSelectionModel().selectedIndexProperty().addListener(((observable, oldValue, newValue) -> {
            if(!ratingTF.getText().isEmpty())
            {
                ratingFilter = "(((m.rtAllCriticsRating + m.rtTopCriticsRating + (m.rtAudienceRating * 2)) / 3)"
                                    + " " + ratingCB.getItems().get(newValue.intValue())
                                    + " " + ratingTF.getText();

                elems.setQueryDisplayText();
            }

        }));

        // --- Set Ratings TextField Listner
        ratingTF.textProperty().addListener(((observable, oldValue, newValue) -> {
            if(ratingCB.getSelectionModel().getSelectedIndex() != -1 && !newValue.isEmpty())
            {
                ratingFilter = "(((m.rtAllCriticsRating + m.rtTopCriticsRating + (m.rtAudienceRating * 2)) / 3)"
                        + " " + ratingCB.getSelectionModel().getSelectedItem()
                        + " " + newValue + ")";

                elems.setQueryDisplayText();
            }
        }));

        // Set numRatings Filter
        ChoiceBox numRatingCB = new ChoiceBox();
        TextField numRatingTF = new TextField();
        Text numRatingT = new Text("Num Ratings ");

        // --- Set numRatings Equality ChoiceBox
        numRatingCB.setItems(FXCollections.observableArrayList(GUI_Utils.EQUALITIES_LIST));
        numRatingCB.getSelectionModel().selectedIndexProperty().addListener(((observable, oldValue, newValue) -> {
            if(!numRatingTF.getText().isEmpty())
            {
                numRatingFilter = "(((m.rtAllCriticsNumReviews + m.rtTopCriticsNumReviews + m.rtAudienceNumRatings) / 3)"
                                    + " " + numRatingCB.getItems().get(newValue.intValue())
                                    + " " + numRatingTF.getText();

                elems.setQueryDisplayText();
            }

        }));

        // --- Set numRatings TextField
        numRatingTF.textProperty().addListener(((observable, oldValue, newValue) -> {
            if(numRatingCB.getSelectionModel().getSelectedIndex() != -1 && !newValue.isEmpty())
            {
                numRatingFilter = "(((m.rtAllCriticsNumReviews + m.rtTopCriticsNumReviews + m.rtAudienceNumRatings) / 3)"
                        + " " + numRatingCB.getSelectionModel().getSelectedItem()
                        + " " + newValue + ")";

                elems.setQueryDisplayText();
            }
        }));

        // Define clear button
        Button clrBtn = new Button("Clear");

        // --- Set clear button action listner
        clrBtn.setOnAction(event ->
        {
            // Set all text to ""
            ratingTF.setText("");
            numRatingTF.setText("");

            ratingFilter = "";
            numRatingFilter = "";

            // Clear CB selection
            ratingCB.getSelectionModel().clearSelection();
            numRatingCB.getSelectionModel().clearSelection();

            // Update display
            elems.setQueryDisplayText();
        });

        // Package everything into the grid
        grid.add(ratingT,  0, 0);
        grid.add(ratingCB, 1, 0);
        grid.add(ratingTF, 2, 0);
        grid.add(numRatingT,  0, 1);
        grid.add(numRatingCB, 1, 1);
        grid.add(numRatingTF, 2, 1);

        // Package everything into the main VBox
        vbox.getChildren().add(grid);
        vbox.getChildren().add(clrBtn);
        vbox.setAlignment(Pos.TOP_CENTER);

        return vbox;
    }

    @Override
    // Return FROM List parameters
    public String getFromList()
    {
        return "";
    }

    @Override
    // Return WHERE List parameters
    public String getWhereList()
    {
        // Depending on which filter is set return the corresponding filter string
        if(ratingFilter.isEmpty() && numRatingFilter.isEmpty())
            return "";
        if(ratingFilter.isEmpty() && !(numRatingFilter.isEmpty()))
            return numRatingFilter;
        if(!(ratingFilter.isEmpty()) && numRatingFilter.isEmpty())
            return ratingFilter;

        return ratingFilter + " AND " + numRatingFilter;
    }

    @Override
    // Return whether or not this filter is used
    public Boolean isUsed()
    {
        return !(ratingFilter.isEmpty() && numRatingFilter.isEmpty());
    }

    @Override
    // Return the name of the filter
    public String getName()
    { return filterName;}
}
