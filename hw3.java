import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

/******************************************************************************************
 Class Name:     hw3

 Purpose:        Serves as the GUI frontend for a database.
                 Allows users to query the database.

 Author:         David Wei
 Creation Date:  10/29/2016
 Last Modified:  11/07/2016
                 - Added SQL integration                        11/07/2016
                 - Cleaned Up code                              11/06/2016
                 - Separated out class for easier maintenence   11/05/2016
                 - Added Year box                               11/04/2016
                 - Added Ratings box                            11/04/2016
                 - Added search bar to checkboxlist             11/04/2016

 IDE Used:       Intellij IDEA 2016.2.5
 ******************************************************************************************/
public class hw3 extends Application
{
    // Define list of Filters
    private ArrayList<GUI_Filters> filterList;

    // Define application Window
    private Scene window;

    // Define Text variable to hold query
    private Text queryDisplay;

    // Define GUI_Elements (Model of Model-View-Controller Design Pattern)
    private GUI_Elements elems;

    // Test Arrays
    private ArrayList<String> genresList;
    private ArrayList<String> countriesList;
    private ArrayList<String> actorsList;
    private ArrayList<String> directorsList;

    // Dummy main added to support Eclipse
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // Instantiate query and filterList variables
        queryDisplay = new Text();
        filterList = new ArrayList<>();

        // Instantiate GUI_Elements
        elems = new GUI_Elements(filterList, queryDisplay);

        System.out.print("Connection Successful");

        // Create and populate filters
        createFilters();

