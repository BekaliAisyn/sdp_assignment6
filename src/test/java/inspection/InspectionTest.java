package inspection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InspectionTest {
    private Directory safeProject(int complexity) {
        return new Directory("safe", List.of(
                new SourceCodeFile("Service.java", 100, complexity, false),
                new TestFile("ServiceTest.java", "Service.java", 40, 3),
                new ConfigurationFile("app.properties", false, false),
                new DependencyFile("pom.xml", List.of(new Dependency("safe-lib", "1.0", false)))));
    }

    @Test
    void sourceDispatchesToSecurityAudit() {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        new SourceCodeFile("Risk.java", 100, 16, true).accept(visitor);
        assertEquals(1, visitor.criticalIssues());
        assertEquals(List.of("CRITICAL: Risk.java: hardcoded credentials",
                "WARNING: Risk.java: high-risk source complexity"), visitor.issues());
    }

    @Test
    void configurationDetectsBothSecurityProblems() {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        new ConfigurationFile("app.properties", true, true).accept(visitor);
        assertEquals(2, visitor.criticalIssues());
        assertTrue(visitor.report().contains("insecure configuration"));
    }

    @Test
    void dependencyAuditReportsOnlyVulnerableDependencies() {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        new DependencyFile("pom.xml", List.of(
                new Dependency("safe", "1.0", false),
                new Dependency("unsafe", "2.0", true))).accept(visitor);
        assertEquals(List.of("CRITICAL: pom.xml: vulnerable dependency unsafe"), visitor.issues());
    }

    @Test
    void safeProjectHasNoSecurityIssues() {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        safeProject(5).accept(visitor);
        assertEquals(0, visitor.criticalIssues());
        assertTrue(visitor.issues().isEmpty());
    }

    @Test
    void testFileContributesLinesAndTestCases() {
        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();
        new TestFile("ServiceTest.java", "Service.java", 40, 3).accept(visitor);
        assertEquals(1, visitor.totalFiles());
        assertEquals(1, visitor.testFiles());
        assertEquals(3, visitor.tests());
        assertEquals(40, visitor.linesOfCode());
        assertEquals(0.0, visitor.testSourceRatio());
    }

    @Test
    void demoStatisticsIncludeEveryFileExactlyOnce() {
        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();
        DemoProject.create().accept(visitor);
        assertEquals(5, visitor.totalFiles());
        assertEquals(360, visitor.linesOfCode());
        assertEquals(2, visitor.sourceFiles());
        assertEquals(6, visitor.tests());
        assertEquals(2, visitor.dependencies());
        assertEquals(12.0, visitor.averageComplexity());
        assertEquals(0.5, visitor.testSourceRatio());
    }

    @Test
    void qualityGatePassesAtComplexityBoundary() {
        QualityGateVisitor visitor = new QualityGateVisitor();
        safeProject(10).accept(visitor);
        assertTrue(visitor.passed());
        assertTrue(visitor.report().contains("FINAL RESULT: PASSED"));
    }

    @Test
    void qualityGateFailsSecurityAndComplexityInDemo() {
        QualityGateVisitor visitor = new QualityGateVisitor();
        DemoProject.create().accept(visitor);
        assertFalse(visitor.securityPassed());
        assertFalse(visitor.complexityPassed());
        assertTrue(visitor.testsPassed());
        assertFalse(visitor.passed());
    }

    @Test
    void qualityGateFailsWhenTestsAreMissing() {
        QualityGateVisitor visitor = new QualityGateVisitor();
        new SourceCodeFile("Service.java", 10, 1, false).accept(visitor);
        assertTrue(visitor.securityPassed());
        assertTrue(visitor.complexityPassed());
        assertFalse(visitor.testsPassed());
        assertFalse(visitor.passed());
    }

    @Test
    void ratioBoundaryOfFortyPercentPasses() {
        QualityGateVisitor visitor = new QualityGateVisitor();
        for (int i = 0; i < 5; i++) {
            new SourceCodeFile("Service" + i, 10, 1, false).accept(visitor);
        }
        for (int i = 0; i < 2; i++) {
            new TestFile("Test" + i, "Service" + i, 5, 1).accept(visitor);
        }
        assertTrue(visitor.testsPassed());
        assertTrue(visitor.passed());
    }

    @Test
    void complianceFindsEveryDemoViolation() {
        ComplianceVisitor visitor = new ComplianceVisitor();
        DemoProject.create().accept(visitor);
        assertEquals(List.of(
                "PaymentService.java: exposed credentials",
                "application.properties: exposed credentials",
                "legacy-crypto: missing version",
                "legacy-crypto: prohibited dependency",
                "UserService.java: production module has no tests"), visitor.violations());
        assertEquals(visitor.violations(), visitor.violations());
    }

    @Test
    void complianceWorksWhenTestsAppearBeforeSources() {
        ComplianceVisitor visitor = new ComplianceVisitor();
        new Directory("root", List.of(
                new TestFile("Test.java", "Service.java", 10, 1),
                new SourceCodeFile("Service.java", 10, 1, false))).accept(visitor);
        assertTrue(visitor.violations().isEmpty());
        assertEquals("Compliance: PASS", visitor.report());
    }

    @Test
    void emptyTestFileDoesNotSatisfyCompliance() {
        ComplianceVisitor visitor = new ComplianceVisitor();
        new Directory("root", List.of(
                new SourceCodeFile("Service.java", 10, 1, false),
                new TestFile("Test.java", "Service.java", 0, 0))).accept(visitor);
        assertEquals(List.of("Service.java: production module has no tests"), visitor.violations());
    }

    @Test
    void nestedDirectoriesAreTraversedExactlyOnce() {
        ProjectElement tree = new SourceCodeFile("Deep.java", 7, 3, false);
        for (int i = 0; i < 100; i++) {
            tree = new Directory("level" + i, List.of(tree));
        }
        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();
        tree.accept(visitor);
        assertEquals(1, visitor.totalFiles());
        assertEquals(7, visitor.linesOfCode());
        assertEquals(3.0, visitor.averageComplexity());
    }

    @Test
    void emptyDirectoryHasFiniteZeroMetricsAndFailsReleaseGate() {
        Directory empty = new Directory("empty", List.of());
        ProjectStatisticsVisitor statistics = new ProjectStatisticsVisitor();
        QualityGateVisitor quality = new QualityGateVisitor();
        empty.accept(statistics);
        empty.accept(quality);
        assertEquals(0, statistics.totalFiles());
        assertEquals(0.0, statistics.averageComplexity());
        assertEquals(0.0, statistics.testSourceRatio());
        assertFalse(quality.passed());
    }

    @Test
    void technicalDebtUsesComplexityConfigurationAndDependencies() {
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();
        DemoProject.create().accept(visitor);
        assertEquals(420, visitor.estimatedMinutes());
        assertEquals("Technical debt: 420 estimated repair minutes", visitor.report());
    }

    @Test
    void safeProjectHasNoEstimatedDebt() {
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();
        safeProject(10).accept(visitor);
        assertEquals(0, visitor.estimatedMinutes());
    }

    @Test
    void negativeMetricsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new SourceCodeFile("Bad.java", -1, 2, false));
        assertThrows(IllegalArgumentException.class,
                () -> new SourceCodeFile("Bad.java", 1, -2, false));
        assertThrows(IllegalArgumentException.class,
                () -> new TestFile("BadTest.java", "Bad.java", 0, -1));
    }
}
