package scio.algebra.planar;

import scio.algebra.GroupAlgebraElement;
import scio.algebra.GroupAlgebraSummand;
import scio.algebra.GroupElement;
import scio.algebra.permutation.Permutation;
import scio.algebra.polynomial.MPolynomial;
import scio.graph.Edge;

/**
 * <b>TemperleyLiebAlgebra.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>May 26, 2007, 10:02 AM</i><br><br>
 *
 * Uses the TemperleyLiebElement class and the GroupAlgebra class to create
 * a working platform for the Temperley-Lieb algebra. Routines contained herein are
 * intended to relate to the complex-algebra with TL elements as generators... meaning
 * sums of TL Elements. Anything relating to something other than this should be contained
 * within the TemperleyLiebElement class.
 */
public class TemperleyLiebElement extends GroupAlgebraElement<TemperleyLiebTerm> {
    /** Static variables for symmetrizers */
//    public final static TemperleyLiebElement[] sym={
//        getSymmetrizer(0).removeCrossings(),
//        getSymmetrizer(1).removeCrossings(),
//        getSymmetrizer(2).removeCrossings(),
//        getSymmetrizer(3).removeCrossings(),
//        getSymmetrizer(4).removeCrossings(),
//        getSymmetrizer(5).removeCrossings()//,
//        //getSymmetrizer(6).removeCrossings()
//    };
    public final static TemperleyLiebElement id1=getSymmetrizer(1);
    public final static TemperleyLiebElement[] sym={
        getSymmetrizer(0),
        getSymmetrizer(1),
        getSymmetrizerB(2),
        getSymmetrizerB(3),
        getSymmetrizerB(4),
        getSymmetrizerB(5),
        getSymmetrizerB(6),
        getSymmetrizerB(7)
    };    
    public final static TemperleyLiebElement[] asym={
        getAntiSymmetrizer(0),
        getAntiSymmetrizer(1),
        getAntiSymmetrizer(2),
        getAntiSymmetrizer(3),
        getAntiSymmetrizer(4)
    };
    
    /** Number of strands */
    private int in;
    private int out;
    
    /** Constructor: creates a new instance of TemperleyLiebAlgebra */
    public TemperleyLiebElement(){super();in=2;out=2;}
    /** Initializes to i inputs, o outputs. */
    public TemperleyLiebElement(int i,int o){super();this.in=i;this.out=o;}
    /** Initializes to a specific n value. */
    public TemperleyLiebElement(int n){this(n,n);}
    /** Initializes to another TL algebra element. */
    public TemperleyLiebElement(TemperleyLiebElement e){
        this.in=e.in;
        this.out=e.out;
        for(GroupAlgebraSummand<TemperleyLiebTerm> t:e.terms){
            appendTerm(t.getWeight(),new TemperleyLiebTerm(t.getElement()));
        }
    }
    
    /** Appends a term to this element. All other elements should call this one! */
    public void appendTerm(GroupAlgebraSummand<TemperleyLiebTerm> tAdd){
        // move weight to the coefficient
        tAdd.setWeight(tAdd.getElement().getTotalWeight(tAdd));
        tAdd.getElement().kinks=0;
        tAdd.getElement().positive=true;
        tAdd.getElement().g.deleteTrivialLoops();
        // if already contains the corresponding ELEMENT, add to that term only!
        for(GroupAlgebraSummand<TemperleyLiebTerm> t:terms){
            if(addIfPossible(t,tAdd)){if(t.getWeight()==0){terms.remove(t);}return;}
        }
        terms.add(tAdd);
    }
    
    /** Checks to see if two terms can be added together... if so, add their weights/kink numbers; result is contained in t1 */
    public boolean addIfPossible(GroupAlgebraSummand<TemperleyLiebTerm> t1,GroupAlgebraSummand<TemperleyLiebTerm> t2){
        if(t1.compareTo(t2)==0){t1.setWeight(t1.getWeight()+t2.getWeight());return true;}
        return false;
    }
    
