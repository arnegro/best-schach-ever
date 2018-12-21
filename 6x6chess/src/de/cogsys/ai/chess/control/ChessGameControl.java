package de.cogsys.ai.chess.control;

import de.cogsys.ai.chess.game.ChessGame;
import de.cogsys.ai.chess.game.ChessMove;
import de.cogsys.ai.chess.player.ChessPlayer;

/**
 * @author Sebastian Otte
 */
public class ChessGameControl {

/*    
    private MainFrame      mainframe;
    private ChessGamePanel gamepanel;
    private StatusPanel    statuspanel;
    private ChessPlayer    player1;
    private ChessPlayer    player2;
    private boolean        showgui;
    private ChessGame      game;
    private ChessMove      lastmove;
    private boolean        running;
    
    public MainFrame getMainFrame() 	 { return this.mainframe; }
    public ChessGamePanel getGamePanel() { return this.gamepanel; }
    public StatusPanel getStatusPanel()  { return this.statuspanel; }
    public ChessPlayer getPlayer1() 	 { return this.player1; }
    public ChessPlayer getPlayer2() 	 { return this.player2; }
    public ChessGame getGame() 			 { return this.game; }
    
    public boolean isRunning() { 
        synchronized (this) {
            return this.running;
        }
    }
    
    public ChessGameControl(
        final MainFrame      mainframe,
        final ChessGamePanel gamepanel,
        final StatusPanel    statuspanel,
        final boolean        showgui
    ) {
        this.mainframe   = mainframe;
        this.gamepanel   = gamepanel;
        this.statuspanel = statuspanel;
        this.showgui     = showgui;
        //
    }
    
    public void update() {
        this.gamepanel.update(this.game, this.lastmove);
        if (this.game.ends()) {
            if (this.game.wins(ChessGame.PLAYER1)) {
                this.statuspanel.status("Player 1 wins!");
            } else if (this.game.wins(ChessGame.PLAYER2)) {
                this.statuspanel.status("Player 2 wins!");
            } else if (this.game.isDraw()) {
                this.statuspanel.status("It's a draw!");
            } else {
                this.statuspanel.status("...");
            }
        } else {
            //
            if (this.game.isCheck(ChessGame.PLAYER1)) {
                this.statuspanel.msg("Player 1's king is in check.");
            }
            if (this.game.isCheck(ChessGame.PLAYER2)) {
                this.statuspanel.msg("Player 2's king is in check.");
            }
            //
            if (this.game.getCurrentPlayer() == ChessGame.PLAYER1) {
                this.statuspanel.status("Player 1's turn...");
            } else {
                this.statuspanel.status("Player 2's turn...");
            }
        }
    }
    
    private static String encodePosition(final int i, final int j) {
        return "" + (char)('A' + j) + (i + 1);
    }

    public int start(final ChessGame game, final ChessPlayer player1, final ChessPlayer player2) {
        synchronized (this) {
            this.running = true;
        }
        this.game     = game;
        this.lastmove = null;
        this.player1  = player1;
        this.player2  = player2;
        //
        if (this.showgui) {
        	this.mainframe.setVisible(true);
        }
        //
        this.gamepanel.clearListener();
        //
        this.player1.initialize(ChessGame.PLAYER1);
        if (this.player1 instanceof Human) {
            ((Human)(this.player1)).initializeGUI(this.gamepanel);
        }
        this.player2.initialize(ChessGame.PLAYER2);
        if (this.player2 instanceof Human) {
            ((Human)(this.player2)).initializeGUI(this.gamepanel);
        }
        //
    	final TimeCounter tc = new TimeCounter();
    	tc.reset();
        Timer timer = null;
        //
    	if (this.showgui) {
    		 timer = new Timer(50, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ChessGameControl.this.statuspanel.updateTimeLeft(
                        Math.max(0, 
                            PLAYER_TIMEOUT - tc.valueMilli()
                        ) / 1000.0
					);
				}
			});
    	}
        //
    	while (!this.game.ends()) {
        	this.update();
            System.out.println();
            System.out.println(this.game);
            System.out.println();
        	//
        	ChessPlayer player;
        	//
        	if (this.game.getCurrentPlayer() == ChessGame.PLAYER1) {
        		player = this.player1;
        	} else {
        		player = this.player2;
        	}
        	//
        	final ChessPlayer currentplayer = player;
        	final ChessMove[] playermove = new ChessMove[1];
        	ChessMove selectedMove = null;
        	//
        	tc.reset();
        	if (this.showgui) {
        		timer.start();
        	}
        	//
        	final Thread thread = new Thread(new Runnable(){
        		@Override
        		public void run() {
        			currentplayer.generateNextMove(
        			    new ChessGameConsole() {
                            @Override
                            public ChessGame getGame() {
                                return new ChessGame(ChessGameControl.this.game);
                            }
                            @Override
                            public long getTimeLeft() {
                                return Math.max(0, 
                                    PLAYER_TIMEOUT - tc.valueMilli()
                                );
                            }
                            @Override
                            public void updateMove(final ChessMove move) {
                                //
                                playermove[0] = move;
                            }
        			        
        			    }
        			);
        		}
        	});
        	//
        	thread.start();
        	//
        	try {
				thread.join(PLAYER_TIMEOUT + TIMEOUT_CULANCE);
				selectedMove = playermove[0];
				
				// interrupt the thread to trigger resource cleanup
				thread.interrupt();
				// wait for it to terminate gracefully
				thread.join(1000);
				// if it hasn't died yet, just kill it
				thread.stop();
			} catch (InterruptedException e) {}
        	//
        	if (this.showgui) {
        		timer.stop();
        	}
        	//
        	if (playermove[0] == null) {
        		if (this.game.getCurrentPlayer() == ChessGame.PLAYER1) {
                    this.statuspanel.msg("Player 1 gives up.");
                    this.statuspanel.status("Player 2 wins!");
                    synchronized (this) {
                        this.running = false;
                    }
                    return ChessGame.PLAYER2;
                } else {
                    this.statuspanel.msg("Player 2 gives up.");
                    this.statuspanel.status("Player 1 wins!");
                    synchronized (this) {
                        this.running = false;
                    }
                    return ChessGame.PLAYER1;
                }
        	} else {
        		//
        		if (this.game.getCurrentPlayer() == ChessGame.PLAYER1) {
        			System.out.print("Player 1");
        		} else {
        			System.out.print("Player 2");
        		}
        		//
        		System.out.println(
        			" performs move : " + encodePosition(playermove[0].i1, playermove[0].j1) + " to " + encodePosition(playermove[0].i2, playermove[0].j2)
        		);
        		System.out.println("");
                //
        		this.game     = this.game.performMove(playermove[0]);
        		this.lastmove = playermove[0];
        	}
        }
        //
        this.update();
        System.out.println();
        System.out.println(this.game);
        System.out.println();
        //
        if (this.game.wins(ChessGame.PLAYER1)) {
            synchronized (this) {
                this.running = false;
            }
        	return ChessGame.PLAYER1;
        }
        if (this.game.wins(ChessGame.PLAYER2)) {
            synchronized (this) {
                this.running = false;
            }
        	return ChessGame.PLAYER2;
        }
        synchronized (this) {
            this.running = false;
        }
        return 0;

    }
    
 */
	public static final int TIMEOUT_CULANCE = 1000;
	private TimeCounter tc;
	private long turntime;
	
