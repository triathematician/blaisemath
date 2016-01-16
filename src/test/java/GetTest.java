/*
 * #%L
 * BlaiseGraphTheory (v3)
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
 * Copyright 2016 elisha.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 * @author elisha
 */
public class GetTest {
    
    public static void main(String[] args) {
        for (int n : new int[]{100, 1000, 10000, 100000}) {
            System.out.println("\n TEST WITH "+n+" ELEMENTS");
            for (int s : new int[]{100000, 1000000, 10000000, 100000000}) {
                System.out.println("-- "+s+" samples --");
                test(n, s);
            }
        }
    }
    
    public static void test(int sz, int samples) {
        Map<Integer,Integer> map = Maps.newHashMap();
        List<Integer> list = Lists.newArrayList();
        int[] arr = new int[sz];
        for (int i = 0; i < sz; i++) {
            map.put(i, i);
            list.add(i);
            arr[i] = i;
        }
        
        Random r = new Random();
        
        long t0 = System.currentTimeMillis();
        
        long sum = 0;
        for (int i = 0; i < samples; i++) {
            sum += map.get(r.nextInt(sz));
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Map gets: "+(t1-t0)+"ms");
        
        sum = 0;
        for (int i = 0; i < samples; i++) {
            sum += list.get(r.nextInt(sz));
        }
        long t2 = System.currentTimeMillis();
        System.out.println("List gets: "+(t2-t1)+"ms");
        
        sum = 0;
        for (int i = 0; i < samples; i++) {
            sum += arr[r.nextInt(sz)];
        }
        long t3 = System.currentTimeMillis();
        System.out.println("Arr gets: "+(t3-t2)+"ms");
    }
    
}
