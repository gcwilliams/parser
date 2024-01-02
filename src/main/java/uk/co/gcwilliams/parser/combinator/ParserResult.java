package uk.co.gcwilliams.parser.combinator;

import java.util.Optional;

/**
 * The parser result
 *
 * @author : Gareth Williams
 **/
public class ParserResult<T> {

    private final T result;

    private final State state;

    private final String remaining;

    private ParserResult(
            T result,
            State state,
            String remaining
    ) {
        this.result = result;
        this.state = state;
        this.remaining = remaining;
    }

    /**
     * Gets the result
     *
     * @return the result
     */
    public Optional<T> result() {
        return Optional.ofNullable(result);
    }

    /**
     * Gets the state
     *
     * @return the state
     */
    public State state() {
        return state;
    }

    /**
     * The parser state
     *
     */
    enum State { SUCCESS, FAILURE }

    /**
     * Gets the remaining source
     *
     * @return the remaining source
     */
    public String remaining() {
        return remaining;
    }

    /**
     * Creates a successful parser result
     *
     * @param result the result
     * @param remaining the remaining source
     * @return the parser result
     */
    public static <T> ParserResult<T> success(T result, String remaining) {
        return new ParserResult<>(result, State.SUCCESS, remaining);
    }

    /**
     * Creates a failure parser result
     *
     * @param source the source
     * @return the parser result
     */
    public static <T> ParserResult<T> failure(String source) {
        return new ParserResult<>(null, State.FAILURE, source);
    }
}
