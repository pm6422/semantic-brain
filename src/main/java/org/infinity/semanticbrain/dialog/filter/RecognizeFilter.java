package org.infinity.semanticbrain.dialog.filter;

import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public interface RecognizeFilter {

    enum TYPE {
        TYPE_IMMEDIATE_TERMINATE, TYPE_PARALLEL_COMPARING, TYPE_SERIAL_COMPARING
    }

    /**
     * Check whether it is a dialog context dialog
     *
     * @return true: dialog context dialog
     */
    boolean isContextFilter();

    /**
     * Get filter name
     *
     * @return filter name
     */
    String getName();

    /**
     * Get filter type
     *
     * @return filter type
     */
    TYPE getType();

    /**
     * For single thread
     *
     * @param input
     * @param output
     * @param lastOutput
     * @param skillCodes
     */
    void doFilter(final Input input, final Output output, final Output lastOutput, List<String> skillCodes) throws InterruptedException;

    /**
     * For multiple thread
     *
     * @param input
     * @param output
     * @param lastOutput
     * @param skillCodes
     * @param countDownLatch
     */
    void doFilter(final Input input, final Output output, final Output lastOutput, List<String> skillCodes, CountDownLatch countDownLatch) throws InterruptedException;

}
