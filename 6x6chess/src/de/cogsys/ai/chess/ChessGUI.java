package de.cogsys.ai.chess;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;

import de.cogsys.ai.chess.control.ChessGameControl;
import de.cogsys.ai.chess.game.ChessGame;
import de.cogsys.ai.chess.game.ChessMove;
import de.cogsys.ai.chess.gui.ChessGamePanel;
import de.cogsys.ai.chess.gui.ChessGamePanelListener;
import de.cogsys.ai.chess.gui.ChessRes;
import de.cogsys.ai.chess.gui.MainFrame;
import de.cogsys.ai.chess.gui.StatusPanel;
import de.cogsys.ai.chess.player.*;

/**
 * @author Sebastian Otte
 */
public class ChessGUI {

	public static <T> T selectDiaglog(final String caption, final String message, final T[] options,
			final T defaultoption, final Icon icon) {
		//
		@SuppressWarnings("unchecked")
		final T result = (T) JOptionPane.showInputDialog(null, message, caption, JOptionPane.QUESTION_MESSAGE, icon,
				options, defaultoption);
		//
		return result;
	}

	public static final String[] agents = {
		"Human", 
		"MrNovice (depth 2)", 
		"MrNovice (depth 3)", 
		"MrNovice (depth 4)",
		"MrBoss", 
		"MrRandom"
	};

	public static ChessPlayer createAgent(final String agent) {
		if (agent.equals(agents[0]))
			return new Human();
		if (agent.equals(agents[1]))
			return new MrNovice(2, 1000);
		if (agent.equals(agents[2]))
			return new MrNovice(3, 1000);
		if (agent.equals(agents[3]))
			return new MrNovice(4, 1000);
		if (agent.equals(agents[4]))
			return new MrBoss(2, 1000);
		if (agent.equals(agents[5]))
			return new MrRandom(1000);
		return null;
	}

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}

		String player1name = agents[0];
		String player2name = agents[1];

		ChessGUI gui = new ChessGUI();
		
		final MainFrame frame = new MainFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		do {
			// select player 1
			player1name = selectDiaglog("Select Player 1", "Select one of the following agents:", agents, player1name,
					new ImageIcon(ChessRes.IMG_WHITE_KING.getScaledInstance(128, 128, BufferedImage.SCALE_SMOOTH)));
			if (player1name == null) {
				break;
			}
			ChessPlayer p1 = createAgent(player1name);

			// select player 2
			player2name = selectDiaglog("Select Player 2", "Select one of the following agents:", agents, player2name,
					new ImageIcon(ChessRes.IMG_BLACK_KING.getScaledInstance(128, 128, BufferedImage.SCALE_SMOOTH)));
			if (player2name == null) {
				break;
			}
			ChessPlayer p2 = createAgent(player2name);

			gui.start(p1, p2);

		} while (JOptionPane.showConfirmDialog(null, "Start a new game?", frame.getTitle(), JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null) == JOptionPane.YES_OPTION);
		//
		System.exit(0);
	}

	public static final long PLAYER_TIMEOUT  = 30000;
	public static final long TIMEOUT_CULANCE = 500;

	private MainFrame mainframe;
	private ChessGamePanel gamepanel;
	private StatusPanel statuspanel;

	public MainFrame getMainFrame() {
		return this.mainframe;
	}

	public ChessGamePanel getGamePanel() {
		return this.gamepanel;
	}

	public StatusPanel getStatusPanel() {
		return this.statuspanel;
	}

	public ChessGUI() {
		final MainFrame frame = new MainFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainframe = frame;
		this.gamepanel = frame.getGamePanel();
		this.statuspanel = frame.getStatusPanel();
	}
	
	private PrintStream makeLogFile(String path) {
		
		Path p = Paths.get(path);
		
		try {
			return new PrintStream(new BufferedOutputStream(Files.newOutputStream(p)));
		} catch (IOException e) {
			return null;
		}
		
	}
	
	public void start(final ChessPlayer player1, final ChessPlayer player2) {
		mainframe.setVisible(true);
		gamepanel.clearListener();

		if (player1 instanceof Human) {
			gamepanel.addListener((ChessGamePanelListener) player1);
			((Human) player1).initializeGUI(gamepanel);
		}
		if (player2 instanceof Human) {
			gamepanel.addListener((ChessGamePanelListener) player2);
			((Human) player2).initializeGUI(gamepanel);
		}
		
		final PrintStream out = makeLogFile("./gamelog.txt");
		
		ChessGameControl ctrl = new ChessGameControl() {
			
			@Override
			protected void playerTurnStart(ChessGame g) {
				System.out.println(g);
				
				if (g.getCurrentPlayer() == ChessGame.PLAYER1) {
					statuspanel.status(player1.getName() + "'s turn...");
				} else {
					statuspanel.status(player2.getName() + "'s turn...");
				}
			}
			
			@Override
			protected void moveSelected(ChessGame g, ChessMove m, int player) {
				if (player == ChessGame.PLAYER1) {
					System.out.print(player1.getName());
				} else {
					System.out.print(player2.getName());
				}
				System.out.println(" performs move : " + g.encodeMove(m));
				System.out.println("");

				gamepanel.update(g.performMove(m), m);
				statuspanel.msg(g.encodeMove(m));
				
				if (out != null) {
					if (player == ChessGame.PLAYER1) {
						out.print("\n" + (g.getTurns()+1) + ": " + g.encodeMove(m));
					}
					else {
						out.print(" " + g.encodeMove(m));
					}
					out.flush();
				}
			}
			
			@Override
			protected void playerResigned(ChessGame g, int player) {
				if (player == ChessGame.PLAYER1) {
					statuspanel.msg(player1.getName() + " gives up.");
				} else {
					statuspanel.msg(player2.getName() + " gives up.");
				}
			}
			
			@Override
			protected void gameEnds(ChessGame g, int winner) {
				System.out.println();
				System.out.println(g);
				System.out.println();
				
				switch (winner) {
				case ChessGame.PLAYER1:
					statuspanel.status(player1.getName() + " wins!");
					break;
				case ChessGame.PLAYER2:
					statuspanel.status(player2.getName() + " wins!");
					break;
				case ChessGame.NONE:
					statuspanel.status("It's a draw!");
					break;
				}
				
				if (out != null) {
					switch (winner) {
					case ChessGame.PLAYER1:
						out.println(" 1-0");
						break;
					case ChessGame.PLAYER2:
						out.println(" 0-1");
						break;
					case ChessGame.NONE:
						out.println(" 1/2-1/2");
					}
					out.close();
				}
			}			
		};
		
		Timer timer = new Timer(50, (ActionEvent e) -> {
				statuspanel.updateTimeLeft(ctrl.getCurrentTurnTimeLeft() / 1000.0);
			});

		ChessGame game = new ChessGame();
		gamepanel.update(game);
		timer.start();
		ctrl.runGame(game, player1, player2, PLAYER_TIMEOUT);
		timer.stop();
	}


}