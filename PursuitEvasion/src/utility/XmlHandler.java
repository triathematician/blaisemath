/**
 * XmlHandler.java
 * Created on Oct 20, 2008
 */

package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import metrics.Goal;
import metrics.Valuation;
import simulation.Simulation;
import simulation.Team;
import tasking.TaskGenerator;

/**
 * This class is designed to handle input/output from XML files, as well as the
 * "marshalling" and "unmarshalling" of simulation settings.
 * 
 * @author Elisha Peterson
 */
public class XmlHandler {
        
    /** Copies simulation data into specified file. */
    public static void marshal(Simulation sim,File outfile) throws FileNotFoundException, JAXBException {
        marshal(sim,new FileOutputStream(outfile));
    }    
    /** Copies simulation data into specified output stream. */
    public static void marshal(Simulation sim,OutputStream os) throws JAXBException {
        JAXBContext.newInstance(Simulation.class).createMarshaller().marshal(sim,os);
    }
        
    /** Unmarshals XML dats into specified simulation variable. */
    public static Simulation unmarshal(File infile) throws JAXBException, FileNotFoundException{
        return unmarshal(new FileInputStream(infile));
    }
    /** Unmarshals XML dats into specified simulation variable. */
    public static Simulation unmarshal(InputStream is) throws JAXBException, FileNotFoundException{
        Simulation newSim = (Simulation) JAXBContext.newInstance(Simulation.class).createUnmarshaller().unmarshal(is);
        newSim.update();
        JAXBContext.newInstance(Simulation.class).createMarshaller().marshal(newSim,new FileOutputStream("testing-in.xml"));
        return newSim;
    }
}
