// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Abs;

/** axis-aligned region of infinity extension in the direction of other axes */
public class FreeBoundedIntervalRegion extends ImplicitFunctionRegion implements Serializable {
  private static final Scalar HALF = RationalScalar.HALF;
  // ---
  private final int index;
  private final Scalar semiwidth;
  private final Scalar center;

  public FreeBoundedIntervalRegion(int index, Scalar lo, Scalar hi) {
    if (Scalars.lessEquals(hi, lo))
      throw TensorRuntimeException.of(lo, hi);
    this.index = index;
    semiwidth = hi.subtract(lo).multiply(HALF);
    center = hi.add(lo).multiply(HALF);
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor x) {
    return semiwidth.subtract(Abs.between(x.Get(index), center));
  }
}
