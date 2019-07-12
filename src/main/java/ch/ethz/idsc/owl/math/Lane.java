// class by jph, gjoel
package ch.ethz.idsc.owl.math;

import java.io.Serializable;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Nest;

public class Lane implements Serializable {
  private final static Tensor OFS_L = Tensors.vector(0, +1, 0).unmodifiable();
  private final static Tensor OFS_R = Tensors.vector(0, -1, 0).unmodifiable();
  // ---
  private final Tensor controlPoints;
  private final Tensor refined;
  private final Tensor lbound;
  private final Tensor rbound;
  public final Scalar width;

  public Lane(GeodesicInterface geodesicInterface, Tensor controlPoints, Scalar width) {
    this(geodesicInterface, controlPoints, width, 1, 3);
  }

  public Lane(GeodesicInterface geodesicInterface, Tensor controlPoints, Scalar width, int degree, int level) {
    this.controlPoints = controlPoints.unmodifiable();
    this.width = width;
    CurveSubdivision curveSubdivision = new LaneRiesenfeldCurveSubdivision(geodesicInterface, degree);
    refined = Nest.of(curveSubdivision::string, controlPoints, level).unmodifiable();
    lbound = boundary(OFS_L, width).unmodifiable();
    rbound = boundary(OFS_R, width).unmodifiable();
  }

  private Tensor boundary(Tensor base, Scalar width) {
    Tensor ofs = base.multiply(width.multiply(RationalScalar.HALF));
    return Tensor.of(refined.stream().map(Se2GroupElement::new).map(se2GroupElement -> se2GroupElement.combine(ofs)));
  }

  public Tensor controlPoints() {
    return controlPoints;
  }

  public Tensor midLane() {
    return refined;
  }

  public Tensor leftBoundary() {
    return lbound;
  }

  public Tensor rightBoundary() {
    return rbound;
  }
}
