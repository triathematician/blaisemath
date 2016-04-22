/*
 * RealIntervalSamplerProvider.java
 * Created Apr 14, 2010
 */

package com.googlecode.blaisemath.line;

/*
 * #%L
 * BlaiseMath
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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.googlecode.blaisemath.coordinate.DomainHint;
import com.googlecode.blaisemath.coordinate.SampleSet;
import com.googlecode.blaisemath.coordinate.ScreenSampleDomainProvider;

/**
 * Utility class for easily generating sampling domains.
 *
 * @author Elisha Peterson
 */
public abstract class RealIntervalSamplerProvider extends RealIntervalBroadcaster
            implements ScreenSampleDomainProvider<Double>, ChangeListener {

    /** Construct to watch for changes from a specified change broadcaster. */
    public RealIntervalSamplerProvider() {
        super(0.0, 1.0);
        setBounds(getNewMinimum(), getNewMaximum());
    }

    /**
     * @return minimum value for when the change broadcaster changes.
     */
    public abstract double getNewMinimum();

    /**
     * @return maximum value for when the change broadcaster changes.
     */
    public abstract double getNewMaximum();

    /**
     * @param pixSpacing
     * @return scale of transformation for the given pixel spacing.
     */
    public abstract double getScale(float pixSpacing);

    @Override
    public SampleSet<Double> samplerWithPixelSpacing(final float pixSpacing, DomainHint hint) {
        final RealIntervalStepSampler result = RealDomainUtils.stepSamplingDomain(this, getScale(pixSpacing), hint);
        addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) { 
                result.setStep( getScale(pixSpacing) ); 
            }
        });
        return result;
    }
    
    @Override
    public void stateChanged(ChangeEvent e) { 
        setBounds(getNewMinimum(), getNewMaximum()); 
    }
}
