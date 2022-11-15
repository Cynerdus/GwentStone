package utils;

public class Hero extends Card {

    private int health = Constants.THIRTY;

    public Hero() { }

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
}
