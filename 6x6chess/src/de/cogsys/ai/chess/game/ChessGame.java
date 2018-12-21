package de.cogsys.ai.chess.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sebastian Otte
 */
public class ChessGame {

	public static final int WIDTH  = 6;
	public static final int HEIGHT = 6;
	
	public static final int UP   = 1;
	public static final int DOWN = -1;
	
	public static final int LEFT  = -1;
	public static final int RIGHT = 1;

	public static final int LEFT_COLUMN = 0;
	public static final int RIGHT_COLUMN = WIDTH - 1;
	public static final int BOTTOM_ROW = 0;
	public static final int TOP_ROW = HEIGHT - 1;
	
	public static final int NONE = 0;
	public static final int PLAYER1  = Figure.WHITE;
	public static final int PLAYER2  = Figure.BLACK;
	
	public static final boolean BAD_BISHOP = true;
	
	public static final int MAXMOVES = 50;
	
	private int[] board;
	
	
	private int   player;
	private int   player1counter;
	private int   player2counter;
	
	public ChessGame() {

		this.board  = new int[WIDTH * HEIGHT];

        this.initialize();
    }
	
	/**
	 * Copy constructor for the game state
	 * @param ChessGame to copy
	 */
	public ChessGame(ChessGame src) {        
        this.player         = src.player;
        this.player1counter = src.player1counter;
        this.player2counter = src.player2counter;
        
        this.board = new int[WIDTH * HEIGHT];
		for (int i = 0; i < this.board.length; i++) {
			this.board[i] = src.board[i];
		}
	}
	
	
	private void initialize() {
		//
		// set initial figure positions.
		//
		for (int i = 0; i < this.board.length; i++) {
			this.board[i] = Figure.EMPTY;
		}
		//
		this.set(BOTTOM_ROW, 0, Figure.WHITE_ROOK);
		this.set(BOTTOM_ROW, 1, Figure.WHITE_KNIGHT);
		this.set(BOTTOM_ROW, 2, Figure.WHITE_QUEEN);
		this.set(BOTTOM_ROW, 3, Figure.WHITE_KING);
		this.set(BOTTOM_ROW, 4, Figure.WHITE_KNIGHT);
		this.set(BOTTOM_ROW, 5, Figure.WHITE_ROOK);
		//
		this.set(BOTTOM_ROW + UP, 0, Figure.WHITE_PAWN);
		this.set(BOTTOM_ROW + UP, 1, Figure.WHITE_PAWN);
		this.set(BOTTOM_ROW + UP, 2, Figure.WHITE_PAWN);
		this.set(BOTTOM_ROW + UP, 3, Figure.WHITE_PAWN);
		this.set(BOTTOM_ROW + UP, 4, Figure.WHITE_PAWN);
		this.set(BOTTOM_ROW + UP, 5, Figure.WHITE_PAWN);
		//
		this.set(TOP_ROW, 0, Figure.BLACK_ROOK);
		this.set(TOP_ROW, 1, Figure.BLACK_KNIGHT);
		this.set(TOP_ROW, 2, Figure.BLACK_QUEEN);
		this.set(TOP_ROW, 3, Figure.BLACK_KING);
		this.set(TOP_ROW, 4, Figure.BLACK_KNIGHT);
		this.set(TOP_ROW, 5, Figure.BLACK_ROOK);
		//
		this.set(TOP_ROW + DOWN, 0, Figure.BLACK_PAWN);
		this.set(TOP_ROW + DOWN, 1, Figure.BLACK_PAWN);
		this.set(TOP_ROW + DOWN, 2, Figure.BLACK_PAWN);
		this.set(TOP_ROW + DOWN, 3, Figure.BLACK_PAWN);
		this.set(TOP_ROW + DOWN, 4, Figure.BLACK_PAWN);
		this.set(TOP_ROW + DOWN, 5, Figure.BLACK_PAWN);
		//
		this.player         = PLAYER1;
		this.player1counter = 0;
		this.player2counter = 0;
	}
	
