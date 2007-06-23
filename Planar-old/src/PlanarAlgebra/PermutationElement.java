package PlanarAlgebra;

import zzDeprecated.GroupElement;
import java.awt.Point;
import java.util.Vector;

/**
 * <b>PermutationElement.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 8, 2007, 1:13 PM</i><br><br>
 *
 * Implements an element of the permutation group, which is an array of
 *   n integers mapping {1,..,n} back to itself. Stored in a standard vector
 *   as a mapping from {0,...,n-1} to itself.
 */
public class PermutationElement extends GroupElement{
    /** Stores the permutation. */
    private int[] p;
    private int n=2;
     
    // Getters & Setters
    public int getN(){return n;}
    public void setN(int n){if(n>=0){this.n=n;}}
    public int[] getP(){return p;}
    public void setP(int[] p2){n=p2.length;p=new int[n];for(int i=0;i<n;i++){p[i]=p2[i];}}
     
    /** Default constructor gives identity. */
    public PermutationElement(){p=new int[n];for(int i=0;i<n;i++){p[i]=i;}}
    /** Constructs given an integer. */
    public PermutationElement(int n){this.n=n;p=new int[n];for(int i=0;i<n;i++){p[i]=i;}}
    /** Constructs given a permutation algebra. */
    public PermutationElement(PermutationGroup pGroup){n=pGroup.getN();p=new int[n];for(int i=0;i<n;i++){p[i]=i;}}
    /** Constructs given an integer array. */
    public PermutationElement(int[] p2){setP(p2);}
    /** Sets to values of another element. */
    public void setTo(PermutationElement p2){setP(p2.getP());}
    /** Checks for validity of the data. */
    public boolean isValid(){return PermutationGroup.validElement(p);}
    /** Determines if element is the identity. */
    public boolean isIdentity(){
        for(int i=0;i<n;i++){if(p[i]!=i){return false;}}
        return true;
    }
    /** Computes the inverse. */
    public PermutationElement getInverse(){
        int[] pInverse=new int[n];
        for(int i=0;i<n;i++){pInverse[p[i]]=i;}
        return new PermutationElement(pInverse);
    }
    /** Group left action. */
    public GroupElement actLeft(GroupElement x){return actLeft((PermutationElement)x);}
    public PermutationElement actLeft(PermutationElement x){
        int[] pNew=new int[n];
        for(int i=0;i<n;i++){pNew[i]=x.p[this.p[i]];}
        return new PermutationElement(pNew);
    }
    public void composeOnLeftWith(PermutationElement x){setTo((PermutationElement)actRight(x));}
    public void composeOnRightWith(PermutationElement x){setTo(actLeft(x));}
    
    /** Converts to a Temperley-Lieb element. */
    public TLElement toTL(){
        int[] t=new int[2*n];
        for(int i=0;i<n;i++){
            t[i]=2*n-1-p[i];
            t[2*n-1-p[i]]=i;
        }
        return new TLElement(t);
    }
    
    /** Returns an array of point pairings determined by the permutation. */
    public Point[][] getPairs(){
        Point[][] pp=new Point[n][2];
        for(int i=0;i<n;i++){pp[i][0]=new Point(i,0);pp[i][1]=new Point(p[i],1);}
        return pp;
    }
    
    /**
     * Returns a vector of point pairings sorted by cycle. Each vector element
     * is a particular cycle, with the next two indices representing the number
     * of the pairing and the point number, respectively.
     */
    public Vector<Vector<Point[]>> getCyclePairs(){
        boolean[] pUsed=new boolean[n];
        Vector<Vector<Point[]>> ppp=new Vector<Vector<Point[]>>();
        for(int i=0;i<n;i++){pUsed[i]=false;}
        for(int i=0;i<n;i++){
            if(!pUsed[i]){
                // initiates a cycle
                pUsed[i]=true;
                Vector<Point[]> pp=new Vector<Point[]>();
                // adds the first line
                Point[] point=new Point[2];
                point[0]=new Point(i,0);point[1]=new Point(p[i],1);
                pp.add(point);
                int j=i;
                while(p[j]!=i){
                    // add subsequent lines
                    j=p[j];
                    pUsed[j]=true;
                Point[] pointb=new Point[2];
                    pointb[0]=new Point(j,0);pointb[1]=new Point(p[j],1);
                    pp.add(pointb);
                }
                // completes the cycle
                ppp.add(pp);
            }
        }
        return ppp;
    }
    
    /**
     * Returns element represented by the given string, if it is valid.
     * For validity, must contain only the digits 0->9, without spaces.
     * TODO make this more robust.
     */
    static public PermutationElement valueOf(String s){
        int[] temp=new int[s.length()];
        for(int i=0;i<temp.length;i++){temp[i]=s.charAt(i)-'1';}
        return new PermutationElement(temp);
    }
        
    /** Gives the standard string representation... overrides object method. */
    public String toString(){
        String s0="";
        String s1="";
        for(int i=0;i<n;i++){
            s0+=Integer.toString(i+1);
            s1+=Integer.toString(p[i]+1);
        }
        return (s0+"->"+s1);
    }
    
    /** Returns just the elements of the array, i.e. where 0,1,2,... map. */
    public String toShortString(){
        String s="";
        for(int i=0;i<n;i++){s+=Integer.toString(p[i]+1);}
        return s;
    }
    
    /** Returns the cycle representation. */
    public String toCycleString(){
        boolean[] pUsed=new boolean[n];
        String s="";
        for(int i=0;i<n;i++){pUsed[i]=false;}
        for(int i=0;i<n;i++){
            if(!pUsed[i]){
                pUsed[i]=true;
                s+="("+Integer.toString(i+1);
                int j=i;
                while(p[j]!=i){
                    j=p[j];
                    pUsed[j]=true;
                    s+=Integer.toString(j+1);
                }
                s+=")";
            }
        }
        return s;
    }
}
