package scio.algebra.planar;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import scio.algebra.GroupAlgebraSummand;
import scio.algebra.GroupElement;
import scio.algebra.GroupElementId;
import scio.algebra.permutation.Permutation;
import scio.algebra.polynomial.MAddInt;
import scio.algebra.polynomial.MPolynomial;
import scio.graph.Edge;
import scio.graph.GraphE;

/**
 * <b>TemperleyLiebElement.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>May 8, 2007, 11:51 AM</i><br><br>
 *
 * The TL algebra may be implemented as a graph... this class limits vertex valency
 * to 1-2 only! and all leafs are either inputs or outputs!<br><br>
 *
 * The main difference between this class and the planar graph classes is that the inputs
 * and outputs are STRONGLY controlled. The class encodes the arrays of inputs and outputs
 * with ALL actions.<br><br>
 *
 * In the standard case (default), the TL Element exists in TL_n, and the inputs are
 * [1,2,...,n]; outputs are [2n,2n-1,...,n+1]. The reverse order ensures that the vertices
 * proceed in order when read in clockwise fashion.<br><br>
 *
 * Additionally keeps track of the number of "kinks" in the element, and the "positivity" in the element.
 * This is specific to special kinds of trace diagrams, and will not extend well to more general cases.<br><br>
 *
 * Here are some default formatting options:
 *   1) Nested parentheses counting: e.g. ( (()()()) () ) () () becomes 53000100... numbers represent how many parentheses are inside
 *   2) Integer pairings...
 */
public class TemperleyLiebTerm extends PlanarGraphTerm implements Iterator<TemperleyLiebTerm> {
    
    // Fields
    
    ArrayList<Integer> inputs;
    ArrayList<Integer> outputs;
    int in=0;
    int out=0;
    int kinks=0;
    boolean positive=true;
    
    // Constructors
    
    /** Default: set to identity, or each input is matched with each output */
    TemperleyLiebTerm(){
        super();
        g.multiEdge=true;
        g.directed=false;
        initPuts(2,2);
        initId();
    }
    /** Default: sets up for a given n. */
    TemperleyLiebTerm(int n){this();initPuts(n);initId();}
    /** Sets up for i inputs and o outputs. */
    TemperleyLiebTerm(int i,int o){this();initPuts(i,o);initId();}
    /** Construct based on a list of pairings... must be ordered 1...2n, half ins/half outs */
    TemperleyLiebTerm(int[][] pair){
        super();g.multiEdge=true;g.directed=false;
        /** pair should be an ?x2 array! */
        for(int i=0;i<pair.length;i++){addEdge(pair[i][0],pair[i][1]);}
        initPuts(pair.length-g.getNumTrivialLoops());
    }
    /** Construct with a pairing list, and a nonstandard i/j pairing of ins/outs */
    TemperleyLiebTerm(int[][] pair,int i,int o){this(pair);initPuts(i,o);}
    /** Construct with a pairing list, and a standard choice of ins/outs */
    TemperleyLiebTerm(int[][] pair,int n){this(pair,n,n);}
    /** Construct based on nesting information */
    TemperleyLiebTerm(int[] paren){
        this(paren.length);
        g.clear();
        setToParen(paren);
    }
    /** Construct based on another TL Element */
    TemperleyLiebTerm(TemperleyLiebTerm e){
        super();g.multiEdge=true;g.directed=false;
        inputs=(ArrayList<Integer>)e.inputs.clone();
        outputs=(ArrayList<Integer>)e.outputs.clone();
        this.in=e.in;
        this.out=e.out;
        kinks=e.kinks;
        positive=e.positive;
        g=e.getGraph().clone();
    }
    /** Construct based on a paren string */
    TemperleyLiebTerm(String s){
        this();
        g.clear();
        setToParen(s);
        initPuts(s.length());
    }
    
    // Initializer methods
    
    /** Resets the underlying graph */
    public void clear(){g.clear();}
    
    /** Initializes inputs to 1->n and outputs to 2n->(n+1) */
    public void initPuts(int i,int o){
        // i+o should always be even
        if((i+o)%2!=0){System.out.println("Error: inputs+outputs should be an even number!");}
        this.in=i;
        this.out=o;
        inputs=new ArrayList<Integer>();
        outputs=new ArrayList<Integer>();
        for(int j=1;j<=in;j++){inputs.add(j);}
        for(int j=1;j<=out;j++){outputs.add(in+out+1-j);}
    }
    /** Initializes with n inputs, n outputs */
    public void initPuts(int n){initPuts(n,n);}
    
