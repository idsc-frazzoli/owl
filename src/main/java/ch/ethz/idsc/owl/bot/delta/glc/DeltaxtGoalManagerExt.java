// code by jl
package ch.ethz.idsc.owl.bot.delta.glc;

import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.Sqrt;

class DeltaxtGoalManagerExt extends SimpleTrajectoryRegionQuery implements GoalInterface {
  private final Tensor center;
  private final Tensor radius;
  private final Scalar maxSpeed;

  public DeltaxtGoalManagerExt(Tensor center, Tensor radius, Scalar maxSpeed) {
    super(new TimeInvariantRegion(new EllipsoidRegion(center, radius)));
    this.center = center;
    this.maxSpeed = maxSpeed;
    if (!radius.Get(0).equals(radius.Get(1)))
      throw TensorRuntimeException.of(radius); // x-y radius have to be equal
    this.radius = radius;
  }

  public DeltaxtGoalManagerExt(Region<Tensor> region, Tensor center, Tensor radius, Scalar maxSpeed) {
    super(new TimeInvariantRegion(region));
    this.center = center;
    this.maxSpeed = maxSpeed;
    if (!radius.Get(0).equals(radius.Get(1)))
      throw TensorRuntimeException.of(radius); // x-y radius have to be equal
    this.radius = radius.Get(0);
  }
  // --

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  /** Ellipsoid with axis: a,b and vector from Center: v = (x,y)
   * specific radius at intersection:
   * r :
   * 
   * a*b * ||v||
   * ---------------
   * sqrt(a²y² + b²x²) */
  @Override
  public Scalar minCostToGoal(Tensor x) {
    // B. Paden: A Generalized Label Correcting Method for Optimal Kinodynamic Motion Planning
    // p. 79 Eq: 6.4.14
    // Heuristic needs to be underestimating: (Euclideandistance-radius) / (MaxControl+Max(|Vectorfield|)
    int toIndex = x.length() - 1;
    Tensor r2x = x.extract(0, toIndex);
    Tensor r2Center = center.extract(0, toIndex);
    Tensor r2Vector = r2x.subtract(r2Center);
    Tensor r2Radius = radius.extract(0, toIndex);
    Scalar root = Sqrt.of(Power.of(r2Radius.Get(0).multiply(r2Vector.Get(1)), 2)//
        .add(Power.of(r2Radius.Get(1).multiply(r2Vector.Get(0)), 2)));
    Scalar specificRadius = radius.Get(0).multiply(radius.Get(1)).multiply(Norm._2.ofVector(r2x.subtract(r2Center))).divide(root);
    return Ramp.of(Norm._2.between(r2x, r2Center).subtract(specificRadius).divide(maxSpeed)); // <- do not change
  }
}
