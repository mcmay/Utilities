import java.util.*;
import java.lang.Iterable;

class FibArray {

	private int[] fibArr;
	private int len;

	public FibArray (int n) {
        this.len = n;
        this.fibArr = new int[len];
    }
    public int[] getFibArr() {
	return fibArr;	
    }
}
class FibGenerator implements Runnable {

	private int len;

	public FibGenerator (int len) {
		this.len = len;
	}

  public void run () {

   int prev, cur, sum, n;
   FibArray fibs = new FibArray(len);
   int[] fibArr = fibs.getFibArr();

    if (len == 1) {
    	fibArr[0] = 1;
    }
    else if (len == 2) {
      fibArr[0] = 1;
      fibArr[1] = 1;
    }
    else{
      prev = 1;
      cur = 1;
     while (len >= 3) {
      sum = prev + cur;
      prev = cur;
      cur = sum;
      len--;
      fibArr[len] = cur;
     }
    }
   }
}

public class FibThread {
    
    public static void main (String[] args) {
    
		if (args.length > 0) {
			if (Integer.parseInt(args[0]) < 1)
					System.err.println(args[0] + " must be > 0.");
			else {
				int argNum = Integer.parseInt(args[0]);

				FibArray fibs = new FibArray(argNum);
				Thread thrd = new Thread(new FibGenerator(argNum));
				thrd.start();

			 		try {
						thrd.join();
						System.out.println("The Fibonacci series up to " + argNum + " is:");
						int[] fibArr = fibs.getFibArr();
						for (int r : fibArr)
							System.out.println(r);		
				
					} catch (InterruptedException ie) { }
			}
		}
		else {
			System.err.println("Usage: program <number>"); 
		}
    }
}

