/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Matrix.java
 * Created on Mar 24, 2008
 */

package org.bm.blaise.scio.matrix;

import java.util.Vector;

/**
 *
 * @author Elisha Peterson
 */
public class Matrix<V> {
    
    /** Stores the underlying data. */
    Vector<Vector<V>> data;
    
    
    // CONSTRUCTOR
    
    public Matrix(int nr,int nc){this(nr,nc,null);}
    /** Initializes to given number of rows and columns. This method merely allots space for the matrix.
     * It does not actually set any values.
     * @param nr the number of rows
     * @param nc the number of columns
     * @param v the object to be placed in every position
     */
    public Matrix(int nr,int nc,V v){fillMatrix(nr,nc,v);}
    /** Initializes to specified matrix. */
    public Matrix(Matrix <V> matrix){
        data = new Vector<Vector<V>> ();
        for(int i=0; i<matrix.getNumRows(); i++) {
            data.add(matrix.getRow(i));
        }
    }
    
    
    // INITIALIZERS       
    
    /** Fills the matrix with a given element */
    public void fillMatrix(int nr,int nc,V v){
        if(data==null){
            data=new Vector<Vector<V>>(nr);
        }else{
            data.clear();
        }
        for(int i=0;i<nr;i++){
            Vector<V> row=new Vector<V>();
            for(int j=0;j<nc;j++){row.add(v);}
            data.add(row);
        }
    }

    /** Fills the matrix with a given element */
    public void fillMatrix(V v){
        if(data==null){
            return;
        }
        for(int i=0;i<data.size();i++){
            for(int j=0;j<data.get(i).size();j++) {
                data.get(i).set(j, v);
            }
        }
    }
    
    
    // BEAN PATTERNS
    
    public V get(int row,int col) throws ArrayIndexOutOfBoundsException {return data.get(row).get(col);}
    public void set(int row,int col,V value) throws ArrayIndexOutOfBoundsException {data.get(row).set(col,value);}
    public void put(int row,int col,V value) throws ArrayIndexOutOfBoundsException {set(row,col,value);}
    public int getNumRows(){return data.size();}
    public int getNumCols(){return data.firstElement().size();}
    
    /** Returns a row of the matrix. */
    public Vector<V> getRow(int i) throws ArrayIndexOutOfBoundsException {return data.get(i);}
    /** Returns a column of the matrix. */
    public Vector<V> getColumn(int j) throws ArrayIndexOutOfBoundsException {
        Vector<V> result=new Vector<V>();
        for(int i=0;i<getNumRows();i++){result.add(get(i,j));}
        return result;
    }
    
    
    // MATRIX OPERATIONS
    
    /** Adds a row to the matrix. */
    public void addRow(Vector<V> row) throws ArrayIndexOutOfBoundsException {
        if(row.size()!=getNumCols()){throw new ArrayIndexOutOfBoundsException();}
        data.add(row);
    }
    /** Adds a column to the matrix. */
    public void addColumn(Vector<V> col) throws ArrayIndexOutOfBoundsException {
        if(col.size()!=getNumRows()){throw new ArrayIndexOutOfBoundsException();}
        for(int i=0;i<getNumRows();i++){data.get(i).add(col.get(i));}
    }    
    /** Deletes a row of the matrix. */
    public void deleteRow(int i) throws ArrayIndexOutOfBoundsException {data.remove(i);}
    /** Deletes a column of the matrix. */
    public void deleteCol(int j) throws ArrayIndexOutOfBoundsException {for(int i=0;i<getNumRows();i++){getRow(i).remove(j);}}
        
    /** Returns the transpose of the matrix. */
    public Matrix<V> getTranspose(){
        Matrix<V> result=new Matrix<V>(getNumCols(),getNumRows());
        for(int i=0;i<getNumRows();i++){
            for(int j=0;j<getNumCols();j++){
                result.set(j,i,get(i,j));
            }
        }
        return result;
    }
    
    
    // COORDINATE METHODS

    @Override
    public boolean equals(Object c2) {
        if(!(c2 instanceof Matrix))
            return false;
        Matrix compareTo = (Matrix) c2;
        if(!(compareTo.getNumCols()==getNumCols() && compareTo.getNumRows()==getNumRows()))
            return false;
        for(int i=0; i<getNumRows(); i++)
            for(int j=0; j<getNumCols(); j++)
                if(get(i,j) != compareTo.get(i,j))
                    return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }

    @Override
    public Object clone() {
        return new Matrix<V>(this);
    }
}
