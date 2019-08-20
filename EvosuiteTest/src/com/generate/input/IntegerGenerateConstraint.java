package com.generate.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.evosuite.symbolic.expr.IntegerConstraint;
import org.evosuite.symbolic.expr.Operator;
import org.evosuite.symbolic.expr.bv.IntegerBinaryExpression;
import org.evosuite.symbolic.expr.bv.IntegerConstant;
import org.evosuite.symbolic.expr.bv.IntegerUnaryExpression;
import org.evosuite.symbolic.expr.bv.IntegerValue;
import org.evosuite.symbolic.expr.bv.IntegerVariable;
import org.evosuite.symbolic.solver.Solver;
import org.evosuite.symbolic.solver.SolverCache;
import org.evosuite.symbolic.solver.SolverFactory;
import org.evosuite.symbolic.solver.SolverResult;
import org.evosuite.symbolic.vm.ConstraintFactory;
import org.evosuite.symbolic.vm.ExpressionFactory;
import org.evosuite.symbolic.vm.math.MIN;

import com.opencsv.CSVWriter;

import net.bytebuddy.jar.asm.Type;

import org.evosuite.symbolic.expr.Comparator;
import org.evosuite.symbolic.expr.Constraint;
import org.evosuite.symbolic.expr.Expression;

public class IntegerGenerateConstraint {
		
	public static IntegerValue RandomRightOperand() {
		//50%->constant
		//50%->variable
		double choose=Math.random();
		if(choose>0.5) {
			int ran;
			ran=new Random().nextInt(100000);
			IntegerValue right=ExpressionFactory.buildNewIntegerConstant(ran);
			return right;
		}
		else {
			return RandomOneOperand();
		}
		
	}
	
	public static Operator RandomOperator(int operandNum){
		// simple operator 
		// more complex ones are not implemented yet
		Operator op;
		if(operandNum==1) {
			op=Operator.values()[new Random().nextInt(1)+18];
			return op;
		}		
		else {
			op=Operator.values()[new Random().nextInt(4)+1];
			return op;
		}
		
	}
	
	public static IntegerValue RandomOneOperand(){
		IntegerValue left;
		
		//根据Pool中的数量决定之后variable的生成方式		
		double probability;
		if(VariablePool.size()<6) probability=0.1;
		else if(VariablePool.size()<11) probability=0.5;
		else if(VariablePool.size()<20) probability=0.8;
		else probability=0.7;
		
		double choose=Math.random();
		//new 
		if(choose>probability||VariablePool.size()==0) {
			
			left=new IntegerVariable("var"+VariablePool.size(),new Random().nextLong(),Long.MIN_VALUE,Long.MAX_VALUE);
			VariablePool.add((IntegerVariable) left);
		}
		else {
			left=VariablePool.get(new Random().nextInt(VariablePool.size()));
		}
		
		return left;
	}
	
	
	public static IntegerValue RandomLeftOperand()
	{
		return RandomOneOperand();
	}
	
	public static IntegerValue RandomBinaryOperand(){
		
		//left oprand 
		//number of operand
		IntegerValue left;
		//random,one operand or two operands
		long num=Math.round(Math.random());
		if(num==0) {
			left=RandomOneOperand();
			
		}
		else {
			//left operand
			IntegerValue l_left=RandomOneOperand();
			
			//right operand
			IntegerValue l_right=RandomOneOperand();
			
			//random operator
			Operator op=RandomOperator(2);
			
			//System.out.println(op.toString());
			left=new IntegerBinaryExpression(l_left,op,l_right,(long)0);
			
		}
		return left;
		
	}
	
	public static Comparator RandomComparator() {
		Comparator cmp=Comparator.values()[new Random().nextInt(Comparator.values().length)];
		return cmp;
	}
	
	public static void init() {
		constraintsSet.clear();
		VariablePool.clear();
	}
	
	private static List<Constraint<?>> constraintsSet = new ArrayList<Constraint<?>>();
	private static List<IntegerVariable> VariablePool = new ArrayList<IntegerVariable>();
	
	/**
	 * solver result
	 * @param constraintsSet
	 * @return if constraints can be solved
	 */
	public static boolean SolveConstraints(List<Constraint<?>> constraintsSet) {
		Solver solver = SolverFactory.getInstance().buildNewSolver();
		SolverCache solverCache = SolverCache.getInstance();
		SolverResult solverResult = solverCache.solve(solver, constraintsSet);
		if(solverResult.isSAT()) return true;
		else return false;
	}
	
