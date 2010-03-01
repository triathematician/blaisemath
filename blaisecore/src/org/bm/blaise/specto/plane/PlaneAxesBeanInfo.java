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
public class PlaneAxesBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( org.bm.blaise.specto.plane.PlaneAxes.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_adjusting = 0;
    private static final int PROPERTY_editable = 1;
    private static final int PROPERTY_labelStyle = 2;
    private static final int PROPERTY_selected = 3;
    private static final int PROPERTY_strokeStyle = 4;
    private static final int PROPERTY_style = 5;
    private static final int PROPERTY_ticksVisible = 6;
    private static final int PROPERTY_visible = 7;
    private static final int PROPERTY_XLabel = 8;
    private static final int PROPERTY_YLabel = 9;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[10];
    
        try {
            properties[PROPERTY_adjusting] = new PropertyDescriptor ( "adjusting", org.bm.blaise.specto.plane.PlaneAxes.class, "isAdjusting", null ); // NOI18N
            properties[PROPERTY_adjusting].setHidden ( true );
            properties[PROPERTY_editable] = new PropertyDescriptor ( "editable", org.bm.blaise.specto.plane.PlaneAxes.class, "isEditable", "setEditable" ); // NOI18N
            properties[PROPERTY_editable].setExpert ( true );
            properties[PROPERTY_labelStyle] = new PropertyDescriptor ( "labelStyle", org.bm.blaise.specto.plane.PlaneAxes.class, "getLabelStyle", "setLabelStyle" ); // NOI18N
            properties[PROPERTY_selected] = new PropertyDescriptor ( "selected", org.bm.blaise.specto.plane.PlaneAxes.class, "isSelected", "setSelected" ); // NOI18N
            properties[PROPERTY_selected].setHidden ( true );
            properties[PROPERTY_strokeStyle] = new PropertyDescriptor ( "strokeStyle", org.bm.blaise.specto.plane.PlaneAxes.class, "getStrokeStyle", "setStrokeStyle" ); // NOI18N
            properties[PROPERTY_style] = new PropertyDescriptor ( "style", org.bm.blaise.specto.plane.PlaneAxes.class, "getStyle", "setStyle" ); // NOI18N
            properties[PROPERTY_style].setPreferred ( true );
            properties[PROPERTY_ticksVisible] = new PropertyDescriptor ( "ticksVisible", org.bm.blaise.specto.plane.PlaneAxes.class, "isTicksVisible", "setTicksVisible" ); // NOI18N
            properties[PROPERTY_ticksVisible].setPreferred ( true );
            properties[PROPERTY_visible] = new PropertyDescriptor ( "visible", org.bm.blaise.specto.plane.PlaneAxes.class, "isVisible", "setVisible" ); // NOI18N
            properties[PROPERTY_XLabel] = new PropertyDescriptor ( "XLabel", org.bm.blaise.specto.plane.PlaneAxes.class, "getXLabel", "setXLabel" ); // NOI18N
            properties[PROPERTY_XLabel].setPreferred ( true );
            properties[PROPERTY_YLabel] = new PropertyDescriptor ( "YLabel", org.bm.blaise.specto.plane.PlaneAxes.class, "getYLabel", "setYLabel" ); // NOI18N
            properties[PROPERTY_YLabel].setPreferred ( true );
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
            eventSets[EVENT_changeListener] = new EventSetDescriptor ( org.bm.blaise.specto.plane.PlaneAxes.class, "changeListener", javax.swing.event.ChangeListener.class, new String[] {"stateChanged"}, "addChangeListener", "removeChangeListener" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Events

    // Here you can add code for customizing the event sets array.

        return eventSets;     }//GEN-LAST:Events

    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_clone0 = 0;
    private static final int METHOD_instance1 = 1;
    private static final int METHOD_instance2 = 2;
    private static final int METHOD_instance3 = 3;
    private static final int METHOD_isClickablyCloseTo4 = 4;
    private static final int METHOD_mouseClicked5 = 5;
    private static final int METHOD_mouseDragged6 = 6;
    private static final int METHOD_mouseEntered7 = 7;
    private static final int METHOD_mouseExited8 = 8;
    private static final int METHOD_mouseMoved9 = 9;
    private static final int METHOD_mousePressed10 = 10;
    private static final int METHOD_mouseReleased11 = 11;
    private static final int METHOD_paintComponent12 = 12;
    private static final int METHOD_stateChanged13 = 13;
    private static final int METHOD_toString14 = 14;
    private static final int METHOD_visometryChanged15 = 15;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[16];
    
        try {
            methods[METHOD_clone0] = new MethodDescriptor(org.bm.blaise.specto.plane.PlaneAxes.class.getMethod("clone", new Class[] {})); // NOI18N
            methods[METHOD_clone0].setDisplayName ( "" );
            methods[METHOD_instance1] = new MethodDescriptor(org.bm.blaise.specto.plane.PlaneAxes.class.getMethod("instance", new Class[] {java.lang.String.class, java.lang.String.class})); // NOI18N
            methods[METHOD_instance1].setDisplayName ( "" );
            methods[METHOD_instance2] = new MethodDescriptor(org.bm.blaise.specto.plane.PlaneAxes.class.getMethod("instance", new Class[] {org.bm.blaise.specto.plane.PlaneAxes.AxisStyle.class})); // NOI18N
            methods[METHOD_instance2].setDisplayName ( "" );
            methods[METHOD_instance3] = new MethodDescriptor(org.bm.blaise.specto.plane.PlaneAxes.class.getMethod("instance", new Class[] {org.bm.blaise.specto.plane.PlaneAxes.AxisStyle.class, java.lang.String.class, java.lang.String.class})); // NOI18N
            methods[METHOD_instance3].setDisplayName ( "" );
            methods[METHOD_isClickablyCloseTo4] = new MethodDescriptor(org.bm.blaise.specto.plane.PlaneAxes.class.getMethod("isClickablyCloseTo", new Class[] {org.bm.blaise.specto.visometry.VisometryMouseEvent.class})); // NOI18N
            methods[METHOD_isClickablyCloseTo4].setDisplayName ( "" );
            methods[METHOD_mouseClicked5] = new MethodDescriptor(org.bm.blaise.specto.visometry.AbstractDynamicPlottable.class.getMethod("mouseClicked", new Class[] {org.bm.blaise.specto.visometry.VisometryMouseEvent.class})); // NOI18N
            methods[METHOD_mouseClicked5].setDisplayName ( "" );
            methods[METHOD_mouseDragged6] = new MethodDescriptor(org.bm.blaise.specto.visometry.AbstractDynamicPlottable.class.getMethod("mouseDragged", new Class[] {org.bm.blaise.specto.visometry.VisometryMouseEvent.class})); // NOI18N
            methods[METHOD_mouseDragged6].setDisplayName ( "" );
            methods[METHOD_mouseEntered7] = new MethodDescriptor(org.bm.blaise.specto.visometry.AbstractDynamicPlottable.class.getMethod("mouseEntered", new Class[] {org.bm.blaise.specto.visometry.VisometryMouseEvent.class})); // NOI18N
            methods[METHOD_mouseEntered7].setDisplayName ( "" );
            methods[METHOD_mouseExited8] = new MethodDescriptor(org.bm.blaise.specto.visometry.AbstractDynamicPlottable.class.getMethod("mouseExited", new Class[] {org.bm.blaise.specto.visometry.VisometryMouseEvent.class})); // NOI18N
            methods[METHOD_mouseExited8].setDisplayName ( "" );
            methods[METHOD_mouseMoved9] = new MethodDescriptor(org.bm.blaise.specto.visometry.AbstractDynamicPlottable.class.getMethod("mouseMoved", new Class[] {org.bm.blaise.specto.visometry.VisometryMouseEvent.class})); // NOI18N
            methods[METHOD_mouseMoved9].setDisplayName ( "" );
            methods[METHOD_mousePressed10] = new MethodDescriptor(org.bm.blaise.specto.visometry.AbstractDynamicPlottable.class.getMethod("mousePressed", new Class[] {org.bm.blaise.specto.visometry.VisometryMouseEvent.class})); // NOI18N
            methods[METHOD_mousePressed10].setDisplayName ( "" );
            methods[METHOD_mouseReleased11] = new MethodDescriptor(org.bm.blaise.specto.visometry.AbstractDynamicPlottable.class.getMethod("mouseReleased", new Class[] {org.bm.blaise.specto.visometry.VisometryMouseEvent.class})); // NOI18N
            methods[METHOD_mouseReleased11].setDisplayName ( "" );
            methods[METHOD_paintComponent12] = new MethodDescriptor(org.bm.blaise.specto.plane.PlaneAxes.class.getMethod("paintComponent", new Class[] {org.bm.blaise.specto.visometry.VisometryGraphics.class})); // NOI18N
            methods[METHOD_paintComponent12].setDisplayName ( "" );
            methods[METHOD_stateChanged13] = new MethodDescriptor(org.bm.blaise.specto.visometry.AbstractPlottable.class.getMethod("stateChanged", new Class[] {javax.swing.event.ChangeEvent.class})); // NOI18N
            methods[METHOD_stateChanged13].setDisplayName ( "" );
            methods[METHOD_toString14] = new MethodDescriptor(org.bm.blaise.specto.plane.PlaneAxes.class.getMethod("toString", new Class[] {})); // NOI18N
            methods[METHOD_toString14].setDisplayName ( "" );
            methods[METHOD_visometryChanged15] = new MethodDescriptor(org.bm.blaise.specto.plane.PlaneAxes.class.getMethod("visometryChanged", new Class[] {org.bm.blaise.specto.visometry.Visometry.class, org.bm.blaise.specto.visometry.VisometryGraphics.class})); // NOI18N
            methods[METHOD_visometryChanged15].setDisplayName ( "" );
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

