package org.infinity.semanticbrain.dialog.filter;

import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;

import java.util.List;

public interface SemanticFilterChain {

    /**
     * FilterChain接口的doFilter方法用于把请求交给Filter链中的下一个Filter去处理
     *
     * @param input
     * @param output
     * @param lastOutput
     * @param skillCodes
     */
    void doFilter(final Input input, final Output output, final Output lastOutput, List<String> skillCodes);

}