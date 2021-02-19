// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;

import ch.ethz.idsc.sophus.ply.d2.Polygons;
import ch.ethz.idsc.tensor.Tensor;

/** check if input tensor is inside a polygon in R^2
 * 
 * @see Polygons */
public class PolygonRegion implements Region<Tensor>, Serializable {
  private final Tensor polygon;

  /** @param polygon as matrix with dimensions n x 2 */
  public PolygonRegion(Tensor polygon) {
    this.polygon = polygon;
  }

  @Override // from Region
  public boolean isMember(Tensor tensor) {
    // LONGTERM depending on complexity of given polygon, prepend AABB check
    return Polygons.isInside(polygon, tensor);
  }

  public Tensor polygon() {
    return polygon.unmodifiable();
  }
}
