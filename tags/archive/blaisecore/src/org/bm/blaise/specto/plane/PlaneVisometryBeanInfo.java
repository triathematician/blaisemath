/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.specto.plane;

import java.beans.*;

/**
 *
 * @author ae3263
 */
public class PlaneVisometryBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( org.bm.blaise.specto.plane.PlaneVisometry.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_aspectRatio = 0;
    private static final int PROPERTY_desiredRange = 1;
    private static final int PROPERTY_maxPointVisible = 2;
    private static final int PROPERTY_minPointVisible = 3;
    private static final int PROPERTY_scaleX = 4;
    private static final int PROPERTY_scaleY = 5;
    private static final int PROPERTY_visibleRange = 6;
    private static final int PROPERTY_windowBounds = 7;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[8];
    
        try {
            properties[PROPERTY_aspectRatio] = new PropertyDescriptor ( "aspectRatio", org.bm.blaise.specto.plane.PlaneVisometry.class, "getAspectRatio", "setAspectRatio" ); // NOI18N
            properties[PROPERTY_aspectRatio].setPreferred ( true );
            properties[PROPERTY_desiredRange] = new PropertyDescriptor ( "desiredRange", org.bm.blaise.specto.plane.PlaneVisometry.class, "getDesiredRange", "setDesiredRange" ); // NOI18N
            properties[PROPERTY_desiredRange].setPreferred ( true );
            properties[PROPERTY_maxPointVisible] = new PropertyDescriptor ( "maxPointVisible", org.bm.blaise.specto.plane.PlaneVisometry.class, "getMaxPointVisible", null ); // NOI18N
            properties[PROPERTY_minPointVisible] = new PropertyDescriptor ( "minPointVisible", org.bm.blaise.specto.plane.PlaneVisometry.class, "getMinPointVisible", null ); // NOI18N
            properties[PROPERTY_scaleX] = new PropertyDescriptor ( "scaleX", org.bm.blaise.specto.plane.PlaneVisometry.class, "getScaleX", null ); // NOI18N
            properties[PROPERTY_scaleY] = new PropertyDescriptor ( "scaleY", org.bm.blaise.specto.plane.PlaneVisometry.class, "getScaleY", null ); // NOI18N
            properties[PROPERTY_visibleRange] = new PropertyDescriptor ( "visibleRange", org.bm.blaise.specto.plane.PlaneVisometry.class, "getVisibleRange", null ); // NOI18N
            properties[PROPERTY_windowBounds] = new PropertyDescriptor ( "windowBounds", org.bm.blaise.specto.plane.PlaneVisometry.class, "getWindowBounds", "setWindowBounds" ); // NOI18N
            properties[PROPERTY_windowBounds].setHidden ( true );
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Properties

    // Here you can add code for customizing the properties array.

        return properties;     }//GEN-LAST:Properties

    // EventSet identifiers//GEN-FIRST:Events
    private static final int EVENT_changeListener = 0;

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[1];
    
        try {
            eventSets[EVENT_changeListener] = new EventSetDescriptor ( org.bm.blaise.specto.plane.PlaneVisometry.class, "changeListener", javax.swing.event.ChangeListener.class, new String[] {"stateChanged"}, "addChangeListener", "removeChangeListener" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Events

    // Here you can add code for customizing the event sets array.

        return eventSets;     }//GEN-LAST:Events

    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_computeTransformation0 = 0;
    private static final int METHOD_getCoordinateOf1 = 1;
    private static final int METHOD_getWindowPointOf2 = 2;
    private static final int METHOD_getWindowPointOfInfiniteAngle3 = 3;
    private static final int METHOD_randomValue4 = 4;
    private static final int METHOD_setDesiredRange5 = 5;
    private static final int METHOD_stateChanged6 = 6;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[7];
    
        try {
            methods[METHOD_computeTransformation0] = new MethodDescriptor(org.bm.blaise.specto.plane.PlaneVisometry.class.getMethod("computeTransformation", new Class[] {})); // NOI18N
            methods[METHOD_computeTransformation0].setDisplayName ( "" );
            methods[METHOD_getCoordinateOf1] = new MethodDescriptor(org.bm.blaise.specto.plane.PlaneVisometry.class.getMethod("getCoordinateOf", new Class[] {java.awt.geom.Point2D.class})); // NOI18N
            methods[METHOD_getCoordinateOf1].setDisplayName ( "" );
            methods[METHOD_getWindowPointOf2] = new MethodDescriptor(org.bm.blaise.specto.plane.PlaneVisometry.class.getMethod("getWindowPointOf", new Class[] {java.awt.geom.Point2D.Double.class})); // NOI18N
            methods[METHOD_getWindowPointOf2].setDisplayName ( "" );
            methods[METHOD_getWindowPointOfInfiniteAngle3] = new MethodDescriptor(org.bm.blaise.specto.plane.PlaneVisometry.class.getMethod("getWindowPointOfInfiniteAngle", new Class[] {double.class})); // NOI18N
            methods[METHOD_getWindowPointOfInfiniteAngle3].setDisplayName ( "" );
            methods[METHOD_randomValue4] = new MethodDescriptor(org.bm.blaise.specto.plane.PlaneVisometry.class.getMethod("randomValue", new Class[] {})); // NOI18N
            methods[METHOD_randomValue4].setDisplayName ( "" );
            methods[METHOD_setDesiredRange5] = new MethodDescriptor(org.bm.blaise.specto.plane.PlaneVisometry.class.getMethod("setDesiredRange", new Class[] {double.class, double.class, double.class, double.class})); // NOI18N
            methods[METHOD_setDesiredRange5].setDisplayName ( "" );
            methods[METHOD_stateChanged6] = new MethodDescriptor(org.bm.blaise.specto.plane.PlaneVisometry.class.getMethod("stateChanged", new Class[] {javax.swing.event.ChangeEvent.class})); // NOI18N
            methods[METHOD_stateChanged6].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods

    // Here you can add code for customizing the methods array.
    
        return methods;     }//GEN-LAST:Methods

    private static java.awt.Image iconColor16 = null;//GEN-BEGIN:IconsDef
    private static java.awt.Image iconColor32 = null;
    private static java.awt.Image iconMono16 = null;
    private static java.awt.Image iconMono32 = null;//GEN-END:IconsDef
    private static String iconNameC16 = null;//GEN-BEGIN:Icons
    private static String iconNameC32 = null;
    private static String iconNameM16 = null;
    private static String iconNameM32 = null;//GEN-END:Icons

    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx

    
//GEN-FIRST:Superclass

    // Here you can add code for customizing the Superclass BeanInfo.

//GEN-LAST:Superclass
	
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     * 
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
	return getBdescriptor();
    }

    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     * 
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
	return getPdescriptor();
    }

    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     * 
     * @return  An array of EventSetDescriptors describing the kinds of 
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
	return getEdescriptor();
    }

    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     * 
     * @return  An array of MethodDescriptors describing the methods 
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
	return getMdescriptor();
    }

    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are 
     * customizing the bean.
     * @return  Index of default property in the PropertyDescriptor array
     * 		returned by getPropertyDescriptors.
     * <P>	Returns -1 if there is no default property.
     */
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }

    /**
     * A bean may have a "default" event that is the event that will
     * mostly commonly be used by human's when using the bean. 
     * @return Index of default event in the EventSetDescriptor array
     *		returned by getEventSetDescriptors.
     * <P>	Returns -1 if there is no default event.
     */
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }

    /**
     * This method returns an image object that can be used to
     * represent the bean in toolboxes, toolbars, etc.   Icon images
     * will typically be GIFs, but may in future include other formats.
     * <p>
     * Beans aren't required to provide icons and may return null from
     * this method.
     * <p>
     * There are four possible flavors of icons (16x16 color,
     * 32x32 color, 16x16 mono, 32x32 mono).  If a bean choses to only
     * support a single icon we recommend supporting 16x16 color.
     * <p>
     * We recommend that icons have a "transparent" background
     * so they can be rendered onto an existing background.
     *
     * @param  iconKind  The kind of icon requested.  This should be
     *    one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32, 
     *    ICON_MONO_16x16, or ICON_MONO_32x32.
     * @return  An image object representing the requested icon.  May
     *    return null if no suitable icon is available.
     */
    public java.awt.Image getIcon(int iconKind) {
        switch ( iconKind ) {
        case ICON_COLOR_16x16:
            if ( iconNameC16 == null )
                return null;
            else {
                if( iconColor16 == null )
                    iconColor16 = loadImage( iconNameC16 );
                return iconColor16;
            }
        case ICON_COLOR_32x32:
            if ( iconNameC32 == null )
                return null;
            else {
                if( iconColor32 == null )
                    iconColor32 = loadImage( iconNameC32 );
                return iconColor32;
            }
        case ICON_MONO_16x16:
            if ( iconNameM16 == null )
                return null;
            else {
                if( iconMono16 == null )
                    iconMono16 = loadImage( iconNameM16 );
                return iconMono16;
            }
        case ICON_MONO_32x32:
            if ( iconNameM32 == null )
                return null;
            else {
                if( iconMono32 == null )
                    iconMono32 = loadImage( iconNameM32 );
                return iconMono32;
            }
	default: return null;
        }
    }

}

