package rogueProject;

/**
 * A derived class from Entity for representing the monster configured in the game world with additional attributes.
 * @author:  Yingxue Chen, yingxuec1@student.unimelb.edu.au, 1210613
 **/

public class Monster extends Unit {

    /**
     * constructors
     */
    public Monster(String name, int maxHealth, int damage) {
        super(name, damage, maxHealth);
        int monsterX = 4;
        int monsterY = 2;
        setX(monsterX);
        setY(monsterY);
        setIcon(name.substring(0, 1).toLowerCase());
    }

    public Monster(String name, int monsterX, int monsterY, int maxHealth, int damage) {
        super(name, maxHealth, damage);
        setX(monsterX);
        setY(monsterY);
        setIcon(name.substring(0, 1).toLowerCase());
    }

    @Override
    /**
     * To override the inherited getIcon method to get the lowercase
     */
    public String getIcon() {
        return super.getIcon().toLowerCase();
    }
}

