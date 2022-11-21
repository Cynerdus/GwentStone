package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.CardInput;
import fileio.GameInput;
import fileio.Input;
import utils.AbilityManager;
import utils.Card;
import utils.CardNames;
import utils.Commands;
import utils.Constants;
import utils.Exceptions;
import utils.Hero;
import utils.Player;

import java.util.ArrayList;

public final class GameManager {
    private final Input inputData;
    private final ArrayNode outputData;

    private Player currentPlayer;
    private final Player player1;
    private final Player player2;

    private int playerOneWins = Constants.ZERO;
    private int playerTwoWins = Constants.ZERO;

    private GameInput currentSession;

    private final AbilityManager abilityManager = new AbilityManager();

    public GameManager(final Input inputData, final ArrayNode outputData) {
        this.inputData = inputData;
        this.outputData = outputData;

        player1 = playerSetup(1);
        player2 = playerSetup(2);

        generateGameSessions();
    }

    /**
     *
     * @return      the output in .json
     */
    public ArrayNode generateOutput() {
        return outputData;
    }

    /**
     *
     * @param playerIndex       index of player to be set up
     * @return                  the new player
     */
    private Player playerSetup(final int playerIndex) {
        Player player = new Player();
        ArrayList<ArrayList<CardInput>> playerDecks = (playerIndex == 1)
                                        ? inputData.getPlayerOneDecks().getDecks()
                                        : inputData.getPlayerTwoDecks().getDecks();

        player.setDeckList(playerDecks);
        player.setDeckCount(playerDecks.size());
        player.setCardsInDeckCount(playerDecks.get(0).size());

        return player;
    }

    /**
     * method to manage game sessions
     */
    private void generateGameSessions() {
        for (GameInput session : inputData.getGames()) {
            currentSession = session;

            sessionSetup(player1);
            sessionSetup(player2);

            currentPlayer = (session.getStartGame().getStartingPlayer() == 1) ? player1 : player2;

            for (ActionsInput action : session.getActions()) {
                int commandIndex = getCommandIndex(action.getCommand());

                if (commandIndex < Constants.THIRTEEN) {
                    debugCommand(commandIndex, action);
                } else {
                    actionCommand(commandIndex, action);
                }
            }
        }
    }

    /**
     *
     * @param player        player to apply the changes to
     */
    private void sessionSetup(final Player player) {

        int playerOneIndex = currentSession.getStartGame().getPlayerOneDeckIdx();
        int playerTwoIndex = currentSession.getStartGame().getPlayerTwoDeckIdx();

        Hero playerOneHero = (Hero) player.getParsedCard(currentSession.getStartGame()
                                                                       .getPlayerOneHero());
        Hero playerTwoHero = (Hero) player.getParsedCard(currentSession.getStartGame()
                                                                       .getPlayerTwoHero());

        player.setCurrentDeckIndex((player.equals(player1)) ? playerOneIndex : playerTwoIndex);

        player.replaceDeck(player.getDeckList().get(player.getCurrentDeckIndex()));
        player.shuffleDeck(currentSession.getStartGame().getShuffleSeed() + "");

        player.resetCards();
        player.addCardInHand();
        player.setCurrentHero((player.equals(player1)) ? playerOneHero : playerTwoHero);
        player.setMana(Constants.ONE);
        player.setManaIncrement(1);
    }

