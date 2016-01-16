/**
 * IndexedBean.java
 * Created on Jul 2, 2009
 */

package com.googlecode.blaisemath.firestarter;

/*
 * #%L
 * Firestarter
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

import java.util.Arrays;

/**
 * <p>
 *   <code>IndexedBean</code> ...
 * </p>
 *
 * @author Elisha Peterson
 */
public class IndexedBean {

    /** Test method to retrieve an instance of this class subject to the provided enum value. */
    public static IndexedBean getInstance(TestEnum value) {
        IndexedBean result = new IndexedBean(); result.string[0] = value.s; result.string[1] = Integer.toString(value.i); return result;
    }

    protected String[] string = new String[]{"hello", "me", "you", "everyone"};
    public String[] getStrings() { return string; }
    public void setStrings(String[] string) { this.string = string; }
    public String getStrings(int index) { return this.string[index]; }
    public void setStrings(int index, String newString) { this.string[index] = newString; }

    protected NumberBean[] nb = new NumberBean[]{new NumberBean(), new NumberBean()};
    public NumberBean[] getNbs() { return nb; }
    public void setNbs(NumberBean[] nb) { this.nb = nb; }
    public NumberBean getNbs(int index) { return this.nb[index]; }
    public void setNbs(int index, NumberBean nb) { this.nb[index] = nb; }

    protected TestEnum myEnum = TestEnum.YO;
    public TestEnum getMyEnum() { return myEnum; }
    public void setMyEnum(TestEnum myEnum) { this.myEnum = myEnum; }

    public enum TestEnum { 
        HI (1, "hi"), YO (2, "yo"), DUDE (28, "dude");
        int i; String s;
        TestEnum(int i, String s) { this.i = i; this.s = s; }
    }

    public static class Indexed2 {

        protected IndexedBean inBean;

        public Indexed2() {
            inBean = new IndexedBean();
        }

        /**
         * Get the value of inBean
         *
         * @return the value of inBean
         */
        public IndexedBean getInBean() {
            return inBean;
        }

        /**
         * Set the value of inBean
         *
         * @param inBean new value of inBean
         */
        public void setInBean(IndexedBean inBean) {
            this.inBean = inBean;
        }

    }
}
