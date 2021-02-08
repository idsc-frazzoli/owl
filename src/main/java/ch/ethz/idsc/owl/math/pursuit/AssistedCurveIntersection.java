// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.RotateLeft;
import ch.ethz.idsc.tensor.itp.BinaryAverage;

public abstract class AssistedCurveIntersection extends SimpleCurveIntersection //
    implements AssistedCurveIntersectionInterface {
  /** @param radius non-negative
   * @param binaryAverage
   * @throws Exception if given radius is negative */
  protected AssistedCurveIntersection(Scalar radius, BinaryAverage binaryAverage) {
    super(radius, binaryAverage);
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