    /** Initializes to identity, if n->n strands. If in/out are not the same value, then will
     * not return the identity, but still returns what you get by connecting the first vertex
     * to the last, the second to the next to last, etc. */
    public void initId(){
        g.clear();
        int n=(in+out)/2;
        for(int i=1;i<=n;i++){addEdge(i,2*n+1-i);}
    }
    
    /** Initializes to a "standard" connection for an admissible triple a,b,c...
     * Assumes a,b at the bottom and c at the top. */
    public void initLUL(int a,int b,int c){
        if(!TemperleyLiebElement.admissible(a,b,c)){System.out.println("Admissibility error!");return;}
        g.clear();
        initPuts(a+b,c);
        int n=(a+b+c)/2;
        int[] paren=new int[n];
        int t1=(a+c-b)/2;
        int t2=(a+b-c)/2;
        int t3=(b+c-a)/2;
        for(int i=1;i<=t1;i++){paren[i-1]=n-i;}
        for(int i=1;i<=t2;i++){paren[t1+i-1]=t2-i;}
        for(int i=1;i<=t3;i++){paren[t1+t2+i-1]=t3-i;}
        this.setToParen(paren);
    }
    
    /** Flips the term upside-down... corresponds to reversing the order of the vertices. */
    public void flipVertical(){
        GraphE g2=g.clone();
        g2.reverseLabels();
        g.clear();
        for(Edge e:g2){this.addEdge(e.getSource(),e.getSink(),e.getWeight());}
        initPuts(out,in);
    }
    
    /** Override add edge method to ensure ordering of vertices. */
    public void addEdge(int a,int b,int w){
        if(a>b){int c=a;a=b;b=c;}
        if(validEdge(a,b,w)){g.addEdge(a,b,w);}
    }
    
    // Set methods
    
    /** Returns a TL element corresponding to a given permutation. */
    public TemperleyLiebTerm setToPermutation(Permutation p){
        initPuts(p.getN());
        g.clear();
        for(int i=1;i<=p.getN();i++){addEdge(i,2*p.getN()+1-p.get(i));}
        return this;
    }
    
    /** Adds to parenthetical elements from given start position. */
    public void setToParen(int i0,int[] paren,int ps,int pe){
        if(paren==null||pe<ps||ps>=paren.length||pe>=paren.length||i0>=2*paren.length){return;}
        /** Add first edge of paren */
        int inParen=paren[ps];
        //System.out.println("adding edge: "+i0+" to "+(i0+2*inParen+1));
        g.addEdge(i0,i0+2*inParen+1);
        /** Add edges inside paren 0 */
        setToParen(i0+1,paren,ps+1,ps+inParen);
        /** Add edges outside paren 0 */
        setToParen(i0+2*inParen+2,paren,ps+inParen+1,pe);
    }
    /** Same method without bounds. */
    public void setToParen(int[] paren){setToParen(1,paren,0,paren.length-1);}
    /** Sets paren using a string... notation is simply a list of numbers */
    public void setToParen(String s){
        int[] p=new int[s.length()];
        for(int i=0;i<s.length();i++){
           p[i]=s.charAt(i)-'0';
        }
        setToParen(p);
    }
    
    /** Initializes to element represented by the string. Returns false if not valid.
     * Sample notation: instance3.setTo("{(2)(1-10)(2-3)(4-6)(5-7)(8-9)}"); */
    public boolean setTo(String s){
        TreeMap<Integer,Integer> result=new TreeMap<Integer,Integer>();
        // Match numbers/spaces/-/parentheses inside brackets
        Matcher m1=Pattern.compile("\\{([\\s\\d\\(\\)\\-]*)\\}").matcher(s);
        if(m1.find()){s=m1.group(1);}else{return false;}
        g.clear();
        // Match trivial loops (no dash inside parentheses)
        Matcher m2=Pattern.compile("\\([\\s]*([\\d]*)[\\s]*\\)").matcher(s);
        if(m2.find()){g.addEdge(-1,-1,Integer.valueOf(m2.group(1)));}
        // Now separates groups inside each set of parentheses
        Matcher m3=Pattern.compile("\\([\\s]*([\\d]*)[\\s]*\\-[\\s]*([\\d]*)[\\s]*\\)").matcher(s);
        // Copy into the hashmap
        while(m3.find()){this.addEdge(Integer.valueOf(m3.group(1)),Integer.valueOf(m3.group(2)));}
        int n=g.size();
        if(g.getNumTrivialLoops()>0){n--;}
        initPuts(n);
        return true;
    }
    
