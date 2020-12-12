// code by vc
// inspired by https://rosettacode.org/wiki/Sutherland-Hodgman_polygon_clipping#Java
package ch.ethz.idsc.sophus.ply;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

/** Sutherland-Hodgman polygon clipping
 * 
 * The implementation finds the polygon that results from the intersection
 * of a clip region with the given polygons. The polygons are specified as
 * tensors that contain vertex in counter clock-wise order.
 * 
 * Careful: input exists such that the resulting polygon degenerates into
 * two or more areas, that are connected only with 1-dimensional edges. In
 * such cases, the returned polygon may contain duplicate vertices. */
public class PolygonClip implements Serializable {
  /** @param clip convex, vertices ordered ccw, with dimensions n x 2
   * @return */
  public static TensorUnaryOperator of(Tensor clip) {
    SutherlandHodgmanAlgorithm sutherlandHodgmanAlgorithm = SutherlandHodgmanAlgorithm.of(clip);
    return subject -> sutherlandHodgmanAlgorithm.apply(subject).tensor();
  }
}