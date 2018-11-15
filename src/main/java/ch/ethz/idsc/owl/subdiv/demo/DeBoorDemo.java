// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Arrays;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.opt.BSplineFunction;
import ch.ethz.idsc.tensor.opt.DeBoor;

class DeBoorDemo {
  private final TimerFrame timerFrame = new TimerFrame();

  DeBoorDemo() {
    timerFrame.jFrame.setTitle(getClass().getSimpleName());
    SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
    SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
    // ---
    timerFrame.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        GraphicsUtil.setQualityHigh(graphics);
        int degree = spinnerDegree.getValue();
        {
          graphics.setStroke(new BasicStroke(1.25f));
          Tensor matrix = geometricLayer.getMatrix();
          geometricLayer.pushMatrix(Inverse.of(matrix));
          {
            ColorDataIndexed cyclic = ColorDataLists._097.cyclic().deriveWithAlpha(192);
            graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
            // control length
            for (int length = 2; length <= 6; ++length) {
              Tensor string = Tensors.fromString("{{100, 0, 0}, {0, -80, 0}, {0, 0, 1}}");
              string.set(RealScalar.of(110 * length), 1, 2);
              geometricLayer.pushMatrix(string);
              Tensor domain = Subdivide.of(0, length - 1, 100);
              {
                for (int k_th = 0; k_th < length; ++k_th) {
                  graphics.setColor(cyclic.getColor(k_th));
                  BSplineFunction bSplineFunction = BSplineFunction.of(degree, UnitVector.of(length, k_th));
                  DeBoor deBoor = bSplineFunction.deBoor(RealScalar.of(k_th));
                  Tensor knots = deBoor.knots();
                  Point2D point2d = geometricLayer.toPoint2D(k_th, 0);
                  graphics.drawString(length + " " + (k_th) + ":" + knots.toString().replace(" ", ""), //
                      (int) point2d.getX(), //
                      (int) point2d.getY() + 10);
                  Tensor values = domain.map(bSplineFunction);
                  Tensor tensor = Transpose.of(Tensors.of(domain, values));
                  Path2D path2d = geometricLayer.toPath2D(tensor);
                  graphics.draw(path2d);
                }
              }
              geometricLayer.popMatrix();
            }
          }
          geometricLayer.popMatrix();
          graphics.setStroke(new BasicStroke(1f));
        }
        graphics.setColor(new Color(255, 128, 128, 255));
      }
    });
    {
      spinnerDegree.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6));
      spinnerDegree.setValue(1);
      spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "degree");
    }
    {
      spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
      spinnerRefine.setValue(4);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    }
  }

  public static void main(String[] args) {
    DeBoorDemo curveSubdivisionDemo = new DeBoorDemo();
    curveSubdivisionDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    curveSubdivisionDemo.timerFrame.jFrame.setVisible(true);
  }
}
