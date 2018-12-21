package de.cogsys.ai.chess.game;

/**
 * @author Sebastian Otte
 */
public class Figure {

    public static final int EMPTY = 0x0;
	
	public static final int FIGURE_MASK = 0x0F;
    
    public static final int PAWN   = 0x1;
    public static final int ROOK   = 0x2;
    public static final int KNIGHT = 0x3;
    public static final int BISHOP = 0x4;
    public static final int KING   = 0x5;
    public static final int QUEEN  = 0x6;
    
    public static final int COLOR_MASK  = 0xF0;
    public static final int COLOR_SHIFT = 4;
    
    public static final int BLACK = 0x2;
    public static final int WHITE = 0x1;
        
    public static final int BLACK_PAWN   = (BLACK << COLOR_SHIFT) | PAWN;
    public static final int BLACK_ROOK   = (BLACK << COLOR_SHIFT) | ROOK;
    public static final int BLACK_KNIGHT = (BLACK << COLOR_SHIFT) | KNIGHT;
    public static final int BLACK_BISHOP = (BLACK << COLOR_SHIFT) | BISHOP;
    public static final int BLACK_KING   = (BLACK << COLOR_SHIFT) | KING;
    public static final int BLACK_QUEEN  = (BLACK << COLOR_SHIFT) | QUEEN;

    public static final int WHITE_PAWN   = (WHITE << COLOR_SHIFT) | PAWN;
    public static final int WHITE_ROOK   = (WHITE << COLOR_SHIFT) | ROOK;
    public static final int WHITE_KNIGHT = (WHITE << COLOR_SHIFT) | KNIGHT;
    public static final int WHITE_BISHOP = (WHITE << COLOR_SHIFT) | BISHOP;
    public static final int WHITE_KING   = (WHITE << COLOR_SHIFT) | KING;
    public static final int WHITE_QUEEN  = (WHITE << COLOR_SHIFT) | QUEEN;
    
    public static int figure(final int value) {
        return value & FIGURE_MASK;
    }
    
    public static int color(final int value) {
        return (value & COLOR_MASK) >> COLOR_SHIFT;
    }
    
    public static int other_color(final int color) {
        if (color == BLACK) return WHITE;
        if (color == WHITE) return BLACK;
        return EMPTY;
    }
    
    public static int make_figure(final int color, final int figure) {
    	return (color << COLOR_SHIFT) | figure;
    }
}