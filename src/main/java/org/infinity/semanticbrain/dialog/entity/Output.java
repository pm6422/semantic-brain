package org.infinity.semanticbrain.dialog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.infinity.semanticbrain.dialog.filter.ProcessFilter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ApiModel("意图识别结果")
public class Output implements Serializable {

    public static final BigDecimal          TOP_SCORE     = new BigDecimal(100);
    @ApiModelProperty("用户输入信息")
    private             Input               input;
    @ApiModelProperty("意图识别列表")
    private             List<Intent>        intents       = new ArrayList<Intent>();
    @ApiModelProperty("分数")
    private             BigDecimal          score         = new BigDecimal(0);
    @ApiModelProperty("技能代码")
    private             List<String>        skillCodes    = new ArrayList<String>();
    @ApiModelProperty("识别过滤器列表")
    private             List<ProcessFilter> filters       = new ArrayList<ProcessFilter>();
    @ApiModelProperty("最终识别的过滤器")
    private             String              matchedFilter = "";
    @ApiModelProperty("状态信息")
    private             String              status        = "";
    @ApiModelProperty("创建时间")
    private             Instant             time;
    @ApiModelProperty("上下文对话有效时间秒数")
    private             Integer             keepAliveSeconds;
    @ApiModelProperty("技能模式下上下文对话有效时间秒数")
    private             Integer             skillModeKeepAliveSeconds;
    @ApiModelProperty("耗费时间")
    private             Long                elapsed; // Time in ms
    @ApiModelProperty("其他信息")
    private             Extra               extra;


    public Output() {
    }

    public Output(Input input, List<Intent> intents, BigDecimal score) {
        this.input = input;
        this.intents = intents;
        this.score = score;
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public List<Intent> getIntents() {
        return intents;
    }

    public void setIntents(List<Intent> intents) {
        this.intents = intents;
    }

    public void addIntent(Intent intent) {
        this.intents.add(intent);
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public List<String> getSkillCodes() {
        return skillCodes;
    }

    public void setSkillCodes(List<String> skillCodes) {
        this.skillCodes = skillCodes;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Integer getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(Integer keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public Integer getSkillModeKeepAliveSeconds() {
        return skillModeKeepAliveSeconds;
    }

    public void setSkillModeKeepAliveSeconds(Integer skillModeKeepAliveSeconds) {
        this.skillModeKeepAliveSeconds = skillModeKeepAliveSeconds;
    }

    public Long getElapsed() {
        return elapsed;
    }

    public void setElapsed(Long elapsed) {
        this.elapsed = elapsed;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }


    /**
     * Check whether the dialog is recognized or not
     *
     * @return
     */
    public boolean recognized() {
        return CollectionUtils.isNotEmpty(intents);
    }

    /**
     * Check whether the dialog is recognized and all required slots got.
     *
     * @return
     */
    public boolean isDetermined() {
        return this.recognized() && intents.stream().allMatch(x -> x.isDetermined());
    }

    /**
     * Check whether the dialog context is expired or not
     *
     * @return true: expired, false: not yet expired
     */
    public boolean expired() {
        return keepAliveSeconds != null && Instant.now().isAfter(time.plusSeconds(keepAliveSeconds));
    }

    /**
     * Check whether the dialog context is expired or not
     *
     * @return true: expired, false: not yet expired
     */
    public boolean skillModeExpired() {
        return skillModeKeepAliveSeconds != null && Instant.now().isAfter(time.plusSeconds(skillModeKeepAliveSeconds));
    }

    public Intent findOneIntent() {
        return this.recognized() ? intents.get(0) : null;
    }

    public static class Extra {
        private String host;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }
    }
}
