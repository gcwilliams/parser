package uk.co.gcwilliams.parser.combinator;

/**
 * The mapper functions
 *
 * @author : Gareth Williams
 **/
public interface Mapper {

    @FunctionalInterface
    interface Mapper2<T1, T2, R> { R apply(T1 t1, T2 t2); }

    @FunctionalInterface
    interface Mapper3<T1, T2, T3, R> { R apply(T1 t1, T2 t2, T3 t3); }

    @FunctionalInterface
    interface Mapper4<T1, T2, T3, T4, R> { R apply(T1 t1, T2 t2, T3 t3, T4 t4); }

    @FunctionalInterface
    interface Mapper5<T1, T2, T3, T4, T5, R> { R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5); }

    @FunctionalInterface
    interface Mapper6<T1, T2, T3, T4, T5, T6, R> { R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6); }

    @FunctionalInterface
    interface Mapper7<T1, T2, T3, T4, T5, T6, T7, R> { R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7); }

    @FunctionalInterface
    interface Mapper8<T1, T2, T3, T4, T5, T6, T7, T8, R> { R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8); }

    @FunctionalInterface
    interface Mapper9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> { R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9); }
}
