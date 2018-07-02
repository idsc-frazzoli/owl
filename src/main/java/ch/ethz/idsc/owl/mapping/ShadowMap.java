// code by ynager
package ch.ethz.idsc.owl.mapping;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

public interface ShadowMap {
  // void updateMap(Mat area, StateTime stateTime, float timeDelta);
  void updateMap(StateTime stateTime, float timeDelta);

  Mat getInitMap();

  Point state2pixel(Tensor state);
}
