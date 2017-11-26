// code by jph
package ch.ethz.idsc.owl.math.noise;

/** interface that declares bivariate and trivariate noise functions
 * with input of double type */
public interface NativeContinuousNoise {
  /** @param x
   * @return value in the interval [-1, 1] that varies smoothly with x, y */
  double at(double x);

  /** @param x
   * @param y
   * @return value in the interval [-1, 1] that varies smoothly with x, y */
  double at(double x, double y);

  /** @param x
   * @param y
   * @param z
   * @return value in the interval [-1, 1] that varies smoothly with x, y, z */
  double at(double x, double y, double z);
}
