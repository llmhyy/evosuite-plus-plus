package regression.hybrid.example;

public class HybridExample {
	public void test(int x, int y) {
		if(x>5) {
			if(x==y) {
				return;
			}
		}
	}
	
//	1.compare x,y
	public int compare(int x,int y) {
		int z = 0;
		if(x >= y) {
			if(x == y)
				return z;
			return z+1;
		}
		return z-1;
	}
	
//	2.count the number of comparisons
	public int countCompare(int n,int m) {
		int z; 	
		z = compare(n,m);
		if(z == 0) {
			return z;
		}else if(z > 0)
			return z + countCompare(n-1,m);
		else return z + countCompare(n,m-1);
	}
	
//	3.breakPalindrome
	public String breakPalindrome(String palindrome) {
        if (palindrome == null || palindrome.length() <= 1) {
            return "";
        }
        for (int i = 0; i < palindrome.length() / 2; i++) {
            if (palindrome.charAt(i) != 'a') {
                return palindrome.substring(0, i) + 'a' + palindrome.substring(i + 1);
            }
        }
        return palindrome.substring(0, palindrome.length() - 1) + 'b';
    }
	
//	4.Palindrome
	public boolean Palindrome(String s) {
		int n = s.length();
		
		if(s == null || n == 0)
			return false;
		
		if(n == 1) return true;
		
		for(int i = 0;i <= n/2;i++)
		{
			if(s.charAt(i) != s.charAt(n-i-1))
				return false;
		}
		return true;
	}
	
//	5.
	public String shortestPalindrome(String s) {
		int length = s.length();
		
		String s1 = "";
		
		for(int n = length-1;n >= 0 && length > 1;n--) {
			s1 = s1 + s.charAt(n);
			if(Palindrome(s1 + s))
				break;
		}
		return s1 + s;

    }
	
//	6.
	public void mixtrue(String s) {
		if(Palindrome(s)) {
			s = breakPalindrome(s);
		}
		else
			s = shortestPalindrome(s);
	}
	
	public void randomCannot(int x,int y) {
		
//		if(x == y + 10000) {
//			return;
//		}else if(x > y * 20) {
//			return;
//		}
		
		if(x>5) {
			if(x==y) {
				return;
			}
		}
	}
	
}
