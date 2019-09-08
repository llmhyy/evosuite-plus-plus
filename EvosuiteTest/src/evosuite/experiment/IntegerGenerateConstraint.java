package evosuite.experiment;

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
		}		
		else {
			Operator op1=Operator.values()[new Random().nextInt(4)+1];
			Operator op2=Operator.values()[new Random().nextInt(6)+12];
			op=Math.random()>0.5?op1:op2;
		}
		return op;
		
	}
	
	//variable
	public static IntegerValue RandomOneOperand(){
		IntegerValue left;
			
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
		return Math.random()>0.5?RandomOneOperand():RandomBinaryOperand();
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
		int i=20;
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
	
	public static int parsingBinaryOperator(IntegerBinaryExpression b) {
		Operator op=b.getOperator();
		int type=0;
		switch(op) {
		case DIV:
			type=0;
			break;
		case MUL:
			type=1;
			break;
		case MINUS:
			type=2;
			break;
		case PLUS:
			type=3;
			break;
		case IAND:
			type=4;
			break;
		case IOR:
			type=5;
			break;
		case SHR:
			type=6;
			break;
		case SHL:
			type=7;
			break;
		case USHR:
			type=8;
			break;
		case IXOR:
			type=9;
			break;
			default:
				break;
		}
		return type;
		
	}
	
	public static String[] parsingOperand(Expression<?> operand) {
		String[] op_parameters=new String[] {"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"};
		if(operand.containsSymbolicVariable()) {
			op_parameters[1]="1";
		}
		else {			
			op_parameters[0]=operand.getConcreteValue().toString();
			op_parameters[2]="1";
		}
		if(operand.getSize()==1) op_parameters[3]="1";
		else {
			int op_type=parsingBinaryOperator((IntegerBinaryExpression)operand);
			op_parameters[op_type]="1";
		}
			
		op_parameters[14]="1";
		
		return op_parameters;
	}
	
	public static String[] parsingComparator(Comparator cmp) {
		String[] comparator=new String[] {"0","0","0","0","0","0"} ;
		int type=0;
		switch(cmp) {
		case EQ:
			type=0;
			break;
		case NE:
			type=1;
			break;
		case LT:
			type=2;
			break;
		case GT:
			type=3;
			break;
		case LE:
			type=4;
			break;
		case GE:
			type=5;
			break;
			default:
				break;
		}
		comparator[type]="1";
		return comparator;
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
		String[] data=new String[42];
		String[] left=parsingOperand(cons.getLeftOperand());
		String[] right=parsingOperand(cons.getRightOperand());
		String[] cmp=parsingComparator(cons.getComparator());
		System.arraycopy(left, 0, data, 0, 17);
		System.arraycopy(right, 0, data, 17, 17);
		System.arraycopy(cmp, 0, data, 34, 6);
		data[39]="1";
		data[40]="0";
		data[41]="0";
		return data;
		
	}
	public static String[] strategyParsing(String[] cons) {
		String[] strategy=new String[] {"0","0","0","0"};
		if(cons[34]=="1"||cons[35]=="1") {
			strategy[1]="1";
			strategy[3]=Double.toString(new Random().nextDouble()*0.05);
			return strategy;
		}
		else if(cons[36]=="1"||cons[37]=="1") {
			int flag=0;
			if(cons[3]=="1") flag=1;
			if(cons[4]=="1") flag=1;
			if(cons[5]=="1") flag=1;
			if(cons[6]=="1") flag=1;
			if(cons[7]=="1") flag=1;
			if(flag==1) {
				strategy[0]="1";
				strategy[3]=Double.toString(new Random().nextDouble()*0.05);
				return strategy;
				
			}	
		}
		strategy[2]="1";
		strategy[3]=Double.toString(new Random().nextDouble()/2+0.5);
		return strategy;
	}
	
	public static void main(String[] argv) throws Exception{
		
	    try { 
	        // create FileWriter object with file as parameter 
	        FileWriter test_file = new FileWriter("D:\\xianglin\\git_space\\bi-direct-rnn\\input\\test.csv",false); 
	        FileWriter test_label_file = new FileWriter("D:\\xianglin\\git_space\\bi-direct-rnn\\input\\test_label.csv",false);
	        CSVWriter writer1 = new CSVWriter(test_file,',','\0');
	        CSVWriter writer2 = new CSVWriter(test_label_file,',','\0');
	        // adding header to csv 
	        String[] header1 = { 
	        		"l_constantValue", 
	        		"l_isSymbolicValue",
	        		"l_nonSymbolicValue",
	        		"l_non_op",
	        		"l_DIV",
	        		"l_MUL",
	        		"l_MINUS",
	        		"l_PLUS",
	        		"l_IAND",
	        		"l_IOR",
	        		"l_SHR", 
	        		"l_SHL",
	        		"l_USHR",
	        		"l_IXOR",
	        		"l_int",
	        		"l_real",
	        		"l_string",
	        		"r_constantValue", 
	        		"r_isSymbolicValue",
	        		"r_nonSymbolicValue",
	        		"r_non_op",
	        		"r_DIV",
	        		"r_MUL",
	        		"r_MINUS",
	        		"r_PLUS",
	        		"r_IAND",
	        		"r_IOR",
	        		"r_SHR", 
	        		"r_SHL",
	        		"r_USHR",
	        		"r_IXOR",
	        		"r_int",
	        		"r_real",
	        		"r_string",
	        		"EQ",
	        		"NE",
	        		"LT",
	        		"GT",
	        		"LE",
	        		"GE",
	        		"if",
	        		"loop"
	        		}; 
	        String[] header2 = { "SE","SEARCH","RT", "timeout" }; 
	        writer1.writeNext(header1); 
	        writer2.writeNext(header2);
	        for(int i=0;i<10000;i++) {
	        	List<Constraint<?>> constraintsSet = new ArrayList<Constraint<?>>();
	 			constraintsSet=generateConstraints();
	 			for(int j=0;j<constraintsSet.size();j++) {
	 				String[] data=constraintParsing(constraintsSet.get(j));
	 				String[] strategy=strategyParsing(data);
	 				writer1.writeNext(data);
	 				writer2.writeNext(strategy);
	 			}
	        }
			writer1.close();
			writer2.close();
	    } 
	    catch (IOException e) { 
	        e.printStackTrace(); 
	    } 
	   
	}
	
}
