// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.planar.CurvatureComb;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.red.Norm;

public abstract class ControlPointsDemo extends GeodesicDisplayDemo {
  private static final Scalar THRESHOLD = RealScalar.of(0.2);
  /** control points */
  private static final PointsRender POINTS_RENDER_0 = //
      new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
  /** refined points */
  private static final PointsRender POINTS_RENDER_1 = //
      new PointsRender(new Color(160, 160, 160, 128 + 64), Color.BLACK);
  // ---
  private final JButton jButton = new JButton("clear");
  private final JToggleButton jToggleComb = new JToggleButton("comb");
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
        GeodesicDisplay geodesicDisplay = geodesicDisplay();
        Optional<Integer> optional = closest();
        graphics.setColor(optional.isPresent() ? Color.ORANGE : Color.GREEN);
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(geodesicDisplay.project(mouse)));
        graphics.fill(geometricLayer.toPath2D(geodesicDisplay.shape()));
        geometricLayer.popMatrix();
      }
    }
  };
  private final ActionListener actionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
      min_index = null;
      control = Tensors.of(Array.zeros(3));
    }
  };

  public ControlPointsDemo(boolean clearButton, boolean curvatureButton, List<GeodesicDisplay> list) {
    super(list);
    if (clearButton) {
      jButton.addActionListener(actionListener);
      timerFrame.jToolBar.add(jButton);
    }
    jToggleComb.setSelected(true);
    if (curvatureButton)
      timerFrame.jToolBar.add(jToggleComb);
    // ---
    timerFrame.geometricComponent.jComponent.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == 1) {
          if (Objects.isNull(min_index)) {
            min_index = closest().orElse(null);
            if (Objects.isNull(min_index)) {
              min_index = control.length();
              control.append(mouse);
            }
          } else
            min_index = null;
        }
      }
    });
    timerFrame.geometricComponent.addRenderInterface(renderInterface);
  }

  private Optional<Integer> closest() {
    Scalar cmp = THRESHOLD;
    int index = 0;
    Integer min_index = null;
    for (Tensor point : control) {
      Scalar distance = Norm._2.between(point.extract(0, 2), mouse.extract(0, 2));
      if (Scalars.lessThan(distance, cmp)) {
        cmp = distance;
        min_index = index;
      }
      ++index;
    }
    return Optional.ofNullable(min_index);
  }

  public final void addButtonDubins() {
    JButton jButton = new JButton("dubins");
    jButton.setToolTipText("project control points to dubins path");
    jButton.addActionListener(actionEvent -> setControl(DubinsGenerator.project(control)));
    timerFrame.jToolBar.add(jButton);
  }

  /** @param control points as matrix of dimensions N x 3 */
  public final void setControl(Tensor control) {
    this.control = MatrixQ.require(control);
    List<Integer> list = Dimensions.of(control);
    if (list.get(1) != 3)
      throw TensorRuntimeException.of(control);
  }

  public final Tensor control() {
    return Tensor.of(control.stream().map(geodesicDisplay()::project)).unmodifiable();
  }

  protected final void renderControlPoints(GeometricLayer geometricLayer, Graphics2D graphics) {
    POINTS_RENDER_0.new Show(geodesicDisplay(), control()).render(geometricLayer, graphics);
  }

  protected final void renderPoints(GeometricLayer geometricLayer, Graphics2D graphics, Tensor points) {
    POINTS_RENDER_1.new Show(geodesicDisplay(), points).render(geometricLayer, graphics);
  }

  private static final Color COLOR_CURVATURE_COMB = new Color(0, 0, 0, 128);
  private static final Scalar COMB_SCALE = DoubleScalar.of(1); // .5 (1 for presentation)
  // ---
  private final PathRender pathRenderCurve = new PathRender(Color.BLUE, 1.25f);
  private final PathRender pathRenderCurvature = new PathRender(COLOR_CURVATURE_COMB);

  protected final void renderCurve(Tensor refined, boolean isCyclic, GeometricLayer geometricLayer, Graphics2D graphics) {
    if (0 < refined.length())
      if (Unprotect.dimension1(refined) != 2)
        throw TensorRuntimeException.of(refined);
    pathRenderCurve.setCurve(refined, isCyclic).render(geometricLayer, graphics);
    if (jToggleComb.isSelected())
      pathRenderCurvature.setCurve(CurvatureComb.of(refined, COMB_SCALE, isCyclic), isCyclic).render(geometricLayer, graphics);
  }
}
