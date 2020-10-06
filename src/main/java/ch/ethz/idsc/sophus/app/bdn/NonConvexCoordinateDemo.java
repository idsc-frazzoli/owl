// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.util.Arrays;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.H2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.PolygonCoordinates;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.tensor.Tensors;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class NonConvexCoordinateDemo extends PolygonCoordinatesDemo {
  public NonConvexCoordinateDemo() {
    super(Arrays.asList(PolygonCoordinates.MEAN_VALUE));
  }

  @Override
  public void actionPerformed(GeodesicDisplay geodesicDisplay) {
    if (geodesicDisplay instanceof R2GeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-1.017, -0.953, 0.000}, {-0.991, 0.113, 0.000}, {-0.644, 0.967, 0.000}, {0.509, 0.840, 0.000}, {0.689, 0.513, 0.000}, {0.956, -0.627, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof H2GeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-2.400, 0.350, 0.000}, {-2.417, 1.817, 0.000}, {2.300, 2.117, 0.000}, {2.250, -1.883, 0.000}, {0.967, -2.933, 0.000}, {-2.550, -1.817, 0.000}, {0.783, 0.600, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof S2GeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString( //
          "{{0.232, -0.458, 0.000}, {-0.885, 0.283, 0.000}, {0.274, 0.850, 0.000}, {-0.451, 0.433, 0.000}, {0.824, 0.333, 0.000}, {0.615, -0.667, 0.000}, {-0.200, -0.846, 0.000}}"));
    }
  }

  @Override
  int resolution() {
    return 70;
  }

  public static void main(String[] args) {
    new NonConvexCoordinateDemo().setVisible(1300, 900);
  }
}
