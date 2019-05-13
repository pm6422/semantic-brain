package org.infinity.semanticbrain.dialog.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.infinity.semanticbrain.dialog.entity.Output;

import java.io.Serializable;
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

    /**
     * Used to serialize
     */
    public DialogContext() {
    }

    public static DialogContext of(int latestOutputSize) {
        DialogContext context = new DialogContext();
        context.setLatestOutputSize(latestOutputSize);
        return context;
    }

    public Output getLastOutput() {
        return lastOutput;
    }

    public void setLastOutput(Output lastOutput) {
        this.lastOutput = lastOutput;
    }

    public List<Output> getLatestOutputs() {
        return latestOutputs;
    }

    public void setLatestOutputs(List<Output> latestOutputs) {
        this.latestOutputs = latestOutputs;
    }

    public int getLatestOutputSize() {
        return latestOutputSize;
    }

    public void setLatestOutputSize(int latestOutputSize) {
        this.latestOutputSize = latestOutputSize;
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
            if (prev.getInput().getOriginalText().equals(lastOne.getInput().getOriginalText())) {
                repeatOccurrences++;
            } else {
                break;
            }
            i--;
        }
        return repeatOccurrences;
    }
}
