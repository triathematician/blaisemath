package Function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>Parser.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 3, 2007, 2:17 PM</i><br><br>
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
public class Parser {
    
    // operators which act on the left
    static String operatorLeft="-D";
    
    // operators which act on the right
    static String operatorRight="!";
    
    // operators which act on both sides
    static String operator="+-*/^";
    
    // functions... expects group after this
    static String[] function={"sin","cos","sec","csc","tan","cot"};
    
    // constants
    static String[] constant={"e","pi"};
    
    // left group symbols
    static String groupLeft="([";
    
    // right group symbols
    static String groupRight=")]";
    
    /** Constructor: creates a new instance of Parser */
    public Parser() {
    }
    
    // Regular expressions for searching functions...
    final static String XSPACE="[\\x00-\\x20]*";
    final static String SOME_SPACE="[\\x00-\\x20]+";
    final static String NOSPACE="[^\\x00-\\x20]";
    final static String DIGIT="\\p{Digit}";
    final static String DIGITS="(\\p{Digit}+)";
    final static String DOUBLE_BASIC=XSPACE+"((("+DIGITS+"(\\.)?("+DIGITS+"?))|"+
            "(\\.("+DIGITS+"))))"+XSPACE;
    final static String SIGNED_DOUBLE=XSPACE+"[+-]?"+DOUBLE_BASIC;
    final static String OPERATOR="[+*/^-]";
    final static String NON_OPERATOR="[^+*/^-]";
    final static String NON_NUMBER="[^0123456789\\.]";
    final static String NON_OPERATOR_NON_NUMBER="[^0123456789\\.+*/^-]";
    final static String PARAMETER_X=XSPACE+"[x]"+XSPACE;
    final static String CONSTANT_PI=XSPACE+"[pi]"+XSPACE;
    final static String CONSTANT_E=XSPACE+"[e]"+XSPACE;
    
    // Patterns for searching
    static Pattern numPattern=Pattern.compile(DOUBLE_BASIC);
    /** Finds extra space on the beginning and end */
    static Pattern outsideSpacePattern=Pattern.compile(XSPACE+"("+NOSPACE+".*"+NOSPACE+")"+XSPACE);
    /** Finds extra space on the inside which may be interpreted as *. */
    static Pattern insideSpacePattern=Pattern.compile("("+NON_OPERATOR+")"+SOME_SPACE+"("+NON_OPERATOR+")");
    /** Finds places with an implicit *, for example 3x and 3(x) -> 3*(x).
     * Assumes a double or constant value followed by a non-operator. */
    static Pattern implicitStarPattern=Pattern.compile("("+DIGIT+")"+"(["+NON_OPERATOR_NON_NUMBER+"])");
    /** Finds an inner set of parentheses */
    static Pattern parentheticalPattern=Pattern.compile("[(][^(]*[)]");
    static Pattern[] opPattern=
    {Pattern.compile("\\+"),
     /** Finds a minus sign which should be treated as an operator... anything
      * that follows * or / or + or - should be treated as negation. */
     Pattern.compile("[^+*/^]\\-"),
     /** Multiplication may be represented by either a space (which is not next
      * to an operator) or by *. */
     Pattern.compile("\\*"),
     Pattern.compile("\\/"),
     Pattern.compile("\\^")};
    
