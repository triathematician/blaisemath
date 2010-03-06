package scio.algebra;

/**
 * <b>GroupAlgebraTerm.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>May 8, 2007, 9:40 AM</i><br><br>
 *
 * Class which combines two groups to create an algebra. Think of one as the
 * coefficient and the other as the term, but in reality this is just the
 * product of two groups. The coefficients are taken to be floats.
 */

public class GroupAlgebraSummand<ELEMENT extends GroupElementId> extends GroupElementId{
    protected ELEMENT e;
    protected float w;
    GroupAlgebraSummand(){e=(ELEMENT)e.getIdentity();w=1;}
    GroupAlgebraSummand(ELEMENT e){this.e=e;w=1;}
    GroupAlgebraSummand(float w,ELEMENT e){this.e=e;this.w=w;}
    GroupAlgebraSummand(GroupAlgebraSummand<ELEMENT> g){this.e=g.e;this.w=g.w;}
    public ELEMENT getElement(){return e;}
    public float getWeight(){return w;}
    public void setWeight(float w){this.w=w;}
    public static GroupElementId getIdentity(){return new GroupAlgebraSummand();}
    public boolean isIdentity(){return super.isIdentity()&&w==1;}
    public GroupElement getInverse(){
        if(w==0){return null;}
        return new GroupAlgebraSummand(1/w,(ELEMENT)e.getInverse());
    }
    public GroupElement actLeft(GroupElement x){
        GroupAlgebraSummand x1=new GroupAlgebraSummand(this);
        GroupAlgebraSummand xb=(GroupAlgebraSummand)x;
        x1.w*=xb.w;x1.e=(ELEMENT)e.actLeft(xb.e);
        return x1;
    }
    
    /** Tests whether a float is really an integer. */
    public String ifInt(Float w){if(w.intValue()==w){return ""+w.intValue();}return w.toString();}
    /** Returns coefficient string... simply + or - if weight is 1 or -1, otherwise +2, -1.5, etc. */
    public String coeffString(){
        if(w==1){return "+";}
        if(w==-1){return "-";}
        return ((w<0)?"":"+")+ifInt(w);
    }
    /** Returns term string */
    public String termString(){return e.toString();}
    /** Returns string view */
    public String toString(){if(w==0){return "0";}return coeffString()+termString();}
    
    /** Pass comparing routine down to ELEMENT! */
    public int compareTo(Object o){return e.compareTo(((GroupAlgebraSummand<ELEMENT>)o).getElement());}
}
