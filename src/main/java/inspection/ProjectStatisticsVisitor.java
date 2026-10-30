package inspection;

import java.util.Locale;

public final class ProjectStatisticsVisitor implements ProjectVisitor {
    private int totalFiles;
    private int linesOfCode;
    private int sourceFiles;
    private int testFiles;
    private int tests;
    private int dependencies;
    private long complexity;

    @Override
    public void visit(SourceCodeFile file) {
        totalFiles++;
        sourceFiles++;
        linesOfCode += file.linesOfCode();
        complexity += file.complexity();
    }

    @Override
    public void visit(TestFile file) {
        totalFiles++;
        testFiles++;
        linesOfCode += file.linesOfCode();
        tests += file.testCount();
    }

    @Override
    public void visit(ConfigurationFile file) {
        totalFiles++;
    }

    @Override
    public void visit(DependencyFile file) {
        totalFiles++;
        dependencies += file.dependencies().size();
    }

    @Override
    public void visit(Directory directory) {
    }

    public int totalFiles() { return totalFiles; }
    public int linesOfCode() { return linesOfCode; }
    public int sourceFiles() { return sourceFiles; }
    public int testFiles() { return testFiles; }
    public int tests() { return tests; }
    public int dependencies() { return dependencies; }

    public double averageComplexity() {
        return sourceFiles == 0 ? 0 : (double) complexity / sourceFiles;
    }

    public double testSourceRatio() {
        return sourceFiles == 0 ? 0 : (double) testFiles / sourceFiles;
    }

    public String report() {
        return String.format(Locale.ROOT,
                "Project statistics\nFiles: %d\nLines of code: %d\nTests: %d\nDependencies: %d"
                + "\nAverage complexity: %.2f\nTest/source ratio: %.2f",
                totalFiles, linesOfCode, tests, dependencies, averageComplexity(), testSourceRatio());
    }
}
