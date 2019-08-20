package com.generate.input;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.evosuite.symbolic.expr.Operator;
import org.evosuite.symbolic.expr.RealConstraint;
import org.evosuite.symbolic.expr.StringConstraint;
import org.evosuite.symbolic.expr.bv.IntegerConstant;
import org.evosuite.symbolic.expr.bv.StringBinaryComparison;
import org.evosuite.symbolic.expr.bv.StringComparison;
import org.evosuite.symbolic.expr.fp.RealConstant;
import org.evosuite.symbolic.expr.fp.RealValue;
import org.evosuite.symbolic.expr.str.StringConstant;
import org.evosuite.symbolic.expr.str.StringUnaryExpression;
import org.evosuite.symbolic.expr.str.StringValue;
import org.evosuite.symbolic.expr.str.StringVariable;
import org.evosuite.symbolic.solver.Solver;
import org.evosuite.symbolic.solver.SolverCache;
import org.evosuite.symbolic.solver.SolverFactory;
import org.evosuite.symbolic.solver.SolverResult;
import org.evosuite.symbolic.vm.ExpressionFactory;

import org.evosuite.symbolic.expr.Comparator;
import org.evosuite.symbolic.expr.Constraint;

public class StringGenerateConstraint {
	
	
	//var0 op var1(or constant) == 0, var(unary or binary)
	
	 public static StringValue RandomStringConstant(int num) {
		 int leftLimit = 32; // letter ' '
		 int rightLimit = 126; // letter '~'
		 int targetStringLength = num;
		 Random random = new Random();
		 StringBuilder buffer = new StringBuilder(targetStringLength);
		 for (int i = 0; i < targetStringLength; i++) {
		     int randomLimitedInt = leftLimit + (int) 
		       (random.nextFloat() * (rightLimit - leftLimit + 1));
		     buffer.append((char) randomLimitedInt);
		 }
		 String generatedString = buffer.toString();
		 StringValue stringConstant=ExpressionFactory.buildNewStringConstant(generatedString);
		 return stringConstant;
		 
	}
	 
	 public static String RandomString(int num) {
		 int leftLimit = 32; // letter ' '
		 int rightLimit = 126; // letter '~'
		 int targetStringLength = num;
		 Random random = new Random();
		 StringBuilder buffer = new StringBuilder(targetStringLength);
		 for (int i = 0; i < targetStringLength; i++) {
		     int randomLimitedInt = leftLimit + (int) 
		       (random.nextFloat() * (rightLimit - leftLimit + 1));
		     buffer.append((char) randomLimitedInt);
		 }
		 return buffer.toString();
		 
	}
	 
	 public static StringValue RandomUnaryOperator(StringValue var) {
		 long ran=Math.round(Math.random());
		 if(ran==0) {
			 Operator op=Operator.values()[new Random().nextInt(2)+66];
			 StringValue result=new StringUnaryExpression(var,op,"");
			 return result;
		 }else return var;
	 }
		
	public static StringValue RandomLeftOperand(){
		StringValue left;
		
		//根据Pool中的数量决定之后variable的生成方式
		
		double probability;
		if(VariablePool.size()<6) probability=0.1;
		else if(VariablePool.size()<11) probability=0.3;
		else if(VariablePool.size()<20) probability=0.5;
		else probability=0.7;
		
		double choose=Math.random();
		//new 
		if(choose>probability||VariablePool.size()==0) {
			int ran=new Random().nextInt(15);
			left=new StringVariable("var"+VariablePool.size(),RandomString(ran));
			VariablePool.add((StringVariable) left);
		}
		else {
			left=VariablePool.get(new Random().nextInt(VariablePool.size()));
		}
		//return RandomUnaryOperator(left);
		return left;

	}
	
	public static StringValue RandomRightOperand() {
		
		StringValue right;
		
		
		//右边从Pool里找的概率较低
		//根据Pool中的数量决定之后variable的生成方式
		
		double probability;
		if(VariablePool.size()<6) probability=0.1;
		else if(VariablePool.size()<11) probability=0.2;
		else if(VariablePool.size()<20) probability=0.3;
		else probability=0.4;
		
		double choose=Math.random();
		//new 
		if(choose>probability||VariablePool.size()==0) {
			//constant or variable
			choose=Math.random();
			//constant
			if(choose<0.5) {
				int ran=new Random().nextInt(15);
				right=RandomStringConstant(ran);
			}
			//variable
			else {
				int ran=new Random().nextInt(15);
				right=new StringVariable("var"+VariablePool.size(),RandomString(ran));
				VariablePool.add((StringVariable) right);
				//right=RandomUnaryOperator(right);
			}
		}
		else {
			right=VariablePool.get(new Random().nextInt(VariablePool.size()));
			//right=RandomUnaryOperator(right);
		}
		return right;
		
	}
	
