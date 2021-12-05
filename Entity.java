package rogueProject;

/**
 * A base class for the entities existing in the game world, including player, monsters and items. They all share some
 * common attributes including coordinates, traversable settings and icon
 * @author: Yingxue Chen, yingxuec1@student.unimelb.edu.au, 1210613
 */
public class Entity {
    private int x;
    private int y;
    private boolean traversable;
    private String icon;


    /**
     * constructors
     */
    public Entity(String itemIcon) {
        icon = itemIcon;
        setTraversable(itemIcon);
    }

    public Entity() {

    }

    /**
     * getters and setters
     */
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    public boolean isTraversable() {
        return traversable;
    }

    public void setTraversable(String itemIcon) {
        this.traversable = itemIcon.equals(".");
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
