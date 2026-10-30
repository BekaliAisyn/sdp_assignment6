package inspection;

import java.util.List;
import java.util.Objects;

public record Directory(String name, List<ProjectElement> children) implements ProjectElement {
    public Directory {
        Objects.requireNonNull(name);
        children = List.copyOf(children);
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
        for (ProjectElement child : children) {
            child.accept(visitor);
        }
    }
}
