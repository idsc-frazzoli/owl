// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** GeodesicAverage applies the resulting splits of a weight mask and a tree shape to a sequence of control points */
public class GeodesicAverage implements TensorUnaryOperator {
  // TODO OB: Tests
  /** @param geodesicInterface
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, Tensor splits) {
    return new GeodesicAverage(geodesicInterface, splits);
  }

  // ---
  private final GeodesicInterface geodesicInterface;
  private final Tensor splits;

  private GeodesicAverage(GeodesicInterface geodesicInterface, Tensor splits) {
    this.geodesicInterface = Objects.requireNonNull(geodesicInterface);
    this.splits = splits;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor sequence) {
    return recursion(splits, sequence);
  }

  public Tensor recursion(Tensor splits, Tensor sequence) {
    Tensor left = splits.get(0);
    Tensor right = splits.get(1);
    Scalar alpha = splits.Get(2);
    if (left.length() == 4 && right.length() == 4) {
      return geodesicInterface.split(recursion(left, sequence), recursion(right, sequence), alpha);
    } else if (left.length() == 4) {
      Scalar q = splits.Get(1);
      return geodesicInterface.split(recursion(left, sequence), sequence.get(q.number().intValue()), alpha);
    } else if (right.length() == 4) {
      Scalar p = splits.Get(0);
      return geodesicInterface.split(sequence.get(p.number().intValue()), recursion(right, sequence), alpha);
    } else {
      Scalar p = splits.Get(0);
      Scalar q = splits.Get(1);
      return geodesicInterface.split(sequence.get(p.number().intValue()), sequence.get(q.number().intValue()), alpha);
    }
  }
}
