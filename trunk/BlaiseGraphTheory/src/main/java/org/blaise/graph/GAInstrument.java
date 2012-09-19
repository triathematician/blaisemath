/*
 * GAInstrument.java
 * Created on Jun 8, 2012
 */
package org.blaise.graph;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Provides centralized instrumentation for potentially long-executing graph algorithms.
 *
 * @author petereb1
 */
public class GAInstrument {

    static int id = 0;
    private static synchronized int nextId() { return id++; }

    private static final String START = "start";
    private static final String END = "end";

    /** Max number to keep in log */
    private static int maxEvents = 10000;
    /** All log events */
    private static final Map<Integer,LogEvent> all = new LinkedHashMap<Integer,LogEvent>();
    /** Log events split by algorithm */
    private static final Map<String,List<LogEvent>> log = new HashMap<String,List<LogEvent>>();

    /**
     * Log a start algorithm event
     * @param algorithm name of algorithm/method/etc.
     * @param info additional information
     * @return unique id for log event
     */
    public static synchronized int start(String algorithm, String... info) {
        return log(algorithm, START, info);
    }

    /**
     * Log an intermediate algorithm event
     * @param id id of log event
     * @param event name of event
     * @param info additional information
     */
    public static synchronized void middle(int id, String event, String... info) {
        log(id, event);
    }

    /**
     * Log a start algorithm event
     * @param id id of log event
     * @param info additional information
     */
    public static synchronized void end(int id) {
        log(id, END);
    }

    private static synchronized void log(int id, String event, String... info) {
        try {
            LogEvent le = all.get(id);
            if (le != null) {
                if (event == END) {
                    le.end();
                } else {
                    String[] logged = new String[info.length+1];
                    logged[0] = event;
                    System.arraycopy(info, 0, logged, 1, info.length);
                    le.addInfo(logged);
                }
            }
        } catch (Exception e) {}
    }

    private static synchronized int log(String algorithm, String event, String... info) {
        try {
            if (!log.containsKey(algorithm)) {
                log.put(algorithm, new ArrayList<LogEvent>());
            }
            String[] logged = new String[info.length+1];
            logged[0] = event;
            System.arraycopy(info, 0, logged, 1, info.length);
            LogEvent e = new LogEvent(logged);
            log.get(algorithm).add(e);
            all.put(e.id,e);
            if (all.size() > 1.5*maxEvents) {
                Set<Integer> rid = new HashSet<Integer>();
                Set<LogEvent> rem = new HashSet<LogEvent>();
                int n = 0;
                for (Entry<Integer,LogEvent> i : all.entrySet()) {
                    rid.add(i.getKey());
                    rem.add(i.getValue());
                    if (n++ > .75*maxEvents) {
                        break;
                    }
                }
                all.keySet().removeAll(rid);
                for (List<LogEvent> l : log.values()) {
                    l.removeAll(rem);
                }
            }
            return e.id;
        } catch (Exception e) {
            return -1;
        }
    }

    public static void print(PrintStream out, long minT) {
        try {
            out.println("Graph Algorithm Log");
            for (String a : log.keySet()) {
                out.println(" -- Algorithm " + a + " --");
                for (LogEvent l : log.get(a)) {
                    if (l.dur >= minT) {
                        out.println(l);
                    }
                }
            }
        } catch (Exception e) {}
    }

    public static void print(PrintStream out) {
        print(out, 10);
    }



    //
    // INNER CLASS
    //

    /** Captures a single event */
    private static class LogEvent {
        int id;
        long start;
        long dur;
        List<String[]> info = new ArrayList<String[]>();
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
            dur = System.currentTimeMillis()-start;
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

    private GAInstrument() {
    }
}
