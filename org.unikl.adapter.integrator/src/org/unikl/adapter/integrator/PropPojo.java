package org.unikl.adapter.integrator;

public class PropPojo {

	//String oid;
	String property;
	String value;

	/*
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}
	*/

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getValue() {
		return property;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
//		return "Property update [" + "oid=" + oid + ", property=" + property + ", value=" + value + "]";
		return "Property update [property=" + property + ", value=" + value + "]";
	}

}
