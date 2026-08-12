public class Relay extends Component {

    private final double coilVoltage;
    private final double contactRatingAmps;

    public Relay(int id, String name, int quantity, double coilVoltage, double contactRatingAmps) {
        super(id, name, Category.RELAY, quantity);
        this.coilVoltage = coilVoltage;
        this.contactRatingAmps = contactRatingAmps;
    }

    public double getCoilVoltage() {
        return coilVoltage;
    }

    public double getContactRatingAmps() {
        return contactRatingAmps;
    }

    @Override
    public String specs() {
        return "%.0fV coil, %.1fA contacts".formatted(coilVoltage, contactRatingAmps);
    }
}
