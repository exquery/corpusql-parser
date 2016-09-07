/*
 * Copyright © 2016, Evolved Binary Ltd. <tech@evolvedbinary.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Corpus Query Language Grammar for Antlr v4
 */
grammar CorpusQL;

query:              complexQuery ;

complexQuery:       simpleQuery (queryOperator complexQuery)? ;

queryOperator:      WITHIN          # within
                    | CONTAINING    # containing
                    ;

simpleQuery:        sequence (booleanOperator simpleQuery)? ;

sequence:           sequencePart+ ;

sequencePart:       ((NAME | NUMBER) ':')?
                    (
                        tag
                        | position
                        | '(' complexQuery ')'
                    ) repetitionAmount? ;

tag:                '<' '/'? NAME attribute* '/'? '>' ;

attribute:          NAME '=' quotedString ;

position:           positionWord                #positionPositionWord
                    | '[' positionLong? ']'     #positionPositionLong
                    ;

positionWord:       quotedString ;

positionLong:       positionLongPart (booleanOperator positionLong)? ;

positionLongPart:   attValuePair | '(' positionLong ')' | '!' positionLongPart ;

attValuePair:       propName '=' valuePart      # attValuePairEquals
                    | propName '!=' valuePart   # attValuePairNotEquals
                    | valuePart                 # attValuePairDefaultEquals
                    ;

propName:           NAME ('/' NAME)? ;

repetitionAmount:   '*'                             # repetitionZeroOrMore
                    | '+'                           # repetitionOneOrMore
                    | '?'                           # repetitionZeroOrOne
                    | '{' NUMBER '}'                # repetitionExactly
                    | '{' NUMBER ',' NUMBER? '}'    # repetitionMinMax
                    ;

quotedString:       DOUBLE_QUOTED_STRING | SINGLE_QUOTED_STRING ;

booleanOperator:    '&'     # and
                    | '|'   # or
                    | '->'  # implication
                    ;

valuePart:          quotedString        # valuePartString
                    | '(' value ')'     # valuePartParenthesised
                    ;

value:              valuePart booleanOperator value     # valueWith
                    | valuePart                         # valueWithout
                    ;

WITHIN:                 'WITHIN';
CONTAINING:             'CONTAINING';
NAME:                   [a-zA-Z_] [a-zA-Z0-9_]* ;
NUMBER:                 [0-9]+ ;
DOUBLE_QUOTED_STRING:   '"' (DOUBLE_QUOTED_ESC|.)*? '"' ;
fragment
DOUBLE_QUOTED_ESC: '\\"' | '\\\\' ;        // 2-char sequences \" and \\
SINGLE_QUOTED_STRING:   '\'' (SINGLE_QUOTED_ESC|.)*? '\'' ;
fragment
SINGLE_QUOTED_ESC: '\\\'' | '\\\\' ;       // 2-char sequences \' and \\
WS: [ \t\r\n]+ -> skip ;    // discard whitespace


// token literals for operator use in Java
LT:                     '<' ;
GT:                     '>' ;
SOLIDUS:                '/' ;
EQUALS:                 '=' ;
LEFT_SQUARE_BRACKET:    '[' ;
RIGHT_SQUARE_BRACKET:   ']' ;
LEFT_PARENTHESIS:       '(' ;
RIGHT_PARENTHESIS:      ')' ;
EXCLAMATION_MARK:       '!' ;
ASTERISK:               '*' ;
PLUS:                   '+' ;
QUESTION_MARK:          '?' ;
LEFT_CURLY_BRACKET:     '{' ;
LEFT_RIGHT_BRACKET:     '}' ;
AMPERSAND:              '&' ;
VERTICAL_LINE:          '|' ;
HYPHEN_MINUS:           '-' ;
