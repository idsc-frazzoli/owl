// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.java.awt.SpinnerListener;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.H2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Se2AbstractGeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.ThreePointCoordinates;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Drop;
import ch.ethz.idsc.tensor.alg.Subdivide;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class ThreePointCoordinateDemo extends A2ScatteredSetCoordinateDemo implements SpinnerListener<GeodesicDisplay> {
  private static final Tensor DOMAIN = Drop.tail(Subdivide.of(0.0, 1.0, 10), 1);

  public ThreePointCoordinateDemo() {
    super(true, GeodesicDisplays.R2_H2_S2, ThreePointCoordinates.list());
    GeodesicDisplay geodesicDisplay = R2GeodesicDisplay.INSTANCE;
    actionPerformed(geodesicDisplay);
    addSpinnerListener(this);
    addSpinnerListener(l -> recompute());
    recompute();
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    super.render(geometricLayer, graphics);
    // ---
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor sequence = getGeodesicControlPoints();
    Tensor all = Tensors.empty();
    for (int index = 0; index < sequence.length(); ++index) {
      Tensor prev = sequence.get(Math.floorMod(index - 1, sequence.length()));
      Tensor next = sequence.get(index);
      DOMAIN.map(geodesicDisplay.geodesicInterface().curve(prev, next)).stream() //
          .map(geodesicDisplay::toPoint) //
          .forEach(all::append);
    }
    Path2D path2d = geometricLayer.toPath2D(all);
    graphics.setColor(new Color(192, 192, 128, 64));
    graphics.fill(path2d);
    graphics.setColor(new Color(192, 192, 128, 192));
    graphics.draw(path2d);
  }

  @Override
  public void actionPerformed(GeodesicDisplay geodesicDisplay) {
    if (geodesicDisplay instanceof R2GeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-1.017, -0.953, 0.000}, {-0.991, 0.113, 0.000}, {-0.644, 0.967, 0.000}, {0.509, 0.840, 0.000}, {0.689, 0.513, 0.000}, {0.956, -0.627, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof S2GeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString( //
          "{{0.300, 0.092, 0.000}, {-0.563, -0.658, 0.262}, {-0.854, -0.200, 0.000}, {-0.746, 0.663, -0.262}, {0.467, 0.758, 0.262}, {0.446, -0.554, 0.262}}"));
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.521, 0.621, 0.262}, {-0.863, 0.258, 0.000}, {-0.725, 0.588, -0.785}, {0.392, 0.646, 0.000}, {-0.375, 0.021, 0.000}, {-0.525, -0.392, 0.000}}"));
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.583, 0.338, 0.000}, {-0.904, -0.258, 0.262}, {-0.513, 0.804, 0.000}, {0.646, 0.667, 0.000}, {0.704, -0.100, 0.000}, {0.396, -0.688, 0.000}}"));
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.363, 0.388, 0.000}, {-0.825, -0.271, 0.000}, {-0.513, 0.804, 0.000}, {0.646, 0.667, 0.000}, {0.704, -0.100, 0.000}, {-0.075, -0.733, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof Se2AbstractGeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString(
          "{{3.150, -2.700, -0.524}, {-1.950, -3.683, 0.000}, {-1.500, -1.167, 2.094}, {4.533, -0.733, -1.047}, {8.567, -3.300, -1.309}, {2.917, -5.050, -1.047}}"));
    } else //
    if (geodesicDisplay instanceof H2GeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-1.900, 1.783, 0.000}, {-0.083, 2.517, 0.000}, {2.300, 2.117, 0.000}, {2.833, 0.217, 0.000}, {1.000, -1.550, 0.000}, {-1.450, -1.650, 0.000}}"));
    }
  }

  public static void main(String[] args) {
    new ThreePointCoordinateDemo().setVisible(1300, 900);
  }
}
