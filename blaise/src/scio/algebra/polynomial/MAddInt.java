package scio.algebra.polynomial;

import scio.algebra.GroupElement;
import scio.algebra.GroupElementId;

/**
 * <b>AddInt.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>June 21, 2007, 1:56 PM</i><br><br>
 *
 * A group product of Z, (Z x Z x Z x ..., +), group operation +
 */
public class MAddInt extends GroupElementId {
    /** Number of factors */
    private int nf=1;
    /** Strings for factors */
    final String[] fs={"x","y","z","r","s","t","u","v","w"};
    /** Values of each factor */
    final int[] n;    
    /** Constructor */
    public MAddInt(){nf=1;n=new int[nf];}
    /** Initializes to given value */
    public MAddInt(int nf){this.nf=nf;n=new int[nf];}
    /** Initializes to a single term */
    public MAddInt(int[] n){this.nf=n.length;this.n=n;}
    /** Returns value of ith coordinate */
    public int get(int i){return n[i];}
    /** Returns the identity element. */
    public static GroupElementId getIdentity(){return new MAddInt(3);}
    /** Gets the inverse (-n) */
    public GroupElement getInverse(){
        int[] ni=new int[nf];
        for(int i=0;i<nf;i++){ni[i]=-n[i];}
        return new MAddInt(ni);
    }
    /** Group action is addition */
    public GroupElement actLeft(GroupElement x){
        int[] nr=new int[nf];
        for(int i=0;i<nf;i++){nr[i]=get(i)+((MAddInt)x).get(i);}
        return new MAddInt(nr);
    }
    /** Returns if the underlying group is commutative. */
    public static boolean isCommutative(){return true;}
    /** Compare to another MultAddInt... simple lexicographic ordering */
    public int compareTo(Object o){
        MAddInt x=(MAddInt)o;
        int i=0;
        while(get(i)==x.get(i)){i++;}
        return get(i)-x.get(i);
    }
    /** String output of the term... either "1" or "x^i y^j z^k" */
    public String toString(){
        String s="";
        boolean empty=true;
        for(int i=0;i<nf;i++){
            if(n[i]!=0){
                if(!empty){s+=" ";}
                s+=fs[i];
                if(n[i]!=1){s+="^"+Integer.toString(n[i]);}
                empty=false;
            }
        }
        if(empty){s="1";}
        return s;
    }
}
