package egps2.utils.common.model.datatransfer;

/**
 * FourTuple provides shared utility logic for eGPS modules and UI.
 */
public class FourTuple<A,B,C,D> extends ThreeTuple<A, B, C> {
	public final D fourth;
	
	public FourTuple(A a, B b, C c ,D d) {
		super(a, b, c);
		fourth = d;
	}
	
	@Override
	public String toString() {
		return super.toString() + "\t" + fourth;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ThreeTuple) {
			FourTuple tt = (FourTuple) obj;
			return first.equals(tt.first) && second.equals(tt.second) && third.equals(tt.third) && fourth.equals(tt.fourth);
		}else {
			return false;
		}
		
	}
	
	@Override
	public int hashCode() {
		return first.hashCode() + second.hashCode()+ third.hashCode() + fourth.hashCode();
	}
}
