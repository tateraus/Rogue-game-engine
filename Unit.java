package rogueProject;

/**
 * A derived abstract class from the Entity class, it describes the shared attributes and functions of player and
 * monsters in the game world
 * @author: Yingxue Chen, yingxuec1@student.unimelb.edu.au, 1210613
 */

public class Unit extends Entity {
    private String name;
    private int damage;
    private int maxHealth;
    private int curHealth;

    /**
     * contructors
     * @param name
     */
    public Unit(String name) {
        setName(name);
    }

    public Unit(String name, int maxHealth, int damage) {
        setName(name);
        setMaxHealth(maxHealth);
        setDamage(damage);
        setCurHealth(maxHealth);
    }

    /**
     * getters and setters
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getCurHealth() {
        return curHealth;
    }

    public void setCurHealth(int curHealth) {
        this.curHealth = curHealth;
    }


    /**
     * To concatenate an entity's name, current health and max health into a string
     */
    public String toString() {
        return this.getName() + " " + this.getCurHealth() + "/" + this.getMaxHealth();
    }


}
