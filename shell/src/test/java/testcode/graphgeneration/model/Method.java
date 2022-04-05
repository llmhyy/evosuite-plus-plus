package testcode.graphgeneration.model;

public class Method {
	public static enum MethodVisibility {
		PUBLIC("public"),
		PRIVATE("private"),
		PROTECTED("protected"),
		DEFAULT("");
		
		String stringRepresentation;
		MethodVisibility(String stringRepresentation) {
			this.stringRepresentation = stringRepresentation;
		}
		
		@Override
		public String toString() {
			return this.stringRepresentation;
		}
	};
	
	private MethodVisibility visibility = MethodVisibility.PRIVATE;
	private String name;
	private String returnType;
	private String declaringClass;

	public Method(String declaringClass, String name, String returnType) {
		super();
		this.declaringClass = declaringClass;
		this.name = name;
		this.returnType = returnType;
	}

	public MethodVisibility getVisibility() {
		return visibility;
	}
	
	public void setAsPublic() {
		visibility = MethodVisibility.PUBLIC;
	}
	
	public void setAsPrivate() {
		visibility = MethodVisibility.PRIVATE;
	}

	public void setAsProtected() {
		visibility = MethodVisibility.PROTECTED;
	}
	
	public void setAsDefaultVisibility() {
		visibility = MethodVisibility.DEFAULT;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	
	@Override
	public String toString() {
		return this.getVisibility() + " " + this.getReturnType() + " " + this.getName();
	}
}
