package utils;

import fileio.CardInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Player {
    private int gamesPlayed = 0;

    private Card currentHero;

    private int cardsInDeckCount;
    private int deckCount;
    private int currentDeckIndex;
    private ArrayList<Card> currentDeck;
    private ArrayList<ArrayList<Card>> deckList;

    private ArrayList<Card> cardsInHand;
    private ArrayList<Card> cardsInFrontRow;
    private ArrayList<Card> cardsInBackRow;

    public Player() { }

    /**
     *  getting the number of games played
     */
    public int getGamesPlayed() {
        return gamesPlayed;
    }

    /**
     *  setting the number of games played
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

    /**
     *  setting the current deck
     */
    public void setCurrentDeck(final ArrayList<CardInput> currentDeck) {
        this.currentDeck = new ArrayList<Card>();
        for (CardInput card : currentDeck) {
            this.currentDeck.add(getParsedCard(card));
        }
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

    /**
     *  getting current hero
     */
    public Card getCurrentHero() {
        return currentHero;
    }

    /**
     *  setting current hero
     */
    public void setCurrentHero(final CardInput currentHero) {
        this.currentHero = getParsedCard(currentHero);
    }

    /**
     *  adding a card in the hand
     */
    public void addCardInHand(final CardInput card) {
        cardsInHand.add(getParsedCard(card));
    }

    /**
     *  removing a card from the hand
     */
    public void removeCardFromHand(final CardInput card) {
        cardsInHand.remove(getParsedCard(card));
    }

    /**
     *  removing a card from the current deck
     */
    public void removeCardFromDeck(final int index) {
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
        parsedCard = (cardType == 1) ? new Minion(card.getHealth(), card.getAttackDamage())
                   : (cardType == 2) ? new Environment() : new Hero();

        parsedCard.setName(card.getName());
        parsedCard.setMana(card.getMana());
        parsedCard.setDescription(card.getDescription());
        parsedCard.setColors(card.getColors());

        return parsedCard;
    }
}
