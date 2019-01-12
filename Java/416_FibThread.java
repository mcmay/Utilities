mport java.util.*;

class FibArray {

	private int[] fibArr;
	private int len;

	public FibArray (int n) {
        this.len = n;
        this.fibArr = new int[len];
    }
    public int[] getFibArr() {
	return FibArr;	
    }
}
class FibGenerator implements Runnable {

        private	FibData fibs;

		public FibGenerator(FibData fb) {

				this.fibs = fb;
		}

  public void run () {

   int prev, cur, sum, n;

   n = fibs.length;

    if (n == 1) {
     fibs[0] = 1;
    }
    else if (n == 2) {
      fibs[0] = 1;
      fibs[1] = 1;
    }
    else{
      prev = 1;
      cur = 1;
     while (n >= 3) {
      sum = prev + cur;
      prev = cur;
      cur = sum;
      n--;
      fibs[n] = cur;
     }
    }
    return FibData;
    }
}

public class MainThread {
    
    public void main (String[] args) {
    
		if (args.length > 0) {
			if (Integer.parseInt(args[0]) < 1)
					System.err.println(args[0] + " must be > 0.");
			else {

			 		try {
							
					}
			}
		}
    }
}