    // Get methods
    
    /** Returns parenthetical notation, if possible... does not check validity! */
    public int[] toParen(){
        int[] result=new int[(in+out)/2];
        int i=0;
        for(Edge e:g){if(!e.isTrivial()){result[i]=(e.getSink()-e.getSource()-1)/2;i++;}}
        return result;
    }
    
    /** Returns paren string */
    public String toParenString(){
        if(hasCrossings()){return "";}
        int[] p=toParen();
        String s="";
        for(int i=0;i<p.length;i++){s+=p[i];}
        return s;
    }/** Prints out the TL element */
    public String toString(){return ((kinks%2==1)?"-":"")+g.toString();}
    /** Prints out the TL element, with containing group */
    public String toLongString(){return ((kinks%2==1)?"-":"")+g.toString()+" in TL("+in+","+out+")";}
    
    /** Returns TemperleyLiebElement containing just this term. */
    public TemperleyLiebElement toElement(){
        TemperleyLiebElement result=new TemperleyLiebElement(in,out);
        result.appendTerm(this);
        return result;
    }
    
    /** Returns loops made by connecting top and bottom. */
    public ArrayList<ArrayList<Integer>> toLoops(){
        if(in!=out){return null;}
        // Look at just the graph. Add edges from top to bottom.
        GraphE ga=new GraphE(g);
        for(int i=1;i<=in;i++){ga.addEdge(2*in+1-i,i);}
        return ga.getLoops();
    }
    /** Traces each closed loop in the term and outputs code for the resulting loop.
     * @param split ={# a strands, # b strands, etc.}
     * @return String like [ab_a_, ab_a_a_b_, ...] (underscore represents direction change)
     */
    public ArrayList<String> toLoopStrings(int[] split){
        ArrayList<String> r=new ArrayList<String>();
        // sets up labels
        ArrayList<String> s=new ArrayList<String>();
        s.add("");
        for(int i=0;i<split.length;i++){
            char c=(char)('a'+i);
            for(int j=0;j<split[i];j++){s.add(String.valueOf(c));}
        }
        // copies results to strings
        ArrayList<ArrayList<Integer>> loops=toLoops();
        for(ArrayList<Integer> lp:loops){
            String rtemp="";
            lp.add(lp.get(0));
            for(int i=0;i<lp.size()-1;i++){
                if(inputs.contains(lp.get(i))){rtemp+=s.get(lp.get(i));}
                if((inputs.contains(lp.get(i))&&inputs.contains(lp.get(i+1)))
                        ||(outputs.contains(lp.get(i))&&outputs.contains(lp.get(i+1)))) {
                    rtemp+="_";}
            }
            r.add(rtemp);
        }
        return r;
    }
    /** 
     * Associates a polynomial to the term... works much like the above. 
     * @param split     determines # of each matrix variable to use
     */
    public MPolynomial toPolynomial(int[] split){
        // first labels are "1", the second are "2" and so on...
        ArrayList<Integer> s=new ArrayList<Integer>();
        s.add(-1); // placeholder
        for(int i=0;i<split.length;i++){
            for(int j=0;j<split[i];j++){s.add(i+1);}
        }
        // start by multiplying by the weight of this term
        MAddInt rTerm=new MAddInt(3);
        float rWeight=pown(kinks)*pow(2,g.getNumTrivialLoops());
        // copies results to strings
        ArrayList<ArrayList<Integer>> loops=toLoops();
        //ArrayList<ArrayList<Integer>> result=new ArrayList<ArrayList<Integer>>();
        //System.out.println(toParenString());
        for(ArrayList<Integer> lp:loops){
            lp.add(lp.get(0));
            // keep track of positioning of strands... useful later
            boolean[] lower=new boolean[lp.size()-1];
            boolean[] upper=new boolean[lp.size()-1];
            int up=1; // keeps track of up or down orientation            
            String sKink=""; // keeps track of kinks
            ArrayList<Integer> r=new ArrayList<Integer>(); // keeps track of variables
            for(int i=0;i<lp.size()-1;i++){
                // tracks caps and cups
                lower[i]=inputs.contains(lp.get(i))&&inputs.contains(lp.get(i+1));
                upper[i]=outputs.contains(lp.get(i))&&outputs.contains(lp.get(i+1));
                // appends variable... only for the bottom strands
                if(inputs.contains(lp.get(i))){
                    if(r.size()>0&&r.get(r.size()-1)+up*s.get(lp.get(i))==0){r.remove(r.size()-1);
                    } else{r.add(up*s.get(lp.get(i)));}
                }
                // for caps and cups, reverse orientation, log kink string
                if(lower[i]||upper[i]){up=-up;}
                if(lower[i]){sKink+=(lp.get(i+1)>lp.get(i)?"+":"-");}
                if(upper[i]){sKink+=(lp.get(i+1)>lp.get(i)?"-":"+");}
            }
            
            // Count up the total number of kinks
            Pattern ppmm=Pattern.compile("\\+{2}+|\\-{2}+");
            Matcher m=ppmm.matcher(sKink);
            int kinkCount=0;
            while(m.find()){
                sKink=m.replaceFirst("");
                m=ppmm.matcher(sKink);
                kinkCount++;
            }
            rWeight*=pown(kinkCount);
            
            // Eliminate variables and their inverses
            while(r.size()>0&&r.get(0)+r.get(r.size()-1)==0){
                r.remove(0);
                r.remove(r.size()-1);
            }
            
            // compute the correct polynomial term...
            if(r.toString().equals("[]")){rWeight*=2;}
            rTerm=(MAddInt)rTerm.actLeft(new MAddInt(convertPoly(r, split.length)));
        }
        
        return new MPolynomial(rWeight,rTerm);
    }
    
