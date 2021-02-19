// code by vc
// inspired by https://rosettacode.org/wiki/Sutherland-Hodgman_polygon_clipping#Java
package ch.ethz.idsc.sophus.ply;

import java.io.Serializable;

import ch.ethz.idsc.sophus.math.d2.Det2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;

/** Sutherland-Hodgman polygon clipping
 * 
 * The implementation finds the polygon that results from the intersection
 * of a clip region with the given polygons. The polygons are specified as
 * tensors that contain vertex in counter clock-wise order.
 * 
 * Careful: input exists such that the resulting polygon degenerates into
 * two or more areas, that are connected only with 1-dimensional edges. In
 * such cases, the returned polygon may contain duplicate vertices. */
public class SutherlandHodgmanAlgorithm implements Serializable {
  /** @param clip convex, vertices ordered ccw, with dimensions n x 2
   * @return */
  public static SutherlandHodgmanAlgorithm of(Tensor clip) {
    clip.stream().forEach(vertex -> VectorQ.requireLength(vertex, 2));
    return new SutherlandHodgmanAlgorithm(clip);
  }

  /***************************************************/
  private final Tensor[] vertex;

  private SutherlandHodgmanAlgorithm(Tensor clip) {
    vertex = clip.stream().toArray(Tensor[]::new);
  }

  public static class PolyclipResult {
    private Tensor tensor = Tensors.empty();
    private Tensor belong = Tensors.empty();

    public Tensor tensor() {
      return tensor;
    }

    public Tensor belong() {
      return belong;
    }
  }

  public PolyclipResult apply(Tensor subject) {
    int length = vertex.length;
    if (length == 0)
      return new PolyclipResult();
    Tensor tensor = subject;
    Tensor belong = Array.zeros(subject.length());
    for (int i = 0; i < length; ++i) {
      Tensor a = vertex[Math.floorMod(i - 1, length)];
      Tensor b = vertex[i];
      Tensor input = tensor;
      int size = input.length();
      tensor = Tensors.empty();
      Tensor belonh = Tensors.empty();
      for (int j = 0; j < size; ++j) {
        Tensor p = input.get(Math.floorMod(j - 1, size));
        Tensor q = input.get(j);
        if (isInside(a, b, q)) {
          if (!isInside(a, b, p)) {
            tensor.append(intersection(a, b, p, q));
            belonh.append(RealScalar.ONE);
          }
          {
            tensor.append(q);
            belonh.append(belong.get(j));
          }
        } else //
        if (isInside(a, b, p)) {
          tensor.append(intersection(a, b, p, q));
          belonh.append(RealScalar.ONE);
        }
      }
      belong = belonh;
    }
    PolyclipResult polyclipResult = new PolyclipResult();
    polyclipResult.tensor = tensor;
    polyclipResult.belong = belong;
    return polyclipResult;
  }

  /** @param a
   * @param b
   * @param c
   * @return sign of signed area of triangle spanned by a, b, c */
  private static boolean isInside(Tensor a, Tensor b, Tensor c) {
    return Sign.isPositive(Det2D.of(a.subtract(c), b.subtract(c)));
  }

  /* package */ static Tensor intersection(Tensor a, Tensor b, Tensor p, Tensor q) {
    Tensor ab = a.subtract(b);
    Tensor pq = p.subtract(q);
    Scalar den = Det2D.of(ab, pq);
    if (Chop._40.isZero(den))
      throw TensorRuntimeException.of(a, b, p, q);
    return pq.multiply(Det2D.of(ab, a)).add(ab.multiply(Det2D.of(p, pq))).divide(den);
  }
}