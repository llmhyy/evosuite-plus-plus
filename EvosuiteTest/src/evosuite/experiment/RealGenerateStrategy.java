package evosuite.experiment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.evosuite.Properties.Strategy;
import org.evosuite.symbolic.expr.Constraint;

public class RealGenerateStrategy {

	private static List<List<Outcome>> Outcome=new ArrayList<List<Outcome>>();
	
	public static List<Constraint<?>> getRandomConstraints(){
		List<Constraint<?>> cons=RealGenerateConstraint.generateConstraints();
		return cons;
	}
	
	public static void init() {
		Outcome.clear();
	}
	
	public static Strategy getRandomStrategy() {
		int strategy=new Random().nextInt(3);
		Strategy s=Strategy.RANDOM;
		switch(strategy) {
		case 1:
			s=Strategy.RANDOM;
			break;
		case 2:
			s=Strategy.MOSUITE;
			break;
		case 0:
			s=Strategy.DSE;
			break;
			default:
				break;
		}
		
		return s;
	}
	
	public static long getRandomTimeOut() {
		long timeout=(long)(Math.random()*20000);
		return timeout;
	}
	
	public static List<Outcome> getRandomPathOutcome(){
		List<Outcome> outcome=new ArrayList<Outcome>();
		List<Constraint<?>> cons=getRandomConstraints();
		Strategy s;
		long timeout;
		
		for(int i=0;i<cons.size();i++) {
			s=getRandomStrategy();
			timeout=getRandomTimeOut();
			
			Outcome oc=new Outcome(s,timeout,cons.get(i));
			outcome.add(oc);
			//System.out.println(oc.strategy+" "+oc.TIMEOUT_MILLIS+" "+oc.constraint.toString());
		}
		return outcome;
	}
	
	public static void Output() throws IOException{
		OutputStream f = new FileOutputStream("D:\\xianglin\\git_space\\evosuite\\EvosuiteTest\\src\\com\\generate\\RealOutcome.txt");
		for(int i=0;i<Outcome.size();i++) {
			List<Outcome> outcome=new ArrayList<Outcome>();
			outcome=Outcome.get(i);
			f.write(((i+1)+"-th\n").getBytes());
			for(int j=0;j<outcome.size();j++) {
				f.write((outcome.get(j).strategy).toString().getBytes());
				f.write((" ").getBytes());
				f.write(String.valueOf(outcome.get(j).TIMEOUT_MILLIS).getBytes());
				f.write((" ").getBytes());
				f.write((outcome.get(j).constraint).toString().getBytes());
				f.write((" ").getBytes());
				f.write(("\n").getBytes());
			}
		}
	}
	
	public static void Random(int num){
		for(;num>0;num--) {
			List<Outcome> outcome=new ArrayList<Outcome>();
			outcome=getRandomPathOutcome();
			Outcome.add(outcome);		
		}
	}

	public static void main(String[] argv) throws IOException {
		init();
		Random(Integer.parseInt(argv[0]));
		//Output();	
		OutputStream f = new FileOutputStream("D:\\xianglin\\git_space\\evosuite\\EvosuiteTest\\src\\com\\generate\\input\\RealOutcome.txt");
		for(int i=0;i<Outcome.size();i++) {
			List<Outcome> outcome=new ArrayList<Outcome>();
			outcome=Outcome.get(i);
			f.write(((i+1)+"-th\n").getBytes());
			for(int j=0;j<outcome.size();j++) {
				f.write((outcome.get(j).strategy).toString().getBytes());
				f.write((" ").getBytes());
				f.write(String.valueOf(outcome.get(j).TIMEOUT_MILLIS).getBytes());
				f.write((" ").getBytes());
				f.write((outcome.get(j).constraint).toString().getBytes());
				f.write((" ").getBytes());
				f.write(("\n").getBytes());
			}
		}
		
	}
}
