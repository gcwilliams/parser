package uk.co.gcwilliams.parser.combinator;

/**
 * @author : Gareth Williams
 **/
public interface TokenParser<T>  {

    /**
     * Applies the parser to the context
     *
     * @param ctx the context
     * @return the token parser result
     */
    TokenParserResult<T> parse(TokenContext ctx);
}