    /** Produces list of n integers with 1 in ith position */
    private int[] getArray(int n,int i){
        int[] result=new int[n];
        if(i>=0&&i<n){result[i-1]=1;}
        return result;
    }
    /** Converts an integer list as produced above to an (MAddInt) integer list. */
    public int[] convertPoly(ArrayList<Integer> r, int l){
        int n=12;
        if(r.size()==0){return getArray(n,-1);}
        if(r.size()==1){return getArray(n,r.get(0));}
        if(r.size()==2){return getArray(n,Math.abs(r.get(0))+Math.abs(r.get(1))+l-2);}
        if(r.size()==3){
            if(r.toString().equals("[1, 3, -2]")){return getArray(n,7);}
            if(r.toString().equals("[1, -2, 3]")){return getArray(n,8);}
        }
        if(r.size()==4){
            if(r.toString().equals("[1, 2, -3, -2]")){return getArray(n,9);}
            if(r.toString().equals("[1, -2, -3, 2]")){return getArray(n,10);}
        }
        if(r.size()==5){
            if(r.toString().equals("[1, 3, -1, 2, -3]")){return getArray(n,11);}
            if(r.toString().equals("[1, -3, 2, -1, 3]")){return getArray(n,12);}
            
        }
        System.out.println("TemperleyLiebTerm.convertPoly: unsupported array "+r.toString());
        return null;
    }
    
    /** Gets combined weight */
    public static int pow(int a,int p){if(p==0){return 1;}return a*pow(a,p-1);}
    public static int pown(int p){if(p%2==0){return 1;}return -1;}
    public float getTotalWeight(GroupAlgebraSummand<TemperleyLiebTerm> owner){
        return (float)(owner.getWeight()*pown(kinks)*pow(2,g.getNumTrivialLoops()));
    }
    public float getTotalWeight(){return (float)pown(kinks)*pow(2,g.getNumTrivialLoops());}
    
    /** Returns whether this is indeed a basis element... */
    public boolean isBasisElement(){
        //System.out.println("crossings: "+Boolean.toString(hasCrossings())+", trivial: "+Boolean.toString(g.getNumTrivialLoops()==0)+", kinks: "+Boolean.toString(kinks==0));
        return (!hasCrossings())&&g.getNumTrivialLoops()==0&&kinks==0;
    }
    
    /** Validity checking redefined! Determines if it's "ok" to add the given edge.
     * No vertex can be used more than once!
     */
    public boolean validEdge(int a,int b, int w){
        if(a==b&&a==-1){return true;}
        if(a<1||b<1){return false;}
        if(a==b||w!=1){return false;}
        TreeSet<Integer> vxUsed=g.getVertices();
        return !(vxUsed.contains(a)||vxUsed.contains(b));
    }
    
