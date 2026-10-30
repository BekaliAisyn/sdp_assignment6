# Smart Code Inspection & Compliance Engine

Assignment 6. A small Java 17+ console project using the Visitor pattern.

## Run

Install JDK 17 or newer and Maven 3.9 or newer.

```sh
mvn clean verify
java -jar target/smart-code-inspection-1.0.0.jar
```

The demo uses in-memory metadata, not a real source-code parser or vulnerability database. Security flags describe sample problems. No external service or credentials are needed.

```text
fintech-project/
├── src/
│   ├── PaymentService.java
│   └── UserService.java
├── tests/
│   └── PaymentServiceTest.java
├── config/
│   └── application.properties
└── pom.xml
```

## Rules

- Security: credentials, insecure configuration and vulnerable dependencies are critical. Source complexity above 15 produces a warning.
- Statistics: directories are excluded from file counts. Lines of code include source and test files. Average complexity uses source files only. Test/source ratio means test files divided by source files. With no sources, both averages are zero.
- Quality: average complexity must be at most 10, test/source ratio at least 0.40, and critical security issues zero. An empty project fails the test ratio rule.
- Compliance: no exposed credentials, no blank dependency versions, no `legacy-crypto` or `unsafe-logger`, and at least one test case for each source. Source names are unique identifiers in this model; test files refer to them through `sourceName`.
- Technical debt: 30 minutes per complexity point above 10, 60 per insecure configuration, and 120 per vulnerable dependency. This is a simple estimate.

Create a fresh visitor for each analysis. Visitors collect results during one traversal. `Directory.accept()` visits the directory and recursively visits its children. Visitors do not traverse children again.

## Pattern questions

**What problem does Visitor solve?** It separates inspection operations from project data, so we can add inspections without editing element classes.

**Where are Element and Visitor?** `ProjectElement` and its five implementations are in `src/main/java/inspection`. `ProjectVisitor` declares five overloaded `visit` methods. The five concrete visitors implement the operations.

**What is double dispatch?** Calling `element.accept(visitor)` selects the actual element's method. Inside it, `visitor.visit(this)` selects the overload for that element, then the implementation belonging to the actual visitor. Both objects determine the operation.

**Why not put operations in elements?** It would mix data with security, quality and compliance rules. Each new operation would require editing every element.

**Is adding a Visitor or an Element easier?** A visitor is easier: implement `ProjectVisitor` and use the existing `accept` methods. `TechnicalDebtVisitor` was added in its own commit without changing elements.

**What if we add DatabaseMigrationFile?** Create the class implementing `ProjectElement`, add `visit(DatabaseMigrationFile)` to `ProjectVisitor`, and implement it in all five concrete visitors. The quality gate must delegate the new type to its security and statistics visitors. Update tests and UML, and add a demo instance if needed. Existing concrete elements and directory traversal need no changes. Until all visitor methods exist, compilation fails.

**When is Visitor a bad choice?** When element types change often or only one simple operation is needed. Every new type affects all visitors.

**How is it different from Strategy?** Strategy swaps an algorithm used by a context. Visitor applies an operation to different element types through double dispatch.

**Which SOLID principles apply?** Single Responsibility separates data from inspections. Open/Closed supports adding operations without changing elements, but adding element types requires changes. Dependency Inversion lets elements use the `ProjectVisitor` interface and directories use `ProjectElement`.

## Tests and report

JUnit 5 tests cover all element types, security, statistics, quality PASS/FAIL, compliance, nested and empty directories, technical debt, and boundary/invalid inputs.

See [UML diagrams](docs/UML.md) and the [short report](REPORT.md).

## Local Git history

The work is stored in separate local commits. Push the current branch and all its ancestor commits to `main` with:

```sh
git push -u origin HEAD:main
```

Publishing the GitHub repository and submitting it to Moodle are manual steps.
