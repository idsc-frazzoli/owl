// code by jph, gjoel
package ch.ethz.idsc.sophus.gui.win;

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
import ch.ethz.idsc.sophus.gds.GeodesicDisplayDemo;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.PointsRender;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.sophus.ref.d1.CurveSubdivision;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Insert;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** class is used in other projects outside of owl */
public abstract class ControlPointsDemo extends GeodesicDisplayDemo {
  /** mouse snaps 20 pixel to control points */
  private static final Scalar PIXEL_THRESHOLD = RealScalar.of(20.0);
  /** control points */
  protected static final PointsRender POINTS_RENDER_0 = //
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
  private boolean midpointIndicated = true;
  // ---
  private final static Color ORANGE = new Color(255, 200, 0, 192);
  private final static Color GREEN = new Color(0, 255, 0, 192);

  private class Midpoints {
    private final ManifoldDisplay geodesicDisplay = manifoldDisplay();
    private final Tensor midpoints;
    private final int index;

    public Midpoints() {
      CurveSubdivision curveSubdivision = ControlMidpoints.of(geodesicDisplay.geodesicInterface());
      midpoints = curveSubdivision.string(getGeodesicControlPoints());
      Tensor mouse_dist = Tensor.of(midpoints.stream() //
          .map(geodesicDisplay::toPoint) //
          .map(mouse.extract(0, 2)::subtract) //
          .map(Vector2Norm::of));
      ArgMinValue argMinValue = ArgMinValue.of(mouse_dist);
      index = argMinValue.index();
    }

    Tensor closestXY() {
      return geodesicDisplay.toPoint(midpoints.get(index));
    }
  }

  // ---
  private final RenderInterface renderInterface = new RenderInterface() {
    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      if (!isPositioningEnabled())
        return;
      mouse = geometricLayer.getMouseSe2State();
      if (isPositioningOngoing())
        control.set(mouse, min_index);
      else {
        ManifoldDisplay geodesicDisplay = manifoldDisplay();
        final boolean hold;
        {
          Tensor mouse_dist = Tensor.of(control.stream() //
              .map(mouse::subtract) //
              .map(Extract2D.FUNCTION) //
              .map(Vector2Norm::of));
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
        if (!hold && Tensors.nonEmpty(control) && midpointIndicated) {
          graphics.setColor(Color.RED);
          graphics.setStroke(STROKE);
          graphics.draw(geometricLayer.toLine2D(mouse, new Midpoints().closestXY()));
          graphics.setStroke(new BasicStroke());
        }
      }
    }
  };

  /** Hint: {@link #setPositioningEnabled(boolean)} controls positioning of control points
   * 
   * @param addRemoveControlPoints whether the number of control points is variable
   * @param list */
  public ControlPointsDemo(boolean addRemoveControlPoints, List<ManifoldDisplay> list) {
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
        case MouseEvent.BUTTON1:
          if (isPositioningOngoing()) {
            min_index = null; // release
            // released();
          } else {
            {
              Tensor mouse_dist = Tensor.of(control.stream() //
                  .map(mouse::subtract) //
                  .map(Extract2D.FUNCTION) //
                  .map(Vector2Norm::of));
              ArgMinValue argMinValue = ArgMinValue.of(mouse_dist);
              min_index = argMinValue.index(getPositioningThreshold()).orElse(null);
            }
            if (!isPositioningOngoing() && addRemoveControlPoints) {
              // insert
              if (control.length() < 2) {
                control = control.append(mouse);
                min_index = control.length() - 1;
              } else {
                Midpoints midpoints = new Midpoints();
                control = Insert.of(control, mouse, midpoints.index);
                min_index = midpoints.index;
              }
            }
          }
          break;
        case MouseEvent.BUTTON3: // remove point
          if (addRemoveControlPoints) {
            if (!isPositioningOngoing()) {
              Tensor mouse_dist = Tensor.of(control.stream() //
                  .map(mouse::subtract) //
                  .map(Extract2D.FUNCTION) //
                  .map(Vector2Norm::of));
              ArgMinValue argMinValue = ArgMinValue.of(mouse_dist);
              min_index = argMinValue.index(getPositioningThreshold()).orElse(null);
            }
            if (isPositioningOngoing()) {
              control = Join.of(control.extract(0, min_index), control.extract(min_index + 1, control.length()));
              min_index = null;
            }
          }
          break;
        }
      }
    };
    // ---
    timerFrame.geometricComponent.jComponent.addMouseListener(mouseAdapter);
    timerFrame.geometricComponent.jComponent.addMouseMotionListener(mouseAdapter);
    timerFrame.geometricComponent.addRenderInterface(renderInterface);
  }

  /** function is called when mouse is released */
  // public void released() {
  // API needs comments and better naming
  // }
  /** when positioning is disabled, the mouse position is not indicated graphically
   * 
   * @param enabled */
  public void setPositioningEnabled(boolean enabled) {
    if (!enabled)
      min_index = null;
    mousePositioning = enabled;
  }

  /** @return */
  public boolean isPositioningEnabled() {
    return mousePositioning;
  }

  /** @return whether user is currently dragging a control point */
  public boolean isPositioningOngoing() {
    return Objects.nonNull(min_index);
  }

  public void setMidpointIndicated(boolean enabled) {
    midpointIndicated = enabled;
  }

  public boolean isMidpointIndicated() {
    return midpointIndicated;
  }

  public Scalar getPositioningThreshold() {
    return PIXEL_THRESHOLD.divide(Sqrt.FUNCTION.apply(Abs.of(Det.of(timerFrame.geometricComponent.getModel2Pixel()))));
  }

  // TODO function should not be here!
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
    return control.unmodifiable(); // TODO should return copy!?
  }

  /** @return control points for selected {@link ManifoldDisplay} */
  public final Tensor getGeodesicControlPoints() {
    return getGeodesicControlPoints(0, Integer.MAX_VALUE);
  }

  /** @param skip
   * @param maxSize
   * @return */
  public final Tensor getGeodesicControlPoints(int skip, int maxSize) {
    return Tensor.of(control.stream() //
        .skip(skip) //
        .limit(maxSize) //
        .map(manifoldDisplay()::project) //
        .map(N.DOUBLE::of));
  }

  protected final void renderControlPoints(GeometricLayer geometricLayer, Graphics2D graphics) {
    POINTS_RENDER_0.show(manifoldDisplay()::matrixLift, getControlPointShape(), getGeodesicControlPoints()).render(geometricLayer, graphics);
  }

  protected final void renderPoints( //
      ManifoldDisplay geodesicDisplay, Tensor points, //
      GeometricLayer geometricLayer, Graphics2D graphics) {
    POINTS_RENDER_1.show(geodesicDisplay::matrixLift, getControlPointShape(), points).render(geometricLayer, graphics);
  }

  /** function exists so that shape can be altered, for instance magnified
   * 
   * @return */
  protected Tensor getControlPointShape() {
    return manifoldDisplay().shape();
  }
}
