package de.cogsys.ai.chess.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.cogsys.ai.chess.control.ChessGameConsole;
import de.cogsys.ai.chess.game.ChessGame;
import de.cogsys.ai.chess.game.ChessMove;
import de.cogsys.ai.chess.game.Figure;

/**
 * @author Sebastian Otte
 */
public class MrNovice extends ChessPlayer {
	
    private static final long TIME_THRESHOLD = 2000;
    private static final int  DEFAULT_DEPTH  = 4;
    private static final long DEFAULT_DELAY = 1000;
	
	private int mycolor;
		
    private int    depth;
    private long    delay;
    private Random rnd;

    public MrNovice(final int depth, final long delay, final long seed) {
        this.depth = depth;
        this.delay = delay;
        this.rnd   = new Random(seed);
    }

    public MrNovice(final int depth, final int delay) {
        this(depth, delay, System.currentTimeMillis());
    }
    
    public MrNovice(final int depth) {
    	this(depth, DEFAULT_DELAY, System.currentTimeMillis());
    }
    
    public MrNovice() {
        this(DEFAULT_DEPTH, DEFAULT_DELAY, System.currentTimeMillis());
    }


    @Override
    public void initialize(final int color) {

        this.mycolor = color;
    }
    
    private static final double SCORE_WIN    = 1000;
    private static final double SCORE_DRAW   = 0;

    private static final double SCORE_PAWN   = 10;
    private static final double SCORE_ROOK   = 50;
    private static final double SCORE_BISHOP = 50;
    private static final double SCORE_KNIGHT = 50;
    private static final double SCORE_QUEEN  = 200;

    private static final double SCORE_CHECK     = 20;
    
    private static double evaluateGame(final ChessGame game, int color) {

        double score = 0;

        if (game.wins(color)) {
            return SCORE_WIN;
        } else if (game.wins(Figure.other_color(color))) {
            return -SCORE_WIN;
        } else if (game.isDraw()) {
            return SCORE_DRAW;
        } 

        if (game.isCheck(color)) {
            score -= SCORE_CHECK;
        }
        if (game.isCheck(Figure.other_color(color))) {
            score += SCORE_CHECK;
        }

        final int[] board = game.getBoard();
        for (int i = 0; i < board.length; i++) {
            final int cell_value  = board[i];
            final int cell_figure = Figure.figure(cell_value);
            final int cell_color  = Figure.color(cell_value);

            double figurescore = 0;

            switch (cell_figure) {
                case Figure.PAWN:
                    figurescore = SCORE_PAWN; 
                    break;
                case Figure.ROOK:
                    figurescore = SCORE_ROOK;
                    break;
                case Figure.BISHOP:
                    figurescore = SCORE_BISHOP;
                    break;
                case Figure.KNIGHT:
                    figurescore = SCORE_KNIGHT;
                    break;
                case Figure.QUEEN:
                    figurescore = SCORE_QUEEN;
                    break;
            }

            if (cell_color == color) {
                score += figurescore;
            } else {
                score -= figurescore;
            }
        }

        return score;
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

}