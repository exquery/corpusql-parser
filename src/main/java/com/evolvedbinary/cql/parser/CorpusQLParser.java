/**
 * XPath 2 Parser
 * A Parser for XPath 2
 * Copyright (C) 2016 Evolved Binary Ltd.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.evolvedbinary.cql.parser;

import com.evolvedbinary.cql.parser.ast.*;
import org.jetbrains.annotations.Nullable;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

import org.parboiled.support.Var;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO(AR) think about whether we can remove the use of org.parboiled.support.Var in favour of PartialASTNode, then we would have an immutable AST production
//TODO(AR) may be possible to replace some uses of Var with popAllR

@BuildParseTree
public class CorpusQLParser extends BaseParser<Expr> {

    private final boolean enableActions;

    public CorpusQLParser(final Boolean enableActions) {
        this.enableActions = enableActions;
    }



    /* Corpus Query Language Rules (Non-Terminal Symbols) */


    /**
     * [1] Query ::= ComplexQuery
     */
    public Rule Query() {
        return ComplexQuery();
    }

    /**
     * [2] ComplexQuery	::=	SimpleQuery (QueryOperator ComplexQuery)?
     */
    public Rule ComplexQuery() {
        return Sequence(SimpleQuery(), Optional(QueryOperator(), ComplexQuery()));
    }

    /**
     * [3] QueryOperator ::= (Within | Containing)
     */
    public Rule QueryOperator() {
        return FirstOf(Within(), Containing());
    }

    /**
     * [4] SimpleQuery ::= Sequence (BooleanOperator SimpleQuery)?
     */
    public Rule SimpleQuery() {
        return Sequence(Sequence(), Optional(BooleanOperator(), SimpleQuery()));
    }

    /**
     * [5] Sequence	::=	SequencePart+
     */
    public Rule Sequence() {
        return OneOrMore(SequencePart(), WS());
    }

    /**
     * [6] SequencePart	::=	((Name | Number) ":")? (Tag | Position | "(" ComplexQuery ")") RepetitionAmount?
     */
    public Rule SequencePart() {
        return Sequence(Optional(FirstOf(Name(), Number()), ':'), FirstOf(Tag(), Position(), Sequence('(', ComplexQuery(), ')')), Optional(RepetitionAmount()));
    }

    /**
     * [7] Tag ::= "<" "/"? Name Attribute* "/"? ">"
     */
    public Rule Tag() {
        return Sequence('<', Optional('/'), Name(), ZeroOrMore(Attribute()), Optional('/'), '>');
    }

    /***
     * [8] Attribute ::= Name "=" QuotedString
     */
    public Rule Attribute() {
        final Var<String> value = new Var<String>();
        return Sequence(Name(), '=', QuotedString(value));
    }

    /**
     * [9] Position ::=	PositionWord | "[" PositionLong? "]"
     */
    public Rule Position() {
        return FirstOf(PositionWord(), Sequence('[', Optional(PositionLong()), ']'));
    }

    /**
     * [10] PositionWord ::= QuotedString
     */
    public Rule PositionWord() {
        final Var<String> string = new Var<String>();
        return QuotedString(string);
    }

    /**
     * [11] PositionLong ::= PositionLongPart (BooleanOperator PositionLong)?
     */
    public Rule PositionLong() {
        return Sequence(PositionLongPart(), Optional(WS(), BooleanOperator(), WS(), PositionLong()));
    }

    /**
     * [12] PositionLongPart ::= AttValuePair | "(" PositionLong ")" | "!" PositionLongPart
     */
    public Rule PositionLongPart() {
        return FirstOf(AttValuePair(), Sequence('(', PositionLong(), ')'), Sequence('!', PositionLongPart()));
    }

    /**
     * [13] AttValuePair ::= PropName "=" ValuePart | PropName "!=" ValuePart |	ValuePart
     */
    public Rule AttValuePair() {
        final Var<String> name = new Var<String>();
        return FirstOf(
                Sequence(PropName(), name.set(match()), '=', ValuePart(), push(new AttributeValue(name.get(), AttributeValue.ComparisonType.EQUAL, (ValuePart)pop()))),
                Sequence(PropName(), name.set(match()), "!=", ValuePart(), push(new AttributeValue(name.get(), AttributeValue.ComparisonType.NOT_EQUAL, (ValuePart)pop()))),
                Sequence(ValuePart(), push(new AttributeValue(null, AttributeValue.ComparisonType.EQUAL, (ValuePart)pop())))
        );
    }

    /**
     * [14] PropName ::= Name ("/" Name)?
     */
    public Rule PropName() {
        return Sequence(Name(), Optional('/', Name()));
    }

    /**
     * RepetitionAmount	::=	"*" | "+" | "?" | "{" Number "}" | "{" Number "," Number? "}"
     */
    public Rule RepetitionAmount() {
        return FirstOf('*', '+', '?', Sequence('{', Number(), '}'), Sequence('{', Number(), ',', Optional(Number()), '}'));
    }

    /**
     * QuotedString ::= DoubleQuotedString | SingleQuotedString
     */
    public Rule QuotedString(final Var<String> string) {
        return FirstOf(DoubleQuotedString(string), SingleQuotedString(string));
    }


    /*  Corpus Query Language Terminal Symbols */

    /**
     * BooleanOperator ::= ( "&" | "|" | "->" )
     */
    public Rule BooleanOperator() {
        return FirstOf('&', '|', "->");
    }

    /**
     * Within ::= "within"
     */
    public Rule Within() {
        return String("within");
    }

    /**
     * Containing ::= "containing"
     */
    public Rule Containing() {
        return String("containing");
    }

    /**
     * Name ::= [A-Za-z_] ([A-Za-z_0-9])*
     */
    public Rule Name() {
        return Sequence(FirstOf(CharRange('A', 'Z'), CharRange('a', 'z')), ZeroOrMore(FirstOf(CharRange('A', 'Z'), CharRange('a', 'z'), '_', CharRange('0', '9'))));
    }

    /**
     * Number ::= [0-9]+
     */
    public Rule Number() {
        return OneOrMore(CharRange('0', '9'));
    }

    /**
     * DoubleQuotedString ::= '"' (~["\] | "\" ~[])* '"'
     */
    public Rule DoubleQuotedString(final Var<String> string) {
        return Sequence('"', ZeroOrMore(FirstOf(Sequence("\\", ANY), Sequence(TestNot(AnyOf(new char[]{'"'})), ANY))), string.set(match()), '"');
    }


    /**
     * SingleQuotedString ::= "'" (~['\] | "\" ~[])* "'"
     */
    public Rule SingleQuotedString(final Var<String> string) {
        return Sequence('\'', ZeroOrMore(FirstOf(Sequence("\\", ANY), Sequence(TestNot(AnyOf(new char[]{'\''})), ANY))), string.set(match()), '\'');
    }

    /**
     * Value ::= ValuePart BooleanOperator Value | ValuePart
     */
    public Rule Value() {
        return FirstOf(Sequence(ValuePart(), BooleanOperator(), Value()), ValuePart());
    }

    /**
     * ValuePart ::= QuotedString | "(" Value ")"
     */
    public Rule ValuePart() {
        final Var<String> string = new Var<String>();
        return FirstOf(
                Sequence(QuotedString(string), push(new ValuePart(string.get()))),
                Sequence('(', Value(), ')', push(new ValuePart(new Value())))   //TODO(AR) `new Value()` should be popped from the stack
        );
    }

    /* Corpus Query Language Whitespace Rules */

    /**
     * Whitespace handling
     *
     * WS ::= (S+ | Comment+)*
     */
    Rule WS() {
        return ZeroOrMore(FirstOf(OneOrMore(S()), OneOrMore(Comment())));
    }

    /**
     * S ::= #x20 | #x9 | #xD | #xA
     */
    Rule S() {
        return AnyOf(new char[] {0x20, 0x9, 0xD, 0xA});
    }

    /**
     * Comment ::= SingleLineComment | MultiLineComment
     */
    Rule Comment() {
        return FirstOf(SingleLineComment(), MultiLineComment());
    }

    /**
     * SingleLineComment ::= "#" (~["\n","\r"])* ("\n" | "\r" | "\r\n")?
     */
    Rule SingleLineComment() {
        return Sequence("#", TestNot(ZeroOrMore(AnyOf(new char[] {'\n', '\r'}))), Optional(FirstOf("\r\n", "\r", "\n")));
    }

    /**
     * MultiLineComment ::= "/*" (~["*"] | "*" ~["/"])* "*&#47;"
     */
    Rule MultiLineComment() {
        return Sequence("/*", ZeroOrMore(FirstOf(Sequence('*', TestNot('/')), TestNot('*'))), "*/");
    }


    /**
     * Wraps any other rule to consume all input
     * End of input is signalled by a {@link org.parboiled.support.Chars#EOI}
     *
     * @param rule Any CorpusQLParser rule
     *
     * @return The rule followed by an EOI rule
     */
    public Rule withEOI(final Rule rule) {
        return Sequence(rule, EOI);
    }



    /** utility methods **/
    @Override
    public boolean push(final Expr value) {
        if(enableActions) {
            return super.push(value);
        } else {
            return true;
        }
    }

    @Override
    public Expr pop() {
        if(enableActions) {
            return super.pop();
        } else {
            return null;
        }
    }

    @Override
    public Expr peek() {
        if(enableActions) {
            return super.peek();
        } else {
            return null;
        }
    }
}
