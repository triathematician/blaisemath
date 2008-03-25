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
public class HashMatrix<W,V> extends Matrix<V> {
    
    HashMap<W,Integer> map;

    public HashMatrix(W[] hiddenStates) {
        this(hiddenStates.length,hiddenStates.length);
        map=new HashMap<W,Integer>();
        for(int i=0;i<hiddenStates.length;i++){
            map.put(hiddenStates[i], i);
        }
    }
    public HashMatrix(W[] hiddenStates, V[][] tMx) {
        this(hiddenStates);
        for(int i=0;i<getNumRows();i++){
            for(int j=0;j<getNumRows();j++){
                put(i,j,tMx[i][j]);
            }
        }
    }
    
    public HashMatrix(int nr,int nc){super(nr,nc);}
    
    public V get(W row,W col){return super.get(map.get(row),map.get(col));}
    public void put(W row,W col,V cell){super.put(map.get(row),map.get(col),cell);}
    
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
