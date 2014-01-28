/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.instrumentation;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class LocationImpl implements Location {
    private final int firstLine;
    private final int lastLine;
    private final int firstColumn;
    private final int lastColumn;

    public LocationImpl(int firstLine, int firstColumn, int lastLine, int lastColumn) {
        this.firstLine = firstLine;
        this.lastLine = lastLine;
        this.firstColumn = firstColumn;
        this.lastColumn = lastColumn;
    }
    
    public LocationImpl(Location l) {
        this.firstLine = l.getFirstLine();
        this.lastLine = l.getLastLine();
        this.firstColumn = l.getFirstColumn();
        this.lastColumn = l.getLastColumn();
    }

    /**
     * @return the firstLine
     */
    @Override
    public int getFirstLine() {
        return firstLine;
    }

    /**
     * @return the lastLine
     */
    @Override
    public int getLastLine() {
        return lastLine;
    }

    /**
     * @return the firstColumn
     */
    @Override
    public int getFirstColumn() {
        return firstColumn;
    }

    /**
     * @return the lastColumn
     */
    @Override
    public int getLastColumn() {
        return lastColumn;
    }
}
