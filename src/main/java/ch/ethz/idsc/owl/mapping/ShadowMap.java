package ch.ethz.idsc.owl.mapping;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

public interface ShadowMap {
  public void updateMap(Mat area, StateTime stateTime, float timeDelta);

  public void updateMap(StateTime stateTime, float timeDelta);

  public Mat getInitMap();

  public Point state2pixel(Tensor state);
}
