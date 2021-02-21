// code by ob, jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.io.GokartPoseData;
import ch.ethz.idsc.sophus.app.io.GokartPoseDatas;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.lie.LieDifferences;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.fft.Spectrogram;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.win.WindowFunctions;

/* package */ abstract class AbstractSpectrogramDemo extends AbstractDatasetFilterDemo {
  private static final ScalarUnaryOperator MAGNITUDE_PER_SECONDS = QuantityMagnitude.SI().in("s^-1");
  // ---
  private final GokartPoseData gokartPoseData;
  protected final SpinnerLabel<String> spinnerLabelString = new SpinnerLabel<>();
  protected final SpinnerLabel<Integer> spinnerLabelLimit = new SpinnerLabel<>();
  protected final SpinnerLabel<WindowFunctions> spinnerKernel = new SpinnerLabel<>();
  // TODO JPH refactor
  protected Tensor _control = null;
  // protected final SpinnerLabel<ColorDataGradients> spinnerLabelCDG = new SpinnerLabel<>();

  protected AbstractSpectrogramDemo(GokartPoseData gokartPoseData) {
    this(GeodesicDisplays.CL_SE2_R2, gokartPoseData);
  }

  protected AbstractSpectrogramDemo(List<ManifoldDisplay> list, GokartPoseData gokartPoseData) {
    super(list);
    this.gokartPoseData = gokartPoseData;
    timerFrame.geometricComponent.setModel2Pixel(GokartPoseDatas.HANGAR_MODEL2PIXEL);
    {
      spinnerLabelString.setList(gokartPoseData.list());
      spinnerLabelString.addSpinnerListener(type -> updateState());
      spinnerLabelString.setIndex(0);
      spinnerLabelString.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "data");
    }
    {
      spinnerLabelLimit.setList(Arrays.asList(10, 20, 50, 100, 250, 500, 1000, 2000, 5000));
      spinnerLabelLimit.setIndex(4);
      spinnerLabelLimit.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "limit");
      spinnerLabelLimit.addSpinnerListener(type -> updateState());
    }
    {
      spinnerKernel.setList(Arrays.asList(WindowFunctions.values()));
      spinnerKernel.setValue(WindowFunctions.GAUSSIAN);
      spinnerKernel.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "smoothing kernel");
      spinnerKernel.addSpinnerListener(value -> updateState());
    }
  }

  protected void updateState() {
    int limit = spinnerLabelLimit.getValue();
    String name = spinnerLabelString.getValue();
    _control = gokartPoseData.getPose(name, limit);
  }

  @Override
  protected final Tensor control() {
    return Tensor.of(_control.stream().map(manifoldDisplay()::project)).unmodifiable();
  }

  /** @return */
  protected abstract String plotLabel();

  private static final ColorDataGradient COLOR_DATA_GRADIENT = //
      ColorDataGradients.VISIBLESPECTRUM.deriveWithOpacity(RealScalar.of(0.75));
  private static final int MAGNIFY = 4;

  @Override
  protected void differences_render( //
      Graphics2D graphics, ManifoldDisplay geodesicDisplay, Tensor refined, boolean spectrogram) {
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    if (Objects.nonNull(lieGroup)) {
      LieDifferences lieDifferences = new LieDifferences(geodesicDisplay.lieExponential());
      Scalar sampleRate = MAGNITUDE_PER_SECONDS.apply(gokartPoseData.getSampleRate());
      Tensor speeds = lieDifferences.apply(refined).multiply(sampleRate);
      if (0 < speeds.length()) {
        int dimensions = speeds.get(0).length();
        VisualSet visualSet = new VisualSet();
        visualSet.setPlotLabel(plotLabel());
        visualSet.setAxesLabelX("sample no.");
        Tensor domain = Range.of(0, speeds.length());
        final int width = timerFrame.geometricComponent.jComponent.getWidth();
        int offset_y = 0;
        for (int index = 0; index < dimensions; ++index) {
          Tensor signal = speeds.get(Tensor.ALL, index).unmodifiable();
          visualSet.add(domain, signal);
          // ---
          if (spectrogram) {
            ScalarUnaryOperator window = spinnerKernel.getValue().get();
            Tensor image = Spectrogram.of(signal, window, COLOR_DATA_GRADIENT);
            BufferedImage bufferedImage = ImageFormat.of(image);
            int wid = bufferedImage.getWidth() * MAGNIFY;
            int hgt = bufferedImage.getHeight() * MAGNIFY;
            graphics.drawImage(bufferedImage, width - wid, offset_y, wid, hgt, null);
            offset_y += hgt + MAGNIFY;
          }
        }
        JFreeChart jFreeChart = ListPlot.of(visualSet);
        jFreeChart.draw(graphics, new Rectangle2D.Double(0, 0, 80 + speeds.length(), 400));
      }
    }
  }
}
