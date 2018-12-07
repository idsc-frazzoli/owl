// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.group.Se2CoveringGeodesic;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.owl.subdiv.surf.CatmullClarkSubdivision;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ class CatmullClarkSubdivisionDemo extends AbstractDemo {
  private static final Tensor ARROWHEAD_HI = Arrowhead.of(0.40);
  private static final Tensor ARROWHEAD_LO = Arrowhead.of(0.18);
  // ---
  private final TimerFrame timerFrame = new TimerFrame();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  // ---
  private Tensor control = Tensors.of(Array.zeros(3));
  private Tensor mouse = Array.zeros(3);
  private Integer min_index = null;
  private boolean printref = false;
  private boolean ref2ctrl = false;

  CatmullClarkSubdivisionDemo() {
    timerFrame.jFrame.setTitle(getClass().getSimpleName());
    {
      control = Tensors.fromString("{{0,0,0},{1,0,0},{2,0,0},{0,1,0},{1,1,0},{2,1,0}}").multiply(RealScalar.of(2));
    }
    timerFrame.geometricComponent.addRenderInterface(this);
    {
      spinnerRefine.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
      spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5));
      spinnerRefine.setValue(2);
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
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    mouse = geometricLayer.getMouseSe2State();
    if (Objects.nonNull(min_index))
      control.set(mouse, min_index);
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
    CatmullClarkSubdivision catmullClarkSubdivision = new CatmullClarkSubdivision(Se2CoveringGeodesic.INSTANCE);
    Tensor refined = Nest.of( //
        catmullClarkSubdivision::refine, //
        ArrayReshape.of(control, 2, 3, 3), //
        spinnerRefine.getValue());
    for (Tensor points : refined)
      for (Tensor point : points) {
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
    if (Objects.isNull(min_index)) {
      graphics.setColor(Color.GREEN);
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(mouse));
      graphics.fill(geometricLayer.toPath2D(ARROWHEAD_HI));
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

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new CatmullClarkSubdivisionDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
