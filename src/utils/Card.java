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

    private boolean hasAttackedThisTurn = false;

    /**
     *
     * @return the card's mana
     */
    public int getMana() {
        return mana;
    }

    /**
     *
     * @param mana the card's mana
     */
    public void setMana(final int mana) {
        this.mana = mana;
    }

    /**
     *
     * @return the card's health
     */
    public int getHealth() {
        return health;
    }

    /**
     *
     * @param health the card's health
     */
    public void setHealth(final int health) {
        this.health = health;
    }

    /**
     *
     * @return the card's damage
     */
    public int getAttackDamage() {
        return attackDamage;
    }

    /**
     *
     * @param attackDamage the card's damage
     */
    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    /**
     *
     * @return the card's description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description the card's description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     *
     * @return the palette of the card
     */
    public ArrayList<String> getColors() {
        return colors;
    }

    /**
     *
     * @param colors the palette of the card
     */
    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    /**
     *
     * @return the card's name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name the card's name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     *
    * @return true      if card is stunned
     *        false     otherwise
     */
    public boolean isStunned() {
        return stunned;
    }

    /**
     *
     * @param stunned true      if card is stunned
     *                false     otherwise
     */
    public void setStunned(final boolean stunned) {
        this.stunned = stunned;
    }

    /**
     *
     * @return      true        if the card has attacked once this turn
     *              false       otherwise
     */
    public boolean hasAttackedThisTurn() {
        return hasAttackedThisTurn;
    }

    /**
     *
     * @param hasAttackedThisTurn       true        if the card has attacked once this turn
 *                                      false       otherwise
     */
    public void setHasAttackedThisTurn(boolean hasAttackedThisTurn) {
        this.hasAttackedThisTurn = hasAttackedThisTurn;
    }
}
