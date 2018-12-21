package de.cogsys.ai.chess.gui;

import java.awt.image.BufferedImage;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;


/**
 * @author Sebastian Otte
 */
public class ChessRes {
	
	public static final BufferedImage IMG_WHITE_PAWN;
	public static final BufferedImage IMG_WHITE_ROOK;
	public static final BufferedImage IMG_WHITE_KNIGHT;
	public static final BufferedImage IMG_WHITE_BISHOP;
	public static final BufferedImage IMG_WHITE_KING;
	public static final BufferedImage IMG_WHITE_QUEEN;
    
    public static final BufferedImage IMG_BLACK_PAWN;
    public static final BufferedImage IMG_BLACK_ROOK;
    public static final BufferedImage IMG_BLACK_KNIGHT;
    public static final BufferedImage IMG_BLACK_BISHOP;
    public static final BufferedImage IMG_BLACK_KING;
    public static final BufferedImage IMG_BLACK_QUEEN;
	
    public static final URL load(final String resource) throws URISyntaxException {
    	final URL url = (
    		ClassLoader.getSystemClassLoader().getResource(resource)
    	);
    	return url;
    }
    
	static {
		try {
			//
			IMG_WHITE_PAWN   = ImageIO.read(load("resources/white_pawn.png"));
		    IMG_WHITE_ROOK   = ImageIO.read(load("resources/white_rook.png"));
		    IMG_WHITE_KNIGHT = ImageIO.read(load("resources/white_knight.png"));
		    IMG_WHITE_BISHOP = ImageIO.read(load("resources/white_bishop.png"));
		    IMG_WHITE_KING   = ImageIO.read(load("resources/white_king.png"));
		    IMG_WHITE_QUEEN  = ImageIO.read(load("resources/white_queen.png"));
		    //
		    IMG_BLACK_PAWN   = ImageIO.read(load("resources/black_pawn.png"));
		    IMG_BLACK_ROOK   = ImageIO.read(load("resources/black_rook.png"));
		    IMG_BLACK_KNIGHT = ImageIO.read(load("resources/black_knight.png"));
		    IMG_BLACK_BISHOP = ImageIO.read(load("resources/black_bishop.png"));
		    IMG_BLACK_KING   = ImageIO.read(load("resources/black_king.png"));
		    IMG_BLACK_QUEEN  = ImageIO.read(load("resources/black_queen.png"));
		    //
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}