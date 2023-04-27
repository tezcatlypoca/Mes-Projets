abstract class Backtrack implements Search
{
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
}

