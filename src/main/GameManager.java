package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.*;
import utils.*;

import java.util.ArrayList;

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
            case Commands.USE_ENVIRONMENT_CARD -> Constants.FIFTEEN;
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
            case Constants.THREE -> getCardsOnTable(actionOutput);
            case Constants.FOUR -> getPlayerTurn(actionOutput);
            case Constants.FIVE -> getPlayerHero(actionOutput, action, refPlayer);
            case Constants.SIX -> getPlayerMana(actionOutput, action, refPlayer);
            case Constants.SEVEN -> getCardAtPosition(actionOutput, action);
            case Constants.EIGHT -> getEnvironmentCardsInHand(actionOutput, action, refPlayer);
            case Constants.NINE -> getFrozenCardsOnTable(actionOutput);
            default -> { }
        }
    }

    public void actionCommand(int index, ActionsInput action) {
        switch (index) {
            case Constants.THIRTEEN -> changePlayerTurn();
            case Constants.FOURTEEN -> placeCard(action, action.getHandIdx());
            case Constants.FIFTEEN -> useEnvironmentCard(action);
            default -> { }
        }
    }

    public void throwException(String exceptionName, ActionsInput action) {
        ObjectNode actionOutput = JsonNodeFactory.instance.objectNode();

        outputData.add(actionOutput);
        actionOutput.put("command", action.getCommand());

        switch (exceptionName) {
            case Exceptions.ENVIRONMENT_CARD_ON_TABLE ->
                            environmentCardOnTable(actionOutput, action);
            case Exceptions.NOT_ENOUGH_MANA_TO_PLACE_CARD ->
                            notEnoughManaToPlaceCard(actionOutput, action);
            case Exceptions.ROW_IS_FULL ->
                            rowIsFull(actionOutput, action);
            case Exceptions.CARD_NOT_TYPE_ENVIRONMENT ->
                            cardNotTypeEnvironment(actionOutput, action);
            case Exceptions.NOT_ENOUGH_MANA_ENVIRONMENT ->
                            notEnoughManaEnvironment(actionOutput, action);
            case Exceptions.ROW_NOT_FROM_ENEMY ->
                            rowNotFromEnemy(actionOutput, action);
            case Exceptions.NO_STEAL_ROW_FULL ->
                            noStealRowFull(actionOutput, action);
            case Exceptions.NO_CARD_AT_POSITION ->
                            noCardAtPosition(actionOutput, action);
            default -> { }
        }
    }

    public void changePlayerTurn() {
        currentPlayer.removeStunFromCards();
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

            currentPlayer.placeCardWithIndex(handIndex);
        }
    }

    public void useEnvironmentCard(ActionsInput action) {
        ArrayList<Card> affectedRow;

        switch (action.getAffectedRow()) {
            case Constants.ZERO -> affectedRow = player2.getCardsInBackRow();
            case Constants.ONE -> affectedRow = player2.getCardsInFrontRow();
            case Constants.TWO -> affectedRow = player1.getCardsInFrontRow();
            case Constants.THREE -> affectedRow = player1.getCardsInBackRow();
            default -> affectedRow = new ArrayList<>();
        }

        boolean rowFromEnemy = (action.getAffectedRow() == 0 || action.getAffectedRow() == 1
                                && currentPlayer.equals(player1)) ||
                                (action.getAffectedRow() == 2 || action.getAffectedRow() == 3
                                && currentPlayer.equals(player2));

        if (!currentPlayer.getCardsInHand().isEmpty() && currentPlayer.getCardsInHand().size() > action.getHandIdx()) {
            Card card = currentPlayer.getCardsInHand().get(action.getHandIdx());
            String cardName = card.getName();

            System.out.println("CARD NAME: " + card.getName() + " | type: " + currentPlayer.getCardType(card.getName()));

            if (currentPlayer.getCardType(card.getName()) != 2) {
                throwException(Exceptions.CARD_NOT_TYPE_ENVIRONMENT, action);
                return;
            }

            if (card.getMana() > currentPlayer.getMana()) {
                throwException(Exceptions.NOT_ENOUGH_MANA_ENVIRONMENT, action);
                return;
            }

            if (!cardName.matches(CardNames.HEART_HOUND) && !rowFromEnemy) {
                throwException(Exceptions.ROW_NOT_FROM_ENEMY, action);
                return;
            }

            if (cardName.matches(CardNames.HEART_HOUND) &&
                isRowFull(action.getAffectedRow())) {

                throwException(Exceptions.NO_STEAL_ROW_FULL, action);
                return;
            }

            currentPlayer.useEnvironmentCard(card, affectedRow, action.getAffectedRow());
        }
    }

    public boolean isRowFull(int affectedRow) {
        return switch(affectedRow) {
            case Constants.ZERO -> (player1.checkRowStatus("backRow"));
            case Constants.ONE -> (player1.checkRowStatus("frontRow"));
            case Constants.TWO -> (player2.checkRowStatus("frontRow"));
            case Constants.THREE -> (player2.checkRowStatus("backRow"));
            default -> false;
        };
    }

    public ArrayNode createCardsArrayNode(final ArrayList<Card> cards) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode cardList = objectMapper.createArrayNode();

        for (Card card : cards) {
            ObjectNode node = getCardNode(card);
            cardList.add(node);
        }

        return cardList;
    }

    public void getPlayerDeck(ObjectNode actionOutput, ActionsInput action, Player player) {
        actionOutput.put("playerIdx", action.getPlayerIdx());
        actionOutput.set("output", createCardsArrayNode(player.getCurrentDeck()));
    }

    public void getCardsInHand(ObjectNode actionOutput, ActionsInput action, Player player) {
        actionOutput.put("playerIdx", action.getPlayerIdx());
        actionOutput.set("output", createCardsArrayNode(player.getCardsInHand()));
    }

    public void getCardsOnTable(ObjectNode actionOutput) {
        ObjectMapper objectMapper = new ObjectMapper();

        ArrayNode otherPlayerBackRow = createCardsArrayNode(player2.getCardsInBackRow());
        ArrayNode otherPlayerFrontRow = createCardsArrayNode(player2.getCardsInFrontRow());
        ArrayNode startingPlayerFrontRow = createCardsArrayNode(player1.getCardsInFrontRow());
        ArrayNode startingPlayerBackRow = createCardsArrayNode(player1.getCardsInBackRow());

        ArrayNode table = objectMapper.createArrayNode();
        table.add(otherPlayerBackRow).add(otherPlayerFrontRow).add(startingPlayerFrontRow).add(startingPlayerBackRow);

        actionOutput.set("output", table);
    }

    public void getPlayerTurn(ObjectNode actionOutput) {
        actionOutput.put("output", currentPlayer.equals(player1) ? 1 : 2);
    }

    public void getPlayerHero(ObjectNode actionOutput, ActionsInput action, Player player) {
        ObjectNode hero = getCardNode(player.getCurrentHero());

        actionOutput.put("playerIdx", action.getPlayerIdx());
        actionOutput.set("output", hero);
    }

    public void getPlayerMana(ObjectNode actionOutput, ActionsInput action, Player player) {
        actionOutput.put("output", player.getMana());
        actionOutput.put("playerIdx", action.getPlayerIdx());
    }

    public void getEnvironmentCardsInHand(ObjectNode actionOutput, ActionsInput action, Player player) {
        ArrayList<Card> environmentCards = new ArrayList<>();
        for (Card card : player.getCardsInHand()) {
            if (player.getCardType(card.getName()) == 2)
                environmentCards.add(card);
        }

        actionOutput.put("playerIdx", action.getPlayerIdx());
        actionOutput.set("output", createCardsArrayNode(environmentCards));
    }

    public void getCardAtPosition(ObjectNode actionOutput, ActionsInput action) {
        int x = action.getX();
        int y = action.getY();

        int startingPlayerIndex = currentSession.getStartGame().getStartingPlayer();
        Player startingPlayer = (startingPlayerIndex == 1) ? player1 : player2;
        Player otherPlayer = (startingPlayer.equals(player1)) ? player2 : player1;

        ArrayList<Card> row;

        switch (x) {
            case Constants.ZERO -> row = otherPlayer.getCardsInBackRow();
            case Constants.ONE -> row = otherPlayer.getCardsInFrontRow();
            case Constants.TWO -> row = startingPlayer.getCardsInFrontRow();
            case Constants.THREE -> row = startingPlayer.getCardsInBackRow();
            default -> row = new ArrayList<>();
        }

        if (isCardOnRow(y, row)) {
            System.out.println("Card at pos x = " + x + " y = " + y + " is " + row.get(y).getName());
            printCardAtPosition(actionOutput, row.get(y), x, y);
        }
        else
            throwException(Exceptions.NO_CARD_AT_POSITION, action);
    }

    public void getFrozenCardsOnTable(ObjectNode actionOutput) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode node = objectMapper.createArrayNode();

        for (Card card : player2.getCardsInBackRow())
            if (card.isStunned())
                node.add(getCardNode(card));

        for (Card card : player2.getCardsInFrontRow())
            if (card.isStunned())
                node.add(getCardNode(card));

        for (Card card : player1.getCardsInFrontRow())
            if (card.isStunned())
                node.add(getCardNode(card));

        for (Card card : player1.getCardsInBackRow())
            if (card.isStunned())
                node.add(getCardNode(card));

        actionOutput.set("output", node);
    }

    public ObjectNode getCardNode(Card card) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode cardNode = objectMapper.createObjectNode();

        if (currentPlayer.getCardType(card.getName()) == Constants.ONE)
            cardNode.put("health", card.getHealth())
                    .put("attackDamage", card.getAttackDamage());

        if (currentPlayer.getCardType(card.getName()) == Constants.THREE)
            cardNode.put("health", card.getHealth());

        cardNode.put("mana", card.getMana())
                .put("description", card.getDescription())
                .put("name", card.getName())
                .set("colors", getCardColors(card));

        return cardNode;
    }

    private ArrayNode getCardColors(Card card) {
        ObjectMapper objectMapper = new ObjectMapper();

        ArrayNode colors = objectMapper.createArrayNode();
        for (String color : card.getColors())
            colors.add(color);

        return colors;
    }

    public boolean isCardOnRow(int index, ArrayList<Card> row) {
        return !row.isEmpty() && (index < row.size());
    }

    public void printCardAtPosition(ObjectNode actionOutput, Card card, int x, int y) {
        actionOutput.put("x", x);
        actionOutput.put("y", y);

        ObjectNode cardNode = getCardNode(card);
        actionOutput.set("output", cardNode);
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

    public void cardNotTypeEnvironment(ObjectNode actionOutput, ActionsInput action) {
        actionOutput.put("handIdx", action.getHandIdx());
        actionOutput.put("affectedRow", action.getAffectedRow());
        actionOutput.put("error", Exceptions.CARD_NOT_TYPE_ENVIRONMENT);
    }

    public void notEnoughManaEnvironment(ObjectNode actionOutput, ActionsInput action) {
        actionOutput.put("handIdx", action.getHandIdx());
        actionOutput.put("affectedRow", action.getAffectedRow());
        actionOutput.put("error", Exceptions.NOT_ENOUGH_MANA_ENVIRONMENT);
    }

    public void rowNotFromEnemy(ObjectNode actionOutput, ActionsInput action) {
        actionOutput.put("handIdx", action.getHandIdx());
        actionOutput.put("affectedRow", action.getAffectedRow());
        actionOutput.put("error", Exceptions.ROW_NOT_FROM_ENEMY);
    }

    public void noStealRowFull(ObjectNode actionOutput, ActionsInput action) {
        actionOutput.put("handIdx", action.getHandIdx());
        actionOutput.put("affectedRow", action.getAffectedRow());
        actionOutput.put("error", Exceptions.NO_STEAL_ROW_FULL);
    }

    public void noCardAtPosition(ObjectNode actionOutput, ActionsInput action) {
        actionOutput.put("x", action.getX());
        actionOutput.put("y", action.getY());
        actionOutput.put("error", Exceptions.NO_CARD_AT_POSITION);
    }
}
