// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Rationalize;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class BSpline1CurveSubdivision implements TensorUnaryOperator {
  private final GeodesicInterface geodesicInterface;

  public BSpline1CurveSubdivision(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor curve = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      Tensor p = tensor.get(index);
      Tensor q = tensor.get((index + 1) % tensor.length());
      curve.append(p);
      curve.append(geodesicInterface.split(p, q, RationalScalar.HALF));
    }
    return curve;
  }

  public static void main(String[] args) {
    BSpline1CurveSubdivision subdivision = new BSpline1CurveSubdivision(EuclideanGeodesic.INSTANCE);
    ScalarUnaryOperator operator = Rationalize.withDenominatorLessEquals(100);
    Tensor tensor = CirclePoints.of(4).map(operator);
    Tensor curve = subdivision.apply(tensor);
    System.out.println(curve);
    // Tensor tensor = INSTANCE.split(Tensors.vector(10, 1), Tensors.vector(11, 0), RealScalar.of(-1));
    // System.out.println(tensor);
  }
}
