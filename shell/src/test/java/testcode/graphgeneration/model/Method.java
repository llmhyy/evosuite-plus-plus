package testcode.graphgeneration.model;

import java.util.Objects;

import testcode.graphgeneration.Visibility;

public class Method extends CodeElement{	
	private Visibility visibility = Visibility.PUBLIC;
	private String name;
	private String returnType;
	private String declaringClass;

	public Method(String declaringClass, String name, String returnType) {
		super();
		this.declaringClass = declaringClass;
		this.name = name;
		this.returnType = returnType;
	}
	
	public Visibility getVisibility() {
		return visibility;
	}
	
	public void setAsPublic() {
		visibility = Visibility.PUBLIC;
	}
	
	public void setAsPrivate() {
		visibility = Visibility.PRIVATE;
	}

	public void setAsProtected() {
		visibility = Visibility.PROTECTED;
	}
	
	public void setAsDefaultVisibility() {
		visibility = Visibility.DEFAULT;
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
	
	public String getDeclaringClass() { 
		return this.declaringClass;
	}
	
	@Override
	public String toString() {
		return this.getVisibility() + " " + this.getReturnType() + " " + this.getName();
	}

	@Override
	public int hashCode() {
		return Objects.hash(declaringClass, name, returnType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Method other = (Method) obj;
		return Objects.equals(declaringClass, other.declaringClass) && Objects.equals(name, other.name)
				&& Objects.equals(returnType, other.returnType);
	}
	
	
}
