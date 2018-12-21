package de.cogsys.ai.chess.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.cogsys.ai.chess.game.ChessGame;
import de.cogsys.ai.chess.game.ChessMove;
import de.cogsys.ai.chess.game.Figure;


/**
 * @author Sebastian Otte
 */
public class ChessGamePanel extends JPanel implements MouseListener {
	private static final long serialVersionUID = 1L;

	public static final int CELLMARGIN = 4;
	public static final int CELLWIDTH  = 80;
	public static final int CELLHEIGHT = 80;
	
	public static final int PANELMARGIN = 20;
	
	public static final int BOARDWIDTH;
	public static final int BOARDHEIGHT;
	
	public static final int WIDTH;
	public static final int HEIGHT;
	
	public static final Color COLOR_CELL_DARK  = new Color(0x8C, 0x63, 0x3B);
	public static final Color COLOR_CELL_LIGHT = new Color(0xF2, 0xC9, 0x88);

	public static final int LABELBORDER = 20;
	
	public static final BasicStroke STROKE_GAMEAREA = new BasicStroke(2);

	public static final Font  FONT_BOARDLABEL   = new Font("Verdana", Font.BOLD, 16);
	public static final Color COLOR_BOARDLABEL  = new Color(180, 180, 200);
	
	public static Color COLOR_BACKGROUND = Color.BLACK;
	public static Color COLOR_CELL       = new Color(30, 30, 30);
	public static Color COLOR_BORDER     = new Color(50, 50, 50);
	
	static {
		BOARDWIDTH  = ChessGame.WIDTH * (CELLWIDTH  + CELLMARGIN) + CELLMARGIN;
		BOARDHEIGHT = ChessGame.HEIGHT * (CELLHEIGHT + CELLMARGIN) + CELLMARGIN;
		
		WIDTH  = PANELMARGIN + LABELBORDER + BOARDWIDTH + PANELMARGIN;
		HEIGHT = PANELMARGIN + LABELBORDER + BOARDHEIGHT + PANELMARGIN;
	}
	
	private static Color mix(final double mix, final Color c1, final Color c2) {
	    return new  Color(
	        (int)((1.0 - mix) * c1.getRed() + mix * c2.getRed()),
            (int)((1.0 - mix) * c1.getGreen() + mix * c2.getGreen()),
            (int)((1.0 - mix) * c1.getBlue() + mix * c2.getBlue())
	    );
	}
	
	private ChessGame game;
	
	private int[] marked = new int[0];
	
	public static Color COLOR_MARK_RED   = new Color(200, 50, 50);
	public static Color COLOR_MARK_GREEN = new Color(50, 200, 50);
    public static Color COLOR_MARK_BLUE  = new Color(30, 120, 200);
    public static Color COLOR_MARK_MOVE  = new Color(200, 200, 50);
    
    public static final Color COLOR_CELLMOVE_DARK  = mix(0.5, new Color(0x8C, 0x63, 0x3B), new Color(250, 120, 0));
    public static final Color COLOR_CELLMOVE_LIGHT = mix(0.5, new Color(0xF2, 0xC9, 0x88), new Color(250, 120, 0));
    
	public static final int MARK_NONE   = 0;
	public static final int MARK_RED    = 1;
	public static final int MARK_GREEN  = 2;
    public static final int MARK_BLUE   = 3;
	
	private List<ChessGamePanelListener> listener = new ArrayList<ChessGamePanelListener>();
	
	private ChessMove lastmove = null;
	
	public void addListener(final ChessGamePanelListener listener) {
	    this.listener.add(listener);
	}
	
	public void clearListener() {
	    this.listener.clear();
	}
	
	public JFrame frame;
	
	public ChessGamePanel(JFrame frame) {
		
		this.frame = frame;
		
		final Dimension dimension = new Dimension(WIDTH, HEIGHT);
		this.setPreferredSize(dimension);
		this.setMinimumSize(dimension);
		this.setMaximumSize(dimension);
        this.setSize(dimension);

		this.addMouseListener(this);
	}
	
