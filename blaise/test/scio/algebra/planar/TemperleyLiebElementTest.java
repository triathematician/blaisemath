/*
 * TemperleyLiebAlgebraTest.java
 * JUnit based test
 *
 * Created on June 6, 2007, 2:12 PM
 */

package scio.algebra.planar;

import scio.algebra.planar.TemperleyLiebElement;
import junit.framework.*;
import scio.algebra.permutation.Permutation;
import scio.algebra.polynomial.MPolynomial;

/**
 *
 * @author ae3263
 */
public class TemperleyLiebElementTest extends TestCase {
    
    public static Test suite(){return new TestSuite(TemperleyLiebElementTest.class);}
    
    public TemperleyLiebElementTest(String testName) {
        super(testName);
    }
    
    TemperleyLiebElement a0,a1,a2,a3,a4;
    
    protected void setUp() throws Exception {
        int[] s1={1,0};TemperleyLiebTerm e1=new TemperleyLiebTerm(s1);
        int[] s2={0,0};TemperleyLiebTerm e2=new TemperleyLiebTerm(s2);
        int[] s3={2,0,0};TemperleyLiebTerm e3=new TemperleyLiebTerm(s3);
        int[] s4={1,0,0};TemperleyLiebTerm e4=new TemperleyLiebTerm(s4);
        a0=new TemperleyLiebElement(2);a0.appendTerm(e1);a0.appendTerm(e2);
        a1=new TemperleyLiebElement(2);a1.appendTerm(e1);a1.appendTerm(-1,e2);
        a2=new TemperleyLiebElement(3);a2.appendTerm(.5f,e3);a2.appendTerm(.2f,e4);
        a3=new TemperleyLiebElement(3);
        TemperleyLiebTerm e=new TemperleyLiebTerm(3);int i=1;
        do{a3.appendTerm(i,e);e=e.next();i++;}while(e.hasNext());a3.appendTerm(i,e);
        TemperleyLiebTerm e5=new TemperleyLiebTerm(3);
        TemperleyLiebTerm e6=new TemperleyLiebTerm(3);e6.clear();e6.addEdge(1,6);e6.addEdge(2,4);e6.addEdge(3,5);
        TemperleyLiebTerm e7=new TemperleyLiebTerm(3);e7.clear();e7.addEdge(1,4);e7.addEdge(2,6);e7.addEdge(3,5);
        a4=new TemperleyLiebElement(3);a4.appendTerm(e5);a4.appendTerm(-.5f,e6);a4.appendTerm(2,e7);
    }
    
    protected void tearDown() throws Exception {
    }
    
    /**
     * Tests hasCrossings
     */
    public void testHasCrossings() {
        System.out.println("hasCrossings");
        assertEquals("0",false,a0.hasCrossings());
        assertEquals("1",false,a1.hasCrossings());
        assertEquals("2",false,a2.hasCrossings());
        assertEquals("3",false,a3.hasCrossings());
        assertEquals("4",true,a4.hasCrossings());
    }
    
    /**
     * Tests appendTerm
     */
    public void testAppendTerm() {
        System.out.println("appendTerm");
        assertEquals("0","+(10)+(00)",a0.toString());
        assertEquals("1","+(10)-(00)",a1.toString());
        assertEquals("2","+0.5(200)+0.2(100)",a2.toString());
        assertEquals("3","+(210)+2(200)+3(100)+4(010)+5(000)",a3.toString());
        assertEquals("4","+{(1-6)(2-5)(3-4)}-0.5{(1-6)(2-4)(3-5)}+2{(1-4)(2-6)(3-5)}",a4.toString());
    }
    
    /**
     * Tests actLeft
     */
    public void testActLeft() {
        System.out.println("actLeft");
        System.out.println("test1");
        assertEquals("01","+(10)-2(00)",TemperleyLiebElement.actLeft(a0,a1).toString());
        System.out.println("test2");
        assertEquals("10","+(10)-2(00)",a0.actRight(a1).toString());
        System.out.println("test3");
        TemperleyLiebElement a23=TemperleyLiebElement.actLeft(a2,a3);
        // TODO: fix the rounding error!
        assertEquals("23","+(200)+0.39999998(100)+1.5(010)+0.6(000)",TemperleyLiebElement.actLeft(a2,a3).toString());
        TemperleyLiebElement a34=TemperleyLiebElement.actLeft(a3,a4);
        assertEquals("34c","+{(1-6)(2-5)(3-4)}-0.5{(1-6)(2-4)(3-5)}+11{(1-6)(2-3)(4-5)}+2{(1-4)(2-6)(3-5)}+14.5{(1-4)(2-3)(5-6)}-6{(1-3)(2-6)(4-5)}-8.5{(1-3)(2-4)(5-6)}+4{(1-2)(3-6)(4-5)}+5{(1-2)(3-4)(5-6)}",a34.toString());
        a34.removeCrossings();
        assertEquals("34u","+2.5(210)+3.5(200)+6(100)-4(010)-5.5(000)",a34.toString());
    }
    
