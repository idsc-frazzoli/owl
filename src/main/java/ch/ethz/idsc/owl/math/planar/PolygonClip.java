// code by vc
// inspired by https://rosettacode.org/wiki/Sutherland-Hodgman_polygon_clipping#Java
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.DeleteDuplicates;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;

/** Sutherland-Hodgman polygon clipping
 * 
 * the implementation finds the polygon that results from the intersection of a clip region with the given polygons.
 * the polygons are specified as Tensors that contain vertex in counter clock-wise order. */
public class PolygonClip implements TensorUnaryOperator {
  /** @param clipper, has to be convex, vertices ordered ccw
   * @return */
  public static TensorUnaryOperator of(Tensor clip) {
    return new PolygonClip(clip);
  }
  // ---

  private final Tensor clip;

  private PolygonClip(Tensor clip) {
    this.clip = clip;
  }

  /** @param subject with vertices ordered ccw
   * @return */
  @Override
  public Tensor apply(Tensor subject) {
    int len = clip.length();
    Tensor tensor = subject.copy();
    for (int i = 0; i < len; ++i) {
      Tensor a = clip.get((i + len - 1) % len);
      Tensor b = clip.get(i);
      Tensor input = tensor;
      int len2 = input.length();
      tensor = Tensors.empty();
      for (int j = 0; j < len2; ++j) {
        Tensor p = input.get((j + len2 - 1) % len2);
        Tensor q = input.get(j);
        if (isInside(a, b, q)) {
          if (!isInside(a, b, p))
            tensor.append(intersection(a, b, p, q));
          tensor.append(q);
        } else //
        if (isInside(a, b, p))
          tensor.append(intersection(a, b, p, q));
      }
    }
    return DeleteDuplicates.of(tensor);
  }

  /** @param a
   * @param b
   * @param c
   * @return sign of signed area of triangle spanned by a, b, c */
  private static boolean isInside(Tensor a, Tensor b, Tensor c) {
    Tensor ac = a.subtract(c);
    Tensor bc = b.subtract(c);
    return Scalars.lessThan( //
        ac.Get(1).multiply(bc.Get(0)), //
        ac.Get(0).multiply(bc.Get(1)) //
    );
  }

  // package for testing
  /* package */ static Tensor intersection(Tensor a, Tensor b, Tensor p, Tensor q) {
    Tensor ab = a.subtract(b);
    Tensor pq = p.subtract(q);
    Scalar denom = StaticHelper.det(ab, pq);
    if (Chop._40.allZero(denom))
      throw TensorRuntimeException.of(a, b, p, q);
    return pq.multiply(StaticHelper.det(ab, a)).subtract(ab.multiply(StaticHelper.det(pq, p))).divide(denom);
  }
}