public class L {
        Object head;
        L tail;
        public static L nil=null;
        public static boolean isempty( L l) { return null==l; }
        public static Object hd( L l) { return l.head; }
        public static L tl( L l) { return l.tail; }
        public static L cons( Object o, L q) { L l=new L(); l.head=o; l.tail=q; return l;}
}

