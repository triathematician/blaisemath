/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scribo.parser.semantic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ae3263
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Method m = null;
        try {
            m = NewMain.class.getMethod("testMethod", double[].class);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            m.invoke(null, new double[]{3, 4, 5, 6});
            m.invoke(null, new double[]{3,4});
            m.invoke(null, new double[]{3});
            m.invoke(null, new double[]{});
            m.invoke(null, new Object[]{new double[]{3,4}});
            m.invoke(null, (Object) null);

        } catch (IllegalAccessException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static double testMethod(double... inputs) {
        System.out.println("invoked with arguments " + Arrays.toString(inputs));
        return 0.0;
    }

}