    // basic parser handles only +-*/^ values
    static double basicParse(String s,double x){
        
        // TODO throw exceptions...
        // TODO implement operators...
        
        println("Input String:"+s);
        
        // eliminate extra space on the beginning and end
        Matcher mp=outsideSpacePattern.matcher(s);
        if(mp.matches()){s=mp.group(1);}
        
        // inserts implicit * for non-spaces
        mp=implicitStarPattern.matcher(s);
        if(mp.find()){s=mp.replaceAll(mp.group(1)+"*"+mp.group(2));}
        
        // inserts implicit * for spaces
        mp=insideSpacePattern.matcher(s);
        if(mp.find()){s=mp.replaceAll(mp.group(1)+"*"+mp.group(2));}
        
        // returns a double value if possible
        if(s.matches(SIGNED_DOUBLE)){return Double.valueOf(s);}
        if(s.equals("x")){return x;}
        if(s.equals("pi")){return Math.PI;}
        if(s.equals("e")){return Math.E;}
        
        
        // find extra space that can be interpreted as multiplication...
        // stuff like 3x or 3sin(x) follows in this category... pretty much a
        // number followed by any letter... or something like x e^x sin x. I don't
        // think I want to allow things like xe^x to be interpreted as x*e^x.
        
        
        boolean mismatched=false;
        
        // find inner sets of parentheses & replaces them
        mp=parentheticalPattern.matcher(s);
        while(mp.find()){
            s=mp.replaceFirst(Double.toString(basicParse(s.substring(mp.start()+1,mp.end()-1),x)));
            println("Input String:"+s);
            mp=parentheticalPattern.matcher(s);
        }
        
        // appends a 0 if starts with -
        if(s.charAt(0)=='-'){s="0"+s;}
        
        // checks for fancy operations
        String[] splitS;
        Matcher opm;
        // addition
        if(opPattern[0].matcher(s).find()){
            splitS=opPattern[0].split(s);
            double temp=0;
            for(String s2:splitS){temp+=basicParse(s2,x);}
            return temp;
        }
        // subtraction
        opm=opPattern[1].matcher(s);
        if(opm.find()){
            double temp=basicParse(s.substring(0,opm.start()+1),x);
            splitS=opPattern[1].split(s.substring(opm.end()));
            for(String s2:splitS){temp-=basicParse(s2,x);}
            return temp;
        }
        // multiplication
        if(opPattern[2].matcher(s).find()){
            splitS=opPattern[2].split(s);
            double temp=1;
            for(String s2:splitS){temp*=basicParse(s2,x);}
            return temp;
        }
        opm=opPattern[3].matcher(s);
        if(opm.find()){
            double temp=basicParse(s.substring(0,opm.start()),x);
            splitS=opPattern[3].split(s.substring(opm.start()+1));
            for(String s2:splitS){temp/=basicParse(s2,x);}
            return temp;
        }
        opm=opPattern[4].matcher(s);
        if(opm.find()){
            return Math.pow(basicParse(s.substring(0,opm.start()),x),
                    basicParse(s.substring(opm.start()+1),x));
        }
        
        println("Sorry... don't know what to do with this!");
        return Double.NaN;
    }
    
    // searches for the next expression after the operator... assumes spaces already removed
    //   the precedent is in group 1; the operator in group 2.
    static Pattern newOP=Pattern.compile("("+OPERATOR+")([\\-]?["+NON_OPERATOR+"]*)");
    
    // alternate parser, proceeds by constructing an evaluation tree
    FunctionTree basicParse2(String s){
        // TODO put all this cleanup stuff in one method
        // eliminate extra space on the beginning and end
        Matcher mp=outsideSpacePattern.matcher(s);
        if(mp.matches()){s=mp.group(1);}
        // inserts implicit * for non-spaces
        mp=implicitStarPattern.matcher(s);
        if(mp.find()){s=mp.replaceAll(mp.group(1)+"*"+mp.group(2));}
        // inserts implicit * for spaces
        mp=insideSpacePattern.matcher(s);
        if(mp.find()){s=mp.replaceAll(mp.group(1)+"*"+mp.group(2));}
        // remove remaining spaces
        s.replaceAll(SOME_SPACE,"");
        // find inner sets of parentheses & replaces them
        mp=parentheticalPattern.matcher(s);
        while(mp.find()){
            return resolveTree(s.substring(0,mp.start()),
                    basicParse2(s.substring(mp.start()+1,mp.end()-1)),
                    s.substring(mp.end()));
        }
        // at this point there should be no more parentheses or spaces
        
        // ensure operator + or - is at the beginning of the group
        if(!Pattern.compile(OPERATOR).matcher(s).lookingAt()){s="+"+s;}
        // now parse the piece after each operator
        String opString="";
        LinkedList<String> xs=new LinkedList<String>();
        Matcher m=newOP.matcher(s);
        while(m.find()){opString+=m.group(1);xs.add(m.group(2));}
        // resolve the expressions using the operator tree
        return new FunctionTree();
    }
    
