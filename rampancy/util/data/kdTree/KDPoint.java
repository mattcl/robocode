package rampancy.util.data.kdTree;

import java.util.Arrays;

public class KDPoint<T> {
	public T value;
	public double[] features;
	
	public KDPoint(T value, double[] features) {
		this.value = value;
		this.features = Arrays.copyOf(features, features.length);
	}
	
	public double distanceTo(KDPoint<T> point) {
		double distance = 0;
		for (int i = 0; i < features.length; i++) {
			double diff = this.features[i] - point.features[i];
			distance += diff * diff;
		}
		return distance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(features);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		KDPoint<T> other = (KDPoint<T>) obj;
		if (!Arrays.equals(features, other.features))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
