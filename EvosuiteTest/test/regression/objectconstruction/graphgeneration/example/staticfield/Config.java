package regression.objectconstruction.graphgeneration.example.staticfield;

public class Config {
	public static int numPublic;

	private static int numPrivate;

	public static PublicStaticFieldExample objPublic;

	private static PublicStaticFieldExample objPrivate;

	public static void setNumPrivate(int num) {
		numPrivate = num;
	}

	public static int getNumPrivate() {
		return numPrivate;
	}

	public static void setObjPrivate(PublicStaticFieldExample obj) {
		objPrivate = obj;
	}

	public static PublicStaticFieldExample getObjPrivate() {
		return objPrivate;
	}
}
