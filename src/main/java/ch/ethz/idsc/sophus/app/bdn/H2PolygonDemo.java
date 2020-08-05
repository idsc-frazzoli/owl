// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.util.function.Predicate;

import ch.ethz.idsc.sophus.app.api.D2BarycentricCoordinates;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.r2.Polygons;
import ch.ethz.idsc.sophus.math.TensorMapping;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class H2PolygonDemo extends H2ScatteredSetCoordinateDemo {
  public H2PolygonDemo() {
    super(D2BarycentricCoordinates.list());
    setControlPointsSe2(Tensors.fromString( //
        "{{-1.900, 1.783, 0.000}, {-0.083, 2.517, 0.000}, {2.300, 2.117, 0.000}, {2.833, 0.217, 0.000}, {1.000, -1.550, 0.000}, {-1.450, -1.650, 0.000}}"));
    recompute();
  }

  @Override
  Predicate<Tensor> isRenderable() {
    VectorLogManifold vectorLogManifold = geodesicDisplay().vectorLogManifold();
    Tensor sequence = getGeodesicControlPoints();
    return new Predicate<Tensor>() {
      @Override
      public boolean test(Tensor point) {
        TensorMapping tensorMapping = vectorLogManifold.logAt(point)::vectorLog;
        return Polygons.isInside(tensorMapping.slash(sequence));
      }
    };
  }

  public static void main(String[] args) {
    new H2PolygonDemo().setVisible(1200, 900);
  }
}
