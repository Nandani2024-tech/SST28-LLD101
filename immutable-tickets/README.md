Incident Ticket System Refactoring Report
Applying Immutability and Builder Pattern

1. Introduction
The given system is designed to manage incident tickets in a support or monitoring environment.
Each ticket contains information such as:
Ticket ID
Reporter email
Title
Description
Priority
Tags
Assignee
SLA time
Source
The instructor intentionally provided a broken design to demonstrate problems caused by mutable objects and poor object construction.
The objective of this task was to:
Refactor the ticket system to use immutable objects
Implement the Builder Pattern
Prevent external modification of internal data
Centralize validation logic

2. Original Project Structure
The instructor provided the following project structure:
src
│
├── com.example.tickets
│   ├── IncidentTicket.java
│   ├── TicketService.java
│   └── Validation.java
│
└── TryIt.java

File Responsibilities
File
Purpose
IncidentTicket
Represents a ticket object
TicketService
Handles ticket creation and updates
Validation
Contains helper methods for validation
TryIt
Demonstration program to test the system


3. Problems in the Original Code
The original implementation contained several design issues.

3.1 Mutable Ticket Object
In the original design, ticket fields were mutable:
private String id;
private String reporterEmail;
private String title;

The class also provided public setters:
public void setPriority(String priority)
public void setAssigneeEmail(String assigneeEmail)

This allowed the ticket to be modified after creation.
Example:
t.setPriority("CRITICAL");

Why this is a problem
In real incident systems, tickets should behave like records or logs.
Once created, their state should not change unexpectedly.
Mutable objects cause:
inconsistent data
hard-to-track changes
poor auditability

3.2 External Modification of Internal Data
The original code returned the internal tags list directly:
public List<String> getTags() { return tags; }

This exposes internal data.
Example from TryIt:
List<String> tags = t.getTags();
tags.add("HACKED_FROM_OUTSIDE");

This modifies the ticket without using the service layer, which breaks encapsulation.

3.3 Multiple Constructors
The original class had several constructors:
IncidentTicket()
IncidentTicket(String id, String reporterEmail, String title)
IncidentTicket(String id, String reporterEmail, String title, String priority)

This causes constructor explosion, where too many combinations become difficult to manage.
It also allows creation of partially valid objects.
Example:
new IncidentTicket()

creates an empty ticket without required fields.

3.4 Scattered Validation
Validation logic was partially placed in TicketService.
Example:
if (id == null || id.trim().isEmpty())

But other validations were missing or inconsistent.
This leads to duplicate validation logic across the system.

3.5 Service Mutating Objects
The service layer was modifying objects after creation.
Example:
t.setPriority("MEDIUM");
t.setSource("CLI");
t.setCustomerVisible(false);

And later:
t.setPriority("CRITICAL");

This makes the ticket change state over time, which is risky for systems that require reliable history.

4. Refactored Design (My Solution)
To fix the problems, the ticket class was redesigned to be:
Immutable
Created using the Builder Pattern
Protected against external modification
Validated during object construction

5. New Design Overview
src
│
├── com.example.tickets
│   ├── IncidentTicket (immutable + builder)
│   ├── TicketService (returns new ticket instances)
│   └── Validation
│
└── TryIt


6. Making the Ticket Immutable
All fields were changed to final.
Example:
private final String id;
private final String reporterEmail;
private final String title;

The constructor was also made private.
private IncidentTicket(Builder b)

This prevents objects from being created directly.
Instead, tickets must be created through the Builder.

7. Implementing the Builder Pattern
The Builder Pattern allows flexible object construction.
Example usage:
IncidentTicket ticket =
    IncidentTicket.builder()
        .id("TCK-1001")
        .reporterEmail("reporter@example.com")
        .title("Payment failing")
        .priority("MEDIUM")
        .source("CLI")
        .addTag("NEW")
        .build();

Why Builder is better
Builder solves several problems:
avoids constructor overload
improves readability
ensures object is valid before creation

8. Preventing List Mutation
To prevent external modification of tags, a defensive copy is used.
this.tags = List.copyOf(b.tags);

This creates an immutable list.
Now external code cannot modify it.
Example from TryIt:
tags.add("HACKED_FROM_OUTSIDE");

This now throws an exception.
Output:
Tags are immutable from outside ✔


9. Centralized Validation
All validation rules are applied during build().
Example:
Validation.requireTicketId(id);
Validation.requireEmail(reporterEmail, "reporterEmail");
Validation.requireNonBlank(title, "title");

This ensures that invalid tickets cannot be created.

10. Refactoring TicketService
The service layer was modified so that it no longer mutates objects.
Instead, it returns new ticket instances.

Before (Mutable)
t.setPriority("CRITICAL");


After (Immutable)
return t.toBuilder()
        .priority("CRITICAL")
        .addTag("ESCALATED")
        .build();

This creates a new ticket instance, preserving the original ticket.

11. Using toBuilder() for Updates
The method:
public Builder toBuilder()

creates a builder initialized with the existing ticket's data.
Example:
IncidentTicket t2 = service.assign(t1, "agent@example.com");

t1 remains unchanged.

12. Program Execution Example
Output example:
Created: IncidentTicket{...}

Original remains unchanged: IncidentTicket{...}

After assign: IncidentTicket{assigneeEmail='agent@example.com'}

After escalation: IncidentTicket{priority='CRITICAL', tags=[NEW, ESCALATED]}

Tags are immutable from outside ✔

This demonstrates that:
original tickets remain unchanged
updates create new objects
internal lists cannot be modified externally

13. Advantages of the Refactored Design
1. Immutable Objects
Tickets cannot change state after creation.

2. Safe Data Access
Internal collections cannot be modified externally.

3. Clear Object Construction
Builder pattern makes ticket creation readable and flexible.

4. Centralized Validation
All validation occurs during object construction.

5. Improved Maintainability
Future fields can be added easily without breaking constructors.

6. Better Reliability
Since objects do not change unexpectedly, the system becomes more predictable.

14. Conclusion
The original implementation suffered from several design problems, including:
mutable ticket objects
data leakage through internal lists
scattered validation
complex constructors
service methods mutating objects
The refactored design solves these issues by:
making IncidentTicket immutable
introducing the Builder Pattern
preventing external modification of internal data
centralizing validation logic
returning new objects instead of modifying existing ones
These improvements lead to a system that is safer, easier to maintain, and better aligned with modern object-oriented design principles.

