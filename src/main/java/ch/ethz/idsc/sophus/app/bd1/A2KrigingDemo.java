// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.LinearColorDataGradient;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ abstract class A2KrigingDemo extends AnKrigingDemo {
  private final SpinnerLabel<Scalar> spinnerCvar = new SpinnerLabel<>();
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = SpinnerLabel.of(ColorDataGradients.values());
  private final SpinnerLabel<Integer> spinnerRes = new SpinnerLabel<>();
  private final JToggleButton jToggleVarian = new JToggleButton("est/var");
  private final JToggleButton jToggleButton = new JToggleButton("thres");
  private final JButton jButtonPrint = new JButton("print");
  private final JButton jButtonExport = new JButton("export");

  public A2KrigingDemo(List<GeodesicDisplay> geodesicDisplays) {
    super(geodesicDisplays);
    {
      spinnerCvar.setList(Tensors.fromString("{0, 0.01, 0.1, 0.5, 1}").stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerCvar.setIndex(0);
      spinnerCvar.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "error");
    }
    {
      spinnerColorData.setValue(ColorDataGradients.PARULA);
      spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "color scheme");
    }
    {
      spinnerRes.setArray(20, 30, 50, 75, 100, 150, 200, 250);
      spinnerRes.setValue(30);
      spinnerRes.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "resolution");
    }
    {
      timerFrame.jToolBar.add(jToggleVarian);
      timerFrame.jToolBar.add(jToggleButton);
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
    {
      jButtonPrint.addActionListener(l -> System.out.println(getControlPointsSe2()));
      timerFrame.jToolBar.add(jButtonPrint);
    }
    {
      jButtonExport.addActionListener(e -> export());
      timerFrame.jToolBar.add(jButtonExport);
    }
    // timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override
  public final void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    prepare();
    // ---
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor sequence = getGeodesicControlPoints();
    Tensor values = getControlPointsSe2().get(Tensor.ALL, 2);
    try {
      Tensor matrix = matrix(spinnerRes.getValue(), sequence, values);
      BufferedImage bufferedImage = bufferedImage(matrix);
      RenderQuality.setDefault(graphics);
      Tensor pixel2model = geodesicDisplay.geodesicArrayPlot().pixel2model(new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight()));
      ImageRender.of(bufferedImage, pixel2model).render(geometricLayer, graphics);
    } catch (Exception exception) {
      System.out.println(exception);
      exception.printStackTrace();
    }
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    LeversRender leversRender = new LeversRender(geodesicDisplay, sequence, null, values, geometricLayer, graphics);
    leversRender.renderWeights();
  }

  private Tensor matrix(int resolution, Tensor sequence, Tensor values) {
    TensorScalarFunction tsf = function(sequence, values);
    Scalar[][] array = geodesicDisplay().geodesicArrayPlot().array(resolution, tsf.andThen(Clips.unit()));
    Tensor matrix = Tensors.matrix(array);
    // ---
    if (jToggleButton.isSelected())
      matrix = matrix.map(Round.FUNCTION); // effectively maps to 0 or 1
    return matrix;
  }

  private BufferedImage bufferedImage(Tensor matrix) {
    Tensor tensor = ResourceData.of("/colorscheme/" + spinnerColorData.getValue().toString().toLowerCase() + ".csv");
    ScalarUnaryOperator suo = s -> s.multiply(RationalScalar.HALF).add(RealScalar.of(127));
    tensor.set(suo, Tensor.ALL, 0);
    tensor.set(suo, Tensor.ALL, 1);
    tensor.set(suo, Tensor.ALL, 2);
    ColorDataGradient colorDataGradient = LinearColorDataGradient.of(tensor);
    Tensor colorData = matrix.map(colorDataGradient);
    return ImageFormat.of(colorData);
  }

  private void export() {
    // Tensor sequence = getGeodesicControlPoints();
    // Tensor values = getControlPointsSe2().get(Tensor.ALL, 2);
    // File folder = HomeDirectory.Pictures(getClass().getSimpleName(), spinnerColorData.getValue().toString());
    // folder.mkdirs();
    // System.out.println("exporting");
    // int index = 0;
    // ScalarUnaryOperator variogram = variogram();
    // for (HsScalarFunction hsScalarFunction : spinnerKriging.getList()) {
    // String format = String.format("%02d%s.png", index, hsScalarFunction);
    // System.out.println(format);
    // Tensor matrix = matrix( //
    // 256, //
    // geodesicDisplay().vectorLogManifold(), //
    // hsScalarFunction, //
    // variogram, //
    // sequence, values);
    // ArrayPlotRender arrayPlotRender = ArrayPlotRender.uniform(matrix, spinnerColorData.getValue(), 1);
    // BufferedImage bufferedImage = arrayPlotRender.export();
    // GeometricLayer geometricLayer = GeometricLayer.of(Inverse.of(geodesicDisplay().geodesicArrayPlot().pixel2model(arrayPlotRender.getDimension())));
    // Graphics2D graphics = bufferedImage.createGraphics();
    // RenderQuality.setQuality(graphics);
    // renderControlPoints(geometricLayer, graphics);
    // {
    // graphics.setColor(Color.WHITE);
    // graphics.drawString(hsScalarFunction.toString(), 0, 10);
    // }
    // try {
    // ImageIO.write(bufferedImage, "png", new File(folder, format));
    // } catch (Exception exception) {
    // exception.printStackTrace();
    // }
    // }
  }

  void prepare() {
    // ---
  }
}
