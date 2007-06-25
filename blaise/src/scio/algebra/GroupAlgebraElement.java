package scio.algebra;

import java.util.TreeSet;

/**
 * <b>GroupAlgebraElement.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>April 30, 2007, 11:51 AM</i><br><br>
 *
 * Group algebra of elements... basically a weighted array of the specified group elements.
 * Requires three types: one for the group, one for the group elements, and one for coefficients.
 */
public class GroupAlgebraElement<ELEMENT extends GroupElementId> extends GroupElementId {
    /** Stores elements as a (sorted) array of terms */
    protected TreeSet<GroupAlgebraSummand<ELEMENT>> terms;
    
    /** Constructor: creates a new instance of GroupAlgebraElement. Defaults to contain the identity only. */
    public GroupAlgebraElement(){terms=new TreeSet<GroupAlgebraSummand<ELEMENT>>();}
    public GroupAlgebraElement(ELEMENT e){terms=new TreeSet<GroupAlgebraSummand<ELEMENT>>();appendTerm(e);}
    public GroupAlgebraElement(GroupAlgebraSummand<ELEMENT> e){terms=new TreeSet<GroupAlgebraSummand<ELEMENT>>();appendTerm(e);}
    public GroupAlgebraElement(float w,ELEMENT e){terms=new TreeSet<GroupAlgebraSummand<ELEMENT>>();appendTerm(w,e);}
    
    /** Appends a term to this element. All other elements should call this one! */
    public void appendTerm(GroupAlgebraSummand<ELEMENT> tAdd){
        // if already contains the corresponding ELEMENT, add to that term only!
        for(GroupAlgebraSummand<ELEMENT> t:terms){
            if(addIfPossible(t,tAdd)){if(t.getWeight()==0){terms.remove(t);}return;}
        }
        terms.add(tAdd);
    }
    
    /** Checks to see if two terms can be added together... if so, add their weights */
    public boolean addIfPossible(GroupAlgebraSummand<ELEMENT> t1,GroupAlgebraSummand<ELEMENT> t2){
        if(t1.compareTo(t2)==0){t1.w+=t2.w;return true;}
        return false;
    }
    
    
    /** Synonyms... all of these call the above! */
    public void appendTerm(ELEMENT e){appendTerm(new GroupAlgebraSummand(1,e));}
    public void appendTerm(float w,ELEMENT e){appendTerm(new GroupAlgebraSummand(w,e));}
    public void append(GroupAlgebraElement<ELEMENT> e){for(GroupAlgebraSummand t:e.getTermList()){appendTerm(t);}}
    public void append(float w,GroupAlgebraElement<ELEMENT> e){
        for(GroupAlgebraSummand<ELEMENT> t:e.getTermList()){appendTerm(w*t.getWeight(),t.getElement());}
    }
    
    /** Gets the element list */
    public TreeSet<GroupAlgebraSummand<ELEMENT>> getTermList(){return terms;}
    
    /** Override getIdentity method */
    public static GroupElementId getIdentity(){return new GroupAlgebraElement();}
    
    public boolean isIdentity(){return this.toString()=="0";}
    public GroupElement getInverse(){
        if(terms.size()==1){return new GroupAlgebraElement<ELEMENT>((GroupAlgebraSummand<ELEMENT>)terms.first().getInverse());}
        return null;
    }
    /** Perform group action... in place */
    public GroupElement actLeft(GroupElement x){
        GroupAlgebraElement<ELEMENT> x2=(GroupAlgebraElement<ELEMENT>)x;
        GroupAlgebraElement<ELEMENT> temp=new GroupAlgebraElement<ELEMENT>();
        temp.append(this);
        this.terms.clear();
        for(GroupAlgebraSummand<ELEMENT> g1:temp.terms){
            for(GroupAlgebraSummand<ELEMENT> g2:x2.terms){
                //System.out.println("action: "+g1.toString()+" and "+g2.toString());
                GroupAlgebraSummand<ELEMENT> result=(GroupAlgebraSummand<ELEMENT>) g1.actLeft(g2);
                this.appendTerm(result);
                //System.out.println("=> result: "+result.toString());
            }
        }
        //System.out.println("---> result: "+toString());
        return this;
    }
    
    /** String output */
    public String toString(){
        if(terms.size()==0){return "0";}
        String s="";
        for(GroupAlgebraSummand<ELEMENT> t:terms){s+=t.toString();}
        return s;
    }

    /** Comparison */
    // TODO: Fix this!
    public int compareTo(Object o){return 0;}
}
