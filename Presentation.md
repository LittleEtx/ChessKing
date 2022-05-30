# Presentation

## The Game Engine
### FXGL
Useful module which is a super set of JavaFX.

### Based on javafx
JavaFX offers full time support for MVC.

MVC (Mode-view-controller) divides the program logic into three interconnected elements. 
It emphasizes on the separation between a software's business logic and display.


Model: the data the app should contain.

View: how the app's data should be displayed.

Controller: the logic that updates the model and/or view in response to input from users.
<img src="https://developer.mozilla.org/en-US/docs/Glossary/MVC/model-view-controller-light-blue.png" width="700">


***
## One Important Dream: Make a Complete Game
### Make Functions of the Game Complete

With all these functions we want to achieve, we can't just add so many buttons to the side of our 
chessboard with titles "Save" and "Replay". That makes our game look like a homework that is made 
to fulfill random requirements of teachers. 

That is why we made a complete UI for the main menu, and put all the buttons there. We think that 
makes a lot more sense than putting everything together with the chessboard.

<img src="pre/LocalGameMenu.png" alt="Local Game Menu" width="700">

### Make Rules of Chess Complete
The rules for the chess in our project is not so complete. It doesn't include "Draw by agreement", 
"Fifty-move rule", or "Surrender". So we added draw by agreement to our AI, so that you and him 
could shake hands peacefully and enjoy a win-win situation. You can also use the "Fifty-move rule" 
to force a draw game on your opponent, but we strongly doubt that anyone will do that. When you 
don't want to play a game, and your mind is on leaving, you don't even need to wait fifteen 
minutes to "Surrender".

<img src="pre/EndGameSurrender.png" width="700" alt="Lots of choices to end game">

### User Friendly
User Interface
* Choose different local players
* Players can change their name, avatar, skin, chessboard colour, and background
* Independent saves for different players
* Can delete players, saves, replays.

Game information display in game
* Targets of your chess
* Your allies 
* Your enemies
* Where to place your chess
* All the previous steps
* Checkmate notification

<img src="pre/Animations.png" width="400" alt="animation icons">

Save and replay function
* retract false move
* watch a played game
* play from where you left

Play remotely with LAN connection
* connect with friends around you in CS classes
* watch others play the game of chess


***
## AI Algorithms
### Minimax and Alpha-Beta pruning search
As is informed by the requirement, we use minimax search as well as Alpha-Beta pruning search to
implement our AI algorithms.

<img src="pre/AI_1.png" width="400" alt="Minimax and Alpha-Beta pruning search">

```java
//src/main/java/edu/sustech/chessking/gameLogic/ai/AiEnemy.java
public class AiEnemy {
    
    //alpha-beta pruning
    private int searchMax(int availableMax, int index) {
        //System.out.println("Search max with index " + index);
        int maxScore = negativeInfinite;
        ArrayList<Move> availableMove = gameCore.getAvailableMove();
        //ranking the moves from best to worst
        availableMove.sort((o1, o2) -> getScore(o2) - getScore(o1));

        int score;
        for (Move move : availableMove) {
            score = getMaxSearchScore(index, maxScore, move);
            if (score > availableMax) {
                return score;
            }
            maxScore = Math.max(maxScore, score);
        }
        return maxScore;
    }
}
```
### Difficulties
We set up three levels of AI:
* Easy: 2 search depth, no time limit
* Normal: 4 search depth, game time limit as 90 minutes
* Hard: 6 search depth, game time limit as 60 minutes


### Optimization
#### ranking

```
availableMove.sort((o1, o2) -> getScore(o2) - getScore(o1));
```
Calculate an approximate score for each move and rank them from high to low. 
This helps improve the pruning efficiency.

#### The end of searching tree
In order to reduce the chance of misjudging at the end of the searching tree,
we use a more complicated method calculate the score, like calculate the expected exchange score.

```java
//src/main/java/edu/sustech/chessking/gameLogic/ai/EvaluationMethod.java
public class EvaluationMethod {
    public static int getAccurateScore(Move move, GameCore gameCore) {
        /* other codes */
        
        int exchangeScore = 0;
        Chess enemyChess, allyChess = chess;
        int i = 0, j = 0;
        while (i < enemyList.size()) {
            enemyChess = enemyList.get(i);
            //if no chess to eat the enemyChess, or it is weaker, then eat
            if (j >= allyList.size() ||
                    getChessScore(enemyChess) <= getChessScore(allyChess))
                exchangeScore -= getChessScore(allyChess);
            else
                break;

            if (j >= allyList.size())
                break;
            allyChess = allyList.get(j);
            //If no enemy chess can eat, or allayChess is weaker, then eat
            if (i + 1 >= enemyList.size() ||
                    getChessScore(allyChess) <= getChessScore(enemyChess))
                exchangeScore += getChessScore(enemyChess);
            else
                break;
            ++i;
            ++j;
        }
        score += exchangeScore;

        /* other codes */
    }
}
```

### Multiply threads
Since at the highest level of AI needs time to do its calculation, put it in the 
FXMainThread will result in window no-response. Hence, we create a new thread for its calculation:

```java
//src/main/java/edu/sustech/chessking/ChessKingApp.java
public class ChessKingApp extends GameApplication {
    @Override
    protected void onUpdate(double tpf) {
        /* other codes */
        
        Thread thread = new Thread(() -> {
            Move move = ai.getNextMove();
            Entity chess = getChessEntity(
                    toPoint(move.getChess().getPosition()));
            if (chess == null)
                throw new RuntimeException("Cannot find chess!");
            chess.getComponent(ChessComponent.class).computerExecuteMove(move);
        });
        thread.setDaemon(true);
        thread.start();

        /* other codes */
    }
}
```

*** 
## Multiplayer
### Network Interface provided by FXGL
FXGL provides many useful interfaces for network, without which we have no way finish lan multi-playing.
In general, FXGL provides a Connection class which may add handler messages and send messages, 
with which we are able to build the multiplayer system.

```
//Create connection
var client = FXGL.getNetService().newTCPClient("localhost", 55555);
client.setOnConnected(connection -> {
    connection.addMessageHandlerFX((conn, message) -> {
          /* do something here */
    });
});

//add handler and send message
Connection<Bundle> connection = ...
connection.addMessageHandlerFX((conn, message) -> {
    /* do something here */
});

var data = new Bundle("");
data.put("key", "value");
connection.send(data);
```
### Structure



### Abstract class for information receive
Since time interval from request from the server to receive the result is not instant, 
we use the abstract class to construct



### Game Searching
We want the client can automatically search for any existing game within the lan.

***
## Regrets
### a)耦合度太高，没有满足单一职责原则，点名GameCore类，应该使用接口+继承
#### LittleEtx

_Some of your problems here plz._

#### MrBHAAA
Later I found that many of the subscenes inside the UI design package can actually inherit from a 
more general parent. For example, the ChooseLocalPlayer, ChoosePlayer2, LoadSave, LoadReplay, DeleteSave, 
DeletePlayer and DeleteReplay subscenes all have the same basic view elements consisting of a grey background,
a linear-gradient window, a title, a list in the middle, and some buttons at the bottom. 

We should have written a big ChooseSomething subscene to be the parent of all these subscenes. That would 
have made my work as a UI designer much easier. I could also have spent less time on wrtting a same 
background for each of my subscene.

<img src="pre/bad1.png" width="200" alt="bad1">
<img src="pre/bad2.png" width="200" alt="bad2">
<img src="pre/bad3.png" width="200" alt="bad3">
<img src="pre/bad4.png" width="200" alt="bad4">

### b)没有进行充分的测试，实例测试->Bug
### c)经常改架构