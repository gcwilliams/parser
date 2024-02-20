package uk.co.gcwilliams.parser.combinator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static uk.co.gcwilliams.parser.combinator.Mapper.Mapper2;
import static uk.co.gcwilliams.parser.combinator.Mapper.Mapper3;
import static uk.co.gcwilliams.parser.combinator.Mapper.Mapper4;
import static uk.co.gcwilliams.parser.combinator.Mapper.Mapper5;
import static uk.co.gcwilliams.parser.combinator.Mapper.Mapper6;
import static uk.co.gcwilliams.parser.combinator.Mapper.Mapper7;
import static uk.co.gcwilliams.parser.combinator.Mapper.Mapper8;
import static uk.co.gcwilliams.parser.combinator.Mapper.Mapper9;

/**
 * The parsers
 *
 * @author : Gareth Williams
 **/
public class Parsers {

    private Parsers() { } // static

    /**
     * Parses the char, if the source begins with that character a successful result is returned, otherwise a failure
     * result is returned.
     *
     * @param expected the expected character
     * @return the parser
     */
    public static Parser<String> is(char expected) {
        return is(ch -> ch == expected);
    }

    /**
     * Parses a character which matches the prediciate, if the source matches the predicate a successful result is
     * returned, otherwise a failure result is returned.
     *
     * @param predicate the predicate
     * @return the parser
     */
    public static Parser<String> is(Predicate<Character> predicate) {
        return source -> source.length() > 0 && predicate.test(source.charAt(0))
            ? ParserResult.success(String.valueOf(source.charAt(0)), source.substring(1))
            : ParserResult.failure(source);
    }

    /**
     * Parses the string, if the source begins with the string a successful result is returned, otherwise a failure
     * result is returned.
     *
     * @param expected the expected string
     * @return the parser
     */
    public static Parser<String> is(String expected) {
        return source -> source.startsWith(expected)
            ? ParserResult.success(expected, source.substring(expected.length()))
            : ParserResult.failure(source);
    }

    /**
     * A parser that consumes no input always succeeds
     *
     * @return the parser
     */
    public static Parser<String> constant() {
        return source -> ParserResult.success("", source);
    }

    /**
     * A parser that succeeds only if there is no input remaining
     *
     * @return the parser
     */
    @SuppressWarnings("unchecked")
    public static Parser<Void> eof() {
        return source -> source.isEmpty()
            ? (ParserResult<Void>)(ParserResult<?>)ParserResult.success("", "")
            : ParserResult.failure(source);
    }

    /**
     * Maps the result of the parsing
     *
     * @param parser the parser
     * @param mapper the mapper
     * @return the parser
     */
    public static <T, R> Parser<R> map(Parser<T> parser, Function<T, R> mapper) {
        return source -> {
            ParserResult<T> result = parser.parse(source);
            return result.state() == ParserResult.State.SUCCESS
                ? ParserResult.success(mapper.apply(result.result().orElseThrow()), result.remaining())
                : ParserResult.failure(source);
        };
    }

    /**
     * Parses the input 0 or more times with the supplied parser
     *
     * @param parser the parser
     * @return the parser
     */
    public static <T> Parser<List<T>> many(Parser<T> parser) {
        return times(parser, 0, Integer.MAX_VALUE);
    }

    /**
     * Parses the input 1 or more times with the supplied parser
     *
     * @param parser the parser
     * @return the parser
     */
    public static <T> Parser<List<T>> many1(Parser<T> parser) {
        return times(parser, 1, Integer.MAX_VALUE);
    }

    /**
     * Parses the input the specified times with the supplied parser
     *
     * @param parser the parser
     * @param times the times
     * @return the parser
     */
    public static <T> Parser<List<T>> times(Parser<T> parser, int times) {
        return times(parser, times, times);
    }

    /**
     * Parses the input at least the minimum or up to the maximum number of times with the supplied parser
     *
     * @param parser the parser
     * @param minimum the minimum number of times
     * @param maximum the maximum number of times
     * @return the parser
     */
    public static <T> Parser<List<T>> times(Parser<T> parser, int minimum, int maximum) {
        return source -> {
            List<T> results = new ArrayList<>();
            String remaining = source;
            while (results.size() < maximum) {
                ParserResult<T> result = parser.parse(remaining);
                if (result.state() == ParserResult.State.FAILURE) {
                    break;
                }
                results.add(result.result().orElseThrow());
                remaining = result.remaining();
            }
            return results.size() >= minimum
                ? ParserResult.success(results, remaining)
                : ParserResult.failure(source);
        };
    }

