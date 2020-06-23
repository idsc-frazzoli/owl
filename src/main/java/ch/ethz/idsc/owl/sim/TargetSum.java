// code by jph
package ch.ethz.idsc.owl.sim;

public class TargetSum {
  public static boolean check(int[] array, int target) {
    return check(array, target, 0);
  }

  private static boolean check(int[] array, int target, int index) {
    if (array.length == index)
      return target == 0;
    int value = array[index];
    if (value % 5 == 0)
      return index == array.length - 1 || array[index + 1] != 1 //
          ? check(array, target - value, index + 1) // force
          : check(array, target, index + 1); // prohibit
    return check(array, target - value, index + 1) //
        || check(array, target, index + 1);
  }
}
