package uk.co.gcwilliams.parser.combinator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The token parsers
 *
 * @author : Gareth Williams
 **/
public class TokenParsers {

    private TokenParsers() { // static
    }

    /**
     * Creates a token parser from the supplied parser
     *
     * @param parser the token parser
     * @return the token parser
     */
    public static <T> TokenParser<T> token(Parser<T> parser) {
        return ctx -> {
            if (ctx.token().isEmpty()) {
                return TokenParserResult.failure(ctx);
            }
            ParserResult<T> result = parser.parse(ctx.token().orElseThrow());
            return result.remaining().isEmpty()
                ? TokenParserResult.success(result.result().orElseThrow(), TokenContext.next(ctx))
                : TokenParserResult.failure(ctx);
        };
    }

    /**
     * Creates a token parser which succeeds if one of the supplied parser succeeds
     *
     * @param parsers the token parsers
     * @return the token parser
     */
    @SafeVarargs
    public static <T> TokenParser<T> or(TokenParser<T>... parsers) {
        return ctx -> {
            for (TokenParser<T> parser : parsers) {
                TokenParserResult<T> result = parser.parse(ctx);
                if (result.state() == TokenParserResult.State.SUCCESS) {
                    return result;
                }
            }
            return TokenParserResult.failure(ctx);
        };
    }

    /**
     * Parses the input 0 or more times with the supplied parser
     *
     * @param parser the parser
     * @return the parser
     */
    public static <T> TokenParser<List<T>> many(TokenParser<T> parser) {
        return times(parser, 0, Integer.MAX_VALUE);
    }

    /**
     * Parses the input 1 or more times with the supplied parser
     *
     * @param parser the parser
     * @return the parser
     */
    public static <T> TokenParser<List<T>> many1(TokenParser<T> parser) {
        return times(parser, 1, Integer.MAX_VALUE);
    }

