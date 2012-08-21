/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * HashMatrix.java
 * Created on Mar 25, 2008
 */
package org.bm.blaise.scio.matrix;

import java.util.Collection;
import java.util.HashMap;

/**
 * A matrix with rows and columns referenced according to objects of type W and U, respectively.
 * @author Elisha Peterson
 */
public class HashHashMatrix<W, U, V> extends Matrix<V> {

    HashMap<W, Integer> rowMap;
    HashMap<U, Integer> colMap;

    /** Initializes with a given number of rows and columns. */
    public HashHashMatrix(int nr, int nc) {
        super(nr, nc);
    }

    /** Initializes with specified array of W as the indices of the rows and columns */
    public HashHashMatrix(Object[] rowIndices, Object[] colIndices) {
        this(rowIndices.length, colIndices.length);
        init(rowIndices, colIndices);
    }

    /** Initializes with specified array of indices, but sets values to the given double array. */
    public HashHashMatrix(Object[] rowIndices, Object[] colIndices, V[][] values) {
        this(rowIndices, colIndices);
        for (int i = 0; i < getNumRows(); i++) {
            for (int j = 0; j < getNumRows(); j++) {
                put(i, j, values[i][j]);
            }
        }
    }

    public void init(Object[] rowIndices, Object[] colIndices) {
        fillMatrix(rowIndices.length, colIndices.length, null);
        rowMap = new HashMap<W, Integer>();
        for (int i = 0; i < rowIndices.length; i++) {
            rowMap.put((W) rowIndices[i], i);
        }
        colMap = new HashMap<U, Integer>();
        for (int i = 0; i < colIndices.length; i++) {
            colMap.put((U) colIndices[i], i);
        }
    }

    // GET AND PUT METHODS
    public Collection<W> getRows() {
        return rowMap.keySet();
    }

    public Collection<U> getCols() {
        return colMap.keySet();
    }

    public V get(W row, U col) {
        return super.get(rowMap.get(row), colMap.get(col));
    }

    public void put(W row, U col, V cell) {
        super.put(rowMap.get(row), colMap.get(col), cell);
    }

    // SIZE ADJUSTMENT METHODS
    public void deleteRow(W row) {
        int pos = rowMap.get(row);
        deleteRow(pos);
        rowMap.remove(row);
        for (W tRow : rowMap.keySet()) {
            if (rowMap.get(tRow) > pos) {
                rowMap.put(tRow, rowMap.get(tRow) - 1);
            }
        }
    }

    public void deleteCol(W col) {
        int pos = colMap.get(col);
        deleteCol(colMap.get(col));
        colMap.remove(col);
        for (U tCol : colMap.keySet()) {
            if (colMap.get(tCol) > pos) {
                colMap.put(tCol, colMap.get(tCol) - 1);
            }
        }
    }

    @Override public String toString() {
        String result = "HHM: " + rowMap + ", " + colMap;
        for (int i = 0; i < super.getNumRows(); i++) {
            result += "  row " + i + ": " + super.getRow(i) + "\n";
        }
        return result;
    }
}
