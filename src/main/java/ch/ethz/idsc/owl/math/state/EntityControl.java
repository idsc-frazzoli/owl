// code by jph
package ch.ethz.idsc.owl.math.state;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface EntityControl {
  /** @param tail last simulated or estimated state of entity
   * @param now time
   * @return control input to {@link EpisodeIntegrator} */
  Tensor control(StateTime tail, Scalar now);
}
