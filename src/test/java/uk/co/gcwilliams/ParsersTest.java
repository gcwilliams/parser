package uk.co.gcwilliams;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.gcwilliams.parser.combinator.Parser;
import uk.co.gcwilliams.parser.combinator.ParserResult;
import uk.co.gcwilliams.parser.combinator.Parsers;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The parsers test
 *
 * @author : Gareth Williams
 **/
class ParsersTest {

    static Stream<Arguments> isChar() {
        return Stream.of(
            Arguments.of("a", ParserResult.success("a", "")),
            Arguments.of("1", ParserResult.failure("1"))
        );
    }

    @ParameterizedTest
    @MethodSource("isChar")
    <T> void isChar(
        String source,
        ParserResult<T> expected
    ) {

        // act
        ParserResult<String> result = Parsers.is('a').parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    static Stream<Arguments> charPredicate() {
        return Stream.of(
            Arguments.of("1", ParserResult.success("1", "")),
            Arguments.of("a", ParserResult.failure("a"))
        );
    }

    @ParameterizedTest
    @MethodSource("charPredicate")
    <T> void charPredicate(
        String source,
        ParserResult<T> expected
    ) {

        // act
        ParserResult<String> result = Parsers.is(Character::isDigit).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    static Stream<Arguments> isString() {
        return Stream.of(
                Arguments.of("parser", ParserResult.success("parser", "")),
                Arguments.of("combinator", ParserResult.failure("combinator"))
        );
    }

    @ParameterizedTest
    @MethodSource("isString")
    <T> void isString(
        String source,
        ParserResult<T> expected
    ) {

        // act
        ParserResult<String> result = Parsers.is("parser").parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    static Stream<Arguments> eof() {
        return Stream.of(
            Arguments.of("", ParserResult.success("", "")),
            Arguments.of("parser", ParserResult.failure("parser"))
        );
    }

    @ParameterizedTest
    @MethodSource("eof")
    <T> void eof(
        String source,
        ParserResult<T> expected
    ) {

        // act
        ParserResult<Void> result = Parsers.eof().parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    static Stream<Arguments> many() {
        return Stream.of(
            Arguments.of("bbbb", ParserResult.success(List.of(), "bbbb")),
            Arguments.of("abbb", ParserResult.success(List.of("a"), "bbb")),
            Arguments.of("aabb", ParserResult.success(List.of("a", "a"), "bb"))
        );
    }

    @ParameterizedTest
    @MethodSource("many")
    <T> void many(
        String source,
        ParserResult<T> expected
    ) {

        // act
        ParserResult<List<String>> result = Parsers.many(Parsers.is('a')).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    static Stream<Arguments> many1() {
        return Stream.of(
            Arguments.of("bbbb", ParserResult.failure("bbbb")),
            Arguments.of("abbb", ParserResult.success(List.of("a"), "bbb")),
            Arguments.of("aabb", ParserResult.success(List.of("a", "a"), "bb"))
        );
    }

    @ParameterizedTest
    @MethodSource("many1")
    <T> void many1(
        String source,
        ParserResult<T> expected
    ) {

        // act
        ParserResult<List<String>> result = Parsers.many1(Parsers.is('a')).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    static Stream<Arguments> times() {
        return Stream.of(
            Arguments.of("bbbb", ParserResult.failure("bbbb")),
            Arguments.of("abbb", ParserResult.failure("abbb")),
            Arguments.of("aabb", ParserResult.success(List.of("a", "a"), "bb")),
            Arguments.of("aaab", ParserResult.success(List.of("a", "a"), "ab"))
        );
    }

    @ParameterizedTest
    @MethodSource("times")
    <T> void times(
        String source,
        ParserResult<T> expected
    ) {

        // act
        ParserResult<List<String>> result = Parsers.times(Parsers.is('a'), 2).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    static Stream<Arguments> between() {
        return Stream.of(
            Arguments.of("bbbb", ParserResult.failure("bbbb")),
            Arguments.of("abbb", ParserResult.success(List.of("a"), "bbb")),
            Arguments.of("aabb", ParserResult.success(List.of("a", "a"), "bb")),
            Arguments.of("aaab", ParserResult.success(List.of("a", "a", "a"), "b")),
            Arguments.of("aaaa", ParserResult.success(List.of("a", "a", "a"), "a"))
        );
    }

    @ParameterizedTest
    @MethodSource("between")
    <T> void between(
        String source,
        ParserResult<T> expected
    ) {

        // act
        ParserResult<List<String>> result = Parsers.times(Parsers.is('a'), 1, 3).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    static Stream<Arguments> or() {
        return Stream.of(
            Arguments.of("1", ParserResult.success("1", "")),
            Arguments.of("2", ParserResult.success("2", "")),
            Arguments.of("3", ParserResult.failure("3"))
        );
    }

    @ParameterizedTest
    @MethodSource("or")
    <T> void or(
        String source,
        ParserResult<T> expected
    ) {

        // act
        ParserResult<String> result = Parsers.or(Parsers.is('1'), Parsers.is('2')).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    static Stream<Arguments> sequence() {
        return Stream.of(
            Arguments.of("12", ParserResult.success(List.of("1", "2"), "")),
            Arguments.of("23", ParserResult.failure("23"))
        );
    }

    @ParameterizedTest
    @MethodSource("sequence")
    <T> void sequence(
        String source,
        ParserResult<List<T>> expected
    ) {

        // act
        ParserResult<List<String>> result = Parsers.sequence(Parsers.is('1'), Parsers.is('2')).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    void sequence2() {

        // arrange
        String source = "123";

        // act
        ParserResult<String> result = Parsers.sequence(
            Parsers.is('1'),
            Parsers.is('2'),
            (__, ___) -> __ + ___
        ).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(ParserResult.success("12", "3"));
    }

    @Test
    void sequence3() {

        // arrange
        String source = "1234";

        // act
        ParserResult<String> result = Parsers.sequence(
            Parsers.is('1'),
            Parsers.is('2'),
            Parsers.is('3'),
            (__, ___, ____) -> __ + ___ + ____
        ).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(ParserResult.success("123", "4"));
    }

    @Test
    void sequence4() {

        // arrange
        String source = "12345";

        // act
        ParserResult<String> result = Parsers.sequence(
            Parsers.is('1'),
            Parsers.is('2'),
            Parsers.is('3'),
            Parsers.is('4'),
            (__, ___, ____, _____) -> __ + ___ + ____ + _____
        ).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(ParserResult.success("1234", "5"));
    }

    @Test
    void sequence5() {

        // arrange
        String source = "123456";

        // act
        ParserResult<String> result = Parsers.sequence(
            Parsers.is('1'),
            Parsers.is('2'),
            Parsers.is('3'),
            Parsers.is('4'),
            Parsers.is('5'),
            (__, ___, ____, _____, ______) -> __ + ___ + ____ + _____ + ______
        ).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(ParserResult.success("12345", "6"));
    }

    @Test
    void sequence6() {

        // arrange
        String source = "1234567";

        // act
        ParserResult<String> result = Parsers.sequence(
            Parsers.is('1'),
            Parsers.is('2'),
            Parsers.is('3'),
            Parsers.is('4'),
            Parsers.is('5'),
            Parsers.is('6'),
            (__, ___, ____, _____, ______, _______) -> __ + ___ + ____ + _____ + ______ + _______
        ).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(ParserResult.success("123456", "7"));
    }

    @Test
    void sequence7() {

        // arrange
        String source = "12345678";

        // act
        ParserResult<String> result = Parsers.sequence(
                Parsers.is('1'),
                Parsers.is('2'),
                Parsers.is('3'),
                Parsers.is('4'),
                Parsers.is('5'),
                Parsers.is('6'),
                Parsers.is('7'),
                (__, ___, ____, _____, ______, _______, ________) -> __ + ___ + ____ + _____ + ______ + _______ + ________
        ).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(ParserResult.success("1234567", "8"));
    }

    @Test
    void sequence8() {

        // arrange
        String source = "123456789";

        // act
        ParserResult<String> result = Parsers.sequence(
            Parsers.is('1'),
            Parsers.is('2'),
            Parsers.is('3'),
            Parsers.is('4'),
            Parsers.is('5'),
            Parsers.is('6'),
            Parsers.is('7'),
            Parsers.is('8'),
            (__, ___, ____, _____, ______, _______, ________, _________) -> __ + ___ + ____ + _____ + ______ + _______ + ________ + _________
        ).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(ParserResult.success("12345678", "9"));
    }

    @Test
    void sequence9() {

        // arrange
        String source = "1234567890";

        // act
        ParserResult<String> result = Parsers.sequence(
            Parsers.is('1'),
            Parsers.is('2'),
            Parsers.is('3'),
            Parsers.is('4'),
            Parsers.is('5'),
            Parsers.is('6'),
            Parsers.is('7'),
            Parsers.is('8'),
            Parsers.is('9'),
            (__, ___, ____, _____, ______, _______, ________, _________, __________) -> __ + ___ + ____ + _____ + ______ + _______ + ________ + _________ + __________
        ).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(ParserResult.success("123456789", "0"));
    }

    @Test
    void infixl() {

        // arrange
        Parser<String> letter = Parsers.is(Character::isAlphabetic);
        Parser<String> whitespace = Parsers.or(Parsers.is(Character::isWhitespace), Parsers.constant());

        Parser<String> parser = Parsers.infixl(letter, whitespace, (l, op, r) -> r + op + l);

        // act
        ParserResult<String> result = parser.parse("Hello World");

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(ParserResult.success("dlroW olleH", ""));
    }

    @Test
    void infix() {

        // arrange
        Parser<String> letter = Parsers.is(Character::isAlphabetic);
        Parser<String> whitespace = Parsers.or(Parsers.is(Character::isWhitespace), Parsers.constant());

        Parser<String> parser = Parsers.infixr(letter, whitespace, (l, op, r) -> l + op + r);

        // act
        ParserResult<String> result = parser.parse("Hello World");

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(ParserResult.success("dlroW olleH", ""));
    }

    @Test
    void precedence() {

        // arrange
        Parser<Integer> digit = Parsers.map(Parsers.many1(Parsers.is(Character::isDigit)), values -> Integer.parseInt(String.join("", values)));
        Parser<String> plus = Parsers.is('+');
        Parser<String> times = Parsers.is('*');

        Parser<Integer> parser;
        parser = Parsers.infixl(digit, times, (l, op, r) -> l * r);
        parser = Parsers.infixl(parser, plus, (l, op, r) -> l + r);

        // act
        ParserResult<Integer> result = parser.parse("1+10*2");

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(ParserResult.success(21, ""));
    }

    @Test
    void reference() {

        // arrange
        Parser<Integer> digit = Parsers.map(Parsers.many1(Parsers.is(Character::isDigit)), values -> Integer.parseInt(String.join("", values)));
        Parser<String> times = Parsers.is('*');
        Parser<String> divide = Parsers.is('/');
        Parser<String> plus = Parsers.is('+');
        Parser<String> minus = Parsers.is('-');

        Parser<String> open = Parsers.is('(');
        Parser<String> closed = Parsers.is(')');

        Parsers.Lazy<Integer> lazy = Parsers.lazy();

        Parser<Integer> parser = Parsers.or(lazy.ref().between(open, closed), digit);
        parser = Parsers.infixl(parser, times, (l, op, r) -> l * r);
        parser = Parsers.infixl(parser, divide, (l, op, r) -> l / r);
        parser = Parsers.infixl(parser, plus, (l, op, r) -> l + r);
        parser = Parsers.infixl(parser, minus, (l, op, r) -> l - r);

        lazy.set(parser);

        // act
        ParserResult<Integer> result = parser.parse("((1+4/2)*123-9)*100");

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(ParserResult.success(((1+4/2)*123-9)*100, ""));
    }

    static Stream<Arguments> not() {
        return Stream.of(
            Arguments.of("bbbb", ParserResult.success("bbbb", "")),
            Arguments.of("bbaa", ParserResult.success("bb", "aa")),
            Arguments.of("aaaa", ParserResult.success("", "aaaa"))
        );
    }

    @ParameterizedTest
    @MethodSource("not")
    <T> void not(
        String source,
        ParserResult<T> expected
    ) {

        // arrange
        Parser<String> not = Parsers.not(Parsers.is("a"));

        // act
        ParserResult<String> result = not.parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    static Stream<Arguments> until() {
        return Stream.of(
            Arguments.of("/** This is a comment */", ParserResult.success("This is a comment", "")),
            Arguments.of("/** This is a comment ", ParserResult.failure("/** This is a comment "))
        );
    }

    @ParameterizedTest
    @MethodSource("until")
    <T> void until(
        String source,
        ParserResult<T> expected
    ) {

        // arrange
        Parser<String> commentStart = Parsers.is("/**");
        Parser<String> commentEnd = Parsers.is("*/");
        Parser<String> comment = Parsers.until(commentStart, commentEnd, (__, v, ___) -> v.trim());

        // act
        ParserResult<String> result = comment.parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    void tokenizes() {

        // assert
        Parser<String> integer = Parsers.is(Character::isDigit).many1().map(values -> String.join("", values));
        Parser<String> plus = Parsers.is('+');
        Parser<String> minus = Parsers.is('-');
        Parser<String> tokens = Parsers.or(integer, plus, minus);

        Parser<String> whitespace = Parsers.is(Character::isWhitespace);
        Parser<String> comment = Parsers.until(Parsers.is("/**"), Parsers.is("*/"), (b, c, u) -> b + c + u);
        Parser<List<String>> ignore = Parsers.or(whitespace, comment).many();

        Parser<List<String>> parser = Parsers.tokenize(tokens, ignore);

        // act
        ParserResult<List<String>> result = parser.parse("1 + 2 - 3 /** comment */   + 2 3 4    333");

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(ParserResult.success(
                List.of("1", "+", "2", "-", "3", "+", "2", "3", "4", "333"),
                ""
            ));
    }
}
