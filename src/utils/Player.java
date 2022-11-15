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

    public Player() {}

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getCardsInDeckCount() {
        return cardsInDeckCount;
    }

    public void setCardsInDeckCount(int cardsInDeckCounts) {
        this.cardsInDeckCount = cardsInDeckCounts;
    }

    public int getDeckCount() {
        return deckCount;
    }

    public void setDeckCount(int deckCount) {
        this.deckCount = deckCount;
    }

    public int getCurrentDeckIndex() {
        return currentDeckIndex;
    }

    public void setCurrentDeckIndex(int currentDeckIndex) {
        this.currentDeckIndex = currentDeckIndex;
    }

    public ArrayList<Card> getCurrentDeck() {
        return currentDeck;
    }

    public void setCurrentDeck(ArrayList<CardInput> currentDeck) {
        this.currentDeck = new ArrayList<Card>();
        for (CardInput card : currentDeck) {
            this.currentDeck.add(getParsedCard(card));
        }
    }

    public ArrayList<ArrayList<Card>> getDeckList() {
        return deckList;
    }

    public void setDeckList(ArrayList<ArrayList<CardInput>> deckList) {
        this.deckList = new ArrayList<ArrayList<Card>>();
        for (ArrayList<CardInput> deck : deckList) {
            ArrayList<Card> parsedDeck = new ArrayList<>();

            for (CardInput card : deck) {
                parsedDeck.add(getParsedCard(card));
            }

            this.deckList.add(parsedDeck);
        }
    }

    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    public void setCardsInHand(ArrayList<CardInput> cardsInHand) {
        this.cardsInHand = new ArrayList<Card>();
        for (CardInput card : cardsInHand) {
            this.cardsInHand.add(getParsedCard(card));
        }
    }

    public ArrayList<Card> getCardsInFrontRow() {
        return cardsInFrontRow;
    }

    public void setCardsInFrontRow(ArrayList<CardInput> cardsInFrontRow) {
        this.cardsInFrontRow = new ArrayList<Card>();
        for (CardInput card : cardsInFrontRow) {
            this.cardsInFrontRow.add(getParsedCard(card));
        }
    }

    public ArrayList<Card> getCardsInBackRow() {
        return cardsInBackRow;
    }

    public void setCardsInBackRow(ArrayList<CardInput> cardsInBackRow) {
        this.cardsInBackRow = new ArrayList<Card>();
        for (CardInput card : cardsInBackRow) {
            this.cardsInBackRow.add(getParsedCard(card));
        }
    }

    public Card getCurrentHero() {
        return currentHero;
    }

    public void setCurrentHero(CardInput currentHero) {
        this.currentHero = getParsedCard(currentHero);
    }

    public void addCardInHand(CardInput card) {
        cardsInHand.add(getParsedCard(card));
    }

    public void removeCardFromHand(CardInput card) {
        cardsInHand.remove(getParsedCard(card));
    }

    public void removeCardFromDeck(int index) {
        currentDeck.remove(index);
    }

    public void shuffleDeck(String seed) {
        Random random = new Random(Long.parseLong(seed));
        Collections.shuffle(currentDeck, random);
    }

    public int getCardType(String name) {
        int type = 0;
        switch (name) {
            case CardNames.SENTINEL:
                type = 1;
                break;
            case CardNames.BERSERKER:
                type = 1;
                break;
            case CardNames.GOLIATH:
                type = 1;
                break;
            case CardNames.WARDEN:
                type = 1;
                break;
            case CardNames.THE_RIPPER:
                type = 1;
                break;
            case CardNames.MIRAJ:
                type = 1;
                break;
            case CardNames.THE_CURSED_ONE:
                type = 1;
                break;
            case CardNames.DISCIPLE:
                type = 1;
                break;
            case CardNames.FIRESTORM:L:
            type = 2;
                break;
            case CardNames.WINTERFELL:
                type = 2;
                break;
            case CardNames.HEART_HOUND:
                type = 2;
                break;
            case CardNames.LORD_ROYCE:
                type = 3;
                break;
            case CardNames.EMPRESS_THORINA:
                type = 3;
                break;
            case CardNames.KING_MUDFACE:
                type = 3;
                break;
            case CardNames.GENERAL_KOCIORAW:
                type = 3;
                break;
        }

        return type;
    }

    public Card getParsedCard(CardInput card) {
        Card parsedCard;

        int cardType = getCardType(card.getName());
        parsedCard = (cardType == 1) ? new Minion(card.getHealth(), card.getAttackDamage()) : (cardType == 2) ? new Environment() : new Hero();

        parsedCard.setName(card.getName());
        parsedCard.setMana(card.getMana());
        parsedCard.setDescription(card.getDescription());
        parsedCard.setColors(card.getColors());

        return parsedCard;
    }
}
