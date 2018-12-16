// code by jph
package ch.ethz.idsc.sophus.app.demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.red.Norm;

public abstract class ControlPointsDemo extends AbstractDemo {
  protected static final Tensor ARROWHEAD_HI = Arrowhead.of(0.4);
  protected static final Tensor CIRCLE_HI = CirclePoints.of(15).multiply(RealScalar.of(0.1));
  protected static final Color CP_FILL = new Color(255, 128, 128, 64);
  protected static final Color CP_EDGE = new Color(255, 128, 128, 255);
  // ---
  protected final JButton jButton = new JButton("clear");
  protected final JToggleButton jToggleButton = new JToggleButton("R2");
  // ---
  private Tensor control = Tensors.of(Array.zeros(3));
  private Tensor mouse = Array.zeros(3);
  private Integer min_index = null;
  // ---
  private final RenderInterface renderInterface = new RenderInterface() {
    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      mouse = geometricLayer.getMouseSe2State();
      if (Objects.nonNull(min_index))
        control.set(mouse, min_index);
      if (Objects.isNull(min_index)) {
        graphics.setColor(Color.GREEN);
        geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(mouse));
        graphics.fill(geometricLayer.toPath2D(shape()));
        geometricLayer.popMatrix();
      }
    }
  };

  public ControlPointsDemo() {
    jButton.addActionListener(actionEvent -> control = Tensors.of(Array.zeros(3)));
    // --
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
    timerFrame.geometricComponent.addRenderInterface(renderInterface);
  }

  public void addButtonDubins() {
    JButton jButton = new JButton("dubins");
    jButton.setToolTipText("project control points to dubins path");
    jButton.addActionListener(actionEvent -> setControl(DubinsGenerator.project(controlSe2())));
    timerFrame.jToolBar.add(jButton);
  }

  public Tensor shape() {
    return jToggleButton.isSelected() ? CIRCLE_HI : ARROWHEAD_HI;
  }

  protected void renderControlPoints(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor shape = shape();
    for (Tensor point : controlSe2()) {
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point));
      Path2D path2d = geometricLayer.toPath2D(shape);
      path2d.closePath();
      graphics.setColor(CP_FILL);
      graphics.fill(path2d);
      graphics.setColor(CP_EDGE);
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
  }

  public void setControl(Tensor control) {
    this.control = control;
  }

  public Tensor controlR2() {
    return Tensor.of(control.stream().map(Extract2D::of));
  }

  public Tensor controlSe2() {
    return control.copy();
  }
}
