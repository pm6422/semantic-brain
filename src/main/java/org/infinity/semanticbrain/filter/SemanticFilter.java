package org.infinity.semanticbrain.filter;

import org.infinity.semanticbrain.entity.Input;
import org.infinity.semanticbrain.entity.Output;

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
     * @return output
     */
    Output doFilter(final Input input, final Output output, final Output lastOutput);

    /**
     * For multiple thread
     *
     * @param input
     * @param output
     * @param lastOutput
     * @param countDownLatch
     * @return output
     */
    Output doFilter(final Input input, final Output output, final Output lastOutput, CountDownLatch countDownLatch);

}
