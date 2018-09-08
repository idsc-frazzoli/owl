// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class LinearVelocity2MinTime {
  private final Scalar v_max;
  private final Scalar a_max;
  private final Clip v_range;

  /** @param v_max positive
   * @param a_max positive */
  public LinearVelocity2MinTime(Scalar v_max, Scalar a_max) {
    this.v_max = Sign.requirePositive(v_max);
    this.a_max = Sign.requirePositive(a_max);
    v_range = Clip.function(v_max.negate(), v_max);
  }

  /** @param d_tar
   * @param v_cur in [0, v_max]
   * @return */
  public Scalar minTime(final Scalar d_tar, final Scalar v_cur) {
    final Scalar minDistToV_max = minDistToV_max(v_cur);
    final Scalar d_remain = d_tar.subtract(minDistToV_max);
    if (Sign.isPositive(d_remain)) {
      System.out.println("d_remain=" + d_remain);
      Scalar t_remain = d_remain.divide(v_max);
      System.out.println("t_remain=" + t_remain);
      return timeToV_max(v_cur).add(t_remain);
    } else {
      // ---
    }
    throw new RuntimeException();
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
