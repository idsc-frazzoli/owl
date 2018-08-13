package ch.ethz.idsc.owl.mapping;

import java.util.List;
import java.util.Optional;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.indexer.Indexer;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.lie.TensorProduct;

public enum ShadowEvaluator {
  ;
  /** Evalates min reaction time necessary to avoid shadow region along trajectory */
  public static void minReactionTime(ShadowMapSpherical shadowMap, List<StateTime> trajectory, LidarEmulator lidar) {
    final Mat initArea = shadowMap.getInitMap();
    final int RESOLUTION = 10;
    final int MAX_TREACT = 3;
    final float DELTA_TREACT = 0.2f; // [s]
    final Scalar a = DoubleScalar.of(0.8); // [m/s^2];
    final Tensor dir = AngleVector.of(RealScalar.ZERO);
    final Tensor tReactVec = Subdivide.of(0, MAX_TREACT, (int) (MAX_TREACT / DELTA_TREACT));
    final StateTime oob = new StateTime(Tensors.vector(-100, -100, -100), RealScalar.ZERO); // TODO YN not nice
    // -
    Tensor stateTimeReact = Tensors.empty();
    // -
    for (int i = 0; i < trajectory.size(); i++) {
      StateTime stateTime = trajectory.get(i);
      Mat simArea = initArea.clone();
      Indexer indexer = simArea.createIndexer();
      // -
      Scalar vel = stateTime.state().Get(3);
      Scalar tStop = vel.divide(a); // 0 reaction time
      Scalar dStop = tStop.multiply(vel).divide(RealScalar.of(2));
      Se2Bijection se2Bijection = new Se2Bijection(stateTime.state());
      // -
      Tensor range = Subdivide.of(0, dStop.number(), RESOLUTION);
      Tensor ray = TensorProduct.of(range, dir);
      // -
      boolean clear = ray.stream().parallel() //
          .map(se2Bijection.forward()) //
          .map(shadowMap::state2pixel) //
          .anyMatch(local -> isMember(indexer, local, simArea.cols(), simArea.rows()));
      //
      shadowMap.updateMap(simArea, stateTime, tStop.number().floatValue());
      //
      Scalar tMinReact = RealScalar.of(-1);
      if (clear) {
        // -
        for (Tensor tReact : tReactVec) {
          // get stateTime tReact in future
          Optional<StateTime> fut = trajectory.stream()//
              .skip(i) //
              .filter(st -> Scalars.lessEquals(stateTime.time().add(tReact), st.time())) //
              .findFirst(); // get new future state on trajectory after tReact
          // -
          if (fut.isPresent()) {
            se2Bijection = new Se2Bijection(fut.get().state());
            shadowMap.updateMap(simArea, oob, DELTA_TREACT);
            dStop = tStop.add(tReact).multiply(vel).divide(RealScalar.of(2));
            Tensor st = dir.multiply(dStop);
            Point px = shadowMap.state2pixel(se2Bijection.forward().apply(st));
            boolean intersect = isMember(indexer, px, simArea.cols(), simArea.rows());
            if (intersect)
              break;
            tMinReact = tReact.Get();
          }
        }
      }
      Tensor concat = Tensors.of(stateTime.state(), stateTime.time(), tMinReact);
      stateTimeReact.append(concat);
    }
  }

  private static boolean isMember(Indexer indexer, Point pixel, int cols, int rows) {
    return pixel.y() < rows //
        && pixel.x() < cols //
        && indexer.getDouble(pixel.y(), pixel.x()) == 255.0;
  }
}
