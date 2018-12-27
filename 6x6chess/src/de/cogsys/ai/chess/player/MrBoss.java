package de.cogsys.ai.chess.player;

import de.cogsys.ai.chess.game.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.cogsys.ai.chess.control.ChessGameConsole;

public class MrBoss extends ChessPlayer {
	
    private static final long TIME_THRESHOLD = 2000;
    private static final int  DEFAULT_DEPTH  = 4;
    private static final long DEFAULT_DELAY = 1000;
	
	private int mycolor;
		
    private int    depth;
    private long   delay;
    private Random rnd;

    public MrBoss(final int depth, final long delay, final long seed) {
        this.depth = depth;
        this.delay = delay;
        this.rnd   = new Random(seed);
    }

    public MrBoss(final int depth, final int delay) {
        this(depth, delay, System.currentTimeMillis());
    }
    
    public MrBoss(final int depth) {
    	this(depth, DEFAULT_DELAY, System.currentTimeMillis());
    }
    
    public MrBoss() {
        this(DEFAULT_DEPTH, DEFAULT_DELAY, System.currentTimeMillis());
    }


    @Override
    public void initialize(final int color) {

        this.mycolor = color;
    }

	@Override
    public void generateNextMove(final ChessGameConsole c) {

    	final long startTime = c.getTimeLeft();

        final ChessGame g = c.getGame();

        System.out.println("Searching " + g.generateValidMoves().size() + " moves");

        double maxscore = Double.NEGATIVE_INFINITY;
        List<ChessMove> bestmoves = new ArrayList<ChessMove>();

		
		List<ChessMove> moves = g.generateValidMoves();
		Collections.shuffle(moves, rnd);

		for (ChessMove m : moves) {
            final double score = min(g.performMove(m), c, this.depth);
            
            if (Thread.currentThread().isInterrupted()) {
            	break;
            }

            if (score > maxscore) {
                maxscore = score;
                bestmoves.clear();
                bestmoves.add(m);
                c.updateMove(m);
            } else if (score == maxscore) {
                bestmoves.add(m);
            }
        }

        final ChessMove bestmove = bestmoves.get(rnd.nextInt(bestmoves.size()));
        c.updateMove(bestmove);
        
        final long timeElapsed = startTime - c.getTimeLeft();
        System.out.println("Took " + timeElapsed/1000.0 + " seconds to find a move");
        if (timeElapsed < delay) {
        	try {
				Thread.sleep(delay - timeElapsed);
			} catch (InterruptedException e) {}
        }
    } 
    
    private double min(final ChessGame game, final ChessGameConsole c, final int depth) {
        if ((depth <= 0) || game.ends() || (c.getTimeLeft() < TIME_THRESHOLD)) {
            return evaluateGame(game, this.mycolor);
        }

        final List<ChessMove> moves = game.generateValidMoves();
        Collections.shuffle(moves, this.rnd);

        double minscore = Double.POSITIVE_INFINITY;

        for (ChessMove m : moves) {
            final double score = max(game.performMove(m), c, depth - 1);

            if (score < minscore) {
                minscore = score;
            }
        }

        return minscore;        
    }
    
    private double max(final ChessGame game, final ChessGameConsole c, final int depth) {
        if ((depth <= 0) || game.ends() || (c.getTimeLeft() < TIME_THRESHOLD)) {
            return evaluateGame(game, this.mycolor);
        }

        final List<ChessMove> moves = game.generateValidMoves();
        Collections.shuffle(moves, this.rnd);
        
        double maxscore = Double.NEGATIVE_INFINITY;

        for (ChessMove m : moves) {
            final double score = min(game.performMove(m), c, depth - 1);

            if (score > maxscore) {
                maxscore = score;
            }
        }

        return maxscore;
    }
    
    private double evaluateGame(ChessGame game, int color) {
    	int[] board = game.getBoard();
    	double beThis = 0;
    	double beOther = 0;
    	
    	 if (game.wins(color)) {
             return 200;
         } else if (game.wins(Figure.other_color(color))) {
             return -200;
         } else if (game.isDraw()) {
             return 0;
         } 
    	
    	for (int fig : board) {
    		if (Figure.color(fig) == color) {
    			beThis += figValue(fig);
    		} else {
    			beOther += figValue(fig);
    		}
    	}
    	return beThis-beOther;
    }
	
	private int figValue(int fig) {

        fig = Figure.figure(fig);
		
		switch (fig) {
        case Figure.PAWN:
            return 10;
        case Figure.ROOK:
        	return 50;
        case Figure.BISHOP:
            return 30;
        case Figure.KNIGHT:
            return 30;
        case Figure.QUEEN:
            return 90;
		}
		return 0;
	}

}
