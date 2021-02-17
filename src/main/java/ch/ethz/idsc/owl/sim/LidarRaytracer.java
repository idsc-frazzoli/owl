// code by jph
package ch.ethz.idsc.owl.sim;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.sophus.hs.r2.Se2Bijection;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.lie.TensorProduct;
import ch.ethz.idsc.tensor.lie.r2.AngleVector;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.red.Max;

/** LONGTERM implementation can be made more efficient */
public class LidarRaytracer implements Serializable {
  private final Tensor directions;
  private final Scalar max_range;
  private final List<Tensor> localRays;

  /** @param angles vector
   * @param ranges vector with non-negative entries */
  public LidarRaytracer(Tensor angles, Tensor ranges) {
    directions = Tensor.of(angles.stream().map(Scalar.class::cast).map(AngleVector::of));
    max_range = ranges.stream().map(Scalar.class::cast).reduce(Max::of).get();
    localRays = directions.stream().map(dir -> TensorProduct.of(ranges, dir)).collect(Collectors.toList());
  }

  /** @param trajectoryRegionQuery
   * @param stateTime
   * @return ranges as observed at given state-time */
  public Tensor scan(StateTime stateTime, TrajectoryRegionQuery trajectoryRegionQuery) {
    Scalar time = stateTime.time();
    Se2Bijection se2Bijection = new Se2Bijection(stateTime.state());
    TensorUnaryOperator forward = se2Bijection.forward();
    return Tensor.of(localRays.stream().parallel() //
        .map(ray -> ray.stream() //
            .filter(local -> trajectoryRegionQuery.isMember(new StateTime(forward.apply(local), time))) //
            .findFirst() //
            .map(Vector2Norm::of) //
            .orElse(max_range)));
  }

  /** @param scan vector obtained by {@link #scan(TrajectoryRegionQuery, StateTime)}
   * @return list of 2D-points */
  public Tensor toPoints(Tensor scan) {
    return scan.pmul(directions);
  }
}
