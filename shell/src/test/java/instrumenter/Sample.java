package instrumenter;

public class Sample extends SampleParent {
	private static final long serialVersionUID = -293584882622008707L;
	
	private Class clazz;

	public Sample(Class clazz, String term, TypeEnum type) throws Exception {
		super(clazz, clazz.getName() + "-method", term, type);
		this.clazz = clazz;
	}
}