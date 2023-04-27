
public class Pbm implements Search
{
	public boolean estsolution( Object o) { int i= ((Integer)o).intValue(); return 100==i; }
	public L empilefils( Object s, L pile)
	{ int n=((Integer) s).intValue();
	  if (n < 100) { Object x1= new Integer(2*n); 
			 Object x2 = new Integer(2*n +1); 
			 System.out.println( "etat n, empile n1, empile n2 = " + n + ", " + x1 + ", " + x2 );
			 return L.cons( x1, L.cons( x2, pile));
			}
	 else return pile;
	}
	public L backtrack( L pile, L solutions)
	{ 	if (L.nil==pile) return solutions;
		Object etat= L.hd( pile); pile= L.tl( pile);
		if (estsolution( etat)) return 	backtrack( pile, L.cons( etat, solutions));
		else return backtrack( empilefils( etat, pile), solutions);
	}
	public static void main (String[] args)
	{
		Pbm pb= new Pbm();		
		L sol= pb.backtrack( L.cons( new Integer( 1), L.nil), L.nil);
		for( L l=sol; null!=l; l=L.tl(l)) 
		{ int x=((Integer) L.hd(l)).intValue(); System.out.println( "Solution: "+ x); }
	}
}
