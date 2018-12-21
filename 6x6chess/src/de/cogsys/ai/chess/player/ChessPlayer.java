package de.cogsys.ai.chess.player;

import de.cogsys.ai.chess.control.ChessGameConsole;

/**
 * @author Sebastian Otte
 */
public abstract class ChessPlayer {
	
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	
	/**
	 * Initializes the player.
	 * @param color The color of the player for this game.
	 */
	public void initialize(final int color) {}
	/**
	 * Generates a next move based on the current game situation.
	 * @param console Instance of ChessGameConsole, which provides
	 * the current game situation, the remaining time and a method
	 * for "storing" the next move.
	 */
	public abstract void generateNextMove(final ChessGameConsole console);
	
}