    /** Reduces the vertex label set to remove gaps */
    private void relabelVertices(){g.relabelVertices();initPuts(in,out);}
    
    /** Determines if two edges incident to the given vertices are "crossed". Easy test given the cyclic ordering of vertices.
     * Uses a "cross" product to determine this!
     */
    public boolean crossed(int va,int vb){
        int va2=g.getAdjacency(va).first();
        int vb2=g.getAdjacency(vb).first();
        boolean result=(va-vb)*(va2-vb)*(va-vb2)*(va2-vb2)<0;
        //System.out.println("strand between verices "+va+" and "+vb+ " is "+(result?"crossed.":"not crossed."));
        return result;
    }
    /** As above, but with edges */
    public boolean crossed(Edge a,Edge b){
        int va=a.getSource();
        int va2=a.getSink();
        int vb=b.getSource();
        int vb2=b.getSink();
        return (va-vb)*(va2-vb)*(va-vb2)*(va2-vb2)<0;
    }
    /** Returns array of two edges which are crossed, or null if there are none such! */
    public Edge[] getCrossedEdges(){
        for(Edge e1:g){for(Edge e2:g){
            if(e1==e2){continue;}
            if(crossed(e1,e2)){Edge[] result={e1,e2};return result;}
        }}
        return null;
    }
    /** Returns whether there are any crossings. */
    public boolean hasCrossings(){
        return (getCrossedEdges()!=null);
    }
    
    /** Fundamental group action... puts this TL element on top of another, reduces the
     * corresponding diagram, and returns the result.
     * @param x The TL element to act upon (goes on the bottom)
     * @return The result of the gluing operation.
     */
    public GroupElementId actLeft(GroupElement x){
        // perform the gluing; result is contained in gTemp
        TemperleyLiebTerm tlx=(TemperleyLiebTerm)x;
        // tests to see if elements are compatible
        if(this.inputs.size()!=tlx.outputs.size()){
            System.out.println("TemperleyLiebTerm.actLeft error: TL elements cannot be glued!");
            return null;
        }
        // set up gTemp as the bottom portion of the diagram, so its puts will not change
        GraphE gTemp=tlx.getGraph().clone();
        int plus=gTemp.glueTo(this.getGraph(),this.inputs,tlx.outputs);
        //System.out.println("gtemp"+gTemp.toString());
        
        // Constructs the new graph based on the above computation.
        TemperleyLiebTerm result=new TemperleyLiebTerm();
        result.g.clear();
        result.inputs=tlx.inputs;
        result.in=tlx.in;
        result.outputs=this.outputs;
        result.out=this.out;
        
        if(this.inputs.size()==0){
            result.getGraph().addAll(gTemp);
        }else{
            // populate a mapping of vertices to adjacent vertices
            TreeMap<Integer,TreeSet<Integer>> adj=gTemp.getAllAdjacencies();
            ArrayDeque<ArrayDeque<Integer>> strands=getAllStrandsFromAdjacency(gTemp,adj);
            //System.out.println("adj"+adj.toString());
            //System.out.println("strands"+strands.toString());
            
            // Construct and add an edge for each strand. Here's where some account
            // must be taken for signs/kinks/vertex removals.
            GraphE resultGraph=new GraphE();resultGraph.directed=false;resultGraph.multiEdge=true;
            resultGraph.addEdge(-1,-1,gTemp.getLoopsAt(-1));
            int newKinks=0;
            for(ArrayDeque<Integer> strand:strands){
                //System.out.println("kinks in strand "+strand.toString()+" = "+kinkNumber(strand));
                newKinks+=kinkNumber(strand);
                if(strand.getFirst()==strand.getLast()){resultGraph.addTrivialLoop();} else{
                    int start=strand.getFirst();
                    int end=strand.getLast();
                    resultGraph.addEdge(start,end);
                    //check for crossings along first/last edge of the strand
                    if(start>plus&&end>plus&&this.crossed(start-plus,end-plus)){newKinks++;} else if(start<=plus&&end<=plus&&tlx.crossed(start,end)){newKinks++;}
                }
            }
            //System.out.println(resultGraph.toString());
            result.getGraph().addAll(resultGraph);
            result.kinks=tlx.kinks+this.kinks+newKinks;
        }
        result.relabelVertices();
        return result;
    }
    
    // STATIC METHODS
    
