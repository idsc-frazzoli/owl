// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Norm;

class CurveSubdivisionDemo {
  private static final Tensor ARROWHEAD_HI = Tensors.matrixDouble( //
      new double[][] { { .3, 0 }, { -.1, -.1 }, { -.1, +.1 } }).multiply(RealScalar.of(2));
  private static final Tensor ARROWHEAD_LO = Tensors.matrixDouble( //
      new double[][] { { .3, 0 }, { -.1, -.1 }, { -.1, +.1 } }).multiply(RealScalar.of(.5));
  private static final Tensor CIRCLE_HI = CirclePoints.of(15).multiply(RealScalar.of(.2));
  private static final Tensor CIRCLE_LO = CirclePoints.of(15).multiply(RealScalar.of(.05));
  private static final Scalar SCALE = DoubleScalar.of(3);
  // ---
  private final Tensor control = Tensors.of(Array.zeros(3));
  private final TimerFrame timerFrame = new TimerFrame();
  private Tensor mouse = Array.zeros(3);
  private Integer min_index = null;
  private static final Tensor FCURVE = FresnelCurve.of(300).multiply(RealScalar.of(10));

  CurveSubdivisionDemo() {
    SpinnerLabel<CurveSubdivisionSchemes> spinnerLabel = new SpinnerLabel<>();
    SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
    JToggleButton jToggleButton = new JToggleButton("R2");
    timerFrame.jToolBar.add(jToggleButton);
    timerFrame.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        mouse = geometricLayer.getMouseSe2State();
        if (Objects.nonNull(min_index))
          control.set(mouse, min_index);
        {
          // LogarithmicSpiral logarithmicSpiral = new LogarithmicSpiral(RealScalar.of(2), RealScalar.of(0.1759));
          // Tensor path = Tensors.empty();
          // for (Tensor _r : Subdivide.of(0, 20, 100)) {
          // Scalar theta = _r.Get();
          // Scalar z = ComplexScalar.fromPolar(logarithmicSpiral.apply(theta), theta);
          // path.append(Tensors.of(Real.of(z), Imag.of(z)));
          // }
          // graphics.setColor(new Color(128, 128, 128, 128));
          // graphics.draw(geometricLayer.toPath2D(path));
        }
        {
          // LogarithmicSpiral logarithmicSpiral = new LogarithmicSpiral(RealScalar.of(2), RealScalar.of(0.1759));
          // Tensor path = Tensors.empty();
          // for (Tensor _r : Subdivide.of(0, 20, 100)) {
          // Scalar theta = _r.Get();
          // Scalar z = ComplexScalar.fromPolar(logarithmicSpiral.apply(theta), theta);
          // path.append(Tensors.of(Real.of(z), Imag.of(z)));
          // }
          graphics.setColor(new Color(128, 128, 128, 128));
          graphics.draw(geometricLayer.toPath2D(FCURVE));
        }
        Function<GeodesicInterface, CurveSubdivision> function = spinnerLabel.getValue().function;
        boolean isR2 = jToggleButton.isSelected();
        if (isR2) {
          CurveSubdivision curveSubdivision = function.apply(RnGeodesic.INSTANCE);
          Tensor rnctrl = Tensor.of(control.stream().map(ExtractXY::of));
          Tensor refined = Nest.of(curveSubdivision::string, rnctrl, spinnerRefine.getValue());
          {
            graphics.setColor(new Color(0, 0, 255, 128));
            graphics.draw(geometricLayer.toPath2D(refined));
          }
          graphics.setColor(new Color(255, 128, 128, 255));
          for (Tensor point : control) {
            geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point));
            graphics.fill(geometricLayer.toPath2D(CIRCLE_HI));
            geometricLayer.popMatrix();
          }
          {
            graphics.setColor(Color.BLUE);
            graphics.draw(geometricLayer.toPath2D(refined));
            graphics.setColor(Color.BLACK);
            graphics.draw(geometricLayer.toPath2D(StaticHelper.curvature(refined, SCALE)));
          }
        } else {
          CurveSubdivision curveSubdivision = function.apply(Se2Geodesic.INSTANCE);
          Tensor refined = Nest.of(curveSubdivision::string, control, spinnerRefine.getValue());
          graphics.setColor(new Color(128, 128, 128, 128));
          for (Tensor point : refined) {
            geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point));
            graphics.fill(geometricLayer.toPath2D(ARROWHEAD_LO));
            geometricLayer.popMatrix();
          }
          graphics.setColor(new Color(255, 128, 128, 255));
          for (Tensor point : control) {
            geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point));
            graphics.fill(geometricLayer.toPath2D(ARROWHEAD_HI));
            geometricLayer.popMatrix();
          }
          {
            graphics.setColor(Color.BLUE);
            graphics.draw(geometricLayer.toPath2D(refined));
            graphics.setColor(Color.BLACK);
            graphics.draw(geometricLayer.toPath2D(StaticHelper.curvature(refined, SCALE)));
          }
        }
        if (Objects.isNull(min_index)) {
          graphics.setColor(Color.GREEN);
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(mouse));
          graphics.fill(geometricLayer.toPath2D(isR2 ? CIRCLE_HI : ARROWHEAD_HI));
          geometricLayer.popMatrix();
        }
      }
    });
    {
      spinnerLabel.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
      spinnerLabel.setArray(CurveSubdivisionSchemes.values());
      spinnerLabel.setIndex(2);
      spinnerLabel.addToComponentReduced(timerFrame.jToolBar, new Dimension(150, 28), "scheme");
    }
    {
      spinnerRefine.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
      spinnerRefine.setList(Arrays.asList(3, 4, 5, 6, 7, 8, 9));
      spinnerRefine.setIndex(2);
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
    timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
  }

  public static void main(String[] args) {
    CurveSubdivisionDemo curveSubdivisionDemo = new CurveSubdivisionDemo();
    curveSubdivisionDemo.timerFrame.jFrame.setBounds(100, 100, 600, 600);
    curveSubdivisionDemo.timerFrame.jFrame.setVisible(true);
  }
}
