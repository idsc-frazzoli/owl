// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.group.LieDifferences;
import ch.ethz.idsc.owl.math.group.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.group.Se2Geodesic;
import ch.ethz.idsc.owl.math.group.Se2Group;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenter;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenterFilter;
import ch.ethz.idsc.owl.symlink.SmoothingKernel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

class GeodesicCenterFilterDemo {
  private static final Tensor ARROWHEAD_HI = Arrowhead.of(0.10);
  private static final Tensor ARROWHEAD_LO = Arrowhead.of(0.12);
  // ---
  private final TimerFrame timerFrame = new TimerFrame();
  private Tensor control = Tensors.of(Array.zeros(3));

  GeodesicCenterFilterDemo() {
    timerFrame.jFrame.setTitle(getClass().getSimpleName());
    SpinnerLabel<SmoothingKernel> spinnerFilter = new SpinnerLabel<>();
    SpinnerLabel<Integer> spinnerRadius = new SpinnerLabel<>();
    {
      SpinnerLabel<String> spinnerLabel = new SpinnerLabel<>();
      List<String> list = ResourceData.lines("/dubilab/app/pose/index.txt");
      spinnerLabel.addSpinnerListener(resource -> //
      control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + resource + ".csv").stream() //
          .limit(270).map(row -> row.extract(1, 4))));
      spinnerLabel.setList(list);
      spinnerLabel.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "data");
    }
    // try {
    // Tensor rows = Import.of(new File("/home/datahaki/Documents/datasets/swisstrolley/processed/1.csv"));
    // control = Tensors.empty();
    // Scalar RAD2DEG = RealScalar.of(180 / Math.PI);
    // int index = 0;
    // for (Tensor row : rows) {
    // Scalar x = row.Get(0).multiply(RAD2DEG);
    // Scalar y = row.Get(1).multiply(RAD2DEG);
    // Tensor transform = WGS84toCH1903LV03Plus.transform(x, y);
    // control.append(transform.append(row.Get(2)));
    // ++index;
    // if (2000 < index)
    // break;
    // }
    // control = Tensor.of(control.stream().map(r -> r.pmul(Tensors.vector(.1, .1, -1))));
    // Tensor mean = Mean.of(control);
    // mean.set(RealScalar.of(Math.PI / 2), 2);
    // control = Tensor.of(control.stream().map(row -> row.subtract(mean)));
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    JToggleButton jToggleCtrl = new JToggleButton("ctrl");
    jToggleCtrl.setSelected(true);
    timerFrame.jToolBar.add(jToggleCtrl);
    // ---
    JToggleButton jToggleLine = new JToggleButton("line");
    jToggleLine.setSelected(true);
    timerFrame.jToolBar.add(jToggleLine);
    // ---
    JToggleButton jToggleDiff = new JToggleButton("diff");
    jToggleDiff.setSelected(true);
    timerFrame.jToolBar.add(jToggleDiff);
    // ---
    JToggleButton jToggleWait = new JToggleButton("wait");
    jToggleWait.setSelected(false);
    timerFrame.jToolBar.add(jToggleWait);
    // ---
    timerFrame.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        if (jToggleWait.isSelected())
          return;
        // graphics.drawImage(image, 100, 100, null);
        GraphicsUtil.setQualityHigh(graphics);
        // boolean isR2 = jToggleButton.isSelected();
        // Tensor _control = control.copy();
        final int radius = spinnerRadius.getValue();
        final Tensor refined;
        // final Tensor curve;
        if (jToggleCtrl.isSelected()) {
          final Color color = new Color(255, 128, 128, 255);
          if (jToggleLine.isSelected()) {
            graphics.setColor(color);
            graphics.draw(geometricLayer.toPath2D(control));
          }
          for (Tensor point : control) {
            geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point));
            Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_HI);
            path2d.closePath();
            graphics.setColor(new Color(255, 128, 128, 64));
            graphics.fill(path2d);
            graphics.setColor(color);
            graphics.draw(path2d);
            geometricLayer.popMatrix();
          }
        }
        TensorUnaryOperator geodesicCenterFilter = //
            GeodesicCenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, spinnerFilter.getValue()), radius);
        refined = geodesicCenterFilter.apply(control);
        if (jToggleDiff.isSelected()) {
          final int baseline_y = 200;
          {
            graphics.setColor(Color.BLACK);
            graphics.drawLine(0, baseline_y, 300, baseline_y);
          }
          ColorDataIndexed colorDataIndexed = ColorDataLists._097.cyclic();
          {
            int piy = 30;
            graphics.drawString("Filter: " + spinnerFilter.getValue(), 0, piy);
            Scalar width = Quantity.of(0.05 * (spinnerRadius.getValue() * 2 + 1), "s");
            graphics.drawString("Window: " + Round._3.apply(width), 0, piy += 15);
            graphics.setColor(colorDataIndexed.getColor(0));
            graphics.drawString("Tangent velocity", 0, piy += 15);
            graphics.setColor(colorDataIndexed.getColor(1));
            graphics.drawString("Side slip", 0, piy += 15);
            graphics.setColor(colorDataIndexed.getColor(2));
            graphics.drawString("Rotational rate", 0, piy += 15);
          }
          LieDifferences lieDifferences = //
              new LieDifferences(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
          Tensor speeds = lieDifferences.apply(refined);
          for (int c = 0; c < 3; ++c) {
            graphics.setColor(colorDataIndexed.getColor(c));
            Path2D path2d = plotFunc(graphics, speeds.get(Tensor.ALL, c).multiply(RealScalar.of(400)), baseline_y);
            graphics.setStroke(new BasicStroke(1.3f));
            graphics.draw(path2d);
          }
        }
        graphics.setStroke(new BasicStroke(1f));
        int rgb = 128 + 32;
        final Color color = new Color(rgb, rgb, rgb, 128 + 64);
        if (jToggleLine.isSelected()) {
          graphics.setColor(color);
          graphics.draw(geometricLayer.toPath2D(refined));
        }
        for (Tensor point : refined) {
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point.copy().append(RealScalar.ZERO)));
          Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_LO);
          geometricLayer.popMatrix();
          path2d.closePath();
          graphics.setColor(color);
          graphics.fill(path2d);
          graphics.setColor(Color.BLACK);
          graphics.draw(path2d);
        }
      }
    });
    {
      // spinnerFilter.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
      spinnerFilter.setList(Arrays.asList(SmoothingKernel.values()));
      spinnerFilter.setValue(SmoothingKernel.GAUSSIAN);
      spinnerFilter.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "filter");
    }
    {
      spinnerRadius.setList(IntStream.range(0, 21).boxed().collect(Collectors.toList()));
      spinnerRadius.setValue(6);
      spinnerRadius.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    }
  }

  protected Path2D plotFunc(Graphics2D graphics, Tensor tensor, int baseline_y) {
    Path2D path2d = new Path2D.Double();
    if (Tensors.nonEmpty(tensor))
      path2d.moveTo(0, baseline_y - tensor.Get(0).number().doubleValue());
    for (int pix = 1; pix < tensor.length(); ++pix)
      path2d.lineTo(pix, baseline_y - tensor.Get(pix).number().doubleValue());
    return path2d;
  }

  public static void main(String[] args) {
    GeodesicCenterFilterDemo geodesicCenterFilterDemo = new GeodesicCenterFilterDemo();
    geodesicCenterFilterDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    geodesicCenterFilterDemo.timerFrame.jFrame.setVisible(true);
  }
}
