package org.infinity.semanticbrain.filter;

import org.infinity.semanticbrain.entity.Input;
import org.infinity.semanticbrain.entity.Output;

public interface SemanticFilterChain {

    /**
     * FilterChain接口的doFilter方法用于把请求交给Filter链中的下一个Filter去处理
     *
     * @param input
     * @param output
     * @param lastOutput
     */
    void doFilter(final Input input, final Output output, final Output lastOutput) throws InterruptedException;

}