// code by jph, gjoel
package ch.ethz.idsc.sophus.app.api;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JButton;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Insert;
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
  private static final Stroke STROKE = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  // ---
  private Tensor control = Tensors.empty();
  private Tensor mouse = Array.zeros(3);
  /** min_index is non-null while the user drags a control points */
  private Integer min_index = null;
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
        final boolean hold;
        {
          Tensor mouse_dist = Tensor.of(control.stream().map(mouse::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector));
          ArgMinValue argMinValue = ArgMinValue.of(mouse_dist);
          Optional<Scalar> value = argMinValue.value(getPositioningThreshold());
          hold = value.isPresent() && isPositioningEnabled();
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
        }
        if (!hold && Tensors.nonEmpty(control)) {
          CurveSubdivision curveSubdivision = ControlMidpoints.of(geodesicDisplay.geodesicInterface());
          Tensor midpoints = curveSubdivision.string(control);
          // graphics.setColor(new Color(128, 128, 128, 32));
          // Tensor shape = getControlPointShape().multiply(RealScalar.of(.5));
          // for (Tensor midpoint : midpoints) {
          // geometricLayer.pushMatrix(geodesicDisplay.matrixLift(geodesicDisplay.project(midpoint)));
          // graphics.fill(geometricLayer.toPath2D(shape));
          // geometricLayer.popMatrix();
          // }
          Tensor mouse_dist = Tensor.of(midpoints.stream().map(mouse::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector));
          ArgMinValue argMinValue = ArgMinValue.of(mouse_dist);
          int pos_index = argMinValue.index();
          graphics.setColor(Color.RED);
          graphics.setStroke(STROKE);
          graphics.draw(geometricLayer.toLine2D(mouse, midpoints.get(pos_index)));
          graphics.setStroke(new BasicStroke());
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
      public void mousePressed(MouseEvent mouseEvent) {
        if (!isPositioningEnabled())
          return;
        switch (mouseEvent.getButton()) {
        case MouseEvent.BUTTON1: // insert point
          if (Objects.isNull(min_index)) {
            {
              Tensor mouse_dist = Tensor.of(control.stream().map(mouse::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector));
              ArgMinValue argMinValue = ArgMinValue.of(mouse_dist);
              min_index = argMinValue.index(getPositioningThreshold()).orElse(null);
            }
            if (Objects.isNull(min_index) && addRemoveControlPoints) {
              // insert
              if (control.length() < 2) {
                control = control.append(mouse);
                min_index = control.length() - 1;
              } else {
                CurveSubdivision curveSubdivision = ControlMidpoints.of(geodesicDisplay().geodesicInterface());
                Tensor midpoints = curveSubdivision.string(control);
                Tensor mouse_dist = Tensor.of(midpoints.stream().map(mouse::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector));
                ArgMinValue argMinValue = ArgMinValue.of(mouse_dist);
                int pos_index = argMinValue.index();
                control = Insert.of(control, mouse, pos_index);
                min_index = argMinValue.index();
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
    timerFrame.geometricComponent.jComponent.addMouseListener(mouseAdapter);
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
