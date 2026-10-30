package inspection;

import java.util.ArrayList;
import java.util.List;

public final class SecurityAuditVisitor implements ProjectVisitor {
    private final List<String> issues = new ArrayList<>();
    private int criticalIssues;

    private void critical(String message) {
        criticalIssues++;
        issues.add("CRITICAL: " + message);
    }

    @Override
    public void visit(SourceCodeFile file) {
        if (file.hardcodedCredentials()) {
            critical(file.name() + ": hardcoded credentials");
        }
        if (file.complexity() > 15) {
            issues.add("WARNING: " + file.name() + ": high-risk source complexity");
        }
    }

    @Override
    public void visit(TestFile file) {
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.exposedCredentials()) {
            critical(file.name() + ": exposed credentials");
        }
        if (file.insecure()) {
            critical(file.name() + ": insecure configuration");
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (Dependency dependency : file.dependencies()) {
            if (dependency.vulnerable()) {
                critical(file.name() + ": vulnerable dependency " + dependency.name());
            }
        }
    }

    @Override
    public void visit(Directory directory) {
    }

    public int criticalIssues() {
        return criticalIssues;
    }

    public List<String> issues() {
        return List.copyOf(issues);
    }

    public String report() {
        return "Security audit: " + criticalIssues + " critical issue(s)\n"
                + (issues.isEmpty() ? "No security issues" : String.join("\n", issues));
    }
}
