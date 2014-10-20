/**
 * NumberBean.java
 * Created on Jun 29, 2009
 */

package com.googlecode.blaisemath.firestarter;

/*
 * #%L
 * Firestarter
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

/**
 * <p>
 *   <code>NumberBean</code> ...
 * </p>
 *
 * @author Elisha Peterson
 */
public class NumberBean {

    protected Byte NByte=-5;
    public Byte getNByte() { return NByte; }
    public void setNByte(Byte NByte) { this.NByte = NByte;}

    protected Long NLong=199823844l;
    public Long getNLong() {return NLong;}
    public void setNLong(Long NLong) {this.NLong = NLong;}

    protected double ndouble=2.0;
    public double getNDouble() {return ndouble;}
    public void setNDouble(double doub) {this.ndouble = doub;}

    protected float flot=1f;
    public float getNFloat() {return flot;}
    public void setNFloat(float flot) {this.flot = flot;}

    protected short shrt=3;
    public short getNShort() {return shrt;}
    public void setNShort(short shrt) {this.shrt = shrt;}

    protected int intint=4;
    public int getNInteger() {return intint;}
    public void setNInteger(int intint) {this.intint = intint;}

    @Override
    public String toString() {
        return "" + NByte + NLong + ndouble + flot + shrt + intint;
    }

}
