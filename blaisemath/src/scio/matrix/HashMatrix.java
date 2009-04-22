/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * HashMatrix.java
 * Created on Mar 25, 2008
 */

package scio.matrix;

import java.util.HashMap;

/**
 * A matrix with rows and columns referenced according to objects of type W. Uses a hashmap to assign values
 * of the rows and columns to integers.
 * 
 * @author Elisha Peterson
 */
public class HashMatrix<W,V> extends Matrix<V> {
    
    HashMap<W,Integer> map;
    
    
    // CONSTRUCTORS
    
    /** Initializes with a given number of rows and columns. */
    public HashMatrix(int nr,int nc){super(nr,nc);}
    
    /** Initializes with specified array of W as the indices of the rows and columns */
    public HashMatrix(W[] indices) {
        this(indices.length,indices.length);
        map=new HashMap<W,Integer>();
        for(int i=0;i<indices.length;i++){
            map.put(indices[i], i);
        }
    }
    
    /** Initializes with specified array of indices, but sets values to the given double array. */
    public HashMatrix(W[] indices, V[][] values) {
        this(indices);
        for(int i=0;i<getNumRows();i++){
            for(int j=0;j<getNumRows();j++){
                put(i,j,values[i][j]);
            }
        }
    }
    
    
    // GET AND PUT METHODS
    
    public HashMap<W,Integer> getMap(){return map;}
    public V get(W row,W col){return super.get(map.get(row),map.get(col));}
    public void put(W row,W col,V cell){super.put(map.get(row),map.get(col),cell);}
    
    
    // PRINTING
    
    @Override
    public String toString(){
        String result="[ ";
        for(int i=0;i<map.size();i++){
            result += "[ ";
            for(int j=0;j<map.size();j++){
                result+=get(i,j)+ " ";
            }
            result +="] ";
        }
        result+="]";
        return result;
    }
}
