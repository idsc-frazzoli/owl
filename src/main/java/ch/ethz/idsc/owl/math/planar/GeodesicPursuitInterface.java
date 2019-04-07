// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface GeodesicPursuitInterface {
  /** @return first/current turning ratio required to drive the calculated geodesic curve */
  Optional<Scalar> firstRatio();

  Tensor ratios();

  /** @return Tensor of planned geodesic curve trajectory */
  Tensor curve();
}