	public void clearMarked() {
	    for (int i = 0; i < this.marked.length; i++) {
	        this.marked[i] = MARK_NONE;
	    }
	}
	
	
	
	public void addMarked(final int i, final int j, final int mark) {
	    this.marked[ChessGame.idx(i, j)] = mark;
	}
	
	public void update(final ChessGame game) {
	    this.update(game, null);
	}
	
	public void update(final ChessGame game, final ChessMove lastmove) {
		this.game   = game;
		this.marked = new int[game.getBoard().length];
		//
		this.lastmove = lastmove;
		//
	    this.repaint();
	}
	
	private void printBoardLabel(final int x, final int y, final String label, final Graphics2D g2d) {
		g2d.setColor(COLOR_BOARDLABEL);
		g2d.setFont(FONT_BOARDLABEL);
		final Rectangle2D rect = g2d.getFontMetrics().getStringBounds(label, g2d);
		g2d.drawString(label, x - (int)(rect.getWidth() / 2), y + (int)(rect.getHeight() / 2));
	}
	
	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		//
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(
		    RenderingHints.KEY_ANTIALIASING,
		    RenderingHints.VALUE_ANTIALIAS_ON
		);
		g2d.setRenderingHint(
		    RenderingHints.KEY_INTERPOLATION,
		    RenderingHints.VALUE_INTERPOLATION_BICUBIC
		);
		//
		// draw background.
		//
		g2d.setColor(COLOR_BACKGROUND);
		g2d.fillRect(0, 0, WIDTH, HEIGHT);
		//
		// draw board.
		//
		g2d.setColor(COLOR_BORDER);
		g2d.fillRect(PANELMARGIN + LABELBORDER, PANELMARGIN, BOARDWIDTH, BOARDHEIGHT);

