package de.cogsys.ai.chess.game;

import java.util.Objects;

/**
 * @author Sebastian Otte
 */
public class ChessMove {
    /**
     * Row of the source position.
     */
    public final int i1;
    /**
     * Column of the source position.
     */
    public final int j1;
    /**
     * Row of the target position.
     */
    public final int i2;
    /**
     * Column of the target position.
     */
    public final int j2;
    
    public ChessMove(final int i1, final int j1, final int i2, final int j2) {
    	this.i1 = i1;
    	this.j1 = j1;
    	this.i2 = i2;
    	this.j2 = j2;
    }
    
    public ChessMove(ChessMove base, final int promotion) {
    	this.i1 = base.i1;
    	this.j1 = base.j1;
    	this.i2 = base.i2;
    	this.j2 = base.j2;
    }
    
    @Override
    public boolean equals(Object o) {
    	if (o == this) {
    		return true;
    	}
    	if (!(o instanceof ChessMove)) {
    		return false;
    	}
    	ChessMove other = (ChessMove)o;
    	return this.i1 == other.i1 &&
    			this.j1 == other.j1 &&
    			this.i2 == other.i2 &&
    			this.j2 == other.j2;
    }
    
    @Override
    public int hashCode() {
    	return Objects.hash(i1, j1, i2, j2);
    }
    
}

