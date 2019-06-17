// code by jph
package ch.ethz.idsc.owl.math.pursuit;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface GeodesicPursuitInterface {
  /** @return first/current turning ratio required to drive the calculated geodesic curve */
  Optional<Scalar> firstRatio();

  /** @return */
  Tensor ratios();
}
