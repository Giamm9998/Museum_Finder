package gateway;

public class GatewayPSQLFactory implements GatewayFactory {
    public GatewayPSQLFactory() {
    }

    @Override
    public MuseumGateway getMuseumGateway() {
        return MuseumGateway.getInstance();
    }

    @Override
    public UserGateway getUserGateway() {
        return UserGateway.getInstance();
    }

    @Override
    public ReportGateway getReportGateway() {
        return ReportGateway.getInstance();
    }

    @Override
    public ReviewGateway getReviewGateway() {
        return ReviewGateway.getInstance();
    }

    @Override
    public EventGateway getEventGateway() {
        return EventGateway.getInstance();
    }
}
