package uk.co.gcwilliams.parser.combinator;

import java.util.List;
import java.util.function.Function;

/**
 * The parser
 *
 * @author : Gareth Williams
 **/
public interface Parser<T> {

    /**
     * Parses the source and returns the parser result
     *
     * @param source The source
     * @return the parser result
     */
    ParserResult<T> parse(String source);

    /**
     * Maps the result of the parsing
     *
     * @param mapper the mapper
     * @return the parser
     */
    default <R> Parser<R> map(Function<T, R> mapper) {
        return Parsers.map(this, mapper);
    }

    /**
     * Parses the input 0 or more times with the supplied parser
     *
     * @return the parser
     */
    default Parser<List<T>> many() {
        return Parsers.many(this);
    }

    /**
     * Parses the input 1 or more times with the supplied parser
     *
     * @return the parser
     */
    default Parser<List<T>> many1() {
        return Parsers.many1(this);
    }

    /**
     * Parses the input the specified times with the supplied parser
     *
     * @param times the times
     * @return the parser
     */
    default Parser<List<T>> times(int times) {
        return Parsers.times(this, times);
    }

    /**
     * Parses the input at least the minimum or up to the maximum number of times with the supplied parser
     *
     * @param minimum the minimum number of times
     * @param maximum the maximum number of times
     * @return the parser
     */
    default Parser<List<T>> between(int minimum, int maximum) {
        return Parsers.between(this, minimum, maximum);
    }

    /**
     * Parses the input followed by the specified parser (the result of which is discarded)
     *
     * <pre>{@code
     * Parser.is("Hello").followedBy(Parsers.eof());
     * }</pre>
     *
     * @param parser the followed by parser
     * @return the parser
     */
    default <O> Parser<T> followedBy(Parser<O> parser) {
        return Parsers.sequence(this, parser, (__, ___) -> __);
    }
}
