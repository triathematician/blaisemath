package Matrix;

import java.util.ArrayList;

/**
 * <b>Matrix.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>June 15, 2007, 4:09 PM</i><br><br>
 *
 * Represents a growable matrix with entries corresponding to some object.
 */
public class Matrix<V> extends ArrayList<ArrayList<V>>{
    
    /** Constructor: creates a new instance of Matrix */
    public Matrix(){super();this.add(new ArrayList<V>());}
    
    /** Resets to a 0x0 matrix. */
    public void clear(){super.clear();}
    
    /** Fills the matrix with a given element */
    public void fillMatrix(int nr,int nc,V v){
        clear();
//        System.out.println("fill "+nr+"x"+nc+" with "+v.toString());
        for(int i=0;i<nr;i++){
            ArrayList<V> col=new ArrayList<V>();
            for(int j=0;j<nc;j++){col.add(v);}
            this.add(col);
        }
    }
    
    /** Puts specified element in the specified location. */
    public boolean put(int i,int j,V v){
//        System.out.println(this.toString());
//        System.out.println("put "+v.toString()+" at ("+i+","+j+")");
        if(i>=getNRows()||j>=getNCols()){return false;}
        this.get(i).set(j,v);
        return true;
    }
    public boolean set(int i,int j,V v){return put(i,j,v);}
    /** Returns element at given location. */
    public V get(int i,int j){return this.get(i).get(j);}
    
    /** Returns number of rows. */
    public int getNRows(){return this.size();}
    /** Returns number of columns. */
    public int getNCols(){return this.get(0).size();}
    
    /** Adds a row to the matrix. */
    public boolean addRow(ArrayList<V> row){
        if(row.size()!=getNCols()){return false;}
        this.add(row);
        return true;
    }
    /** Adds a column to the matrix. */
    public boolean addCol(ArrayList<V> col){
        if(col.size()!=getNRows()){return false;}
        for(int i=0;i<getNRows();i++){this.get(i).add(col.get(i));}
        return true;
    }
    
    /** Returns a row of the matrix. */
    public ArrayList<V> getRow(int i){return this.get(i);}
    /** Returns a column of the matrix. */
    public ArrayList<V> getColumn(int j){
        ArrayList<V> result=new ArrayList<V>();
        for(int i=0;i<getNRows();i++){result.add(this.get(i).get(j));}
        return result;
    }
    
    /** Deletes a row of the matrix. */
    public void deleteRow(int i){this.remove(i);}
    /** Deletes a column of the matrix. */
    public void deleteCol(int j){for(int i=0;i<getNRows();i++){this.get(i).remove(j);}}
    
    /** Returns the transpose of the matrix. */
    public Matrix<V> getTranspose(){
        return null;
    }
}
