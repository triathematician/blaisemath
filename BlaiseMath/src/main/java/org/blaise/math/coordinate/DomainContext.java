/*
 * DomainContext.java
 * Created Apr 2010
 */

package org.blaise.math.coordinate;

import java.util.HashMap;

/**
 * Keeps track of a collection of domains identified by strings. Intended to be used
 * for interested objects to retrieve a particular domain.
 * 
 * @author Elisha Peterson
 */
public class DomainContext {

    /** Table that stores the domains. */
    HashMap<String, DomainEntry> entries;

    /** Construct with no entries. */
    public DomainContext() {
        entries = new HashMap<String, DomainEntry>();
    }

    /** Add an entry to the domain context. */
    public <T> void addEntry(String id, Domain<T> domain, Class<? extends T> cls) {
        entries.put(id, new DomainEntry(domain, cls));
    }

    /** Remove an entry from the domain context. */
    public <T> void removeEntry(String id) {
        entries.remove(id);
    }

    /**
     * Attempts to find a sampler of the given identifier and class type. Looks through the table of
     * entries to find the property generator. Returns null if the table has no sampler with
     * matching id, or if that sampler does not have the requisite class type.
     *
     * @param <T> class type of the sampler
     * @param cls class identifier of the sampler
     * @param id identifier of the sampler
     * @return the found sample generator, or null if it cannot be found.     */

    public <T> Domain<T> getDomain(String id, Class<? extends T> cls) {
        DomainEntry en = entries.get(id);
        if (en != null && en.cls == cls)
            return en.gen;
        return null;
    }




    /** Class used to store entries, consisting of a domain and its underlying class. */
    static class DomainEntry {
        Domain gen;
        Class cls;
        public <T> DomainEntry(Domain<T> gen, Class<? extends T> cls) {
            this.gen = gen;
            this.cls = cls;
        }
    }
}
