package utils;

import java.util.ArrayList;

public abstract class Card {
    private int mana;

    private int health;

    private int attackDamage;

    private String description;

    private ArrayList<String> colors;

    private String name;

    private boolean stunned = false;

    /**
     *  getting the card's mana
     */
    public int getMana() {
        return mana;
    }

    /**
     *  setting the card's mana
     */
    public void setMana(final int mana) {
        this.mana = mana;
    }

    /**
     *  getting a card's description
     */


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

    public String getDescription() {
        return description;
    }

    /**
     *  setting a card's description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     *  getting a card's color palette
     */
    public ArrayList<String> getColors() {
        return colors;
    }

    /**
     *  setting a card's color palette
     */
    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    /**
     *  getting a card's name
     */
    public String getName() {
        return name;
    }

    /**
     *  setting a card's name
     */
    public void setName(final String name) {
        this.name = name;
    }

    public boolean isStunned() {
        return stunned;
    }

    public void setStunned(boolean stunned) {
        this.stunned = stunned;
    }
}
