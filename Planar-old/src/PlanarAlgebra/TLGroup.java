package PlanarAlgebra;

import zzDeprecated.Category.CObject;
import zzDeprecated.GroupElement;

/**
 * <b>TLGroup.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 7, 2007, 10:23 AM</i><br><br>
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
public class TLGroup {
    /** The number of objects permuting. */
    int n=2;    
    
    /** Default constructor. */
    public TLGroup(){}
    /** Strands constructor. */
    public TLGroup(int n){this.n=n;}
    
    // Getters & Setters
    public int getN(){return n;}
    public void setN(int n){if(n>=0){this.n=n;}}
    
    /**
     * Returns element representing a basic swap of two positions... really
     *   it's just a permutation.
     */
    public static TLElement getSwap(int j,int k,int n){return PermutationGroup.getSwap(j,k,n).toTL();}
    
    /**
     * Returns a basis element in the group TL_n, represented
     * by a cup/cap pair in position i->i+1.
     */   
    public static TLElement getBasic(int i,int n){
        if(i<0||i>=n-1){return null;}
        int[] t=new TLElement(n).getT();
        t[i]=i+1;t[i+1]=i;
        t[2*n-1-i]=2*n-1-(i+1);t[2*n-1-(i+1)]=2*n-1-i;
        return new TLElement(t);
    }
    
    // Abstract Group Methods
    
    /** Gets the identity element. */
    public GroupElement getIdentity(){return (GroupElement)(new TLElement());}
    
    /** Checks for validity of data... no length prescribed. */
    public static boolean validElement(int[] t){
        for(int i=0;i<t.length;i++){if(t[t[i]]!=i){return false;}}
        return true;        
    }
    /** Tests to see if it contains an object. */
    public boolean contains(CObject o){TLElement e=(TLElement)o;return ((e.getN()==n)&&e.isValid());}
    /** Tests to see if the group is valid. */
    public boolean wellDefined(){return true;}
    
    public static void main(String[] args){
    }
    
    private static void out(String string) {
    }
}