    /** Concatenates two TemperleyLiebTerm's together, returns a third. */
    public static TemperleyLiebTerm concatenate(TemperleyLiebTerm a,TemperleyLiebTerm b){
        TemperleyLiebTerm result=new TemperleyLiebTerm(a);
        result.initPuts(a.in+b.in,a.out+b.out);
        result.g.addToLabelsAbove(b.in+b.out,a.in);
        for(Edge e:b.g){
            if(e.isTrivial()){result.addEdge(-1,-1,e.getWeight());} else{result.g.addEdge(e.getSource()+a.in,e.getSink()+a.in,e.getWeight());}
        }
        return result;
    }
    
    /** Returns whether a given integer list is a valid paren notation. */
    public static boolean validParen(int[] p,int ps,int pe){
        if(p==null||pe>=p.length){return false;}
        if(pe<ps){return true;}
        if(pe==ps){return p[ps]==0;}
        return validParen(p,ps+1,ps+p[ps])&&validParen(p,ps+p[ps]+1,pe);
    }
    /** Same method without bounds. */
    public static boolean validParen(int[] p){return validParen(p,0,p.length-1);}
    
    /** Help method. Determines which of a given integer list is contained in an arraydeque. */
    private static Integer notContained(TreeSet<Integer> adj,ArrayDeque<Integer> deque){
        int v0=adj.first();
        if(!deque.contains(v0)){return v0;}
        int v1=adj.last();
        if(!deque.contains(v1)){return v1;}
        return null;
    }
    
    /** Returns a deque containing the given integer based on a list of adjacencies. */
    public static ArrayDeque<Integer> getStrandFromAdjacency(GraphE g,TreeMap<Integer,TreeSet<Integer>> adj,Integer i){
        ArrayDeque<Integer> result=new ArrayDeque<Integer>();
        result.add(i);
        // add onto the beginning of the deque
        Integer newFirst;
        do{
            newFirst=notContained(adj.get(result.getFirst()),result);
            if(newFirst==null){break;}
            result.addFirst(newFirst);
        }while(true);
        // add onto the end of the deque
        Integer newLast;
        do{
            newLast=notContained(adj.get(result.getLast()),result);
            if(newLast==null){break;}
            result.addLast(newLast);
        }while(true);
        // if the result is a loop, add on another vertex to indicate this
        if(g.getValency(result.getFirst())==2){result.addLast(result.getFirst());}
        return result;
    }
    
    /** Returns an array of deques as given above, one for each connected component. */
    public static ArrayDeque<ArrayDeque<Integer>> getAllStrandsFromAdjacency(GraphE g,TreeMap<Integer,TreeSet<Integer>> adj){
        ArrayDeque<ArrayDeque<Integer>> result=new ArrayDeque<ArrayDeque<Integer>>();
        TreeSet<Integer> vertices=new TreeSet<Integer>();
        vertices.addAll(adj.keySet());
        vertices.remove(-1);
        do{
            result.add(getStrandFromAdjacency(g,adj,vertices.first()));
            vertices.removeAll(result.getLast());
        }while(vertices.size()>0);
        return result;
    }
    
    /** Counts the number of kinks in an ArrayDeque. To have a kink, there must be at least 4 elements (if a loop) or 5 (if an edge).
     * In the case of an edge, the first and last elements are ignored. Otherwise, a kink exists when the list of integers increases or
     * decreases twice in a row... e.g. 2-3-4 represents a kink, while 2-4-3 does not. This simply counts the number of such!
     * The case with an X above a U (or upsidedown) is considered outside of this method.
     * @param strand A list of integers beginning/ending with the same value if a loop... a strand in the composition of two elements.
     * @return Number of kinks contained in the strand.
     */
    public static int kinkNumber(ArrayDeque<Integer> strand){
        // NEW VERSION
        boolean open=strand.getFirst()!=strand.getLast();
        if(strand.size()<4){return 0;}
        ArrayDeque<Integer> scopy=strand.clone();
        if(open){scopy.removeFirst();scopy.removeLast();}
        int last=0;
        String s="";
        for(Integer current:scopy){if(last!=0){s+=(current>last?"+":"-");}last=current;}// in the case of open strands, delete the first and last sign
        Pattern ppmm=Pattern.compile("\\+{2}+|\\-{2}+");
        Matcher m=ppmm.matcher(s);
        int count=0;
        while(m.find()){
            s=m.replaceFirst("");
            m=ppmm.matcher(s);
            count++;
        }
        return count;
    }
    
