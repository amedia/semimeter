package org.semispace.semimeter.bean;

public class GroupedSumsQuery {
    private String key;
    private Long startAt;
    private Long endAt;
    private Integer maxResults;
    private TokenizedPathInfo query;
    private String resolution;

    public GroupedSumsQuery() {
        //default constructor
    }

    public GroupedSumsQuery(final String resolution, final long startAt, final long endAt, final int maxResults, final TokenizedPathInfo query) {
        this.resolution = resolution;
        this.startAt = startAt;
        this.endAt = endAt;
        this.maxResults = maxResults;
        this.query = query;
        this.key = query.buildPathFromTokens() + "_" + resolution + "_" + maxResults;
    }

    public String getResolution() {
        return resolution;
    }


    public Long getStartAt() {
        return startAt;
    }


    public Long getEndAt() {
        return endAt;
    }


    public Integer getMaxResults() {
        return maxResults;
    }


    public TokenizedPathInfo getQuery() {
        return query;
    }


    public String getKey() {
        return key;
    }

}
