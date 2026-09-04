package egps2.utils.common.model.datatransfer;

/**
 * ThreeTuple provides shared utility logic for eGPS modules and UI.
 */
public class ThreeTuple<A,B,C> extends TwoTuple<A,B>{ 

	public final C third;
	public ThreeTuple(A a, B b,C c) {
		super(a, b);
		third = c;
	}
	
	@Override
	public String toString() {
		return first +"\t" + second + "\t" + third;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ThreeTuple) {
			ThreeTuple tt = (ThreeTuple) obj;
			return first.equals(tt.first) && second.equals(tt.second) && third.equals(tt.third);
		}else {
			return false;
		}
		
	}
	
	@Override
	public int hashCode() {
		return first.hashCode() + second.hashCode()+ third.hashCode();
	}

}
