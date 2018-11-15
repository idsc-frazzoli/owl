// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

/** .
 * G0 - Position, tangent of curve is not continuous, example: polygons
 * G1 - Tangent, curvature is discontinuous, example: Dubins path
 * G2 - Curvature, curvature is continuous but not regular, cubic B-spline
 * G3 - Curvature is regular
 * 
 * source:
 * http://www.aliasworkbench.com/theoryBuilders/images/CombPlot4.jpg */
public enum CurvatureComb {
  ;
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);

  /** @param tensor
   * @param scalar
   * @return tensor + normal * curvature * scalar */
  public static Tensor of(Tensor tensor, Scalar scalar, boolean isCyclic) {
    if (Tensors.isEmpty(tensor))
      return Tensors.empty();
    List<Integer> dims = Dimensions.of(tensor);
    if (2 < dims.get(1))
      tensor = Tensor.of(tensor.stream().map(Extract2D::of));
    return tensor.add((isCyclic ? cyclic(tensor) : string(tensor)).multiply(scalar));
  }

  /** @param tensor of dimension n x 2
   * @return normals of dimension n x 2 scaled according to {@link SignedCurvature2D} */
  public static Tensor string(Tensor tensor) {
    Tensor normal = Tensors.empty();
    // TODO JPH can do better at the start and end
    if (0 < tensor.length())
      normal.append(Array.zeros(2));
    for (int index = 1; index < tensor.length() - 1; ++index) {
      Tensor a = tensor.get(index - 1);
      Tensor b = tensor.get(index + 0);
      Tensor c = tensor.get(index + 1);
      Optional<Scalar> optional = SignedCurvature2D.of(a, b, c);
      normal.append(optional.isPresent() //
          ? NORMALIZE.apply(Cross2D.of(c.subtract(a))).multiply(optional.get())
          : Array.zeros(2));
    }
    if (1 < tensor.length())
      normal.append(Array.zeros(2));
    return normal;
  }

  /** @param tensor of dimension n x 2
   * @return normals of dimension n x 2 scaled according to {@link SignedCurvature2D} */
  public static Tensor cyclic(Tensor tensor) {
    Tensor normal = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      Tensor a = tensor.get((index - 1 + tensor.length()) % tensor.length());
      Tensor b = tensor.get(index);
      Tensor c = tensor.get((index + 1) % tensor.length());
      Optional<Scalar> optional = SignedCurvature2D.of(a, b, c);
      normal.append(optional.isPresent() //
          ? NORMALIZE.apply(Cross2D.of(c.subtract(a))).multiply(optional.get())
          : Array.zeros(2));
    }
    return normal;
  }
}
