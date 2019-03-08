package org.infinity.semanticbrain.dialog.filter;

import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;

import java.util.concurrent.CountDownLatch;

public interface SemanticFilter {

    enum TYPE {
        TYPE_IMMEDIATE_TERMINATE, TYPE_PARALLEL_COMPARING, TYPE_SERIAL_COMPARING
    }

    boolean isContextFilter();

    String getName();

    TYPE getType();


    /**
     * For single thread
     *
     * @param input
     * @param output
     * @param lastOutput
     */
    void doFilter(final Input input, final Output output, final Output lastOutput) throws InterruptedException;

    /**
     * For multiple thread
     *
     * @param input
     * @param output
     * @param lastOutput
     * @param countDownLatch
     */
    void doFilter(final Input input, final Output output, final Output lastOutput, CountDownLatch countDownLatch) throws InterruptedException;

}
