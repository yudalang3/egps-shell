package egps2.frame.gui.interacive;

/**
 * RectObj supports the main eGPS window, actions, or tab management.
 */
public abstract class RectObj implements RectAdjustMent{
	public double x;
	public double y;
	public double w;
	public double h;
	
	protected boolean available;

	public RectObj(double x, double y, double w, double h) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	@Override
	public boolean contains(double x, double y) {
		double x0 = this.x;
		double y0 = this.y;
		return (x >= x0 && y >= y0 && x < x0 + w && y < y0 + h);
	}

	@Override
	public boolean isAvailable() {
		return available;
	}

	@Override
	public String toString() {
		return x + "\t" + y + "\t" + w + "\t" + h;
	}

	@Override
	public abstract void adjustPaintings(double d, double e) ;
}
