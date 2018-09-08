// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.qty.Quantity;
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
    v_range = Clip.function(v_max.zero(), v_max);
  }

  /** @param d_tar
   * @param v_cur in [0, v_max]
   * @return */
  public Scalar minTime(final Scalar d_tar, final Scalar v_cur) {
    final Scalar minDistToV_max = minDistToV_max(v_cur);
    final Scalar d_remain = d_tar.subtract(minDistToV_max);
    if (Sign.isPositive(d_remain)) {
      Scalar t_remain = d_remain.divide(v_max); // full speed
      return exactTimeToV_max(v_cur).add(t_remain);
    } else {
      // ---
    }
    throw TensorRuntimeException.of(d_tar, v_cur);
  }

  /** @param v_cur
   * @return */
  public Scalar exactTimeToV_max(Scalar v_cur) {
    v_range.requireInside(v_cur);
    return v_max.subtract(v_cur).divide(a_max);
  }

  /** @param v_cur
   * @return minimum distance required to achieve v_max regardless of integration method */
  public Scalar minDistToV_max(Scalar v_cur) {
    Scalar timeToV_max = exactTimeToV_max(v_cur);
    return v_cur.multiply(timeToV_max);
  }

  public Scalar exactDistToV_max(Scalar v_cur) {
    Scalar timeToV_max = exactTimeToV_max(v_cur);
    Tensor coeffs = Tensors.of(RealScalar.ZERO, v_cur, a_max.multiply(RationalScalar.HALF));
    return timeToV_max.map(Series.of(coeffs)).Get();
  }

  public static void main(String[] args) {
    Scalar v_max = Quantity.of(10, "m*s^-1");
    Scalar a_max = Quantity.of(3, "m*s^-2");
    LinearVelocity2MinTime linearVelocity2MinTime = new LinearVelocity2MinTime(v_max, a_max);
    Scalar timeToV_max = linearVelocity2MinTime.exactTimeToV_max(Quantity.of(2, "m*s^-1"));
    Scalar minDistToV_max = linearVelocity2MinTime.minDistToV_max(Quantity.of(2, "m*s^-1"));
    Scalar minTime = linearVelocity2MinTime.minTime(Quantity.of(100, "m"), Quantity.of(2, "m*s^-1"));
    linearVelocity2MinTime.exactDistToV_max(Quantity.of(2, "m*s^-1"));
  }
}
