// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import ch.ethz.idsc.sophus.app.io.GokartPoseDataV1;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.subare.util.HtmlUtf8;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ResourceData;

/* package */ enum GeodesicFilterEvaluation {
  ;
  public static Tensor process(String data) throws IOException {
    ParameterMinimizer parameterMinimizer = new ParameterMinimizer();
    Tensor minimizer = parameterMinimizer.minimizer(data);
    GeodesicCurveEvaluation geodesicCurveEvaluation = new GeodesicCurveEvaluation(minimizer, data);
    geodesicCurveEvaluation.windowSizeCurves();
    geodesicCurveEvaluation.alphaCurves();
    return minimizer;
  }

  private static Scalar time(String data) {
    Tensor time = Tensor.of(ResourceData.of("/dubilab/app/pose/" + data + ".csv").stream() //
        .map(row -> row.extract(0, 1)));
    Scalar duration = time.Get(time.length() - 1, 0).subtract(time.Get(0, 0));
    return duration;
  }

  private static Scalar length(String data) {
    Scalar length = RealScalar.of(Tensor.of(ResourceData.of("/dubilab/app/pose/" + data + ".csv").stream() //
        .map(row -> row.extract(0, 1))).length());
    return length;
  }

  public static void htmlWriter(String data, Tensor minimizer) throws IOException {
    try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(HomeDirectory.Pictures(data.replace('/', '_') + ".html"))) {
      Tensor minimizingAlphas = minimizer.get(0);
      Tensor minimizingWindowSizes = minimizer.get(1);
      Tensor minimizingKernels = minimizer.get(2);
      Tensor minimizingErrors = minimizer.get(3).divide(length(data));
      htmlUtf8.appendln("<tr><th>Suggested Filter parameters for dataset: " + data + "</th></th>");
      htmlUtf8.appendln("<table>");
      htmlUtf8.appendln(
          "<tr><td width = 200>Minimizers: </td><td width = 200>alpha</td><td width = 200>WindowSize</td><td width = 200>Kernel</td><td width = 200>Resulting Error</td><td width = 200>Unit</td></tr>");
      htmlUtf8.appendln("<tr><td width = 200>Pose</td><td width = 200>" + minimizingAlphas.Get(0) + "</td><td width = 200>" + minimizingWindowSizes.Get(0)
          + "</td><td width = 200>" + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(0))] + "</td><td width = 200>"
          + minimizingErrors.Get(0) + "</td><td width = 200>[m]</td></tr>");
      htmlUtf8.appendln("<tr><td width = 200>Orientation</td><td width = 200>" + minimizingAlphas.Get(1) + "</td><td width = 200>"
          + minimizingWindowSizes.Get(1) + "</td><td width = 200>" + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(1))]
          + "</td><td width = 200>" + minimizingErrors.Get(1) + "</td><td width = 200>[rad]</td></tr>");
      htmlUtf8.appendln("<tr><td width = 200>Pose change</td><td width = 200>" + minimizingAlphas.Get(2) + "</td><td width = 200>"
          + minimizingWindowSizes.Get(2) + "</td><td width = 200>" + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(2))]
          + "</td><td width = 200>" + minimizingErrors.Get(2).divide(time(data)) + "</td><td width = 200>[m/s]</td></tr>");
      htmlUtf8.appendln("<tr><td width = 200>Orientation change</td><td width = 200>" + minimizingAlphas.Get(3) + "</td><td width = 200>"
          + minimizingWindowSizes.Get(3) + "</td><td width = 200>" + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(3))]
          + "</td><td width = 200>" + minimizingErrors.Get(3).divide(time(data)) + "</td><td width = 200>[rad/s]</td></tr>");
      htmlUtf8.appendln("</table>");
      htmlUtf8.appendln("<p>resulting errors refer to average error per measurement</p>");
      String name = "GeodesicFilterEvaluation_";
      String win = "windowSizeCurves";
      String alpha = "alphaCurves";
      htmlUtf8.appendln("<p><b>Error as a function of alpha:</b></p>");
      htmlUtf8.appendln("<p>Pose:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + alpha + "_x.png' />");
      htmlUtf8.appendln("<p>Orientation:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + alpha + "_a.png' />");
      htmlUtf8.appendln("<p>Pose change:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + alpha + "_xdot.png' />");
      htmlUtf8.appendln("<p>Orientation change:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + alpha + "_adot.png' />");
      htmlUtf8.appendln("<p><b>Error as a function of WindowSize:</b></p>");
      htmlUtf8.appendln("<p>Pose:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + win + "_x.png' />");
      htmlUtf8.appendln("<p>Orientation:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + win + "_a.png' />");
      htmlUtf8.appendln("<p>Pose change:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + win + "_xdot.png' />");
      htmlUtf8.appendln("<p>Orientation change:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + win + "_adot.png' />");
      FrequencyResponsePlot frequencyResponsePlot = new FrequencyResponsePlot(minimizer);
      frequencyResponsePlot.evaluate();
      htmlUtf8.appendln("<p><b>Frequency Responses: </b></p>");
      // change name of frequency responses s.t. they are not overwritten each time
      htmlUtf8.appendln("<img src='FrequencyResponsePlot_MagnitudeResponse_Test.png' />");
      htmlUtf8.appendln("<img src='FrequencyResponsePlot_PhaseResponse_Test.png' />");
    }
  }

  public static void htmlComparison(String data, Tensor minimizer, HtmlUtf8 htmlUtf8) {
    Tensor minimizingAlphas = minimizer.get(0);
    Tensor minimizingWindowSizes = minimizer.get(1);
    Tensor minimizingKernels = minimizer.get(2);
    Tensor minimizingErrors = minimizer.get(3).divide(length(data));
    htmlUtf8.appendln("<p><b>Suggested Filter parameters for dataset: " + data + "</b></p>");
    htmlUtf8.appendln("<table>");
    htmlUtf8.appendln(
        "<tr><td width = 200>Minimizers: </td><td width = 200>alpha</td><td width = 200>WindowSize</td><td width = 200>Kernel</td><td width = 200>Resulting Error</td><td width = 200>Unit</td></tr>");
    htmlUtf8.appendln("<tr><td width = 200>Pose</td><td width = 200>" + minimizingAlphas.Get(0) + "</td><td width = 200>" + minimizingWindowSizes.Get(0)
        + "</td><td width = 200>" + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(0))] + "</td><td width = 200>"
        + minimizingErrors.Get(0) + "</td><td width = 200>[m]</td></tr>");
    htmlUtf8.appendln("<tr><td width = 200>Orientation</td><td width = 200>" + minimizingAlphas.Get(1) + "</td><td width = 200>" + minimizingWindowSizes.Get(1)
        + "</td><td width = 200>" + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(1))] + "</td><td width = 200>"
        + minimizingErrors.Get(1) + "</td><td width = 200>[rad]</td></tr>");
    htmlUtf8.appendln("<tr><td width = 200>Pose change</td><td width = 200>" + minimizingAlphas.Get(2) + "</td><td width = 200>" + minimizingWindowSizes.Get(2)
        + "</td><td width = 200>" + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(2))] + "</td><td width = 200>"
        + minimizingErrors.Get(2).divide(time(data)) + "</td><td width = 200>[m/s]</td></tr>");
    htmlUtf8.appendln("<tr><td width = 200>Orientation change</td><td width = 200>" + minimizingAlphas.Get(3) + "</td><td width = 200>"
        + minimizingWindowSizes.Get(3) + "</td><td width = 200>" + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(3))]
        + "</td><td width = 200>" + minimizingErrors.Get(3).divide(time(data)) + "</td><td width = 200>[rad/s]</td></tr>");
    htmlUtf8.appendln("</table>");
  }

  public static void main(String[] args) throws IOException {
    List<String> list = GokartPoseDataV1.INSTANCE.list();
    Iterator<String> iterator = list.iterator();
    while (iterator.hasNext()) {
      String data = iterator.next();
      Tensor minimizer = GeodesicFilterEvaluation.process(data);
      GeodesicFilterEvaluation.htmlWriter(data, minimizer);
      try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(HomeDirectory.Pictures("ComparisonTest.html"))) {
        htmlComparison(data, minimizer, htmlUtf8);
      }
    }
  }
}
