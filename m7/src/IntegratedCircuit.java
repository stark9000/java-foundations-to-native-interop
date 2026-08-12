public class IntegratedCircuit extends Component {

    private final String partNumber;
    private final int pinCount;

    public IntegratedCircuit(int id, String name, int quantity, String partNumber, int pinCount) {
        super(id, name, Category.INTEGRATED_CIRCUIT, quantity);
        this.partNumber = partNumber;
        this.pinCount = pinCount;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public int getPinCount() {
        return pinCount;
    }

    @Override
    public String specs() {
        return "%s, %d-pin".formatted(partNumber, pinCount);
    }
}
