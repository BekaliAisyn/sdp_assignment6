package inspection;

import java.util.Objects;

public record TestFile(String name, String sourceName, int linesOfCode,
                       int testCount) implements ProjectElement {
    public TestFile {
        Objects.requireNonNull(name);
        Objects.requireNonNull(sourceName);
        if (linesOfCode < 0 || testCount < 0) {
            throw new IllegalArgumentException("Test metrics cannot be negative");
        }
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
