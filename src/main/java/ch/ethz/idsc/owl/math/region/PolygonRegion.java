// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.owl.math.planar.Polygons;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.N;

/** check if input tensor is inside a polygon */
public class PolygonRegion implements Region<Tensor> {
  private final Tensor polygon;

  /** @param polygon is mapped to numeric precision */
  public PolygonRegion(Tensor polygon) {
    this.polygon = N.DOUBLE.of(polygon);
  }

  @Override // from Region
  public boolean isMember(Tensor tensor) {
    return Polygons.isInside(polygon, tensor);
  }

  public Tensor polygon() {
    return polygon.unmodifiable();
  }
}
