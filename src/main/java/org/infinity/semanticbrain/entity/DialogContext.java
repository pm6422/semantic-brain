package org.infinity.semanticbrain.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.Stack;

/**
 * DialogContext class
 * <p>
 * Note: Serialization friendly class
 */
public class DialogContext implements Serializable {

    private Stack<Output> latestOutputs   = new Stack<>();
    private int           stackSize       = 4;
    private int           maxAgeInSeconds = 40;

    public DialogContext(int stackSize, int maxAgeInSeconds) {
        this.stackSize = stackSize;
        this.maxAgeInSeconds = maxAgeInSeconds;
    }

    public void push(Output output) {
        if (latestOutputs.size() == stackSize) {
            this.removeHead();
        }
        latestOutputs.push(output);
    }

    private void removeHead() {
        if (!latestOutputs.isEmpty()) {
            latestOutputs.removeElementAt(0);
        }
    }

    public Output pop() {
        if (latestOutputs.isEmpty()) {
            return null;
        }
        Output output = latestOutputs.pop();
        if (Instant.now().minusSeconds(maxAgeInSeconds).isAfter(output.getTime())) {
            // Expired
            return null;
        }
        return output;
    }

    /**
     * Always get the first element if revoke multiple times
     *
     * @return
     */
    public Output peek() {
        if (latestOutputs.isEmpty()) {
            return null;
        }
        Output output = latestOutputs.peek();
        if (Instant.now().minusSeconds(maxAgeInSeconds).isAfter(output.getTime())) {
            // Expired
            return null;
        }
        return output;
    }

    public int getRepeatInputTimes() {
        if (latestOutputs.isEmpty()) {
            return 0;
        }

        int repeatCount = 0;
        Output last = latestOutputs.elementAt(latestOutputs.size() - 1);
        int i = latestOutputs.size() - 2;
        while (i > 0) {
            Output next = latestOutputs.elementAt(i);
            if (next.getInput().getText().equals(last.getInput().getText())) {
                repeatCount++;
            } else {
                break;
            }
            i--;
        }
        return repeatCount;
    }
}
