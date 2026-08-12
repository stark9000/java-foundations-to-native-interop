/**
 * The entire contract VuMeterPanel actually depends on. Module 6's
 * SignalGenerator and Module 9's NativeSignalGenerator both already
 * had a matching next() method - neither needed a single line changed
 * to satisfy this interface. Introducing it here, in the packaging
 * chapter, is what turns "these two classes happen to look similar"
 * into an explicit, compiler-checked substitutability.
 */
public interface LevelSource {
    double next();
}
