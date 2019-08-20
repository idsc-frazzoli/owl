// code by ob
package ch.ethz.idsc.sophus.flt.ga;

import java.util.Objects;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.BinaryAverage;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** filter blends extrapolated value with measurement */
public class GeodesicIIR2 implements TensorUnaryOperator {
  private static final Scalar TWO = RealScalar.of(2);
  // ---
  private final BinaryAverage binaryAverage;
  private final Scalar alpha;
  // ---
  private transient Tensor p = null;
  private transient Tensor q = null;

  public GeodesicIIR2(BinaryAverage binaryAverage, Scalar alpha) {
    this.binaryAverage = binaryAverage;
    this.alpha = alpha;
  }

  /** @return extrapolated "best guess" value from the previous predictions */
  private Tensor extrapolate() {
    if (Objects.isNull(p))
      return q;
    return binaryAverage.split(p, q, TWO);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    if (Objects.isNull(q)) {
      q = tensor.copy();
      return q.copy();
    }
    Tensor result = binaryAverage.split(extrapolate(), tensor, alpha);
    p = q;
    q = result.copy();
    return result;
  }
}
