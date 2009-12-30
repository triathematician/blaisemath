/*
 * ParsedUnivariateRealFunction.java
 * Created Dec 29, 2009
 */

package org.bm.blaise.scio.function;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.bm.blaise.scribo.parser.ParseException;
import org.bm.blaise.scribo.parser.SemanticTreeEvaluationException;
import org.bm.blaise.scribo.parser.real.ParsedRealFunction;

/**
 * <p>
 *   This class wraps the <code>ParsedRealFunction</code> class defined in the
 *   <b>blaiseparser2</b> library in order to implement <code>UnivariateRealFunction</code>.
 *   Parsing is handled by the <code>RealTreeBuilder</code> class with grammar
 *   defined by <code>RealGrammar</code>.
 * </p>
 * @author Elisha Peterson
 */
public class ParsedUnivariateRealFunction extends ParsedRealFunction implements UnivariateRealFunction {

    /** Construct a real function specified by the given input string.
     * @param funcString the function as a string
     * @throws ParseException if the function cannot be parsed
     */
    public ParsedUnivariateRealFunction(String funcString) throws ParseException {
        super(funcString);
    }

    public double value(double x) throws FunctionEvaluationException {
        try {
            return valueOf(x);
        } catch (SemanticTreeEvaluationException ex) {
            throw new FunctionEvaluationException(x);
        }
    }


}
