// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class LinearVelocity3MinTime {
  private final Scalar v_max;
  private final Scalar a_max;
  @SuppressWarnings("unused")
  private final Scalar v_tar;
  private final Clip v_range;

  /** @param v_max positive
   * @param a_max positive
   * @param v_tar non-negative */
  public LinearVelocity3MinTime(Scalar v_max, Scalar a_max, Scalar v_tar) {
    this.v_max = Sign.requirePositive(v_max);
    this.a_max = Sign.requirePositive(a_max);
    this.v_tar = Sign.requirePositiveOrZero(v_tar);
    v_range = Clip.function(v_max.negate(), v_max);
  }

  /** @param d_tar
   * @param v_cur in [0, v_max]
   * @return */
  public Scalar minTime(final Scalar d_tar, final Scalar v_cur) {
    return null;
  }

  /** @param v_cur
   * @return */
  public Scalar timeToV_max(Scalar v_cur) {
    v_range.requireInside(v_cur);
    return v_max.subtract(v_cur).divide(a_max);
  }

  /** @param v_cur
   * @return minimum distance required to achieve v_max regardless of integration method */
  public Scalar minDistToV_max(Scalar v_cur) {
    return v_cur.multiply(timeToV_max(v_cur));
  }
}
