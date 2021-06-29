package gateway;

import model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.naming.SizeLimitExceededException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ReportGatewayTest {
    private static final GatewayFactory gatewayFactory = new GatewayPSQLFactory();
    private static final String email = "test@gmail.com";
    private static final String password = "testPassword";
    private static final String name = "Test";
    private static final String role = User.OWNER;
    private static final String testString = "test";
    private static int museumId;

    @BeforeAll
    static void initialize() throws SQLException {
        System.out.println("Starting database...");
        System.out.println("Registering test user...");
        gatewayFactory.getUserGateway().register(email, password, name, name, role);
        System.out.println("Adding Test Museum");
        museumId = gatewayFactory.getMuseumGateway().addMuseum(testString, testString, testString, testString, 1, 1, testString, testString, testString);
        System.out.println("Added Test Museum with id: " + museumId);
    }

    @AfterAll
    static void close() throws SQLException {
        System.out.println("Removing test user...");
        gatewayFactory.getUserGateway().removeUser(email);
        System.out.println("Removing test museum");
        gatewayFactory.getMuseumGateway().removeMuseum(museumId);
        System.out.println("Closing database...");

    }

    @Test
    public void reportsAdminTest() throws SQLException {
        ReportGateway reportGateway = gatewayFactory.getReportGateway();
        int nRep = reportGateway.getAllReports().size();

        //testing that admin doesn't see not approved reports
        reportGateway.report(1, museumId, testString, false);
        Assertions.assertEquals(reportGateway.getAllReports().size(), nRep);

        //testing that admin sees approved reports
        reportGateway.report(1, museumId, testString, true);
        Assertions.assertEquals(reportGateway.getAllReports().size(), nRep + 1);
        int id1 = reportGateway.getAllReports().get(reportGateway.getAllReports().size() - 1).getId();
        Assertions.assertEquals(testString, reportGateway.getAllReports().get(reportGateway.getAllReports().size() - 1).getDescription());

        //testing that admin correctly removes reports
        reportGateway.report(1, museumId, testString, true);
        Assertions.assertEquals(reportGateway.getAllReports().size(), nRep + 2);
        int id2 = reportGateway.getAllReports().get(reportGateway.getAllReports().size() - 1).getId();
        reportGateway.resolveReport(id1);
        Assertions.assertEquals(reportGateway.getAllReports().size(), nRep + 1);
        reportGateway.resolveReport(id2);
        Assertions.assertEquals(reportGateway.getAllReports().size(), nRep);
    }

    @Test
    public void reportOwnerTest() throws SQLException, SizeLimitExceededException {
        ReportGateway reportGateway = gatewayFactory.getReportGateway();
        //adding owner
        gatewayFactory.getMuseumGateway().addOwner(email, museumId);
        User owner = gatewayFactory.getUserGateway().login(email, password);
        int userId = owner.getId();
        int nOwnRep = reportGateway.getOwnerReports(userId).size();
        int nAdmRep = reportGateway.getAllReports().size();

        //testing reports effectively stored
        reportGateway.report(1, museumId, testString, false);
        reportGateway.report(1, museumId, testString, false);
        Assertions.assertEquals(nOwnRep + 2, reportGateway.getOwnerReports(userId).size());
        Assertions.assertEquals(reportGateway.getAllReports().size(), nAdmRep);


        //testing that owner approves report correctly
        int id1 = reportGateway.getOwnerReports(userId).get(reportGateway.getOwnerReports(userId).size() - 1).getId();
        reportGateway.approveReport(id1);
        Assertions.assertEquals(nOwnRep + 1, reportGateway.getOwnerReports(userId).size());
        Assertions.assertEquals(reportGateway.getAllReports().size(), nAdmRep + 1);

        //testing that owner rejects report correctly
        int id2 = reportGateway.getOwnerReports(userId).get(reportGateway.getOwnerReports(userId).size() - 1).getId();
        reportGateway.rejectReport(id2);
        Assertions.assertEquals(nOwnRep, reportGateway.getOwnerReports(userId).size());
        Assertions.assertEquals(reportGateway.getAllReports().size(), nAdmRep + 1);
    }

    @Test
    public void reportSQLInjectionTest() throws SQLException {
        gatewayFactory.getReportGateway().report(1, museumId, "prova,true); DROP TABLE reports --", true);
        Assertions.assertNotEquals(0, gatewayFactory.getReportGateway().getAllReports().size());
    }
}
