package de.cogsys.ai.chess.player;

import de.cogsys.ai.chess.game.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.cogsys.ai.chess.control.ChessGameConsole;

public class MrBoss extends ChessPlayer {
	
    private static final long TIME_THRESHOLD = 2000;
    private static final int  DEFAULT_DEPTH  = 5;
    private static final long DEFAULT_DELAY = 1000;
	
	private int mycolor;
		
    private int    depth;
    private long   delay;
    private Random rnd;
    
	Map<Integer,Integer> FigValues = new HashMap<Integer,Integer>();

	final double WinValue = 20000;
	final double CheckValue = 20;
	
    public MrBoss(final int depth, final long delay, final long seed) {
        this.depth = depth;
        this.delay = delay;
        this.rnd   = new Random(seed);
    	FigValues.put(Figure.PAWN, 10);
    	FigValues.put(Figure.ROOK, 60);
    	FigValues.put(Figure.BISHOP, 30);
    	FigValues.put(Figure.KNIGHT, 50);
    	FigValues.put(Figure.QUEEN, 200);
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

	
    private double evaluateGame(ChessGame game, int color) {
    	int[] board = game.getBoard();
    	
    	this.updateFigVals(game, color);
    	
    	 if (game.wins(color)) {
             return WinValue;
         } else if (game.wins(Figure.other_color(color))) {
             return -WinValue;
         } else if (game.isDraw()) {
             return 0;
         }
    	 
    	 double score = 0;
    	 
    	 if (game.isCheck(color)) {
             score -= CheckValue;
         }
         if (game.isCheck(Figure.other_color(color))) {
             score += CheckValue;
         }
    	
    	for (int fig : board) {
    		if (Figure.color(fig) == color) {
    			score += figValue(fig);
    		} else {
    			score += figValue(fig);
    		}
    	}
    	return score;
    }
	
	private int figValue(int fig) {

        fig = Figure.figure(fig);
		
		if (FigValues.containsKey(fig))
			return FigValues.get(fig);
		else
			return 0;
	}

	@Override
    public void generateNextMove(final ChessGameConsole c) {

    	final long startTime = c.getTimeLeft();

        final ChessGame g = c.getGame();

        System.out.println("Searching " + g.generateValidMoves().size() + " moves");

        double maxscore = Double.NEGATIVE_INFINITY;
		double alpha = Double.NEGATIVE_INFINITY;
        List<ChessMove> bestmoves = new ArrayList<ChessMove>();

		
		List<ChessMove> moves = g.generateValidMoves();
		Collections.shuffle(moves, rnd);

		for (ChessMove m : moves) {
            final double score = min(g.performMove(m), c, this.depth, alpha, Double.POSITIVE_INFINITY);
            
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
            alpha = Math.max(alpha, maxscore);
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
    
    private double min(final ChessGame game, final ChessGameConsole c, final int depth, double alpha, double beta) {
        if ((depth <= 0) || game.ends() || (c.getTimeLeft() < TIME_THRESHOLD)) {
            return evaluateGame(game, this.mycolor);
        }

        final List<ChessMove> moves = game.generateValidMoves();
        Collections.shuffle(moves, this.rnd);

        double minscore = Double.POSITIVE_INFINITY;

        for (ChessMove m : moves) {
            final double score = max(game.performMove(m), c, depth - 1, alpha, beta);

            if (score < minscore) {
                minscore = score;
            }
            if (minscore <= alpha) {
            	return minscore;
            }
            beta = Math.min(beta, minscore);
        }

        return minscore;        
    }
    
    private double max(final ChessGame game, final ChessGameConsole c, final int depth, double alpha, double beta) {
        if ((depth <= 0) || game.ends() || (c.getTimeLeft() < TIME_THRESHOLD)) {
            return evaluateGame(game, this.mycolor);
        }

        final List<ChessMove> moves = game.generateValidMoves();
        Collections.shuffle(moves, this.rnd);
        
        double maxscore = Double.NEGATIVE_INFINITY;

        for (ChessMove m : moves) {
            final double score = min(game.performMove(m), c, depth - 1, alpha, beta);

            if (score > maxscore) {
                maxscore = score;
            }
            if (maxscore >= beta) {
            	return maxscore;
            }
            alpha = Math.max(alpha, maxscore);
        }

        return maxscore;
    }
    
    private void updateFigVals(ChessGame game, int color) {
    	
    }
    
}
