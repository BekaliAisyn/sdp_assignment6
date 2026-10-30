# Assignment 6 Report

The project uses Visitor to inspect an in-memory fintech project. Five element types store project data. Five visitors perform security, statistics, quality, compliance and technical debt checks. Directories support nested elements.

The demo contains five files, 360 lines of code, six test cases and two dependencies. Average source complexity is 12 and the test/source ratio is 0.50. Security finds four critical issues. The quality gate fails security and complexity but passes the test ratio. Compliance finds five violations. Estimated technical debt is 420 minutes.

These demo failures are expected because the sample contains unsafe settings, credentials, a vulnerable dependency and an untested source file.

Validation: `mvn clean verify` passed all 18 JUnit 5 tests with zero failures, errors or skipped tests. The executable JAR ran successfully and its report values were checked. The build used JDK 21 with Java 17 as the compilation target.

TechnicalDebtVisitor was added in a separate commit without changing the elements. The README explains double dispatch, SOLID, Strategy and the changes needed for DatabaseMigrationFile. The UML class and sequence diagrams are in `docs/UML.md`.
