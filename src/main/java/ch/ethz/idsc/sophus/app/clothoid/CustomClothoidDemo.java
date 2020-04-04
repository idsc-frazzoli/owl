// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidContext;
import ch.ethz.idsc.sophus.crv.clothoid.MidpointTangentOrder2;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class CustomClothoidDemo extends ControlPointsDemo implements ChangeListener {
  private static final Tensor CONFIG = Tensors.fromString("{{0, 0}, {5, 0}}");
  private static final PathRender PATH_RENDER = new PathRender(new Color(0, 0, 255, 128), 2f);
  private static final Tensor LAMBDAS = Subdivide.of(-20.0, 20.0, 1001);
  // ---
  private final JSlider jSlider = new JSlider(0, LAMBDAS.length() - 1, LAMBDAS.length() / 2);
  private final JTextField jTextField = new JTextField(10);
  private final JLabel jLabel = new JLabel();
  private final SpinnerLabel<Integer> spinnerSolution = SpinnerLabel.of(0, 1, 2, 3, 4, 5, 6, 7, 8);
  // ---
  private ClothoidDefectContainer clothoidDefectContainer = null;

  public CustomClothoidDemo() {
    super(false, GeodesicDisplays.SE2C_ONLY);
    {
      jTextField.setPreferredSize(new Dimension(100, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    {
      jSlider.addChangeListener(this);
      jSlider.setPreferredSize(new Dimension(600, 28));
      timerFrame.jToolBar.add(jSlider);
    }
    {
      JButton jButton = new JButton("fit");
      jButton.addActionListener(e -> {
        ClothoidContext clothoidContext = clothoidDefectContainer.clothoidContext;
        Scalar lambda = MidpointTangentOrder2.INSTANCE.apply(clothoidContext.s1(), clothoidContext.s2());
        setLambda(lambda);
      });
      timerFrame.jToolBar.add(jButton);
    }
    spinnerSolution.addSpinnerListener(index -> {
      Optional<Scalar> optional = clothoidDefectContainer.getSolution(spinnerSolution.getIndex());
      if (optional.isPresent())
        setLambda(optional.get());
    });
    spinnerSolution.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "sol. index");
    {
      timerFrame.jToolBar.add(jLabel);
    }
    // ---
    stateChanged(null);
    setControlPointsSe2(Array.zeros(2, 3));
    timerFrame.configCoordinateOffset(300, 700);
    validateContainer();
  }

  private boolean validateContainer() {
    {
      Tensor control = getControlPointsSe2().copy();
      control.set(CONFIG.get(Tensor.ALL, 0), Tensor.ALL, 0);
      control.set(CONFIG.get(Tensor.ALL, 1), Tensor.ALL, 1);
      setControlPointsSe2(control);
    }
    // ---
    Tensor p = getControlPointsSe2().get(0);
    Tensor q = getControlPointsSe2().get(1);
    ClothoidContext clothoidContext = new ClothoidContext(p, q);
    if (Objects.isNull(clothoidDefectContainer) || !clothoidDefectContainer.encodes(clothoidContext)) {
      clothoidDefectContainer = new ClothoidDefectContainer(clothoidContext);
      jLabel.setText("s1=" + clothoidContext.s1().map(Round._4) + " s2=" + clothoidContext.s2().map(Round._4));
      return true;
    }
    return false;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    boolean validated = validateContainer();
    if (validated) {
      graphics.setColor(Color.BLACK);
      graphics.drawString("validated", 0, 30);
    }
    // ---
    ClothoidContext clothoidContext = clothoidDefectContainer.clothoidContext;
    Scalar lambda = LAMBDAS.Get(jSlider.getValue());
    try {
      jTextField.setBackground(new Color(128 + 64, 255, 128 + 64));
      lambda = Scalars.fromString(jTextField.getText());
      if (!(lambda instanceof RealScalar))
        throw new IllegalArgumentException();
    } catch (Exception exception) {
      jTextField.setBackground(new Color(255, 128 + 64, 128 + 64));
    }
    for (Tensor _lambda : clothoidDefectContainer.solutions) {
      Tensor points = ClothoidSampler.of(CustomClothoids.of((Scalar) _lambda, clothoidContext.p, clothoidContext.q));
      new PathRender(new Color(64, 255, 64, 64)).setCurve(points, false).render(geometricLayer, graphics);
    }
    {
      Tensor points = ClothoidSampler.of(CustomClothoids.of(lambda, clothoidContext.p, clothoidContext.q));
      PATH_RENDER.setCurve(points, false).render(geometricLayer, graphics);
    }
    // ---
    renderControlPoints(geometricLayer, graphics);
    {
      Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
      GeometricLayer plotLayer = GeometricLayer.of(Tensors.matrix(new Number[][] { //
          { 20, 0, dimension.width / 2 }, //
          { 0, -20, 100 }, //
          { 0, 0, 1 } }));
      GridRender gridRender = new GridRender(Subdivide.of(-20, 20, 10), Subdivide.of(-3, 3, 6));
      gridRender.render(plotLayer, graphics);
      clothoidDefectContainer.render(plotLayer, graphics);
      graphics.setColor(Color.RED);
      graphics.draw(plotLayer.toLine2D(Tensors.of(lambda, RealScalar.ONE), Tensors.of(lambda, RealScalar.ONE.negate())));
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    setLambda(LAMBDAS.Get(jSlider.getValue()));
  }

  public void setLambda(Scalar lambda) {
    jTextField.setText(lambda.map(Round._6).toString());
  }

  public static void main(String[] args) {
    new CustomClothoidDemo().setVisible(1200, 900);
  }
}
