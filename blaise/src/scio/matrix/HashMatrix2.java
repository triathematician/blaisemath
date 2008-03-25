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
 * A matrix with rows and columns referenced according to objects of type W.
 * @author Elisha Peterson
 */
public class HashMatrix2<W,U,V> extends Matrix<V> {
    
    HashMap<W,Integer> rowMap;
    HashMap<U,Integer> colMap;
    
    public HashMatrix2(int nr,int nc){super(nr,nc);}
    
    public V get(W row,U col){return super.get(rowMap.get(row),colMap.get(col));}
}
