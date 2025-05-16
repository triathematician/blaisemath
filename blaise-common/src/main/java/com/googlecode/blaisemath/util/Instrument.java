package com.googlecode.blaisemath.util;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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

import com.google.common.annotations.Beta;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Provides centralized instrumentation for potentially long-executing graph algorithms.
 *
 * @author Elisha Peterson
 */
@Beta
public class Instrument {

    private static int id = 0;
    private static final String START = "start";
    private static final String END = "end";

    /** Max number to keep in log */
    private static final int MAX_EVENTS = 10000;
    /** All log events */
    private static final Map<Integer,LogEvent> ALL = Maps.newLinkedHashMap();
    /** Log events split by algorithm */
    private static final Multimap<String,LogEvent> LOG = LinkedHashMultimap.create();

    private Instrument() {
    }
    
    private static synchronized int nextId() {
        return id++; 
    }

    /**
     * Log a start algorithm event
     * @param algorithm name of algorithm/method/etc.
     * @param info additional information
     * @return unique id for log event
     */
    @Beta
    public static synchronized int start(String algorithm, String... info) {
        return log(algorithm, START, info);
    }

    /**
     * Log an intermediate algorithm event
     * @param id id of log event
     * @param event name of event
     * @param info additional information
     */
    @Beta
    public static synchronized void middle(int id, String event, String... info) {
        log(id, event, info);
    }

    /**
     * Log a start algorithm event
     * @param id id of log event
     */
    @Beta
    public static synchronized void end(int id) {
        log(id, END);
    }

    private static synchronized void log(int id, String event, String... info) {
        LogEvent le = ALL.get(id);
        if (le != null) {
            if (END.equals(event)) {
                le.end();
            } else {
                String[] logged = new String[info.length+1];
                logged[0] = event;
                System.arraycopy(info, 0, logged, 1, info.length);
                le.addInfo(logged);
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static synchronized int log(String algorithm, String event, String... info) {
        String[] logged = new String[info.length+1];
        logged[0] = event;
        System.arraycopy(info, 0, logged, 1, info.length);
        LogEvent e = new LogEvent(logged);
        LOG.put(algorithm, e);
        ALL.put(e.id, e);
        if (ALL.size() > 1.5 * MAX_EVENTS) {
            Set<Integer> rid = new HashSet<>();
            Set<LogEvent> rem = new HashSet<>();
            int n = 0;
            for (Entry<Integer, LogEvent> i : ALL.entrySet()) {
                rid.add(i.getKey());
                rem.add(i.getValue());
                if (n++ > .75 * MAX_EVENTS) {
                    break;
                }
            }
            ALL.keySet().removeAll(rid);
            for (String l : LOG.keySet()) {
                LOG.get(l).removeAll(rem);
            }
        }
        return e.id;
    }

    @Beta
    public static synchronized void print(PrintStream out, long minT) {
        out.println("Graph Algorithm Log");
        for (String a : LOG.keySet()) {
            out.println(" -- Algorithm " + a + " --");
            for (LogEvent l : LOG.get(a)) {
                if (l.dur >= minT) {
                    out.println(l);
                }
            }
        }
    }

    @Beta
    public static void print(PrintStream out) {
        print(out, 10);
    }

    @Beta
    private static class LogEvent {
        private final int id;
        private final long start;
        private final List<String[]> info = new ArrayList<>();
        private long dur;

        LogEvent(String... info) {
            this.id = nextId();
            this.start = System.currentTimeMillis();
            this.info.add(info);
        }

        void addInfo(String... info) {
            this.info.add(info);
        }

        void end() {
            info.add(new String[]{END});
            dur = System.currentTimeMillis() - start;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(100);
            sb.append(String.format("LogEvent[id=%d, start=%d, dur=%d]\t", id, start, dur));
            for (String[] arr : info) {
                sb.append(String.format("\t%s", Arrays.asList(arr)));
            }
            return sb.toString();
        }
    }
}
