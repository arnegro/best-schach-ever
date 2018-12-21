Notes for Assignment 9 (Chess AI)
--------------------------------------------------------------------------------

o We recommend using an IDE (like eclipse) for working on this assignment.
  Create a new Java project and import the "src" folder.

o The game gui can be started with the main method of the ChessGui class. To be
  able to use your agent in gui, you will need to add it to the list of agents
  in this class.

o For testing your agent, please use the command line option "-Xmx2g" which
  limits the size of the JVM heap to 4GB (in eclipse you can add this under
  "Run -> Run Configurations -> ChessGui -> Arguments -> VM arguments"). If your
  agents crosses this threshold, an OutOfMemoryError will be thrown which can be
  caught if you want.

o For experimenting, you can change the time per move by adjusting the constant 
  PLAYER_TIMEOUT in the class ChessGui (time in ms). Please note that for
  the evaluation, the value 30000 will be used (pending further tests, a higher
  value may instead be used to account for the server running the games being
  slightly slower, however your agent should be prepared for being given less 
  time than expected by registering preliminary moves)

o If your agent is still running after the timelimit has passed, your agent will
  lose unless you have registered a preliminary move with updateMove (see next
  point). The thread running your agent will also receive an interrupt signal.
  This does not automatically kill the thread, but it will set a flag which can
  be checked with

    Thread.currentThread().isInterrupted()

  After receiving the interrupt, your agent must stop running at the next
  convenient time to free up resources.

o Your submission should consist of (the source code for) a class implementing
  the ChessPlayer interface, you are not allowed to modify any other file in the
  program, so please make sure your agent works with the base version of the
  game as distributed.

  Important: please include some documentation for your agent (either as
  comments in the java file or as a separate document). Document which algorithm
  you are using, what the idea behind your heuristic/evaluation function is, etc.

o The most important part of the agent ist the method generateNextMove. The
  argument to this method is an instance of the ChessGameConsole interface. This
  interface gives access to the following methods:

  - getGame: returns the current game state

  - getTimeLeft: returns the milliseconds left for the current turn

  - updateMove: registers the move you want to play. This can be called multiple
    times to update the selected move and it is advisable to call this method at
    least once early in the turn to avoid forfeiting the game if the computation
    takes longer than expected.

o If possible, please avoid splitting your implementation across multiple files.
  If you need to create additional classes, please create them as inner classes
  (even if this might not be software engineering best practice) or prefix their
  name with your agent's name.

o Your code cannot rely on any libraries other than the java class libraries for
  Java 1.8

o Your agent should be single-threaded. While a multi-threaded agent will get
  marks for the assignment, it will be disqualified from the tournament.

o The class ChessGame contains several methods that provide information on the
  current state of the game, which can of course be used by your agent.

o Two agents are included in this framework to allow you to test your agent:

  - MrRandom: A very primitive agent that selects its moves randomly from the
    list of legal moves. Basically any agent should be able to beat this.

  - MrNovice: A minimax agent with a simple cutoff heuristic. After reaching a
    given depth in the gametree (or finding a leaf node) the current position
    will be evaluated by counting the material value of both players and whether
    a player's king is in check.

o If you find any bugs in the code, please report them to me, e.g. via ILIAS

--------------------------------------------------------------------------------

Author:
Hauke Neitzel
