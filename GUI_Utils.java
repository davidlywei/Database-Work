import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;

/******************************************************************************************
 Class Name:     GUI_Utils

 Purpose:        This class contains commonly used methods shared across classes for the
                 GUI

 Author:         David Wei
 Date:           11/05/2016
 Last Modified:  Initial Revision
 IDE Used:       Intellij IDEA 2016.2.5
 ******************************************************************************************/
public class GUI_Utils
{
    public static final int MAX_WINDOW_WIDTH = 1000;
    public static final int MAX_WINDOW_LENGTH = 500;
    public static final int MARGIN = 10;
    public static final String[] EQUALITIES_LIST = {"=", ">", ">=", "<", "<=" };

    public static final String RESULTS =
        "SELECT title, LISTAGG(genre, ', ') WITHIN GROUP (ORDER BY genre) as genres, \n" +
        "   tags, year, country, rtAllCriticsRating, rtAllCriticsNumReviews, \n" +
        "   rtTopCriticsRating, rtTopCriticsNumReviews, \n" +
        "   rtAudienceRating, rtAudienceNumRatings\n" +
        "FROM ( SELECT title, genre, LISTAGG(value, ', ') WITHIN GROUP (ORDER BY value) as tags, \n" +
        "               year, country, rtAllCriticsRating, rtAllCriticsNumReviews, \n" +
        "               rtTopCriticsRating, rtTopCriticsNumReviews, rtAudienceRating, \n" +
        "               rtAudienceNumRatings \n" +
        "       FROM (   SELECT motr.title, gotr.genre, totr.value, motr.year, cotr.country, \n" +
        "                       motr.rtAllCriticsRating, motr.rtAllCriticsNumReviews, \n" +
        "                       motr.rtTopCriticsRating, motr.rtTopCriticsNumReviews, \n" +
        "                       motr.rtAudienceRating, motr.rtAudienceNumRatings\n" +
        "                FROM movies motr, movie_genres gotr, tags totr, user_taggedmovies_timestamps uotr, \n" +
        "                       movie_countries cotr, \n" +
        "                       (<QUERY>) minn \n" +
        "                WHERE minn.ID = motr.ID AND motr.ID = gotr.movieID AND motr.ID = uotr.movieID AND uotr.tagID = totr.id \n" +
        "                           AND motr.ID = cotr.movieID) \n" +
        "       GROUP BY title, genre, year, country, rtAllCriticsRating, rtAllCriticsNumReviews, \n" +
        "                   rtTopCriticsRating, rtTopCriticsNumReviews, rtAudienceRating, \n" +
        "                   rtAudienceNumRatings) \n"+
        "GROUP BY title, tags, year, country, rtAllCriticsRating, rtAllCriticsNumReviews, \n" +
        "           rtTopCriticsRating, rtTopCriticsNumReviews, rtAudienceRating, \n" +
        "           rtAudienceNumRatings";

    public static String getSeparatedList(ArrayList<String> list, String delimiter)
    {
        String separatedList = "";

        for(int i = 0; i <= list.size() - 2; i++)
        {
            separatedList += list.get(i) + " " + delimiter + " ";
        }

        separatedList += list.get(list.size() - 1);

        return separatedList;
    }

    public static class TABLECONSTANTS
    {
        private TABLECONSTANTS() {};

        public enum TableID
        {
            DIRECTORS,
            USER_TIMESTAMPS,
            COUNTRIES,
            USER_TAGS,
            MOVIES,
            MOVIE_TAGS,
            ACTORS,
            GENRES,
            LOCATIONS,
            USER_RATED_TIMESTAMP,
            USER_RATED,
            TAGS
        }

        public static final String[] DIRECTORS              = {"movie_directors", "d", "directorName"};
        public static final String[] USER_TIMESTAMPS        = {"user_taggedmovies_timestamps", "uts", "timestamp"};
        public static final String[] COUNTRIES              = {"movie_countries", "c", "country"};
        public static final String[] USER_TAGS              = {"user_taggedmovies", "ut", "tagID"};
        public static final String[] MOVIES                 = {"movies", "m", "id"};
        public static final String[] MOVIE_TAGS             = {"movie_tags", "mt", "tagID"};
        public static final String[] ACTORS                 = {"movie_actors", "a", "actorName"};
        public static final String[] GENRES                 = {"movie_genres", "g", "genre"};
        public static final String[] LOCATIONS              = {"movie_locations", "l", "movieID"};
        public static final String[] USER_RATED_TIMESTAMP   = {"user_ratedmovies_timestamps", "urt", "timestamp"};
        public static final String[] USER_RATED             = {"user_ratedmovies", "ur", "rating"};
        public static final String[] TAGS                   = {"tags", "t", "value"};
    }

    public static void error(String errorMessage)
    {
        Alert errorDialog = new Alert(Alert.AlertType.ERROR);

        errorDialog.setTitle("Error");
        errorDialog.setHeaderText(null);

        VBox vbox = new VBox(10);

        Text errorText = new Text(errorMessage);
        errorText.setWrappingWidth(500);

        vbox.getChildren().add(errorText);

        errorDialog.getDialogPane().setContent(vbox);
        errorDialog.getDialogPane().setPadding(new Insets(10));

        errorDialog.setResizable(true);
        errorDialog.setWidth(300);

        errorDialog.showAndWait();
    }
}
