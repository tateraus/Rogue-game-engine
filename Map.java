package rogueProject;

/**
 * A class for the map used in the game world, including the map dimensions and map terrain items
 * @author: Yingxue Chen, yingxuec1@student.unimelb.edu.au, 1210613
 */
import java.util.ArrayList;

public class Map {
    private int mapWidth;
    private int mapHeight;
    private ArrayList<ArrayList<Entity>> originalMapList;

    /**
     * constructor for default map
     */
    public Map() {
        int defaultWidth = 6;
        int defaultHeight = 4;
        mapWidth = defaultWidth;
        mapHeight = defaultHeight;
        this.originalMapList = new ArrayList<>();
    }

    /**
     * file structured map constructor
     * @param mapWidth
     * @param mapHeight
     */
    public Map(int mapWidth,int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.originalMapList = new ArrayList<>();
    }

    /**
     * getter and setter
     * @return
     */
    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public ArrayList<ArrayList<Entity>> getOriginalMapList() {
        return originalMapList;
    }

    public void setOriginalMapList(ArrayList<Entity> innerList) {
        originalMapList.add(innerList);
    }

    /**
     * To check whether a spot is traversable with given x and y
     * @param x
     * @param y
     * @return true if it is traversable, false otherwise
     */
    public boolean canTraverse (int x, int y) {
        // if the coordinates is outside of the map boundary, then obviously not traversable
        if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight) {
            return false;
        }
        // then check whether the spot is ".", which means traversable
        else return originalMapList.get(y).get(x).isTraversable();
    }
}


