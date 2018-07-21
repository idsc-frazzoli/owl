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

public enum CurvatureComb {
  ;
  /** @param tensor
   * @param scalar
   * @return tensor + normal * curvature * scalar */
  public static Tensor of(Tensor tensor, Scalar scalar) {
    if (Tensors.isEmpty(tensor))
      return Tensors.empty();
    List<Integer> dims = Dimensions.of(tensor);
    if (2 < dims.get(1))
      tensor = Tensor.of(tensor.stream().map(ExtractXY::of));
    return tensor.add(string(tensor).multiply(scalar));
  }

  /** @param tensor of dimension n x 2
   * @return normals of dimension n x 2 scaled according to {@link SignedCurvature2D} */
  public static Tensor string(Tensor tensor) {
    Tensor normal = Tensors.empty();
    if (0 < tensor.length())
      normal.append(Array.zeros(2));
    for (int index = 1; index < tensor.length() - 1; ++index) {
      Tensor a = tensor.get(index - 1).extract(0, 2);
      Tensor b = tensor.get(index + 0).extract(0, 2);
      Tensor c = tensor.get(index + 1).extract(0, 2);
      Optional<Scalar> optional = SignedCurvature2D.of(a, b, c);
      normal.append(optional.isPresent() //
          ? Normalize.of(Cross2D.of(c.subtract(a))).multiply(optional.get())
          : Array.zeros(2));
    }
    if (1 < tensor.length())
      normal.append(Array.zeros(2));
    return normal;
  }
}
