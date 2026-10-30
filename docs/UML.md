# UML diagrams

## Class diagram

```mermaid
classDiagram
    class ProjectElement {
        <<interface>>
        +accept(ProjectVisitor visitor) void
    }
    class ProjectVisitor {
        <<interface>>
        +visit(SourceCodeFile file) void
        +visit(TestFile file) void
        +visit(ConfigurationFile file) void
        +visit(DependencyFile file) void
        +visit(Directory directory) void
    }
    class SourceCodeFile {
        +String name
        +int linesOfCode
        +int complexity
        +boolean hardcodedCredentials
        +accept(ProjectVisitor visitor) void
    }
    class TestFile {
        +String name
        +String sourceName
        +int linesOfCode
        +int testCount
        +accept(ProjectVisitor visitor) void
    }
    class ConfigurationFile {
        +String name
        +boolean exposedCredentials
        +boolean insecure
        +accept(ProjectVisitor visitor) void
    }
    class DependencyFile {
        +String name
        +List~Dependency~ dependencies
        +accept(ProjectVisitor visitor) void
    }
    class Dependency {
        +String name
        +String version
        +boolean vulnerable
    }
    class Directory {
        +String name
        +List~ProjectElement~ children
        +accept(ProjectVisitor visitor) void
    }
    class SecurityAuditVisitor {
        +criticalIssues() int
        +issues() List~String~
        +report() String
    }
    class ProjectStatisticsVisitor {
        +totalFiles() int
        +linesOfCode() int
        +sourceFiles() int
        +testFiles() int
        +tests() int
        +dependencies() int
        +averageComplexity() double
        +testSourceRatio() double
        +report() String
    }
    class QualityGateVisitor {
        +securityPassed() boolean
        +complexityPassed() boolean
        +testsPassed() boolean
        +passed() boolean
        +report() String
    }
    class ComplianceVisitor {
        +violations() List~String~
        +report() String
    }
    class TechnicalDebtVisitor {
        +estimatedMinutes() long
        +report() String
    }
    ProjectElement <|.. SourceCodeFile
    ProjectElement <|.. TestFile
    ProjectElement <|.. ConfigurationFile
    ProjectElement <|.. DependencyFile
    ProjectElement <|.. Directory
    Directory o-- "0..*" ProjectElement : children
    DependencyFile o-- "0..*" Dependency : dependencies
    ProjectVisitor <|.. SecurityAuditVisitor
    ProjectVisitor <|.. ProjectStatisticsVisitor
    ProjectVisitor <|.. QualityGateVisitor
    ProjectVisitor <|.. ComplianceVisitor
    ProjectVisitor <|.. TechnicalDebtVisitor
    ProjectElement ..> ProjectVisitor : accept calls visit
    ProjectVisitor ..> SourceCodeFile : visit
    ProjectVisitor ..> TestFile : visit
    ProjectVisitor ..> ConfigurationFile : visit
    ProjectVisitor ..> DependencyFile : visit
    ProjectVisitor ..> Directory : visit
    QualityGateVisitor *-- SecurityAuditVisitor
    QualityGateVisitor *-- ProjectStatisticsVisitor
```

Each concrete visitor implements all five `visit` overloads. Java records expose the listed data through accessor methods. `Main` creates the `DemoProject` and runs all five visitors on the same root.

## Sequence diagram

```mermaid
sequenceDiagram
    participant Main
    participant Root as Directory(fintech-project)
    participant Src as Directory(src)
    participant File as SourceCodeFile(PaymentService.java)
    participant Audit as SecurityAuditVisitor
    Main->>Root: accept(audit)
    Root->>Audit: visit(root)
    Root->>Src: accept(audit)
    Src->>Audit: visit(src)
    Src->>File: accept(audit)
    File->>Audit: visit(this)
    Audit->>Audit: record credential issue and complexity warning
    Audit-->>File: return
    File-->>Src: return
    Src-->>Root: return after processing src children
    Note over Root,Audit: Directory.accept continues with the remaining children
    Root-->>Main: traversal complete
    Main->>Audit: report()
    Audit-->>Main: security report
```
