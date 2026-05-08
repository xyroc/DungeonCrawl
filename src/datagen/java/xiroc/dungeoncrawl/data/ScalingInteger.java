package xiroc.dungeoncrawl.data;

/**
 * Represents an integer value that scales with each step.
 *
 * @param base         The base value at step 0.
 * @param absoluteStep The absolute amount to add per step.
 * @param relativeStep The multiplier to apply to the base per step.
 */
public record ScalingInteger(int base, int absoluteStep, double relativeStep) {
    public int atStep(int step) {
        return (int) Math.round(base * Math.pow(relativeStep, step) + absoluteStep * step);
    }
}
