package feature.hybrid.example;

public class SeedStrategyExample {
	public static void removeComment(String paramString)
	{
		String char1 = ";";
		String char2 = "\"";
		if ((char1 + char2).equals(paramString)) {
			System.out.print("Find");
		}
	}
}