	//goal
	//operand的数�?
	//operand是�?�是unary operand，�?�是�?�加上�?作符(问题是很多operator都没有implemented)
	//如果是两个operand，operator�?机，并且覆盖所有情况
	//operand是新建还是从variable中找
	
	
	public static Operator RandomComparator() {
		//目前全是2元，还有REGIONMATCHES("regionMatches", 6), not yet implemented
		Operator cmp=Operator.values()[new Random().nextInt(6)+54];
		return cmp;
	}
	
	public static void init() {
		constraintsSet.clear();
		VariablePool.clear();
	}
	
	private static List<Constraint<?>> constraintsSet = new ArrayList<Constraint<?>>();
	private static List<StringVariable> VariablePool = new ArrayList<StringVariable>();
	
	public static void SolveConstraints(List<Constraint<?>> constraintsSet) {
		Solver solver = SolverFactory.getInstance().buildNewSolver();
		SolverCache solverCache = SolverCache.getInstance();
		SolverResult solverResult = solverCache.solve(solver, constraintsSet);
		System.out.println(solverResult.toString());
	}
	
	public static StringConstraint generateConstraint(){
		// compare
		Operator op=RandomComparator();
		//left operand 
		//number of operand
		StringValue left=RandomLeftOperand();
		//right operand, constant
		StringValue right=RandomRightOperand();
					
		StringComparison l=new StringBinaryComparison(left,op,right,(long)0);
		IntegerConstant r=new IntegerConstant((long)0);
					
		StringConstraint ic=new StringConstraint(l,Comparator.EQ,r);
		return ic;
	}
	
	public static List<Constraint<?>> generateConstraints() {	
		init();
		int i=15,ran=0;
		for(;i>0;i--) {
			StringConstraint ic=generateConstraint();
		
			//System.out.println(ic.toString());
			//ConstarintFactory.normalizer(ic);
			constraintsSet.add(ic);		
			
		}
		//System.out.println(constraintsSet.toString());
		return constraintsSet;
	}
	public static void dynamicVariableConvertToConstant(){
		//change some variable into constant using their initial value
		int ran=new Random().nextInt(VariablePool.size()-1);
		StringValue old=VariablePool.get(ran);
		StringConstant n=ExpressionFactory.buildNewStringConstant(old.getConcreteValue());
		for(int i=0;i<constraintsSet.size();i++) {
			StringBinaryComparison curr=(StringBinaryComparison)constraintsSet.get(i).getLeftOperand();
			if(curr.getLeftOperand()==old) {
				Operator cmp=curr.getOperator();
				StringValue right=(StringValue)curr.getRightOperand();					
				StringComparison l=new StringBinaryComparison(n,cmp,right,(long)0);
				IntegerConstant r=new IntegerConstant((long)0);		
				StringConstraint ic=new StringConstraint(l,Comparator.EQ,r);
				if(ic.isSolveable()) constraintsSet.set(i, ic);
				else constraintsSet.remove(i);
			}
			else if(curr.getRightOperand()==old) {
				Operator cmp=curr.getOperator();
				StringValue left=(StringValue)curr.getLeftOperand();					
				StringComparison l=new StringBinaryComparison(left,cmp,n,(long)0);
				IntegerConstant r=new IntegerConstant((long)0);		
				StringConstraint ic=new StringConstraint(l,Comparator.EQ,r);
				if(ic.isSolveable()) constraintsSet.set(i, ic);
				else constraintsSet.remove(i);
			}
		}
	}
	
	
	public static void main(String[] argc) {
//		List<Constraint<?>> g=generateConstraints();
//		for(int i=0;i<g.size();i++) {
//			System.out.println(g.get(i).toString());
//		}
//		SolveConstraints(g);
		 List<Constraint<?>> c= new ArrayList<Constraint<?>>();
		  c=generateConstraints(); 
		  for(int i=0;i<c.size();i++) {
			  System.out.println(c.get(i).toString()); 
		  }
		  dynamicVariableConvertToConstant();
		  System.out.println("\n"); 
		  for(int i=0;i<c.size();i++) {
			  System.out.println(c.get(i).toString()); 
		  }
	}

}
