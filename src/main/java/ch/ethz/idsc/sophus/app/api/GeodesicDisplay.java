// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.Exponential;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Hint: the interface GeodesicDisplay is intended for use in the demo layer
 * but not in the library functions. */
public interface GeodesicDisplay {
  /** @return */
  GeodesicInterface geodesicInterface();

  int dimensions();

  /** @return polygon to visualize the control point */
  Tensor shape();

  /** @param xya
   * @return control point */
  Tensor project(Tensor xya);

  /** @param p control point
   * @return vector of length 2 with grid coordinates {x, y} */
  Tensor toPoint(Tensor p);

  /** @param p control point
   * @return matrix with dimensions 3 x 3 */
  Tensor matrixLift(Tensor p);

  /** @return lie group if the space is a lie group, or null if function is not applicable */
  LieGroup lieGroup();

  /** @return lie exponential if the space is a lie group, or null if function is not applicable */
  Exponential exponential();

  HsExponential hsExponential();

  /** @return flattenLogManifold, or null if not applicable */
  FlattenLogManifold flattenLogManifold();

  /** @param p control point
   * @param q control point
   * @return (pseudo-) distance between given control points p and q
   * @throws Exception if functionality is not supported */
  Scalar parametricDistance(Tensor p, Tensor q);

  /** @return biinvariantMean, or null, if geodesic space does not support the computation of an biinvariant mean */
  BiinvariantMean biinvariantMean();

  @Override // from Object
  String toString();
}
