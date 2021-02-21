// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.BufferedImageSupplier;
import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplayDemo;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;

/* package */ abstract class AbstractDatasetFilterDemo extends GeodesicDisplayDemo {
  private static final Color COLOR_CURVE = new Color(255, 128, 128, 255);
  private static final Color COLOR_SHAPE = new Color(160, 160, 160, 192);
  private static final GridRender GRID_RENDER = new GridRender(Subdivide.of(0, 100, 10));
  // ---
  private final PathRender pathRenderCurve = new PathRender(COLOR_CURVE);
  private final PathRender pathRenderShape = new PathRender(COLOR_SHAPE);
  // ---
  private final JToggleButton jToggleDiff = new JToggleButton("diff");
  private final JToggleButton jToggleSpec = new JToggleButton("spec");
  private final JToggleButton jToggleData = new JToggleButton("data");
  private final JToggleButton jToggleConv = new JToggleButton("conv");
  private final JToggleButton jToggleSymi = new JToggleButton("graph");

  public AbstractDatasetFilterDemo(List<ManifoldDisplay> list) {
    super(list);
    timerFrame.geometricComponent.addRenderInterfaceBackground(GRID_RENDER);
    // ---
    jToggleDiff.setSelected(true);
    jToggleDiff.addActionListener(l -> jToggleSpec.setEnabled(jToggleDiff.isSelected()));
    timerFrame.jToolBar.add(jToggleDiff);
    // ---
    jToggleSpec.setToolTipText("spectrogram");
    jToggleSpec.setSelected(true);
    timerFrame.jToolBar.add(jToggleSpec);
    // ---
    jToggleData.setSelected(true);
    timerFrame.jToolBar.add(jToggleData);
    // ---
    jToggleConv.setSelected(true);
    timerFrame.jToolBar.add(jToggleConv);
    // ---
    if (this instanceof BufferedImageSupplier) {
      jToggleSymi.setSelected(true);
      timerFrame.jToolBar.add(jToggleSymi);
    }
  }

  @Override
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor control = control();
    RenderQuality.setQuality(graphics);
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    final Tensor shape = geodesicDisplay.shape().multiply(markerScale());
    if (jToggleData.isSelected()) {
      pathRenderCurve.setCurve(control, false).render(geometricLayer, graphics);
      for (Tensor point : control) {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(shape);
        path2d.closePath();
        graphics.setColor(new Color(255, 128, 128, 64));
        graphics.fill(path2d);
        graphics.setColor(COLOR_CURVE);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    Tensor refined = protected_render(geometricLayer, graphics);
    // ---
    if (this instanceof BufferedImageSupplier && //
        jToggleSymi.isSelected()) {
      BufferedImageSupplier bufferedImageSupplier = (BufferedImageSupplier) this;
      graphics.drawImage(bufferedImageSupplier.bufferedImage(), 0, 0, null);
    }
    // ---
    graphics.setStroke(new BasicStroke(1f));
    if (jToggleConv.isSelected()) {
      pathRenderShape.setCurve(refined, false).render(geometricLayer, graphics);
      for (Tensor point : refined) {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(shape);
        path2d.closePath();
        graphics.setColor(COLOR_SHAPE);
        graphics.fill(path2d);
        graphics.setColor(Color.BLACK);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    RenderQuality.setDefault(graphics);
    if (jToggleDiff.isSelected())
      differences_render(graphics, manifoldDisplay(), refined, jToggleSpec.isSelected());
  }

  public Scalar markerScale() {
    return RealScalar.of(0.03);
  }

  protected abstract Tensor control();

  protected abstract Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics);

  protected abstract void differences_render( //
      Graphics2D graphics, ManifoldDisplay geodesicDisplay, Tensor refined, boolean spectrogram);
}
