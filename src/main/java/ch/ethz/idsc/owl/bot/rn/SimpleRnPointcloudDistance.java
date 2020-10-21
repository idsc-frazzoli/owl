// code by mg, jph
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.red.Norm;

/** shortest distance from given point to a collection of points
 * 
 * class name reflects that results is obtained in a simple manner:
 * by iterating over all points. Due to the O(n) complexity of the
 * query, the use of the algorithm is not recommended for most
 * applications, perhaps only for testing purpose.
 * 
 * The implementation is used in external libraries.
 * 
 * @see RnPointcloudRegion which uses a nd-map */
public class SimpleRnPointcloudDistance implements TensorScalarFunction {
  private static final long serialVersionUID = -8241926217737812068L;

  /** @param points
   * @param norm
   * @return */
  public static TensorScalarFunction of(Tensor points, Norm norm) {
    return new SimpleRnPointcloudDistance(points, norm);
  }

  /***************************************************/
  private final Tensor points;
  private final Norm norm;

  private SimpleRnPointcloudDistance(Tensor points, Norm norm) {
    this.points = points;
    this.norm = norm;
  }

  @Override
  public Scalar apply(Tensor point) {
    return points.stream() //
        .map(vector -> norm.between(vector, point)) //
        .min(Scalars::compare).get();
  }
}
