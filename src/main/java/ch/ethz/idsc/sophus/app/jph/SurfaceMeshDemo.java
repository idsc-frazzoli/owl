// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.srf.SurfaceMesh;
import ch.ethz.idsc.sophus.srf.SurfaceMeshExamples;
import ch.ethz.idsc.sophus.srf.subdiv.CatmullClarkRefinement;
import ch.ethz.idsc.sophus.srf.subdiv.SurfaceMeshRefinement;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

public class SurfaceMeshDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED_DRAW = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final ColorDataIndexed COLOR_DATA_INDEXED_FILL = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  // ---
  private final JToggleButton axes = new JToggleButton("axes");
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SurfaceMesh surfaceMesh = SurfaceMeshExamples.quads6();

  public SurfaceMeshDemo() {
    super(false, GeodesicDisplays.SE2C_R2);
    timerFrame.jToolBar.add(axes);
    setControlPointsSe2(surfaceMesh.vrt);
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4));
    spinnerRefine.setValue(2);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (axes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    surfaceMesh.vrt = getControlPointsSe2();
    GraphicsUtil.setQualityHigh(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    SurfaceMeshRefinement surfaceMeshRefinement = //
        CatmullClarkRefinement.of(geodesicDisplay.biinvariantMean());
    SurfaceMesh refine = surfaceMesh;
    for (int count = 0; count < spinnerRefine.getValue(); ++count)
      refine = surfaceMeshRefinement.refine(refine);
    for (Tensor polygon : refine.polygons()) {
      Path2D path2d = geometricLayer.toPath2D(polygon);
      path2d.closePath();
      graphics.setColor(COLOR_DATA_INDEXED_DRAW.getColor(0));
      graphics.draw(path2d);
      graphics.setColor(COLOR_DATA_INDEXED_FILL.getColor(0));
      graphics.fill(path2d);
    }
    graphics.setColor(new Color(192, 192, 192, 192));
    Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(.5));
    for (Tensor mean : refine.vrt) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(mean));
      graphics.fill(geometricLayer.toPath2D(shape));
      geometricLayer.popMatrix();
    }
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new SurfaceMeshDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
