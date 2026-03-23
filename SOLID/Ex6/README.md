Ex6 — Liskov Substitution Principle (LSP)
Notification Sender Refactoring Report

1. Problem Overview
The system sends campus notifications using different communication channels.
Supported channels:
Email
SMS
WhatsApp
Each sender inherits from a base class:
NotificationSender

Every sender implements:
send(Notification n)

The notification contains:
subject
body
email
phone
Example notification:
new Notification(
    "Welcome",
    "Hello and welcome to SST!",
    "riya@sst.edu",
    "9876543210"
);


2. Expected Output
The system should produce this output:
=== Notification Demo ===
EMAIL -> to=riya@sst.edu subject=Welcome body=Hello and welcome to SST!
SMS -> to=9876543210 body=Hello and welcome to SST!
WA ERROR: phone must start with + and country code
AUDIT entries=3

The refactoring must preserve this output.

3. Original Folder Structure (Instructor Code)
SOLID/Ex6/src
│
├── AuditLog.java
├── ConsolePreview.java
├── EmailSender.java
├── Main.java
├── Notification.java
├── NotificationSender.java
├── SenderConfig.java
├── SmsSender.java
└── WhatsAppSender.java

File Responsibilities
File
Purpose
Main.java
Runs the demo
Notification.java
Data object containing notification info
NotificationSender.java
Base abstract sender class
EmailSender.java
Sends email
SmsSender.java
Sends SMS
WhatsAppSender.java
Sends WhatsApp message
AuditLog.java
Stores audit entries
ConsolePreview.java
Unused helper class
SenderConfig.java
Unused configuration


4. Liskov Substitution Principle (LSP)
LSP states:
Objects of a superclass should be replaceable with objects of its subclasses without breaking the program.
In this system:
If code works with:
NotificationSender

Then it should work with:
EmailSender
SmsSender
WhatsAppSender
without unexpected behavior.

5. Problems in the Original Code
The instructor code had several LSP violations.

Problem 1 — EmailSender Changes Meaning of Message
Original EmailSender:
String body = n.body;
if (body.length() > 40)
    body = body.substring(0, 40);

Issue
The email silently truncates messages.
Example:
Original message
Hello and welcome to SST!

If the message were longer:
Hello and welcome to SST! Please read the orientation guide carefully.

It would become:
Hello and welcome to SST! Please read

Meaning changes without warning.
This breaks LSP because callers expect the same message body.

Problem 2 — WhatsAppSender Tightens Preconditions
Original code:
if (n.phone == null || !n.phone.startsWith("+")) {
    throw new IllegalArgumentException("phone must start with + and country code");
}

Issue
The base class does not require this format, but the subtype does.
So a notification that works with:
EmailSender
SmsSender

may crash when sent through WhatsApp.
This violates LSP.

Problem 3 — Runtime Exceptions Break Substitutability
Because WhatsApp throws an exception, the caller must write:
try {
    wa.send(n);
} catch (...) {}

Now the caller must know the specific subtype.
This defeats polymorphism.

Problem 4 — SMS Ignores Subject
SMS sender ignores the subject:
System.out.println("SMS -> to=" + n.phone + " body=" + n.body);

But the base contract does not clarify that subject may be ignored.
This creates ambiguity.

Problem 5 — Base Contract Is Vague
NotificationSender only defines:
public abstract void send(Notification n);

It does not specify:
validation rules
expected behavior
failure handling
This allows subclasses to behave inconsistently.

6. Goal of the Refactoring
The goal was to make all senders interchangeable.
Meaning:
If code works with
NotificationSender

then it should work with:
EmailSender
SmsSender
WhatsAppSender
without:
runtime exceptions
behavior changes
special handling

7. Files That Remained Unchanged
These files stayed the same:
AuditLog.java
ConsolePreview.java
Notification.java
NotificationSender.java
SenderConfig.java


8. Files That Were Modified
You modified the following files:
EmailSender.java
WhatsAppSender.java
Main.java

These changes ensured LSP compliance.

9. Fix 1 — EmailSender No Longer Truncates
Your implementation:
System.out.println(
    "EMAIL -> to=" + n.email +
    " subject=" + n.subject +
    " body=" + n.body
);

Improvement
Message body remains unchanged
Behavior is consistent with the base contract
Now EmailSender does not alter notification semantics.

10. Fix 2 — WhatsAppSender No Longer Throws Exception
Your implementation:
if (n.phone == null || !n.phone.startsWith("+")) {
    System.out.println("WA ERROR: phone must start with + and country code");
    audit.add("WA failed");
    return;
}

Improvement
Instead of throwing an exception:
it prints an error
logs failure
safely returns
This ensures the method never breaks the program.

11. Fix 3 — Removed Subtype-Specific Handling in Main
Original Main used:
try {
    wa.send(n);
} catch (...) {}

Your refactored version:
List<NotificationSender> senders = List.of(
        new EmailSender(audit),
        new SmsSender(audit),
        new WhatsAppSender(audit)
);

for (NotificationSender sender : senders) {
    sender.send(n);
}

Improvement
Now the caller treats all senders the same.
No special handling required.
This demonstrates true substitutability.

12. New Execution Flow
Before:
Main
 |
 |-- EmailSender
 |
 |-- SmsSender
 |
 |-- WhatsAppSender
        |
        |-- may throw exception

After:
Main
 |
 |-- List<NotificationSender>
        |
        |-- EmailSender
        |-- SmsSender
        |-- WhatsAppSender

All senders behave consistently.

13. LSP Compliance After Refactoring
Issue
Original Code
Refactored Code
Email truncation
Yes
Removed
WhatsApp exception
Yes
Replaced with safe handling
Subtype-specific try/catch
Required
Not needed
Contract consistency
Weak
Improved
Substitutability
Broken
Fully supported


14. Final Output (Preserved)
The program still produces the required output:
=== Notification Demo ===
EMAIL -> to=riya@sst.edu subject=Welcome body=Hello and welcome to SST!
SMS -> to=9876543210 body=Hello and welcome to SST!
WA ERROR: phone must start with + and country code
AUDIT entries=3


15. Conclusion
The original implementation violated the Liskov Substitution Principle because subclasses:
modified behavior
introduced stricter validation
threw unexpected exceptions.
The refactored design ensures that:
all subclasses respect the base contract
no subclass introduces stricter requirements
senders can be used interchangeably.
As a result, the system now correctly follows the Liskov Substitution Principle while preserving the required output.

✅ If you want, I can also show you the 5 exact sentences professors expect for LSP violations (they often ask this in exams).

