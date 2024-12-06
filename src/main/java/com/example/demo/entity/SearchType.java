package com.example.demo.entity;

public enum SearchType {
    AUTHOR("author"),
    TITLE("title"),
    TITLE_AND_CONTENT("title,contents"),
    CONTENT("contents");

    private final String fields;

    SearchType(String fields) {
        this.fields = fields;
    }

    public String[] getFields() {
        return fields.split(",");
    }
}
