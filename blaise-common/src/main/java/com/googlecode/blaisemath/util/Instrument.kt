package com.googlecode.blaisemath.util

import com.google.common.annotations.Beta
import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.Maps
import com.google.common.collect.Multimap
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.io.PrintStream
import java.util.*

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
*/ /**
 * Provides centralized instrumentation for potentially long-executing graph algorithms.
 *
 * @author Elisha Peterson
 */
object Instrument {
    private var id = 0
    private val START: String? = "start"
    private val END: String? = "end"

    /** Max number to keep in log  */
    private const val MAX_EVENTS = 10000

    /** All log events  */
    private val ALL: MutableMap<Int?, LogEvent?>? = Maps.newLinkedHashMap()

    /** Log events split by algorithm  */
    private val LOG: Multimap<String?, LogEvent?>? = LinkedHashMultimap.create()
    @Synchronized
    private fun nextId(): Int {
        return id++
    }

    /**
     * Log a start algorithm event
     * @param algorithm name of algorithm/method/etc.
     * @param info additional information
     * @return unique id for log event
     */
    @Beta
    @Synchronized
    fun start(algorithm: String?, vararg info: String?): Int {
        return log(algorithm, START, *info)
    }

    /**
     * Log an intermediate algorithm event
     * @param id id of log event
     * @param event name of event
     * @param info additional information
     */
    @Beta
    @Synchronized
    fun middle(id: Int, event: String?, vararg info: String?) {
        log(id, event, *info)
    }

    /**
     * Log a start algorithm event
     * @param id id of log event
     */
    @Beta
    @Synchronized
    fun end(id: Int) {
        log(id, END)
    }

    @Synchronized
    private fun log(id: Int, event: String?, vararg info: String?) {
        val le = ALL.get(id)
        if (le != null) {
            if (END == event) {
                le.end()
            } else {
                val logged = arrayOfNulls<String?>(info.size + 1)
                logged[0] = event
                System.arraycopy(info, 0, logged, 1, info.size)
                le.addInfo(*logged)
            }
        }
    }

    @Synchronized
    private fun log(algorithm: String?, event: String?, vararg info: String?): Int {
        val logged = arrayOfNulls<String?>(info.size + 1)
        logged[0] = event
        System.arraycopy(info, 0, logged, 1, info.size)
        val e = LogEvent(*logged)
        LOG.put(algorithm, e)
        ALL[e.id] = e
        if (ALL.size > 1.5 * MAX_EVENTS) {
            val rid: MutableSet<Int?> = HashSet()
            val rem: MutableSet<LogEvent?> = HashSet()
            var n = 0
            for ((key, value) in ALL) {
                rid.add(key)
                rem.add(value)
                if (n++ > .75 * MAX_EVENTS) {
                    break
                }
            }
            ALL.keys.removeAll(rid)
            for (l in LOG.keySet()) {
                LOG.get(l).removeAll(rem)
            }
        }
        return e.id
    }

    @Beta
    @Synchronized
    fun print(out: PrintStream?, minT: Long) {
        out.println("Graph Algorithm Log")
        for (a in LOG.keySet()) {
            out.println(" -- Algorithm $a --")
            for (l in LOG.get(a)) {
                if (l.dur >= minT) {
                    out.println(l)
                }
            }
        }
    }

    @Beta
    fun print(out: PrintStream?) {
        print(out, 10)
    }

    @Beta
    private class LogEvent internal constructor(vararg info: String?) {
        private val id: Int
        private val start: Long
        private val info: MutableList<Array<String?>?>? = ArrayList()
        private var dur: Long = 0
        fun addInfo(vararg info: String?) {
            this.info.add(info)
        }

        fun end() {
            info.add(arrayOf(END))
            dur = System.currentTimeMillis() - start
        }

        override fun toString(): String {
            val sb = StringBuilder(100)
            sb.append(String.format("LogEvent[id=%d, start=%d, dur=%d]\t", id, start, dur))
            for (arr in info) {
                sb.append(String.format("\t%s", Arrays.asList<String?>(*arr)))
            }
            return sb.toString()
        }

        init {
            this.id = nextId()
            start = System.currentTimeMillis()
            this.info.add(info)
        }
    }
}