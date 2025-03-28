package com.phantoms.phantomsbackend.common.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class SearchResult {
    // getters & setters
    @Setter
    @Getter
    private float score;
    @Setter
    @Getter
    private String sheet;
    @JsonProperty("row_id")
    private int rowId;
    @JsonProperty("subrow_id")
    private Integer subrowId;
    @Setter
    @Getter
    private Object fields;
    @JsonProperty("transient")
    private Object transientFields;

    // 构造函数
    public SearchResult() {}

    @JsonProperty("row_id")
    public int getRowId() { return rowId; }
    @JsonProperty("row_id")
    public void setRowId(int rowId) { this.rowId = rowId; }

    @JsonProperty("subrow_id")
    public Integer getSubrowId() { return subrowId; }
    @JsonProperty("subrow_id")
    public void setSubrowId(Integer subrowId) { this.subrowId = subrowId; }

    @JsonProperty("transient")
    public Object getTransientFields() { return transientFields; }
    @JsonProperty("transient")
    public void setTransientFields(Object transientFields) { this.transientFields = transientFields; }

}
