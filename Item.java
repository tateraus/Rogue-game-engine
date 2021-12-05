package rogueProject;

/**
 * A derived class from the Entity class, it describes all the items which can be picked by the player
 * in the game world
 * @author: Yingxue Chen, yingxuec1@student.unimelb.edu.au, 1210613
 */
public class Item extends Entity {
    /**
     * constructor
     * @param icon
     * @param itemX
     * @param itemY
     */
    public Item(String icon, int itemX, int itemY) {
        setIcon(icon);
        setX(itemX);
        setY(itemY);
        setTraversable(icon);
    }
}
