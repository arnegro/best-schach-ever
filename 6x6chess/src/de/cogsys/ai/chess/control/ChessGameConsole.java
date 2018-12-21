package de.cogsys.ai.chess.control;

import de.cogsys.ai.chess.game.ChessGame;
import de.cogsys.ai.chess.game.ChessMove;

/**
 * @author Sebastian Otte
 */
public interface ChessGameConsole {
    /**
     * Returns the current game situation.
     */
	public ChessGame getGame();
    /**
     * Returns the remaining time for making a move in milliseconds. 
     */
	public long getTimeLeft();
    /**
     * Updates the current move that will be performed after timeout
     * or leaving the generateNextMove method of a player. This method
     * can be called multiple times during one turn and only the call
     * will be considered. 
     */
	public void updateMove(final ChessMove move);
}