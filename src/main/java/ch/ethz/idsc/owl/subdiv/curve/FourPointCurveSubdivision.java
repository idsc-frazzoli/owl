// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Rationalize;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class FourPointCurveSubdivision implements TensorUnaryOperator {
  private final static Scalar WEIGHTA = RationalScalar.of(+9, 8);
  private final static Scalar WEIGHTB = RationalScalar.of(-1, 8);
  // ---
  private final GeodesicInterface geodesicInterface;

  public FourPointCurveSubdivision(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor curve = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      Tensor p = tensor.get((index - 1 + tensor.length()) % tensor.length());
      Tensor q = tensor.get(index);
      Tensor r = tensor.get((index + 1) % tensor.length());
      Tensor t = tensor.get((index + 2) % tensor.length());
      Tensor pq = geodesicInterface.split(p, q, WEIGHTA);
      Tensor rt = geodesicInterface.split(r, t, WEIGHTB);
      curve.append(q);
      curve.append(geodesicInterface.split(pq, rt, RationalScalar.HALF));
    }
    return curve;
  }

  public static void main(String[] args) {
    FourPointCurveSubdivision subdivision = new FourPointCurveSubdivision(EuclideanGeodesic.INSTANCE);
    ScalarUnaryOperator operator = Rationalize.withDenominatorLessEquals(100);
    Tensor tensor = CirclePoints.of(4).map(operator);
    Tensor curve = Nest.of(subdivision, tensor, 1);
    System.out.println(curve);
    // Tensor tensor = INSTANCE.split(Tensors.vector(10, 1), Tensors.vector(11, 0), RealScalar.of(-1));
    // System.out.println(tensor);
  }
}
