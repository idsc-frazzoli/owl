// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.planar.Polygons;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.N;

/** check if input tensor is inside a polygon */
public class PolygonRegion implements Region<Tensor>, Serializable {
  /** @param polygon is mapped to numeric precision */
  public static Region<Tensor> of(Tensor polygon) {
    // TODO depending on complexity of given polygon, prepend AABB check
    return new PolygonRegion(polygon);
  }
  // ---

  private final Tensor polygon;

  private PolygonRegion(Tensor polygon) {
    this.polygon = N.DOUBLE.of(polygon); // TODO do not N by default
  }

  @Override // from Region
  public boolean isMember(Tensor tensor) {
    return Polygons.isInside(polygon, tensor);
  }

  public Tensor polygon() {
    return polygon.unmodifiable();
  }
}
