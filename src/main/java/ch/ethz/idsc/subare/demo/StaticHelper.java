// code by fluric
package ch.ethz.idsc.subare.demo;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.subare.analysis.DiscreteModelErrorAnalysis;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualRow;
import ch.ethz.idsc.tensor.fig.VisualSet;

/* package */ enum StaticHelper {
  ;
  private static final int WIDTH = 1280;
  private static final int HEIGHT = 720;

  private static File directory() {
    File directory = HomeDirectory.Pictures("plots");
    directory.mkdir();
    return directory;
  }

  public static void createPlot(Map<String, Tensor> map, String path, List<DiscreteModelErrorAnalysis> errorAnalysisList) {
    for (int index = 0; index < errorAnalysisList.size(); ++index) {
      VisualSet visualSet = StaticHelper.create(map, index);
      // return a new chart containing the overlaid plot...
      String subPath = path + "_" + errorAnalysisList.get(index).name().toLowerCase();
      JFreeChart jFreeChart = plot(subPath, subPath, "Number batches", "Error", visualSet);
      try {
        savePlot(directory(), path, jFreeChart);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  private static VisualSet create(Map<String, Tensor> map, int index) {
    VisualSet visualSet = new VisualSet();
    for (Entry<String, Tensor> entry : map.entrySet()) {
      VisualRow visualRow = visualSet.add(entry.getValue());
      visualRow.setLabel(entry.getKey());
    }
    return visualSet;
  }

  private static JFreeChart plot( //
      String filename, String diagramTitle, String axisLabelX, String axisLabelY, VisualSet visualSet) {
    visualSet.setPlotLabel(diagramTitle);
    visualSet.setAxesLabelX(axisLabelX);
    visualSet.setAxesLabelY(axisLabelY);
    return ListPlot.of(visualSet);
  }

  private static File savePlot(File directory, String fileTitle, JFreeChart jFreeChart) throws Exception {
    File file = new File(directory, fileTitle + ".png");
    ChartUtils.saveChartAsPNG(file, jFreeChart, WIDTH, HEIGHT);
    System.out.println("Exported " + fileTitle + ".png to " + directory);
    return file;
  }
}
