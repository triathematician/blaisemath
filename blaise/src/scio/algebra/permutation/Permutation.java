package scio.algebra.permutation;

import scio.algebra.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>PermutationElement.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>May 26, 2007, 10:21 AM</i><br><br>
 *
 * Stores a permutation as an array of elements permuting 1,...,n. Always defaults
 * to the identity. The array has length n+1, and the first element is ignored.
 *
 * The default string representation is (a,b,c,d,...) where these are the elements
 * mapped from (1,2,3,..,n). For example, (1,2,3) is the identity permutation, while
 * (1,3,2) swaps the last two elements. Supports multiple other output forms.
 */
public class Permutation extends GroupElementId implements Iterator<Permutation> {
    /** Stores the permutation. */
    private int[] p;
    
    /** Constructor: creates a new instance of PermutationElement */
    public Permutation(){setToIdentity(2);}
    public Permutation(int n){setToIdentity(n);}
    public Permutation(int[] p){setP(p);}
    public Permutation(Permutation pe){setP(pe.getP());}
    public Permutation(String s){setTo(s);}
    
    
// METHODS USED TO CONSTRUCT/DEFINE THE PERMUTATION
    
    /** Sets the permutation to the identity. */
    protected void setToIdentity(int n){
        p=new int[n+1];
        for(int i=1;i<=n;i++){p[i]=i;}
    }
    
    /** Sets to a permutation specified by an integer array. */
    public boolean setP(int[] p){
        if(!isValid(p)){return false;}
        this.p=p.clone();
        return true;
    }
    
    /** Sets the permuation based on a string representation of the permutation.
     * Returns false if the string is not valid. e.g. "(1,3 ,2,5,4)"  */
    public boolean setTo(String s){
        /** Matches only spaces and digits inside parentheses */
        Matcher m=Pattern.compile("\\(([\\s\\d]*)\\)").matcher(s);
        if(m.find()){s=m.group(1);}else{return false;}
        /** Now separates groups of digits */
        String[] si=Pattern.compile("[,\\s]+").split(s);
        p=new int[si.length+1];
        p[0]=0;
        for(int i=0;i<si.length;i++){p[i+1]=Integer.valueOf(si[i]);}
        return true;
    }
    
    /** Sets the permutation based on a cycle string representation.
     * Returns false if the string is not valid.
     */
    public boolean setToCycle(String s){
        ArrayList<Integer[]> cycles=new ArrayList<Integer[]>();
        /** Matches groups of numbers inside parentheses */
        Matcher m=Pattern.compile("\\(([\\s\\d]*)\\)").matcher(s);
        while(m.find()){
            // Copy a string of digits into an integer list
            String[] s2i=Pattern.compile("[,\\s]+").split(m.group(1));
            Integer[] s2il=new Integer[s2i.length];
            for(int i=0;i<s2i.length;i++){s2il[i]=Integer.valueOf(s2i[i]);}
            if(s2il.length!=0){cycles.add(s2il);}
        }
        if(cycles.size()==0){return false;}
        // Now copy cycle information into a hash map, keep max value at same time
        int max=0;
        HashMap<Integer,Integer> tPerm=new HashMap<Integer,Integer>();
        for(Integer[] c:cycles){
            for(int i=0;i<c.length;i++){
                if(c[i]>max){max=c[i];}
                tPerm.put(c[i],c[(i+1)%c.length]);
            }
        }
        // Now copy all this into the permutation... but first copy in default values
        p=new int[max+1];
        for(int i=0;i<p.length;i++){p[i]=i;}
        for(Integer i:tPerm.keySet()){p[i]=tPerm.get(i);}
        return true;
    }
    
    
// BASIC VIEWS OF THE ELEMENT
    
    // Getters & Setters
    public int[] getP(){return p;}
    public int getN(){return p.length-1;}
    
    /** Returns a permutation value. */
    public int get(int i){if(i<p.length){return p[i];}return -1;}
    
    /** Returns the cycle starting at the given point as an ArrayList. */
    public ArrayList<Integer> getCycle(int i){
        if(i<=0||i>p.length){return null;}
        ArrayList<Integer> result=new ArrayList<Integer>();
        result.add(i);
        int j=i;
        while(p[j]!=i){j=p[j];result.add(j);}
        return result;
    }
    
    /** Tests to see if this is the identity. */
    public boolean isIdentity(){return isIdentity(p);}
    
    /** Returns the sign of the permutation, as +1 or -1. */
    public int sign(){
        int num=0;
        for(int i=1;i<p.length;i++){num+=Math.abs(p[i]-i);}
        return (num%4==0)?+1:-1;
    }
    
// VARIOUS FORMATTING/CONVERSION OUTPUT METHODS == GLOBAL VIEWS
    
    /** Outputs string representation. */
    public String toString(){
        if(p==null||p.length<2){return "()";}
        String s="("+p[1];
        for(int i=2;i<p.length;i++){s+=" "+p[i];}
        return s+")";
    }
    
