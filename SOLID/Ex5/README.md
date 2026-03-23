
Export System Refactoring Report
Applying Liskov Substitution Principle (LSP)
1. Introduction
The given system is designed to export content into different formats such as PDF, CSV, and JSON. The program takes a request containing a title and body, then different exporter classes convert this data into the required format.
However, the original implementation had design issues and violations of the Liskov Substitution Principle (LSP).
The goal of this task was to identify those issues and refactor the design so that all exporters behave consistently and follow a clear contract.

2. Original Folder / Class Structure
The instructor provided the following classes:
Exporter (abstract base class)

├── CsvExporter
├── JsonExporter
├── PdfExporter

ExportRequest
ExportResult
SampleData
Main

Class Responsibilities
Class
Purpose
Exporter
Abstract base class defining export behavior
CsvExporter
Converts content into CSV format
JsonExporter
Converts content into JSON format
PdfExporter
Converts content into PDF format
ExportRequest
Stores input data (title and body)
ExportResult
Stores output data (content type + bytes)
SampleData
Provides test data
Main
Runs the export demo


3. Issues in the Original Code
3.1 No Enforced Contract in Exporter
The Exporter class had this method:
public abstract ExportResult export(ExportRequest req);

The problem is that each subclass implemented the method differently, which created inconsistent behavior.
Example differences:
Exporter
Behavior
CSV
Accepts null body and replaces characters
JSON
Returns empty result if request is null
PDF
Throws exception if body > 20 characters

This means the same method behaves differently depending on the subclass, which violates LSP.

3.2 Liskov Substitution Principle (LSP) Violation
What is LSP?
The Liskov Substitution Principle states:
Objects of a superclass should be replaceable with objects of its subclasses without breaking the program.
In simple terms:
If code expects an Exporter, it should work the same way whether it receives:
CsvExporter
JsonExporter
PdfExporter

Example of the Problem
In the main program:
Exporter pdf = new PdfExporter();
Exporter csv = new CsvExporter();
Exporter json = new JsonExporter();

All are treated as Exporter.
But:
Exporter
Result
CSV
Works
JSON
Works
PDF
Throws exception if body > 20

So the program cannot safely substitute exporters, which breaks LSP.

3.3 Inconsistent Null Handling
Different exporters handled null values differently:
Exporter
Null Request Handling
CSV
Converts null body to empty
JSON
Returns empty JSON
PDF
Might throw exception

This inconsistency leads to unpredictable behavior.

4. Refactored Design (My Solution)
To fix the issues, the design was changed using the Template Method Pattern.
The key idea:
Move common validation logic to the base class and let subclasses only handle encoding logic.

5. New Design Structure
Exporter (template method)

├── CsvExporter
├── JsonExporter
├── PdfExporter

ExportRequest
ExportResult
SampleData
Main

The main change is inside the Exporter class.

6. Key Change: Template Method in Exporter
The new Exporter class now controls the export process.
public final ExportResult export(ExportRequest req)

This method now:
Validates the request
Normalizes null values
Calls subclass logic
public final ExportResult export(ExportRequest req) {
    if (req == null) {
        throw new IllegalArgumentException("request cannot be null");
    }

    String title = req.title == null ? "" : req.title;
    String body = req.body == null ? "" : req.body;

    return doExport(new ExportRequest(title, body));
}


Why This Fix Works
Now:
All exporters receive valid and normalized input
Subclasses cannot override validation
Behavior is consistent

7. New Abstract Method for Subclasses
Subclasses now implement:
protected abstract ExportResult doExport(ExportRequest normalizedReq);

This ensures subclasses only focus on format encoding.

8. Changes in Each Exporter
8.1 CSV Exporter
protected ExportResult doExport(ExportRequest req)

It now only handles CSV formatting.
Example transformation:
Input:
Name,Score
Ayaan,82

Output CSV body:
Name Score Ayaan 82

Special characters like commas and line breaks are replaced to maintain CSV structure.

8.2 JSON Exporter
JSON exporter now only builds the JSON string.
String json = "{\"title\":\"" + escape(req.title) +
              "\",\"body\":\"" + escape(req.body) + "\"}";

The escape method prevents invalid JSON formatting.
Example:
Input title:
Report "Weekly"

Output:
"Report \"Weekly\""


8.3 PDF Exporter
The PDF exporter still has a limitation:
if (req.body.length() > 20)

But now the request is already validated and normalized, so behavior is consistent across exporters.

9. Example Program Execution
Input request:
Title: Weekly Report
Body:
Name,Score
Ayaan,82
Riya,91

Output example:
=== Export Demo ===

PDF: ERROR: PDF cannot handle content > 20 chars
CSV: OK bytes=45
JSON: OK bytes=60

Each exporter processes the same normalized request.

10. Advantages of the Refactored Design
1. LSP Compliance
All exporters follow the same contract defined in Exporter.

2. Consistent Validation
Null values are handled in one place only.

3. Cleaner Code
Subclasses only implement formatting logic.

4. Better Maintainability
If validation rules change, they only need to be updated in one class.

5. Prevents Incorrect Overrides
The export() method is declared:
public final

So subclasses cannot change core behavior.

11. Design Pattern Used
This refactoring uses the Template Method Pattern.
Structure:
Exporter.export()   ← template method

    ↓

Exporter.doExport() ← implemented by subclasses

The template method controls the workflow while subclasses implement specific steps.

12. Conclusion
The original implementation had several problems, mainly:
LSP violations
Inconsistent null handling
Unclear subclass responsibilities
The refactored design fixes these issues by:
Centralizing validation in the base class
Using the Template Method Pattern
Ensuring exporters follow a consistent contract
This results in a system that is:
More predictable
Easier to maintain
Fully compliant with object-oriented design principles.


