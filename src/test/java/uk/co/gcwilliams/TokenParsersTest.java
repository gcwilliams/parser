package uk.co.gcwilliams;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.gcwilliams.parser.combinator.Parser;
import uk.co.gcwilliams.parser.combinator.ParserResult;
import uk.co.gcwilliams.parser.combinator.Parsers;
import uk.co.gcwilliams.parser.combinator.TokenParser;
import uk.co.gcwilliams.parser.combinator.TokenParsers;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The token parsers tests
 *
 * @author : Gareth Williams
 **/
class TokenParsersTest {

    @Test
    void token1() {

        // arrange

        // ... parser
        Parser<String> hello = Parsers.is("Hello");
        Parser<String> world = Parsers.is("World");
        TokenParser<String> helloWorld = TokenParsers.sequence(hello.token(), world.token(), (h, w) -> h + w);

        // ... ignored
        Parser<String> whitespace = Parsers.is(Character::isWhitespace);
        Parser<String> comment = Parsers.until(Parsers.is("/**"), Parsers.is("*/"), (b, c, u) -> b + c + u);
        Parser<List<String>> ignore = Parsers.or(whitespace, comment).many();

        // ... token parser
        Parser<String> parser = TokenParsers.tokenize(Parsers.or(hello, world), ignore, helloWorld);

        // act
        ParserResult<String> parsed = parser.parse("Hello     /** comment   **/ World");

        // assert
        assertThat(parsed.state()).isEqualTo(ParserResult.State.SUCCESS);
        assertThat(parsed.result()).hasValue("HelloWorld");
        assertThat(parsed.remaining()).isEmpty();
    }

    static Stream<Arguments> token2() {
        return Stream.of(
            Arguments.of("1 + /** comment   **/ 2 + 3  -1   + 3   -1", ParserResult.State.SUCCESS, 1),
            Arguments.of("1 + /** comment   **/ 2 + 3  -1   + 3   1", ParserResult.State.FAILURE, null) // trailing digit, no operator
        );
    }

    @ParameterizedTest
    @MethodSource("token2")
    void token2(
        String source,
        ParserResult.State state,
        Integer result
    ) {

        // arrange

        // ... tokens
        Parser<String> digit = Parsers.is(Character::isDigit).map(ns -> String.join("", ns));
        Parser<String> plus = Parsers.is('+');
        Parser<String> minus = Parsers.is('-');

        // ... ignored
        Parser<String> whitespace = Parsers.is(Character::isWhitespace);
        Parser<String> comment = Parsers.until(Parsers.is("/**"), Parsers.is("*/"), (b, c, u) -> b + c + u);
        Parser<List<String>> ignore = Parsers.or(whitespace, comment).many();

        // ... parser
        Parser<Integer> number = digit.map(Integer::parseInt);
        TokenParser<Integer> sum;
        sum = TokenParsers.infixl(number.token(), plus.token(), (l, op, r) -> l + r);
        sum = TokenParsers.infixl(sum, minus.token(), (l, op, r) -> l - r);

        // ... token parser
        Parser<Integer> parser = TokenParsers.tokenize(Parsers.or(digit, plus, minus), ignore, sum);

        // act
        ParserResult<Integer> parsed = parser.parse(source);

        // assert
        assertThat(parsed.state()).isEqualTo(state);
        if (state == ParserResult.State.SUCCESS) {
            assertThat(parsed.result()).hasValue(result);
            assertThat(parsed.remaining()).isEmpty();
        } else {
            assertThat(parsed.result()).isEmpty();
            assertThat(parsed.remaining()).isEqualTo(source);
        }
    }
}
