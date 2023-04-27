class L 
{   // GERER LES LISTES D'ECHIQUIERS
    E elt; L next;
   static  L cons( E x, L tail)
        { L cell=new L(); cell.elt=x; cell.next=tail; return cell;}
   static  L nil= new L();  // j'aimerai bien mettre NULL, 0, ou 1 truc comme ca...
   static  boolean isempty( L liste) { return liste==nil ; }
   static  E head( L liste) { return liste.elt; }
   static  L tail(  L liste) { return liste.next; }
}

class E // ECHIQUIER: pos[col]=la ligne o`u est posee la reine de la colonne col
{  
   int [] pos; int nbposees; // nbposees reines sont posees
   static E  creer( int n) 
   { 	E e=new E(); e.pos= new int[n]; e.nbposees=0; return e; 
   }
   static  boolean possible( E etat, int col,  int lig)
   {    int [] pos= etat.pos; 
        if (etat.nbposees == pos.length) return false;
        for( int c=0; c < etat.nbposees; c++)
	 if (pos[c]==lig || Math.abs(col-c) == Math.abs( pos[c]-lig)) return false;
	return true; 
   }
   static E poser( E ech,  int col, int lig) // cree un E comme ech avec une reine en plusse
   {   E e= creer( ech.pos.length );
       assert( col == ech.nbposees) ;
       e.nbposees = ech.nbposees + 1;
       for (int i=0; i< ech.nbposees; i++) e.pos[i] = ech.pos[i];
       e.pos[ col] = lig; return e; 
   }
   static L fistons(  E s, L stack) // empile les fils d'un etat; la pile est une L
   { int n = s.pos.length;
     if (s.nbposees == n) return stack;
     for (int l=0; l < n ; l++ )  // pour ttes les lignes possibles
         if (possible( s, s.nbposees, l)) 
		stack = L.cons( E.poser( s, s.nbposees, l), stack); 
     return stack; 
   }
   static void print( E e)
   { for( int i=0; i < e.pos.length; i++) System.out.print( e.pos[i] + " ");
     System.out.println(); 
   }
   static boolean est_solution( E e) { return e.nbposees == e.pos.length; }
}

class Reine {
   static L solve( int n) // rend la liste des solutions au probleme des n reines
   {    L stack= L.cons( E.creer( n), L.nil ); L solutions = L.nil;
        for( ; ! L.isempty( stack); )
        { 	E e = L.head( stack); stack = L.tail( stack); // depiler
 		if (E.est_solution( e)) solutions= L.cons( e, solutions);
		else stack= E.fistons( e,  stack) ;
	}
        return solutions;
   }   
   public static void main (String[] args)
   {	int n= 8;
        if (args.length==1) n=Integer.parseInt(args[0]); 
        L solutions=solve( n); 
	L l = solutions ;
	for( int num=1; ! L.isempty( l); l= L.tail(l), num++) 
	{ E sol= L.head( l); System.out.print( num + " : "); E.print( sol); } 
   }
}
