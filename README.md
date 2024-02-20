# Parser

Parser Combinator Library

### Source Level Parsers

TBC

### Token Level Parsers

Lex then parse

An example for expressions like `1 + 3 - 4` ignoring comments `/** comment **/` and whitespace

<pre>1 + 3 - 4</pre> is equivalent to <pre>1 /** this is a comment **/      +3        -4</pre>

The token level parsers, integers, '-', or '+'  

    Parser<String> digit = Parsers.is(Character::isDigit).map(ns -> String.join("", ns));
    Parser<String> plus = Parsers.is('+');
    Parser<String> minus = Parsers.is('-');

Ignored parts of the expression, comments `/** comment **/`, or whitespace

    Parser<String> whitespace = Parsers.is(Character::isWhitespace);
    Parser<String> comment = Parsers.until(Parsers.is("/**"), Parsers.is("*/"), (b, c, u) -> b + c + u);
    Parser<List<String>> ignore = Parsers.or(whitespace, comment).many();

A parser from the tokens

    Parser<Integer> number = digit.map(Integer::parseInt);

    TokenParser<Integer> sum;
    sum = TokenParsers.infixl(number.token(), plus.token(), (l, op, r) -> l + r);
    sum = TokenParsers.infixl(sum, minus.token(), (l, op, r) -> l - r);

Now combine, to separate the lexical analysis from parsing

    Parser<Integer> parser = TokenParsers.tokenize(Parsers.or(digit, plus, minus), ignore, sum);

The input is tokenized, and then the token parsing takes place on the individual parsed tokens.