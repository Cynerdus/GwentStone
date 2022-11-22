# GwentStone

Assignment for 2nd year OOP module.

Card game simulator inspired after Gwent and HearthStone. Includes functionalities from both games and homebrew modifications for better debugging purposes.

### The Game
GwentStone runs on what we call _game sessions_.
Each game session has its own properties and actions.
An _action_ is basically a command given to the program.

The commands could be either for debugging or for player managing.
A debug action would be, for example, retrieving information about
the cards a player has left in his deck. Through these commands
it is possible to simulate a whole game. Cards could be used to attack,
use abilities and even tank.

Each player is given a special card, named **Hero**. A Hero has 30 hit points
and may wield special abilities which go above other cards. However,
the main powerhouse of the game is represented by the **Minion** cards.
They are not as strong as the Hero, but their usage is cheaper
and, often, power lies in the numbers. The third category are
the **Environment** cards. They act as one-time amplifiers/limitations.

Following this, the game ends with the death of the Hero.

## Implementation
The logic behind is implemented in a multi-purpose class,
```GameManager.java``` which acts both as a generator for the
game sessions and a common bridge between commands and their functionalities.

The method ```generateGameSessions()``` brings information 
from ```inputData```, activating the ```sessionSetup()``` method
for each player. Based on the actions taken in each session, we
fabricate a ```commandIndex``` to manipulate the code in a more
efficient and readable manner.

### Debug Commands
They print out the current status of different sections of the game.
The output shall be printed only after the required validity check
will be made.
### Action Commands
For dealing with the flow of the game, we utilize these.
### Exceptions
Errors found while dealing with the precious two categories. They
get treated first, so that the game will run without mistakes.

#### Other Useful Classes:
* ```Commands.java```;
* ```Constants.java```;
* ```Exceptions.java```;
* ```AbilityManager.java```;

##### Copyright 2022 Popescu Cleopatra 323CA