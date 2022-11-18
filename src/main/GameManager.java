package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.*;
import utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class GameManager {
    private final Input inputData;
    private final ArrayNode outputData;

    private Player currentPlayer;
    private final Player player1;
    private final Player player2;

    private GameInput currentSession;

    public GameManager(final Input inputData, final ArrayNode outputData) {
        this.inputData = inputData;
        this.outputData = outputData;

        player1 = playerSetup(1);
        player2 = playerSetup(2);

        generateGameSessions();
    }

    /**
     *  retrieving the output in json
     */
    public ArrayNode generateOutput() {
        return outputData;
    }

    /*      pre-games setup     */
    private Player playerSetup(int playerIndex) {
        Player player = new Player();
        ArrayList<ArrayList<CardInput>> playerDecks = (playerIndex == 1 ) ? inputData.getPlayerOneDecks().getDecks() : inputData.getPlayerTwoDecks().getDecks();

        player.setDeckList(playerDecks);
        player.setDeckCount(playerDecks.size());
        player.setCardsInDeckCount(playerDecks.get(0).size());

        return player;
    }

    /*      simulate the games      */
    private void generateGameSessions() {
        for (GameInput session : inputData.getGames()) {
            currentSession = session;

            sessionSetup(player1);
            sessionSetup(player2);
            System.out.println("Players have been set up!");
            System.out.println("Player1 | mana: " + player1.getMana() + " | hero name: " + player1.getCurrentHero().getName() + " | deck index: " + player1.getCurrentDeckIndex());
            System.out.println("Player2 | mana: " + player2.getMana() + " | hero name: " + player2.getCurrentHero().getName() + " | deck index: " + player2.getCurrentDeckIndex());

            currentPlayer = (session.getStartGame().getStartingPlayer() == 1) ? player1 : player2;

            for (ActionsInput action : session.getActions()) {
                System.out.println("Current player: " + (currentPlayer.equals(player1) ? 1 : 2));
                System.out.println("Mana: " + currentPlayer.getMana());
                System.out.println("Command: " + action.getCommand());

                System.out.print("Deck: ");
                for (Card card : currentPlayer.getCurrentDeck()) {
                    System.out.print(card.getName() + ", ");
                }
                System.out.print("\n");

                System.out.print("Hand: ");
                for (Card card : currentPlayer.getCardsInHand()) {
                    System.out.print(card.getName() + ", ");
                }
                System.out.print("\n");

                System.out.print("Front row: ");
                for (Card card : currentPlayer.getCardsInFrontRow()) {
                    System.out.print(card.getName() + ", ");
                }
                System.out.print("\n");

                System.out.print("Back row: ");
                for (Card card : currentPlayer.getCardsInBackRow()) {
                    System.out.print(card.getName() + ", ");
                }
                System.out.print("\n");

                int commandIndex = getCommandIndex(action.getCommand());
                if (commandIndex < Constants.THIRTEEN)
                    debugCommand(commandIndex, action);
                else
                    actionCommand(commandIndex, action);

            }
        }
    }

    private void sessionSetup(Player player) {
        System.out.println("Setting up player " + (player.equals(player1) ? 1 : 2) + " ...");

        int playerOneIndex = currentSession.getStartGame().getPlayerOneDeckIdx();
        int playerTwoIndex = currentSession.getStartGame().getPlayerTwoDeckIdx();

        Hero playerOneHero = (Hero) player.getParsedCard(currentSession.getStartGame().getPlayerOneHero());
        Hero playerTwoHero = (Hero) player.getParsedCard(currentSession.getStartGame().getPlayerTwoHero());

        player.setCurrentDeckIndex((player.equals(player1)) ? playerOneIndex : playerTwoIndex);
        player.setCurrentDeck(player.getDeckList().get(player.getCurrentDeckIndex()));
        player.shuffleDeck(currentSession.getStartGame().getShuffleSeed() + "");

        player.addCardInHand();
        player.setCurrentHero((player.equals(player1)) ? playerOneHero : playerTwoHero);
        player.setMana(Constants.ONE);
    }

    private int getCommandIndex(final String command) {
        return switch (command) {
            case Commands.GET_PLAYER_DECK -> Constants.ONE;
            case Commands.GET_CARDS_IN_HAND -> Constants.TWO;
            case Commands.GET_CARDS_ON_TABLE -> Constants.THREE;
            case Commands.GET_PLAYER_TURN -> Constants.FOUR;
            case Commands.GET_PLAYER_HERO -> Constants.FIVE;
            case Commands.GET_PLAYER_MANA -> Constants.SIX;
            case Commands.GET_CARD_AT_POSITION -> Constants.SEVEN;
            case Commands.GET_ENVIRONMENT_CARDS_IN_HAND -> Constants.EIGHT;
            case Commands.GET_FROZEN_CARDS_ON_TABLE -> Constants.NINE;
            case Commands.GET_TOTAL_GAMES_PLAYED -> Constants.TEN;
            case Commands.GET_PLAYER_ONE_WINS -> Constants.ELEVEN;
            case Commands.GET_PLAYER_TWO_WINS -> Constants.TWELVE;
            case Commands.END_PLAYER_TURN -> Constants.THIRTEEN;
            case Commands.PLACE_CARD -> Constants.FOURTEEN;
            default -> Constants.ZERO;
        };
    }

    private void debugCommand(final int index, final ActionsInput action) {
        ObjectNode actionOutput = JsonNodeFactory.instance.objectNode();

        outputData.add(actionOutput);
        actionOutput.put("command", action.getCommand());

        Player refPlayer = (action.getPlayerIdx() == 1) ? player1 : player2;

        switch (index) {
            case Constants.ONE -> getPlayerDeck(actionOutput, action, refPlayer);
            case Constants.TWO -> getCardsInHand(actionOutput, action, refPlayer);
            case Constants.THREE -> getCardsOnTable(actionOutput, action, refPlayer);
            case Constants.FOUR -> getPlayerTurn(actionOutput);
            case Constants.FIVE -> getPlayerHero(actionOutput, action, refPlayer);
            case Constants.SIX -> getPlayerMana(actionOutput, action, refPlayer);
            default -> { }
        }
    }

    public void actionCommand(int index, ActionsInput action) {
        switch (index) {
            case Constants.THIRTEEN -> changePlayerTurn();
            case Constants.FOURTEEN -> placeCard(action, action.getHandIdx());
            default -> { }
        }
    }

    public void throwException(String exceptionName, ActionsInput action) {
        ObjectNode actionOutput = JsonNodeFactory.instance.objectNode();

        outputData.add(actionOutput);
        actionOutput.put("command", action.getCommand());

        Player refPlayer = (action.getPlayerIdx() == 1) ? player1 : player2;

        switch (exceptionName) {
            case Exceptions.ENVIRONMENT_CARD_ON_TABLE ->
                            environmentCardOnTable(actionOutput, action);
            case Exceptions.NOT_ENOUGH_MANA_TO_PLACE_CARD ->
                            notEnoughManaToPlaceCard(actionOutput, action);
            case Exceptions.ROW_IS_FULL ->
                            rowIsFull(actionOutput, action);
            default -> { }
        }
    }

    public void changePlayerTurn() {
        currentPlayer = (currentPlayer.equals(player1)) ? player2 : player1;

        int startingPlayer = currentSession.getStartGame().getStartingPlayer();
        boolean endRound = (currentPlayer.equals(player1) &&  startingPlayer == 1) ||
                            (currentPlayer.equals(player2) && startingPlayer == 2);

        if (endRound) {
            applyNewRoundChanges(player1);
            applyNewRoundChanges(player2);
            System.out.println("--- New Round ---");
        }
    }

    public void applyNewRoundChanges(Player player) {
        player.incrementMana();
        player.addCardInHand();
    }

    public void placeCard(ActionsInput action, int handIndex) {
        if (!currentPlayer.getCardsInHand().isEmpty() && currentPlayer.getCardsInHand().size() > handIndex) {
            Card card = currentPlayer.getCardsInHand().get(handIndex);

            if (currentPlayer.getCardType(card.getName()) == Constants.TWO) {
                throwException(Exceptions.ENVIRONMENT_CARD_ON_TABLE, action);
                return;
            }

            if (card.getMana() > currentPlayer.getMana()) {
                throwException(Exceptions.NOT_ENOUGH_MANA_TO_PLACE_CARD, action);
                return;
            }

            if (currentPlayer.isTheRowFullForCard(card.getName())) {
                throwException(Exceptions.ROW_IS_FULL, action);
                return;
            }

            currentPlayer.removeCardFromHand(handIndex);
        }
    }

    public ArrayNode createCardsArrayNode(final ArrayList<Card> cards, Player player) {
        ObjectMapper objectMapper = new ObjectMapper();

        ArrayNode cardList = objectMapper.createArrayNode();

        for (Card card : cards) {
            int cardType = player.getCardType(card.getName());

            ObjectNode node = objectMapper.createObjectNode();

            node.put("mana", card.getMana());
            if (cardType == Constants.ONE) {
                node.put("attackDamage", card.getAttackDamage());
                node.put("health", card.getHealth());
            }

            node.put("description", card.getDescription());

            ArrayNode colors = objectMapper.createArrayNode();
            for (String color : card.getColors()) {
                colors.add(color);
            }

            node.set("colors", colors);
            node.put("name", card.getName());

            cardList.add(node);
        }

        return cardList;
    }

    public void getPlayerDeck(ObjectNode actionOutput, ActionsInput action, Player player) {
        actionOutput.put("playerIdx", action.getPlayerIdx());
        actionOutput.set("output", createCardsArrayNode(player.getCurrentDeck(), player));
    }

    public void getCardsInHand(ObjectNode actionOutput, ActionsInput action, Player player) {
        actionOutput.put("playerIdx", action.getPlayerIdx());
        actionOutput.set("output", createCardsArrayNode(player.getCardsInHand(), player));
    }

    public void getCardsOnTable(ObjectNode actionOutput, ActionsInput action, Player player) {
        ObjectMapper objectMapper = new ObjectMapper();

        player = (currentPlayer.equals(player1)) ? player2 : player1;

        ArrayNode oppositeBackRow = createCardsArrayNode(player.getCardsInBackRow(), player);
        ArrayNode oppositeFrontRow = createCardsArrayNode(player.getCardsInFrontRow(), player);
        ArrayNode thisFrontRow = createCardsArrayNode(currentPlayer.getCardsInFrontRow(), currentPlayer);
        ArrayNode thisBackRow = createCardsArrayNode(currentPlayer.getCardsInBackRow(), currentPlayer);

        ArrayNode table = objectMapper.createArrayNode();
        table.add(thisBackRow).add(thisFrontRow).add(oppositeFrontRow).add(oppositeBackRow);

        actionOutput.set("output", table);
    }

    public void getPlayerTurn(ObjectNode actionOutput) {
        actionOutput.put("output", currentPlayer.equals(player1) ? 1 : 2);
    }

    public void getPlayerHero(ObjectNode actionOutput, ActionsInput action, Player player) {
        ObjectMapper objectMapper = new ObjectMapper();

        actionOutput.put("playerIdx", action.getPlayerIdx());

        ObjectNode hero = objectMapper.createObjectNode();
        hero.put("mana", player.getCurrentHero().getMana());
        hero.put("description", player.getCurrentHero().getDescription());

        ArrayNode colors = objectMapper.createArrayNode();
        for (String color : player.getCurrentHero().getColors()) {
            colors.add(color);
        }

        hero.set("colors", colors);
        hero.put("name", player.getCurrentHero().getName());
        hero.put("health", player.getCurrentHero().getHealth());

        actionOutput.set("output", hero);
    }

    public void getPlayerMana(ObjectNode actionOutput, ActionsInput action, Player player) {
        actionOutput.put("output", player.getMana());
        actionOutput.put("playerIdx", action.getPlayerIdx());
    }

    public void environmentCardOnTable(ObjectNode actionOutput, ActionsInput action) {
        actionOutput.put("error", Exceptions.ENVIRONMENT_CARD_ON_TABLE);
        actionOutput.put("handIdx", action.getHandIdx());
    }

    public void notEnoughManaToPlaceCard(ObjectNode actionOutput, ActionsInput action) {
        actionOutput.put("error", Exceptions.NOT_ENOUGH_MANA_TO_PLACE_CARD);
        actionOutput.put("handIdx", action.getHandIdx());
    }

    public void rowIsFull(ObjectNode actionOutput, ActionsInput action) {
        actionOutput.put("error", Exceptions.ROW_IS_FULL);
        actionOutput.put("handIdx", action.getHandIdx());
    }
}
