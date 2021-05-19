package org.infinity.semanticbrain.dialog.filter;

import lombok.Data;

@Data
public class ProcessFilter {
    /**
     * Filter name
     */
    private String name;
    /**
     * Time unit in milliseconds
     */
    private Long   elapsed;

    public static ProcessFilter of(String name, Long elapsed) {
        ProcessFilter processFilter = new ProcessFilter();
        processFilter.setName(name);
        processFilter.setElapsed(elapsed);
        return processFilter;
    }
}
