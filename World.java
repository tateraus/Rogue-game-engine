package rogueProject;

/**
 * A class for generating the game world, game battles and game related entities setting happen within this class.
 * @author: Yingxue Chen, yingxuec1@student.unimelb.edu.au, 1210613
 */
import java.util.ArrayList;

/**
 * A class for representing the game world with player and monster in the game, Rogue
 * @author: Yingxue Chen, yingxuec1@student.unimelb.edu.au, 1210613
 **/

public class World {
    private Map myCurrentMap;
    private Player player;
    private Monster monster;
    private ArrayList<Monster> monsterInfoInFile = new ArrayList<>();
    private ArrayList<Item> itemInfoInFile = new ArrayList<>();

    private final int MONSTER_DEFAULT_X = 4;
    private final int MONSTER_DEFAULT_Y = 2;
    private final int PLAYER_DEFAULT_X = 1;
    private final int PLAYER_DEFAULT_Y = 1;


    /**
     *
     * @param
     * @param player
     * @param monsterInfoInFile
     * @param itemInfoInFile
     */
    public World(Map map, Player player, ArrayList<Monster> monsterInfoInFile, ArrayList<Item> itemInfoInFile) {
        this.myCurrentMap = map;
        this.player = player;
        this.monsterInfoInFile = monsterInfoInFile;
        this.itemInfoInFile = itemInfoInFile;
    }

    /**
     *  world constructor for default map
     */
    public World (Player player, Monster monster, Map map) {
        player.setX(PLAYER_DEFAULT_X);
        player.setY(PLAYER_DEFAULT_Y);
        monster.setX(MONSTER_DEFAULT_X);
        monster.setY(MONSTER_DEFAULT_Y);
        this.player = player;
        this.monster = monster;
        this.myCurrentMap = map;

    }

    /**
     * getters and setters
     */
    public void setMyCurrentMap(Map map) {
        this.myCurrentMap = map;
    }

    public ArrayList<Monster> getMonsterInfoInFile() {
        return monsterInfoInFile;
    }

    public ArrayList<Item> getItemInfoInFile() {
        return itemInfoInFile;
    }

    /**
     * To generate a deepcopy from the original map so that the original not get changed
     * @return a deepcopy of a 2-dimensional ArrayList of the world map
     */
    public ArrayList<ArrayList<Entity>> deepCopyList() {
        ArrayList<ArrayList<Entity>> originalMap = myCurrentMap.getOriginalMapList();
        ArrayList<ArrayList<Entity>> copyOriginalMap = new ArrayList<>();
        for (ArrayList<Entity> listElem: originalMap) {
            ArrayList<Entity> tempInnerList = new ArrayList<>();
            for (Entity element: listElem) {
                tempInnerList.add(element);
            }
            copyOriginalMap.add(tempInnerList);
        }
        return copyOriginalMap;
    }

    /**
     * To check if player's move valid and update the player's coordinates
     * @param move
     */
    public void playerMoveUpdateXy(String move) {
        int playerX = player.getX(); int playerY = player.getY();
        switch (move) {
            case "w":
                playerY -= 1;
                break;
            case "s":
                playerY += 1;
                break;
            case "a":
                playerX -= 1;
                break;
            case "d":
                playerX += 1;
                break;
        }
        if (myCurrentMap.canTraverse(playerX, playerY)) { // if there is a "." at the (x,y) in the original map then
            // it is traversable and we can set the player to the new spot
            player.setX(playerX);
            player.setY(playerY);
        }
    }


    /**
     * To insert the player and monster updated coordinates into the map in the default game, and check if the player
     * encounters the monster
     * @return true if encounters, false otherwise
     */
    public boolean insertPlayerMonsterDefaultGame() {
        boolean encounter = false;
        if (player.getX() == monster.getX() && player.getY() == monster.getY()) {
            encounter = true;
        } else {
            myCurrentMap.getOriginalMapList().get(player.getY()).set(player.getX(), player);
            myCurrentMap.getOriginalMapList().get(monster.getY()).set(monster.getX(), monster);
        }
        return encounter;
    }

