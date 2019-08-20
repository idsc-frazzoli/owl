// code by vc
// inspired by https://rosettacode.org/wiki/Sutherland-Hodgman_polygon_clipping#Java
package ch.ethz.idsc.sophus.ply;

import ch.ethz.idsc.sophus.math.Det2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;

/** Sutherland-Hodgman polygon clipping
 * 
 * The implementation finds the polygon that results from the intersection
 * of a clip region with the given polygons. The polygons are specified as
 * tensors that contain vertex in counter clock-wise order.
 * 
 * Careful: input exists such that the resulting polygon degenerates into
 * two or more areas, that are connected only with 1-dimensional edges. In
 * such cases, the returned polygon may contain duplicate vertices. */
public class PolygonClip implements TensorUnaryOperator {
  /** @param clip convex, vertices ordered ccw, with dimensions n x 2
   * @return */
  public static TensorUnaryOperator of(Tensor clip) {
    clip.stream().forEach(vertex -> VectorQ.requireLength(vertex, 2));
    return new PolygonClip(clip);
  }

  // ---
  private final Tensor[] vertex;

  private PolygonClip(Tensor clip) {
    vertex = clip.stream().map(Tensor::copy).toArray(Tensor[]::new);
  }

  @Override
  public Tensor apply(Tensor subject) {
    int length = vertex.length;
    Tensor tensor = subject.copy();
    for (int i = 0; i < length; ++i) {
      Tensor a = vertex[(i + length - 1) % length];
      Tensor b = vertex[i];
      Tensor input = tensor;
      int len = input.length();
      tensor = Tensors.empty();
      for (int j = 0; j < len; ++j) {
        Tensor p = input.get((j + len - 1) % len);
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
    return tensor;
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

  /* package */ static Tensor intersection(Tensor a, Tensor b, Tensor p, Tensor q) {
    Tensor ab = a.subtract(b);
    Tensor pq = p.subtract(q);
    Scalar den = Det2D.of(ab, pq);
    if (Chop._40.allZero(den))
      throw TensorRuntimeException.of(a, b, p, q);
    return pq.multiply(Det2D.of(ab, a)).add(ab.multiply(Det2D.of(p, pq))).divide(den);
  }
}