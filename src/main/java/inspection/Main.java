package inspection;

public final class Main {
    public static void main(String[] args) {
        Directory project = DemoProject.create();
        SecurityAuditVisitor security = new SecurityAuditVisitor();
        ProjectStatisticsVisitor statistics = new ProjectStatisticsVisitor();
        QualityGateVisitor quality = new QualityGateVisitor();
        ComplianceVisitor compliance = new ComplianceVisitor();
        project.accept(security);
        project.accept(statistics);
        project.accept(quality);
        project.accept(compliance);
        System.out.println(security.report());
        System.out.println("\n" + statistics.report());
        System.out.println("\n" + quality.report());
        System.out.println("\n" + compliance.report());
    }
}
