// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.function.Predicate;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.D2BarycentricCoordinates;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.r2.Polygons;
import ch.ethz.idsc.sophus.math.TensorMapping;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class S2PolygonDemo extends S2ScatteredSetCoordinateDemo {
  public S2PolygonDemo() {
    super(GeodesicDisplays.S2_ONLY, D2BarycentricCoordinates.list());
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    super.render(geometricLayer, graphics);
    // ---
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor sequence = getGeodesicControlPoints();
    Tensor doma = Subdivide.of(0.0, 1.0, 11);
    for (int index = 0; index < sequence.length(); ++index) {
      Tensor prev = sequence.get(Math.floorMod(index - 1, sequence.length()));
      Tensor next = sequence.get(index);
      Tensor curve = doma.map(geodesicDisplay.geodesicInterface().curve(prev, next));
      Tensor polygon = Tensor.of(curve.stream().map(geodesicDisplay::toPoint));
      Path2D path2d = geometricLayer.toPath2D(polygon);
      graphics.draw(path2d);
    }
  }

  @Override
  Predicate<Tensor> isRenderable() {
    Tensor sequence = getGeodesicControlPoints();
    return new Predicate<Tensor>() {
      @Override
      public boolean test(Tensor point) {
        TensorMapping tensorMapping = geodesicDisplay().vectorLogManifold().logAt(point)::vectorLog;
        return Polygons.isInside(tensorMapping.slash(sequence));
      }
    };
  }

  public static void main(String[] args) {
    new S2PolygonDemo().setVisible(1300, 900);
  }
}
