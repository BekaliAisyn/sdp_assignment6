package inspection;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ComplianceVisitor implements ProjectVisitor {
    private final List<String> violations = new ArrayList<>();
    private final Set<String> sources = new LinkedHashSet<>();
    private final Set<String> testedSources = new LinkedHashSet<>();
    private final Set<String> prohibited = Set.of("unsafe-logger", "legacy-crypto");

    @Override
    public void visit(SourceCodeFile file) {
        sources.add(file.name());
        if (file.hardcodedCredentials()) {
            violations.add(file.name() + ": exposed credentials");
        }
    }

    @Override
    public void visit(TestFile file) {
        if (file.testCount() > 0) {
            testedSources.add(file.sourceName());
        }
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.exposedCredentials()) {
            violations.add(file.name() + ": exposed credentials");
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (Dependency dependency : file.dependencies()) {
            if (dependency.version().isBlank()) {
                violations.add(dependency.name() + ": missing version");
            }
            if (prohibited.contains(dependency.name())) {
                violations.add(dependency.name() + ": prohibited dependency");
            }
        }
    }

    @Override
    public void visit(Directory directory) {
    }

    public List<String> violations() {
        List<String> result = new ArrayList<>(violations);
        for (String source : sources) {
            if (!testedSources.contains(source)) {
                result.add(source + ": production module has no tests");
            }
        }
        return List.copyOf(result);
    }

    public String report() {
        List<String> result = violations();
        return "Compliance: " + (result.isEmpty() ? "PASS" : "FAIL\n" + String.join("\n", result));
    }
}
