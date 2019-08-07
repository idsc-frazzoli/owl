// code by jph, gjoel
package ch.ethz.idsc.sophus.app.api;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JButton;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** class is used in other projects outside of owl */
public abstract class ControlPointsDemo extends GeodesicDisplayDemo {
  /** mouse snaps 20 pixel to control points */
  private static final Scalar PIXEL_THRESHOLD = RealScalar.of(20.0);
  /** control points */
  private static final PointsRender POINTS_RENDER_0 = //
      new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
  /** refined points */
  private static final PointsRender POINTS_RENDER_1 = //
      new PointsRender(new Color(160, 160, 160, 128 + 64), Color.BLACK);
  // ---
  private Tensor control = Tensors.empty();
  private Tensor mouse = Array.zeros(3);
  /** min_index is non-null while the user drags a control points */
  private Integer min_index = null;
  private Integer pos_index = null;
  private boolean mousePositioning = true;
  // ---
  private final static Color ORANGE = new Color(255, 200, 0, 192);
  private final static Color GREEN = new Color(0, 255, 0, 192);
  // ---
  private final RenderInterface renderInterface = new RenderInterface() {
    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      mouse = geometricLayer.getMouseSe2State();
      if (Objects.nonNull(min_index))
        control.set(mouse, min_index);
      else {
        GeodesicDisplay geodesicDisplay = geodesicDisplay();
        Tensor mouse_dist = Tensor.of(control.stream().map(mouse::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector));
        ArgMinValue argMinValue = ArgMinValue.of(mouse_dist);
        Optional<Scalar> value = argMinValue.value(getPositioningThreshold());
        boolean hold = value.isPresent() && isPositioningEnabled();
        graphics.setColor(hold ? ORANGE : GREEN);
        Tensor posit = mouse;
        if (hold) {
          graphics.setStroke(new BasicStroke(2f));
          Tensor closest = control.get(argMinValue.index());
          graphics.draw(geometricLayer.toPath2D(Tensors.of(mouse, closest)));
          graphics.setStroke(new BasicStroke());
          posit.set(closest.get(0), 0);
          posit.set(closest.get(1), 1);
        }
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(geodesicDisplay.project(posit)));
        graphics.fill(geometricLayer.toPath2D(getControlPointShape()));
        geometricLayer.popMatrix();
        if (Objects.nonNull(pos_index)) {
          try {
            // Line2D line2d = ;
            graphics.setColor(Color.RED);
            graphics.draw(geometricLayer.toLine2D(mouse, control.get(pos_index)));
          } catch (Exception exception) {
            System.err.println("problem: " + pos_index);
          }
        }
      }
    }
  };

  /** @param addRemoveControlPoints
   * @param list */
  public ControlPointsDemo(boolean addRemoveControlPoints, List<GeodesicDisplay> list) {
    super(list);
    // ---
    if (addRemoveControlPoints) {
      ActionListener actionListener = actionEvent -> {
        min_index = null;
        control = Tensors.empty();
      };
      JButton jButton = new JButton("clear");
      jButton.addActionListener(actionListener);
      timerFrame.jToolBar.add(jButton);
    }
    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent mouseEvent) {
        if (Objects.isNull(min_index)) {
          // System.out.println("moved");
          // if (addRemoveControlPoints)
          Tensor mouse_dist = Tensor.of(control.stream().map(mouse::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector));
          ArgMinValue argMinValue = ArgMinValue.of(mouse_dist);
          pos_index = argMinValue.index(getPositioningThreshold()).orElse(null);
          if (Objects.isNull(pos_index) && addRemoveControlPoints) {
            // System.out.println("here ");
            pos_index = argMinValue.index();
            if (pos_index == control.length() - 1) {
              // pos_index = control.length();
              // control = control.append(mouse);
            } else //
            if (pos_index == 0) {
              // control = Join.of(Tensors.of(mouse), control);
            } else {
              if (Scalars.lessThan(mouse_dist.Get(pos_index + 1), mouse_dist.Get(pos_index - 1)))
                pos_index++;
              // control = Join.of(control.extract(0, pos_index).append(mouse), control.extract(pos_index, control.length()));
            }
          }
        } else {
          pos_index = null;
        }
      }

      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        if (!isPositioningEnabled())
          return;
        switch (mouseEvent.getButton()) {
        case MouseEvent.BUTTON1: // insert point
          if (Objects.isNull(min_index)) {
            Tensor mouse_dist = Tensor.of(control.stream().map(mouse::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector));
            ArgMinValue argMinValue = ArgMinValue.of(mouse_dist);
            min_index = argMinValue.index(getPositioningThreshold()).orElse(null);
            if (Objects.isNull(min_index) && addRemoveControlPoints) {
              min_index = argMinValue.index();
              if (min_index == control.length() - 1) {
                control = control.append(mouse);
                min_index = control.length();
              } else //
              if (min_index == 0)
                control = Join.of(Tensors.of(mouse), control);
              else {
                if (Scalars.lessThan(mouse_dist.Get(min_index + 1), mouse_dist.Get(min_index - 1)))
                  ++min_index;
                control = Join.of(control.extract(0, min_index).append(mouse), control.extract(min_index, control.length()));
              }
            }
          } else {
            min_index = null;
            released();
          }
          break;
        case MouseEvent.BUTTON3: // remove point
          if (addRemoveControlPoints) {
            if (Objects.isNull(min_index)) {
              Tensor mouse_dist = Tensor.of(control.stream().map(mouse::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector));
              ArgMinValue argMinValue = ArgMinValue.of(mouse_dist);
              min_index = argMinValue.index(getPositioningThreshold()).orElse(null);
            }
            if (Objects.nonNull(min_index)) {
              control = Join.of(control.extract(0, min_index), control.extract(min_index + 1, control.length()));
              min_index = null;
            }
          }
          break;
        }
      }
    };
    // timerFrame.geometricComponent.jComponent.addMouseListener(mouseAdapter);
    timerFrame.geometricComponent.jComponent.addMouseMotionListener(mouseAdapter);
    timerFrame.geometricComponent.addRenderInterface(renderInterface);
  }

  public Tensor getControlPointShape() {
    return geodesicDisplay().shape();
  }

  /** function is called when mouse is released */
  public void released() {
    // ---
  }

  public void setPositioningEnabled(boolean enabled) {
    if (!enabled)
      min_index = null;
    mousePositioning = enabled;
  }

  public boolean isPositioningEnabled() {
    return mousePositioning;
  }

  public Scalar getPositioningThreshold() {
    return PIXEL_THRESHOLD.divide(Sqrt.FUNCTION.apply(Det.of(timerFrame.geometricComponent.getModel2Pixel()).abs()));
  }

  public final void addButtonDubins() {
    JButton jButton = new JButton("dubins");
    jButton.setToolTipText("project control points to dubins path");
    jButton.addActionListener(actionEvent -> setControlPointsSe2(DubinsGenerator.project(control)));
    timerFrame.jToolBar.add(jButton);
  }

  /** @param control points as matrix of dimensions N x 3 */
  public final void setControlPointsSe2(Tensor control) {
    this.control = Tensor.of(control.stream() //
        .map(row -> VectorQ.requireLength(row, 3).map(Tensor::copy)));
  }

  /** @return control points as matrix of dimensions N x 3 */
  public final Tensor getControlPointsSe2() {
    return control.unmodifiable();
  }

  /** @return control points for selected {@link GeodesicDisplay} */
  public final Tensor getGeodesicControlPoints() {
    return Tensor.of(control.stream().map(geodesicDisplay()::project).map(N.DOUBLE::of));
  }

  protected final void renderControlPoints(GeometricLayer geometricLayer, Graphics2D graphics) {
    POINTS_RENDER_0.new Show(geodesicDisplay(), getControlPointShape(), getGeodesicControlPoints()).render(geometricLayer, graphics);
  }

  protected final void renderPoints( //
      GeodesicDisplay geodesicDisplay, Tensor points, //
      GeometricLayer geometricLayer, Graphics2D graphics) {
    POINTS_RENDER_1.new Show(geodesicDisplay, getControlPointShape(), points).render(geometricLayer, graphics);
  }
}
