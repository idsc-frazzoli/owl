// code by jph
package ch.ethz.idsc.owl.sim;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.gui.GeometricLayer;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.math.Degree;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

// TODO implementation can be made more efficient
public class LidarEmulator implements RenderInterface {
  public static final Tensor DEFAULT = Subdivide.of(Degree.of(+90), Degree.of(-90), 32);
  public static final Tensor RAYDEMO = Subdivide.of(Degree.of(+90), Degree.of(-90), 5);
  // ---
  public static final Scalar RANGE_MAX = RealScalar.of(5.0);
  /** if the number of lasers are bounded by MAX_RAYS each rays is visualized as a line,
   * otherwise a polygonal area is drawn */
  private static final int MAX_RAYS = 11;
  private static final Color COLOR_LASER_RAY = new Color(255, 0, 0, 32);
  private static final Color COLOR_FREESPACE_FILL = new Color(0, 255, 0, 16);
  private static final Color COLOR_FREESPACE_DRAW = new Color(0, 255, 0, 64);
  // ---
  private final Supplier<StateTime> supplier;
  private final TrajectoryRegionQuery raytraceQuery;
  private final Tensor directions;
  private final List<Tensor> localRays = new ArrayList<>();

  /** @param resolution angular resolution (should be tensor)
   * @param supplier
   * @param raytraceQuery */
  public LidarEmulator(Tensor sampling, Supplier<StateTime> supplier, TrajectoryRegionQuery raytraceQuery) {
    this.supplier = supplier;
    this.raytraceQuery = raytraceQuery;
    // ---
    directions = Tensor.of(sampling.stream().map(Scalar.class::cast).map(AngleVector::of));
    for (Tensor dir : directions)
      localRays.add(Tensor.of(Subdivide.of(RealScalar.ZERO, RANGE_MAX, 60).stream() // magic const
          .map(Scalar.class::cast) //
          .map(dir::multiply)));
  }

  /** @param stateTime
   * @return ranges as observed at given state-time */
  public Tensor detectRange(StateTime stateTime) {
    Scalar time = stateTime.time();
    Se2Bijection se2Bijection = new Se2Bijection(stateTime.state());
    TensorUnaryOperator forward = se2Bijection.forward();
    // Stopwatch stopwatch = Stopwatch.started();
    Tensor range = Tensor.of(localRays.stream().parallel() //
        .map(rays -> {
          Optional<Tensor> first = rays.stream() //
              .filter(local -> raytraceQuery.isMember(new StateTime(forward.apply(local), time))) //
              .findFirst();
          return first.isPresent() ? Norm._2.ofVector(first.get()) : RANGE_MAX;
        }));
    // System.out.println(stopwatch.display_seconds());
    return range;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final Tensor range = detectRange(supplier.get()).unmodifiable();
    // ---
    Se2Bijection se2Bijection = new Se2Bijection(supplier.get().state());
    geometricLayer.pushMatrix(se2Bijection.forward_se2());
    Tensor polygon = range.pmul(directions);
    Tensor origin = Array.zeros(2);
    if (range.length() <= MAX_RAYS) {
      graphics.setColor(COLOR_LASER_RAY);
      for (Tensor point : polygon) {
        Shape shape = geometricLayer.toVector(origin, point);
        graphics.draw(shape);
      }
    } else {
      polygon.append(origin);
      Path2D path2D = geometricLayer.toPath2D(polygon);
      graphics.setColor(COLOR_FREESPACE_FILL);
      graphics.fill(path2D);
      path2D.closePath();
      graphics.setColor(COLOR_FREESPACE_DRAW);
      graphics.draw(path2D);
    }
    geometricLayer.popMatrix();
  }
}
