package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.CardInput;
import fileio.GameInput;
import fileio.Input;
import utils.Commands;
import utils.Constants;
import utils.Player;

import java.util.ArrayList;

public final class GameManager {
    private final Input inputData;

    private final ArrayNode outputData;

    private Player currentPlayer;
    private Player player1;
    private Player player2;

    public GameManager(final Input inputData, final ArrayNode outputData) {
        this.inputData = inputData;
        this.outputData = outputData;

        playerDecksSetup();
        generateGameSessions();
    }

    /**
     *  retrieving the output in json
     */
    public ArrayNode generateOutput() {
        return outputData;
    }

    /*      pre-games setup     */
    private void playerDecksSetup() {

        player1 = new Player();
        player2 = new Player();

        player1.setDeckList(inputData.getPlayerOneDecks().getDecks());
        player1.setDeckCount(inputData.getPlayerOneDecks().getNrDecks());
        player1.setCardsInDeckCount(inputData.getPlayerOneDecks().getNrCardsInDeck());

        player2.setDeckList(inputData.getPlayerTwoDecks().getDecks());
        player2.setDeckCount(inputData.getPlayerTwoDecks().getNrDecks());
        player2.setCardsInDeckCount(inputData.getPlayerTwoDecks().getNrCardsInDeck());
    }

    /*      simulate the games      */
    private void generateGameSessions() {
        for (GameInput session : inputData.getGames()) {
            /*      player1 - current deck - initial hand - hero    */

            player1.setCurrentDeckIndex(session.getStartGame().getPlayerOneDeckIdx());
            player1.setCurrentDeck(inputData.getPlayerOneDecks().getDecks()
                    .get(player1.getCurrentDeckIndex()));

            player1.shuffleDeck(session.getStartGame().getShuffleSeed() + "");
            player1.setCardsInHand(new ArrayList<CardInput>());
            player1.addCardInHand(inputData.getPlayerOneDecks().getDecks()
                    .get(player1.getCurrentDeckIndex()).get(0));
            player1.removeCardFromDeck(0);

            player1.setCurrentHero(session.getStartGame().getPlayerOneHero());

            /*      player2 - current deck - initial hand - hero    */

            player2.setCurrentDeckIndex(session.getStartGame().getPlayerTwoDeckIdx());
            player2.setCurrentDeck(inputData.getPlayerTwoDecks().getDecks()
                    .get(player2.getCurrentDeckIndex()));

            player2.shuffleDeck(session.getStartGame().getShuffleSeed() + "");
            player2.setCardsInHand(new ArrayList<CardInput>());
            player2.addCardInHand(inputData.getPlayerTwoDecks().getDecks()
                    .get(player2.getCurrentDeckIndex()).get(0));
            player2.removeCardFromDeck(0);

            player2.setCurrentHero(session.getStartGame().getPlayerTwoHero());

            currentPlayer = (session.getStartGame().getStartingPlayer() == 1) ? player1 : player2;

            for (ActionsInput action : session.getActions()) {
                /* TODO change players and keep track of their rounds
                     by looking at the starting player*/
                int commandIndex = getCommandIndex(action.getCommand());
                applyCommand(commandIndex, action);
            }
        }
    }

    private int getCommandIndex(final String command) {
        int commandIndex;

        switch (command) {
            case Commands.GET_PLAYER_DECK:
                commandIndex = Constants.ONE;
                break;
            case Commands.GET_CARDS_IN_HAND:
                commandIndex = Constants.TWO;
                break;
            case Commands.GET_CARDS_ON_TABLE:
                commandIndex = Constants.THREE;
                break;
            case Commands.GET_PLAYER_TURN:
                commandIndex = Constants.FOUR;
                break;
            case Commands.GET_PLAYER_HERO:
                commandIndex = Constants.FIVE;
                break;
            case Commands.GET_PLAYER_MANA:
                commandIndex = Constants.SIX;
                break;
            case Commands.GET_CARD_AT_POSITION:
                commandIndex = Constants.SEVEN;
                break;
            case Commands.GET_ENVIRONMENT_CARDS_IN_HAND:
                commandIndex = Constants.EIGHT;
                break;
            case Commands.GET_FROZEN_CARDS_ON_TABLE:
                commandIndex = Constants.NINE;
                break;
            case Commands.GET_TOTAL_GAMES_PLAYED:
                commandIndex = Constants.TEN;
                break;
            case Commands.GET_PLAYER_ONE_WINS:
                commandIndex = Constants.ELEVEN;
                break;
            case Commands.GET_PLAYER_TWO_WINS:
                commandIndex = Constants.TWELVE;
                break;
            default:
                commandIndex = 0;
        }

        return commandIndex;
    }

    private void applyCommand(final int index, final ActionsInput action) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionOutput = JsonNodeFactory.instance.objectNode();
        Player refPlayer;
        outputData.add(actionOutput);
        actionOutput.put("command", action.getCommand());
        switch (index) {
            case Constants.ONE: /*      GET_PLAYER_DECK     */
                actionOutput.put("playerIdx", action.getPlayerIdx());

                refPlayer = (action.getPlayerIdx() == 1) ? player1 : player2;
                actionOutput.putPOJO("output", refPlayer.getCurrentDeck());

                break;
            case Constants.FOUR: /*      GET_PLAYER_TURN     */
                actionOutput.put("output", currentPlayer.equals(player1) ? 1 : 2);

                break;
            case Constants.FIVE: /*      GET_PLAYER_HERO     */
                actionOutput.put("playerIdx", action.getPlayerIdx());

                refPlayer = (action.getPlayerIdx() == 1) ? player1 : player2;
                actionOutput.putPOJO("output", refPlayer.getCurrentHero());
                break;
            default:
        }
    }
}
