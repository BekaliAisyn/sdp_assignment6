package inspection;

import java.util.Objects;

public record ConfigurationFile(String name, boolean exposedCredentials,
                                boolean insecure) implements ProjectElement {
    public ConfigurationFile {
        Objects.requireNonNull(name);
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
