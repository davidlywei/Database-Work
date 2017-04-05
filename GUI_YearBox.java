import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/******************************************************************************************
 Class Name:     GUI_YearBox

 Purpose:        Allows user to create filter by movie year

 Author:         David Wei
 Creation Date:  11/05/2016
 Last Modified:  Initial Revision
 IDE Used:       Intellij IDEA 2016.2.5
 ******************************************************************************************/
public class GUI_YearBox implements GUI_Filters
{
    private String fromFilter;
    private String toFilter;

    private String filterName;
    private GUI_Elements elems;

    public GUI_YearBox(String name, GUI_Elements el)
    {
        filterName = name;

        fromFilter = "";
        toFilter = "";

        elems = el;
    }

    @Override
    public Node getNode()
    {
        VBox vbox = new VBox(10);

        GridPane grid = new GridPane();

        TextField fromTF = new TextField();
        TextField toTF = new TextField();

        fromTF.textProperty().addListener((observable, oldVal, newVal) -> {
            fromFilter = "m.year >= '" + fromTF.getText() + "'";

            elems.setQueryDisplayText();
        });

        toTF.textProperty().addListener((observable, oldVal, newVal) -> {
            toFilter = "m.year <= '" + toTF.getText() + "'";

            elems.setQueryDisplayText();
        });

        Text fromT = new Text("From: ");
        Text toT = new Text("To: ");

        Button clrBtn = new Button("Clear");
        clrBtn.setOnAction(event -> {
            fromTF.setText("");
            toTF.setText("");

            fromFilter = "";
            toFilter = "";

            elems.setQueryDisplayText();
        });

        grid.add(fromT, 0, 0);
        grid.add(fromTF, 1, 0);
        grid.add(toT, 0, 1);
        grid.add(toTF, 1, 1);

        vbox.getChildren().add(grid);
        vbox.getChildren().add(clrBtn);
        vbox.setAlignment(Pos.TOP_CENTER);

        return vbox;
    }

    @Override
    public String getFromList()
    {
        return "";
    }

    @Override
    public String getWhereList()
    {
        if(fromFilter.isEmpty() && toFilter.isEmpty())
            return "";
        if(fromFilter.isEmpty() && !(toFilter.isEmpty()))
            return toFilter;
        if(!(fromFilter.isEmpty()) && toFilter.isEmpty())
            return fromFilter;

        return fromFilter + " AND " + toFilter;
    }

    @Override
    public Boolean isUsed()
    {
        return !(fromFilter.isEmpty() && toFilter.isEmpty());
    }

    @Override
    public String getName()
    {
        return filterName;
    }
}
