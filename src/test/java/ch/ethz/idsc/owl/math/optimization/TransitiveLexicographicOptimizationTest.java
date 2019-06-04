// code by astoll
package ch.ethz.idsc.owl.math.optimization;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import ch.ethz.idsc.owl.demo.order.ScalarTotalOrder;
import ch.ethz.idsc.owl.math.order.LexicographicComparator;
import ch.ethz.idsc.owl.math.order.OrderComparator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class TransitiveLexicographicOptimizationTest extends TestCase {
  public void testSimple() {
    List<Tensor> inputList = new LinkedList<>();
    inputList.add(Tensors.fromString("{1,2}"));
    inputList.add(Tensors.fromString("{1,2}"));
    List<Function<Tensor, Tensor>> featureFunctionVector = new LinkedList<>();
    featureFunctionVector.add(x -> x.Get(0));
    featureFunctionVector.add(x -> x.Get(1));
    List<Function<Tensor, Tensor>> scoringFunctionVector = Arrays.asList(x -> x);
    List<OrderComparator<? extends Object>> comparators = Collections.nCopies(2, ScalarTotalOrder.INSTANCE);
    LexicographicComparator genericLexicographicOrder = new LexicographicComparator(comparators);
    TransitiveLexicographicOptimization opt = new TransitiveLexicographicOptimization(inputList, featureFunctionVector, scoringFunctionVector,
        genericLexicographicOrder);
    assertEquals(inputList, opt.inputs);
    assertEquals(featureFunctionVector, opt.featureFunctionVector);
    assertEquals(genericLexicographicOrder, opt.orderComparator);
  }

  public void testGetElementInObjectiveSpace() {
    List<Tensor> inputList = new LinkedList<>();
    inputList.add(Tensors.fromString("{1,2}"));
    inputList.add(Tensors.fromString("{-2,2}"));
    List<Function<Tensor, Tensor>> featureFunctionVector = new LinkedList<>();
    featureFunctionVector.add(x -> x.Get(0).abs());
    featureFunctionVector.add(x -> x.Get(1));
    List<Function<Tensor, Tensor>> scoringFunctionVector = Arrays.asList(x -> x);
    List<OrderComparator<? extends Object>> comparators = Collections.nCopies(2, ScalarTotalOrder.INSTANCE);
    LexicographicComparator genericLexicographicOrder = new LexicographicComparator(comparators);
    TransitiveLexicographicOptimization opt = new TransitiveLexicographicOptimization(inputList, featureFunctionVector, scoringFunctionVector,
        genericLexicographicOrder);
    assertEquals(Tensors.fromString("{1,2}"), opt.getElementInObjectiveSpace(opt.inputs.get(0)));
    assertEquals(Tensors.fromString("{2,2}"), opt.getElementInObjectiveSpace(opt.inputs.get(1)));
  }

  public void testInputsInObjectiveSpace() {
    List<Tensor> inputList = new LinkedList<>();
    inputList.add(Tensors.fromString("{1,2}"));
    inputList.add(Tensors.fromString("{-2,2}"));
    List<Function<Tensor, Tensor>> featureFunctionVector = new LinkedList<>();
    featureFunctionVector.add(x -> x.Get(0).abs());
    featureFunctionVector.add(x -> x.Get(1));
    List<Function<Tensor, Tensor>> scoringFunctionVector = Arrays.asList(x -> x);
    List<OrderComparator<? extends Object>> comparators = Collections.nCopies(2, ScalarTotalOrder.INSTANCE);
    LexicographicComparator genericLexicographicOrder = new LexicographicComparator(comparators);
    TransitiveLexicographicOptimization opt = new TransitiveLexicographicOptimization(inputList, featureFunctionVector, scoringFunctionVector,
        genericLexicographicOrder);
    assertTrue(opt.inputsInObjectiveSpace().containsAll(Arrays.asList(Tensors.fromString("{1,2}"), Tensors.fromString("{2,2}"))));
  }

  public void testGetScore() {
    List<Tensor> inputList = new LinkedList<>();
    inputList.add(Tensors.fromString("{1,2}"));
    inputList.add(Tensors.fromString("{-2,2}"));
    List<Function<Tensor, Tensor>> featureFunctionVector = new LinkedList<>();
    featureFunctionVector.add(x -> x.Get(0).abs());
    featureFunctionVector.add(x -> x.Get(1));
    List<Function<Tensor, Tensor>> scoringFunctionVector = Arrays.asList(x -> x.Get(0).add(RealScalar.ONE), x -> x.Get(1).add(RealScalar.ONE));
    List<OrderComparator<? extends Object>> comparators = Collections.nCopies(2, ScalarTotalOrder.INSTANCE);
    LexicographicComparator genericLexicographicOrder = new LexicographicComparator(comparators);
    TransitiveLexicographicOptimization opt = new TransitiveLexicographicOptimization(inputList, featureFunctionVector, scoringFunctionVector,
        genericLexicographicOrder);
    assertEquals(Tensors.fromString("{2,3}"), opt.getScoreOfObjectives(opt.getElementInObjectiveSpace(opt.inputs.get(0))));
    assertEquals(Tensors.fromString("{3,3}"), opt.getScoreOfObjectives(opt.getElementInObjectiveSpace(opt.inputs.get(1))));
  }

  public void testGetOptimalSolutions() {
    List<Tensor> inputList = new LinkedList<>();
    inputList.add(Tensors.fromString("{1,2}"));
    inputList.add(Tensors.fromString("{-2,2}"));
    List<Function<Tensor, Tensor>> featureFunctionVector = new LinkedList<>();
    featureFunctionVector.add(x -> x.Get(0).abs());
    featureFunctionVector.add(x -> x.Get(1));
    List<Function<Tensor, Tensor>> scoringFunctionVector = Arrays.asList(x -> x.Get(0).add(RealScalar.ONE), x -> x.Get(1).add(RealScalar.ONE));
    List<OrderComparator<? extends Object>> comparators = Collections.nCopies(2, ScalarTotalOrder.INSTANCE);
    LexicographicComparator genericLexicographicOrder = new LexicographicComparator(comparators);
    TransitiveLexicographicOptimization opt = new TransitiveLexicographicOptimization(inputList, featureFunctionVector, scoringFunctionVector,
        genericLexicographicOrder);
    assertTrue(opt.getOptimalSolutions().contains(Tensors.fromString("{1,2}")));
  }

  public void testGetOptimalvalues() {
    List<Tensor> inputList = new LinkedList<>();
    inputList.add(Tensors.fromString("{3,2}"));
    inputList.add(Tensors.fromString("{-2,2}"));
    List<Function<Tensor, Tensor>> featureFunctionVector = new LinkedList<>();
    featureFunctionVector.add(x -> x.Get(0).abs());
    featureFunctionVector.add(x -> x.Get(1));
    List<Function<Tensor, Tensor>> scoringFunctionVector = Arrays.asList(x -> x.Get(0).add(RealScalar.ONE), x -> x.Get(1).add(RealScalar.ONE));
    List<OrderComparator<? extends Object>> comparators = Collections.nCopies(2, ScalarTotalOrder.INSTANCE);
    LexicographicComparator genericLexicographicOrder = new LexicographicComparator(comparators);
    TransitiveLexicographicOptimization opt = new TransitiveLexicographicOptimization(inputList, featureFunctionVector, scoringFunctionVector,
        genericLexicographicOrder);
    assertTrue(opt.getOptimalValues().contains(Tensors.fromString("{2,2}")));
  }
}