    /** Test of getArrow method */
    public void testGetArrow(){
        System.out.println("getArrow");
        assertEquals("+2(10)-(00)",TemperleyLiebElement.getArrow(2).toString());
        assertEquals("+4(3210)-(2100)-2(2000)-3(0100)",TemperleyLiebElement.getArrow(4).toString());
        assertEquals("+3(210)-(100)-2(000)",TemperleyLiebElement.getArrow(3).toString());
    }
    
    /** Test of getSymmetrizer method, of class scio.planar.TemperleyLiebAlgebra. */
    public void testGetSymmetrizerB() {
        System.out.println("getSymmetrizerB");
        assertEquals("1u","+(0)",TemperleyLiebElement.getSymmetrizerB(1).toString());
        assertEquals("2u","+2(10)-(00)",TemperleyLiebElement.getSymmetrizerB(2).toString());
        assertEquals("3u","+6(210)-4(200)-2(100)-2(010)-4(000)",TemperleyLiebElement.getSymmetrizerB(3).toString());
        assertEquals("4u","+24(3210)-18(3200)-12(3100)-12(3010)-24(3000)-6(2100)-12(2000)+4(1010)+8(1000)-6(0210)-12(0200)-18(0100)+8(0010)+16(0000)",TemperleyLiebElement.getSymmetrizerB(4).toString());
    }
    
    /** Test of getSymmetrizer method, of class scio.planar.TemperleyLiebAlgebra. */
    public void testGetSymmetrizer() {
        System.out.println("getSymmetrizer");
        TemperleyLiebElement i=TemperleyLiebElement.getSymmetrizer(1);
        assertEquals("1c","+(0)",i.toString());
        i.removeCrossings();
        assertEquals("1u","+(0)",i.toString());
        
        i=TemperleyLiebElement.getSymmetrizer(2);
        assertEquals("2c","+{(1-4)(2-3)}+{(1-3)(2-4)}",i.toString());
        i.removeCrossings();
        assertEquals("2u","+2(10)-(00)",i.toString());
        
        i=TemperleyLiebElement.getSymmetrizer(3);
        i.removeCrossings();
        assertEquals("3u","+6(210)-4(200)-2(100)-2(010)-4(000)",i.toString());
        
        i=TemperleyLiebElement.getSymmetrizer(4);
        i.removeCrossings();
        assertEquals("4u","+24(3210)-18(3200)-12(3100)-12(3010)-24(3000)-6(2100)-12(2000)+4(1010)+8(1000)-6(0210)-12(0200)-18(0100)+8(0010)+16(0000)",i.toString());
    }
    
    /** Test of getTripleSymmetrizer method */
    public void testGetTripleSymmetrizer(){
        System.out.println("getTripleSymmetrizer");
        assertEquals("112","+2(10)-(00)",TemperleyLiebElement.getTripleSymmetrizer(1,1,2).removeCrossings().toString());
        assertEquals("220","+4(1010)+2(1000)",TemperleyLiebElement.getTripleSymmetrizer(2,2,0).removeCrossings().toString());
        assertEquals("222","+4(3010)+8(3000)+4(2000)-4(1010)-4(1000)",TemperleyLiebElement.getTripleSymmetrizer(2,2,2).removeCrossings().toString());
    }
    
    /**
     * Test of removeCrossings method, of class scio.planar.TemperleyLiebAlgebra.
     */
    public void testRemoveCrossings() {
        System.out.println("removeCrossings");
        a0.removeCrossings();
        assertEquals("a0","+(10)+(00)",a0.toString());
        TemperleyLiebElement t=new TemperleyLiebElement(2);
        TemperleyLiebTerm tt=new TemperleyLiebTerm(2);tt.clear();tt.addEdge(1,3);tt.addEdge(2,4);
        t.appendTerm(2,tt);
        t.removeCrossings();
        assertEquals("t","+2(10)-2(00)",t.toString());
        a4.removeCrossings();
        assertEquals("a4","+2.5(210)-1.5(200)-2(010)-2(000)",a4.toString());
    }
    
    /**
     * Test of getAntiSymmetrizer method, of class scio.planar.TemperleyLiebAlgebra.
     */
    public void testGetAntiSymmetrizer() {
        System.out.println("getAntiSymmetrizer: NOTE--> WILL NOT WORK YET");
        //        TemperleyLiebElement i=TemperleyLiebElement.getSymmetrizer(1);
        //        assertEquals("1c","+(0)",i.toString());
        //        i.removeCrossings();
        //        assertEquals("1u","+(0)",i.toString());
        //
        //        i=TemperleyLiebElement.getSymmetrizer(2);
        //        assertEquals("2c","+0.5{(1-4)(2-3)}-0.5{(1-3)(2-4)}",i.toString());
        //        i.removeCrossings();
        //        assertEquals("2u","+0.5(00)",i.toString());
        //
        //        i=TemperleyLiebElement.getSymmetrizer(4);
        //        assertEquals("4c","??",i.toString());
        //        i.removeCrossings();
        //        assertEquals("4u","??",i.toString());
    }
    
    /**
     * Test of factorial method, of class scio.planar.TemperleyLiebAlgebra.
     */
    public void testFactorial() {
        System.out.println("factorial");
        assertEquals(720,TemperleyLiebElement.factorial(6));
    }
    
