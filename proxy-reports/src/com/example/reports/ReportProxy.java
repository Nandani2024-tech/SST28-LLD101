package com.example.reports;

/**
 * Proxy that controls access and lazy-loads the real report
 */
public class ReportProxy implements Report {

    private final String reportId;
    private final String title;
    private final String classification;

    private final AccessControl accessControl = new AccessControl();

    // Cached real subject
    private RealReport realReport;

    public ReportProxy(String reportId, String title, String classification) {
        this.reportId = reportId;
        this.title = title;
        this.classification = classification;
    }

    @Override
    public void display(User user) {

        // 1️⃣ Access check
        if (!accessControl.canAccess(user, classification)) {
            System.out.println("ACCESS DENIED -> user=" + user.getName()
                    + " role=" + user.getRole()
                    + " report=" + title);
            return;
        }

        // 2️⃣ Lazy loading
        if (realReport == null) {
            System.out.println("[proxy] creating RealReport for " + reportId);
            realReport = new RealReport(reportId, title, classification);
        }

        // 3️⃣ Delegate call
        realReport.display(user);
    }
}