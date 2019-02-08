package org.infinity.semanticbrain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * DialogContext class
 * <p>
 * Note: Serialization friendly class
 */
public class DialogContext implements Serializable {

    private List<Output> latestOutputs    = new CopyOnWriteArrayList<>();
    private int          stackSize        = 4;  // Context history size
    private int          keepAliveSeconds = 40; // Context life cycle

    public DialogContext(int stackSize, int keepAliveSeconds) {
        this.stackSize = stackSize;
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public void push(Output output) {
        if (latestOutputs.size() == stackSize) {
            latestOutputs.remove(0);
        }
        latestOutputs.add(output);
    }

    public Output pop() {
        Output output = this.peek();
        latestOutputs.remove(latestOutputs.size() - 1);
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
        Output output = latestOutputs.get(latestOutputs.size() - 1);
        if (LocalDateTime.now().minusSeconds(keepAliveSeconds).isAfter(output.getTime())) {
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
        Output last = latestOutputs.get(latestOutputs.size() - 1);
        int i = latestOutputs.size() - 2;
        while (i > 0) {
            Output next = latestOutputs.get(i);
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