    /** Returns TemperleyLiebAlgebra element with crossings removed. */
    public static TemperleyLiebElement removeCrossings(TemperleyLiebTerm e){
        //if(e.kinks!=0){System.out.println("--> kinks!!");}
        //if(e.g.getNumTrivialLoops()!=0){System.out.println("--> loops!!");}
        TemperleyLiebElement result=new TemperleyLiebElement(e.in,e.out);
        Edge[] ce=e.getCrossedEdges();
        if(ce==null){
            result.appendTerm(1,e);
        }else{
            int p1=ce[0].getSource();
            int p2=ce[1].getSource();
            int p3=ce[0].getSink();
            int p4=ce[1].getSink();
            float COEFF1=+1;
            float COEFF2=+1;
            // Determine coefficients, based on where the edges are... copy down the points first to determine this
            // The above occurs by default (both positive). The only case where this doesn't happen is if the two strands
            // are both vertically oriented (i.e. a simple X). In this case, the opposite source/sink pairing (p1-p4,p2-p3) is (+1),
            // and the source/source plus sink/sink pairing (p1-p2,p3-p4) is (-1).
            // To check for the "X", both sources must be inputs, both sinks outputs... then change COEFF2 to -1
            if(e.inputs.contains(p1)&&e.inputs.contains(p2)&&e.outputs.contains(p3)&&e.outputs.contains(p4)){COEFF2=-1;}
            // Now create the new term and add in the edges
            TemperleyLiebTerm e1=new TemperleyLiebTerm(e);
            e1.getGraph().remove(ce[0]);
            e1.getGraph().remove(ce[1]);
            e1.addEdge(p1,p4);
            e1.addEdge(p2,p3);
            result.append(COEFF1,removeCrossings(e1));
            TemperleyLiebTerm e2=new TemperleyLiebTerm(e);
            e2.getGraph().remove(ce[0]);
            e2.getGraph().remove(ce[1]);
            e2.addEdge(p1,p2);
            e2.addEdge(p3,p4);
            result.append(COEFF2,removeCrossings(e2));
        }
        return result;
    }
    
    /** Removes all crossings in current algebra element. */
    public TemperleyLiebElement removeCrossings(){
        TemperleyLiebElement result=new TemperleyLiebElement(in,out);
        for(GroupAlgebraSummand<TemperleyLiebTerm> t:getTermList()){
            TemperleyLiebElement rc=removeCrossings(t.getElement());
            //System.out.println("before: "+t.getElement().toString()+" & after: "+rc.toString());
            result.append(t.getWeight(),rc);
        }
        this.terms=result.terms;
        return this;
    }
    
    /** Concenates two tl elements */
    public static TemperleyLiebElement concatenate(TemperleyLiebElement a,TemperleyLiebElement b){
        TemperleyLiebElement result=new TemperleyLiebElement(a.in+b.in,a.out+b.out);
        for(GroupAlgebraSummand<TemperleyLiebTerm> s:a.terms){
            for(GroupAlgebraSummand<TemperleyLiebTerm> t:b.terms){
                result.appendTerm(s.getWeight()*t.getWeight(),TemperleyLiebTerm.concatenate(s.getElement(),t.getElement()));
            }
        }
        return result;
    }
    
    /** Returns an "arrow" from the TL algebra... */
    public static TemperleyLiebElement getArrow(int n){
        TemperleyLiebElement result=new TemperleyLiebElement(n);
        result.appendTerm(n,new TemperleyLiebTerm(n));
        String s="";
        for(int i=0;i<n-1;i++){s+=(n-i-2);}
        s+="0";
        // replace i'th position with zero and append result with appropriate coefficient
        for(int i=0;i<n-1;i++){
            String t="";
            t+=s.substring(0,i);
            t+="0";
            t+=s.substring(i+1,s.length());
            result.appendTerm(TemperleyLiebTerm.pown(1)*(n-i-1),new TemperleyLiebTerm(t));
        }
        return result;
    }
    
    /** Alternate computation of symmetrizer: uses recursion. */
    public static TemperleyLiebElement getSymmetrizerB(int n){
        if(n==1){return id1;}
        TemperleyLiebElement prior=getSymmetrizerB(n-1);
        TemperleyLiebElement result=concatenate(id1,prior);
        result.actLeft(getArrow(n));
        return result;
    }
    
