package evosuite.experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.evosuite.symbolic.expr.Comparator;
import org.evosuite.symbolic.expr.Constraint;
import org.evosuite.symbolic.expr.IntegerConstraint;
import org.evosuite.symbolic.expr.Operator;
import org.evosuite.symbolic.expr.RealConstraint;
import org.evosuite.symbolic.expr.bv.IntegerConstant;
import org.evosuite.symbolic.expr.bv.IntegerValue;
import org.evosuite.symbolic.expr.fp.RealBinaryExpression;
import org.evosuite.symbolic.expr.fp.RealConstant;
import org.evosuite.symbolic.expr.fp.RealUnaryExpression;
import org.evosuite.symbolic.expr.fp.RealValue;
import org.evosuite.symbolic.expr.fp.RealVariable;
import org.evosuite.symbolic.solver.Solver;
import org.evosuite.symbolic.solver.SolverCache;
import org.evosuite.symbolic.solver.SolverFactory;
import org.evosuite.symbolic.solver.SolverResult;
import org.evosuite.symbolic.vm.ExpressionFactory;

public class RealGenerateConstraint {
		
	public static RealValue RandomRightOperand() {
		//50%->constant
		//50%->variable
		double choose=Math.random();
		if(choose>0.5) {
			double ran;
			ran=(double)(new Random().nextInt(100000))+new Random().nextDouble();
			RealConstant right=new RealConstant(ran);
			return right;
		}
		else return RandomOneOperand();
		
	}
	
	public static Operator RandomOperator(int operandNum) {
		// simple operator 
		// more complex ones are not implemented yet
		Operator op;
		if(operandNum==1) {
			op=Operator.values()[0];
			return op;
		}
		else {
			op=Operator.values()[new Random().nextInt(4)+1];
			return op;
		}
	}
	
	public static RealValue RandomOneOperand(){
		RealValue left;
		
		//根据Pool中的数量决定之后variable的生成方式
		
		double probability;
		if(VariablePool.size()<6) probability=0.1;
		else if(VariablePool.size()<11) probability=0.3;
		else if(VariablePool.size()<20) probability=0.5;
		else probability=0.7;
		
		double choose=Math.random();
		//new 
		if(choose>probability||VariablePool.size()==0) {
			left=new RealVariable("var"+VariablePool.size(),new Random().nextDouble(),Double.MIN_VALUE,Double.MAX_VALUE);
			VariablePool.add((RealVariable) left);
		}
		else {
			left=VariablePool.get(new Random().nextInt(VariablePool.size()));
		}

//		//random
//		//unary operator
//		choose=Math.random();
//		//0表示没有operator，1表示有一个一元操作符
//		if(choose>0.5) {
//			Operator op=RandomOperator(1);
//			RealValue l=new RealUnaryExpression(left,op,(double)0);
//			return l;
//		}else return left;
		return left;
	}
	
	//goal
	//operand的数�?
	//operand是�?�是unary operand，�?�是�?�加上�?作符(问题是很多operator都没有implemented)
	//如果是两个operand，operator�?机，并且覆盖所有情况
	//operand是新建还是从variable中找
	
	public static RealValue RandomLeftOperand() {
		return RandomOneOperand();
	}
	
	public static RealValue RandomBinaryOperand(){
		
		//left oprand 
		//number of operand
		RealValue left;
		//random,one operand or two operands
		long num=Math.round(Math.random());
		if(num==0) {
			left=RandomOneOperand();
			
		}
		else {
			//left operand
			RealValue l_left=RandomOneOperand();
			
			//right operand
			RealValue l_right=RandomOneOperand();
			
			//random operator
			Operator op=RandomOperator(2);
			
			//System.out.println(op.toString());
			left=new RealBinaryExpression(l_left,op,l_right,(double)0);
			
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
	private static List<RealVariable> VariablePool = new ArrayList<RealVariable>();
	
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
		//System.out.println(solverResult.toString());
	}
	
	public static RealConstraint generateConstraint(){
		// compare
		Comparator cmp=RandomComparator();
		//left oprand 
		//number of operand
		RealValue left=RandomLeftOperand();
		//right operand, constant
		RealValue right=RandomRightOperand();
							
		RealConstraint ic=new RealConstraint(left,cmp,right);
				
		return ic;
	}
	
	public static List<Constraint<?>> generateConstraints() {
		
		init();
		int i=15;
		for(;i>0;i--) {
			RealConstraint ic=generateConstraint();
			//System.out.println(ic.toString());
			//ConstarintFactory.normalizer(ic);
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
			//System.out.println(" ");
			if(SolveConstraints(constraintsSet)) succ++;
		}
		System.out.print(succ);
	}
	
	public static void dynamicVariableConvertToConstant(){
		//change some variable into constant using their initial value
		int ran=new Random().nextInt(VariablePool.size()-1);
		RealValue old=VariablePool.get(ran);
		RealConstant n=ExpressionFactory.buildNewRealConstant(old.getConcreteValue());
		for(int i=0;i<constraintsSet.size();i++) {
			if(constraintsSet.get(i).getLeftOperand()==old) {
				Comparator cmp=constraintsSet.get(ran).getComparator();
				RealValue right=(RealValue)constraintsSet.get(ran).getRightOperand();		
				RealConstraint ic=new RealConstraint(n,cmp,right);
				if(ic.isSolveable()) constraintsSet.set(i, ic);
				else constraintsSet.remove(i);
			}
			else if(constraintsSet.get(i).getRightOperand()==old) {
				Comparator cmp=constraintsSet.get(ran).getComparator();
				RealValue left=(RealValue)constraintsSet.get(ran).getRightOperand();		
				RealConstraint ic=new RealConstraint(left,cmp,n);
				if(ic.isSolveable()) constraintsSet.set(i, ic);
				else constraintsSet.remove(i);
			}
		}
	}
	
	public static void main(String[] argc){
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
		
		
		/*
		 * List<Constraint<?>> g=generateConstraints(); for(int i=0;i<g.size();i++) {
		 * System.out.println(g.get(i).toString()); } SolveConstraints(g);
		 */
		
	}
}
