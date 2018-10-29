package org.infinity.semanticbrain.entity;

public class ProcessFilter {
    private String name;
    private Long   elapsed; // Time in ms


    public ProcessFilter(String name, Long elapsed) {
        this.name = name;
        this.elapsed = elapsed;
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