    /**
     * Creates a parser which succeeds if one of the supplied parser succeeds
     *
     * @param parsers the parsers
     * @return the parser
     */
    @SafeVarargs
    public static <T> Parser<T> or(Parser<T>... parsers) {
        return source -> {
            for (Parser<T> parser : parsers) {
                ParserResult<T> result = parser.parse(source);
                if (result.state() == ParserResult.State.SUCCESS) {
                    return result;
                }
            }
            return ParserResult.failure(source);
        };
    }

    /**
     * Creates a parser which succeeds if all the supplied parsers succeed in order
     *
     * @param parsers the parsers
     * @return the parser
     */
    @SafeVarargs
    public static <T> Parser<List<T>> sequence(Parser<T>... parsers) {
        return source -> {
            List<T> results = new LinkedList<>();
            String remaining = source;
            for (Parser<T> parser : parsers) {
                ParserResult<T> result = parser.parse(remaining);
                if (result.state() == ParserResult.State.FAILURE) {
                    return ParserResult.failure(source);
                }
                results.add(result.result().orElseThrow());
                remaining = result.remaining();
            }
            return ParserResult.success(results, remaining);
        };
    }

    /**
     * Creates a parser which succeeds if all the supplied parsers succeed in order, the results are passed to
     * the mapper
     *
     * @param t1 Parser 1
     * @param t2 Parser 2
     * @param mapper The mapper
     * @return the parser
     */
    public static <T1, T2, R> Parser<R> sequence(
            Parser<T1> t1,
            Parser<T2> t2,
            Mapper2<T1, T2, R> mapper
    ) {
        return source -> {
            ParserResult<T1> t1Result = t1.parse(source);
            if (t1Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T2> t2Result = t2.parse(t1Result.remaining());
            if (t2Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            R mapped = mapper.apply(
                t1Result.result().orElseThrow(),
                t2Result.result().orElseThrow()
            );
            return ParserResult.success(mapped, t2Result.remaining());
        };
    }

    /**
     * Creates a parser which succeeds if all the supplied parsers succeed in order, the results are passed to
     * the mapper
     *
     * @param t1 Parser 1
     * @param t2 Parser 2
     * @param t3 Parser 3
     * @param mapper The mapper
     * @return the parser
     */
    public static <T1, T2, T3, R> Parser<R> sequence(
            Parser<T1> t1,
            Parser<T2> t2,
            Parser<T3> t3,
            Mapper3<T1, T2, T3, R> mapper
    ) {
        return source -> {
            ParserResult<T1> t1Result = t1.parse(source);
            if (t1Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T2> t2Result = t2.parse(t1Result.remaining());
            if (t2Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T3> t3Result = t3.parse(t2Result.remaining());
            if (t3Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            R mapped = mapper.apply(
                t1Result.result().orElseThrow(),
                t2Result.result().orElseThrow(),
                t3Result.result().orElseThrow()
            );
            return ParserResult.success(mapped, t3Result.remaining());
        };
    }

    /**
     * Creates a parser which succeeds if all the supplied parsers succeed in order, the results are passed to
     * the mapper
     *
     * @param t1 Parser 1
     * @param t2 Parser 2
     * @param t3 Parser 3
     * @param t4 Parser 4
     * @param mapper The mapper
     * @return the parser
     */
    public static <T1, T2, T3, T4, R> Parser<R> sequence(
            Parser<T1> t1,
            Parser<T2> t2,
            Parser<T3> t3,
            Parser<T4> t4,
            Mapper4<T1, T2, T3, T4, R> mapper
    ) {
        return source -> {
            ParserResult<T1> t1Result = t1.parse(source);
            if (t1Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T2> t2Result = t2.parse(t1Result.remaining());
            if (t2Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T3> t3Result = t3.parse(t2Result.remaining());
            if (t3Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T4> t4Result = t4.parse(t3Result.remaining());
            if (t4Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            R mapped = mapper.apply(
                t1Result.result().orElseThrow(),
                t2Result.result().orElseThrow(),
                t3Result.result().orElseThrow(),
                t4Result.result().orElseThrow()
            );
            return ParserResult.success(mapped, t4Result.remaining());
        };
    }

    /**
     * Creates a parser which succeeds if all the supplied parsers succeed in order, the results are passed to
     * the mapper
     *
     * @param t1 Parser 1
     * @param t2 Parser 2
     * @param t3 Parser 3
     * @param t4 Parser 4
     * @param t5 Parser 5
     * @param mapper The mapper
     * @return the parser
     */
    public static <T1, T2, T3, T4, T5, R> Parser<R> sequence(
            Parser<T1> t1,
            Parser<T2> t2,
            Parser<T3> t3,
            Parser<T4> t4,
            Parser<T5> t5,
            Mapper5<T1, T2, T3, T4, T5, R> mapper
    ) {
        return source -> {
            ParserResult<T1> t1Result = t1.parse(source);
            if (t1Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T2> t2Result = t2.parse(t1Result.remaining());
            if (t2Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T3> t3Result = t3.parse(t2Result.remaining());
            if (t3Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T4> t4Result = t4.parse(t3Result.remaining());
            if (t4Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T5> t5Result = t5.parse(t4Result.remaining());
            if (t5Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            R mapped = mapper.apply(
                t1Result.result().orElseThrow(),
                t2Result.result().orElseThrow(),
                t3Result.result().orElseThrow(),
                t4Result.result().orElseThrow(),
                t5Result.result().orElseThrow()
            );
            return ParserResult.success(mapped, t5Result.remaining());
        };
    }

    /**
     * Creates a parser which succeeds if all the supplied parsers succeed in order, the results are passed to
     * the mapper
     *
     * @param t1 Parser 1
     * @param t2 Parser 2
     * @param t3 Parser 3
     * @param t4 Parser 4
     * @param t5 Parser 5
     * @param t6 Parser 6
     * @param mapper The mapper
     * @return the parser
     */
    public static <T1, T2, T3, T4, T5, T6, R> Parser<R> sequence(
            Parser<T1> t1,
            Parser<T2> t2,
            Parser<T3> t3,
            Parser<T4> t4,
            Parser<T5> t5,
            Parser<T6> t6,
            Mapper6<T1, T2, T3, T4, T5, T6, R> mapper
    ) {
        return source -> {
            ParserResult<T1> t1Result = t1.parse(source);
            if (t1Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T2> t2Result = t2.parse(t1Result.remaining());
            if (t2Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T3> t3Result = t3.parse(t2Result.remaining());
            if (t3Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T4> t4Result = t4.parse(t3Result.remaining());
            if (t4Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T5> t5Result = t5.parse(t4Result.remaining());
            if (t5Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T6> t6Result = t6.parse(t5Result.remaining());
            if (t6Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            R mapped = mapper.apply(
                t1Result.result().orElseThrow(),
                t2Result.result().orElseThrow(),
                t3Result.result().orElseThrow(),
                t4Result.result().orElseThrow(),
                t5Result.result().orElseThrow(),
                t6Result.result().orElseThrow()
            );
            return ParserResult.success(mapped, t6Result.remaining());
        };
    }

    /**
     * Creates a parser which succeeds if all the supplied parsers succeed in order, the results are passed to
     * the mapper
     *
     * @param t1 Parser 1
     * @param t2 Parser 2
     * @param t3 Parser 3
     * @param t4 Parser 4
     * @param t5 Parser 5
     * @param t6 Parser 6
     * @param t7 Parser 7
     * @param mapper The mapper
     * @return the parser
     */
    public static <T1, T2, T3, T4, T5, T6, T7, R> Parser<R> sequence(
            Parser<T1> t1,
            Parser<T2> t2,
            Parser<T3> t3,
            Parser<T4> t4,
            Parser<T5> t5,
            Parser<T6> t6,
            Parser<T7> t7,
            Mapper7<T1, T2, T3, T4, T5, T6, T7, R> mapper
    ) {
        return source -> {
            ParserResult<T1> t1Result = t1.parse(source);
            if (t1Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T2> t2Result = t2.parse(t1Result.remaining());
            if (t2Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T3> t3Result = t3.parse(t2Result.remaining());
            if (t3Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T4> t4Result = t4.parse(t3Result.remaining());
            if (t4Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T5> t5Result = t5.parse(t4Result.remaining());
            if (t5Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T6> t6Result = t6.parse(t5Result.remaining());
            if (t6Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T7> t7Result = t7.parse(t6Result.remaining());
            if (t7Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            R mapped = mapper.apply(
                t1Result.result().orElseThrow(),
                t2Result.result().orElseThrow(),
                t3Result.result().orElseThrow(),
                t4Result.result().orElseThrow(),
                t5Result.result().orElseThrow(),
                t6Result.result().orElseThrow(),
                t7Result.result().orElseThrow()
            );
            return ParserResult.success(mapped, t7Result.remaining());
        };
    }

    /**
     * Creates a parser which succeeds if all the supplied parsers succeed in order, the results are passed to
     * the mapper
     *
     * @param t1 Parser 1
     * @param t2 Parser 2
     * @param t3 Parser 3
     * @param t4 Parser 4
     * @param t5 Parser 5
     * @param t6 Parser 6
     * @param t7 Parser 7
     * @param mapper The mapper
     * @return the parser
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8, R> Parser<R> sequence(
            Parser<T1> t1,
            Parser<T2> t2,
            Parser<T3> t3,
            Parser<T4> t4,
            Parser<T5> t5,
            Parser<T6> t6,
            Parser<T7> t7,
            Parser<T8> t8,
            Mapper8<T1, T2, T3, T4, T5, T6, T7, T8, R> mapper
    ) {
        return source -> {
            ParserResult<T1> t1Result = t1.parse(source);
            if (t1Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T2> t2Result = t2.parse(t1Result.remaining());
            if (t2Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T3> t3Result = t3.parse(t2Result.remaining());
            if (t3Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T4> t4Result = t4.parse(t3Result.remaining());
            if (t4Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T5> t5Result = t5.parse(t4Result.remaining());
            if (t5Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T6> t6Result = t6.parse(t5Result.remaining());
            if (t6Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T7> t7Result = t7.parse(t6Result.remaining());
            if (t7Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T8> t8Result = t8.parse(t7Result.remaining());
            if (t8Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            R mapped = mapper.apply(
                t1Result.result().orElseThrow(),
                t2Result.result().orElseThrow(),
                t3Result.result().orElseThrow(),
                t4Result.result().orElseThrow(),
                t5Result.result().orElseThrow(),
                t6Result.result().orElseThrow(),
                t7Result.result().orElseThrow(),
                t8Result.result().orElseThrow()
            );
            return ParserResult.success(mapped, t8Result.remaining());
        };
    }

    /**
     * Creates a parser which succeeds if all the supplied parsers succeed in order, the results are passed to
     * the mapper
     *
     * @param t1 Parser 1
     * @param t2 Parser 2
     * @param t3 Parser 3
     * @param t4 Parser 4
     * @param t5 Parser 5
     * @param t6 Parser 6
     * @param t7 Parser 7
     * @param mapper The mapper
     * @return the parser
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> Parser<R> sequence(
            Parser<T1> t1,
            Parser<T2> t2,
            Parser<T3> t3,
            Parser<T4> t4,
            Parser<T5> t5,
            Parser<T6> t6,
            Parser<T7> t7,
            Parser<T8> t8,
            Parser<T9> t9,
            Mapper9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> mapper
    ) {
        return source -> {
            ParserResult<T1> t1Result = t1.parse(source);
            if (t1Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T2> t2Result = t2.parse(t1Result.remaining());
            if (t2Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T3> t3Result = t3.parse(t2Result.remaining());
            if (t3Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T4> t4Result = t4.parse(t3Result.remaining());
            if (t4Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T5> t5Result = t5.parse(t4Result.remaining());
            if (t5Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T6> t6Result = t6.parse(t5Result.remaining());
            if (t6Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T7> t7Result = t7.parse(t6Result.remaining());
            if (t7Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T8> t8Result = t8.parse(t7Result.remaining());
            if (t8Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            ParserResult<T9> t9Result = t9.parse(t8Result.remaining());
            if (t9Result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            R mapped = mapper.apply(
                t1Result.result().orElseThrow(),
                t2Result.result().orElseThrow(),
                t3Result.result().orElseThrow(),
                t4Result.result().orElseThrow(),
                t5Result.result().orElseThrow(),
                t6Result.result().orElseThrow(),
                t7Result.result().orElseThrow(),
                t8Result.result().orElseThrow(),
                t9Result.result().orElseThrow()
            );
            return ParserResult.success(mapped, t9Result.remaining());
        };
    }

    /**
     * Creates a infix left parser from the supplied parser and the infix operator
     *
     * @param parser the parser
     * @param infix the infix operator
     * @param mapper the mapper
     * @return the parser
     */
    public static <T, I> Parser<T> infixl(Parser<T> parser, Parser<I> infix, Mapper3<T, I, T, T> mapper) {
        record Rhs<T, I>(I op, T value) { }
        return sequence(parser, many(sequence(infix, parser, Rhs::new)), (value, rhs) -> {
            T result = value;
            for (Rhs<T, I> r : rhs) {
                result = mapper.apply(result, r.op(), r.value());
            }
            return result;
        });
    }

    /**
     * Creates a infix right parser from the supplied parser and the infix operator
     *
     * @param parser the parser
     * @param infix the infix operator
     * @param mapper the mapper
     * @return the parser
     */
    public static <T, I> Parser<T> infixr(Parser<T> parser, Parser<I> infix, Mapper3<T, I, T, T> mapper) {
        record Rhs<T, I>(I op, T value) { }
        return sequence(parser, many(sequence(infix, parser, Rhs::new)), (value, rhs) -> {
            if (rhs.isEmpty()) {
                return value;
            }
            T result = rhs.get(rhs.size() - 1).value();
            for (int idx = rhs.size() - 2; idx >= 0; idx--) {
                T next = rhs.get(idx).value();
                I op = rhs.get(idx + 1).op();
                result = mapper.apply(result, op, next);
            }
            return mapper.apply(result, rhs.get(0).op(), value);
        });
    }

    /**
     * The lazy parser
     *
     */
    public interface Lazy<T> {

        /**
         * Gets the parser reference
         *
         * @return the reference
         */
        Parser<T> ref();

        /**
         * Sets the parser
         *
         * @param parser the parser
         */
        void set(Parser<T> parser);
    }

    /**
     * Creates a reference for a parser which can be set later
     *
     * @return the reference
     */
    public static <T> Lazy<T> lazy() {
        return new LazyImpl<>();
    }

    /**
     * The reference implementation
     *
     */
    private static class LazyImpl<T> implements Lazy<T>, Parser<T> {

        private final AtomicReference<Parser<T>> reference = new AtomicReference<>();

        @Override
        public ParserResult<T> parse(String source) {
            return requireNonNull(reference.get(), "Parser not set").parse(source);
        }

        @Override
        public Parser<T> ref() {
            return this;
        }

        @Override
        public void set(Parser<T> parser) {
            reference.set(parser);
        }
    }

    /**
     * A parser that continually succeeds until the supplied parser succeeds, fails if EOF is reached.
     *
     * @param parser the parser
     * @return the parser
     */
    public static <T> Parser<String> not(Parser<T> parser) {
        Parser<String> not = source -> {
            if (source.isEmpty()) {
                return ParserResult.failure(source);
            }
            ParserResult<T> result = parser.parse(source);
            return result.state() == ParserResult.State.SUCCESS
                ? ParserResult.failure(source)
                : ParserResult.success(Character.toString(source.charAt(0)), source.substring(1));
        };
        return not.many().map(v -> String.join("", v));
    }

    /**
     * A parser that starts with begin, consumes the input until the specified parser succeeds, and maps the result
     *
     * @param begin the begin parser
     * @param until the until parser
     * @param map the mapper
     * @return the parser
     */
    public static <T, U, R> Parser<R> until(Parser<T> begin, Parser<U> until, Mapper3<T, String, U, R> map) {
        return sequence(begin, not(until), until, (p, vs, u) -> map.apply(p, String.join("", vs), u));
    }
}
