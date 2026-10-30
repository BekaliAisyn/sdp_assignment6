package inspection;

public final class QualityGateVisitor implements ProjectVisitor {
    private final SecurityAuditVisitor security = new SecurityAuditVisitor();
    private final ProjectStatisticsVisitor statistics = new ProjectStatisticsVisitor();

    @Override
    public void visit(SourceCodeFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(TestFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(ConfigurationFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(DependencyFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(Directory directory) {
    }

    public boolean securityPassed() { return security.criticalIssues() == 0; }
    public boolean complexityPassed() { return statistics.averageComplexity() <= 10; }
    public boolean testsPassed() { return statistics.testSourceRatio() >= 0.40; }

    public boolean passed() {
        return securityPassed() && complexityPassed() && testsPassed();
    }

    private String status(boolean passed) {
        return passed ? "PASS" : "FAIL";
    }

    public String report() {
        return "Quality gate\nSecurity: " + status(securityPassed())
                + "\nComplexity: " + status(complexityPassed())
                + "\nTests: " + status(testsPassed())
                + "\nFINAL RESULT: " + (passed() ? "PASSED" : "FAILED");
    }
}
