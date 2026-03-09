package com.example.reports;

/**
 * Viewer now depends on the abstraction (Report)
 */
public class ReportViewer {

    public void open(Report report, User user) {
        report.display(user);
    }
}