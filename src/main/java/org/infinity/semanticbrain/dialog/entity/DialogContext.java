package org.infinity.semanticbrain.dialog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ApiModel("对话上下文")
public class DialogContext implements Serializable {

    private static final long         serialVersionUID = 6996205094278136766L;
    @ApiModelProperty("上次输出结果")
    private              Output       lastOutput;
    @ApiModelProperty("最近多次输出结果历史")
    private              List<Output> latestOutputs    = new CopyOnWriteArrayList<Output>();// Read more than write
    @ApiModelProperty("最近多次输出结果历史大小")
    private              int          latestOutputSize = 5;
    @ApiModelProperty("上下文对话有效时间秒数")
    private              int          keepAliveSeconds = 45;

    public DialogContext() {
    }

    public static DialogContext of(int latestOutputSize, int keepAliveSeconds) {
        DialogContext context = new DialogContext();
        context.setLatestOutputSize(latestOutputSize);
        context.setKeepAliveSeconds(keepAliveSeconds);
        return context;
    }

    public Output getLastOutput() {
        return lastOutput;
    }

    public void setLastOutput(Output lastOutput) {
        this.lastOutput = lastOutput;
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
        this.lastOutput = latestOutput;
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
        if (lastOutput == null || expired()) {
            return null;
        }
        return lastOutput;
    }

    /**
     * Check whether the dialog context is expired or not
     *
     * @return
     */
    public boolean expired() {
        return Instant.now().isAfter(lastOutput.getTime().plusSeconds(keepAliveSeconds));
    }

    /**
     * Check the latest dialog whether is recognized or not
     *
     * @return
     */
    public boolean recognized() {
        return lastOutput.recognized();
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
