// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface HermiteSubdivision {
  Tensor iterate();
}
