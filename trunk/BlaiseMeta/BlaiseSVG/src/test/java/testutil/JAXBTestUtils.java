/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testutil;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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


import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import static junit.framework.Assert.assertEquals;

/**
 * @author petereb1
 */
public class JAXBTestUtils {

    /** 
     * Tests a marshal/unmarshal cycle to ensure object compatibility 
     * @param testEquals whether to use a .equals test on the recycled object
     * @return recycled object
     */
    public static Object testRecycleObject(Object o, @Nullable JAXBContext jc, 
            boolean testEquals, boolean testStringEquals, PrintStream w) throws JAXBException {
        if (jc == null) {
            jc = JAXBContext.newInstance(o.getClass());
        }
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter sw = new StringWriter();
        marshaller.marshal(o, sw);
        if (w != null) {
            marshaller.marshal(o, w);
        }

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Object m2 = unmarshaller.unmarshal(new StringReader(sw.toString()));
        if (testEquals) {
            assertEquals(m2, o);
        }
        if (testStringEquals) {
            assertEquals(""+o, ""+m2);
        }
        
        StringWriter sw2 = new StringWriter();
        marshaller.marshal(m2, sw2);
        assertEquals(sw.toString(), sw2.toString());
        
        return m2;
    }
    
}
