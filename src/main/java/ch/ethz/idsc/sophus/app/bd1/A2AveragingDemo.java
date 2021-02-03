// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.sophus.gds.GeodesicArrayPlot;
import ch.ethz.idsc.sophus.gds.GeodesicDisplay;
import ch.ethz.idsc.sophus.gui.ren.ArrayPlotRender;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.ext.Cache;
import ch.ethz.idsc.tensor.ext.Timing;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ abstract class A2AveragingDemo extends AnAveragingDemo {
  private final SpinnerLabel<Scalar> spinnerCvar = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerMagnif = new SpinnerLabel<>();
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = SpinnerLabel.of(ColorDataGradients.values());
  private final SpinnerLabel<Integer> spinnerRes = new SpinnerLabel<>();
  private final JToggleButton jToggleVarian = new JToggleButton("est/var");
  private final JToggleButton jToggleThresh = new JToggleButton("thresh");

  public A2AveragingDemo(List<GeodesicDisplay> geodesicDisplays) {
    super(geodesicDisplays);
    {
      spinnerCvar.setList(Tensors.fromString("{0, 0.01, 0.1, 0.5, 1}").stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerCvar.setIndex(0);
      spinnerCvar.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "error");
    }
    {
      spinnerMagnif.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
      spinnerMagnif.setValue(6);
      spinnerMagnif.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "magnify");
      spinnerMagnif.addSpinnerListener(v -> recompute());
    }
    {
      spinnerColorData.setValue(ColorDataGradients.PARULA);
      spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "color scheme");
      spinnerColorData.addSpinnerListener(v -> recompute());
    }
    {
      spinnerRes.setArray(20, 30, 50, 75, 100, 150, 200, 250);
      spinnerRes.setValue(30);
      spinnerRes.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "resolution");
      spinnerRes.addSpinnerListener(v -> recompute());
    }
    {
      timerFrame.jToolBar.add(jToggleVarian);
      timerFrame.jToolBar.add(jToggleThresh);
    }
    {
      JButton jButton = new JButton("round");
      jButton.addActionListener(e -> {
        Tensor tensor = getControlPointsSe2().copy();
        tensor.set(Round.FUNCTION, Tensor.ALL, 2);
        setControlPointsSe2(tensor);
      });
      timerFrame.jToolBar.add(jButton);
    }
    timerFrame.jToolBar.addSeparator();
    addSpinnerListener(v -> recompute());
  }

  private final Cache<Tensor, BufferedImage> cache = Cache.of(this::computeImage, 1);

  @Override
  protected final void recompute() {
    System.out.println("clear");
    cache.clear();
  }

  private final BufferedImage computeImage(Tensor tensor) {
    System.out.print("computeImage ");
    Tensor sequence = tensor.get(0);
    Tensor values = tensor.get(1);
    int resolution = spinnerRes.getValue();
    try {
      TensorScalarFunction tensorScalarFunction = function(sequence, values);
      GeodesicArrayPlot geodesicArrayPlot = geodesicDisplay().geodesicArrayPlot();
      Timing timing = Timing.started();
      Tensor matrix = geodesicArrayPlot.raster(resolution, tensorScalarFunction, DoubleScalar.INDETERMINATE);
      System.out.println(timing.seconds());
      // ---
      if (jToggleThresh.isSelected())
        matrix = matrix.map(Round.FUNCTION); // effectively maps to 0 or 1
      // ---
      ColorDataGradient colorDataGradient = spinnerColorData.getValue();
      return ArrayPlotRender.rescale(matrix, colorDataGradient, spinnerMagnif.getValue()).export();
    } catch (Exception exception) {
      System.out.println(exception);
      exception.printStackTrace();
    }
    return null;
  }

  @Override
  public final void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    prepare();
    // ---
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor sequence = getGeodesicControlPoints();
    Tensor values = getControlPointsSe2().get(Tensor.ALL, 2);
    BufferedImage bufferedImage = cache.apply(Unprotect.byRef(sequence, values)
    // .map(Round._3)
    );
    // if (Objects.isNull(bufferedImage))
    // recompute();
    if (Objects.nonNull(bufferedImage)) {
      RenderQuality.setDefault(graphics); // default so that raster becomes visible
      Tensor pixel2model = geodesicDisplay.geodesicArrayPlot().pixel2model(new Dimension(bufferedImage.getHeight(), bufferedImage.getHeight()));
      ImageRender.of(bufferedImage, pixel2model).render(geometricLayer, graphics);
    }
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    LeversRender leversRender = //
        LeversRender.of(geodesicDisplay, sequence, values, geometricLayer, graphics);
    leversRender.renderWeights(values);
  }

  void prepare() {
    // ---
  }
}
