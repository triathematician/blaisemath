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
    protected TreeSet<GroupAlgebraTerm<ELEMENT>> elements;
    
    /** Constructor: creates a new instance of GroupAlgebraElement. Defaults to contain the identity only. */
    public GroupAlgebraElement(){elements=new TreeSet<GroupAlgebraTerm<ELEMENT>>();}
    public GroupAlgebraElement(ELEMENT e){elements=new TreeSet<GroupAlgebraTerm<ELEMENT>>();appendTerm(e);}
    public GroupAlgebraElement(GroupAlgebraTerm<ELEMENT> e){elements=new TreeSet<GroupAlgebraTerm<ELEMENT>>();appendTerm(e);}
    public GroupAlgebraElement(float w,ELEMENT e){elements=new TreeSet<GroupAlgebraTerm<ELEMENT>>();appendTerm(w,e);}
    
    /** Appends a term to this element. All other elements should call this one! */
    public void appendTerm(GroupAlgebraTerm<ELEMENT> tAdd){
        // if already contains the corresponding ELEMENT, add to that term only!
        for(GroupAlgebraTerm<ELEMENT> t:elements){
            if(t.canAdd(tAdd)){
                if(t.getWeight()==0){elements.remove(t);}
                return;
            }
        }
        elements.add(tAdd);
    }
    /** Synonyms... all of these call the above! */
    public void appendTerm(ELEMENT e){appendTerm(new GroupAlgebraTerm(1,e));}
    public void appendTerm(float w,ELEMENT e){appendTerm(new GroupAlgebraTerm(w,e));}
    public void append(GroupAlgebraElement<ELEMENT> e){for(GroupAlgebraTerm t:e.getTermList()){appendTerm(t);}}
    public void append(float w,GroupAlgebraElement<ELEMENT> e){
        for(GroupAlgebraTerm<ELEMENT> t:e.getTermList()){appendTerm(w*t.getWeight(),t.getElement());}
    }
    
    /** Gets the element list */
    public TreeSet<GroupAlgebraTerm<ELEMENT>> getTermList(){return elements;}
    
    /** Override getIdentity method */
    public static GroupElementId getIdentity(){return new GroupAlgebraElement();}
    
    public boolean isIdentity(){return this.toString()=="0";}
    public GroupElement getInverse(){
        if(elements.size()==1){return new GroupAlgebraElement<ELEMENT>((ELEMENT)elements.first().getInverse());}
        return null;
    }
    /** Perform group action... in place */
    public GroupElement actLeft(GroupElement x){
        GroupAlgebraElement<ELEMENT> x2=(GroupAlgebraElement<ELEMENT>)x;
        GroupAlgebraElement<ELEMENT> temp=new GroupAlgebraElement<ELEMENT>();
        temp.append(this);
        this.elements.clear();
        for(GroupAlgebraTerm<ELEMENT> g1:temp.elements){
            for(GroupAlgebraTerm<ELEMENT> g2:x2.elements){
                this.appendTerm((GroupAlgebraTerm<ELEMENT>) g1.actLeft(g2));
            }
        }
        return this;
    }
    
    /** String output */
    public String toString(){
        if(elements.size()==0){return "0";}
        String s="";
        for(GroupAlgebraTerm<ELEMENT> t:elements){s+=t.toString();}
        return s;
    }

    /** Comparison */
    // TODO: Fix this!
    public int compareTo(Object o){return 0;}
}