    /** Returns the symmetrizer on n elements as an element of the TL algebra...
     * this is a weighted sum of all permutations.
     */
    public static TemperleyLiebElement getSymmetrizer(int n){
        Permutation p=new Permutation(n);
        TemperleyLiebElement result=new TemperleyLiebElement(n);
        float weight=1; //divide by/(float)factorial(n);
        while(p.hasNext()){
            TemperleyLiebTerm e=new TemperleyLiebTerm(n);
            e.setToPermutation(p);
            result.appendTerm(weight,e);
            p=p.next();
        }
        TemperleyLiebTerm e=new TemperleyLiebTerm(n);
        e.setToPermutation(p);
        result.appendTerm(weight,e);
        return result;
    }
    public static long factorial(int n){return(n<=1)?1:n*factorial(n-1);}
    /** Returns the anti-symmetrizer */
    public static TemperleyLiebElement getAntiSymmetrizer(int n){
        Permutation p=new Permutation(n);
        TemperleyLiebElement result=new TemperleyLiebElement(n);
        float weight=1; //divide by/(float)factorial(n);
        while(p.hasNext()){
            TemperleyLiebTerm e=new TemperleyLiebTerm(n);
            e.setToPermutation(p);
            result.appendTerm(weight*p.sign(),e);
            p=p.next();
        }
        TemperleyLiebTerm e=new TemperleyLiebTerm(n);
        e.setToPermutation(p);
        result.appendTerm(weight,e);
        return result;
    }
    /** Returns whether there are any terms with crossings. */
    public boolean hasCrossings(){
        for(GroupAlgebraSummand<TemperleyLiebTerm> t:terms){if(t.getElement().hasCrossings()){return true;}}
        return false;
    }
    /** String output. Defaults to parenthetical notation if all elements are basis
     * elements, otherwise gives explicit notation.
     */
    public String toString(){
        if(terms.size()==0){return "0";}
        String s="";
        if(!hasCrossings()){
            for(GroupAlgebraSummand<TemperleyLiebTerm> t:terms.descendingSet()){
                s+=t.coeffString()+"("+t.getElement().toParenString()+")";
            }
            return s;
        }
        for(GroupAlgebraSummand<TemperleyLiebTerm> t:terms.descendingSet()){
            s+=t.coeffString()+t.getElement().toString();
        }
        return s;
    }
    /** Override actLeft method... required to deal with inputs/outputs properly */
    public GroupElement actLeft(GroupElement x){
        this.in=((TemperleyLiebElement)x).in;
        TemperleyLiebElement temp=new TemperleyLiebElement(this);
        TemperleyLiebElement xtle=(TemperleyLiebElement)x;
        this.terms.clear();
        for(GroupAlgebraSummand<TemperleyLiebTerm> g1:temp.terms){
            for(GroupAlgebraSummand<TemperleyLiebTerm> g2:xtle.terms){
                TemperleyLiebTerm resultTerm=(TemperleyLiebTerm)((TemperleyLiebTerm)g1.getElement()).actLeft(g2.getElement());
                float resultWeight=g1.getWeight()*g2.getWeight();
                this.appendTerm(resultWeight,resultTerm);
            }
        }
        return this;
    }
    /** Provide static computation */
    public static TemperleyLiebElement actLeft(GroupElement x1,GroupElement x2){
        TemperleyLiebElement result=(TemperleyLiebElement)new TemperleyLiebElement((TemperleyLiebElement)x1).actLeft(x2);
        result.in=((TemperleyLiebElement)x2).in;
        result.out=((TemperleyLiebElement)x1).out;
        return result;
    }
    
    /** Returns polynomial representation of the element */
    public MPolynomial toPolynomial(int[] split){
        MPolynomial result=new MPolynomial();
        for(GroupAlgebraSummand<TemperleyLiebTerm> s:terms){
            //System.out.println("Term "+s.getElement().toParenString()+" becomes "+s.getElement().toPolynomial(split));
            result.append(s.getWeight(),s.getElement().toPolynomial(split));
        }
        return result;
    }
   
