package regression.objectconstruction.graphgeneration.example.multipath;

public class MultiPathExample {
	public void checkRules1(Param p1, Param p2){
		State s1 = p1.getState();
		State s2 = p2.getState();
		
		Player player11 = s1.player();
		Player player12 = s1.player();
		Player player21 = s2.player();
		Player player22 = s2.player();
		
		if(player11.isAlive() 
				&& player12.isAlive()
				&& player21.isAlive()
				&& player22.isAlive()){
			System.currentTimeMillis();
		}
		
	}
	
	public void checkRules2(Param p1, Param p2){
		State s1 = p1.getState();
		State s2 = p2.getState();
		
		Player player11 = s1.player();
		Player player21 = s2.player();
		
		if(player11.isAlive() 
				&& player21.isAlive()){
			System.currentTimeMillis();
		}
	}
}
