
public class Eratosthene 
{
   static public void crible( int n)
   { int isprime[] = new int[n+1];
     for( int i=0; i <= n; i++) isprime[i] = 1; // true existe t il ?
     for( int k=2; k*k <= n; k++) 
        if (1==isprime[k]) 
           for( int multiple=k*k; multiple <= n; multiple += k) isprime[ multiple] = 0; 
     for (int k=2; k <= n; k++) if (1==isprime[k]) System.out.print( k + "  ");
     System.out.println(); 
   }

 
   public static void main (String[] args)
   {
    crible( 10000 );
   }
}
