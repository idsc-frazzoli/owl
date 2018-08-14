package ch.ethz.idsc.owl.mapping;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.indexer.Indexer;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.GlcPlannerCallback;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.lie.TensorProduct;

public class ShadowEvaluator {
  private final ShadowMapSpherical shadowMap;

  public ShadowEvaluator(ShadowMapSpherical shadowMap) {
    this.shadowMap = shadowMap;
  }

  /** Evalates min reaction time necessary to avoid shadow region along trajectory */
  public GlcPlannerCallback minReactionTime = new GlcPlannerCallback() {
    @Override
    public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
      final Mat initArea = shadowMap.getInitMap();
      final int RESOLUTION = 10;
      final int MAX_TREACT = 2;
      final float DELTA_TREACT = 0.1f; // [s]
      final Scalar a = DoubleScalar.of(0.85); // [m/s^2];
      final Tensor dir = AngleVector.of(RealScalar.ZERO);
      final Tensor tReactVec = Subdivide.of(0, MAX_TREACT, (int) (MAX_TREACT / DELTA_TREACT));
      final StateTime oob = new StateTime(Tensors.vector(-100, -100, 0), RealScalar.ZERO); // TODO YN not nice
      // -
      Optional<GlcNode> optional = trajectoryPlanner.getBest();
      if (optional.isPresent()) {
        List<TrajectorySample> tail = //
            GlcTrajectories.detailedTrajectoryTo(trajectoryPlanner.getStateIntegrator(), optional.get());
        List<TrajectorySample> trajectory = Trajectories.glue(head, tail);
        // -
        Tensor stateTimeReact = Tensors.empty();
        // -
        Scalar tEnd = Lists.getLast(trajectory).stateTime().time();
        int maxSize = trajectory.stream().filter(c -> Scalars.lessThan(c.stateTime().time(), tEnd.subtract(RealScalar.of(MAX_TREACT)))) //
            .collect(Collectors.toList()).size();
        for (int i = 0; i < maxSize; i++) {
          System.out.println("processing sample " + i + " / " + maxSize);
          StateTime stateTime = trajectory.get(i).stateTime();
          Mat simArea = initArea.clone();
          Indexer indexer = simArea.createIndexer();
          // -
          Scalar vel = RealScalar.ZERO;
          if (stateTime.state().length() == 4) // TODO YN not nice
            vel = stateTime.state().Get(3); // vel is in state
          else if (trajectory.get(i).getFlow().isPresent())
            vel = trajectory.get(i).getFlow().get().getU().Get(0); // vel is in flow
          // -
          Scalar tStop = vel.divide(a); // 0 reaction time
          Scalar dStop = tStop.multiply(vel).divide(RealScalar.of(2));
          Se2Bijection se2Bijection = new Se2Bijection(stateTime.state());
          // -
          Tensor range = Subdivide.of(0, dStop.number(), RESOLUTION);
          Tensor ray = TensorProduct.of(range, dir);
          // -
          shadowMap.updateMap(simArea, stateTime, tStop.number().floatValue());
          boolean clear = !ray.stream().parallel() //
              .map(se2Bijection.forward()) //
              .map(shadowMap::state2pixel) //
              .anyMatch(local -> isMember(indexer, local, simArea.cols(), simArea.rows()));
          // -
          Scalar tMinReact = RealScalar.of(-1);
          if (clear) {
            tMinReact = RealScalar.ZERO;
            for (Tensor tReact : tReactVec) {
              // get stateTime tReact in future
              Optional<TrajectorySample> fut = trajectory.stream()//
                  .skip(i) //
                  .filter(st -> Scalars.lessEquals(stateTime.time().add(tReact), st.stateTime().time())) //
                  .findFirst(); // get new future state on trajectory after tReact
              // -
              if (fut.isPresent()) {
                se2Bijection = new Se2Bijection(fut.get().stateTime().state());
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
        File file = UserHome.file("" + "minReactionTime" + ".csv");
        try {
          Export.of(file, stateTimeReact.map(CsvFormat.strict()));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    }

    private boolean isMember(Indexer indexer, Point pixel, int cols, int rows) {
      return pixel.y() < rows //
          && pixel.x() < cols //
          && indexer.getDouble(pixel.y(), pixel.x()) == 255.0;
    }
  };
}
