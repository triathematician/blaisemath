/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.sequor.fk;

/**
 * Accesses a function kernel to return a function of specified type.
 * So a call to setFunction should result in the kernel's function changing,
 * and correspondingly all clients that use that function.
 * 
 * @author ae3263
 */
public class FunctionKernelClient {

    /** The kernel used to retrieve the function. */
    FunctionKernel kernel;
    /** Name of the function in the kernel. */
    String funcName;
    
    int nInputs;
    int nOutputs;

    public FunctionKernelClient(FunctionKernel kernel, String funcName, int nInputs, int nOutputs) {
        this.kernel = kernel;
        this.funcName = funcName;
        this.nInputs = nInputs;
        this.nOutputs = nOutputs;
    }

    /** Returns the function. */
    public Object getFunction() {
        return kernel.getFunction(funcName);
    }
    
    /** Sets the function in the kernel. */
    public void setFunction(Object function) {
        kernel.setFunction(funcName, function);
    }

    public String getFunctionName() {
        return funcName;
    }

    /** Returns number of inputs of function. */
    public int getNumInputs() { return nInputs; }

    /** Returns number of outputs of function. */
    public int getNumOutputs() { return nOutputs; }
}
