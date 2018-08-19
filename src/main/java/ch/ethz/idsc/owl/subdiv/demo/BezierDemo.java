// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.owl.math.planar.CurvatureComb;
import ch.ethz.idsc.owl.math.planar.ExtractXY;
import ch.ethz.idsc.owl.subdiv.curve.BezierCurve;
import ch.ethz.idsc.owl.subdiv.curve.RnGeodesic;
import ch.ethz.idsc.owl.subdiv.curve.Se2CoveringGeodesic;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.red.Norm;

class BezierDemo {
  private static final Tensor ARROWHEAD_HI = Arrowhead.of(0.40);
  private static final Tensor ARROWHEAD_LO = Arrowhead.of(0.18);
  private static final Tensor CIRCLE_HI = CirclePoints.of(15).multiply(RealScalar.of(.1));
  private static final Scalar COMB_SCALE = DoubleScalar.of(1); // .5 (1 for presentation)
  private static final Color COLOR_CURVATURE_COMB = new Color(0, 0, 0, 128);
  // ---
  private Tensor control = Tensors.of(Array.zeros(3));
  private final TimerFrame timerFrame = new TimerFrame();
  private Tensor mouse = Array.zeros(3);
  private Integer min_index = null;
  private boolean printref = false;
  private boolean ref2ctrl = false;

