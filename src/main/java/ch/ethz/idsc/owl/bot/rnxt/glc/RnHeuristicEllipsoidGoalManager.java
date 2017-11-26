// code by jph and jl
package ch.ethz.idsc.owl.bot.rnxt.glc;

import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.red.Hypot;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Ramp;

/** objective is minimum time
 * path length is measured in Euclidean distance
 * Heuristic is minimum Time along Euclidean distance */
/* package */ class RnHeuristicEllipsoidGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  /** constructor creates a spherical region in R^n with given center and radius.
   * distance measure is Euclidean distance, if radius(i) = infinity => cylinder
   * 
   * @param center vector with length == n
   * @param radius positive */
  public static GoalInterface create(Tensor center, Scalar radius) {
    return new RnHeuristicEllipsoidGoalManager(new EllipsoidRegion( //
        center, Array.of(l -> radius, center.length())));
  }

  // ---
  private final Tensor center;
  private final Tensor radius;

  /** constructor creates a ellipsoid region in R^n x T with given center and radius.
   * distance measure is Euclidean distance, if radius(i) = infinity => cylinder
   * 
   * @param center vector with length == n
   * @param radius vector with length == n & positive in all entries */
  public RnHeuristicEllipsoidGoalManager(EllipsoidRegion ellipsoidRegion) {
    super(new TimeInvariantRegion(ellipsoidRegion));
    center = ellipsoidRegion.center();
    radius = ellipsoidRegion.radius();
  }

  /** shortest Time Cost */
  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  /** Ellipsoid with axis: a,b and vector from Center: v = (x,y)
   * reference:
   * https://math.stackexchange.com/questions/432902/how-to-get-the-radius-of-an-ellipse-at-a-specific-angle-by-knowing-its-semi-majo
   * specific radius at intersection r =
   * 
   * a*b * ||v||
   * ---------------
   * sqrt(a²y² + b²x²) */
  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    // FIXME JONAS the formula is probably conceptually wrong:
    // we don't need distance in along a certain direction but overall shortest distance regardless of direction
    Tensor rnVector = x.subtract(center);
    Scalar root = Hypot.BIFUNCTION.apply(radius.Get(0).multiply(rnVector.Get(1)), radius.Get(1).multiply(rnVector.Get(0)));
    // ---
    Scalar specificRadius = radius.Get(0).multiply(radius.Get(1)).multiply(Norm._2.between(x, center)).divide(root);
    return Ramp.of(Norm._2.between(x, center).subtract(specificRadius)); // <- do not change
  }
}
