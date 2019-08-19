// code by gjoel, jph
package ch.ethz.idsc.owl.math.lane;

import ch.ethz.idsc.tensor.Tensor;

public interface LaneInterface {
  // TODO JPH OWL 053 possibly remove function
  /** @return points used to generate/describe lane */
  Tensor controlPoints();

  /** @return points of center line of lane */
  Tensor midLane();

  /** @return points of left lane boundary */
  Tensor leftBoundary();

  /** @return points of right lane boundary */
  Tensor rightBoundary();

  /** @return distances to boundary boundary from {@link LaneInterface#midLane()} */
  Tensor margins();
  // maybe also add curvature at some point
}
