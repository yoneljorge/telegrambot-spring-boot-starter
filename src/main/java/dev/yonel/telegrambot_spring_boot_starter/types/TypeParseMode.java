package dev.yonel.telegrambot_spring_boot_starter.types;

public enum TypeParseMode {

    MARKDOWN("Markdown"),
    MARKDOWNV2("MarkdownV2"),
    HTML("HTML");

    private String value;

    TypeParseMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
