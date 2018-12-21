package de.cogsys.ai.chess.player;

import java.util.List;
import java.util.Random;

import de.cogsys.ai.chess.control.ChessGameConsole;
import de.cogsys.ai.chess.game.ChessMove;


/**
 * @author Sebastian Otte
 */
public class MrRandom extends ChessPlayer {
	
	private Random rnd;
	private long   delay;

	public MrRandom(final Random rnd, final long delay) {
		this.rnd   = rnd;
		this.delay = delay;
	}
		
	
	public MrRandom(final Random rnd) {
		this(rnd, 0);
	}
	

	public MrRandom(final long delay) {
		this(new Random(System.currentTimeMillis()), delay);

	}
		
	public MrRandom() {
		this(0);
	}


    @Override
    public void initialize(final int color) {
        //
    }


    @Override
    public void generateNextMove(final ChessGameConsole c) {
        try {
            Thread.sleep(this.delay);
        } catch (InterruptedException e) {
            //
        }
        //
        final List<ChessMove> moves = c.getGame().generateValidMoves();
        if (moves.size() > 0) {
            c.updateMove(moves.get(rnd.nextInt(moves.size())));
            return;
        }
    }
	

}