		if (this.game == null) return;
		Stroke stroke = g2d.getStroke();
		//
		// draw cells.
		//
		int offy = PANELMARGIN + CELLMARGIN;
		int offx;
		//
		for (int i = ChessGame.HEIGHT - 1; i >= 0; i--) {
			//
			offx = PANELMARGIN + CELLMARGIN + LABELBORDER;
			//
			printBoardLabel(
				PANELMARGIN + ((LABELBORDER) / 2), offy + (CELLWIDTH / 2),
				"" + (i + 1), g2d
			);
			//
			for (int j = 0; j < ChessGame.WIDTH; j++) {
				//
			    g2d.setColor(COLOR_CELL);
                //
			    boolean lastmoveflag = false;
			    //
			    if (this.lastmove != null && (
			        (this.lastmove.i1 == i && this.lastmove.j1 == j) || 
			        (this.lastmove.i2 == i && this.lastmove.j2 == j))
			    ) {
			        lastmoveflag = true;
			    } 
                g2d.fillRect(offx, offy, CELLWIDTH, CELLWIDTH);
                //
                // border game area.
                //
                switch (this.marked[ChessGame.idx(i, j)]) {
                    case MARK_RED:
                        g2d.setColor(COLOR_MARK_RED);
                        break;
                    case MARK_GREEN:
                        g2d.setColor(COLOR_MARK_GREEN);
                        break;
                    case MARK_BLUE:
                        g2d.setColor(COLOR_MARK_BLUE);
                        break;
                    default:
                        if ((((i % 2) + j) % 2) == 0) {
                            if (lastmoveflag) {
                                g2d.setColor(COLOR_CELLMOVE_DARK);
                            } else {
                                g2d.setColor(COLOR_CELL_DARK);
                            }
                        } else {
                            if (lastmoveflag) {
                                g2d.setColor(COLOR_CELLMOVE_LIGHT);
                            } else {
                                g2d.setColor(COLOR_CELL_LIGHT);
                            }
                        }
                }
                //
                g2d.fillRect(
                    offx + 1, offy + 1, 
                    CELLWIDTH - 1, 
                    CELLHEIGHT - 1 
                );
                //
			    final int value = this.game.get(i, j);
			    //
			    if (value != Figure.EMPTY) {
			        BufferedImage img = null;
			        switch (value) {
			            case Figure.WHITE_PAWN:
			                img = ChessRes.IMG_WHITE_PAWN;
			                break;
                        case Figure.WHITE_ROOK:
                            img = ChessRes.IMG_WHITE_ROOK;
                            break;
                        case Figure.WHITE_KNIGHT:
                            img = ChessRes.IMG_WHITE_KNIGHT;
                            break;
                        case Figure.WHITE_BISHOP:
                            img = ChessRes.IMG_WHITE_BISHOP;
                            break;
                        case Figure.WHITE_KING:
                            img = ChessRes.IMG_WHITE_KING;
                            break;
                        case Figure.WHITE_QUEEN:
                            img = ChessRes.IMG_WHITE_QUEEN;
                            break;
                        case Figure.BLACK_PAWN:
                            img = ChessRes.IMG_BLACK_PAWN;
                            break;
                        case Figure.BLACK_ROOK:
                            img = ChessRes.IMG_BLACK_ROOK;
                            break;
                        case Figure.BLACK_KNIGHT:
                            img = ChessRes.IMG_BLACK_KNIGHT;
                            break;
                        case Figure.BLACK_BISHOP:
                            img = ChessRes.IMG_BLACK_BISHOP;
                            break;
                        case Figure.BLACK_KING:
                            img = ChessRes.IMG_BLACK_KING;
                            break;
                        case Figure.BLACK_QUEEN:
                            img = ChessRes.IMG_BLACK_QUEEN;
                            break;
			        }
			        if (img != null) {
                        g2d.drawImage(
                            img,
                            offx + 1, offy + 1, 
                            offx + CELLWIDTH - 1, 
                            offy + CELLWIDTH - 1, 
                            0, 0,
                            img.getWidth() - 1,
                            img.getHeight() - 1,
                            null
                        );
			        }
			    }
		    	//
			    offx += (CELLWIDTH + CELLMARGIN);
			}
			//
			offy += (CELLHEIGHT + CELLMARGIN);
		}
		offx = PANELMARGIN + CELLMARGIN + LABELBORDER;
		//
		for (int j = 0; j < ChessGame.WIDTH; j++) {
			printBoardLabel(
				offx + (CELLWIDTH / 2), offy + (LABELBORDER / 2),
				"" + (char)(65 + j), g2d
			);
			//
	    	//
		    offx += (CELLWIDTH + CELLMARGIN);
		}
		//
        g2d.setStroke(stroke);
		//
	}
	
	private void clickedCell(
	    final int i, final int j 
	) {
	    for (ChessGamePanelListener listener : this.listener) {
	        listener.clickedCell(i, j);
	    }
	}
	
    private int[] isOverCell(final int x, final int y) {
        int offy = PANELMARGIN + CELLMARGIN;
        int offx;
        //
        for (int i = ChessGame.HEIGHT - 1; i >= 0; i--) {
            //
            offx = PANELMARGIN + CELLMARGIN + LABELBORDER;
            //
            for (int j = 0; j < ChessGame.WIDTH; j++) {
                //
                if (
                    (x > offx) && 
                    (x < (offx + CELLWIDTH)) && 
                    (y > offy) && 
                    (y < (offy + CELLHEIGHT)) 
                ) {
                    final int cx = offx + (CELLWIDTH / 2);
                    final int cy = offy + (CELLHEIGHT / 2);
                    //
                    return new int[]{i, j, y - cy, x - cx};
                }
                //
                offx += (CELLWIDTH + CELLMARGIN);
            }
            //
            offy += (CELLHEIGHT + CELLMARGIN);
        }      
        return null;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        //
        final int x = e.getX();
        final int y = e.getY();
        //
        final int[] cell = this.isOverCell(x, y);
        if (cell != null) {
            this.clickedCell(cell[0], cell[1]);
        }
        //
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
	
	
	
}