    /**
     *
     * @param       command name of the command
     * @return      index of the command
     */
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
            case Commands.CARD_USES_ATTACK -> Constants.SIXTEEN;
            case Commands.CARD_USES_ABILITY -> Constants.SEVENTEEN;
            case Commands.USE_ATTACK_HERO -> Constants.EIGHTEEN;
            case Commands.USE_HERO_ABILITY -> Constants.NINETEEN;
            default -> Constants.ZERO;
        };
    }

    /**
     *
     * @param index     index for command
     * @param action    current action
     */
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
            case Constants.TEN -> getTotalGamesPlayed(actionOutput);
            case Constants.ELEVEN -> getPlayerOneWins(actionOutput);
            case Constants.TWELVE -> getPlayerTwoWins(actionOutput);
            default -> { }
        }
    }

    /**
     *
     * @param index     index for command
     * @param action    current action
     */
    public void actionCommand(final int index, final ActionsInput action) {
        switch (index) {
            case Constants.THIRTEEN -> changePlayerTurn();
            case Constants.FOURTEEN -> placeCard(action, action.getHandIdx());
            case Constants.FIFTEEN -> useEnvironmentCard(action);
            case Constants.SIXTEEN -> cardUsesAttack(action);
            case Constants.SEVENTEEN -> cardUsesAbility(action);
            case Constants.EIGHTEEN -> useAttackHero(action);
            case Constants.NINETEEN -> useHeroAbility(action);
            default -> { }
        }
    }

    /**
     *
     * @param exceptionName     name of the exception
     * @param action            current action
     */
    public void throwException(final String exceptionName, final ActionsInput action) {
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
            case Exceptions.ATTACK_OWN_CARD ->
                            attackOwnCard(actionOutput, action);
            case Exceptions.CARD_ALREADY_ATTACKED ->
                            cardAlreadyAttacked(actionOutput, action);
            case Exceptions.FROZEN_ATTACKER ->
                            frozenAttacker(actionOutput, action);
            case Exceptions.TANK_CARD_NOT_ATTACKED ->
                            tankCardNotAttacked(actionOutput, action);
            case Exceptions.CARD_NOT_OWN ->
                            cardNotOwn(actionOutput, action);
            case Exceptions.NOT_ENOUGH_MANA_FOR_HERO ->
                            notEnoughManaForHero(actionOutput, action);
            case Exceptions.HERO_ALREADY_ATTACKED ->
                            heroAlreadyAttacked(actionOutput, action);
            case Exceptions.ROW_DOES_NOT_BELONG_TO_ENEMY ->
                            rowDoesNotBelongToEnemy(actionOutput, action);
            case Exceptions.ROW_IS_NOT_OWN ->
                            rowIsNotOwn(actionOutput, action);
            default -> { }
        }
    }

    /*------------------------ACTION COMMANDS------------------------*/

    /**
     * method to apply overall turn/round changes
     */
    public void changePlayerTurn() {
        currentPlayer.removeStunFromCards();
        currentPlayer.resetCardAttacks();
        currentPlayer.getCurrentHero().setHasAttackedThisTurn(false);

        currentPlayer = (currentPlayer.equals(player1)) ? player2 : player1;

        int startingPlayer = currentSession.getStartGame().getStartingPlayer();
        boolean endRound = (currentPlayer.equals(player1) &&  startingPlayer == 1)
                         || (currentPlayer.equals(player2) && startingPlayer == 2);

        if (endRound) {
            applyNewRoundChanges(player1);
            applyNewRoundChanges(player2);
        }
    }

    /**
     *
     * @param player        player to apply the changes to
     */
    public void applyNewRoundChanges(final Player player) {
        player.incrementMana();
        player.addCardInHand();
    }

    /**
     *
     * @param action        current action
     * @param handIndex     index to retrieve the card from the player's hand
     */
    public void placeCard(final ActionsInput action, final int handIndex) {
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

    /**
     *
     * @param action        current action
     */
    public void useEnvironmentCard(final ActionsInput action) {
        ArrayList<Card> affectedRow;

        affectedRow = switch (action.getAffectedRow()) {
            case Constants.ZERO ->  player2.getCardsInBackRow();
            case Constants.ONE -> player2.getCardsInFrontRow();
            case Constants.TWO -> player1.getCardsInFrontRow();
            case Constants.THREE -> player1.getCardsInBackRow();
            default -> new ArrayList<>();
        };

        boolean rowFromEnemy = (action.getAffectedRow() == Constants.ZERO
                                || action.getAffectedRow() == Constants.ONE
                                && currentPlayer.equals(player1))
                                || (action.getAffectedRow() == Constants.TWO
                                || action.getAffectedRow() == Constants.THREE
                                && currentPlayer.equals(player2));

        Card card = currentPlayer.getCardsInHand().get(action.getHandIdx());
        String cardName = card.getName();

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

        if (cardName.matches(CardNames.HEART_HOUND)
            && isRowFull(action.getAffectedRow())) {

            throwException(Exceptions.NO_STEAL_ROW_FULL, action);
            return;
        }

        currentPlayer.useEnvironmentCard(card, affectedRow, action.getAffectedRow());
    }

    /**
     *
     * @param action        action to retrieve information for current command
     */
    public void cardUsesAttack(final ActionsInput action) {
        int attackX = action.getCardAttacker().getX(),
            attackY = action.getCardAttacker().getY(),
            defendX = action.getCardAttacked().getX(),
            defendY = action.getCardAttacked().getY();

        Player attacker = (attackX <= Constants.ONE) ? player2 : player1;
        Player defender = (defendX >= Constants.TWO) ? player1 : player2;

        ArrayList<Card> attackRow = (attackX == Constants.ZERO || attackX == Constants.THREE)
                                    ? attacker.getCardsInBackRow()
                                    : attacker.getCardsInFrontRow();

        ArrayList<Card> defendRow = (defendX == Constants.ZERO || defendX == Constants.THREE)
                                    ? defender.getCardsInBackRow()
                                    : defender.getCardsInFrontRow();

        Card attackingCard = attackRow.get(attackY);
        Card defendingCard = defendRow.get(defendY);

        if (attacker.equals(defender)) {
            throwException(Exceptions.ATTACK_OWN_CARD, action);
            return;
        }

        if (attackingCard.hasAttackedThisTurn()) {
            throwException(Exceptions.CARD_ALREADY_ATTACKED, action);
            return;
        }

        if (attackingCard.isStunned()) {
            throwException(Exceptions.FROZEN_ATTACKER, action);
            return;
        }

        if (areThereTanks(defender) && !isTank(defendingCard)) {
            throwException(Exceptions.TANK_CARD_NOT_ATTACKED, action);
            return;
        }

        attackCard(attackingCard, defendingCard, defendRow);
    }

    /**
     *
     * @param attackingCard     card to perform attack
     * @param defendingCard     card to be attacked
     * @param affectedRow       target row
     */
    public void attackCard(final Card attackingCard,
                           final Card defendingCard,
                           final ArrayList<Card> affectedRow) {

        defendingCard.setHealth(defendingCard.getHealth() - attackingCard.getAttackDamage());
        attackingCard.setHasAttackedThisTurn(true);

        if (defendingCard.getHealth() <= 0) {
            affectedRow.remove(defendingCard);
        }
    }

    /**
     *
     * @param action        action to retrieve information for current command
     */
    public void cardUsesAbility(final ActionsInput action) {
        int attackX = action.getCardAttacker().getX(),
            attackY = action.getCardAttacker().getY(),
            defendX = action.getCardAttacked().getX(),
            defendY = action.getCardAttacked().getY();

        Player attacker = (attackX <= Constants.ONE) ? player2 : player1;
        Player defender = (defendX >= Constants.TWO) ? player1 : player2;

        ArrayList<Card> attackRow = (attackX == Constants.ZERO || attackX == Constants.THREE)
                ? attacker.getCardsInBackRow()
                : attacker.getCardsInFrontRow();

        ArrayList<Card> defendRow = (defendX == Constants.ZERO || defendX == Constants.THREE)
                ? defender.getCardsInBackRow()
                : defender.getCardsInFrontRow();


        Card attackingCard = attackRow.get(attackY);
        Card defendingCard = defendRow.get(defendY);

        if (attackingCard.isStunned()) {
            throwException(Exceptions.FROZEN_ATTACKER, action);
            return;
        }

        if (attackingCard.hasAttackedThisTurn()) {
            throwException(Exceptions.CARD_ALREADY_ATTACKED, action);
            return;
        }

        if (attackingCard.getName().matches(CardNames.DISCIPLE)
            && !defender.equals(attacker)) {

            throwException(Exceptions.CARD_NOT_OWN, action);
            return;
        }

        if ((attackingCard.getName().matches(CardNames.THE_RIPPER)
            || attackingCard.getName().matches(CardNames.MIRAJ)
            || attackingCard.getName().matches(CardNames.THE_CURSED_ONE))) {

            if (defender.equals(attacker)) {
                throwException(Exceptions.ATTACK_OWN_CARD, action);
                return;
            }

            if (areThereTanks(defender) && !isTank(defendingCard)) {
                throwException(Exceptions.TANK_CARD_NOT_ATTACKED, action);
                return;
            }
        }

        useAbility(attackingCard, defendingCard, defendRow);
    }

    /**
     *
     * @param attackingCard     card to perform attack
     * @param defendingCard     card to be attacked/boosted
     * @param affectedRow       target row
     */
    public void useAbility(final Card attackingCard,
                           final Card defendingCard,
                           final ArrayList<Card> affectedRow) {

        switch (attackingCard.getName()) {
            case CardNames.THE_RIPPER ->
                        abilityManager.weakKnees(attackingCard, defendingCard);
            case CardNames.MIRAJ ->
                        abilityManager.skyjack(attackingCard, defendingCard);
            case CardNames.THE_CURSED_ONE ->
                        abilityManager.shapeshift(attackingCard, defendingCard, affectedRow);
            case CardNames.DISCIPLE ->
                        abilityManager.godsPlan(attackingCard, defendingCard);
            default -> { }
        }
    }

    /**
     *
     * @param action    action to retrieve information for current command
     */
    public void useAttackHero(final ActionsInput action) {
        int attackX = action.getCardAttacker().getX(),
            attackY = action.getCardAttacker().getY();

        Player attacker = (attackX <= Constants.ONE) ? player2 : player1;
        Player defender = (attacker.equals(player1)) ? player2 : player1;

        ArrayList<Card> attackRow = (attackX == Constants.ZERO || attackX == Constants.THREE)
                ? attacker.getCardsInBackRow()
                : attacker.getCardsInFrontRow();

        Card attackingCard = attackRow.get(attackY);
        Card attackedHero = defender.getCurrentHero();

        if (attackingCard.isStunned()) {
            throwException(Exceptions.FROZEN_ATTACKER, action);
            return;
        }

        if (attackingCard.hasAttackedThisTurn()) {
            throwException(Exceptions.CARD_ALREADY_ATTACKED, action);
            return;
        }

        if (areThereTanks(defender)) {
            throwException(Exceptions.TANK_CARD_NOT_ATTACKED, action);
            return;
        }

        attackHero(attackingCard, attackedHero, attacker);
    }

    /**
     *
     * @param attackingCard     card to perform attack on hero
     * @param hero              target hero
     * @param attacker          player
     */
    public void attackHero(final Card attackingCard, final Card hero, final Player attacker) {
        hero.setHealth(hero.getHealth() - attackingCard.getAttackDamage());
        attackingCard.setHasAttackedThisTurn(true);

        if (hero.getHealth() <= 0) {
            ObjectNode node = new ObjectMapper().createObjectNode();

            if (attacker.equals(player1)) {
                playerOneWins++;
            } else {
                playerTwoWins++;
            }

            node.put("gameEnded", "Player "
                    + ((attacker.equals(player1)) ? "one" : "two")
                    + " killed the enemy hero.");
            outputData.add(node);
        }
    }

    /**
     *
     * @param action        action to retrieve information for current command
     */
    public void useHeroAbility(final ActionsInput action) {
        Player target = switch (action.getAffectedRow()) {
                            case Constants.ZERO, Constants.ONE -> player2;
                            case Constants.TWO, Constants.THREE -> player1;
                            default -> currentPlayer;
                        };

        ArrayList<Card> affectedRow = switch (action.getAffectedRow()) {
                                        case Constants.ZERO -> player2.getCardsInBackRow();
                                        case Constants.ONE -> player2.getCardsInFrontRow();
                                        case Constants.TWO -> player1.getCardsInFrontRow();
                                        case Constants.THREE -> player1.getCardsInBackRow();
                                        default -> new ArrayList<>();
                                    };

        Card hero = currentPlayer.getCurrentHero();

        if (hero.getMana() > currentPlayer.getMana()) {
            throwException(Exceptions.NOT_ENOUGH_MANA_FOR_HERO, action);
            return;
        }

        if (hero.hasAttackedThisTurn()) {
            throwException(Exceptions.HERO_ALREADY_ATTACKED, action);
            return;
        }

        if ((hero.getName().matches(CardNames.LORD_ROYCE)
            || hero.getName().matches(CardNames.EMPRESS_THORINA))
            && target.equals(currentPlayer)) {

            throwException(Exceptions.ROW_DOES_NOT_BELONG_TO_ENEMY, action);
            return;
        }

        if ((hero.getName().matches(CardNames.GENERAL_KOCIORAW)
            || hero.getName().matches(CardNames.KING_MUDFACE))
            && !target.equals(currentPlayer)) {

            throwException(Exceptions.ROW_IS_NOT_OWN, action);
            return;
        }

        heroAbility(hero, affectedRow);
        currentPlayer.subtractMana(hero.getMana());
    }

    /**
     *
     * @param hero          hero
     * @param affectedRow   target
     */
    public void heroAbility(final Card hero, final ArrayList<Card> affectedRow) {
        switch (hero.getName()) {
            case CardNames.LORD_ROYCE -> abilityManager.subZero(hero, affectedRow);
            case CardNames.EMPRESS_THORINA -> abilityManager.lowBlow(hero, affectedRow);
            case CardNames.KING_MUDFACE -> abilityManager.earthBorn(hero, affectedRow);
            case CardNames.GENERAL_KOCIORAW -> abilityManager.bloodThirst(hero, affectedRow);
            default -> { }
        }
    }

    /*------------------------DEBUG COMMANDS------------------------*/

    /**
     *
     * @param actionOutput      ObjectNode for printing data in .json format
     * @param action            current action
     * @param player            target player
     */
    public void getPlayerDeck(final ObjectNode actionOutput,
                              final ActionsInput action,
                              final Player player) {

        actionOutput.put("playerIdx", action.getPlayerIdx());
        actionOutput.set("output", createCardsArrayNode(player.getCurrentDeck()));
    }

    /**
     *
     * @param actionOutput      ObjectNode for printing data in .json format
     * @param action            current action
     * @param player            target player
     */
    public void getCardsInHand(final ObjectNode actionOutput,
                               final ActionsInput action,
                               final Player player) {

        actionOutput.put("playerIdx", action.getPlayerIdx());
        actionOutput.set("output", createCardsArrayNode(player.getCardsInHand()));
    }

    /**
     *
     * @param actionOutput      ObjectNode for printing data in .json format
     */
    public void getCardsOnTable(final ObjectNode actionOutput) {
        ObjectMapper objectMapper = new ObjectMapper();

        ArrayNode otherPlayerBackRow = createCardsArrayNode(player2.getCardsInBackRow());
        ArrayNode otherPlayerFrontRow = createCardsArrayNode(player2.getCardsInFrontRow());
        ArrayNode startingPlayerFrontRow = createCardsArrayNode(player1.getCardsInFrontRow());
        ArrayNode startingPlayerBackRow = createCardsArrayNode(player1.getCardsInBackRow());

        ArrayNode table = objectMapper.createArrayNode();
        table.add(otherPlayerBackRow)
             .add(otherPlayerFrontRow)
             .add(startingPlayerFrontRow)
             .add(startingPlayerBackRow);

        actionOutput.set("output", table);
    }

    /**
     *
     * @param actionOutput      ObjectNode for printing data in .json format
     */
    public void getPlayerTurn(final ObjectNode actionOutput) {
        actionOutput.put("output", currentPlayer.equals(player1) ? 1 : 2);
    }

    /**
     *
     * @param actionOutput      ObjectNode for printing data in .json format
     * @param action            current action
     * @param player            target player
     */
    public void getPlayerHero(final ObjectNode actionOutput,
                              final ActionsInput action,
                              final Player player) {

        ObjectNode hero = getCardNode(player.getCurrentHero());

        actionOutput.put("playerIdx", action.getPlayerIdx());
        actionOutput.set("output", hero);
    }

    /**
     *
     * @param actionOutput      ObjectNode for printing data in .json format
     * @param action            current action
     * @param player            target player
     */
    public void getPlayerMana(final ObjectNode actionOutput,
                              final ActionsInput action,
                              final Player player) {

        actionOutput.put("output", player.getMana());
        actionOutput.put("playerIdx", action.getPlayerIdx());
    }

    /**
     *
     * @param actionOutput      ObjectNode for printing data in .json format
     * @param action            current action
     * @param player            target player
     */
    public void getEnvironmentCardsInHand(final ObjectNode actionOutput,
                                          final ActionsInput action,
                                          final Player player) {

        ArrayList<Card> environmentCards = new ArrayList<>();
        for (Card card : player.getCardsInHand()) {
            if (player.getCardType(card.getName()) == 2) {
                environmentCards.add(card);
            }
        }

        actionOutput.put("playerIdx", action.getPlayerIdx());
        actionOutput.set("output", createCardsArrayNode(environmentCards));
    }

    /**
     *
     * @param actionOutput      ObjectNode for printing data in .json format
     * @param action            current action
     */
    public void getCardAtPosition(final ObjectNode actionOutput, final ActionsInput action) {
        int x = action.getX();
        int y = action.getY();

        ArrayList<Card> row;

        row = switch (x) {
            case Constants.ZERO -> (player2.getCardsInBackRow());
            case Constants.ONE -> (player2.getCardsInFrontRow());
            case Constants.TWO -> (player1.getCardsInFrontRow());
            case Constants.THREE -> (player1.getCardsInBackRow());
            default -> new ArrayList<>();
        };

        actionOutput.put("x", x);
        actionOutput.put("y", y);

        if (isCardOnRow(y, row)) {
            ObjectNode cardNode = getCardNode(row.get(y));
            actionOutput.set("output", cardNode);
        } else {
            actionOutput.put("output", Exceptions.NO_CARD_AT_POSITION);
        }
    }

    /**
     *
     * @param actionOutput      ObjectNode for printing data in .json format
     */
    public void getFrozenCardsOnTable(final ObjectNode actionOutput) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode node = objectMapper.createArrayNode();

        for (Card card : player2.getCardsInBackRow()) {
            if (card.isStunned()) {
                node.add(getCardNode(card));
            }
        }

        for (Card card : player2.getCardsInFrontRow()) {
            if (card.isStunned()) {
                node.add(getCardNode(card));
            }
        }

        for (Card card : player1.getCardsInFrontRow()) {
            if (card.isStunned()) {
                node.add(getCardNode(card));
            }
        }

        for (Card card : player1.getCardsInBackRow()) {
            if (card.isStunned()) {
                node.add(getCardNode(card));
            }
        }

        actionOutput.set("output", node);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void environmentCardOnTable(final ObjectNode actionOutput, final ActionsInput action) {
        actionOutput.put("error", Exceptions.ENVIRONMENT_CARD_ON_TABLE);
        actionOutput.put("handIdx", action.getHandIdx());
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     */
    public void getTotalGamesPlayed(final ObjectNode actionOutput) {
        actionOutput.put("output", playerOneWins + playerTwoWins);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     */
    public void getPlayerOneWins(final ObjectNode actionOutput) {
        actionOutput.put("output", playerOneWins);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     */
    public void getPlayerTwoWins(final ObjectNode actionOutput) {
        actionOutput.put("output", playerTwoWins);
    }

    /*------------------------EXCEPTION HANDLING------------------------*/

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void notEnoughManaToPlaceCard(final ObjectNode actionOutput, final ActionsInput action) {
        actionOutput.put("error", Exceptions.NOT_ENOUGH_MANA_TO_PLACE_CARD);
        actionOutput.put("handIdx", action.getHandIdx());
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void rowIsFull(final ObjectNode actionOutput, final ActionsInput action) {
        actionOutput.put("error", Exceptions.ROW_IS_FULL);
        actionOutput.put("handIdx", action.getHandIdx());
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void cardNotTypeEnvironment(final ObjectNode actionOutput, final ActionsInput action) {
        actionOutput.put("handIdx", action.getHandIdx());
        actionOutput.put("affectedRow", action.getAffectedRow());
        actionOutput.put("error", Exceptions.CARD_NOT_TYPE_ENVIRONMENT);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void notEnoughManaEnvironment(final ObjectNode actionOutput, final ActionsInput action) {
        actionOutput.put("handIdx", action.getHandIdx());
        actionOutput.put("affectedRow", action.getAffectedRow());
        actionOutput.put("error", Exceptions.NOT_ENOUGH_MANA_ENVIRONMENT);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void rowNotFromEnemy(final ObjectNode actionOutput, final ActionsInput action) {
        actionOutput.put("handIdx", action.getHandIdx());
        actionOutput.put("affectedRow", action.getAffectedRow());
        actionOutput.put("error", Exceptions.ROW_NOT_FROM_ENEMY);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void noStealRowFull(final ObjectNode actionOutput, final ActionsInput action) {
        actionOutput.put("handIdx", action.getHandIdx());
        actionOutput.put("affectedRow", action.getAffectedRow());
        actionOutput.put("error", Exceptions.NO_STEAL_ROW_FULL);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void attackOwnCard(final ObjectNode actionOutput, final ActionsInput action) {
        printCardAttackDebug(actionOutput, action);
        actionOutput.put("error", Exceptions.ATTACK_OWN_CARD);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void cardAlreadyAttacked(final ObjectNode actionOutput, final ActionsInput action) {
        printCardAttackDebug(actionOutput, action);
        actionOutput.put("error", Exceptions.CARD_ALREADY_ATTACKED);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void frozenAttacker(final ObjectNode actionOutput, final ActionsInput action) {
        printCardAttackDebug(actionOutput, action);
        actionOutput.put("error", Exceptions.FROZEN_ATTACKER);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void tankCardNotAttacked(final ObjectNode actionOutput, final ActionsInput action) {
        printCardAttackDebug(actionOutput, action);
        actionOutput.put("error", Exceptions.TANK_CARD_NOT_ATTACKED);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void cardNotOwn(final ObjectNode actionOutput, final ActionsInput action) {
        printCardAttackDebug(actionOutput, action);
        actionOutput.put("error", Exceptions.CARD_NOT_OWN);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void notEnoughManaForHero(final ObjectNode actionOutput, final ActionsInput action) {
        actionOutput.put("affectedRow", action.getAffectedRow());
        actionOutput.put("error", Exceptions.NOT_ENOUGH_MANA_FOR_HERO);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void heroAlreadyAttacked(final ObjectNode actionOutput, final ActionsInput action) {
        actionOutput.put("affectedRow", action.getAffectedRow());
        actionOutput.put("error", Exceptions.HERO_ALREADY_ATTACKED);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void rowDoesNotBelongToEnemy(final ObjectNode actionOutput, final ActionsInput action) {
        actionOutput.put("affectedRow", action.getAffectedRow());
        actionOutput.put("error", Exceptions.ROW_DOES_NOT_BELONG_TO_ENEMY);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void rowIsNotOwn(final ObjectNode actionOutput, final ActionsInput action) {
        actionOutput.put("affectedRow", action.getAffectedRow());
        actionOutput.put("error", Exceptions.ROW_IS_NOT_OWN);
    }

    /**
     *
     * @param actionOutput  ObjectNode for printing data in .json format
     * @param action        current action
     */
    public void printCardAttackDebug(final ObjectNode actionOutput, final ActionsInput action) {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode cardAttacker = objectMapper.createObjectNode();
        ObjectNode cardAttacked = objectMapper.createObjectNode();

        cardAttacker.put("x", action.getCardAttacker().getX());
        cardAttacker.put("y", action.getCardAttacker().getY());
        actionOutput.set("cardAttacker", cardAttacker);

        if (!action.getCommand().matches(Commands.USE_ATTACK_HERO)) {
            cardAttacked.put("x", action.getCardAttacked().getX());
            cardAttacked.put("y", action.getCardAttacked().getY());
            actionOutput.set("cardAttacked", cardAttacked);
        }
    }

    /*------------------------UTILS------------------------*/

    /**
     *
     * @param card      card whose values are to be parsed to .json
     * @return          ObjectNode with the card's values
     */
    public ObjectNode getCardNode(final Card card) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode cardNode = objectMapper.createObjectNode();

        if (currentPlayer.getCardType(card.getName()) == Constants.ONE) {
            cardNode.put("health", card.getHealth())
                    .put("attackDamage", card.getAttackDamage());
        }

        if (currentPlayer.getCardType(card.getName()) == Constants.THREE) {
            cardNode.put("health", card.getHealth());
        }

        cardNode.put("mana", card.getMana())
                .put("description", card.getDescription())
                .put("name", card.getName())
                .set("colors", getCardColors(card));

        return cardNode;
    }

    /**
     *
     * @param index     supposed card index
     * @param row       target row
     * @return          true if card is on row
     *                  false otherwise
     */
    public boolean isCardOnRow(final int index, final ArrayList<Card> row) {
        return !row.isEmpty() && (index < row.size());
    }

    /**
     *
     * @param card      card in question
     * @return          the colors
     */
    private ArrayNode getCardColors(final Card card) {
        ObjectMapper objectMapper = new ObjectMapper();

        ArrayNode colors = objectMapper.createArrayNode();
        for (String color : card.getColors()) {
            colors.add(color);
        }

        return colors;
    }

    /**
     *
     * @param cards     list of cards to be parsed to .json
     * @return          ArrayNode with the list's cards
     */
    public ArrayNode createCardsArrayNode(final ArrayList<Card> cards) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode cardList = objectMapper.createArrayNode();

        for (Card card : cards) {
            ObjectNode node = getCardNode(card);
            cardList.add(node);
        }

        return cardList;
    }

    /**
     *
     * @param player        defending player
     * @return              true    if yes there are tanks
     *                      false   otherwise
     */
    public boolean areThereTanks(final Player player) {
        for (Card card : player.getCardsInFrontRow()) {
            if (card.getName().matches(CardNames.GOLIATH)
                    || card.getName().matches(CardNames.WARDEN)) {

                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param card              card to be checked for  t a n k n e s s
     * @return                  true    if yes
     *                          false   otherwise
     */
    public boolean isTank(final Card card) {
        return (card.getName().matches(CardNames.GOLIATH)
                || card.getName().matches(CardNames.WARDEN));
    }

    /**
     *
     * @param affectedRow       index for the affected row
     * @return                  true if row is full
     *                          false otherwise
     */
    public boolean isRowFull(final int affectedRow) {
        return switch (affectedRow) {
            case Constants.ZERO -> player1.checkRowStatus("backRow");
            case Constants.ONE -> player1.checkRowStatus("frontRow");
            case Constants.TWO -> player2.checkRowStatus("frontRow");
            case Constants.THREE -> player2.checkRowStatus("backRow");
            default -> false;
        };
    }
}
