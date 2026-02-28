package io.yefangwong.guard.core.model;

public class RelationMetadata {
    private String sourceColumn;
    private String targetTable;
    private String targetColumn;

    public RelationMetadata() {}

    public String getSourceColumn() { return sourceColumn; }
    public void setSourceColumn(String sourceColumn) { this.sourceColumn = sourceColumn; }
    public String getTargetTable() { return targetTable; }
    public void setTargetTable(String targetTable) { this.targetTable = targetTable; }
    public String getTargetColumn() { return targetColumn; }
    public void setTargetColumn(String targetColumn) { this.targetColumn = targetColumn; }
}
