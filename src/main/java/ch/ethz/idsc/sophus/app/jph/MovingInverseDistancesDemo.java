// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ArrayRender;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.PointsRender;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;

/* package */ abstract class MovingInverseDistancesDemo extends ScatteredSetCoordinateDemo {
  private static final PointsRender POINTS_RENDER_POINTS = //
      new PointsRender(new Color(64, 255, 64, 64), new Color(64, 255, 64, 255));
  // ---
  private final JButton jButton = new JButton("snap");
  // ---
  MovingDomain2D movingDomain2D;

  MovingInverseDistancesDemo(List<GeodesicDisplay> list, Supplier<BarycentricCoordinate>[] array) {
    super(false, list, array);
    setMidpointIndicated(false);
    // ---
    spinnerBarycentric.addSpinnerListener(v -> recompute());
    {
      jButton.addActionListener(l -> recompute());
      timerFrame.jToolBar.add(jButton);
    }
  }

  final void recompute() {
    updateOrigin(getControlPointsSe2().map(N.DOUBLE));
  }

  abstract BiinvariantMean biinvariantMean();

  abstract void updateOrigin(Tensor originSe2);

  abstract Tensor shapeOrigin();

  @Override // from RenderInterface
  public synchronized void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor origin = movingDomain2D.origin();
    Tensor target = getGeodesicControlPoints();
    Tensor[][] array = movingDomain2D.forward(target, biinvariantMean());
    new ArrayRender(array, colorDataGradient().deriveWithOpacity(RationalScalar.HALF)) //
        .render(geometricLayer, graphics);
    graphics.setColor(Color.RED);
    for (int index = 0; index < origin.length(); ++index)
      graphics.draw(geometricLayer.toPath2D(Tensors.of(origin.get(index), target.get(index))));
    POINTS_RENDER_POINTS //
        .show(geodesicDisplay::matrixLift, shapeOrigin(), origin) //
        .render(geometricLayer, graphics);
    renderControlPoints(geometricLayer, graphics);
    if (jToggleHeatmap.isSelected())
      new ArrayPlotRender(movingDomain2D.weights(), colorDataGradient(), 0, 12, 3).render(geometricLayer, graphics);
  }
}
