package formulaCleaner.nameLookup;

public class BucketHash {
	static final int N_BUCKETS = 100;
	
	static int hashFunction(String nameIn) {
		int hash = 0;
		String name = nameIn.toUpperCase();
		if (name.length() >7) {
			for(int i=0; i<7; i++) {
				hash += name.charAt(i)*17;
			}
		}
		else {
			for (int i=0; i<name.length(); i++) {
				hash+= name.charAt(i);
			}
		}
		return hash;
	}
	
	static int getBucket(String nameIn) {
		int hashValue = hashFunction(nameIn);
		int bucket = hashValue % N_BUCKETS;
		return bucket;
	}

}
