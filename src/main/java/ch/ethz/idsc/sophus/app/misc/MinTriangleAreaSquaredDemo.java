// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JTextField;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.DubinsGenerator;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.lev.LeverRender;
import ch.ethz.idsc.sophus.lie.r3.MinTriangleAreaSquared;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

/* package */ class MinTriangleAreaSquaredDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  // ---
  private final PathRender pathRenderBall = new PathRender(COLOR_DATA_INDEXED.getColor(0), 1.5f);
  private final PathRender pathRenderHull = new PathRender(COLOR_DATA_INDEXED.getColor(1), 1.5f);

  MinTriangleAreaSquaredDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    // ---
    timerFrame.geometricComponent.addRenderInterface(pathRenderBall);
    timerFrame.geometricComponent.addRenderInterface(pathRenderHull);
    // ---
    JTextField jTextField = new JTextField(10);
    jTextField.setPreferredSize(new Dimension(100, 28));
    timerFrame.jToolBar.add(jTextField);
    // ---
    Tensor blub = Tensors.fromString("{{1, 0, 0}, {0, 1, 0}, {2, 0, 2.5708}, {1, 0, 2.1}}");
    setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
        Tensor.of(blub.stream().map(Tensors.vector(2, 1, 1)::pmul))));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    final GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor control = getGeodesicControlPoints();
    pathRenderHull.setCurve(control, true);
    if (0 < control.length()) {
      Tensor polygon = control.copy();
      polygon.stream().forEach(row -> row.append(RealScalar.ONE));
      Tensor weights = MinTriangleAreaSquared.weights(polygon);
      Tensor weiszfeld = weights.dot(polygon).extract(0, 2);
      LeverRender leverRender = new LeverRender(geodesicDisplay, control, weiszfeld, weights, geometricLayer, graphics);
      leverRender.renderWeights();
      leverRender.renderOrigin();
      leverRender.renderLevers();
    }
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new MinTriangleAreaSquaredDemo().setVisible(1000, 800);
  }
}
