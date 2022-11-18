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
        this.deckList = new ArrayList<ArrayList<Card>>();
        for (ArrayList<CardInput> deck : deckList) {
            ArrayList<Card> parsedDeck = new ArrayList<>();

            for (CardInput card : deck) {
                parsedDeck.add(getParsedCard(card));
            }

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
        this.cardsInHand = new ArrayList<Card>();
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
        this.cardsInFrontRow = new ArrayList<Card>();
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
     *  setting the back row cards
     */
    public void setCardsInBackRow(final ArrayList<CardInput> cardsInBackRow) {
        this.cardsInBackRow = new ArrayList<Card>();
        for (CardInput card : cardsInBackRow) {
            this.cardsInBackRow.add(getParsedCard(card));
        }
    }

    public void setCardsInBackRow2(final ArrayList<Card> cardsInBackRow) {
        this.cardsInBackRow = cardsInBackRow;
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
    public void removeCardFromHand(final Card card) {
        if (!cardsInHand.isEmpty() && cardsInHand.contains(card)
            && mana >= card.getMana()) {
            subtractMana(card.getMana());
            cardsInHand.remove(card);
        }
    }

    /**
     *
     * @param index of removed card
     */
    public void removeCardFromHand(final int index) {
        if (!cardsInHand.isEmpty() && cardsInHand.size() > index
            && mana >= cardsInHand.get(index).getMana()) {
            subtractMana(cardsInHand.get(index).getMana());
            String cardName = cardsInHand.get(index).getName();

            if (isCardEligibleForFrontRow(cardName))
                cardsInFrontRow.add(cardsInHand.get(index));

            if (isCardEligibleForBackRow(cardName))
                cardsInBackRow.add(cardsInHand.get(index));

            System.out.println("Placed card " + cardsInHand.get(index).getName() + ".");
            cardsInHand.remove(index);
            return;
        }

        if (!cardsInHand.isEmpty() && cardsInHand.size() > index)
            System.out.println("Not enough mana! Current: " + mana + " | Required: " + cardsInHand.get(index).getMana());
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
        int type;
        switch (name) {
            case CardNames.SENTINEL:
            case CardNames.GOLIATH:
            case CardNames.BERSERKER:
            case CardNames.WARDEN:
            case CardNames.THE_RIPPER:
            case CardNames.MIRAJ:
            case CardNames.THE_CURSED_ONE:
            case CardNames.DISCIPLE:
                type = Constants.ONE;
                break;
            case CardNames.FIRESTORM:
            case CardNames.WINTERFELL:
            case CardNames.HEART_HOUND:
                type = Constants.TWO;
                break;
            case CardNames.LORD_ROYCE:
            case CardNames.EMPRESS_THORINA:
            case CardNames.KING_MUDFACE:
            case CardNames.GENERAL_KOCIORAW:
                type = Constants.THREE;
                break;
            default:
                type = Constants.ZERO;
        }

        return type;
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
}
