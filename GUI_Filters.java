import javafx.scene.Node;

/******************************************************************************************
 Class Name:     GUI_Filters

 Purpose:        Interface to standardize the filter gui elements

 Author:         David Wei
 Date:           11/05/2016
 Last Modified:  Initial Revision
 IDE Used:       Intellij IDEA 2016.2.5
 ******************************************************************************************/
public interface GUI_Filters
{
    public Node getNode();

    public String getFromList();
    public String getWhereList();

    public Boolean isUsed();

    public String getName();

}
