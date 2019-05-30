// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Join;

public abstract class AssistedCurveIntersection extends SimpleCurveIntersection implements AssistedCurveIntersectionInterface {
  protected AssistedCurveIntersection(Scalar radius) {
    super(radius);
  }

  @Override // from AssistedCurveIntersectionInterface
  public final Optional<CurvePoint> cyclic(Tensor tensor, int prevIdx) {
    return universal(tensor, prevIdx % tensor.length(), 0);
  }

  @Override // from AssistedCurveIntersectionInterface
  public final Optional<CurvePoint> string(Tensor tensor, int prevIdx) {
    return prevIdx > tensor.length() ? Optional.empty() : universal(tensor, prevIdx, 1);
  }

  private Optional<CurvePoint> universal(Tensor tensor, int prevIdx, final int first) {
    Tensor shifted = Join.of(tensor.extract(prevIdx, tensor.length()), tensor.extract(0, prevIdx));
    Optional<CurvePoint> optional = universal(shifted, first);
    return optional.map(curvePoint -> new CurvePoint((curvePoint.getIndex() + prevIdx) % tensor.length(), curvePoint.getTensor()));
  }
}
