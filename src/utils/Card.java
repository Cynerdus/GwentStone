package utils;

import java.util.ArrayList;

public abstract class Card {
    private int mana;

    private String description;

    private ArrayList<String> colors;

    private String name;

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
}