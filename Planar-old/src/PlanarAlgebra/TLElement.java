package PlanarAlgebra;

import zzDeprecated.GroupElement;
import java.awt.Point;
import java.util.Vector;

/**
 * <b>TLElement.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 12, 2007, 6:44 PM</i><br><br>
 *
 * Implements the Temperley-Lieb algebra. Actually, allows
 *   crossings so is a slight generalization of such. The parameter n represents
 *   the number of strands involved, or the number of dots on top/bottom.<br><br>
 *
 * The data is stored as a partition of 2n into n pairs. For simplicity,
 *   this is implemented redundantly as a vector t on 2n elements.
 *   Such an element is valid ONLY IF p[p[i]]=i for all i. The elements are
 *   considered to be ordered in a counter-clockwise fashion, starting in the
 *   bottom left. Hence, the bottom row is 0,...,n-1 and the top row is
 *   2n-1,...,n.<br><br>
 *
 * A number of loops may also be attached to the element. By default, this is
 *   zero, but composition of elements must take these into account.
 */
public class TLElement extends GroupElement{
    /** Stores the element. */
    private int[] t;
    /** The size of TL_n. Defaults to TL_2. Note that t has 2n elements
     * rather than just n. */
    private int n=2;
    /** Stores the number of loops attached to the element. */
    private int nc=0;
    
    /** Returns row of an element... 0 if the first n elements else 1. */
    public int rowOf(int k){if(k<n){return 0;}return 1;}
    /** Returns position of a given element as a point. */
    public Point pointOf(int k){if(k<n){return new Point(k,0);}return new Point(2*n-1-k,1);}
    
    // Getters & Setters
    public int getN(){return n;}
    public void setN(int n){if(n>=0){this.n=n;}}
    public int[] getT(){return t;}
    public void setT(int[] t2){int twoN=t2.length;n=twoN/2;t=new int[twoN];for(int i=0;i<twoN;i++){t[i]=t2[i];}}
    public int getNC(){return nc;}
    public void setNC(int nc){this.nc=nc;}
    public void addNC(int mc){nc+=mc;}
    
    /** Default constructor gives identity. */
    public TLElement(){setTo(new TLElement(2));}
    /** Constructs given an integer. */
    public TLElement(int n){
        this.n=n;
        t=new int[2*n];
        for(int i=0;i<n;i++){
            t[i]=2*n-1-i;t[2*n-1-i]=i;
        }
    }
    /** Constructs given a permutation algebra. */
    public TLElement(TLGroup tGroup){this(tGroup.getN());}
    /** Constructs given an integer array. */
    public TLElement(int[] t2){setT(t2);}
    /** Constructs given an array & number of circles. */
    public TLElement(int[] t2,int nc){setT(t2);setNC(nc);}
    
    /** Sets to values of another element. */
    public void setTo(TLElement t2){setT(t2.getT());setNC(t2.getNC());}
    /** Checks for validity of the data. */
    public boolean isValid(){return TLGroup.validElement(t);}
    /** Determines if element is the identity. */
    public boolean isIdentity(){
        for(int i=0;i<n;i++){if(t[i]!=2*n-1-i){return false;}}
        return (nc==0);
    }
    /** Computes the inverse. */
    public TLElement getInverse(){
        int[] tInv=new int[2*n];
        for(int i=0;i<2*n;i++){tInv[i]=2*n-1-t[i];}
        return new TLElement(tInv,-nc);
    }
    /** Group left action. This is where composition is defined! */
    public TLElement actLeft(TLElement x){
        int[] tNew=new int[2*n]; // stores the composition
        boolean[] used=new boolean[2*n]; // stores usage state of top row, second tl
        for(int i=0;i<2*n;i++){used[i]=false;}
        // go through all points on top & bottom and trace through strands
        for(int i=0;i<2*n;i++){
            int j=(i<n)?(t[i]):(x.t[i]+2*n); // initial point
            while(j>=n&&j<3*n){
                if(j<2*n){used[j]=true;j=x.t[2*n-1-j]+2*n;} // move to top element
                else{j=t[4*n-1-j];} // move to bottom element
            }
            if(j<n){tNew[i]=j;} // ends up on bottom
            else{tNew[i]=j-2*n;} // ends up on top
        }
        // find circles in the intersection
        int nc=0;
        for(int i=n;i<2*n;i++){
            if(!used[i]){
                used[i]=true;
                int j=x.t[2*n-1-i]+2*n; // initial point
                while(j!=i){
                    if(j<2*n){used[j]=true;j=x.t[2*n-1-j]+2*n;} // move to top element
                    else{used[4*n-1-j]=true;j=t[4*n-1-j];} // move to bottom element
                }
                nc++;
            }
        }        
        return new TLElement(tNew,x.nc+this.nc+nc);
    }
    public GroupElement actLeft(GroupElement x){return actLeft((TLElement)x);}
    public void composeOnLeftWith(TLElement x){setTo((TLElement)actRight(x));}
    public void composeOnRightWith(TLElement x){setTo(actLeft(x));}
    
    
    /** Returns an array of point pairings determined by the TL element. */
    public Point[][] getPairs(){
        Point[][] tt=new Point[n][2];
        int j=0;
        for(int i=0;i<2*n;i++){
            if(t[i]>i){
                tt[j][0]=pointOf(i);
                tt[j][1]=pointOf(t[i]);
                j++;
            }
        }
        return tt;
    }
    
    /**
     * Returns a vector of point pairings sorted by cycle. Assumes that the TL
     * element is connected top to bottom.
     */
    public Vector<Vector<Point[]>> getCyclePairs(){
        boolean[] tUsed=new boolean[2*n];
        Vector<Vector<Point[]>> ttt=new Vector<Vector<Point[]>>();
        for(int i=0;i<2*n;i++){tUsed[i]=false;}
        for(int i=0;i<2*n;i++){
            if(!tUsed[i]){
                // initiates a cycle
                Vector<Point[]> tt=new Vector<Point[]>();
                int j=i;
                do{
                    // add subsequent lines
                    Point[] point=new Point[2];
                    point[0]=pointOf(j);point[1]=pointOf(t[j]);
                    tUsed[j]=true;tUsed[t[j]]=true;
                    tt.add(point);
                    j=2*n-1-t[j];
                }while(j!=i);
                // completes the cycle
                ttt.add(tt);
            }
        }
        return ttt;
    }
    
    /**
     * Returns element represented by the given string, if it is valid.
     */
    static public TLElement valueOf(String s){
        // TODO Implement
        return new TLElement(Integer.valueOf(s));
    }
    
    /** Gives the standard string representation... overrides object method. */
    public String toString(){
        String s="";
        boolean[] tUsed=new boolean[2*n];
        for(int i=0;i<2*n;i++){tUsed[i]=false;}
        s+=0+"~"+t[0];tUsed[0]=true;tUsed[t[0]]=true;
        for(int i=1;i<2*n;i++){
            if(!tUsed[i]){
                s+=","+i+"~"+t[i];
                tUsed[i]=true;
                tUsed[t[i]]=true;
            }
        }
        return s;
    }
}
