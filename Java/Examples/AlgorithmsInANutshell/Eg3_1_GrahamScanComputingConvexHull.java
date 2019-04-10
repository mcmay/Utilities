//Example 3-1. GrahamScan implementation
public class NativeGrahamScan implements IConvexHull {
	public IPoint[] compute (IPoint[] pts) {
		int n = pts.length;
		if (n < 3) { return pts; }
	// Find lowest point and swap with last one in points[] array,
	// if it isn't there already
		int lowest = 0;
		double lowestY = pts[0].getY();
			for (int i = 1; i < n; i++) {
				if (pts[i].getY() < lowestY) {
					lowestY = pts[i].getY();
					lowest = i;
				}
		}
		if (lowest != n-1) {
			IPoint temp = pts[n-1];
			pts[n-1] = pts[lowest];
			pts[lowest] = temp;
		}
		// sort points[0..n-2] by descending polar angle with respect
		// to lowest point points[n-1].
		new HeapSort<IPoint>().sort(pts, 0, n-2, new ReversePolarSorter(pts[n-1]));
		// three points *known* to be on the hull are (in this order) the
		// point with lowest polar angle (points[n-2]), the lowest point
		// (points[n-1]), and the point with the highest polar angle
		// (points[0]). Start with first two
		DoubleLinkedList<IPoint> list = new DoubleLinkedList<IPoint>();
		list.insert(pts[n-2]);
		list.insert(pts[n-1]);
		// If all points are collinear, handle now to avoid worrying about later
		double firstAngle = Math.atan2(pts[0].getY() - lowest, pts[0].getX() - pts[n-1].getX());
		double lastAngle = Math.atan2(pts[n-2].getY() - lowest,	pts[n-2].getX() - pts[n-1].getX());
		if (firstAngle == lastAngle) {
			return new IPoint[] { pts[n-1], pts[0] };
		}
		// Sequentially visit each point in order, removing points upon
		// making mistake. Because we always have at least one "right
		// turn," the inner while loop will always terminate
		for (int i = 0; i < n-1; i++) {
			while (isLeftTurn(list.last().prev().value(), list.last().value(), pts[i])) {
				list.removeLast();
			}
			// advance and insert next hull point into proper position
			list.insert(pts[i]);
		}
		// The final point is duplicated, so we take n-1 points starting
		// from lowest point.
		IPoint hull[] = new IPoint[list.size()-1];
		DoubleNode<IPoint> ptr = list.first().next();
		int idx = 0;
		while (idx < hull.length) {
			hull[idx++] = ptr.value();
			ptr = ptr.next();
		}
		return hull;
	}

	/** Use Collinear check to determine left turn. */
	public static boolean isLeftTurn(IPoint p1, IPoint p2, IPoint p3) {
		return (p2.getX() - p1.getX())*(p3.getY() - p1.getY()) - (p2.getY() - p1.getY())*(p3.getX() - p1.getX()) > 0;
	}
}

/** Sorter class for reverse polar angle with respect to a given point. */
class ReversePolarSorter implements Comparator<IPoint> {
	/** Stored x,y coordinate of base point used for comparison. */
	final double baseX;
	final double baseY;
	/** PolarSorter evaluates all points compared to base point. */
	public ReversePolarSorter(IPoint base) {
		this.baseX = base.getX();
		this.baseY = base.getY();
	}
	public int compare(IPoint one, IPoint two) {
		if (one == two) { return 0; }
		// make sure both have computed angle using atan2 function.
		// Works because one.y is always larger than or equal to base.y
		double oneY = one.getY();
		double twoY = two.getY();
		double oneAngle = Math.atan2(oneY - baseY, one.getX() - baseX);
		double twoAngle = Math.atan2(twoY - baseY, two.getX() - baseX);
		if (oneAngle > twoAngle) { return -1; }
		else if (oneAngle < twoAngle) { return +1; }
		// if same angle, then must order by decreasing magnitude
		// to ensure that the convex hull algorithm is correct
		if (oneY > twoY) { return -1; }
		else if (oneY < twoY) { return +1; }
		return 0;
	}
}