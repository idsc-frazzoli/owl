package ch.ethz.idsc.owl.mapping;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.indexer.Indexer;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.data.img.CvHelper;
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
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Degree;

public class ShadowEvaluator {
  private final ShadowMapSpherical shadowMap;
  //
  final int RESOLUTION = 10;
  final int MAX_TREACT = 1;
  final String id;
  final float delta_treact; // [s]
  final Scalar a = DoubleScalar.of(6.0); // [m/s^2];
  final Tensor dir = AngleVector.of(RealScalar.ZERO);
  final Tensor tReactVec;
  final StateTime oob = new StateTime(Tensors.vector(-100, -100, 0), RealScalar.ZERO); // TODO YN not nice

  public ShadowEvaluator(ShadowMapSpherical shadowMap, String id) {
    this.shadowMap = shadowMap;
    this.delta_treact = shadowMap.getMinTimeDelta();
    this.id = id;
    tReactVec = Subdivide.of(0, MAX_TREACT, (int) (MAX_TREACT / delta_treact));
  }

  /** Evalates time to react (TTR) along trajectory
   * The Time-To-React (TTR) is the maximum time we can continue the current trajectory
   * before we have to execute an evasive trajectory to avoid entering the set of colliding states
   * here, colliding states are states intersecting with the shadow region */
  public GlcPlannerCallback timeToReact = new GlcPlannerCallback() {
    @Override
    public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
      Function<StateTime, Mat> mapSupplier = new Function<StateTime, Mat>() {
        @Override
        public Mat apply(StateTime t) {
          return shadowMap.getInitMap();
        }
      };
      Optional<GlcNode> optional = trajectoryPlanner.getBest();
      if (optional.isPresent()) {
        List<TrajectorySample> tail = //
            GlcTrajectories.detailedTrajectoryTo(trajectoryPlanner.getStateIntegrator(), optional.get());
        List<TrajectorySample> trajectory = Trajectories.glue(head, tail);
        // List<TrajectorySample> trajectory = Nodes.listFromRoot(optional.get()).stream().map(n -> new TrajectorySample(n.stateTime(), n.flow()))
        // .collect(Collectors.toList());
        // ---
        Tensor minTimeReact = timeToReact(trajectory, mapSupplier);
        try {
          File file = UserHome.file("" + "minReactionTime_" + id + ".csv");
          Export.of(file, minTimeReact.get().map(CsvFormat.strict()));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    }
  };
  // -
  /** Evalates time to react (TTR) along trajectory for each sector */
  public GlcPlannerCallback sectorTimeToReact = new GlcPlannerCallback() {
    @Override
    public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
      Tensor angles = Subdivide.of(Degree.of(0), Degree.of(360), 72);
      Optional<GlcNode> optional = trajectoryPlanner.getBest();
      if (optional.isPresent()) {
        List<TrajectorySample> tail = //
            GlcTrajectories.detailedTrajectoryTo(trajectoryPlanner.getStateIntegrator(), optional.get());
        List<TrajectorySample> trajectory = tail; // Trajectories.glue(head, tail);
        // System.out.println(optional.get().costFromRoot());
        // List<TrajectorySample> trajectory = Nodes.listFromRoot(optional.get()).stream().map(n -> new TrajectorySample(n.stateTime(), n.flow()))
        // .collect(Collectors.toList());
        // ---
        Tensor mtrMatrix = Tensors.empty();
        for (int i = 0; i < angles.length() - 1; i++) {
          System.out.println("Evaluating sector " + (i + 1) + " / " + (angles.length() - 1));
          final int fi = i;
          Function<StateTime, Mat> mapSupplier = new Function<StateTime, Mat>() {
            @Override
            public Mat apply(StateTime t) {
              return maskSector(shadowMap.getInitMap(), t.state(), angles.extract(fi, fi + 2));
            }
          };
          Tensor minTimeReact = timeToReact(trajectory, mapSupplier);
          mtrMatrix.append(minTimeReact);
        }
        try {
          File file1 = UserHome.file("" + "/Desktop/eval/minSecTTR_" + id + ".csv");
          File file2 = UserHome.file("" + "/Desktop/eval/state_" + id  + ".csv");
          Export.of(file1, mtrMatrix.map(CsvFormat.strict()));
          Export.of(file2, Tensor.of(trajectory.stream().map(a -> a.stateTime().state())).map(CsvFormat.strict()));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    }
  };

  private Tensor timeToReact(List<TrajectorySample> trajectory, Function<StateTime, Mat> mapSupplier) {
    // -
    Tensor timeToReactVec = Tensors.empty();
    // -
    Scalar tEnd = Lists.getLast(trajectory).stateTime().time();
    int maxSize = trajectory.stream().filter(c -> Scalars.lessThan(c.stateTime().time(), tEnd.subtract(RealScalar.of(MAX_TREACT)))) //
        .collect(Collectors.toList()).size();
    for (int i = 0; i < maxSize; i++) {
      // System.out.println("processing sample " + i + " / " + maxSize);
      StateTime stateTime = trajectory.get(i).stateTime();
      Mat simArea = mapSupplier.apply(stateTime).clone();
      Indexer indexer = simArea.createIndexer();
      // -
      Scalar vel = RealScalar.ZERO;
      if (stateTime.state().length() == 4) // TODO YN not nice
        vel = stateTime.state().Get(3); // vel is in state
      else if (trajectory.get(i).getFlow().isPresent())
        vel = trajectory.get(i).getFlow().get().getU().Get(0); // vel is in flow
      // -
      Scalar tBrake = vel.divide(a); // 0 reaction time
      Scalar dBrake = tBrake.multiply(vel).divide(RealScalar.of(2));
      Se2Bijection se2Bijection = new Se2Bijection(stateTime.state());
      // -
      Tensor range = Subdivide.of(0, dBrake.number(), RESOLUTION);
      Tensor ray = TensorProduct.of(range, dir);
      // -
      shadowMap.updateMap(simArea, stateTime, tBrake.number().floatValue());
      boolean clear = !ray.stream().parallel() //
          .map(se2Bijection.forward()) //
          .map(shadowMap::state2pixel) //
          .anyMatch(local -> isMember(indexer, local, simArea.cols(), simArea.rows()));
      // -
      Scalar timeToReact = RealScalar.of(-1);
      if (clear) {
        timeToReact = RealScalar.ZERO;
        for (Tensor tReact : tReactVec) {
          // get stateTime tReact in future
          Optional<TrajectorySample> fut = trajectory.stream()//
              .skip(i) //
              .filter(st -> Scalars.lessEquals(stateTime.time().add(tReact), st.stateTime().time())) //
              .findFirst(); // get new future state on trajectory after tReact
          // -
          if (fut.isPresent()) {
            se2Bijection = new Se2Bijection(fut.get().stateTime().state());
            shadowMap.updateMap(simArea, oob, delta_treact); // update sr by delta_treact w.o. new lidar info
            dBrake = tBrake.multiply(vel).divide(RealScalar.of(2)); // FIXME vel should be updated to vel at fut
            Tensor st = dir.multiply(dBrake);
            Point px = shadowMap.state2pixel(se2Bijection.forward().apply(st));
            boolean intersect = isMember(indexer, px, simArea.cols(), simArea.rows());
            if (intersect)
              break;
            timeToReact = tReact.Get();
          }
        }
      }
      timeToReactVec.append(timeToReact);
    }
    return timeToReactVec;
  }

  private static boolean isMember(Indexer indexer, Point pixel, int cols, int rows) {
    return pixel.y() < rows //
        && pixel.x() < cols //
        && pixel.y() >= 0 //
        && pixel.x() >= 0 //
        && indexer.getDouble(pixel.y(), pixel.x()) == 255.0;
  }

  /** Mask mat with a sector with origin at state and
   * @param mat
   * @param state origin of sector
   * @param angles
   * @return masked mat */
  private Mat maskSector(final Mat mat, Tensor state, Tensor angles) {
    // build polygon
    Se2Bijection se2Bijection = new Se2Bijection(state);
    TensorUnaryOperator forward = se2Bijection.forward();
    // -
    Scalar range = RealScalar.of(100); // TODO magic const
    Tensor rays = Tensor.of(angles.stream().map(Scalar.class::cast).map(AngleVector::of)).multiply(range);
    rays.append(Tensors.vector(0, 0)); // append origin
    // get pixel coordinates as Points
    Tensor polyTens = Tensor.of(rays.stream() //
        .map(forward::apply) //
        .map(shadowMap::state2pixel) //
        .map(a -> Tensors.vector(a.x(), a.y())));
    Point polyPoint = CvHelper.tensorToPoint(polyTens);
    Mat segment = new Mat(mat.size(), mat.type(), opencv_core.Scalar.BLACK);
    // opencv_imgproc.fillPoly(segment, polyPoint, new int[] { 3 }, 1, opencv_core.Scalar.WHITE);
    opencv_imgproc.fillConvexPoly(segment, polyPoint, 3, opencv_core.Scalar.WHITE);
    opencv_core.bitwise_and(mat, segment, segment);
    return segment;
  }
}
