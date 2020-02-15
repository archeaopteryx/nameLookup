package formulaCleaner.nameLookup;

public class GenericArray<T> {
	
	public GenericArray(int n) {
		array = new Object[n];
	}
	
	public int size() {
		return array.length;
	}
	
	public void set(int k, T value) {
		array[k] = value;
	}
	
	@SuppressWarnings("unchecked")
	public T get(int k) {
		return (T) array[k];
	}
	
	private Object[] array;

}
