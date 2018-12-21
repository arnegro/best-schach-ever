package de.cogsys.ai.chess.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import de.cogsys.ai.chess.game.Figure;

public class PawnPromotionDialog extends JDialog {
	

	private static final long serialVersionUID = 1L;

	private static class PawnPromotionChoicePanel extends JPanel implements MouseListener {
		
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
		
		public static final Color COLOR_BACKGROUND = Color.BLACK;
		public static final Color COLOR_BORDER     = new Color(50, 50, 50);
		
		private int color;
		private PawnPromotionDialog parent;
		
		static {
			BOARDWIDTH = CHOICES.length * (CELLWIDTH + CELLMARGIN) + CELLMARGIN;
			BOARDHEIGHT = CELLHEIGHT + 2 * CELLMARGIN;
			
			WIDTH = BOARDWIDTH + 2 * PANELMARGIN;
			HEIGHT = BOARDHEIGHT + 2 * PANELMARGIN;
		}

		public PawnPromotionChoicePanel(int color, PawnPromotionDialog parent) {
			
			this.color = color;
			this.parent = parent;
			
			final Dimension dimension = new Dimension(WIDTH, HEIGHT);
			this.setPreferredSize(dimension);
			this.setMinimumSize(dimension);
			this.setMaximumSize(dimension);
	        this.setSize(dimension);

			this.addMouseListener(this);
		}
		
		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);

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
			g2d.fillRect(PANELMARGIN, PANELMARGIN, BOARDWIDTH, BOARDHEIGHT);
			Stroke stroke = g2d.getStroke();
			//
			// draw cells.
			//
			final int offy = PANELMARGIN + CELLMARGIN;
			int offx = PANELMARGIN + CELLMARGIN;

			for (int j = 0; j < CHOICES.length; j++) {
                if ((j % 2) == 0) {
                    g2d.setColor(COLOR_CELL_DARK);
                } else {
                    g2d.setColor(COLOR_CELL_LIGHT);
                }
                //
                g2d.fillRect(
                    offx + 1, offy + 1, 
                    CELLWIDTH - 1, 
                    CELLHEIGHT - 1 
                );
                //
			    final int value = Figure.make_figure(color, CHOICES[j]);
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
			    offx += (CELLWIDTH + CELLMARGIN);
			}
	        g2d.setStroke(stroke);
		}

		@Override
		public void mousePressed(MouseEvent e) {

	        final int x = e.getX();
	        final int y = e.getY();
	        
	        final int x_board = x - PANELMARGIN;
	        final int y_board = y - PANELMARGIN;
	        
	        if (y_board > CELLMARGIN &&
        		y_board < CELLMARGIN + CELLHEIGHT) {
	        	
	        	// offset within the cell (starting at the margin)
	        	final int x_offs = x_board % (CELLMARGIN + CELLHEIGHT);
	        	if (x_offs > CELLMARGIN && 
        			x_offs < CELLMARGIN+CELLHEIGHT) {
	        		
	        		// cell that was selected
	        		final int x_cell = x_board / (CELLMARGIN + CELLHEIGHT);
	        		if (x_cell >= 0 && x_cell < CHOICES.length) {
	        			parent.makeChoice(CHOICES[x_cell]);
	        		}
	        	}
	        }
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		
	}
	
	public static final int[] CHOICES = {
		Figure.ROOK,
		Figure.KNIGHT,
		//Figure.BISHOP,
		Figure.QUEEN
	};
	
	private int choice = Figure.EMPTY;
	
	public PawnPromotionDialog(JFrame parent, int color) {
		super(parent, "Choose your pawn promotion", true);
		getContentPane().add(new PawnPromotionChoicePanel(color, this));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
	}
	
	public int showDialog() {
		this.setVisible(true);
		return choice;
	}
	
	private void makeChoice(int choice) {
		this.choice = choice;
		this.setVisible(false);
		this.dispose();
	}
	
    public JRootPane createRootPane() {
        JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        Action action = new AbstractAction() {
             
            private static final long serialVersionUID = 1L;
 
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        };
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", action);
        return rootPane;
    }

}