	public static IntegerConstraint generateConstraint(){
		// compare
		Comparator cmp=RandomComparator();
		//left oprand 
		//number of operand
		IntegerValue left=RandomLeftOperand();
		//right operand, constant
		IntegerValue right=RandomRightOperand();
					
		IntegerConstraint ic=new IntegerConstraint(left,cmp,right);
		
		return ic;
	}
	
	public static List<Constraint<?>> generateConstraints(){		
		init();
		int i=15;
		for(;i>0;i--) {
			IntegerConstraint ic=generateConstraint();
			System.out.println(ic.toString());
			constraintsSet.add(ic);
		}
		//System.out.println(constraintsSet.toString());
		return constraintsSet;
	}
	
	/**
	 * print ratio of succ solve constraint
	 */
	public static void solverResult() {
		int succ=0;
		for(int i=0;i<100;i++) {
			List<Constraint<?>> constraintsSet = new ArrayList<Constraint<?>>();
			constraintsSet=generateConstraints();
			if(SolveConstraints(constraintsSet)) succ++;
		}
		System.out.print(succ);
	}
	
	public static String[] parsingOperand(Expression<?> operand) {
		String[] op_parameters=new String[3];
		if(operand.containsSymbolicVariable()) {
			op_parameters[0]="1";
			op_parameters[1]="0";
			op_parameters[2]="0";
		}
		else {
			op_parameters[0]="0";
			op_parameters[1]=operand.getConcreteValue().toString();
			op_parameters[2]="0";
		}
		return op_parameters;
	}
	
	public static String parsingComparator(Comparator cmp) {
		StringBuilder sb = new StringBuilder();
		switch(cmp) {
		case EQ:
			sb.append("0");
			break;
		case NE:
			sb.append("1");
			break;
		case LT:
			sb.append("2");
			break;
		case LE:
			sb.append("3");
			break;
		case GT:
			sb.append("4");
			break;
		case GE:
			sb.append("5");
			break;
			default:
				break;
		}
		return sb.toString();
	}
	
	public static void dynamicVariableConvertToConstant(){
		//change some variable into constant using their initial value
		int ran=new Random().nextInt(VariablePool.size()-1);
		IntegerValue old=VariablePool.get(ran);
		IntegerConstant n=ExpressionFactory.buildNewIntegerConstant(old.getConcreteValue());
		for(int i=0;i<constraintsSet.size();i++) {
			if(constraintsSet.get(i).getLeftOperand()==old) {
				Comparator cmp=constraintsSet.get(ran).getComparator();
				IntegerValue right=(IntegerValue)constraintsSet.get(ran).getRightOperand();		
				IntegerConstraint ic=new IntegerConstraint(n,cmp,right);
				if(ic.isSolveable()) constraintsSet.set(i, ic);
				else constraintsSet.remove(i);
			}
			else if(constraintsSet.get(i).getRightOperand()==old) {
				Comparator cmp=constraintsSet.get(ran).getComparator();
				IntegerValue left=(IntegerValue)constraintsSet.get(ran).getRightOperand();		
				IntegerConstraint ic=new IntegerConstraint(left,cmp,n);
				if(ic.isSolveable()) constraintsSet.set(i, ic);
				else constraintsSet.remove(i);
			}
		}

	}
	
	public static String[] constraintParsing(Constraint<?> cons) {
		String[] data=new String[8];;
		String[] left=parsingOperand(cons.getLeftOperand());
		String[] right=parsingOperand(cons.getRightOperand());
		String cmp=parsingComparator(cons.getComparator());
		System.arraycopy(left, 0, data, 0, 3);
		System.arraycopy(right, 0, data, 3, 3);
		data[6]=cmp;
		data[7]="0";
		return data;
		
	}
	
	public static void main(String[] argv) throws Exception{
		
	    try { 
	        // create FileWriter object with file as parameter 
	        FileWriter outputfile = new FileWriter("D:\\xianglin\\git_space\\bi-direct-rnn\\input\\test.csv",false); 	  
	        CSVWriter writer = new CSVWriter(outputfile,',','\0'); 
	  
	        // adding header to csv 
	        String[] header = { "left_op_constant_value", "left_op_isSymbolicValue", "left_op_type","right_op_constant_value","right_op_isSymbolicValue","right_op_type","cmp","condition" }; 
	        writer.writeNext(header); 
	        
	        for(int i=0;i<10000;i++) {
	        	List<Constraint<?>> constraintsSet = new ArrayList<Constraint<?>>();
	 			constraintsSet=generateConstraints();
	 			for(int j=0;j<constraintsSet.size();j++) {
	 				String[] data=constraintParsing(constraintsSet.get(j));
	 				writer.writeNext(data);
	 			}
	        }
			writer.close();
	    } 
	    catch (IOException e) { 
	        e.printStackTrace(); 
	    } 
	   
	}
	
}
