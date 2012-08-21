/*
 * GAInstrument.java
 * Created on Jun 8, 2012
 */
package org.bm.blaise.scio.graph;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    private static class LogEvent {
        int id;
        long start;
        long dur;
        Map<String,List<String>> info = new TreeMap<String,List<String>>();
        public LogEvent(String[] info) {
            this.id = nextId();
            this.start = System.currentTimeMillis();
            this.info.put("0", Arrays.asList(info));
        }
        void addInfo(String[] info) {
            this.info.put(this.info.size()+"", Arrays.asList(info));
        }
        void end() {
            info.put(this.info.size()+"", Arrays.asList(END));
            dur = System.currentTimeMillis()-start;
        }
        @Override
        public String toString() {
            return "LogEvent{" + "id=" + id + ", start=" + start + ", dur=" + dur + ", info=" + info + '}';
        }
    }

    private static final List<LogEvent> all = new ArrayList<LogEvent>();
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
            if (id < all.size()) {
                LogEvent le = all.get(id);
                if (event == END)
                    le.end();
                else {
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
            if (!log.containsKey(algorithm))
                log.put(algorithm, new ArrayList<LogEvent>());
            String[] logged = new String[info.length+1];
            logged[0] = event;
            System.arraycopy(info, 0, logged, 1, info.length);
            LogEvent e = new LogEvent(logged);
            log.get(algorithm).add(e);
            all.add(e);
            return e.id;
        } catch (Exception e) {
            return -1;
        }
    }

    public static void print(PrintStream out) {
        try {
            out.println("Graph Algorithm Log");
            for (String a : log.keySet()) {
                out.println(" -- Algorithm " + a + " --");
                for (LogEvent l : log.get(a))
                    if (l.dur > 5)
                        out.println(l);
            }
        } catch (Exception e) {}
    }

}
