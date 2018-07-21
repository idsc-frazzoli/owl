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
  /** @param curve
   * @param scalar
   * @return curve + normal * curvature * scalar */
  public static Tensor of(Tensor curve, Scalar scalar) {
    if (Tensors.isEmpty(curve))
      return Tensors.empty();
    List<Integer> dims = Dimensions.of(curve);
    if (2 < dims.get(1))
      curve = Tensor.of(curve.stream().map(ExtractXY::of));
    return curve.add(string(curve).multiply(scalar));
  }

  /** @param curve of dimension n x 2
   * @return normals of dimension n x 2 scaled according to curvature */
  public static Tensor string(Tensor curve) {
    Tensor tensor = Tensors.empty();
    if (0 < curve.length())
      tensor.append(Array.zeros(2));
    for (int index = 1; index < curve.length() - 1; ++index) {
      Tensor a = curve.get(index - 1).extract(0, 2);
      Tensor b = curve.get(index + 0).extract(0, 2);
      Tensor c = curve.get(index + 1).extract(0, 2);
      Optional<Scalar> optional = SignedCurvature2D.of(a, b, c);
      tensor.append(optional.isPresent() //
          ? Normalize.of(Cross2D.of(c.subtract(a))).multiply(optional.get())
          : Array.zeros(2));
    }
    if (1 < curve.length())
      tensor.append(Array.zeros(2));
    return tensor;
  }
}
