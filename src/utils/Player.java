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

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public void incrementMana() {
        if (manaIncrement < 10) {
            manaIncrement++;
        }
        mana += manaIncrement;
    }

    public void subtractMana(int mana) {
        this.mana -= mana;
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
     *  getting the number of cards in current deck
     */
    public int getCardsInDeckCount() {
        return cardsInDeckCount;
    }

    /**
     *  setting the number of cards in current deck
     */
    public void setCardsInDeckCount(final int cardsInDeckCounts) {
        this.cardsInDeckCount = cardsInDeckCounts;
    }

    /**
     *  getting the number of decks
     */
    public int getDeckCount() {
        return deckCount;
    }

    /**
     *  setting the number of decks
     */
    public void setDeckCount(final int deckCount) {
        this.deckCount = deckCount;
    }

    public void decrementDeckCount() {
        this.deckCount--;
    }

    /**
     *  getting the index for the current deck
     */
    public int getCurrentDeckIndex() {
        return currentDeckIndex;
    }

    /**
     *  setting the index for the current deck
     */
    public void setCurrentDeckIndex(final int currentDeckIndex) {
        this.currentDeckIndex = currentDeckIndex;
    }

    /**
     *  getting the current deck
     */
    public ArrayList<Card> getCurrentDeck() {
        return currentDeck;
    }

    public void setCurrentDeck(final ArrayList<Card> currentDeck) {
        this.currentDeck = currentDeck;
    }

    /**
     *  getting the list of decks
     */
    public ArrayList<ArrayList<Card>> getDeckList() {
        return deckList;
    }

    /**
     *  setting the list of decks
     */
    public void setDeckList(final ArrayList<ArrayList<CardInput>> deckList) {
        this.deckList = new ArrayList<>();
        for (ArrayList<CardInput> deck : deckList) {
            ArrayList<Card> parsedDeck = new ArrayList<>();

            for (CardInput card : deck)
                parsedDeck.add(getParsedCard(card));

            this.deckList.add(parsedDeck);
        }
    }

    public void setDeckList2(final ArrayList<ArrayList<Card>> deckList) {
        this.deckList = deckList;
    }

    /**
     *  getting the current hand of cards
     */
    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    /**
     *  setting the current hand of cards
     */
    public void setCardsInHand(final ArrayList<CardInput> cardsInHand) {
        this.cardsInHand = new ArrayList<>();
        for (CardInput card : cardsInHand) {
            this.cardsInHand.add(getParsedCard(card));
        }
    }

    public void setCardsInHand2(final ArrayList<Card> cardsInHand) {
        this.cardsInHand = cardsInHand;
    }

    /**
     *  getting the front row cards
     */
    public ArrayList<Card> getCardsInFrontRow() {
        return cardsInFrontRow;
    }

    /**
     *  setting the front row cards
     */
    public void setCardsInFrontRow(final ArrayList<CardInput> cardsInFrontRow) {
        this.cardsInFrontRow = new ArrayList<>();
        for (CardInput card : cardsInFrontRow) {
            this.cardsInFrontRow.add(getParsedCard(card));
        }
    }

    public void setCardsInFrontRow2(final ArrayList<Card> cardsInFrontRow) {
        this.cardsInFrontRow = cardsInFrontRow;
    }

    /**
     *  getting the back row cards
     */
    public ArrayList<Card> getCardsInBackRow() {
        return cardsInBackRow;
    }

    /**
     *  getting current hero
     */
    public Card getCurrentHero() {
        return currentHero;
    }

    /**
     *  setting current hero
     */
    public void setCurrentHero(final Card currentHero) {
        this.currentHero = currentHero;
    }

    public void addCardInHand() {
        if (!currentDeck.isEmpty()) {
            cardsInHand.add(currentDeck.get(0));
            currentDeck.remove(0);
        }
    }

    /**
     *
     * @param index of removed card
     */
    public void removeCardFromHand(final int index) {
        cardsInHand.remove(index);
    }

    public void placeCardWithIndex(final int index) {
        if (cardsInHand.isEmpty() || cardsInHand.size() <= index)
            return;

        Card card = cardsInHand.get(index);

        String cardName = card.getName();
        if (isCardEligibleForFrontRow(cardName))
            cardsInFrontRow.add(card);

        if (isCardEligibleForBackRow(cardName))
            cardsInBackRow.add(card);

        subtractMana(card.getMana());

        System.out.println("Placed card " + card.getName() + ".");

        removeCardFromHand(index);
    }

    public boolean isCardEligibleForFrontRow(String cardName) {
        return (cardName.matches(CardNames.THE_RIPPER) || cardName.matches(CardNames.MIRAJ) ||
                cardName.matches(CardNames.GOLIATH) || cardName.matches(CardNames.WARDEN)) &&
                cardsInFrontRow.size() < Constants.FIVE;
    }

    public boolean isCardEligibleForBackRow(String cardName) {
        return (cardName.matches(CardNames.SENTINEL) || cardName.matches(CardNames.BERSERKER) ||
                cardName.matches(CardNames.THE_CURSED_ONE) || cardName.matches(CardNames.DISCIPLE)) &&
                cardsInBackRow.size() < Constants.FIVE;
    }

    public boolean isTheRowFullForCard(String cardName) {
        return ((cardName.matches(CardNames.THE_RIPPER) || cardName.matches(CardNames.MIRAJ) ||
                cardName.matches(CardNames.GOLIATH) || cardName.matches(CardNames.WARDEN)) &&
                cardsInFrontRow.size() >= Constants.FIVE) ||
                ((cardName.matches(CardNames.SENTINEL) || cardName.matches(CardNames.BERSERKER) ||
                cardName.matches(CardNames.THE_CURSED_ONE) || cardName.matches(CardNames.DISCIPLE)) &&
                cardsInBackRow.size() >= Constants.FIVE);
    }

    /**
     *  removing a card from the current deck
     */
    public void removeCardFromDeck(final int index) {
        if (!currentDeck.isEmpty() && currentDeck.size() > index)
            currentDeck.remove(index);
    }

    public void useEnvironmentCard(Card card, ArrayList<Card> affectedRow, int row) {

        switch (card.getName()) {
            case CardNames.WINTERFELL -> {
                for (Card affectedCard : affectedRow)
                    affectedCard.setStunned(true);
            }
            case CardNames.FIRESTORM -> {
                for (Card affectedCard : affectedRow)
                    affectedCard.setHealth(affectedCard.getHealth() - 1);

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

                ArrayList<Card> newRowForMinion = (row == 0) ? this.cardsInBackRow : this.cardsInFrontRow;
                newRowForMinion.add(bestMinionOutThere);
                affectedRow.remove(bestMinionOutThere);

            }
        }

        subtractMana(card.getMana());
        getCardsInHand().remove(card);
    }

    /**
     *  randomising the deck cards order
     */
    public void shuffleDeck(final String seed) {
        Random random = new Random(Long.parseLong(seed));
        Collections.shuffle(currentDeck, random);
    }

    /**
     *  getting the card's type
     *  1 = MINION
     *  2 = ENVIRONMENT
     *  3 = HERO
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
     *  parsing a card from CardInput to Card
     */
    public Card getParsedCard(final CardInput card) {
        Card parsedCard;

        int cardType = getCardType(card.getName());
        parsedCard = (cardType == 1) ? new Minion()
                   : (cardType == 2) ? new Environment() : new Hero();

        parsedCard.setName(card.getName());
        parsedCard.setHealth((cardType == 3) ? Constants.THIRTY : card.getHealth());
        parsedCard.setMana(card.getMana());
        parsedCard.setAttackDamage(card.getAttackDamage());
        parsedCard.setDescription(card.getDescription());
        parsedCard.setColors(card.getColors());

        return parsedCard;
    }

    public void removeStunFromCards() {
        for (Card card : cardsInBackRow)
            card.setStunned(false);
        for (Card card : cardsInFrontRow)
            card.setStunned(false);
    }

    public boolean checkRowStatus(String row) {
        return (row.matches("backRow") ? cardsInBackRow.size() == Constants.FIVE :
                                                cardsInFrontRow.size() == Constants.FIVE);
    }
}