    /**
     * To insert entities with updated coordinates onto the map in the file based game
     * @return an updated map which is editable and will not affect the original map
     */
    public ArrayList<ArrayList<Entity>> insertEntities() {
        ArrayList<ArrayList<Entity>> copyMap = deepCopyList();
        // insert player into its updated place if allowed
        if (copyMap.get(player.getY()).get(player.getX()).isTraversable()) {
            copyMap.get(player.getY()).set(player.getX(), player);
        }
        // insert monsters into updated places which is not occupied by the player
        for (Monster eachMonster: monsterInfoInFile) {
            if (copyMap.get(eachMonster.getY()).get(eachMonster.getX()).isTraversable()) {
                // only "." is allowing for insertion
                copyMap.get(eachMonster.getY()).set(eachMonster.getX(), eachMonster);
            }
        }
        // insert items into updated places which are not occupied by the player and monsters
        for (Item eachItem: itemInfoInFile) {
            if (copyMap.get(eachItem.getY()).get(eachItem.getX()).isTraversable()) {
                copyMap.get(eachItem.getY()).set(eachItem.getX(), eachItem);
                }
            }
        return copyMap;
    }

    /**
     * To update monsters coordinates based on the player's position
     */
    public void monsterMoveUpdateXy() {
        int playerX = player.getX(); int playerY = player.getY();
        for (Monster eachMonster: monsterInfoInFile) {
            int monsterX = eachMonster.getX(); int monsterY = eachMonster.getY();
            // monsters only move when player is within 2 cells
            if (Math.abs(playerX - monsterX) <= 2 && Math.abs(playerY - monsterY) <= 2) {
                // if the player and a monster is in the same column
                if (playerX == monsterX && monsterY > playerY) {
                    monsterY -= 1;
                } else if (playerX == monsterX && monsterY < playerY) {
                    monsterY += 1;
                    // if the player and a monster is in the same row
                } else if (playerY == monsterY && playerX < monsterX) {
                    monsterX -= 1;
                } else if (playerY == monsterY && playerX > monsterX) {
                    monsterX += 1;
                    // if the player is in the upper left quadrant
                } else if (playerX < monsterX && playerY < monsterY) {
                    // monster tend to move horizontally first
                    if (myCurrentMap.canTraverse(monsterX - 1, monsterY)) {
                        monsterX -= 1;
                    } else if (myCurrentMap.canTraverse(monsterX, monsterY - 1)) {
                        monsterY -= 1;
                    }
                    // if the player is in the lower left quadrant
                } else if (playerX < monsterX) {
                    if (myCurrentMap.canTraverse(monsterX - 1, monsterY)) {
                        monsterX -= 1;
                    } else if (myCurrentMap.canTraverse(monsterX, monsterY + 1)) {
                        monsterY += 1;
                    }
                    // if the player is in the upper right quadrant
                } else if (playerX > monsterX && playerY < monsterY) {
                    if (myCurrentMap.canTraverse(monsterX + 1, monsterY)) {
                        monsterX += 1;
                    } else if (myCurrentMap.canTraverse(monsterX, monsterY - 1)) {
                        monsterY -= 1;
                    }
                    // if the player is in the lower right quadrant
                } else {
                    if (myCurrentMap.canTraverse(monsterX + 1, monsterY)) {
                        monsterX += 1;
                    } else if (myCurrentMap.canTraverse(monsterX, monsterY + 1)){
                        monsterY += 1;
                    }
                }
            }
            // check whether the set is valid before set the monster coordinates
            if (myCurrentMap.canTraverse(monsterX, monsterY)) {
                eachMonster.setX(monsterX);
                eachMonster.setY(monsterY);
            }
        }
    }
}



