package com.googlecode.blaisemath.util
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

import java.io.PrintStream

/**
 * Centralized instrumentation for potentially long-executing algorithms.
 */
object Instrument {

    private const val START = "start"
    private const val END = "end"

    /** Max number to keep in log */
    private const val maxEvents = 10000

    /** Current event id */
    private var id = 0

    /** All log events  */
    private val ALL = mutableMapOf<Int, LogEvent>()
    /** Log events split by algorithm  */
    private val LOG_EVENTS = mutableMapOf<String, MutableList<LogEvent>>()

    @Synchronized
    private fun nextId() = id++

    /** Log a start algorithm event */
    @kotlin.jvm.JvmStatic
    @Synchronized
    fun start(algorithm: String, vararg info: String) = log(algorithm, START, *info)

    /**
     * Log an intermediate algorithm event
     * @param id id of log event
     * @param event name of event
     * @param info additional information
     */
    @kotlin.jvm.JvmStatic
    @Synchronized
    fun middle(id: Int, event: String, vararg info: String) {
        log(id, event, *info)
    }

    /**
     * Log a start algorithm event
     * @param id id of log event
     */
    @kotlin.jvm.JvmStatic
    @Synchronized
    fun end(id: Int) {
        log(id, END)
    }

    @Synchronized
    private fun log(id: Int, event: String, vararg info: String) {
        val le = ALL[id]
        if (le != null) {
            if (END == event) {
                le.end()
            } else {
                le.addInfo((listOf(event) + info.toList()).toTypedArray())
            }
        }
    }

    @Synchronized
    private fun log(algorithm: String, event: String, vararg info: String): Int {
        val e = LogEvent((listOf(event) + info.toList()).toTypedArray())
        LOG_EVENTS.getOrPut(algorithm) { mutableListOf() }.add(e)
        ALL[e.id] = e
        if (ALL.size > 1.5 * maxEvents) {
            val rid = mutableSetOf<Int>()
            val rem = mutableSetOf<LogEvent>()
            var n = 0
            for ((key, value) in ALL) {
                rid.add(key)
                rem.add(value)
                if (n++ > .75 * maxEvents) {
                    break
                }
            }
            ALL.keys.removeAll(rid)
            for (l in LOG_EVENTS.keys) {
                LOG_EVENTS[l]!!.removeAll(rem)
            }
        }
        return e.id
    }

    @kotlin.jvm.JvmStatic
    fun print(out: PrintStream) = print(out, 10)

    @Synchronized
    fun print(out: PrintStream, minT: Long) {
        out.println("Algorithm Log")
        LOG_EVENTS.forEach { (algorithm, events) ->
            out.println(" -- Algorithm $algorithm --")
            events.filter { it.dur >= minT }.forEach { out.println(it) }
        }
    }

    private class LogEvent(info: Array<out String>) {
        var id = nextId()
        var start = System.currentTimeMillis()
        var dur = 0L
        var info = mutableListOf(info)

        fun addInfo(it: Array<out String>) = info.add(it)

        fun end() {
            info.add(arrayOf(END))
            dur = System.currentTimeMillis() - start
        }

        override fun toString(): String {
            val sb = StringBuilder(100)
            sb.append(String.format("LogEvent[id=%d, start=%d, dur=%d]\t", id, start, dur))
            for (arr in info) {
                sb.append("\t")
                sb.append(arr.toString())
            }
            return sb.toString()
        }
    }
}
