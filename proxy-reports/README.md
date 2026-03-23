CampusVault Report Access System Refactoring Report
Applying the Proxy Design Pattern

1. Introduction
The provided system simulates a secure report access platform called CampusVault.
The system stores different types of reports such as:
Public reports
Faculty-only reports
Admin-only reports
Each report contains confidential data and is stored on disk. Loading the report from disk is an expensive operation and should not be performed unnecessarily.
The instructor intentionally provided a broken implementation that lacks proper security and efficiency.
The goal of this task was to refactor the system by implementing the Proxy Design Pattern to achieve:
Access control (only authorized users can open reports)
Lazy loading (load reports only when needed)
Caching (avoid repeated disk loading)
Better architecture using interfaces

2. Original Project Structure
The instructor provided the following structure:
src
│
└── com.example.reports
    │
    ├── AccessControl.java
    ├── App.java
    ├── QuickCheck.java
    ├── Report.java
    ├── ReportFile.java
    ├── ReportProxy.java
    ├── RealReport.java
    ├── ReportViewer.java
    └── User.java


3. Description of Each Class
Class
Responsibility
User
Represents a user with a name and role
AccessControl
Determines if a user can access a specific report
Report
Interface defining the report behavior
ReportFile
Original implementation that loads reports from disk
RealReport
The actual report object responsible for loading data
ReportProxy
Controls access and lazy loads the real report
ReportViewer
Client class that opens reports
App
Demonstration program
QuickCheck
Test program to verify proxy behavior


4. Problems in the Original Code
The original implementation had several design issues.

4.1 No Access Control
Any user could open any report.
Example:
viewer.open(adminReport, student);

This means a student could access an admin-only report, which is a security risk.

4.2 Expensive Disk Loading on Every Call
The original ReportFile class loads the report from disk every time the report is displayed.
Example:
String content = loadFromDisk();

The loadFromDisk() method simulates a slow disk operation:
[disk] loading report R-303 ...

If the same report is opened multiple times, the system repeatedly loads the same data, which wastes resources.

4.3 No Lazy Loading
Reports are loaded immediately when display() is called.
This means the system performs expensive operations even when unnecessary.

4.4 Client Depends on Concrete Class
The ReportViewer originally depended on the concrete class:
ReportFile

Instead of using the interface:
Report

This creates tight coupling and reduces flexibility.

5. Design Goal of the Refactor
To solve these problems, the Proxy Pattern was introduced.
The proxy acts as a middle layer between the client and the real object.
Architecture after refactoring:
Client
   ↓
ReportProxy
   ↓
RealReport

The proxy performs:
Access control
Lazy loading
Caching
Delegation to the real object

6. Changes Made in the Refactored Solution
Several modifications were made to implement the Proxy Pattern correctly.

6.1 Implementing the Real Subject (RealReport)
The expensive disk loading logic was moved into the RealReport class.
Example:
private String loadFromDisk()

The report content is loaded only once during object creation:
this.content = loadFromDisk();

This ensures the disk operation occurs only once per report instance.

6.2 Implementing the Proxy (ReportProxy)
The proxy class performs three main responsibilities.

1️⃣ Access Control
Before allowing access, the proxy checks user permissions.
Example:
if (!accessControl.canAccess(user, classification))

If the user is not authorized, access is denied.
Example output:
ACCESS DENIED -> user=Jasleen role=STUDENT report=Midterm Review


2️⃣ Lazy Loading
The real report object is created only when it is needed.
Example:
if (realReport == null)

This means the expensive disk operation happens only on the first access.
Example output:
[proxy] creating RealReport for R-303
[disk] loading report R-303 ...


3️⃣ Caching
Once the RealReport is created, the proxy stores it.
private RealReport realReport;

Subsequent calls reuse the same object without loading from disk again.

6.3 Refactoring the Viewer
The ReportViewer class was updated to depend on the interface instead of the concrete class.
Before:
public void open(ReportFile report, User user)

After:
public void open(Report report, User user)

This allows the viewer to work with either:
ReportProxy
RealReport
without changing its code.

6.4 Updating the Application Setup
Reports are now created using the proxy instead of the concrete class.
Example:
Report publicReport = new ReportProxy("R-101", "Orientation Plan", "PUBLIC");

This ensures all report access goes through the proxy.

7. Example Execution
Example execution from App:
viewer.open(publicReport, student);
viewer.open(facultyReport, student);
viewer.open(facultyReport, faculty);
viewer.open(adminReport, admin);
viewer.open(adminReport, admin);

Expected output behavior:
Public Report
[proxy] creating RealReport for R-101
[disk] loading report R-101 ...
REPORT -> ...


Unauthorized Access
ACCESS DENIED -> user=Jasleen role=STUDENT report=Midterm Review


Admin Report (First Access)
[proxy] creating RealReport for R-303
[disk] loading report R-303 ...


Admin Report (Second Access)
REPORT -> id=R-303 ...

No disk loading occurs because the report was cached.

8. Advantages of the New Design
1. Improved Security
Unauthorized users cannot access restricted reports.

2. Better Performance
Reports are loaded only once instead of every time.

3. Lazy Loading
The system loads report data only when required.

4. Clean Architecture
Clients depend on the Report interface rather than concrete implementations.

5. Scalability
New report types or additional proxies can be added easily without changing client code.

9. Final Design Architecture
User
  ↓
ReportViewer
  ↓
Report (interface)
  ↓
ReportProxy
  ↓
RealReport


10. Conclusion
The original system had several problems:
no access control
repeated expensive disk operations
tight coupling with concrete classes
By implementing the Proxy Pattern, the system now:
enforces security checks
lazily loads reports
caches report data
follows better object-oriented design principles
This refactoring results in a secure, efficient, and maintainable report access system.



File / Class
Responsibility
User
Represents a system user. It stores user information such as the user name and role (e.g., STUDENT, FACULTY, ADMIN).
AccessControl
Handles authorization logic. It checks whether a particular user role is allowed to access a specific report classification.
Report (Interface)
Defines the common behavior for all report types. Both the real report and the proxy implement this interface so the client can interact with them in the same way.
RealReport
The actual report object that loads the report data from disk. This is the expensive operation that the system tries to avoid unless necessary.
ReportProxy
Acts as an intermediary between the client and the real report. It performs access control, lazy loading, and caching before delegating the request to the real report.
ReportViewer
The client class that opens and displays reports. It interacts with the Report interface rather than a concrete class.
App
The main application that demonstrates the system by creating users and reports and simulating report access.
QuickCheck
A testing class used to quickly verify that the proxy behavior works correctly, such as access control and lazy loading.


