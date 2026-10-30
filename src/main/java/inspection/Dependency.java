package inspection;

import java.util.Objects;

public record Dependency(String name, String version, boolean vulnerable) {
    public Dependency {
        Objects.requireNonNull(name);
        Objects.requireNonNull(version);
    }
}
