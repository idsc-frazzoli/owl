// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.io.IOException;

import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.subare.util.HtmlUtf8;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ResourceData;

public class GeodesicFilterEvaluation {
  GeodesicFilterEvaluation() {
  }

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
    Scalar duration = time.get(time.length() - 1).Get(0).subtract(time.get(0).Get(0));
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
      // TODO OB change name of frequency responses s.t. they are not overwritten each time
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
    // TODO Jan, Sorry, ich weiss nicht wie ich das schöner machen kann :)
    // datanames
    String data1 = "gyro/20181203T184122_1";
    String data2 = "gyro/20181203T184122_2";
    String data3 = "gyro/20181203T184122_3";
    String data4 = "2r/20180820T165637_1";
    String data5 = "2r/20180820T165637_2";
    String data6 = "2r/20180820T165637_3";
    String data7 = "0w/20180702T133612_2";
    String data8 = "0w/20180702T133612_2";
    String data9 = "3az/20180827T175941_3";
    String data10 = "3az/20180827T170643_3";
    String data11 = "4o/20181008T183011_6";
    String data12 = "4o/20181008T183011_7";
    String data13 = "5m/20190204T185052_03";
    String data14 = "5m/20190204T185052_09";
    // Minimizing each dataset
    Tensor minimizer1 = GeodesicFilterEvaluation.process(data1);
    GeodesicFilterEvaluation.htmlWriter(data1, minimizer1);
    Tensor minimizer2 = GeodesicFilterEvaluation.process(data2);
    GeodesicFilterEvaluation.htmlWriter(data2, minimizer2);
    Tensor minimizer3 = GeodesicFilterEvaluation.process(data3);
    GeodesicFilterEvaluation.htmlWriter(data3, minimizer3);
    Tensor minimizer4 = GeodesicFilterEvaluation.process(data4);
    GeodesicFilterEvaluation.htmlWriter(data4, minimizer4);
    Tensor minimizer5 = GeodesicFilterEvaluation.process(data5);
    GeodesicFilterEvaluation.htmlWriter(data5, minimizer5);
    Tensor minimizer6 = GeodesicFilterEvaluation.process(data6);
    GeodesicFilterEvaluation.htmlWriter(data6, minimizer6);
    Tensor minimizer7 = GeodesicFilterEvaluation.process(data7);
    GeodesicFilterEvaluation.htmlWriter(data7, minimizer7);
    Tensor minimizer8 = GeodesicFilterEvaluation.process(data8);
    GeodesicFilterEvaluation.htmlWriter(data8, minimizer8);
    Tensor minimizer9 = GeodesicFilterEvaluation.process(data9);
    GeodesicFilterEvaluation.htmlWriter(data9, minimizer9);
    Tensor minimizer10 = GeodesicFilterEvaluation.process(data10);
    GeodesicFilterEvaluation.htmlWriter(data10, minimizer10);
    Tensor minimizer11 = GeodesicFilterEvaluation.process(data11);
    GeodesicFilterEvaluation.htmlWriter(data11, minimizer11);
    Tensor minimizer12 = GeodesicFilterEvaluation.process(data12);
    GeodesicFilterEvaluation.htmlWriter(data12, minimizer12);
    Tensor minimizer13 = GeodesicFilterEvaluation.process(data13);
    GeodesicFilterEvaluation.htmlWriter(data13, minimizer13);
    Tensor minimizer14 = GeodesicFilterEvaluation.process(data14);
    GeodesicFilterEvaluation.htmlWriter(data14, minimizer14);
    // add data to collection
    try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(HomeDirectory.Pictures("ComparisonTest.html"))) {
      htmlComparison(data1, minimizer1, htmlUtf8);
      htmlComparison(data2, minimizer2, htmlUtf8);
      htmlComparison(data3, minimizer3, htmlUtf8);
      htmlComparison(data4, minimizer4, htmlUtf8);
      htmlComparison(data5, minimizer5, htmlUtf8);
      htmlComparison(data6, minimizer6, htmlUtf8);
      htmlComparison(data7, minimizer7, htmlUtf8);
      htmlComparison(data8, minimizer8, htmlUtf8);
      htmlComparison(data9, minimizer9, htmlUtf8);
      htmlComparison(data10, minimizer10, htmlUtf8);
      htmlComparison(data11, minimizer11, htmlUtf8);
      htmlComparison(data12, minimizer12, htmlUtf8);
      htmlComparison(data13, minimizer13, htmlUtf8);
      htmlComparison(data14, minimizer14, htmlUtf8);
    }
  }
}
