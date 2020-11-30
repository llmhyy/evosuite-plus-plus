package feature.hybrid.example;

public class HybridSampleClass {
	private static int[] a;
	private static int[] b;
	
	public HybridSampleClass(
			int a0,int a1,int a2,int a3,int a4,
			int b0,int b1,int b2,int b3,int b4,
			int b5,int b6,int b7,int b8,int b9) {
		a = new int[] {a0,a1,a2,a3,a4};
		b = new int[] {b0,b1,b2,b3,b4,b5,b6,b7,b8,b9};
		for(int i = 0;i < b.length;i++) {
			if(b[i] < 0) throw new RuntimeException();
		}
	}
	
	public String run() {
		int k = 0;
		boolean flag = true;
		for(int i = 0;i < a.length ;i++) {
			if(a[i] > i * 1000) {
				for(int j = 0;j < b.length;j++) {
					if(b[j] < -a[i]) {
						k = 1;
					}
				}
			}else {
				flag = false;
			}
		}
		assert(k == 0);
		if(flag) {
			return "Yes";
		}else {
			return null;
		}
	}
}