    /** Test of toPolynomial method */
    public void testToPolynomial(){
        System.out.println("toPolynomial & getCentral");
        assertEquals("404","+576x^4-1728x^2+576",TemperleyLiebElement.getCentral(4,0,4).toString());
        assertEquals("112","+2x y-z",TemperleyLiebElement.getCentral(1,1,2).toString());
        assertEquals("213","+12x^2 y-8x z-8y",TemperleyLiebElement.getCentral(2,1,3).toString());
        assertEquals("220","+4z^2-4",TemperleyLiebElement.getCentral(2,2,0).toString());
        assertEquals("222","-4x^2+8x y z-4y^2-4z^2+8",TemperleyLiebElement.getCentral(2,2,2).toString());
        assertEquals("224","+96x^2 y^2-48x^2-96x y z-48y^2+16z^2+32",TemperleyLiebElement.getCentral(2,2,4).toString());
        assertEquals("314","+144x^3 y-108x^2 z-216x y+72z",TemperleyLiebElement.getCentral(3,1,4).toString());
    }
    
    /** Generic test method */
    public void testMyTheory(){
        System.out.println("Testing...");
        TemperleyLiebElement x1=(new TemperleyLiebTerm().setToPermutation(new Permutation("(1 2 4 3)"))).toElement();
        x1.actLeft(TemperleyLiebElement.concatenate(TemperleyLiebElement.getAntiSymmetrizer(3),TemperleyLiebElement.id1));
        TemperleyLiebElement x2=TemperleyLiebElement.concatenate(TemperleyLiebElement.getAntiSymmetrizer(3),TemperleyLiebElement.id1);
        x2.actLeft((new TemperleyLiebTerm().setToPermutation(new Permutation("(1 4 2 3)"))).toElement());
        TemperleyLiebElement x3=(new TemperleyLiebTerm().setToPermutation(new Permutation("(1 3 4 2)"))).toElement();
        x3.actLeft(TemperleyLiebElement.concatenate(TemperleyLiebElement.getAntiSymmetrizer(3),TemperleyLiebElement.id1));
        x3.actLeft((new TemperleyLiebTerm().setToPermutation(new Permutation("(1 2 4 3)"))).toElement());
        int[] split={1,1,1,1};
        System.out.println("x1: "+x1.toPolynomial(split));
        System.out.println("x2: "+x2.toPolynomial(split));
        System.out.println("x3: "+x2.toPolynomial(split));
    }
    
    
    public static long factorial(int n){return(n<=1)?1:n*factorial(n-1);}
    /** Test of getCentral method */
    public void testGetCentral(){
        System.out.println("getCentral");
        System.out.println("Rank 1 Central Functions:");
        for(int n=0;n<=7;n++){
            System.out.println("cftest["+n+"] := ("
                    +TemperleyLiebElement.getCentral(n).toString()
                    +")/"+factorial(n));
        }
        
        System.out.println("Rank 2 Central Functions:");
        for(int rank=0;rank<=6;rank++){
            for(int c=rank;c>=0;c--){
                for(int a=c;a>=0;a--){
                    int b=2*rank-a-c;
                    if(b>a||b<0){continue;}
                    System.out.println("cftest["+a+","+b+","+c+"] := ("
                            +TemperleyLiebElement.getCentral(a,b,c).toString()
                            +")/"+(factorial(a)*factorial(b)*factorial(c)));
                }
            }
        }
        
        System.out.println("Rank 3 Central Functions:");
        System.out.println("All 2s: "+TemperleyLiebElement.getCentral(2,2,2,2,2,0).toString());
        System.out.println("All 2s: "+TemperleyLiebElement.getCentral(2,2,2,0,2,2).toString());
        System.out.println("All 2s: "+TemperleyLiebElement.getCentral(2,2,2,2,2,2).toString());
        System.out.println("All 2s: "+TemperleyLiebElement.getCentral(2,2,2,2,4,2).toString());
        for(int a=0;a<=3;a++){
            for(int b=0;b<=3;b++){
                for(int c=0;c<=3;c++){
                    for(int d=Math.abs(a-b);d<=a+b;d+=2){
                        if(d>5){continue;}
                        for(int f=Math.abs(a-b);f<=a+b;f+=2){
                            if(f>5){continue;}
                            for(int e=Math.abs(c-d);e<=c+d;e+=2){
                                if(e>5){continue;}
                                if(d==0&&f==0&&e!=0){continue;}
                                if(!TemperleyLiebElement.admissible(c,f,e)){continue;}
                                MPolynomial cf=TemperleyLiebElement.getCentral(a,b,c,d,e,f);
                                if(cf==null){continue;}
                                System.out.println("cftest["+a+","+b+","+c+","+d+","+e+","+f+"] := ("
                                        +TemperleyLiebElement.getCentral(a,b,c,d,e,f).toString()
                                        +")/"+(factorial(a)*factorial(b)*factorial(c)*factorial(d)*factorial(e)*factorial(f)));
                            }
                        }
                    }
                }
            }
        }
    }
}
