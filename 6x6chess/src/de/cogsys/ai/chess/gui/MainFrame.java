package de.cogsys.ai.chess.gui;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

/**
 * @author Sebastian Otte
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private ChessGamePanel gamepanel;
	private StatusPanel    statuspanel;
	
	public ChessGamePanel getGamePanel() {
	    return this.gamepanel;
	}
	
	public StatusPanel getStatusPanel() {
	    return this.statuspanel;
	}
	
	public MainFrame() {
		super("Los Alamos Chess - Cogsys AI Competition 2018/2019");
		//
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		//
		this.gamepanel = new ChessGamePanel(this);
		this.statuspanel = new StatusPanel();
		//
		this.add(this.gamepanel);
		this.add(this.statuspanel);
		this.pack();
		//
		this.setResizable(false);
	}
	
	
}