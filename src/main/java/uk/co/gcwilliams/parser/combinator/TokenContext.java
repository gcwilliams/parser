package uk.co.gcwilliams.parser.combinator;

import java.util.List;
import java.util.Optional;

/**
 * A token context
 *
 * @author : Gareth Williams
 **/
public class TokenContext {

    private final String token;

    private final List<String> remaining;

    private TokenContext(
            String token,
            List<String> remaining
    ) {
        this.token = token;
        this.remaining = remaining;
    }

    /**
     * The current token
     *
     * @return the token
     */
    public Optional<String> token() {
        return Optional.ofNullable(token);
    }

    /**
     * Creates a token context
     *
     * @param tokens the tokens
     * @return the token context
     */
    static TokenContext create(List<String> tokens) {
        return new TokenContext(tokens.isEmpty() ? null : tokens.get(0), tokens.stream().skip(1).toList());
    }

    /**
     * Creates the next token context
     *
     * @param ctx the context
     * @return the next token context
     */
    static TokenContext next(TokenContext ctx) {
        return new TokenContext(ctx.remaining.isEmpty() ? null : ctx.remaining.get(0), ctx.remaining.stream().skip(1).toList());
    }
}
