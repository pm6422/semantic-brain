package org.infinity.semanticbrain.entity;

import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Output implements Serializable {

    public static final BigDecimal          TOP_SCORE  = new BigDecimal(100);
    private             List<Intention>     intentions = new ArrayList<>();
    private             BigDecimal          score      = new BigDecimal(0);
    private             List<ProcessFilter> filters    = new ArrayList<>();
    private             Input               input;
    private             Instant             time;
    private             String              status;
    private             Extra               extra;

    public List<Intention> getIntentions() {
        return intentions;
    }

    public void setIntentions(List<Intention> intentions) {
        this.intentions = intentions;
    }

    public void addIntention(Intention intention) {
        this.intentions.add(intention);
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public List<ProcessFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<ProcessFilter> filters) {
        this.filters = filters;
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }

    public ProcessFilter getLastFilter() {
        return CollectionUtils.isNotEmpty(filters) ? filters.get(filters.size() - 1) : null;
    }

    public Intention getFirstIntention() {
        return CollectionUtils.isNotEmpty(intentions) ? intentions.get(0) : null;
    }

    public boolean isRecognized() {
        return CollectionUtils.isNotEmpty(intentions);
    }


    public static class Extra {
        private String source;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }
}
