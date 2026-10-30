package inspection;

import java.util.List;
import java.util.Objects;

public record DependencyFile(String name, List<Dependency> dependencies) implements ProjectElement {
    public DependencyFile {
        Objects.requireNonNull(name);
        dependencies = List.copyOf(dependencies);
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
