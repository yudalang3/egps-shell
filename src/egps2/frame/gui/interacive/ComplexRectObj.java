package egps2.frame.gui.interacive;

/**
 * Need To re-write the contains() method! 
 *
 */
public abstract class ComplexRectObj extends RectObj implements RectAdjustMent{
	protected boolean ifLeft = false;
	protected boolean ifRight = false;
	protected boolean ifTop = false;
	protected boolean ifBottom = false;
	protected boolean ifCenter = false;
	
	public ComplexRectObj(double x, double y, double w, double h) {
		super(x, y, w, h);
	}
	
	protected void allBooleansBeFalse() {
		ifLeft = false;
		ifRight = false;
		ifTop = false;
		ifBottom = false;
		ifCenter = false;
	}
	public void adjustLeft(double x0, double y0) {
		throw new UnsupportedOperationException();
	};
	public void adjustRight(double x0, double y0){
		throw new UnsupportedOperationException();
	};
	public void adjustTop(double x0, double y0){
		throw new UnsupportedOperationException();
	};
	public void adjustBottom(double x0, double y0){
		throw new UnsupportedOperationException();
	};
	public void adjustCenter(double x0, double y0){
		throw new UnsupportedOperationException();
	};


	@Override
	public void adjustPaintings(double d, double e) {
		if (ifLeft) {
			adjustLeft(d,e);
		}else if (ifRight) {
			adjustRight(d,e);
		}else if (ifTop) {
			adjustTop(d,e);
		}else if (ifBottom) {
			adjustBottom(d,e);
		}else if (ifCenter) {
			adjustCenter(d,e);
		}
	}
	

}
