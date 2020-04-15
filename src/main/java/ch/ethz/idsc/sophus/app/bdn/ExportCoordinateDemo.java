// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogMetricWeighting;
import ch.ethz.idsc.sophus.app.api.LogMetricWeightings;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ abstract class ExportCoordinateDemo extends ScatteredSetCoordinateDemo implements ActionListener {
  private final JButton jButtonExport = new JButton("export");

  public ExportCoordinateDemo( //
      boolean addRemoveControlPoints, //
      List<GeodesicDisplay> list, //
      List<LogMetricWeighting> array) {
    super(addRemoveControlPoints, list, array);
    {
      jButtonExport.addActionListener(this);
      timerFrame.jToolBar.add(jButtonExport);
    }
  }

  @Override
  public final void actionPerformed(ActionEvent actionEvent) {
    File root = HomeDirectory.Pictures(getClass().getSimpleName(), geodesicDisplay().toString());
    root.mkdirs();
    for (LogMetricWeighting logMetricWeighting : LogMetricWeightings.barycentric()) {
      WeightingInterface weightingInterface = logMetricWeighting.from(geodesicDisplay().flattenLogManifold(), geodesicDisplay().parametricDistance());
      System.out.print("computing...");
      Tensor wgs = compute(weightingInterface, 120);
      List<Integer> dims = Dimensions.of(wgs);
      Tensor _wgp = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
      ArrayPlotRender arrayPlotRender = new ArrayPlotRender(_wgp, colorDataGradient(), 0, 0, 1);
      BufferedImage bufferedImage = arrayPlotRender.export();
      try {
        ImageIO.write(bufferedImage, "png", new File(root, logMetricWeighting.toString() + ".png"));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      System.out.println("done");
    }
  }

  abstract Tensor compute(WeightingInterface weightingInterface, int i);
}
