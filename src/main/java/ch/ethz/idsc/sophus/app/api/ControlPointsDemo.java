// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Objects;

import javax.swing.JButton;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.sophus.app.util.DubinsGenerator;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.red.Norm;

public abstract class ControlPointsDemo extends AbstractDemo {
  protected static final Color CP_FILL = new Color(255, 128, 128, 64);
  protected static final Color CP_EDGE = new Color(255, 128, 128, 255);
  // ---
  private final JButton jButton = new JButton("clear");
  protected final SpinnerLabel<GeodesicDisplay> geodesicDisplaySpinner = new SpinnerLabel<>();
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
        graphics.fill(geometricLayer.toPath2D(geodesicDisplay().shape()));
        geometricLayer.popMatrix();
      }
    }
  };

  public ControlPointsDemo(boolean clearButton, List<GeodesicDisplay> list) {
    if (clearButton) {
      jButton.addActionListener(actionEvent -> control = Tensors.of(Array.zeros(3)));
      timerFrame.jToolBar.add(jButton);
    }
    if (!list.isEmpty()) {
      geodesicDisplaySpinner.setList(list);
      geodesicDisplaySpinner.setValue(list.get(0));
      if (1 < list.size()) {
        geodesicDisplaySpinner.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "geodesic type");
        timerFrame.jToolBar.addSeparator();
      }
    }
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
    jButton.addActionListener(actionEvent -> setControl(DubinsGenerator.project(control)));
    timerFrame.jToolBar.add(jButton);
  }

  public GeodesicDisplay geodesicDisplay() {
    return geodesicDisplaySpinner.getValue();
  }

  protected void renderControlPoints(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor shape = geodesicDisplay.shape();
    for (Tensor point : control()) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
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
    this.control = MatrixQ.require(control);
    List<Integer> list = Dimensions.of(control);
    if (list.get(1) != 3)
      System.err.println(list);
    // throw new RuntimeException();
  }

  public Tensor control() {
    return Tensor.of(control.stream().map(geodesicDisplay()::project));
  }

  public GeodesicInterface geodesicInterface() {
    return geodesicDisplay().geodesicInterface();
  }
}
