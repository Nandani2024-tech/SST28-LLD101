package com.example.tickets;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable IncidentTicket with Builder.
 */
public final class IncidentTicket {

    private final String id;
    private final String reporterEmail;
    private final String title;

    private final String description;
    private final String priority;
    private final List<String> tags;
    private final String assigneeEmail;
    private final boolean customerVisible;
    private final Integer slaMinutes;
    private final String source;

    private IncidentTicket(Builder b) {
        this.id = b.id;
        this.reporterEmail = b.reporterEmail;
        this.title = b.title;
        this.description = b.description;
        this.priority = b.priority;
        this.assigneeEmail = b.assigneeEmail;
        this.customerVisible = b.customerVisible;
        this.slaMinutes = b.slaMinutes;
        this.source = b.source;
        this.tags = List.copyOf(b.tags); // immutable defensive copy
    }

    // Getters (safe)
    public String getId() { return id; }
    public String getReporterEmail() { return reporterEmail; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }
    public List<String> getTags() { return tags; }
    public String getAssigneeEmail() { return assigneeEmail; }
    public boolean isCustomerVisible() { return customerVisible; }
    public Integer getSlaMinutes() { return slaMinutes; }
    public String getSource() { return source; }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    public String toString() {
        return "IncidentTicket{" +
                "id='" + id + '\'' +
                ", reporterEmail='" + reporterEmail + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", tags=" + tags +
                ", assigneeEmail='" + assigneeEmail + '\'' +
                ", customerVisible=" + customerVisible +
                ", slaMinutes=" + slaMinutes +
                ", source='" + source + '\'' +
                '}';
    }

    // ===================== BUILDER =====================
    public static class Builder {

        private String id;
        private String reporterEmail;
        private String title;

        private String description;
        private String priority;
        private List<String> tags = new ArrayList<>();
        private String assigneeEmail;
        private boolean customerVisible;
        private Integer slaMinutes;
        private String source;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder reporterEmail(String reporterEmail) {
            this.reporterEmail = reporterEmail;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder priority(String priority) {
            this.priority = priority;
            return this;
        }

        public Builder tags(List<String> tags) {
            if (tags != null) {
                this.tags = new ArrayList<>(tags);
            }
            return this;
        }

        public Builder addTag(String tag) {
            if (tag != null) this.tags.add(tag);
            return this;
        }

        public Builder assigneeEmail(String assigneeEmail) {
            this.assigneeEmail = assigneeEmail;
            return this;
        }

        public Builder customerVisible(boolean customerVisible) {
            this.customerVisible = customerVisible;
            return this;
        }

        public Builder slaMinutes(Integer slaMinutes) {
            this.slaMinutes = slaMinutes;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder from(IncidentTicket t) {
            this.id = t.id;
            this.reporterEmail = t.reporterEmail;
            this.title = t.title;
            this.description = t.description;
            this.priority = t.priority;
            this.tags = new ArrayList<>(t.tags);
            this.assigneeEmail = t.assigneeEmail;
            this.customerVisible = t.customerVisible;
            this.slaMinutes = t.slaMinutes;
            this.source = t.source;
            return this;
        }

        public IncidentTicket build() {

            Validation.requireTicketId(id);
            Validation.requireEmail(reporterEmail, "reporterEmail");

            Validation.requireNonBlank(title, "title");
            Validation.requireMaxLen(title, 80, "title");

            Validation.requireOptionalEmail(assigneeEmail, "assigneeEmail");

            Validation.requireOneOf(priority, "priority",
                    "LOW", "MEDIUM", "HIGH", "CRITICAL");

            Validation.requireRange(slaMinutes, 5, 7200, "slaMinutes");

            return new IncidentTicket(this);
        }
    }
}