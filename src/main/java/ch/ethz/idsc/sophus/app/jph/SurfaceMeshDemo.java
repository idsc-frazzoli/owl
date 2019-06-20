// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringBiinvariantMean;
import ch.ethz.idsc.sophus.math.Arrowhead;
import ch.ethz.idsc.sophus.surf.subdiv.LinearMeshSubdivision;
import ch.ethz.idsc.sophus.surf.subdiv.SurfaceMesh;
import ch.ethz.idsc.sophus.surf.subdiv.SurfaceMeshes;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

public class SurfaceMeshDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED_DRAW = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final ColorDataIndexed COLOR_DATA_INDEXED_FILL = ColorDataLists._097.cyclic().deriveWithAlpha(182);
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  // ---
  private final JToggleButton axes = new JToggleButton("axes");
  private final SurfaceMesh surfaceMesh = SurfaceMeshes.quads5();

  public SurfaceMeshDemo() {
    super(false, GeodesicDisplays.SE2C_ONLY);
    timerFrame.jToolBar.add(axes);
    setControlPointsSe2(surfaceMesh.vrt);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (axes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    surfaceMesh.vrt = getControlPointsSe2();
    LinearMeshSubdivision linearMeshSubdivision = new LinearMeshSubdivision(Se2CoveringBiinvariantMean.INSTANCE);
    SurfaceMesh refine = linearMeshSubdivision.refine(surfaceMesh);
    for (Tensor polygon : refine.polygons()) {
      Path2D path2d = geometricLayer.toPath2D(polygon);
      path2d.closePath();
      graphics.setColor(COLOR_DATA_INDEXED_DRAW.getColor(0));
      graphics.draw(path2d);
      graphics.setColor(COLOR_DATA_INDEXED_FILL.getColor(0));
      graphics.fill(path2d);
    }
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    graphics.setColor(Color.LIGHT_GRAY);
    for (Tensor mean : refine.vrt) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(mean));
      Path2D path2d = geometricLayer.toPath2D(Arrowhead.of(0.2));
      path2d.closePath();
      // graphics.setColor(COLOR_DATA_INDEXED_FILL.getColor(0));
      graphics.fill(path2d);
      // graphics.setColor(COLOR_DATA_INDEXED_DRAW.getColor(0));
      // graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    // Tensor sequence = getControlPointsSe2();
    // int n = sequence.length();
    // Scalar scalar = RationalScalar.of(1, n);
    // Tensor mean = Se2CoveringBiinvariantMean.INSTANCE.mean(sequence, Array.of(l -> scalar, n));
    //
    //
    // graphics.setStroke(STROKE);
    // GraphicsUtil.setQualityHigh(graphics);
    // for (Tensor point : sequence) {
    // Tensor curve = Subdivide.of(0, 1, 20).map(Se2CoveringGeodesic.INSTANCE.curve(point, mean));
    // Path2D path2d = geometricLayer.toPath2D(curve);
    // graphics.draw(path2d);
    // }
    // graphics.setStroke(new BasicStroke(1));
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new SurfaceMeshDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