    // orders the operations into a tree
    String[] OPP={"+","-","*","/","^"};
    private OperatorTree order(String opString,int add){
        if(opString.length()==1){
            OperatorTree tree=new OperatorTree(opString);
            tree.setLeft(new OperatorTree(Integer.toString(add)));
            tree.setRight(new OperatorTree(Integer.toString(add+1)));
            return tree;}
        OperatorTree tree=new OperatorTree();
        for(int i=0;i<=4;i++){
            int j=opString.indexOf(OPP[i]);
            if(j>=0){
                tree.setLabel(OPP[i]);
                if(j>0){
                    tree.setLeft(order(opString.substring(0,j),add));}
                else{
                    tree.setLeft(new OperatorTree(Integer.toString(j+add)));}
                if(j<opString.length()-1){
                    tree.setRight(order(opString.substring(j+1),add+j+1));}
                else{
                    tree.setRight(new OperatorTree(Integer.toString(j+1+add)));}
                return tree;
            }
        }
        return tree;
    }
    
    class OperatorTree{
        String label;
        OperatorTree left,right;
        OperatorTree(){label="";}
        OperatorTree(String s){label=s;}
        void setLabel(String s){label=s;}
        void setLeft(OperatorTree left){this.left=left;}
        void setRight(OperatorTree right){this.right=right;}
        void printMe(String prefix){
            println(prefix+label);
            if(left!=null){left.printMe(prefix+label+"l");}
            if(right!=null){right.printMe(prefix+label+"r");}
        }
    }
    
    // appends the elements listed in "xs"
    private FunctionTree resolveWith(FunctionTree ops,LinkedList<String> xs){
        return ops;
    }
    
    //
    
    private FunctionTree resolveTree(String s1,FunctionTree m,String s2){
        FunctionTree tree=new FunctionTree(s1);
        return tree;
    }
    
    class FunctionTree{
        String label="";
        Vector<FunctionTree> children=new Vector<FunctionTree>();
        FunctionTree(){children=new Vector<FunctionTree>();}
        FunctionTree(String s){label=s;children=new Vector<FunctionTree>();}
        FunctionTree(double d){label=Double.toString(d);children=new Vector<FunctionTree>();}
        void setLabel(String s){label=s;}
        boolean isLeaf(){return children.isEmpty();}
        int getNumChildren(){return children.size();}
        boolean addChild(String s){return addChild(new FunctionTree(s));}
        boolean addChild(FunctionTree ft){children.add(ft);return true;}
        void printMe(String prefix){
            println(prefix+label);
            for(FunctionTree child:children){child.printMe(prefix+"_");}
        }
    }
    
    // Create a single shared BufferedReader for keyboard input
    private static BufferedReader stdin=new BufferedReader(new InputStreamReader(System.in));
    
    public static void main(String[] args) throws IOException {
        //println("Welcome to the string parser!");
        //println("Enter your function of x:");
        String s;
        println("Enter mode (0=regex, 1=function, 2=order of ops):");
        int i=Integer.valueOf(stdin.readLine());
        while(i==2){
            println("Enter operations: ");
            s=stdin.readLine();
            Parser p=new Parser();
            p.order(s,0).printMe("");
        }
        while(i==1){
            println("Enter function: ");
            s=stdin.readLine();
            println("Value: "+basicParse(s,2));
        }
        while(i==0){
            println("Enter regex: ");
            Pattern pattern =Pattern.compile(stdin.readLine());
            
            println("Enter search string: ");
            Matcher matcher = pattern.matcher(stdin.readLine());
            
            boolean found = false;
            while (matcher.find()) {
                println("I found the text "+matcher.group()+" starting at " +
                        "index "+matcher.start()+" and ending at index "+ matcher.end()+
                        "  group 1 is"+matcher.group(1)+".");
                found = true;
            }
            if(!found){
                println("No match found.%n");
            }
        }
        
        // Checks pattern matching stuff
        /*do{
            println("Pattern:");String input1=stdin.readLine();
            println("Expression:");String input2=stdin.readLine();
            println("Matches? "+Pattern.matches(input1,input2));
        }while(true);*/
        
        // Read a line of text from the user.
        //String input = stdin.readLine();
        //println("Your function is: "+input);
        
        //println("Enter the value to evaluate at:");
        //Double x = Double.valueOf(stdin.readLine());
        
        //println("Your function at x="+x+" is f(x)="+evaluateFunction(input,x));
    }
    
    private static void println(String string) {
        System.out.println(string);
    }
}
