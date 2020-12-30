package feature.fbranch.example;

public class FlagEffectExample {
	public int example(int x, int y) {
		int a = x + y;
		if (callFlag(a)) {
			return 0;
		}
		return 1;
	}

	public boolean callFlag(int x) {
		if (x/1000 > 100) {
			return true;
		} else {
			return false;
		}
		
	}
}
