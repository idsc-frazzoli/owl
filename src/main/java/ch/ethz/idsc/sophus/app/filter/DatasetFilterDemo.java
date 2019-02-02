// code by ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplayDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.ResourceData;

/* package */ abstract class DatasetFilterDemo extends GeodesicDisplayDemo {
  private static final Color COLOR_CURVE = new Color(255, 128, 128, 255);
  private static final Color COLOR_SHAPE = new Color(160, 160, 160, 192);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  private static final GridRender GRID_RENDER = new GridRender(Subdivide.of(0, 100, 10));
  private static final int BASELINE_Y = 200;
  // ---
  private final JToggleButton jToggleWait = new JToggleButton("wait");
  private final JToggleButton jToggleDiff = new JToggleButton("diff");
  private final JToggleButton jToggleData = new JToggleButton("data");
  // ---
  private final PathRender pathRenderCurve = new PathRender(COLOR_CURVE);
  private final PathRender pathRenderShape = new PathRender(COLOR_SHAPE);
  protected final JToggleButton jToggleSymi = new JToggleButton("graph");
  protected Tensor _control = Tensors.of(Array.zeros(3));
  private final SpinnerLabel<String> spinnerLabelString = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerLabelLimit = new SpinnerLabel<>();

  protected void updateData() {
    _control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + spinnerLabelString.getValue() + ".csv").stream() //
        .limit(spinnerLabelLimit.getValue()) //
        .map(row -> row.extract(1, 4)));
  }

  protected final Tensor control() {
    return Tensor.of(_control.stream().map(geodesicDisplay()::project)).unmodifiable();
  }

  public DatasetFilterDemo() {
    super(GeodesicDisplays.SE2_R2);
    timerFrame.geometricComponent.setModel2Pixel(StaticHelper.HANGAR_MODEL2PIXEL);
    // ---
    jToggleWait.setSelected(false);
    timerFrame.jToolBar.add(jToggleWait);
    // ---
    jToggleDiff.setSelected(true);
    timerFrame.jToolBar.add(jToggleDiff);
    // ---
    jToggleData.setSelected(true);
    timerFrame.jToolBar.add(jToggleData);
    {
      spinnerLabelString.setList(ResourceData.lines("/dubilab/app/pose/index.txt"));
      spinnerLabelString.addSpinnerListener(type -> updateData());
      spinnerLabelString.setIndex(0);
      spinnerLabelString.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "data");
    }
    {
      spinnerLabelLimit.setList(Arrays.asList(250, 500, 1000, 2000, 5000));
      spinnerLabelLimit.setIndex(0);
      spinnerLabelLimit.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "limit");
      spinnerLabelLimit.addSpinnerListener(type -> updateData());
    }
    timerFrame.jToolBar.addSeparator();
    // ---
    timerFrame.jToolBar.add(jToggleSymi);
  }

  @Override
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleWait.isSelected())
      return;
    GRID_RENDER.render(geometricLayer, graphics);
    Tensor control = control();
    GraphicsUtil.setQualityHigh(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    final Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(.3));
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
    graphics.setStroke(new BasicStroke(1f));
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
    if (jToggleDiff.isSelected())
      differences_render(geometricLayer, graphics, geodesicDisplay(), refined);
  }

  /** @param geometricLayer
   * @param graphics
   * @return */
  protected abstract Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics);

  /** @return */
  protected abstract String plotLabel();

  private void differences_render(GeometricLayer geometricLayer, Graphics2D graphics, GeodesicDisplay geodesicDisplay, Tensor refined) {
    graphics.setColor(Color.BLACK);
    graphics.drawLine(0, BASELINE_Y, 300, BASELINE_Y);
    {
      int piy = 30;
      graphics.drawString(plotLabel(), 0, piy);
      graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
      graphics.drawString("Tangent velocity", 0, piy += 15);
      graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
      graphics.drawString("Side slip", 0, piy += 15);
      graphics.setColor(COLOR_DATA_INDEXED.getColor(2));
      graphics.drawString("Rotational rate", 0, piy += 15);
    }
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    if (Objects.nonNull(lieGroup)) {
      LieDifferences lieDifferences = new LieDifferences(lieGroup, geodesicDisplay.lieExponential());
      Tensor speeds = lieDifferences.apply(refined);
      if (0 < speeds.length()) {
        int dimensions = speeds.get(0).length();
        graphics.setStroke(new BasicStroke(1.3f));
        for (int index = 0; index < dimensions; ++index) {
          graphics.setColor(COLOR_DATA_INDEXED.getColor(index));
          Path2D path2d = plotFunc(graphics, speeds.get(Tensor.ALL, index).multiply(RealScalar.of(400)), BASELINE_Y);
          graphics.draw(path2d);
        }
      }
    }
  }

  private static Path2D plotFunc(Graphics2D graphics, Tensor tensor, int baseline_y) {
    Path2D path2d = new Path2D.Double();
    if (Tensors.nonEmpty(tensor))
      path2d.moveTo(0, baseline_y - tensor.Get(0).number().doubleValue());
    for (int pix = 1; pix < tensor.length(); ++pix)
      path2d.lineTo(pix, baseline_y - tensor.Get(pix).number().doubleValue());
    return path2d;
  }
}
