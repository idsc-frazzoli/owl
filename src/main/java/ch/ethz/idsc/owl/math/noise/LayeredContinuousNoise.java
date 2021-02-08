// code by jph
package ch.ethz.idsc.owl.math.noise;

import java.io.Serializable;
import java.util.stream.IntStream;

public class LayeredContinuousNoise implements NativeContinuousNoise, Serializable {
  /** WARNING: values are not copied but used by reference
   *
   * @param nativeContinuousNoise
   * @param magnitude
   * @param frequency
   * @return */
  public static NativeContinuousNoise of(NativeContinuousNoise nativeContinuousNoise, double[] magnitude, double[] frequency) {
    return new LayeredContinuousNoise(nativeContinuousNoise, magnitude, frequency);
  }

  /***************************************************/
  private final NativeContinuousNoise nativeContinuousNoise;
  private final double[] magnitude;
  private final double[] frequency;

  private LayeredContinuousNoise(NativeContinuousNoise nativeContinuousNoise, double[] magnitude, double[] frequency) {
    if (magnitude.length != frequency.length)
      throw new IllegalArgumentException();
    this.nativeContinuousNoise = nativeContinuousNoise;
    this.magnitude = magnitude;
    this.frequency = frequency;
  }

  @Override // from NativeContinuousNoise
  public double at(double x) {
    return at(x, 0);
  }

  @Override // from NativeContinuousNoise
  public double at(double x, double y) {
    return IntStream.range(0, magnitude.length) //
        .mapToDouble(index -> magnitude[index] * nativeContinuousNoise.at( //
            x * frequency[index], //
            y * frequency[index])) //
        .sum();
  }

  @Override // from NativeContinuousNoise
  public double at(double x, double y, double z) {
    return IntStream.range(0, magnitude.length) //
        .mapToDouble(index -> magnitude[index] * nativeContinuousNoise.at( //
            x * frequency[index], //
            y * frequency[index], //
            z * frequency[index])) //
        .sum();
  }
}
