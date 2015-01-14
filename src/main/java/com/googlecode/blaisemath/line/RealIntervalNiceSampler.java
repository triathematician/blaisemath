/*
 * RealIntervalNiceSampler.java
 * Created on Apr 8, 2010
 */

package com.googlecode.blaisemath.line;

/*
 * #%L
 * BlaiseMath
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import java.util.List;
import com.googlecode.blaisemath.coordinate.NiceRangeGenerator;

/**
 * <p>
 *    This class contains features to sample an interval at evenly-spaced values separated (approximately) by
 *    a given "step". A <code>RealInterval</code> is required, together with a <code>double</code>
 *    representing the step value.
 * </p>
 * <p>
 *    This class uses a <code>NiceRangeGenerator</code> to come up with the sampled values. Generally, the sampled
 *    values will not be separated <i>exactly</i> by the step parameter, but it should be close.
 * </p>
 * @author Elisha Peterson
 */
public class RealIntervalNiceSampler extends RealIntervalStepSampler {

    /** Nice range generator containing the abstract list. */
    List<Double> gen;
    /** Whether to sample at multiples of pi. */
    boolean samplePi = false;

    public RealIntervalNiceSampler(RealInterval domain, double step) {
        super(domain, step);
    }

    public RealIntervalNiceSampler(RealInterval domain, double step, boolean samplePi) {
        super(domain, step);
        this.samplePi = samplePi;
    }

    /** Updates the underlying range. */
    @Override
    public void update() {
        gen = samplePi ? NiceRangeGenerator.PI.niceRange(domain.getMinimum(), domain.getMaximum(), step)
                : NiceRangeGenerator.STANDARD.niceRange(domain.getMinimum(), domain.getMaximum(), step);
    }

    @Override
    public Double get(int index) {
        return gen.get(index);
    }

    @Override
    public int size() {
        return gen.size();
    }

    @Override
    public List<Double> getSamples() {
        return gen;
    }

    @Override
    public Double getSampleDiff() {
        return Math.abs(gen.get(1) - gen.get(0));
    }
}
