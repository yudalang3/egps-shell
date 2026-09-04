package egps2.utils.common.model.datatransfer;

/**
 * TwoTuple provides shared utility logic for eGPS modules and UI.
 */
public class TwoTuple<A,B> {

	public final A first;
	public final B second;
	
	public TwoTuple(A first, B second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public String toString() {
		return first + "\t" + second;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TwoTuple) {
			TwoTuple tt = (TwoTuple) obj;
			return first.equals(tt.first) && second.equals(tt.second);
		}else {
			return false;
		}
		
	}
	
	@Override
	public int hashCode() {
		return first.hashCode() + second.hashCode();
	}
	
}
