package org.infinity.semanticbrain.dialog.filter;

public class ProcessFilter {
    private String name;
    private Long   elapsed; // Time unit in ms

    public static ProcessFilter of(String name, Long elapsed) {
        ProcessFilter processFilter = new ProcessFilter();
        processFilter.setName(name);
        processFilter.setElapsed(elapsed);
        return processFilter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getElapsed() {
        return elapsed;
    }

    public void setElapsed(Long elapsed) {
        this.elapsed = elapsed;
    }
}
