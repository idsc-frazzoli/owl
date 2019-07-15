// class by jph, gjoel
package ch.ethz.idsc.owl.math.lane;

import java.io.Serializable;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Nest;

/** lane of constant width */
public class StableLane implements LaneInterface, Serializable {
  private final static Tensor OFS_L = Tensors.vector(0, +1, 0).unmodifiable();
  private final static Tensor OFS_R = Tensors.vector(0, -1, 0).unmodifiable();

  // ---
  public static LaneInterface of(SplitInterface splitInterface, Tensor controlPoints, Scalar width) {
    return of(splitInterface, controlPoints, width, 1, 3);
  }

  public static LaneInterface of(SplitInterface splitInterface, Tensor controlPoints, Scalar width, int degree, int level) {
    CurveSubdivision curveSubdivision = new LaneRiesenfeldCurveSubdivision(splitInterface, degree);
    Tensor refined = Nest.of(curveSubdivision::string, controlPoints, level).unmodifiable();
    return new StableLane(controlPoints, refined, width);
  }

  // ---
  private final Tensor controlPoints;
  private final Tensor refined;
  private final Tensor lbound;
  private final Tensor rbound;

  private StableLane(Tensor controlPoints, Tensor refined, Scalar width) {
    this.controlPoints = controlPoints.unmodifiable();
    this.refined = refined;
    lbound = boundary(OFS_L, width).unmodifiable();
    rbound = boundary(OFS_R, width).unmodifiable();
  }

  private Tensor boundary(Tensor base, Scalar width) {
    Tensor ofs = base.multiply(width.multiply(RationalScalar.HALF));
    return Tensor.of(refined.stream() //
        .map(Se2GroupElement::new) //
        .map(se2GroupElement -> se2GroupElement.combine(ofs)));
  }

  @Override
  public Tensor controlPoints() {
    return controlPoints;
  }

  @Override
  public Tensor midLane() {
    return refined;
  }

  @Override
  public Tensor leftBoundary() {
    return lbound;
  }

  @Override
  public Tensor rightBoundary() {
    return rbound;
  }
}
