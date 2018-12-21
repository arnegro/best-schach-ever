package de.cogsys.ai.chess.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import de.cogsys.ai.chess.gui.ChessGamePanel;

/**
 * @author Sebastian Otte
 */
public class StatusPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public static final int MSG_DELAY = 1500;

	public static final int WIDTH  = ChessGamePanel.WIDTH;
	public static final int HEIGHT = 140;
	
	public static final int MAX_MSG_SIZE = 4;

	public static final Font  FONT_TEXT    = new Font("Verdana", Font.PLAIN, 28);
	public static final Font  FONT_SUBTEXT = new Font("Verdana", Font.PLAIN, 18);
	public static final Color COLOR_TEXT   = new Color(220, 220, 220);
	
	public static final Color COLOR_TIME         = ChessGamePanel.COLOR_MARK_BLUE;
	public static final Color COLOR_CRITICALTIME = ChessGamePanel.COLOR_MARK_RED;
	
	public static final int   CRITICALTIME_THRESHOLD = 5;
	
	private String        status    = null;
	private List<String>  msgs      = new ArrayList<String>();
	private double 		  timeleft  = 110.24; 
	private Timer         timer     = null;
	private DecimalFormat format    = new DecimalFormat("00");
	
	public void status(final String status) {
	    this.status = status;
	    this.updateStatusPanel();
	}
	
	public void updateTimeLeft(final double timeleft) {
	    this.timeleft = timeleft;
	    this.updateStatusPanel();
	}
	
	public void updateStatusPanel() {
		this.repaint();
	}
	
	//private void 
	
	public void msg(final String msg) {
		synchronized (this.msgs) {
			this.msgs.add(msg);
			//System.out.println(msg);
			//
			while(this.msgs.size() > MAX_MSG_SIZE) {
			    this.msgs.remove(0);
			}
			//
		}
		this.updateStatusPanel();
		//
		if (this.timer == null) {
			this.timer = new Timer(MSG_DELAY, new ActionListener(){
				@Override
				public void actionPerformed(final ActionEvent arg) {
					synchronized (StatusPanel.this.msgs) {
						if (StatusPanel.this.msgs.size() > 0) {
							StatusPanel.this.msgs.remove(0);
						}
						if (StatusPanel.this.msgs.size() == 0) {
							StatusPanel.this.timer.stop();
						}
					}
					//
					StatusPanel.this.updateStatusPanel();
				}
			});
			this.timer.start();
		} else {
			this.timer.stop();
			this.timer.start();
		}
		//
	}
	
	public StatusPanel() {
		//
		final Dimension dimension = new Dimension(WIDTH, HEIGHT);
		this.setPreferredSize(dimension);
		this.setMinimumSize(dimension);
		this.setMaximumSize(dimension);
		this.setSize(dimension);
		//
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
		g2d.setColor(ChessGamePanel.COLOR_BACKGROUND);
		g2d.fillRect(0, 0, WIDTH, HEIGHT);
		//
		// draw board.
		//
		g2d.setColor(ChessGamePanel.COLOR_BORDER);
		g2d.fillRect(
		    ChessGamePanel.PANELMARGIN + ChessGamePanel.LABELBORDER, 
		    0, 
		    WIDTH - (2 * ChessGamePanel.PANELMARGIN + ChessGamePanel.LABELBORDER), 
		    HEIGHT - (ChessGamePanel.PANELMARGIN)
		);
        g2d.setColor(ChessGamePanel.COLOR_CELL);
        g2d.fillRect(
            ChessGamePanel.PANELMARGIN + ChessGamePanel.LABELBORDER + ChessGamePanel.CELLMARGIN,
            0 + ChessGamePanel.CELLMARGIN,
            WIDTH - (ChessGamePanel.LABELBORDER + 2 * (ChessGamePanel.PANELMARGIN + ChessGamePanel.CELLMARGIN)), 
            HEIGHT - (ChessGamePanel.PANELMARGIN + (2 * ChessGamePanel.CELLMARGIN))
        );
        //
        final int innerwidth  = WIDTH - (ChessGamePanel.LABELBORDER + 2 * (ChessGamePanel.PANELMARGIN + ChessGamePanel.CELLMARGIN));
        final int innerheight = HEIGHT - (2 * (ChessGamePanel.PANELMARGIN + ChessGamePanel.CELLMARGIN));
        g2d.fillRect(
            ChessGamePanel.PANELMARGIN + ChessGamePanel.CELLMARGIN + ChessGamePanel.LABELBORDER, 
            ChessGamePanel.PANELMARGIN + ChessGamePanel.CELLMARGIN, 
            innerwidth, 
            innerheight
        );
		//
        final int centerx = (ChessGamePanel.PANELMARGIN + ChessGamePanel.CELLMARGIN + ChessGamePanel.LABELBORDER) + (innerwidth / 2);
        final int centery = (ChessGamePanel.PANELMARGIN / 2) + (innerheight / 2);
        //
        String text = null;
        synchronized (this.msgs) {
	        if (this.msgs.size() > 0) {
	        	text = this.msgs.get(0);
	        } else {
	        	text = this.status;
	        }
        }
        //
        if (text != null) {
            g2d.setColor(COLOR_TEXT);
            g2d.setFont(FONT_TEXT);
            final Rectangle2D fontrect = FONT_TEXT.getStringBounds(text, g2d.getFontRenderContext());
            g2d.drawString(text, (int)(centerx - fontrect.getCenterX()), (int)(centery - fontrect.getCenterY()));
        }
        //
        if (this.timeleft >= 0) {
            if (this.timeleft < CRITICALTIME_THRESHOLD) {
            	g2d.setColor(COLOR_CRITICALTIME);
            } else {
            	g2d.setColor(COLOR_TIME);
            }
            //
            int seconds = (int)this.timeleft;
            int minutes = seconds / 60;
            seconds     = seconds - (minutes * 60);
            //
            final String time = "Time left: " + this.format.format(minutes) + ":" + this.format.format(seconds); 
            final Rectangle2D fontrect = FONT_SUBTEXT.getStringBounds(time, g2d.getFontRenderContext());
            g2d.setFont(FONT_SUBTEXT);
            g2d.drawString(time, (int)((innerwidth / 4) + centerx - fontrect.getCenterX()), (innerheight / 2) + (int)(centery - fontrect.getCenterY()));
        }

	}
	
	
	
}