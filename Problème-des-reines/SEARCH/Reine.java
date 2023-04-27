class Etat 
{
	public int n;
	public int nposees;
	public int tab[];
	Etat( int nb) { tab=new int[nb]; 
			nposees=0; n = nb;
			for(int i=0; i<nb; i++) tab[i]= -1;
			}
	public String toString()
	{
		String s="";
		for (int k=0; k<nposees; k++)
                        s = s + this.tab[k] + ";" ;
		return s;	
	}
}
public class Reine implements Search
{
	public boolean estsolution( Object o)
	{	Etat etat= (Etat) o;
		return etat.nposees==etat.n;	
	}
	public boolean conflit( Etat e, int col, int lig)
	{
		assert( col== e.nposees);
		for (int c=0; c<e.nposees; c++)
		{
			if (e.tab[c]==lig 
		         || Math.abs( col-c) == Math.abs( lig - e.tab[c]))
			return true;
		}
		return false;
	}
	public L empilefils( Object s, L pile)
	{	Etat etat= (Etat) s;
		if (estsolution( s)) return pile;
		for (int k=0; k<etat.n; k++)
		{
			if (!conflit( etat, etat.nposees, k)) 
			{
				Etat fiston=new Etat( etat.n);
				for (int t=0; t<etat.nposees; t++)
					fiston.tab[t]=etat.tab[t];
				fiston.tab[ etat.nposees ]=k;
				fiston.nposees= 1+etat.nposees;
			//System.out.println( "empile "+ fiston.toString());
				pile= L.cons( fiston, pile);
			}
		}
		return pile;
	}

/*
        public L backtrack( L pile, L solutions)
        {       if (L.nil==pile) return solutions;
                Object etat= L.hd( pile); pile= L.tl( pile);
                if (estsolution( etat)) return  backtrack( pile, L.cons( etat, solutions));
                else return backtrack( empilefils( etat, pile), solutions);
        }
*/
public L backtrack( L pile, L solutions)
{	
	while( L.nil != pile)
	{
		Object etat= L.hd( pile); pile= L.tl( pile);
		if (estsolution( etat)) 
			solutions=L.cons( etat, solutions);
		else pile=empilefils( etat, pile);
	}
	return solutions;
}
        public static void main (String[] args)
	{
		int nb= Integer.parseInt(args[0]);
                Reine pb= new Reine();
		Etat vide= new Etat( nb);
                L sol= pb.backtrack( L.cons( vide, L.nil), L.nil);
		int nsol=0;
                for( L l=sol; null!=l; l=L.tl(l), nsol++)
                { Etat e=(Etat) L.hd(l); 
		  System.out.print( "Solution " + nsol + ": ");
		for (int k=0; k<e.nposees; k++) 
			System.out.print( e.tab[k]+"; ");
		System.out.println();
		}
        }



}