	@Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int i = TOP_ROW; i >= BOTTOM_ROW; i--) {
        	out.append(String.valueOf(i + 1) + " ");
            for (int j = LEFT_COLUMN; j <= RIGHT_COLUMN; j++) {
            	out.append("[");
            	String s = "";
            	//
            	final int cell = this.get(i,j);
            	//
            	switch (Figure.figure(cell)) {
	            	case Figure.PAWN:
	            		s = "p";
	            		break;
	            	case Figure.ROOK:
	            		s = "r";
	            		break;
	            	case Figure.KNIGHT:
	            		s = "n";
	            		break;
	            	case Figure.BISHOP:
	            		s = "b";
	            		break;
	            	case Figure.QUEEN:
	            		s = "q";
	            		break;
	            	case Figure.KING:
	            		s = "k";
	            		break;
	            	case Figure.EMPTY:
	            		s = " ";
	            		break;
	            	default:
	            		s = "x";
            	}
            	//
            	if (Figure.color(cell) == Figure.WHITE) {
            		s = s.toUpperCase();
            	}
            	//
            	out.append(s);
            	out.append("]");
            }
            out.append("\n");
        }
        out.append("   ");
        //
        for (int k = 0; k < WIDTH; k++) {
            if (k > 0) {
                out.append("  ");
            }
            out.append(String.valueOf((char)('A' + k)));
        }
        out.append("\n");

        return out.toString();
    }	
	
	// ----------------------------------------------------------------
	// Helper methods.
	// ----------------------------------------------------------------

	/**
	 * Returns the internal board.
	 */
	public int[] getBoard() {
		return this.board;
	}
	
    /**
     * Sets the value of the field at row i and column j.
     */
    public void set(final int i, final int j, final int value) {
        this.board[idx(i, j)] = value;
    }   
    
	/**
	 * Mapping from 2-dimensional to 1-dimension index.
	 */
    public static final int idx(final int i, final int j) {
        return (i * WIDTH) + j;
    }

    /**
     * Return the value of the field at row i and column j. 
     */
	public int get(final int i, final int j) {
		return this.board[idx(i, j)];
	}
	
	/**
	 * Return whether a given coordinate lies within the game area or not.
	 */
	public boolean isInGameArea(final int i, final int j) {
        if (j < LEFT_COLUMN)   return false;
        if (j > RIGHT_COLUMN)  return false;
        if (i > TOP_ROW)    return false;
        if (i < BOTTOM_ROW) return false;
        return true;
    }

	/**
	 * Returns whether the current situation is a draw.
	 */
    public boolean isDraw() {
    	//
    	return ((player1counter >= MAXMOVES) && 
    			(player2counter >= MAXMOVES)) || 
    			this.generateValidMoves().isEmpty();
    }
    
    /**
     * Returns whether given position is attackable by the specified color. 
     */
    public boolean isAttackable(final int i, final int j, final int color) {
    	if (!this.isInGameArea(i, j)) return false;
    	return (Figure.other_color(color) == Figure.color(this.get(i, j)));
    }
    /**
     * Returns whether the given position is accessible (empty).
     */
    public boolean isAccessible(final int i, final int j) {
    	if (!this.isInGameArea(i, j)) return false;
    	return (Figure.figure(this.get(i, j)) == Figure.EMPTY); 
    }
    
    /**
     * Returns whether the given player's king is in check situation.
     */
    public boolean isCheck(final int color) {

    	boolean kingfound = false;
    	int ki = -1;
    	int kj = -1;
    	
    	final int king = Figure.make_figure(color, Figure.KING);

    	for (int i = BOTTOM_ROW; i <= TOP_ROW; i++) {
    		for (int j = LEFT_COLUMN; j <= RIGHT_COLUMN; j++) {
    			if (this.get(i,j) == king) {
    				ki = i;
    				kj = j;
    				kingfound = true;
    				break;
    			}
    		}
    		if (kingfound) break;
    	}
    	assert(kingfound);

    	final List<ChessMove> moves = this.generateValidMoves(Figure.other_color(color));

    	for (ChessMove m : moves) {
    		if ((m.i2 == ki) && (m.j2 == kj)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Returns whether the given player is in a checkmate situation.
     */
    public boolean isCheckMate(final int color) {
    	//
    	// the player is checkmate if
    	//   o it is their turn
    	//   o they are in check <- not in this version
    	//   o they have no valid moves (moves which leave the king in
    	//     check are invalid)
    	//
    	if (this.getCurrentPlayer() != color) {
    		return false;
    	}
    	/*
    	if (!this.isCheck(color)) {
    		return false;
    	}
    	*/
    	return this.generateValidMoves().isEmpty(); 	
    }
    
    /**
     * Returns the number of turns performed by the current player.
     */
    public int getTurns() {
        return this.getTurns(this.getCurrentPlayer());
    }
    
    /**
     * Returns the number of turns performed by the given player.
     */
    public int getTurns(final int color) {
        if (color == PLAYER1) {
            return this.player1counter;
        } else {
            return this.player2counter;
        }
    }

    // ----------------------------------------------------------------
    // Game methods (obligatory).
    // ----------------------------------------------------------------

    public int getCurrentPlayer() {
        return this.player;
    }

    public int getOtherPlayer() {
        return Figure.other_color(this.player);
    }

    public boolean wins(final int player) {
        //
        // the given player wins, if the other player is the current player
        // and in a check mate situation.
        //
        final int other = Figure.other_color(player);
        return (this.player == other) && this.isCheckMate(other);
    }

    public boolean ends() {
        //
        // the game ends if
        //   o there is a draw.
        //   o one player wins the game.
        //
        return this.isDraw() || this.wins(PLAYER1) || this.wins(PLAYER2);
    }

	public int result() {
		if (wins(PLAYER1)) {
			return PLAYER1;
		}
		else if (wins(PLAYER2)) {
			return PLAYER2;
		}
		else {
			return NONE;
		}
	}

    private void addPawnMoves(final int i, final int j, final int color, final List<ChessMove> moves) {

        final int df  = color == Figure.WHITE ? UP : DOWN;
        final int promotionRank = color == Figure.WHITE ? TOP_ROW : BOTTOM_ROW;
        
        List<ChessMove> pawnMoves = new ArrayList<ChessMove>();

        if (this.isAttackable(i + df, j +  LEFT, color)) {
            pawnMoves.add(new ChessMove(i, j, i + df, j + LEFT));
        }
        if (this.isAttackable(i + df, j + RIGHT, color)) {
        	pawnMoves.add(new ChessMove(i, j, i + df, j + RIGHT));
        }
        if (isAccessible(i + df, j)) {
        	pawnMoves.add(new ChessMove(i, j, i + df, j));
        }
        // Possibility of different Promotions
        if (i + df == promotionRank) {
        	for (ChessMove m : pawnMoves) {
        		//moves.add(new ChessMove(m, Figure.ROOK));
        		//moves.add(new ChessMove(m, Figure.KNIGHT));
        		//moves.add(new ChessMove(m, Figure.BISHOP));
        		moves.add(new ChessMove(m, Figure.QUEEN));
        	}
        }
        else {
        	moves.addAll(pawnMoves);
        }
    }

    private void addKnightMoves(final int i, final int j, final int color, final List<ChessMove> moves) {
        //
        final int du  = (color == Figure.WHITE)?(UP):(DOWN);
        final int dd  = (color == Figure.WHITE)?(DOWN):(UP);
        final int dl  = (color == Figure.WHITE)?(LEFT):(RIGHT);
        final int dr  = (color == Figure.WHITE)?(RIGHT):(LEFT);
        //
        {
            final int ni = i + du;
            final int nj = j + dl + dl;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        } {
            final int ni = i + du + du;
            final int nj = j + dl;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        } {
            final int ni = i + du;
            final int nj = j + dr + dr;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        } {
            final int ni = i + du + du;
            final int nj = j + dr;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        } {
            final int ni = i + dd;
            final int nj = j + dl + dl;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        } {
            final int ni = i + dd + dd;
            final int nj = j + dl;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        } {
            final int ni = i + dd;
            final int nj = j + dr + dr;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        } {
            final int ni = i + dd + dd;
            final int nj = j + dr;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        }
    }
    
    private void addRookMoves(final int i, final int j, final int color, final List<ChessMove> moves) {
        //
        final int du  = (color == Figure.WHITE)?(UP):(DOWN);
        final int dd  = (color == Figure.WHITE)?(DOWN):(UP);
        final int dl  = (color == Figure.WHITE)?(LEFT):(RIGHT);
        final int dr  = (color == Figure.WHITE)?(RIGHT):(LEFT);
        //
        // "up"
        //
        {
            int ni = i + du;
            while (this.isAccessible(ni, j)) {
                moves.add(new ChessMove(i, j, ni, j));
                ni += du;
            }
            //
            if (this.isAttackable(ni, j, color)) {
                moves.add(new ChessMove(i, j, ni, j));
            }
        }
        //
        // "down"
        //
        {
            int ni = i + dd;
            while (this.isAccessible(ni, j)) {
                moves.add(new ChessMove(i, j, ni, j));
                ni += dd;
            }
            //
            if (this.isAttackable(ni, j, color)) {
                moves.add(new ChessMove(i, j, ni, j));
            }
        }
        //
        // "left"
        //
        {
            int nj = j + dl;
            while (this.isAccessible(i, nj)) {
                moves.add(new ChessMove(i, j, i, nj));
                nj += dl;
            }
            //
            if (this.isAttackable(i, nj, color)) {
                moves.add(new ChessMove(i, j, i, nj));
            }
        }
        //
        // "right"
        //
        {
            int nj = j + dr;
            while (this.isAccessible(i, nj)) {
                moves.add(new ChessMove(i, j, i, nj));
                nj += dr;
            }
            //
            if (this.isAttackable(i, nj, color)) {
                moves.add(new ChessMove(i, j, i, nj));
            }
        }
    }
    
    private void addBishopMoves(final int i, final int j, final int color, final List<ChessMove> moves) {
        this.addBishopMoves(false, i, j, color, moves);
    }   
    
    private void addBishopMoves(final boolean badbishop, final int i, final int j, final int color, final List<ChessMove> moves) {
        //
        final int du  = (color == Figure.WHITE)?(UP):(DOWN);
        final int dd  = (color == Figure.WHITE)?(DOWN):(UP);
        final int dl  = (color == Figure.WHITE)?(LEFT):(RIGHT);
        final int dr  = (color == Figure.WHITE)?(RIGHT):(LEFT);
        //
        // "up-left"
        //
        {
            int ni = i + du;
            int nj = j + dl;
            //
            while (this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
                ni += du;
                nj += dl;
            }
            //
            if (this.isAttackable(ni, nj, color)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        }
        //
        // "up-right"
        //
        {
            int ni = i + du;
            int nj = j + dr;
            //
            while (this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
                ni += du;
                nj += dr;
            }
            //
            if (this.isAttackable(ni, nj, color)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        }
        //
        // "down-right"
        //
        {
            int ni = i + dd;
            int nj = j + dr;
            //
            while (this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
                ni += dd;
                nj += dr;
            }
            //
            if (this.isAttackable(ni, nj, color)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        }
        //
        // "down-left"
        //
        {
            int ni = i + dd;
            int nj = j + dl;
            //
            while (this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
                ni += dd;
                nj += dl;
            }
            //
            if (this.isAttackable(ni, nj, color)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        }
        //
        // bad bishop?
        //
        if (badbishop) {
            {
                final int ni = i + du;
                final int nj = j;
                if (this.isAccessible(ni, nj)) {
                    moves.add(new ChessMove(i, j, ni, nj));
                }
            } {
                final int ni = i;
                final int nj = j + dr;
                if (this.isAccessible(ni, nj)) {
                    moves.add(new ChessMove(i, j, ni, nj));
                }
            } {
                final int ni = i + dd;
                final int nj = j;
                if (this.isAccessible(ni, nj)) {
                    moves.add(new ChessMove(i, j, ni, nj));
                }
            } {
                final int ni = i;
                final int nj = j + dl;
                if (this.isAccessible(ni, nj)) {
                    moves.add(new ChessMove(i, j, ni, nj));
                }
            }
        }
    }    
    
    
    private void addQueenMoves(final int i, final int j, final int color, final List<ChessMove> moves) {

        addBishopMoves(i, j, color, moves);
        addRookMoves(i, j, color, moves);
    }    
    
    private void addKingMoves(final int i, final int j, final int color, final List<ChessMove> moves) {

        final int du  = (color == Figure.WHITE)?(UP):(DOWN);
        final int dd  = (color == Figure.WHITE)?(DOWN):(UP);
        final int dl  = (color == Figure.WHITE)?(LEFT):(RIGHT);
        final int dr  = (color == Figure.WHITE)?(RIGHT):(LEFT);

        {
            final int ni = i + du;
            final int nj = j;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        } {
            final int ni = i + du;
            final int nj = j + dr;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        } {
            final int ni = i;
            final int nj = j + dr;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        } {
            final int ni = i + dd;
            final int nj = j + dr;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        } {
            final int ni = i + dd;
            final int nj = j;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        } {
            final int ni = i + dd;
            final int nj = j + dl;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        } {
            final int ni = i;
            final int nj = j + dl;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        } {
            final int ni = i + du;
            final int nj = j + dl;
            if (this.isAttackable(ni, nj, color) || this.isAccessible(ni, nj)) {
                moves.add(new ChessMove(i, j, ni, nj));
            }
        }
    }    
    
    private void addValidMoves(final int i, final int j, final int color, final List<ChessMove> moves) {
    	
        final int value       = this.get(i, j);
        final int figure      = Figure.figure(value);
        final int figurecolor = Figure.color(value);

        if (figurecolor != color) return;

        switch (figure) {
            case Figure.PAWN:
                this.addPawnMoves(i, j, color, moves);
                break;
            case Figure.ROOK:
                this.addRookMoves(i, j, color, moves);
                break;
            case Figure.KNIGHT:
                this.addKnightMoves(i, j, color, moves);
                break;
            case Figure.BISHOP:
                this.addBishopMoves(BAD_BISHOP, i, j, color, moves);
                break;
            case Figure.QUEEN:
                this.addQueenMoves(i, j, color, moves);
                break;
            case Figure.KING:
                this.addKingMoves(i, j, color, moves);
                break;
        }
    }
    
    private ChessGame performMove(final ChessMove move, final int color) {
        final ChessGame game = new ChessGame(this);

        // switch player.
        game.player         = Figure.other_color(color);

        // count move.
        if (this.getCurrentPlayer() == PLAYER1) {
            game.player1counter++;
        } else {
            game.player2counter++;
        }

        // promotion occurs when a pawn moves to rank 1 or 6
        if (Figure.figure(this.get(move.i1, move.j1)) == Figure.PAWN && (move.i2 == 0 || move.i2 == 5)) {
        	game.set(move.i2, move.j2, Figure.make_figure(color, Figure.QUEEN));
        }
        else {
        	game.set(move.i2, move.j2, game.get(move.i1, move.j1));
        }
        game.set(move.i1, move.j1, Figure.EMPTY);

        return game;
    }

    public ChessGame performMove(final ChessMove move) {
        //
        final List<ChessMove> moves = new ArrayList<ChessMove>();
        this.addValidMoves(move.i1, move.j1, this.getCurrentPlayer(), moves);
        //
        boolean valid = false;
        //
        for (ChessMove m : moves) {
            if (move.equals(m)) {
                valid = true;
                break;
            }
        }
        //
        if (!valid) {
            throw new RuntimeException("Invalid move!");
        }
        //
        return this.performMove(move, this.getCurrentPlayer());
    }
    
    public List<ChessMove> generateValidMoves(final int color) {
        //
        final int imin  = (color == Figure.WHITE)?(BOTTOM_ROW):(TOP_ROW);
        final int imax  = (color == Figure.WHITE)?(TOP_ROW):(BOTTOM_ROW);
        final int jmin  = (color == Figure.WHITE)?(LEFT_COLUMN):(RIGHT_COLUMN);
        final int jmax  = (color == Figure.WHITE)?(RIGHT_COLUMN):(LEFT_COLUMN);
        final int istep = (color == Figure.WHITE)?(UP):(DOWN);
        final int jstep = (color == Figure.WHITE)?(RIGHT):(LEFT);
        //
        final List<ChessMove> moves = new ArrayList<ChessMove>();
        //
        for (int i = imin; i != (imax + istep); i += istep) {
            for (int j = jmin; j != (jmax + jstep); j+= jstep) {
                //
                final int value  = this.get(i, j);
                final int ccolor = Figure.color(value);
                //
                if (ccolor == color) {
                    addValidMoves(i, j, color, moves);
                }
            }
        }
        //
        return moves;
    }

    public List<ChessMove> generateValidMoves() {
        final List<ChessMove> rawmoves = this.generateValidMoves(this.player);
        final List<ChessMove> moves    = new ArrayList<ChessMove>();
        //
        for (ChessMove m : rawmoves) {
            if (!this.performMove(m, this.player).isCheck(this.player)) {
                moves.add(m);
            }
        }
        return moves;
    }

	public boolean isValidMove(ChessMove selectedMove) {
		return generateValidMoves().contains(selectedMove);
	}
	
	private static final String[] pieceEncode = {"ERROR", "", "R", "N", "B", "K", "Q"};

	public String encodeMove(ChessMove move) {
		
		List<ChessMove> allMoves = this.generateValidMoves();
		ChessGame result = this.performMove(move, this.player);
		
		String moveNotation = "";
		
		moveNotation += pieceEncode[Figure.figure(this.get(move.i1, move.j1))];
		
		// Check whether there are multiple pieces of the same type that can move the same square
		List<ChessMove> ambiguous = allMoves.stream().filter(
				m -> m.i2 == move.i2 && 
			 	  	 m.j2 == move.j2 && 
			 	  	 this.get(m.i1, m.j1) == this.get(move.i1, move.j1)).collect(Collectors.toList());
		if (ambiguous.size() > 1) {
			if (ambiguous.stream().filter(m -> m.j1 == move.j1).count() == 1) {
				moveNotation += (char) ('a' + move.j1);
			}
			else if (ambiguous.stream().filter(m -> m.i1 == move.i1).count() == 1) {
				moveNotation += (char) ('1' + move.i1);
			}
			else {
				moveNotation += (char) ('a' + move.j1);
				moveNotation += (char) ('1' + move.i1);
			}
		}
		
		if (isAttackable(move.i2, move.j2, this.getCurrentPlayer())) {
			moveNotation += "x";
		}
		
		moveNotation += (char) ('a' + move.j2);
		moveNotation += (char) ('1' + move.i2);
		
		// promotion happens when a pawn move to rank 1 or 6 (only to a Queen)
		if (Figure.figure(this.get(move.i1, move.j1)) == Figure.PAWN && (move.i2 == 0 || move.i2 == 5)) {
			moveNotation += "=Q";
		}
		
		if (result.isCheckMate(result.getCurrentPlayer())) {
			moveNotation += "#";
		}
		else if (result.isCheck(result.getCurrentPlayer())) {
			moveNotation += "+";
		}
		
		return moveNotation;
	}
    
    
    
}