package PlanarAlgebra;

import zzDeprecated.Category.CObject;
import zzDeprecated.Group;
import zzDeprecated.GroupElement;


/**
 * <b>PermutationGroup.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 7, 2007, 10:22 AM</i><br><br>
 * 
 * Defines a permutation group on n elements, where n>=0 is an integer stored
 *   within the class. Implemented as both a group and a category.<br><br>
 * 
 * As a category, there is a single object, the collection of n elements, and the
 *   elements of the permutation group are the morphisms.<br><br>
 * 
 * As a group, the operation is composition of permutations.
 */
public class PermutationGroup extends Group {
    /** The number of objects permuting. */
    int n=2;    
    
    /** Default constructor. */
    public PermutationGroup(){commutative=false;}
    /** Strands constructor. */
    public PermutationGroup(int n){this.n=n;commutative=false;}
    
    // Getters & Setters
    public int getN(){return n;}
    public void setN(int n){if(n>=0){this.n=n;}}
    
    // Group element generators
    public static PermutationElement getSwap(int j,int k,int n){
        int[] p=new int[n];
        for(int i=0;i<n;i++){p[i]=i;}
        p[j]=k;p[k]=j;
        return new PermutationElement(p);
    }
    
    // Abstract Group Methods
    
    /** Gets the identity permutation. */
    public GroupElement getIdentity(){return (GroupElement)(new PermutationElement());}
    
    /** Tests for validity of an array as a permutation. */
    public static boolean validElement(int[] p){
        int n=p.length;
        boolean[] pHit=new boolean[n];
        for(int i=0;i<n;i++){pHit[i]=false;}
        for(int i=0;i<n;i++){
            int j=p[i];
            if(j<0||j>=n||pHit[j]){return false;}
            pHit[j]=true;
        }
        return true;
    }
    /** Tests to see if it contains an object. */
    public boolean contains(CObject o){return ((PermutationElement)o).getN()==n;}
    /** Tests to see if the group is valid. */
    public boolean wellDefined(){return true;}
    
    public static void main(String[] args){
        int[] q1={3,2,0,1};
        int[] q2={3,2,1,0};
        out("Testing permutation library.");
        out("Let's try 1234->4312 composed with 1234->4321.");
        out("Creating permutations...");
        PermutationElement p1=new PermutationElement(q1);
        PermutationElement p2=new PermutationElement(q2);
        out("in standard notation, "+p1.toString()+" composed with "+p2.toString()+" is: "+p1.actLeft(p2).toString());
        out("in cycle notation, "+p1.toCycleString()+" composed with "+p2.toCycleString()+" is: "+p1.actLeft(p2).toCycleString());
        out("for the inverses, "+p2.getInverse().toString()+" composed with "+p1.getInverse().toString()+" is: "+(p1.actLeft(p2)).getInverse().toString());
        out("we should be getting for this "+p1.getInverse().actRight(p2.getInverse()).toString());
    }
    
    private static void out(String string) {
        System.out.println(string);
    }
}