  BezierDemo() {
    SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
    {
      Tensor blub = Tensors.fromString("{{1,0,0},{1,0,0},{2,0,2.5708},{1,0,2.1},{1.5,0,0},{2.3,0,-1.2},{1.5,0,0},{4,0,3.14159},{2,0,3.14159},{2,0,0}}");
      control = DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
          Tensor.of(blub.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1)))));
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
    {
      JButton jButton = new JButton("print");
      jButton.addActionListener(actionEvent -> {
        System.out.println(control);
        // long now = System.currentTimeMillis();
        File file = UserHome.file("" + jTextField.getText() + ".csv");
        // File file = new File("src/main/resources/subdiv/se2", now + ".csv");
        try {
          Export.of(file, control.map(CsvFormat.strict()));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      });
      timerFrame.jToolBar.add(jButton);
    }
    {
      JButton jButton = new JButton("p-ref");
      jButton.addActionListener(actionEvent -> printref = true);
      timerFrame.jToolBar.add(jButton);
    }
    {
      JButton jButton = new JButton("r2c");
      jButton.addActionListener(actionEvent -> ref2ctrl = true);
      timerFrame.jToolBar.add(jButton);
    }
    JToggleButton jToggleCtrl = new JToggleButton("ctrl");
    jToggleCtrl.setSelected(true);
    timerFrame.jToolBar.add(jToggleCtrl);
    // ---
    JToggleButton jToggleBndy = new JToggleButton("bndy");
    jToggleBndy.setSelected(true);
    timerFrame.jToolBar.add(jToggleBndy);
    // ---
    JToggleButton jToggleComb = new JToggleButton("comb");
    jToggleComb.setSelected(true);
    timerFrame.jToolBar.add(jToggleComb);
    // ---
    JToggleButton jToggleLine = new JToggleButton("line");
    jToggleLine.setSelected(false);
    timerFrame.jToolBar.add(jToggleLine);
    // ---
    JToggleButton jToggleButton = new JToggleButton("R2");
    jToggleButton.setSelected(Dimensions.of(control).get(1) == 2);
    timerFrame.jToolBar.add(jToggleButton);
    timerFrame.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        // graphics.drawImage(image, 100, 100, null);
        GraphicsUtil.setQualityHigh(graphics);
        mouse = geometricLayer.getMouseSe2State();
        if (Objects.nonNull(min_index))
          control.set(mouse, min_index);
        boolean isR2 = jToggleButton.isSelected();
        Tensor _control = control.copy();
        int levels = spinnerRefine.getValue();
        final Tensor refined;
        if (isR2) {
          BezierCurve bezierCurve = new BezierCurve(RnGeodesic.INSTANCE);
          Tensor rnctrl = Tensor.of(_control.stream().map(ExtractXY::of));
          refined = bezierCurve.refine(rnctrl, 1 << levels);
          {
            graphics.setColor(new Color(0, 0, 255, 128));
            graphics.draw(geometricLayer.toPath2D(refined));
          }
          graphics.setColor(new Color(255, 128, 128, 255));
          for (Tensor point : _control) {
            geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point.copy().append(RealScalar.ZERO)));
            Path2D path2d = geometricLayer.toPath2D(CIRCLE_HI);
            path2d.closePath();
            graphics.setColor(new Color(255, 128, 128, 64));
            graphics.fill(path2d);
            graphics.setColor(new Color(255, 128, 128, 255));
            graphics.draw(path2d);
            geometricLayer.popMatrix();
          }
        } else { // SE2
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
          BezierCurve bezierCurve = new BezierCurve(Se2CoveringGeodesic.INSTANCE);
          refined = bezierCurve.refine(_control, 1 << levels);
        }
        if (jToggleLine.isSelected()) {
          BezierCurve bezierCurve = new BezierCurve(Se2CoveringGeodesic.INSTANCE);
          Tensor linear = bezierCurve.refine(_control, 1 << 8);
          graphics.setColor(new Color(0, 255, 0, 128));
          Path2D path2d = geometricLayer.toPath2D(linear);
          graphics.draw(path2d);
        }
        {
          graphics.setColor(Color.BLUE);
          Path2D path2d = geometricLayer.toPath2D(refined);
          graphics.setStroke(new BasicStroke(1.25f));
          graphics.draw(path2d);
          graphics.setStroke(new BasicStroke(1f));
        }
        if (jToggleComb.isSelected()) {
          graphics.setColor(COLOR_CURVATURE_COMB);
          Path2D path2d = geometricLayer.toPath2D(CurvatureComb.of(refined, COMB_SCALE, false));
          graphics.draw(path2d);
        }
        if (!isR2) {
          if (levels < 5) {
            for (Tensor point : refined) {
              geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point));
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
        }
        if (Objects.isNull(min_index)) {
          graphics.setColor(Color.GREEN);
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(mouse));
          graphics.fill(geometricLayer.toPath2D(isR2 ? CIRCLE_HI : ARROWHEAD_HI));
          geometricLayer.popMatrix();
        }
        if (printref) {
          printref = false;
          System.out.println(refined);
        }
        if (ref2ctrl) {
          ref2ctrl = false;
          control = refined;
        }
      }
    });
    // {
    // spinnerLabel.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
    // spinnerLabel.setArray(CurveSubdivisionSchemes.values());
    // spinnerLabel.setIndex(2);
    // spinnerLabel.addToComponentReduced(timerFrame.jToolBar, new Dimension(150, 28), "scheme");
    // }
    // {
    // spinnerAlpha.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
    // spinnerAlpha.setArray(CurveSubdivisionSchemes.values());
    // spinnerAlpha.setIndex(2);
    // spinnerAlpha.addToComponentReduced(timerFrame.jToolBar, new Dimension(150, 28), "scheme");
    // }
    {
      spinnerRefine.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
      spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
      spinnerRefine.setValue(9);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    }
    timerFrame.geometricComponent.jComponent.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == 1) {
          if (Objects.isNull(min_index)) {
            Scalar cmp = DoubleScalar.of(.2);
            int index = 0;
            for (Tensor point : control) {
              Scalar distance = Norm._2.between(point.extract(0, 2), mouse.extract(0, 2));
              if (Scalars.lessThan(distance, cmp)) {
                cmp = distance;
                min_index = index;
              }
              ++index;
            }
            if (min_index == null) {
              min_index = control.length();
              control.append(mouse);
            }
          } else {
            min_index = null;
          }
        }
      }
    });
    // timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
  }

  public static void main(String[] args) {
    BezierDemo curveSubdivisionDemo = new BezierDemo();
    curveSubdivisionDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    curveSubdivisionDemo.timerFrame.jFrame.setVisible(true);
  }
}
