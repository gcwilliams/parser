package uk.co.gcwilliams.parser.combinator;

import java.util.Optional;

/**
 * The token parser result
 *
 * @author : Gareth Williams
 **/
public class TokenParserResult<T> {

    private final T result;

    private final State state;

    private final TokenContext ctx;

    private TokenParserResult(
            T result,
            State state,
            TokenContext ctx
    ) {
        this.result = result;
        this.state = state;
        this.ctx = ctx;
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
     * Gets the context source
     *
     * @return the context source
     */
    public TokenContext ctx() {
        return ctx;
    }

    /**
     * The parser state
     *
     */
    public enum State { SUCCESS, FAILURE }

    /**
     * Creates a successful parser result
     *
     * @param result the result
     * @param ctx the context
     * @return the parser result
     */
    public static <T> TokenParserResult<T> success(T result, TokenContext ctx) {
        return new TokenParserResult<>(result, State.SUCCESS, ctx);
    }

    /**
     * Creates a failure parser result
     *
     * @param ctx the context
     * @return the parser result
     */
    public static <T> TokenParserResult<T> failure(TokenContext ctx) {
        return new TokenParserResult<>(null, State.FAILURE, ctx);
    }
}
