package rogueProject;

/**
 * A derived class from Entity for representing the player configured in the game world with additional attributes.
 * @author:  Yingxue Chen, yingxuec1@student.unimelb.edu.au, 1210613
 **/

public class Player extends Unit {
    private int level;
    public static final int MAX_HEALTH_MULTIPLIER = 3; // public static final?
    public static final int MAX_HEALTH_INCREMENT = 17; //change to public??


    /**
     * constructors
     */
    public Player(String name) {
        super(name);
        setLevel(1);
        int playerMaxHealth = level * MAX_HEALTH_MULTIPLIER + MAX_HEALTH_INCREMENT;
        int playerDamage = level + 1;
        setX(1);
        setY(1);
        setMaxHealth(playerMaxHealth);
        setCurHealth(playerMaxHealth);
        setDamage(playerDamage);
        setIcon(name.substring(0, 1).toUpperCase());
    }

    public Player(String name, int level, int playerX, int playerY) {
        super(name);
        int playerMaxHealth = level * MAX_HEALTH_MULTIPLIER + MAX_HEALTH_INCREMENT;
        setLevel(level);
        setX(playerX);
        setY(playerY);
        setMaxHealth(playerMaxHealth);
        setCurHealth(playerMaxHealth);
        setDamage(level + 1);
        setIcon(name.substring(0, 1).toUpperCase());
    }

    /**
     * getter and setters
     */
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    /**
     * Override the inherited getIcon method, to make the icon uppercase
     */
    public String getIcon() {
        return super.getIcon().toUpperCase();
    }
}
