/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blaise.pr;

/**
 *
 * @author Elisha
 */
public class Units {
    
    //
    // FACTORIES
    //
    
    public static Distance yards(float val) {
        return new Distance(val, DistanceUnit.Yard);
    }
    
    public static Duration seconds(float val) {
        return new Duration(val, DurationUnit.Second);
    }
    
    public static Duration minSec(float min, float sec) {
        return new Duration(min*60+sec, DurationUnit.Second);
    }
    
    public static Speed speed(Distance distance, Duration time) {
        if (distance.units == DistanceUnit.Yard && time.units == DurationUnit.Second) {
            return new Speed(time.value / (distance.value/50.0), SpeedUnit.SecondsPer50Yard);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    
    //
    // CLASSES
    //
    
    public static class Distance implements Comparable<Distance> {
        private final double value;
        private final DistanceUnit units;

        public Distance(double value, DistanceUnit units) {
            this.value = value;
            this.units = units;
        }

        @Override
        public String toString() {
            return value + " " + units;
        }
        
        public double getValue() {
            return value;
        }
        
        public int compareTo(Distance t) {
            // XXX
            return Double.compare(value, t.value);
        }
    }
    
    public static enum DistanceUnit {
        Foot, Yard, Mile, Meter, Kilometer;
    }
    
    public static class Duration implements Comparable<Duration> {
        private final double value;
        private final DurationUnit units;

        public Duration(double value, DurationUnit units) {
            this.value = value;
            this.units = units;
        }

        @Override
        public String toString() {
            return value + " " + units;
        }

        public double getValue() {
            return value;
        }

        public int compareTo(Duration t) {
            // XXX
            return Double.compare(value, t.value);
        }
    }
    
    public static enum DurationUnit {
        Second, Minute, Hour, Day;
    }
    
    public static class Speed implements Comparable<Speed> {
        private final double value;
        private final SpeedUnit units;

        public Speed(double value, SpeedUnit units) {
            this.value = value;
            this.units = units;
        }

        @Override
        public String toString() {
            return value + " " + units;
        }

        public double getValue() {
            return value;
        }

        public int compareTo(Speed t) {
            // XXX
            return Double.compare(value, t.value);
        }
    }
    
    public static enum SpeedUnit {
        MilesPerHour, MinutesPerMile, SecondsPer50Yard;
    }
    
}
