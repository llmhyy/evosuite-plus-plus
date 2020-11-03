package regression.hybrid.example;

public class HybridExample {
	public void test(int x, int y) {
		if(x == y + 100) {
			if(x >= 500) {
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
	
	public void function(int x,int y,String s) {		
		//3.
		if(x == y + 10000) {
			if(x > y * 20){
				if(Palindrome(s)) {
					s = breakPalindrome(s);
					return;
				}
			return;
			}
		}
		
		
	}
	
	public void magicnumber(int x,int y) {
		if(x == y + 13) {
			return;
		}
	}
	
	public void branch2(int x,int y) {
		if(x == y + 10000) {
			if(x > y * 20) {
			return;
			}
		}
	}
	
	public void ifnull(String s) {
//		if(s == null) 
		if(s == "")
			return;
	}
	
	public void nonlinear1(double n,double m,int x,int z) {
		if(n  > Math.sqrt(m) + 1000) {
			if(z > Math. pow (n,   2 )- 2) {
				if(x >= Math.log(n - 10) + 10000) {
					return;
				}
			}
		return;		
		}
	}
	
	public void nonlinear(double x,double y) {
		if(x  > Math.sqrt(y) + 1010) {
			return;
		}
	}
	
	public int multiply(int A, int B) {
		if(B < 0)
			return 0;
	
        if(B == 0)
            return 0;
        return A + multiply(A,B-1);
    }
	
	public void longpath(int x,int y,int z,String s,double n,double m,boolean t) {	
		if(n  > Math.sqrt(m) + 1000) {
			if(z > Math. pow (n,   2 )- 2) {
				if(x > Math.log(n - 10) + 10000) {
					if(s.equals("sas")) {
						if(y * y * y > z / x) {
							return;
						}
					}
				}
			}
		}
}
	
	
	public int maxSubArray(int[] nums) {
        int max = nums[0];
        int res = nums[0];
        for(int i = 0;i < nums.length;i++){
            res = 0;
            for(int j = i;j < nums.length;j++){
                res += nums[j];
                if(res > max)
                    max = res;
            }
        }
         return max;
    }
	
	public void nonlinear2(double x,double y) {
		if(x * x * x == 8) {
				return;
		}
	}
	
	
	public void longpath1(int x,int y,int z,String s,double n,double m) {	
		if(x * x * x  > 1000) {
			if(y > x * x - 2) {
				if(z <= x / 10000 - 250) {
					if(n * n == 7) {
						if(m * m * m == z / x) {
							return;
							}
						}
					}
				}
			}
		}
	

}

