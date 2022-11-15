package utils;

public class Minion extends Card {

    private int health;

    private int attackDamage;

    public Minion(final int health, final int attackDamage) {
        this.health = health;
        this.attackDamage = attackDamage;
    }

    /**
     *  getting the card's health
     */
    public int getHealth() {
        return health;
    }

    /**
     *  setting the card's health
     */
    public void setHealth(final int health) {
        this.health = health;
    }

    /**
     *  getting the attack damage
     */
    public int getAttackDamage() {
        return attackDamage;
    }

    /**
     *  setting the attack damage
     */
    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }
}
