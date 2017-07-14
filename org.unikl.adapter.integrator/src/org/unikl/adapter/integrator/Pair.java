package org.unikl.adapter.integrator;

public class Pair<L, R> {

	private final L parameterName;
	private final R parameterValue;

	public Pair(L parameterName, R parameterValue) {
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
	}

	public L getParameterName() {
		return parameterName;
	}

	public R getParameterValue() {
		return parameterValue;
	}

	@Override
	public int hashCode() {
		return parameterName.hashCode() ^ parameterValue.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair))
			return false;
		Pair pairo = (Pair) o;
		return this.parameterName.equals(pairo.getParameterName())
				&& this.parameterValue.equals(pairo.getParameterValue());
	}
}