    /**
     * Parses the input the specified times with the supplied parser
     *
     * @param parser the parser
     * @param times the times
     * @return the parser
     */
    public static <T> TokenParser<List<T>> times(TokenParser<T> parser, int times) {
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
    public static <T> TokenParser<List<T>> times(TokenParser<T> parser, int minimum, int maximum) {
        return ctx -> {
            List<T> results = new ArrayList<>();
            TokenContext next = ctx;
            while (results.size() < maximum) {
                TokenParserResult<T> result = parser.parse(next);
                if (result.state() == TokenParserResult.State.FAILURE) {
                    break;
                }
                results.add(result.result().orElseThrow());
                next = result.ctx();
            }
            return results.size() >= minimum
                ? TokenParserResult.success(results, next)
                : TokenParserResult.failure(ctx);
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
    public static <T1, T2, R> TokenParser<R> sequence(
        TokenParser<T1> t1,
        TokenParser<T2> t2,
        Mapper.Mapper2<T1, T2, R> mapper
    ) {
        return ctx -> {
            TokenParserResult<T1> t1Result = t1.parse(ctx);
            if (t1Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T2> t2Result = t2.parse(t1Result.ctx());
            if (t2Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            R mapped = mapper.apply(
                t1Result.result().orElseThrow(),
                t2Result.result().orElseThrow()
            );
            return TokenParserResult.success(mapped, t2Result.ctx());
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
    public static <T1, T2, T3, R> TokenParser<R> sequence(
        TokenParser<T1> t1,
        TokenParser<T2> t2,
        TokenParser<T3> t3,
        Mapper.Mapper3<T1, T2, T3, R> mapper
    ) {
        return ctx -> {
            TokenParserResult<T1> t1Result = t1.parse(ctx);
            if (t1Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T2> t2Result = t2.parse(t1Result.ctx());
            if (t2Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T3> t3Result = t3.parse(t2Result.ctx());
            if (t3Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            R mapped = mapper.apply(
                t1Result.result().orElseThrow(),
                t2Result.result().orElseThrow(),
                t3Result.result().orElseThrow()
            );
            return TokenParserResult.success(mapped, t2Result.ctx());
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
    public static <T1, T2, T3, T4, R> TokenParser<R> sequence(
        TokenParser<T1> t1,
        TokenParser<T2> t2,
        TokenParser<T3> t3,
        TokenParser<T4> t4,
        Mapper.Mapper4<T1, T2, T3, T4, R> mapper
    ) {
        return ctx -> {
            TokenParserResult<T1> t1Result = t1.parse(ctx);
            if (t1Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T2> t2Result = t2.parse(t1Result.ctx());
            if (t2Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T3> t3Result = t3.parse(t2Result.ctx());
            if (t3Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T4> t4Result = t4.parse(t3Result.ctx());
            if (t4Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            R mapped = mapper.apply(
                t1Result.result().orElseThrow(),
                t2Result.result().orElseThrow(),
                t3Result.result().orElseThrow(),
                t4Result.result().orElseThrow()
            );
            return TokenParserResult.success(mapped, t4Result.ctx());
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
    public static <T1, T2, T3, T4, T5, R> TokenParser<R> sequence(
        TokenParser<T1> t1,
        TokenParser<T2> t2,
        TokenParser<T3> t3,
        TokenParser<T4> t4,
        TokenParser<T5> t5,
        Mapper.Mapper5<T1, T2, T3, T4, T5, R> mapper
    ) {
        return ctx -> {
            TokenParserResult<T1> t1Result = t1.parse(ctx);
            if (t1Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T2> t2Result = t2.parse(t1Result.ctx());
            if (t2Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T3> t3Result = t3.parse(t2Result.ctx());
            if (t3Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T4> t4Result = t4.parse(t3Result.ctx());
            if (t4Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T5> t5Result = t5.parse(t4Result.ctx());
            if (t5Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            R mapped = mapper.apply(
                t1Result.result().orElseThrow(),
                t2Result.result().orElseThrow(),
                t3Result.result().orElseThrow(),
                t4Result.result().orElseThrow(),
                t5Result.result().orElseThrow()
            );
            return TokenParserResult.success(mapped, t5Result.ctx());
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
    public static <T1, T2, T3, T4, T5, T6, R> TokenParser<R> sequence(
        TokenParser<T1> t1,
        TokenParser<T2> t2,
        TokenParser<T3> t3,
        TokenParser<T4> t4,
        TokenParser<T5> t5,
        TokenParser<T6> t6,
        Mapper.Mapper6<T1, T2, T3, T4, T5, T6, R> mapper
    ) {
        return ctx -> {
            TokenParserResult<T1> t1Result = t1.parse(ctx);
            if (t1Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T2> t2Result = t2.parse(t1Result.ctx());
            if (t2Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T3> t3Result = t3.parse(t2Result.ctx());
            if (t3Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T4> t4Result = t4.parse(t3Result.ctx());
            if (t4Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T5> t5Result = t5.parse(t4Result.ctx());
            if (t5Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T6> t6Result = t6.parse(t5Result.ctx());
            if (t6Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            R mapped = mapper.apply(
                t1Result.result().orElseThrow(),
                t2Result.result().orElseThrow(),
                t3Result.result().orElseThrow(),
                t4Result.result().orElseThrow(),
                t5Result.result().orElseThrow(),
                t6Result.result().orElseThrow()
            );
            return TokenParserResult.success(mapped, t6Result.ctx());
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
    public static <T1, T2, T3, T4, T5, T6, T7, R> TokenParser<R> sequence(
        TokenParser<T1> t1,
        TokenParser<T2> t2,
        TokenParser<T3> t3,
        TokenParser<T4> t4,
        TokenParser<T5> t5,
        TokenParser<T6> t6,
        TokenParser<T7> t7,
        Mapper.Mapper7<T1, T2, T3, T4, T5, T6, T7, R> mapper
    ) {
        return ctx -> {
            TokenParserResult<T1> t1Result = t1.parse(ctx);
            if (t1Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T2> t2Result = t2.parse(t1Result.ctx());
            if (t2Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T3> t3Result = t3.parse(t2Result.ctx());
            if (t3Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T4> t4Result = t4.parse(t3Result.ctx());
            if (t4Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T5> t5Result = t5.parse(t4Result.ctx());
            if (t5Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T6> t6Result = t6.parse(t5Result.ctx());
            if (t6Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T7> t7Result = t7.parse(t6Result.ctx());
            if (t7Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
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
            return TokenParserResult.success(mapped, t7Result.ctx());
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
    public static <T1, T2, T3, T4, T5, T6, T7, T8, R> TokenParser<R> sequence(
        TokenParser<T1> t1,
        TokenParser<T2> t2,
        TokenParser<T3> t3,
        TokenParser<T4> t4,
        TokenParser<T5> t5,
        TokenParser<T6> t6,
        TokenParser<T7> t7,
        TokenParser<T8> t8,
        Mapper.Mapper8<T1, T2, T3, T4, T5, T6, T7, T8, R> mapper
    ) {
        return ctx -> {
            TokenParserResult<T1> t1Result = t1.parse(ctx);
            if (t1Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T2> t2Result = t2.parse(t1Result.ctx());
            if (t2Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T3> t3Result = t3.parse(t2Result.ctx());
            if (t3Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T4> t4Result = t4.parse(t3Result.ctx());
            if (t4Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T5> t5Result = t5.parse(t4Result.ctx());
            if (t5Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T6> t6Result = t6.parse(t5Result.ctx());
            if (t6Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T7> t7Result = t7.parse(t6Result.ctx());
            if (t7Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T8> t8Result = t8.parse(t7Result.ctx());
            if (t8Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
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
            return TokenParserResult.success(mapped, t8Result.ctx());
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
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> TokenParser<R> sequence(
        TokenParser<T1> t1,
        TokenParser<T2> t2,
        TokenParser<T3> t3,
        TokenParser<T4> t4,
        TokenParser<T5> t5,
        TokenParser<T6> t6,
        TokenParser<T7> t7,
        TokenParser<T8> t8,
        TokenParser<T9> t9,
        Mapper.Mapper9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> mapper
    ) {
        return ctx -> {
            TokenParserResult<T1> t1Result = t1.parse(ctx);
            if (t1Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T2> t2Result = t2.parse(t1Result.ctx());
            if (t2Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T3> t3Result = t3.parse(t2Result.ctx());
            if (t3Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T4> t4Result = t4.parse(t3Result.ctx());
            if (t4Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T5> t5Result = t5.parse(t4Result.ctx());
            if (t5Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T6> t6Result = t6.parse(t5Result.ctx());
            if (t6Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T7> t7Result = t7.parse(t6Result.ctx());
            if (t7Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T8> t8Result = t8.parse(t7Result.ctx());
            if (t8Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
            }
            TokenParserResult<T9> t9Result = t9.parse(t8Result.ctx());
            if (t9Result.state() == TokenParserResult.State.FAILURE) {
                return TokenParserResult.failure(ctx);
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
            return TokenParserResult.success(mapped, t9Result.ctx());
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
    public static <T, I> TokenParser<T> infixl(TokenParser<T> parser, TokenParser<I> infix, Mapper.Mapper3<T, I, T, T> mapper) {
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
    public static <T, I> TokenParser<T> infixr(TokenParser<T> parser, TokenParser<I> infix, Mapper.Mapper3<T, I, T, T> mapper) {
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
     * Creates a parser which tokenizes the source input
     *
     * @param tokens the tokens
     * @param skip the parser to skip
     * @return the parser
     */
    public static <T> Parser<T> tokenize(Parser<String> tokens, Parser<?> skip, TokenParser<T> parser) {
        Mapper.Mapper2<String, List<String>, List<String>> combiner = (t, ts) -> Stream.concat(Stream.of(t), ts.stream()).collect(Collectors.toList());
        Parser<List<String>> tokenizer = Parsers.sequence(tokens, Parsers.many(Parsers.sequence(skip, tokens, (__, t) -> t)), combiner).followedBy(Parsers.eof());
        return source -> {
            ParserResult<List<String>> result = tokenizer.parse(source);
            if (result.state() == ParserResult.State.FAILURE) {
                return ParserResult.failure(source);
            }
            TokenParserResult<T> tokenResult = parser.parse(TokenContext.create(result.result().orElseThrow()));
            return tokenResult.state() == TokenParserResult.State.SUCCESS && tokenResult.ctx().token().isEmpty()
                ? ParserResult.success(tokenResult.result().orElseThrow(), "")
                : ParserResult.failure(source);
        };
    }
}
