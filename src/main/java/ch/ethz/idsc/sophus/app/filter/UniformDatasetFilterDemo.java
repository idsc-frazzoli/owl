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

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.GokartPoseData;
import ch.ethz.idsc.sophus.app.api.GokartPoseDatas;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.lie.LieDifferences;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.util.plot.ListPlot;
import ch.ethz.idsc.sophus.util.plot.VisualSet;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.Spectrogram;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ abstract class UniformDatasetFilterDemo extends DatasetFilterDemo {
  private static final ScalarUnaryOperator SAMPLE_RATE = QuantityMagnitude.SI().in("s^-1");
  // ---
  private final GokartPoseData gokartPoseData;
  protected final SpinnerLabel<String> spinnerLabelString = new SpinnerLabel<>();
  protected final SpinnerLabel<Integer> spinnerLabelLimit = new SpinnerLabel<>();
  // TODO JPH refactor
  protected Tensor _control = null;
  // protected final SpinnerLabel<ColorDataGradients> spinnerLabelCDG = new SpinnerLabel<>();

  protected UniformDatasetFilterDemo(GokartPoseData gokartPoseData) {
    this(GeodesicDisplays.CLOTH_SE2_R2, gokartPoseData);
  }

  protected UniformDatasetFilterDemo(List<GeodesicDisplay> list, GokartPoseData gokartPoseData) {
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
      // spinnerLabelCDG.setArray(ColorDataGradients.values());
      // spinnerLabelCDG.setIndex(0);
      // spinnerLabelCDG.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "limit");
    }
  }

  protected void updateState() {
    int limit = spinnerLabelLimit.getValue();
    String name = spinnerLabelString.getValue();
    _control = gokartPoseData.getPose(name, limit);
    // Make uniform data artificially non-uniform by randomly leaving out elements
    // _control = DeuniformData.of(_control, RealScalar.of(0.2));
    // _control = DuckietownData.states(DuckietownData.POSE_20190325_0);
  }

  @Override
  protected final Tensor control() {
    return Tensor.of(_control.stream().map(geodesicDisplay()::project)).unmodifiable();
  }

  /** @return */
  protected abstract String plotLabel();

  @Override
  protected void differences_render( //
      Graphics2D graphics, GeodesicDisplay geodesicDisplay, Tensor refined, boolean spectrogram) {
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    if (Objects.nonNull(lieGroup)) {
      LieDifferences lieDifferences = new LieDifferences(lieGroup, geodesicDisplay.lieExponential());
      Scalar sampleRate = SAMPLE_RATE.apply(gokartPoseData.getSampleRate());
      Tensor speeds = lieDifferences.apply(refined).multiply(sampleRate);
      if (0 < speeds.length()) {
        int dimensions = speeds.get(0).length();
        VisualSet visualSet = new VisualSet();
        visualSet.setPlotLabel(plotLabel());
        visualSet.setAxesLabelX("sample no.");
        Tensor domain = Range.of(0, speeds.length());
        final int width = timerFrame.geometricComponent.jComponent.getWidth();
        for (int index = 0; index < dimensions; ++index) {
          Tensor signal = speeds.get(Tensor.ALL, index).unmodifiable();
          visualSet.add(domain, signal);
          // ---
          if (spectrogram) {
            Tensor image = Spectrogram.of(signal, ColorDataGradients.VISIBLESPECTRUM);
            BufferedImage bufferedImage = ImageFormat.of(image);
            int wid = bufferedImage.getWidth() * 5;
            int hgt = bufferedImage.getHeight() * 5;
            graphics.drawImage(bufferedImage, width - wid, index * hgt, wid, hgt, null);
          }
        }
        JFreeChart jFreeChart = ListPlot.of(visualSet);
        jFreeChart.draw(graphics, new Rectangle2D.Double(0, 0, 80 + speeds.length(), 400));
      }
    }
  }
}
