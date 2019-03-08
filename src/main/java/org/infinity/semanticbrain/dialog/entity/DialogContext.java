package org.infinity.semanticbrain.dialog.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * DialogContext class
 * Note: Serialization friendly class
 */
public class DialogContext implements Serializable {

    private static final long         serialVersionUID = 6996205094278136766L;
    // Last time output
    private              Output       latestOutput;
    // Dialog context output histories
    private              List<Output> latestOutputs    = new CopyOnWriteArrayList<Output>();// Read more than write
    // Dialog context output history size
    private              int          latestOutputSize = 5;
    // Keep alive time for dialog context
    private              int          keepAliveSeconds = 45;

    public static DialogContext of(int latestOutputSize, int keepAliveSeconds) {
        DialogContext context = new DialogContext();
        context.setLatestOutputSize(latestOutputSize);
        context.setKeepAliveSeconds(keepAliveSeconds);
        return context;
    }

    public Output getLatestOutput() {
        return latestOutput;
    }

    public void setLatestOutput(Output latestOutput) {
        this.latestOutput = latestOutput;
    }

    public int getLatestOutputSize() {
        return latestOutputSize;
    }

    public void setLatestOutputSize(int latestOutputSize) {
        this.latestOutputSize = latestOutputSize;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    /**
     * Add last dialog context output
     *
     * @param latestOutput the latest dialog context output
     */
    public void addOutput(Output latestOutput) {
        this.latestOutput = latestOutput;
        latestOutputs.add(latestOutput);
        if (latestOutputs.size() > latestOutputSize) {
            latestOutputs.remove(0);
        }
    }

    /**
     * Get the latest output and return null if it is expired
     *
     * @return latest dialog context output
     */
    public Output getAliveOutput() {
        if (latestOutput == null || expired()) {
            return null;
        }
        return latestOutput;
    }

    /**
     * Check whether the dialog context is expired or not
     *
     * @return
     */
    public boolean expired() {
        return Instant.now().isAfter(latestOutput.getTime().plusSeconds(keepAliveSeconds));
    }

    /**
     * Check the latest dialog whether is recognized or not
     *
     * @return
     */
    public boolean recognized() {
        return latestOutput.recognized();
    }

    /**
     * Calculate repeat input times
     *
     * @return repeat times
     */
    public int getRepeatInputTimes() {
        if (latestOutputs.isEmpty()) {
            return 0;
        }

        int repeatOccurrences = 0;
        Output lastOne = latestOutputs.get(latestOutputs.size() - 1);
        for (int i = latestOutputs.size() - 2; i >= 0; ) {
            Output prev = latestOutputs.get(i);
            if (prev.getInput().getText().equals(lastOne.getInput().getText())) {
                repeatOccurrences++;
            } else {
                break;
            }
            i--;
        }
        return repeatOccurrences;
    }
}
