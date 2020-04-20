// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.math.WeightingInterface;

@FunctionalInterface
public interface LogWeighting {
  /** @param flattenLogManifold
   * @return */
  WeightingInterface from(FlattenLogManifold flattenLogManifold);
}
