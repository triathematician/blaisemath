/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blaise.pr;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.blaise.pr.PrModel.PrEntry;

/**
 *
 * @author Elisha
 */
public class PrModelTest extends TestCase {

    static SimpleDateFormat DF = new SimpleDateFormat("yyyy-M-d");
    static PrModel createTestModel2() throws IOException, ParseException {
        File is = new File("PrModelTestData.csv");
        BufferedReader br = new BufferedReader(new FileReader(is));
        
        List<PrEntry> ens = Lists.newArrayList();
        while (br.ready()) {
            String line = br.readLine();
            int spc = line.indexOf(" ");
            Date d = DF.parse(line.substring(0, spc));
            String[] times = line.substring(spc+1).split(",");
            for (String t : times) {
                t = t.trim();
                System.out.println(t);
                Integer dist = Integer.valueOf(t.substring(0, t.indexOf(" ")));
                Integer min = 0, sec = null;
                if (t.contains(":")) {
                    min = Integer.valueOf(t.substring(t.indexOf(" ")+1, t.indexOf(":")));
                    sec = Integer.valueOf(t.substring(t.indexOf(":")+1));
                } else {
                    sec = Integer.valueOf(t.substring(t.indexOf(" ")+1));
                }
                ens.add(new PrEntry(d, Units.yards(dist), Units.minSec(min, sec)));
            }            
        }
        
        br.close();
        
        PrModel res = new PrModel();
        res.setEntries(ens);
        return res;
    }
    
    static PrModel createTestModel() {
        List<PrEntry> ens = Lists.newArrayList();
        ens.add(new PrEntry(new Date(2013, 1, 13), Units.yards(50), Units.seconds(37)));
        ens.add(new PrEntry(new Date(2013, 1, 13), Units.yards(100), Units.seconds(79)));
        ens.add(new PrEntry(new Date(2013, 1, 13), Units.yards(150), Units.minSec(2,4)));
        ens.add(new PrEntry(new Date(2013, 1, 13), Units.yards(200), Units.minSec(2,45)));
        ens.add(new PrEntry(new Date(2013, 1, 13), Units.yards(400), Units.minSec(5,46)));
        ens.add(new PrEntry(new Date(2013, 2, 8), Units.yards(100), Units.seconds(77)));
        ens.add(new PrEntry(new Date(2013, 3, 25), Units.yards(150), Units.minSec(1,54)));
        ens.add(new PrEntry(new Date(2013, 3, 25), Units.yards(200), Units.minSec(2,33)));
        ens.add(new PrEntry(new Date(2013, 3, 25), Units.yards(250), Units.minSec(3,14)));
        ens.add(new PrEntry(new Date(2013, 4, 15), Units.yards(400), Units.minSec(5,13)));
        ens.add(new PrEntry(new Date(2013, 2, 2), Units.yards(2400), Units.minSec(35,42)));
        ens.add(new PrEntry(new Date(2013, 4, 22), Units.yards(1000), Units.minSec(13,51)));
        
        PrModel res = new PrModel();
        res.setEntries(ens);
        return res;
    }
    
    public void testModel() {
        createTestModel();
        try {
            createTestModel2();
        } catch (IOException ex) {
            Logger.getLogger(PrModelTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(PrModelTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
