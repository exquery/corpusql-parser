Corpus Query Language Parser
============================
[![Build Status](https://travis-ci.org/exquery/corpusql-parser.png?branch=master)](https://travis-ci.org/exquery/corpusql-parser) [![Java 6+](https://img.shields.io/badge/java-8+-blue.svg)](http://java.oracle.com) [![License](https://img.shields.io/badge/license-GPL%202-blue.svg)](https://www.gnu.org/licenses/gpl-2.0.html)

This library implements a Corpus Query Language Parser in Java 1.6, using [Parboiled](https://github.com/sirthias/parboiled).

The gramar definition for this parser was taken by running `jjdoc` against the javacc [`cql`](https://raw.githubusercontent.com/INL/BlackLab/master/core/src/main/javacc/nl/inl/blacklab/queryParser/corpusql/cql.jj) grammar from the [Institute of Dutch Lexicology](http://www.inl.nl/) [BlackLab](https://github.com/INL/BlackLab) project. 

The parser generates an AST (Abstract Syntax Tree) which you can then use in your own application for whatever you wish. The class [CorpusQLUtil](https://github.com/exquery/corpusql-parser/blob/master/src/main/java/com/evolvedbinary/corpusql/parser/CorpusQLUtil.java) shows how the parser can be used. You can also execute `CorpusQLUtil` as an application if you want to understand the node-tree produced by the parser.


Obtaining
---------
The [compiled artifact](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.evolvedbinary.cql%22%20AND%20a%3A%22corpusql-parser%22) can be obtained from Maven Central by adding the following to the `<dependencies>` section of your `pom.xml`:
```xml
<dependency>
    <groupId>com.evolvedbinary.cql</groupId>
    <artifactId>corpusql-parser</artifactId>
    <version>1.0</version>
</dependency>
```

If you are a Scala, Groovy or Clojure person then you can still use the artifact from Maven Central with your favourite build tool, however I will assume you know what you're doing ;-)


Future Work
-----------
* Provide some tutorials or better documentation
