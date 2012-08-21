
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.Test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TestSpeed.java
 * Created on Jul 18, 2012
 */
/**
 *
 * @author petereb1
 */
public class TestSpeed {


    @Test
    public void test2() {
        Map<Integer,String> map = map();
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++)
            for (Entry<Integer,String> en : map.entrySet()) {
                Integer ii = en.getKey();
                String al = en.getValue();
            }
        System.out.println((System.currentTimeMillis()-t0)+"ms");
    }
    @Test
    public void test1() {
        Map<Integer,String> map = map();
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++)
            for (Integer ii : map.keySet()) {
                String val = map.get(ii);
            }
        System.out.println((System.currentTimeMillis()-t0)+"ms");
    }

    private Map<Integer, String> map() {
        Map<Integer,String> res = new HashMap<Integer,String>();
        for (int i = 0; i < 10000; i++) {
            res.put(i, i*i+"/"+i);
        }
        return res;
    }

}
