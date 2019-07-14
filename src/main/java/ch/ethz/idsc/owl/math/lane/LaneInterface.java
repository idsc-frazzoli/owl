// code by gjoel, jph
package ch.ethz.idsc.owl.math.lane;

import ch.ethz.idsc.tensor.Tensor;

public interface LaneInterface {
  Tensor controlPoints();

  Tensor midLane();

  Tensor leftBoundary();

  Tensor rightBoundary();
}
