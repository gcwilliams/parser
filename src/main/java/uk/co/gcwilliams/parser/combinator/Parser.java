package uk.co.gcwilliams.parser.combinator;

import uk.co.gcwilliams.parser.combinator.Mapper.Mapper3;

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
    default Parser<List<T>> times(int minimum, int maximum) {
        return Parsers.times(this, minimum, maximum);
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

    /**
     * Parser the input and returns the value in between
     *
     * <pre>{@code
     * Parser.is("\"Hello\"").between(Parsers.is('"'));
     * }</pre>
     *
     * @param parser the parser for the source before and after the value
     * @return the parser
     */
    default <O> Parser<T> between(Parser<O> parser) {
        return between(parser, parser);
    }

    /**
     * Parser the input and returns the value in between
     *
     * <pre>{@code
     * Parser.is(Character::isDigit).between(Parsers.is('('), Parsers.is(')'));
     * }</pre>
     *
     * @param left the left parser
     * @param right the right parser
     * @return the parser
     */
    default <O1, O2> Parser<T> between(Parser<O1> left, Parser<O2> right) {
        return Parsers.sequence(left, this, right, (__, ___, ____) -> ___);
    }

    /**
     * A parser that starts with begin, consumes the input until the specified parser succeeds, and maps the result
     *
     * @param until the until parser
     * @param map the mapper
     * @return the parser
     */
    default <U, R> Parser<R> until(Parser<U> until, Mapper3<T, String, U, R> map) {
        return Parsers.until(this, until, map);
    }

    /**
     * Creates a token parser from the current parser
     *
     */
    default TokenParser<T> token() {
        return TokenParsers.token(this);
    }
}
