package inspection;

public final class TechnicalDebtVisitor implements ProjectVisitor {
    private long estimatedMinutes;

    @Override
    public void visit(SourceCodeFile file) {
        estimatedMinutes += Math.max(0L, file.complexity() - 10L) * 30;
    }

    @Override
    public void visit(TestFile file) {
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.insecure()) {
            estimatedMinutes += 60;
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (Dependency dependency : file.dependencies()) {
            if (dependency.vulnerable()) {
                estimatedMinutes += 120;
            }
        }
    }

    @Override
    public void visit(Directory directory) {
    }

    public long estimatedMinutes() {
        return estimatedMinutes;
    }

    public String report() {
        return "Technical debt: " + estimatedMinutes + " estimated repair minutes";
    }
}
