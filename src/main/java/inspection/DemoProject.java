package inspection;

import java.util.List;

public final class DemoProject {
    private DemoProject() {
    }

    public static Directory create() {
        return new Directory("fintech-project", List.of(
                new Directory("src", List.of(
                        new SourceCodeFile("PaymentService.java", 180, 18, true),
                        new SourceCodeFile("UserService.java", 100, 6, false))),
                new Directory("tests", List.of(
                        new TestFile("PaymentServiceTest.java", "PaymentService.java", 80, 6))),
                new Directory("config", List.of(
                        new ConfigurationFile("application.properties", true, true))),
                new DependencyFile("pom.xml", List.of(
                        new Dependency("spring-core", "6.2.0", false),
                        new Dependency("legacy-crypto", "", true)))));
    }
}
