// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.RotateLeft;

public abstract class AssistedCurveIntersection extends SimpleCurveIntersection implements AssistedCurveIntersectionInterface {
  protected AssistedCurveIntersection(Scalar radius, SplitInterface splitInterface) {
    super(radius, splitInterface);
  }

  @Override // from AssistedCurveIntersectionInterface
  public final Optional<CurvePoint> cyclic(Tensor tensor, int prevIdx) {
    return universal(tensor, prevIdx % tensor.length(), 0);
  }

  @Override // from AssistedCurveIntersectionInterface
  public final Optional<CurvePoint> string(Tensor tensor, int prevIdx) {
    return prevIdx > tensor.length() //
        ? Optional.empty()
        : universal(tensor, prevIdx, 1);
  }

  private Optional<CurvePoint> universal(Tensor tensor, int prevIdx, int first) {
    Optional<CurvePoint> optional = universal(RotateLeft.of(tensor, prevIdx), first);
    return optional.map(curvePoint -> curvePoint.withIndex(Math.floorMod(curvePoint.getIndex() + prevIdx, tensor.length())));
  }
}