	public ChessGameControl() {
		tc = new TimeCounter();
	}
	
	public ChessGameControl(TimeCounter tc) {
		this.tc = tc;
	}
	
	/**
	 * @param g Game state before executing the move
	 * @param m Move that was selected by current player
	 * @param p Player who selected the move 
	 */
	protected void moveSelected(ChessGame g, ChessMove m, int player) {}
	
	/**
	 * @param g Game state when player resigned
	 * @param p the player who resigned (currentPlayer in game state)
	 */
	protected void playerResigned(ChessGame g, int player) {}
	
	/**
	 * @param g Final game state
	 * @param winner the player who won or NONE if it as a draw
	 */
	protected void gameEnds(ChessGame g, int player) {}
	
	/**
	 * called at the start of each player's turn
	 * @param g
	 */
	protected void playerTurnStart(ChessGame g) {}
	
	/**
	 * called at the end of each player's turn
	 * @param g
	 */
	protected void playerTurnEnd(ChessGame g) {}
	
	public TimeCounter getTimer() {
		return tc;
	}
	
	public long getCurrentTurnTimeLeft() {
		return Math.max(0, turntime - tc.valueMilli());
	}

	@SuppressWarnings("deprecation")
	public int runGame(ChessGame game, final ChessPlayer p1, final ChessPlayer p2, final long timeout) {
		p1.initialize(ChessGame.PLAYER1);
		p2.initialize(ChessGame.PLAYER2);
		
		turntime = timeout;

		tc.reset();

		boolean running = true;
		int winner = ChessGame.NONE;

		while (running) {
			final ChessPlayer currentPlayer = game.getCurrentPlayer() == ChessGame.PLAYER1 ? p1 : p2;
			

			final ChessMove[] playermove = new ChessMove[1];
			final ChessGame currentState = game;

			final Thread thread = new Thread(() -> {
				currentPlayer.generateNextMove(new ChessGameConsole() {
					@Override
					public ChessGame getGame() {
						return new ChessGame(currentState);
					}

					@Override
					public long getTimeLeft() {
						return Math.max(0, turntime - tc.valueMilli());
					}

					@Override
					public void updateMove(final ChessMove move) {
						playermove[0] = move;
					}
				});
			});

			playerTurnStart(game);
			tc.reset();

			thread.start();

			ChessMove selectedMove = null;
			try {
				thread.join(turntime + TIMEOUT_CULANCE);
				selectedMove = playermove[0];
				playerTurnEnd(game);

				// wait on player thread to prevent resource leaks
				thread.interrupt();
				// after waiting 1 sec., just kill it
				thread.join(1000);
				thread.stop();
			} catch (InterruptedException e) {}

			if (selectedMove == null || !game.isValidMove(selectedMove)) {
				playerResigned(game, game.getCurrentPlayer());
				winner = game.getOtherPlayer();
				running = false;
			} else {
				moveSelected(game, selectedMove, game.getCurrentPlayer());
				game = game.performMove(selectedMove);
				if (game.ends()) {
					winner = game.result();
					running = false;
				}
			}
		}
		gameEnds(game, winner);
		return winner;
	}
}