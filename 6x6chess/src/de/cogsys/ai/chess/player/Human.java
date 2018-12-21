package de.cogsys.ai.chess.player;

import java.util.List;

import javax.swing.JOptionPane;

import de.cogsys.ai.chess.control.ChessGameConsole;
import de.cogsys.ai.chess.game.ChessGame;
import de.cogsys.ai.chess.game.ChessMove;
import de.cogsys.ai.chess.game.Figure;
import de.cogsys.ai.chess.gui.ChessGamePanel;
import de.cogsys.ai.chess.gui.ChessGamePanelListener;

/**
 * @author Sebastian Otte
 */
public final class Human extends ChessPlayer implements ChessGamePanelListener {

    private ChessGamePanel panel;
    
    private int i;
    private int j;
    
    private boolean         state_active;
    private int             state_player;
    private List<ChessMove> state_moves;
    private int             state_i1;
    private int             state_j1;
    private boolean         state_selected;
    private ChessGame       state_game;
    
    private String playerName;
    
    
    public void initializeGUI(final ChessGamePanel panel) {
        this.panel = panel;
        this.panel.addListener(this);
    }
    
    public Human() {
    	do {
    		playerName = JOptionPane.showInputDialog("Please Enter a name for the Human player:");
    	} while (playerName == null || playerName.length() == 0);
    }
    
    @Override
    public String getName() {
    	return playerName;
    }
    
    @Override
    public void initialize(final int color) {
        //
        this.state_active = false;
    }

    
    private void initState(final ChessGame game) {
        this.state_active   = true;
        this.state_selected = false;
        this.state_i1       = -1;
        this.state_j1       = -1;
        this.state_game     = game;
        this.state_player   = game.getCurrentPlayer();
        this.state_moves    = game.generateValidMoves();
    }
    
    private boolean hasMoves(final int i, final int j) {
        for (ChessMove  move : this.state_moves) {
            if ((move.i1 == i) && (move.j1 == j)) {
                return true;
            }
        }
        return false;
    }
    
    private ChessMove processState() {
        //
        if (!this.state_game.isInGameArea(this.i, this.j)) return null;
        //
        final int value = this.state_game.get(this.i, this.j);
        final int color = Figure.color(value);
        //
        this.panel.clearMarked();
        //
        ChessMove move   = null;
        boolean reselect = false;
        //
        if (this.state_selected) {
            if (color == this.state_player) {
                reselect = true;
            } else {
                //
                // check move.
                //
                boolean valid = false;
                
                ChessMove thisMove = new ChessMove(state_i1, state_j1, this.i, this.j);

                for (ChessMove m : this.state_moves) {
                    if (m.equals(thisMove)) {
                    	move = m;
                    	valid = true;
                        break;
                    }
                }                
                //
                // abort?
                //
                if (!valid) {
                    this.state_selected = false;
                }
            }
        } else {
            reselect = true;
        }
        //
        if (reselect) {
            //
            if (color == this.state_player) {
                //
                this.state_i1 = this.i;
                this.state_j1 = this.j;
                //
                if (this.hasMoves(this.state_i1, this.state_j1)) {
                    this.panel.addMarked(this.state_i1, this.state_j1, ChessGamePanel.MARK_GREEN);
                    //
                    for (ChessMove m : this.state_moves) {
                        if ((m.i1 == state_i1) && (m.j1 == state_j1)) {
                            this.panel.addMarked(m.i2, m.j2, ChessGamePanel.MARK_BLUE);
                        }
                    }
                    //
                    this.state_selected = true;
                } else {
                    this.panel.addMarked(this.state_i1, this.state_j1, ChessGamePanel.MARK_RED);
                }
                
            }
        }
        //
        this.panel.repaint();
        //
        return move;
    }
    
    private void cleanUpState() {
        this.state_selected = false;
        this.state_i1       = -1;
        this.state_j1       = -1;
        this.state_game     = null;
        this.state_player   = -1;
        this.state_moves    = null;
        this.state_active   = false;
    }
    
    @Override
    public void generateNextMove(final ChessGameConsole c) {
        //
        final ChessGame game = c.getGame();
        ChessMove move = null;
        //
        synchronized (this) {
            //
            this.initState(game);
            //
            while ((move == null)) {
                try {
                    synchronized (this) {
                        wait();
                    }
                    //
                    // waiter was notified here, process current event and change state.
                    //
                    move = this.processState();
                } catch (InterruptedException e) {
                    //
                }
            }
            this.cleanUpState();
        }
        //
        c.updateMove(move);
    }

    @Override
    public void clickedCell(final int i, final int j) {
        if (!this.state_active) return;
        //
        synchronized (this) {
            //
            this.i = i;
            this.j = j;
            //
            this.notify();
        };
    }
}