    /** Outputs a LONG string representation (complete with arrows a->b) */
    public String toLongString(){
        String s="(1->"+p[1];
        for(int i=2;i<p.length;i++){s+=","+i+"->"+p[i];}
        return s+")";
    }
    
    /** Outputs string cycle representation of the permutation. */
    public String toCycleString(){
        ArrayList<ArrayList<Integer>> cycles=getCycles();
        String s="";
        for(ArrayList<Integer> cycle:cycles){
            s+="("+cycle.get(0);
            for(int i=1;i<cycle.size();i++){s+=" "+cycle.get(i);}
            s+=")";
        }
        return s;
    }
    
    /** Outputs a list of the cycles of the permutation. */
    public ArrayList<ArrayList<Integer>> getCycles(){
        HashSet<Integer> iUsed=new HashSet<Integer>();
        ArrayList<ArrayList<Integer>> result=new ArrayList<ArrayList<Integer>>();
        for(int i=1;i<p.length;i++){
            if(!iUsed.contains(i)){
                ArrayList<Integer> cycle=getCycle(i);
                result.add(cycle);
                iUsed.addAll(cycle);
            }
        }
        return result;
    }
    
    // TODO Routine outputting array representation
    // TODO Routine outputting matrix representation
    
    
// BASIC OPERATIONS
    
    /** Gets the inverse permutation. */
    public GroupElement getInverse() {
        int[] pResult=new int[p.length];
        for(int i=0;i<p.length;i++){pResult[p[i]]=i;}
        return new Permutation(pResult);
    }
    
    /** Permutation composition action. */
    public GroupElement actLeft(GroupElement x) {
        int[] p2=((Permutation)x).getP();
        if(p.length!=p2.length){return null;}
        int[] pResult=new int[p.length];
        for(int i=0;i<p.length;i++){pResult[i]=p[p2[i]];}
        return new Permutation(pResult);
    }
    
    
// STATIC ROUTINES: VALIDITY CHECKS AND STANDARD ELEMENTS
    
    /** Checks if an integer array is a valid permutation. */
    public static boolean isValid(int[] pCheck){
        if(pCheck[0]!=0){return false;}
        boolean[] pUsed=new boolean[pCheck.length];
        for(int i=0;i<pUsed.length;i++){pUsed[i]=false;}
        for(int i=0;i<pCheck.length;i++){pUsed[pCheck[i]]=true;}
        for(int i=0;i<pCheck.length;i++){if(!pUsed[i]){return false;}}
        return true;
    }
    
    /** Checks to see if permutation is the identity. */
    public static boolean isIdentity(int[] pCheck){
        for(int i=0;i<pCheck.length;i++){if(pCheck[i]!=i){return false;}}
        return true;
    }
    
    /** Returns the identity element. Defaults to identity on two elements. */
    public static GroupElementId getIdentity(){return getIdentity(2);}
    public static GroupElementId getIdentity(int n){return new Permutation(n);}
    
// ITERATOR METHODS
    
    /** To *not* have a next value, the permutation must be completely decreasing. */
    public boolean hasNext() {
        for(int i=2;i<=getN();i++){
            if(get(i)>get(i-1)){return true;}}
        return false;
    }
    
    /** Get the next permutation in lexicographic order. */
    public Permutation next(){
        if(!hasNext()){return null;}
        int n=getN();
        int[] pResult=new int[n+1];
        int[] pCurrent=getP();
        /** Find first point at which permutation is not increasing at its end.
         * Example: for [4 5 1 2 3 9 8 7 6], 3 is selected. */
        int iSwap=n;do{iSwap--;}while(pCurrent[iSwap]>pCurrent[iSwap+1]);
        /** Copy elements before iSwap into pResult (above, 4 5 1 2) */
        for(int i=0;i<iSwap;i++){pResult[i]=pCurrent[i];}
        /** Find element to place in iSwap's position...the next largest! (above, 6) */
        int iNew=n;while(pCurrent[iNew]<pCurrent[iSwap]){iNew--;};
        /** Copy iNew into iSwap's position (above, 6) */
        pResult[iSwap]=pCurrent[iNew];
        /** Next, copy elements after iNew in reverse order. (above, ()) */
        for(int i=n;i>iNew;i--){pResult[iSwap+n-i+1]=pCurrent[i];}
        /** Copy iSwap into the next position (above, 3) */
        pResult[iSwap+n-iNew+1]=pCurrent[iSwap];
        /** Copy elements between iSwap and iNew into the final positions. (above, 7 8 9) */
        for(int i=iNew-1;i>iSwap;i--){pResult[iSwap+n-i+1]=pCurrent[i];}
        /** Return the result! */
        return new Permutation(pResult);
    }
    
    /** Not possible to remove elements */
    public void remove(){}
    
    /** Comparison */
    // TODO: Fix this!
    public int compareTo(Object o){return 0;}
}
