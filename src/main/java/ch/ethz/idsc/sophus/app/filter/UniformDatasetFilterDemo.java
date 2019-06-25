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
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.lie.LieDifferences;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.Spectrogram;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;

/* package */ abstract class UniformDatasetFilterDemo extends DatasetFilterDemo {
  // TODO OB/JPH sampling freq is not generic here (because datasets may have other sampling rate)
  private static final Scalar SAMPLING_FREQUENCY = RealScalar.of(20.0);
  // ---
  // TODO JPH refactor
  protected Tensor _control = null;
  protected final SpinnerLabel<String> spinnerLabelString = new SpinnerLabel<>();
  protected final SpinnerLabel<Integer> spinnerLabelLimit = new SpinnerLabel<>();
  // protected final SpinnerLabel<ColorDataGradients> spinnerLabelCDG = new SpinnerLabel<>();

  protected UniformDatasetFilterDemo() {
    this(GeodesicDisplays.CLOTH_SE2_R2);
  }

  protected UniformDatasetFilterDemo(List<GeodesicDisplay> list) {
    super(list);
    timerFrame.geometricComponent.setModel2Pixel(StaticHelper.HANGAR_MODEL2PIXEL);
    {
      spinnerLabelString.setList(GokartPoseData.INSTANCE.list());
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
    _control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + spinnerLabelString.getValue() + ".csv").stream() //
        .limit(spinnerLabelLimit.getValue()) //
        .map(row -> row.extract(1, 4)));
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
  protected void differences_render(Graphics2D graphics, GeodesicDisplay geodesicDisplay, Tensor refined) {
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    if (Objects.nonNull(lieGroup)) {
      LieDifferences lieDifferences = new LieDifferences(lieGroup, geodesicDisplay.lieExponential());
      Tensor speeds = lieDifferences.apply(refined).multiply(SAMPLING_FREQUENCY);
      if (0 < speeds.length()) {
        int dimensions = speeds.get(0).length();
        VisualSet visualSet = new VisualSet();
        visualSet.setPlotLabel(plotLabel());
        visualSet.setAxesLabelX("sample no.");
        Tensor domain = Range.of(0, speeds.length());
        final int width = timerFrame.geometricComponent.jComponent.getWidth();
        for (int index = 0; index < dimensions; ++index) {
          Tensor signal = speeds.get(Tensor.ALL, index).unmodifiable();
          // ---
          Tensor image = Spectrogram.of(signal, ColorDataGradients.VISIBLESPECTRUM);
          BufferedImage bufferedImage = ImageFormat.of(image);
          int wid = bufferedImage.getWidth() * 5;
          int hgt = bufferedImage.getHeight() * 5;
          graphics.drawImage(bufferedImage, width - wid, index * hgt, wid, hgt, null);
          // ---
          visualSet.add(domain, signal); // .setLabel("tangent velocity [m/s]")
        }
        // visualSet.add(domain, speeds.get(Tensor.ALL, 1)).setLabel("side slip [m/s]");
        // visualSet.add(domain, speeds.get(Tensor.ALL, 2)).setLabel("rotational rate [rad/s]");
        JFreeChart jFreeChart = ListPlot.of(visualSet);
        jFreeChart.draw(graphics, new Rectangle2D.Double(0, 0, 80 + speeds.length(), 400));
        // ---
      }
    }
  }
}
