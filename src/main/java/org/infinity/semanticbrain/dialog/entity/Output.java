package org.infinity.semanticbrain.dialog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;
import org.infinity.semanticbrain.dialog.filter.ProcessFilter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ApiModel("意图识别结果")
public class Output implements Serializable {

    public static final BigDecimal          TOP_SCORE = new BigDecimal(100);
    @ApiModelProperty("意图识别列表")
    private             List<Intent>        intents   = new ArrayList<Intent>();
    @ApiModelProperty("分数")
    private             BigDecimal          score     = new BigDecimal(0);
    @ApiModelProperty("识别过滤器列表")
    private             List<ProcessFilter> filters   = new ArrayList<ProcessFilter>();
    @ApiModelProperty("最终识别的过滤器")
    private             String              matchedFilter;
    @ApiModelProperty("用户输入信息")
    private             Input               input;
    @ApiModelProperty("状态信息")
    private             String              status;
    @ApiModelProperty("其他信息")
    private             Extra               extra;
    @ApiModelProperty("识别时间")
    private             Instant             time;
    @ApiModelProperty("耗费时间")
    private             Long                elapsed; // Time in milliseconds

    public List<Intent> getIntents() {
        return intents;
    }

    public void setIntents(List<Intent> intents) {
        this.intents = intents;
    }

    public void addIntention(Intent intent) {
        this.intents.add(intent);
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

    public String getMatchedFilter() {
        return matchedFilter;
    }

    public void setMatchedFilter(String matchedFilter) {
        this.matchedFilter = matchedFilter;
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
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

    //    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Long getElapsed() {
        return elapsed;
    }

    public void setElapsed(Long elapsed) {
        this.elapsed = elapsed;
    }

    public Intent getFirstIntention() {
        return CollectionUtils.isNotEmpty(intents) ? intents.get(0) : null;
    }

    // TODO
    public boolean isDetermined() {
        return false;
    }

    /**
     * Check the dialog whether is recognized or not
     *
     * @return
     */
    public boolean recognized() {
        return CollectionUtils.isNotEmpty(intents);
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
