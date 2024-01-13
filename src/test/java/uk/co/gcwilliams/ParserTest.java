package uk.co.gcwilliams;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.gcwilliams.parser.combinator.ParserResult;
import uk.co.gcwilliams.parser.combinator.Parsers;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The parser test
 *
 * @author : Gareth Williams
 **/
class ParserTest {

    static Stream<Arguments> followedBy() {
        return Stream.of(
            Arguments.of("Hello", ParserResult.success("Hello", "")),
            Arguments.of("Hello World", ParserResult.failure(" World"))
        );
    }

    @ParameterizedTest
    @MethodSource("followedBy")
    <T> void followedBy(
        String source,
        ParserResult<T> expected
    ) {

        // act
        ParserResult<String> result = Parsers.is("Hello").followedBy(Parsers.eof()).parse(source);

        // assert
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }
}
