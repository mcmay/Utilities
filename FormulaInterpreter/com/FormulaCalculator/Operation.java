/** Operation class
  */

package com.FormulaCalculator;

public class Operation {
	private double leftOperand;
	private double rightOperand;
	private String operator;
	private double result;

	public Operation() {
		
	}
	public void setLeftOperand(double leftOperand) {
	    this.leftOperand = leftOperand;
	}
	public void setRighttOperand(double rightOperand) {
	    this.rightOperand = rightOperand;
	}
	public void setOperator(String operator) {
	    this.operator = operator;
	}
	public void setResult (double left, double right, String operator) {
		if (operator.equals("+"))
			result = left + right;
		else if (operator.equals("-"))
			result = left - right;
		else if (operator.equals("*"))
			result = left * right;
		else if (right != 0 && operator.equals("/"))
			result = left / right;
		else if (right != 0 && operator.equals("%"))
			result = (int) left % (int) right;
		else
			System.err.println("Invalide operation.");
	}
	public double getResult () {
		return result;
	}
}