        // Create main window
        createWindow(primaryStage);
    }

    private void createWindow(Stage stage)
    {
        // Defines broad window layout
        /*
            MHB
            ---------------------
            |     |             |
            | FVB |     RVB     |
            |     |             |
            ---------------------

            MHB = Main HBox
                   - Contains everything

            FVB = filters VBox
                   - Contains Filter Accordion and Execute Query button

            RVB = Results VBox
                   - Contains the Text boxes for the SQL query, and
                     the results of the query

        */

        VBox filtersVBox = new VBox();
        HBox mainHBox = new HBox();
        VBox resultsVBox = new VBox();

        // Create section labels
        Text filterTF = new Text("Filters");
        filterTF.setFont(new Font(20));
        Text queryTF = new Text("Query");
        queryTF.setFont(new Font(20));
        Text resultsTF = new Text("Results");
        resultsTF.setFont(new Font(20));

        // Create Filter Accordion
        Accordion filterAccd = new Accordion();
        filterAccd.setMinHeight(GUI_Utils.MAX_WINDOW_LENGTH - 85);
        filterAccd.setMaxHeight(GUI_Utils.MAX_WINDOW_LENGTH - 85);

        // --- Populate Filter Accordion
        for(GUI_Filters filter : filterList)
        {
            TitledPane tp = new TitledPane();
            tp.setText(filter.getName());
            tp.setContent(filter.getNode());

            filterAccd.getPanes().add(tp);
        }

        ListView resultsList = new ListView();
        //ScrollPane resultsSP = new ScrollPane();

        // Create Execute Query Button
        Button exeBtn = new Button();
        exeBtn.setText("Execute Query");
        exeBtn.setOnAction(event -> {
            if(!queryDisplay.getText().isEmpty())
            {
                SQL_Manager sm = elems.getSQLManager();

                try
                {
                    String query = GUI_Utils.RESULTS.replace("<QUERY>", queryDisplay.getText());
                    System.out.println("\nQuery: \n" + query);

                    ArrayList<String[]> result
                            = sm.executeStatementMultiple(query);

                    ObservableList<Node> itemlist = FXCollections.observableArrayList();

                    for(String[] str : result)
                    {
                        GridPane grid = new GridPane();

                        grid.add(new Text("Title: "),                       0,  0);
                        grid.add(new Text("Genres: "),                      0,  1);
                        grid.add(new Text("Tags: "),                        0,  2);
                        grid.add(new Text("ReleaseYear: "),                 0,  3);
                        grid.add(new Text("CountryofOrigin: "),             0,  4);
                        grid.add(new Text("RatingofAllCritics: "),          0,  5);
                        grid.add(new Text("NumberofAllCriticReviews: "),    0,  6);
                        grid.add(new Text("RatingofTopCritics: "),          0,  7);
                        grid.add(new Text("NumberofTopCriticReviews: "),    0,  8);
                        grid.add(new Text("AudienceRating: "),              0,  9);
                        grid.add(new Text("NumberofAudienceRatings: "),     0, 10);

                        grid.add(new Text(str[ 0]), 1,  0);
                        grid.add(new Text(str[ 1]), 1,  1);
                        grid.add(new Text(str[ 2]), 1,  2);
                        grid.add(new Text(str[ 3]), 1,  3);
                        grid.add(new Text(str[ 4]), 1,  4);
                        grid.add(new Text(str[ 5]), 1,  5);
                        grid.add(new Text(str[ 6]), 1,  6);
                        grid.add(new Text(str[ 7]), 1,  7);
                        grid.add(new Text(str[ 8]), 1,  8);
                        grid.add(new Text(str[ 9]), 1,  9);
                        grid.add(new Text(str[10]), 1, 10);

                        itemlist.add(grid);
                        itemlist.add(new Separator(Orientation.HORIZONTAL));
                    }

                    resultsList.setItems(itemlist);

                } catch (SQLException e)
                {
                    e.printStackTrace();
                }

            }
        });

        Separator sep = new Separator(Orientation.HORIZONTAL);
        sep.setPadding(new Insets(10, 0, 10, 0));

        // Fill FVB
        filtersVBox.setAlignment(Pos.TOP_CENTER);
        filtersVBox.getChildren().add(filterTF);
        filtersVBox.getChildren().add(filterAccd);
        filtersVBox.getChildren().add(sep);
        filtersVBox.getChildren().add(exeBtn);
        filtersVBox.setMinWidth(300);
        filtersVBox.setMaxWidth(300);

        // Define width of RVB
        int resultsWidth = (int) (GUI_Utils.MAX_WINDOW_WIDTH - filtersVBox.getMaxWidth() - 3*GUI_Utils.MARGIN);
        int resultsHeight = (GUI_Utils.MAX_WINDOW_LENGTH / 2) - 30;

        queryDisplay.setWrappingWidth(resultsWidth - 3*GUI_Utils.MARGIN);

        // Create ScrollPane to house SQL query
        ScrollPane querySP = new ScrollPane();
        querySP.setPrefWidth(resultsWidth);
        querySP.setPrefHeight(resultsHeight);
        querySP.setContent(elems.getQueryDisplay());

        resultsList.setPrefHeight(resultsWidth);
        resultsList.setPrefHeight(resultsHeight);

        // Populate RVB
        resultsVBox.getChildren().add(queryTF);
        resultsVBox.getChildren().add(querySP);
        resultsVBox.getChildren().add(new Separator(Orientation.HORIZONTAL));
        resultsVBox.getChildren().add(resultsTF);
        resultsVBox.getChildren().add(resultsList);
        resultsVBox.setMinWidth(resultsWidth);
        resultsVBox.setMaxWidth(resultsWidth);

        // Populate MHB
        mainHBox.getChildren().addAll(filtersVBox, new Separator(Orientation.VERTICAL), resultsVBox);
        mainHBox.setAlignment(Pos.TOP_CENTER);
        mainHBox.setSpacing(GUI_Utils.MARGIN);

        // Set window and stage
        window = new Scene(mainHBox, GUI_Utils.MAX_WINDOW_WIDTH, GUI_Utils.MAX_WINDOW_LENGTH);

        stage.setScene(window);
        stage.setTitle("David Wei's HW 3: Movie Lens DB Query");
        stage.setResizable(false);

        stage.show();
    }

    private void createFilters()
    {
        // Define GUI_Filters objects to contain filter
        GUI_CheckboxList genreFilter;
        GUI_CheckboxList countryFilter;
        GUI_CheckboxList actorFilter;
        GUI_CheckboxList directorFilter;
        GUI_RatingBox ratingsFilter;
        GUI_YearBox yearFilter;
        GUI_UserBox userFilter;

        genreFilter = new GUI_CheckboxList("Genre", genresList, GUI_Utils.TABLECONSTANTS.GENRES, elems);
        countryFilter = new GUI_CheckboxList("Country", countriesList, GUI_Utils.TABLECONSTANTS.COUNTRIES, elems);
        actorFilter = new GUI_CheckboxList("Actor", actorsList, GUI_Utils.TABLECONSTANTS.ACTORS, elems);
        directorFilter = new GUI_CheckboxList("Director", directorsList, GUI_Utils.TABLECONSTANTS.DIRECTORS, elems);
        ratingsFilter = new GUI_RatingBox("Rating", elems);
        yearFilter = new GUI_YearBox("Year", elems);
        userFilter = new GUI_UserBox("Users", elems);

        // Add filters to filter list
        filterList.add(genreFilter);
        filterList.add(countryFilter);
        filterList.add(actorFilter);
        filterList.add(directorFilter);
        filterList.add(ratingsFilter);
        filterList.add(yearFilter);
        filterList.add(userFilter);
    }
}

