// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.CurvatureComb;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.BSplineFunction;
import ch.ethz.idsc.tensor.red.Norm;

class BSplineFunctionDemo {
  private static final Tensor CIRCLE_HI = CirclePoints.of(15).multiply(RealScalar.of(.1));
  private static final Scalar COMB_SCALE = DoubleScalar.of(1); // .5 (1 for presentation)
  private static final Color COLOR_CURVATURE_COMB = new Color(0, 0, 0, 128);
  // ---
  private Tensor control = Tensors.of(Array.zeros(3));
  private final TimerFrame timerFrame = new TimerFrame();
  private Tensor mouse = Array.zeros(3);
  private Integer min_index = null;

  BSplineFunctionDemo() {
    timerFrame.jFrame.setTitle(getClass().getSimpleName());
    SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
    SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
    {
      control = Tensors.fromString("{{0,0},{2,0}}");
    }
    {
      JButton jButton = new JButton("clear");
      jButton.addActionListener(actionEvent -> control = Tensors.of(Array.zeros(3)));
      timerFrame.jToolBar.add(jButton);
    }
    JToggleButton jToggleCtrl = new JToggleButton("ctrl");
    jToggleCtrl.setSelected(true);
    timerFrame.jToolBar.add(jToggleCtrl);
    // ---
    JToggleButton jToggleComb = new JToggleButton("comb");
    jToggleComb.setSelected(true);
    timerFrame.jToolBar.add(jToggleComb);
    // ---
    timerFrame.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        GraphicsUtil.setQualityHigh(graphics);
        mouse = geometricLayer.getMouseSe2State();
        if (Objects.nonNull(min_index))
          control.set(mouse.extract(0, 2), min_index);
        int levels = spinnerRefine.getValue();
        BSplineFunction bSplineFunction = BSplineFunction.of(spinnerDegree.getValue(), control);
        final Tensor refined = Subdivide.of(0, control.length() - 1, 4 << levels).map(bSplineFunction);
        {
          graphics.setColor(new Color(0, 0, 255, 128));
          graphics.draw(geometricLayer.toPath2D(refined));
        }
        graphics.setColor(new Color(255, 128, 128, 255));
        for (Tensor point : control) {
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point.copy().append(RealScalar.ZERO)));
          Path2D path2d = geometricLayer.toPath2D(CIRCLE_HI);
          path2d.closePath();
          graphics.setColor(new Color(255, 128, 128, 64));
          graphics.fill(path2d);
          graphics.setColor(new Color(255, 128, 128, 255));
          graphics.draw(path2d);
          geometricLayer.popMatrix();
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
        if (Objects.isNull(min_index)) {
          graphics.setColor(Color.GREEN);
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(mouse));
          graphics.fill(geometricLayer.toPath2D(CIRCLE_HI));
          geometricLayer.popMatrix();
        }
      }
    });
    {
      spinnerDegree.setList(Arrays.asList(0, 1, 2, 3, 4));
      spinnerDegree.setValue(1);
      spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "degree");
    }
    {
      spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
      spinnerRefine.setValue(4);
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
              control.append(mouse.extract(0, 2));
            }
          } else {
            min_index = null;
          }
        }
      }
    });
  }

  public static void main(String[] args) {
    BSplineFunctionDemo curveSubdivisionDemo = new BSplineFunctionDemo();
    curveSubdivisionDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    curveSubdivisionDemo.timerFrame.jFrame.setVisible(true);
  }
}
