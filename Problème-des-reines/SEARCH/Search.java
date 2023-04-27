interface Search 
{
public boolean estsolution( Object o);
public L empilefils( Object state, L pile);
public L backtrack( L pile, L solutions);
/*
	public L backtrack( L pile, L solutions)
	{
		if (nil==pile) return solutions;
		Object etat= hd( pile); pile= tl( pile);
		if (estsolution( etat)) backtrack( pile, cons( etat, solution));
		else backtrack( empilefils( etat, pile), solutions);
	}
*/
}

