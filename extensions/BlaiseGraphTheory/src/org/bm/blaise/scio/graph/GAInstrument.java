/*
 * GAInstrument.java
 * Created on Jun 8, 2012
 */
package org.bm.blaise.scio.graph;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides centralized instrumentation for potentially long-executing graph algorithms.
 *
 * @author petereb1
 */
public class GAInstrument {

    private static final String START = "start";
    private static final String END = "end";
    private static final Map<String,Map<Long,String[]>> log = new HashMap<String,Map<Long,String[]>>();

    /**
     * Log a start algorithm event
     * @param algorithm name of algorithm/method/etc.
     * @param info additional information
     */
    public static synchronized void start(String algorithm, String... info) {
        log(algorithm, START, info);
    }

    /**
     * Log an intermediate algorithm event
     * @param algorithm name of algorithm/method/etc.
     * @param event name of event
     * @param info additional information
     */
    public static synchronized void middle(String algorithm, String event, String... info) {
        log(algorithm, event);
    }

    /**
     * Log a start algorithm event
     * @param algorithm name of algorithm/method/etc.
     * @param info additional information
     */
    public static synchronized void end(String algorithm) {
        log(algorithm, END);
    }

    private static synchronized void log(String algorithm, String event, String... info) {
        if (!log.containsKey(algorithm))
            log.put(algorithm, new LinkedHashMap<Long, String[]>());
        String[] logged = new String[info.length+1];
        logged[0] = event;
        System.arraycopy(info, 0, logged, 1, info.length);
        log.get(algorithm).put(System.currentTimeMillis(), logged);
    }

}
