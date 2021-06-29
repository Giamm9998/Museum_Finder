package gateway;

public interface GatewayFactory {
    MuseumGateway getMuseumGateway();

    UserGateway getUserGateway();

    ReportGateway getReportGateway();

    ReviewGateway getReviewGateway();

    EventGateway getEventGateway();
}