    /** Returns a rank one central function. */
    public static MPolynomial getCentral(int n){
        int[] split={n};
        return sym[n].toPolynomial(split);
    }
    
    /** Determines whether ints a,b,c are "admissible" */
    public static boolean admissible(int a,int b,int c){
        return (a+b+c)%2==0 && a+b>=c && a+c>=b && b+c>=a;
    }
    /** Returns a composition of three symmetrizers with strand #s a,b,c,
     * together with intermediate connections... */
    public static TemperleyLiebElement getTripleSymmetrizer(int a,int b,int c){
        if(!admissible(a,b,c)){System.out.println("Error: non-admissible triple!");return null;}
        TemperleyLiebElement ea=sym[a];
        TemperleyLiebElement eb=sym[b];
        TemperleyLiebElement ec=sym[c];
        TemperleyLiebTerm c1=new TemperleyLiebTerm();c1.initLUL(a,b,c);c1.flipVertical();
        TemperleyLiebTerm c2=new TemperleyLiebTerm();c2.initLUL(a,b,c);
        TemperleyLiebElement result=concatenate(ea,eb);
        result.actLeft(c1.toElement());
        result.actLeft(ec);
        result.actLeft(c2.toElement());
        return result;
    }
    /** Returns central function corresponding to a given admissible triple. */
    public static MPolynomial getCentral(int a,int b,int c){
        int[] split={a,b};
        return getTripleSymmetrizer(a,b,c).toPolynomial(split);
    }
    
    /** Determines whether a sextet is "admissible" for rank 3 central functions */
    public static boolean admissible(int a,int b,int c,int d,int e,int f){
        return admissible(a,b,d) && admissible(a,b,f) && admissible(c,d,e) && admissible(c,f,e);
    }
    /** Returns composition of six symmetrizers */
    public static TemperleyLiebElement getSexySymmetrizer(int a,int b,int c,int d,int e,int f){
        if(!admissible(a,b,c,d,e,f)){System.out.println("Error: non-admissible sextet!");return null;}
        TemperleyLiebElement ea=sym[a];
        TemperleyLiebElement eb=sym[b];
        TemperleyLiebElement ec=sym[c];
        TemperleyLiebElement ed=sym[d];
        TemperleyLiebElement ee=sym[e];
        TemperleyLiebElement ef=sym[f];
        TemperleyLiebTerm abd=new TemperleyLiebTerm();abd.initLUL(a,b,d);abd.flipVertical();
        TemperleyLiebTerm dce=new TemperleyLiebTerm();dce.initLUL(d,c,e);dce.flipVertical();        
        TemperleyLiebTerm fce=new TemperleyLiebTerm();fce.initLUL(f,c,e);
        TemperleyLiebTerm abf=new TemperleyLiebTerm();abf.initLUL(a,b,f);
        TemperleyLiebTerm cid=new TemperleyLiebTerm(c);
        TemperleyLiebElement piece1=concatenate(ea,eb);
        piece1.actLeft(abd.toElement());
        piece1.actLeft(ed);
        TemperleyLiebElement piece2=new TemperleyLiebElement(ef);
        piece2.actLeft(abf.toElement());
        piece2.actLeft(concatenate(ea,eb));
        TemperleyLiebElement result=concatenate(piece1,ec);
        result.actLeft(dce.toElement());
        result.actLeft(ee);
        result.actLeft(fce.toElement());
        result.actLeft(concatenate(piece2,new TemperleyLiebElement(cid.toElement())));
        return result;
    }
    /** Returns central function corresponding to a given admissible triple. */
    public static MPolynomial getCentral(int a,int b,int c,int d,int e,int f){
        int[] split={a,b,c};
        if(!admissible(a,b,c,d,e,f)){System.out.println("Error: non-admissible sextet ["+a+","+b+","+c+","+d+","+e+","+f+"]");return null;}
        return getSexySymmetrizer(a,b,c,d,e,f).removeCrossings().toPolynomial(split);
    }
}