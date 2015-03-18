package org.motechproject.nyvrs.domain;

public enum CampaignType {
    KIKI("Set1"),
    RONALD("Set2"),
    RITA("Set3"),
    NOT_SET("Set0");

    private String value;

    private CampaignType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
