package ch.ethz.idsc.owl.sim;

import java.util.Stack;

public class Parentheses {
  /** Check parentheses:
   * aaa(bb)ccc[(dd)e]{} // OK
   * ff((gg)h]xxx // NOK
   * @param args */
  private static enum Type {
    ROUND, // 1
    SQUARE, // 2
    CURLEY; // 3
  }

  public static boolean checkParentheses(String text) {
    Stack<Type> stack = new Stack<>();
    for (int index = 0; index < text.length(); ++index)
      switch (text.charAt(index)) {
      case '(':
        stack.add(Type.ROUND);
        break;
      case '[':
        stack.add(Type.SQUARE);
        break;
      case '{':
        stack.add(Type.CURLEY);
        break;
      case ')':
        if (stack.isEmpty() || !stack.pop().equals(Type.ROUND))
          return false;
        break;
      case ']':
        if (stack.isEmpty() || !stack.pop().equals(Type.SQUARE))
          return false;
        break;
      case '}':
        if (stack.isEmpty() || !stack.pop().equals(Type.CURLEY))
          return false;
        break;
      }
    return stack.isEmpty();
  }

  public static void main(String[] args) {
    System.out.println(checkParentheses("ab[bd]as]d")); // -> expected TRUE
    System.out.println(checkParentheses("aaa[bb(cc)d]{e}")); // -> expected TRUE
    System.out.println(checkParentheses("aaa[bb(cc]d]{e}")); // -> expected FALSE
  }
}
