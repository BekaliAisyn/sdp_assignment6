package inspection;

import java.util.Objects;

public record SourceCodeFile(String name, int linesOfCode, int complexity,
                             boolean hardcodedCredentials) implements ProjectElement {
    public SourceCodeFile {
        Objects.requireNonNull(name);
        if (linesOfCode < 0 || complexity < 0) {
            throw new IllegalArgumentException("Source metrics cannot be negative");
        }
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
