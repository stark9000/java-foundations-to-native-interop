/**
 * A concrete subclass. It only adds what's actually specific to a
 * resistor - resistance and tolerance - and inherits id/name/category/
 * quantity handling, equals/hashCode, and summary() from Component
 * completely unchanged.
 */
public class Resistor extends Component {

    private final double resistanceOhms;
    private final double tolerancePercent;

    public Resistor(int id, String name, int quantity, double resistanceOhms, double tolerancePercent) {
        // super(...) must be the first statement - a subclass can't
        // exist without its parent's state being constructed first.
        super(id, name, Category.RESISTOR, quantity);
        this.resistanceOhms = resistanceOhms;
        this.tolerancePercent = tolerancePercent;
    }

    public double getResistanceOhms() {
        return resistanceOhms;
    }

    public double getTolerancePercent() {
        return tolerancePercent;
    }

    @Override
    public String specs() {
        return "%.1f ohm +/-%.1f%%".formatted(resistanceOhms, tolerancePercent);
    }
}
