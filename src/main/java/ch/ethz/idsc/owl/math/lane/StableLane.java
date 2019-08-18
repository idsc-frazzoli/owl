// class by jph, gjoel
package ch.ethz.idsc.owl.math.lane;

import java.io.Serializable;

import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;

/** lane of constant width */
public class StableLane implements LaneInterface, Serializable {
  /** the offset vectors are not magic constants but are multiplied by width */
  private final static Tensor OFS_L = Tensors.vector(0, +1, 0).unmodifiable();
  private final static Tensor OFS_R = Tensors.vector(0, -1, 0).unmodifiable();

  /** @param controlPoints in SE2
   * @param tensorUnaryOperator for instance
   * LaneRiesenfeldCurveSubdivision.of(Clothoid3.INSTANCE, 1)::string
   * @param level non-negative
   * @param width
   * @return */
  public static LaneInterface of( //
      Tensor controlPoints, TensorUnaryOperator tensorUnaryOperator, int level, Scalar width) {
    return new StableLane( //
        controlPoints, //
        Nest.of(tensorUnaryOperator, controlPoints, level).unmodifiable(), //
        width);
  }

  // ---
  private final Tensor controlPoints;
  private final Tensor refined;
  private final Tensor lbound;
  private final Tensor rbound;
  private final Tensor margins;

  /** Hint: constructor is public because controlPoint and refined may be generated using
   * various methods from outside
   * 
   * @param controlPoints
   * @param refined
   * @param width */
  public StableLane(Tensor controlPoints, Tensor refined, Scalar width) {
    this.controlPoints = controlPoints.unmodifiable();
    this.refined = refined;
    lbound = boundary(OFS_L, width).unmodifiable();
    rbound = boundary(OFS_R, width).unmodifiable();
    Scalar margin = width.multiply(RationalScalar.HALF);
    margins = ConstantArray.of(margin, refined.length());
  }

  private Tensor boundary(Tensor base, Scalar width) {
    Tensor ofs = base.multiply(width.multiply(RationalScalar.HALF));
    return Tensor.of(refined.stream() //
        .map(Se2GroupElement::new) //
        .map(se2GroupElement -> se2GroupElement.combine(ofs)));
  }

  @Override // from LaneInterface
  public Tensor controlPoints() {
    return controlPoints;
  }

  @Override // from LaneInterface
  public Tensor midLane() {
    return refined;
  }

  @Override // from LaneInterface
  public Tensor leftBoundary() {
    return lbound;
  }

  @Override // from LaneInterface
  public Tensor rightBoundary() {
    return rbound;
  }

  @Override // from LaneInterface
  public Tensor margins() {
    return margins;
  }
}
