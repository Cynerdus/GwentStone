package utils;

import fileio.CardInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Player {
    private int gamesPlayed = 0;

    private Card currentHero;

    private int mana;
    private int manaIncrement = 1;

    private int deckCount;
    private int cardsInDeckCount;
    private int currentDeckIndex;
    private ArrayList<Card> currentDeck;
    private ArrayList<ArrayList<Card>> deckList;

    private ArrayList<Card> cardsInHand = new ArrayList<>();
    private ArrayList<Card> cardsInFrontRow = new ArrayList<>();
    private ArrayList<Card> cardsInBackRow = new ArrayList<>();

    public Player() { }

    /**
     *
     * @return player's mana
     */
    public int getMana() {
        return mana;
    }

    /**
     *
     * @param mana player's mana
     */
    public void setMana(final int mana) {
        this.mana = mana;
    }

    /**
     * increments the player's mana every turn
     */
    public void incrementMana() {
        if (manaIncrement < Constants.TEN) {
            manaIncrement++;
        }
        mana += manaIncrement;
    }

    public void setManaIncrement(final int manaIncrement) {
        this.manaIncrement = manaIncrement;
    }

    /**
     *
     * @param consumedMana mana to be subtracted
     */
    public void subtractMana(final int consumedMana) {
        this.mana -= consumedMana;
    }

    /**
     *
     * @return the number of game sessions
     */
    public int getGamesPlayed() {
        return gamesPlayed;
    }

    /**
     *
     * @param gamesPlayed the number of games sessions
     */
    public void setGamesPlayed(final int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    /**
     *
     * @param cardsInDeckCounts number of cards per deck
     */
    public void setCardsInDeckCount(final int cardsInDeckCounts) {
        this.cardsInDeckCount = cardsInDeckCounts;
    }

    /**
     *
     * @param deckCount number of decks
     */
    public void setDeckCount(final int deckCount) {
        this.deckCount = deckCount;
    }

    /**
     *
     * @return the index of the current deck
     */
    public int getCurrentDeckIndex() {
        return currentDeckIndex;
    }

    /**
     *
     * @param currentDeckIndex the index of the current deck
     */
    public void setCurrentDeckIndex(final int currentDeckIndex) {
        this.currentDeckIndex = currentDeckIndex;
    }

    /**
     *
     * @return the current deck
     */
    public ArrayList<Card> getCurrentDeck() {
        return currentDeck;
    }

    /**
     *
     * @param currentDeck the current deck in Card format
     */
    public void setCurrentDeck(final ArrayList<Card> currentDeck) {
        this.currentDeck = currentDeck;
    }

    /**
     *
     * @return the list of decks in Card format
     */
    public ArrayList<ArrayList<Card>> getDeckList() {
        return deckList;
    }

    /**
     * the method will take the deck list in CardInput format
     * and convert it to list of lists of Card
     *
     * @param deckList the list of decks in CardInput format
     */
    public void setDeckList(final ArrayList<ArrayList<CardInput>> deckList) {
        this.deckList = new ArrayList<>();
        for (ArrayList<CardInput> deck : deckList) {
            ArrayList<Card> parsedDeck = new ArrayList<>();

            for (CardInput card : deck) {
                parsedDeck.add(getParsedCard(card));
            }

            this.deckList.add(parsedDeck);
        }
    }

    public void resetCards() {
        cardsInHand = new ArrayList<>();
        cardsInFrontRow = new ArrayList<>();
        cardsInBackRow = new ArrayList<>();
    }

    /**
     *
     * @return the list of cards in the player's hand
     */
    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    /**
     *
     * @return the list of cards in the front row
     */
    public ArrayList<Card> getCardsInFrontRow() {
        return cardsInFrontRow;
    }

    /**
     *
     * @return the list of cards in the back row
     */
    public ArrayList<Card> getCardsInBackRow() {
        return cardsInBackRow;
    }

    /**
     *
     * @return the current Hero for the game
     */
    public Card getCurrentHero() {
        return currentHero;
    }

    /**
     *
     * @param currentHero the current Hero for the game
     */
    public void setCurrentHero(final Card currentHero) {
        this.currentHero = currentHero;
    }

    /**
     * method to complete the procedure of adding a card to the
     * player's hand
     */
    public void addCardInHand() {
        if (!currentDeck.isEmpty()) {
            cardsInHand.add(currentDeck.get(Constants.ZERO));
            removeCardFromDeck(Constants.ZERO);
        }
    }

    /**
     *
     * @param index of removed card
     */
    public void removeCardFromHand(final int index) {
        cardsInHand.remove(index);
    }

    /**
     *
     * @param index the card's index
     */
    public void placeCardWithIndex(final int index) {
        if (cardsInHand.isEmpty() || cardsInHand.size() <= index) {
            return;
        }

        Card card = cardsInHand.get(index);

        String cardName = card.getName();
        if (isCardEligibleForFrontRow(cardName)) {
            cardsInFrontRow.add(card);
        }

        if (isCardEligibleForBackRow(cardName)) {
            cardsInBackRow.add(card);
        }

        subtractMana(card.getMana());

        System.out.println("Placed card " + card.getName() + ".");

        removeCardFromHand(index);
    }

    /**
     *
     * @param cardName the name of the card
     * @return true     if the front row has slots open
     *         false    otherwise
     */
    public boolean isCardEligibleForFrontRow(final String cardName) {
        return (cardName.matches(CardNames.THE_RIPPER)
                || cardName.matches(CardNames.MIRAJ)
                || cardName.matches(CardNames.GOLIATH)
                || cardName.matches(CardNames.WARDEN))
                && cardsInFrontRow.size() < Constants.FIVE;
    }

    /**
     *
     * @param cardName the name of the card
     * @return true     if the back row has slots open
     *         false    otherwise
     */
    public boolean isCardEligibleForBackRow(final String cardName) {
        return (cardName.matches(CardNames.SENTINEL)
                || cardName.matches(CardNames.BERSERKER)
                || cardName.matches(CardNames.THE_CURSED_ONE)
                || cardName.matches(CardNames.DISCIPLE))
                && cardsInBackRow.size() < Constants.FIVE;
    }

    /**
     *
     * @param cardName the name of the card
     * @return true     if the front/back row is indeed full
     *         false    otherwise
     */
    public boolean isTheRowFullForCard(final String cardName) {
        return ((cardName.matches(CardNames.THE_RIPPER)
                || cardName.matches(CardNames.MIRAJ)
                || cardName.matches(CardNames.GOLIATH)
                || cardName.matches(CardNames.WARDEN))
                    && cardsInFrontRow.size() >= Constants.FIVE)
                || ((cardName.matches(CardNames.SENTINEL)
                || cardName.matches(CardNames.BERSERKER)
                || cardName.matches(CardNames.THE_CURSED_ONE)
                || cardName.matches(CardNames.DISCIPLE))
                    && cardsInBackRow.size() >= Constants.FIVE);
    }

    /**
     *
     * @param index the index of the card to be removed
     */
    public void removeCardFromDeck(final int index) {
        if (!currentDeck.isEmpty() && currentDeck.size() > index) {
            currentDeck.remove(index);
        }
    }

    /**
     *  method to apply the Environment card.
     * Only to be used after checking for exceptions!
     *
     * @param card the Environment card
     * @param affectedRow target row to apply the card's effect
     * @param row the row's index on the table
     */
    public void useEnvironmentCard(final Card card,
                                   final ArrayList<Card> affectedRow,
                                   final int row) {

        switch (card.getName()) {
            case CardNames.WINTERFELL -> {
                for (Card affectedCard : affectedRow) {
                    affectedCard.setStunned(true);
                }
            }
            case CardNames.FIRESTORM -> {
                for (Card affectedCard : affectedRow) {
                    affectedCard.setHealth(affectedCard.getHealth() - 1);
                }

                affectedRow.removeIf(affectedCard -> affectedCard.getHealth() <= 0);
            }
            case CardNames.HEART_HOUND -> {
                Card bestMinionOutThere = new Minion();
                int thisDudesHealth = 0;

                for (Card affectedCard : affectedRow) {
                    if (getCardType(affectedCard.getName()) == 1) {
                        if (affectedCard.getHealth() > thisDudesHealth) {
                            thisDudesHealth = affectedCard.getHealth();
                            bestMinionOutThere = affectedCard;
                        }
                    }
                }

                ArrayList<Card> newRowForMinion = (row == 0) ? this.cardsInBackRow
                                                             : this.cardsInFrontRow;
                newRowForMinion.add(bestMinionOutThere);
                affectedRow.remove(bestMinionOutThere);

            }
            default -> { }
        }

        subtractMana(card.getMana());
        getCardsInHand().remove(card);
    }

    /**
     *
     * @param seed given seed for standard shuffle
     */
    public void shuffleDeck(final String seed) {
        Random random = new Random(Long.parseLong(seed));
        Collections.shuffle(currentDeck, random);
    }

    /**
     *
     * @param name name of the card
     * @return card's type, as it follows:
     *          1 for MINION;
     *          2 for ENVIRONMENT;
     *          3 for HERO.
     */
    public int getCardType(final String name) {
        return switch (name) {
            case
        CardNames.SENTINEL,             CardNames.GOLIATH,

    CardNames.BERSERKER, CardNames.WARDEN, CardNames.THE_RIPPER,

    CardNames.MIRAJ, CardNames.THE_CURSED_ONE, CardNames.DISCIPLE ->

     Constants.ONE; case CardNames.FIRESTORM, CardNames.WINTERFELL,

        CardNames.HEART_HOUND -> Constants.TWO; case /*uwuwuw*/

            CardNames.LORD_ROYCE, CardNames.KING_MUDFACE,

                CardNames.EMPRESS_THORINA,  /*uwu*/

                    CardNames.GENERAL_KOCIORAW ->

                        Constants.THREE;

                            default ->
                          Constants.ZERO;
        };
    }

    /**
     *
     * @param card CardInput instance to be converted to Card
     * @return the parsed card
     */
    public Card getParsedCard(final CardInput card) {
        Card parsedCard;

        int cardType = getCardType(card.getName());
        parsedCard = (cardType == Constants.ONE) ? new Minion()
                   : (cardType == Constants.TWO) ? new Environment() : new Hero();

        parsedCard.setName(card.getName());
        parsedCard.setHealth((cardType == Constants.THREE) ? Constants.THIRTY : card.getHealth());
        parsedCard.setMana(card.getMana());
        parsedCard.setAttackDamage(card.getAttackDamage());
        parsedCard.setDescription(card.getDescription());
        parsedCard.setColors(card.getColors());

        return parsedCard;
    }

    /**
     *  remove the stunned label from all cards
     */
    public void removeStunFromCards() {

        for (Card card : cardsInBackRow) {
            card.setStunned(false);
        }

        for (Card card : cardsInFrontRow) {
            card.setStunned(false);
        }
    }

    /**
     *  resets the attack cool down on all cards
     */
    public void resetCardAttacks() {
        for (Card card : cardsInBackRow) {
            card.setHasAttackedThisTurn(false);
        }

        for (Card card : cardsInFrontRow) {
            card.setHasAttackedThisTurn(false);
        }
    }

    /**
     *
     * @param row string to designate which row to check ("backRow" / "frontRow")
     * @return true     if the row has reached its maximum capacity
     *         false    otherwise
     */
    public boolean checkRowStatus(final String row) {
        return (row.matches("backRow") ? cardsInBackRow.size() == Constants.FIVE
                                             : cardsInFrontRow.size() == Constants.FIVE);
    }

    public void replaceDeck(ArrayList<Card> deck) {
        ArrayList<Card> newDeck = new ArrayList<>();

        for (Card card : deck) {
            Card newCard = (getCardType(card.getName()) == 1 ? new Minion() : new Environment());
            newCard.setName(card.getName());
            newCard.setMana(card.getMana());
            newCard.setHealth(card.getHealth());
            newCard.setDescription(card.getDescription());
            newCard.setColors(card.getColors());
            newCard.setAttackDamage(card.getAttackDamage());

            newDeck.add(newCard);
        }

        this.currentDeck = newDeck;
    }
}
