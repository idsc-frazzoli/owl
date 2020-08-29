// class by jph, gjoel
package ch.ethz.idsc.owl.lane;

import java.io.Serializable;

import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;

/** lane of constant width */
public class StableLane implements LaneInterface, Serializable {
  /** the offset vectors are not magic constants but are multiplied by width */
  private final static Tensor OFS_L = Tensors.vector(0, +1, 0).unmodifiable();
  private final static Tensor OFS_R = Tensors.vector(0, -1, 0).unmodifiable();

  /** @param controlPoints may be null
   * @param refined
   * @param halfWidth */
  public static LaneInterface of(Tensor controlPoints, Tensor refined, Scalar halfWidth) {
    return new StableLane(controlPoints, refined, halfWidth);
  }

  /***************************************************/
  private final Tensor controlPoints;
  private final Tensor refined;
  private final Tensor lbound;
  private final Tensor rbound;
  private final Tensor margins;

  private StableLane(Tensor controlPoints, Tensor refined, Scalar halfWidth) {
    this.controlPoints = controlPoints;
    this.refined = refined;
    lbound = boundary(OFS_L, halfWidth).unmodifiable();
    rbound = boundary(OFS_R, halfWidth).unmodifiable();
    margins = ConstantArray.of(halfWidth, refined.length());
  }

  private Tensor boundary(Tensor base, Scalar halfWidth) {
    Tensor ofs = base.multiply(halfWidth);
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
