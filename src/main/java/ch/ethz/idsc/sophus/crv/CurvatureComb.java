// code by jph
package ch.ethz.idsc.sophus.crv;

import java.util.Optional;

import ch.ethz.idsc.sophus.math.Nocopy;
import ch.ethz.idsc.sophus.math.Normal2D;
import ch.ethz.idsc.sophus.math.SignedCurvature2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.NormalizeUnlessZero;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

/** .
 * G0 - Position, tangent of curve is not continuous, example: polygons
 * G1 - Tangent, curvature is discontinuous, example: Dubins path
 * G2 - Curvature, curvature is continuous but not regular, cubic B-spline
 * G3 - Curvature is regular
 * 
 * source:
 * http://www.aliasworkbench.com/theoryBuilders/images/CombPlot4.jpg
 * 
 * @see Curvature2D */
public enum CurvatureComb {
  ;
  private static final TensorUnaryOperator NORMALIZE = NormalizeUnlessZero.with(Norm._2);
  private static final Tensor ZEROS = Array.zeros(2);

  /** @param tensor with dimensions n x 2 with points of curve
   * @param scaling
   * @param cyclic
   * @return tensor + normal * curvature * scaling */
  public static Tensor of(Tensor tensor, Scalar scaling, boolean cyclic) {
    if (Tensors.isEmpty(tensor))
      return Tensors.empty();
    Tensor normal = cyclic //
        ? cyclic(tensor)
        : string(tensor);
    return tensor.add(normal.multiply(scaling));
  }

  /** @param tensor of dimension n x 2
   * @return normals of dimension n x 2 scaled according to {@link SignedCurvature2D} */
  /* package */ static Tensor string(Tensor tensor) {
    return Curvature2D.string(tensor).pmul(Normal2D.string(tensor));
  }

  /** @param tensor of dimension n x 2
   * @return normals of dimension n x 2 scaled according to {@link SignedCurvature2D} */
  /* package */ static Tensor cyclic(Tensor tensor) {
    int length = tensor.length();
    Nocopy normal = new Nocopy(length);
    if (0 < length) {
      Tensor p = Last.of(tensor);
      Tensor q = tensor.get(0);
      for (int index = 1; index <= length; ++index) {
        Tensor r = tensor.get(index % length);
        normal.append(normal(p, q, r, r.subtract(p)));
        p = q;
        q = r;
      }
    }
    return normal.tensor();
  }

  private static Tensor normal(Tensor a, Tensor b, Tensor c, Tensor tangent) {
    Optional<Scalar> optional = SignedCurvature2D.of(a, b, c);
    return optional.isPresent() //
        ? NORMALIZE.apply(Cross.of(tangent)).multiply(optional.get())
        : ZEROS;
  }
}