    // Iterator Methods... basis elements only!
    
    /** Whether a parenthetical notation has a next value */
    public static boolean hasParenAfter(int[] p,int ps,int pe){
        if(!validParen(p,ps,pe)){return false;}
        for(int i=ps;i<=pe;i++){if(p[i]!=0)return true;}
        return false;
    }
    /** Same method without bounds */
    public static boolean hasParenAfter(int[] p){return hasParenAfter(p,0,p.length-1);}
    /** Determines the next element given parenthetical notation */
    public static int[] parenAfter(int[] p,int ps,int pe){
        //String s="1. ps="+ps+" pe="+pe+" p=[";for(int i=ps;i<=pe;i++){s+=p[i];}System.out.println(s+"]");
        if(ps==pe){int[] result={p[ps]-1};return result;}
        if(!hasParenAfter(p,ps,pe)){return null;}
        int[] result=new int[pe-ps+1];
        for(int i=ps;i<=pe;i++){result[i-ps]=p[i];}
        /** Break p[ps..pe] down into 3 sets: p[ps], p[ps+1..ps+first], p[ps+first+1..pe] */
        int first=p[ps];
        /** Increment the last set first, if possible */
        if(hasParenAfter(p,ps+first+1,pe)){
            //System.out.println("replacing after");
            int[] pReplace=parenAfter(p,ps+first+1,pe);
            for(int i=ps+first+1;i<=pe;i++){result[i-ps]=pReplace[i-ps-first-1];}
        } else if(hasParenAfter(p,ps+1,ps+first)){
            //System.out.println("replacing inside");
            int[] pReplace=parenAfter(p,ps+1,ps+first);
            for(int i=ps+1;i<=ps+first;i++){result[i-ps]=pReplace[i-ps-1];}
        } else if(first<=0){System.out.println("error!");return null;} else{
            //System.out.println("replacing first");
            first--;
            for(int i=0;i<=first;i++){result[i]=first-i;}
            for(int i=first+1;i<pe-ps+1;i++){result[i]=pe-ps-i;}
        }
        //s="2. result=[";for(int i=0;i<pe-ps+1;i++){s+=result[i];}System.out.println(s+"]");
        return result;
    }
    /** Same method without bounds */
    public static int[] parenAfter(int[] p){return parenAfter(p,0,p.length-1);}
    
    /** Determines whether there is a next TL element */
    public boolean hasNext(){return isBasisElement()&&hasParenAfter(toParen());}
    
    /** Determines the next TL element, if it exists */
    public TemperleyLiebTerm next(){if(!hasNext()){return null;}return new TemperleyLiebTerm(parenAfter(toParen()));}
    
    /** Required by Iterator interface */
    public void remove(){}
    
    /** Compare to another TemperleyLiebTerm... there are 3 different cases here. */
    public int compareTo(Object o){
        TemperleyLiebTerm ot=(TemperleyLiebTerm)o;
        // If different numbers of ins/outs, sort by that
        if(this.in!=ot.in){return(this.in-ot.in);} else if(this.out!=ot.out){return(this.out-ot.out);}
        // If there are crossings, sort by the pairings
        if(hasCrossings()||ot.hasCrossings()){
            int n=(in+out)/2;
            int[][] pairs=new int[n][2];
            int i=0;
            Iterator<Edge> i1=g.iterator();
            Iterator<Edge> i2=ot.g.iterator();
            Edge e1,e2;
            int e1min,e1max,e2min,e2max;
            while(i1.hasNext()&&i2.hasNext()){
                // get next nontrivial loop
                do{e1=i1.next();}while(e1.isTrivial()&&i1.hasNext());
                do{e2=i2.next();}while(e2.isTrivial()&&i2.hasNext());
                // compare min/max values
                if(e1.getMin()!=e2.getMin()){return e1.getMin()-e2.getMin();}
                if(e1.getMax()!=e2.getMax()){return e1.getMax()-e2.getMax();}
            }
            if(i1.hasNext()){return 1;} else if(i2.hasNext()){return -1;} else{return 0;}
        }
        // If no crossings, sort by the parenString
        Long thisVal=Long.valueOf(toParenString());
        Long otVal=Long.valueOf(ot.toParenString());
        return thisVal.compareTo(otVal);
    }
}
