package com.FormulaCalculator;

public class OperationString {
	private String leftOp;
	private String op;
	private String rightOp;
	private String parentOp;
	private OperationString next;
	private OperationString previous;
	
	public OperationString () {
		
	}
	public String getLeftOp () {
		return leftOp;
	}
	public String getRightOp () {
		return rightOp;
	}
	public String getOp () {
		return op;
	}
	public String getParentOp () {
		return parentOp;
	}
	public OperationString getNext() {
		return next;
	}
	public OperationString getPrevious() {
		return previous;
	}
	

	public void setOp(String op) {
		this.op = op;
	}
	public void setLeftOp (String leftOp) {
		this.leftOp = leftOp;
	}
	public void setRightOp (String rightOp) {
		this.rightOp = rightOp;
	}
	public void setParentOp(String parentOp) {
		this.parentOp = parentOp;
	}
	public void setNext(OperationString next) {
		this.next = next;
	}
	public void setPrevious(OperationString previous) {
		this.previous = previous;
	}

	public void showString () {
		System.out.println("leftOp: " + leftOp);
		System.out.println("Op: " + op);
		System.out.println("rightOp: " + rightOp);
		System.out.println("parentOp: " + parentOp);
	}
}