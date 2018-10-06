// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.owl.subdiv.curve.FilterMask;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenter;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenterFilter;
import ch.ethz.idsc.owl.subdiv.curve.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

class GeodesicCenterFilterDemo {
  private static final Tensor ARROWHEAD_HI = Arrowhead.of(0.10);
  private static final Tensor ARROWHEAD_LO = Arrowhead.of(0.12);
  // ---
  private final TimerFrame timerFrame = new TimerFrame();
  private Tensor control = Tensors.of(Array.zeros(3));

  GeodesicCenterFilterDemo() {
    SpinnerLabel<FilterMask> spinnerFilter = new SpinnerLabel<>();
    SpinnerLabel<Integer> spinnerRadius = new SpinnerLabel<>();
    {
      String resource;
      resource = "/dubilab/app/filter/0w/20180702T133612_1.csv";
      resource = "/dubilab/app/filter/2r/20180820T143852_1.csv";
      Tensor table = ResourceData.of(resource);
      control = Tensor.of(table.stream().limit(2400).map(row -> row.extract(1, 4)));
    }
    {
      JButton jButton = new JButton("clear");
      jButton.addActionListener(actionEvent -> control = Tensors.of(Array.zeros(3)));
      timerFrame.jToolBar.add(jButton);
    }
    JTextField jTextField = new JTextField(10);
    jTextField.setPreferredSize(new Dimension(100, 28));
    {
      timerFrame.jToolBar.add(jTextField);
    }
    JToggleButton jToggleCtrl = new JToggleButton("ctrl");
    jToggleCtrl.setSelected(true);
    timerFrame.jToolBar.add(jToggleCtrl);
    // ---
    JToggleButton jToggleLine = new JToggleButton("line");
    jToggleLine.setSelected(false);
    timerFrame.jToolBar.add(jToggleLine);
    // ---
    timerFrame.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        // graphics.drawImage(image, 100, 100, null);
        GraphicsUtil.setQualityHigh(graphics);
        // boolean isR2 = jToggleButton.isSelected();
        // Tensor _control = control.copy();
        final int radius = spinnerRadius.getValue();
        final Tensor refined;
        // final Tensor curve;
        if (jToggleCtrl.isSelected())
          for (Tensor point : control) {
            geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point));
            Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_HI);
            path2d.closePath();
            graphics.setColor(new Color(255, 128, 128, 64));
            graphics.fill(path2d);
            graphics.setColor(new Color(255, 128, 128, 255));
            graphics.draw(path2d);
            geometricLayer.popMatrix();
          }
        GeodesicCenterFilter geodesicCenterFilter = //
            new GeodesicCenterFilter(new GeodesicCenter(Se2Geodesic.INSTANCE, spinnerFilter.getValue()), radius);
        refined = geodesicCenterFilter.apply(control);
        Tensor speeds = Se2SpeedEstimate.FUNCTION.apply(refined);
        final int baseline_y = 300;
        {
          graphics.setColor(Color.BLACK);
          graphics.drawLine(0, baseline_y, 1200, baseline_y);
        }
        ColorDataIndexed colorDataIndexed = ColorDataLists._097.cyclic();
        {
          int piy = 30;
          graphics.drawString("Filter: " + spinnerFilter.getValue(), 0, piy);
          Scalar width = Quantity.of(0.05 * (spinnerRadius.getValue() * 2 - 1), "s");
          graphics.drawString("Window: " + Round._3.apply(width), 0, piy += 15);
          graphics.setColor(colorDataIndexed.getColor(0));
          graphics.drawString("Tangent velocity", 0, piy += 15);
          graphics.setColor(colorDataIndexed.getColor(1));
          graphics.drawString("Side slip", 0, piy += 15);
          graphics.setColor(colorDataIndexed.getColor(2));
          graphics.drawString("Rotational rate", 0, piy += 15);
        }
        for (int c = 0; c < 3; ++c) {
          graphics.setColor(colorDataIndexed.getColor(c));
          Path2D path2d = plotFunc(graphics, speeds.get(Tensor.ALL, c).multiply(RealScalar.of(400)), baseline_y);
          graphics.setStroke(new BasicStroke(1.3f));
          graphics.draw(path2d);
        }
        graphics.setStroke(new BasicStroke(1f));
        for (Tensor point : refined) {
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point.copy().append(RealScalar.ZERO)));
          Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_LO);
          geometricLayer.popMatrix();
          int rgb = 128 + 32;
          path2d.closePath();
          graphics.setColor(new Color(rgb, rgb, rgb, 128 + 64));
          graphics.fill(path2d);
          graphics.setColor(Color.BLACK);
          graphics.draw(path2d);
        }
      }
    });
    {
      // spinnerFilter.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
      spinnerFilter.setList(Arrays.asList(FilterMask.values()));
      spinnerFilter.setValue(FilterMask.CONSTANT);
      spinnerFilter.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "filter");
    }
    {
      // spinnerRadius.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
      spinnerRadius.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
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
    GeodesicCenterFilterDemo curveSubdivisionDemo = new GeodesicCenterFilterDemo();
    curveSubdivisionDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    curveSubdivisionDemo.timerFrame.jFrame.setVisible(true);
  }
}
