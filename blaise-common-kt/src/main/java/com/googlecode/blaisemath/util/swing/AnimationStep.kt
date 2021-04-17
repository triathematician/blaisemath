package com.googlecode.blaisemath.util.swing
/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.Timer

/**
 * Represents a single step of logic in a basic animation. A static method initializes
 * a swing [javax.swing.Timer] to execute the animation step for every value
 * between a min and max iteration index, at which point the timer stops. Note that
 * the animation logic runs on the event dispatch thread.
 * @author Elisha Peterson
 */
abstract class AnimationStep {
    /**
     * Execute logic for one step in the animation.
     * @param idx the index of the step
     * @param pct percent completion, between 0 and 1
     */
    abstract fun run(idx: Int, pct: Double)

    /**
     * Runnable that cancels after a given number of steps. The [AnimationStep]
     * is called for every value between a min and a max integer value.
     */
    private class AnimationRunner(private val min: Int, private val max: Int, private val step: AnimationStep) : ActionListener {
        init { require (max >= min) }

        private var runStep = 0
        private var timer: Timer? = null

        /** Run the animation cycle, with the given # of millis between firing. */
        fun startTimer(period: Int) = Timer(0, this).apply {
            runStep = min
            isRepeats = true
            isCoalesce = true
            delay = period
            timer = this
            start()
        }

        override fun actionPerformed(evt: ActionEvent?) {
            val pct = if (min == max) 1.0 else (runStep - min) / (max - min).toDouble()
            step.run(runStep, pct)
            if (runStep == max) {
                timer?.stop()
            }
            runStep++
        }
    }

    companion object {
        /** Launch an animation characterized by the given step. All indices between min and max, inclusive, will be
         * passed to the step. The steps will be executed on fixed delay, meaning each step must complete before the
         * next starts. The events fire on the event dispatch thread.
         * @param min minimum index to pass to the step
         * @param max maximum index to pass to the step
         * @param delay how long to wait between steps, in milliseconds
         * @param step the step logic to execute
         * @return timer executing the animation
         */
        @kotlin.jvm.JvmStatic
        fun animate(min: Int, max: Int, delay: Int, step: AnimationStep) = AnimationRunner(min, max, step).startTimer(delay)
    }
}