package scio.graph;

import java.util.Comparator;
import java.util.Map;

/**
 * <b>Edge.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>May 3, 2007, 1:00 PM</i><br><br>
 *
 * Describes an edge as an ordered pair of integers, with a certain weight... assumed here to be an integer.
 *   v0,v1 must be nonnegative integers, with the exception of the case where v0=v1=-1, which represents a
 *   "trivial" edge.
 */

public class Edge implements Comparable{
    int v0,v1;
    int weight;
    public Edge(){v0=0;v1=0;weight=1;}
    public Edge(int v0,int v1){if(v0<0||v1<0){v0=-1;v1=-1;}this.v0=v0;this.v1=v1;weight=1;}
    public Edge(int v0,int v1,int w){if(v0<0||v1<0){v0=-1;v1=-1;}this.v0=v0;this.v1=v1;this.weight=w;}
    public static Edge getTrivial(){return new Edge(-1,-1,1);}
    public boolean isTrivial(){return v0==-1&&v1==-1;}
    public boolean isLoop(){return v0==v1;}
    public boolean equals(Edge e){return (e.v0==v0)&&(e.v1==v1)&&(e.weight==weight);}
    public boolean equals(int ev0,int ev1){return (ev0==v0)&&(ev1==v1);}
    public int weightedEquals(int ev0,int ev1){
        if ((ev0==v0)&&(ev1==v1)) return weight;
        return 0;
    }
    public int compareTo(Object o){
        Edge e=(Edge)o;
        if (e.v0==v0) return v1-e.v1;
        return v0-e.v0;
    }
    public Edge clone(){return new Edge(v0,v1,weight);}
    public String toString(boolean multiEdge,boolean directed){
        String connector=directed?"->":"-";
        if (multiEdge&&weight!=1) return Integer.toString(weight)+"("+Integer.toString(v0)+connector+Integer.toString(v1)+")";
        return "("+Integer.toString(v0)+connector+Integer.toString(v1)+")";
    }
    public int getSource(){return v0;}
    public int getSink(){return v1;}
    public int getMax(){return (v0>=v1)?v0:v1;}
    public int getMin(){return (v0>=v1)?v1:v0;}
    public int getWeight(){return weight;}
    public boolean addWeight(int i){weight+=i;return weight>0;}
    public boolean startsAt(int i){return v0==i;}
    public boolean endsAt(int i){return v1==i;}
    public boolean contains(int i){return startsAt(i)||endsAt(i);}
    public static Comparator<Edge> getComparator(){
        return new Comparator<Edge>(){
            public int compare(Edge e1,Edge e2){
                if (e1.v0==e2.v0) return e2.v1-e1.v1;
                return e2.v1-e1.v1;
            }
        };
    }
    public void reverse(){int i=v0;v0=v1;v1=i;}
    public void relabel(Map<Integer,Integer> mapping){v0=mapping.get(v0);v1=mapping.get(v1);}
    /** Relabels: va is the old label, vb the new label. */
    public void relabel(int va,int vb){if(v0==va){v0=vb;}if(v1==va){v1=vb;}}
    public void addToLabel(int n){v0+=n;v1+=n;}
    public void addToLabelAbove(int n,int i){if(v0>i){v0+=n;}if(v1>i){v1+=n;}}
}
