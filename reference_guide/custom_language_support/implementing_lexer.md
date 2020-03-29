---
title: 实现词法分析器
---
<!-- Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file. -->

Lexer，或称为
[词法分析器](https://en.wikipedia.org/wiki/Lexical_analysis)，
它定义了如何将文件的内容分解为 token。
词法分析器是自定义语言插件几乎所有特性的基础，从基础语法高亮，到嘎嗷及代码分析特性。
Lexer 的 API 被定义在
[`Lexer`](upsource:///platform/core-api/src/com/intellij/lexer/Lexer.java) 接口中。

IDE 在三个主要的上下文中调用 lexer，并且插件可以为这些上下文提供不同的 lexer 实现：

*  语法高亮: Lexer 将从
   [`SyntaxHighlighterFactory`](upsource:///platform/editor-ui-api/src/com/intellij/openapi/fileTypes/SyntaxHighlighterFactory.java)
   接口的实现中被返回，该接口在 `com.intellij.lang.syntaxHighlighterFactory` 扩展点中被注册。

*  构建文件的语法树：Lexer 预期从
   [`ParserDefinition.createLexer()`](upsource:///platform/core-api/src/com/intellij/lang/ParserDefinition.java),
   中返回，并且
   [`ParserDefinition`](upsource:///platform/core-api/src/com/intellij/lang/ParserDefinition.java)
   接口在 `com.intellij.lang.parserDefinition` 扩展点中被注册。

*  构建文件中包含的单词的索引：
   如果使用了基于 lexer 的单词扫描器的实现，lexer 将被传递到
   [`DefaultWordsScanner`](upsource:///platform/indexing-api/src/com/intellij/lang/cacheBuilder/DefaultWordsScanner.java)
   的构造器中。

用于语法高亮的 lexer 可以被增量调用，以仅处理文件中被修改的部分，而在其他情况下的 lexer 总是被调用来处理整个文件，或以不同的语言嵌入到文件的完整语言结构中。

增量地使用 Lexer 可能需要返回它的*状态*，这意味着上下文对应着文件中的每个位置。
例如，对于顶级上下文，注释上下文与字面字符串上下文，
[Java lexer](upsource:///java/java-psi-impl/src/com/intellij/lang/java/lexer/JavaLexer.java)
可以拥有独立的状态。
对于语法高亮 lexer 的一个重要需求是：当词法分析从文件的中间恢复时，它的状态必须通过单个整型数字表示，这个数字从
[`Lexer.getState()`](upsource:///platform/core-api/src/com/intellij/lexer/Lexer.java) 中被返回。
那个状态将被传递到
[`Lexer.start()`](upsource:///platform/core-api/src/com/intellij/lexer/Lexer.java)
方法中，以及要处理的片段的起始偏移量。
用于其他上下文的 Lexer 可以总是从 `getState()` 方法中返回 `0`。

最简单的为自定义语言查案件创建 lexer 的方式是使用 [JFlex](https://jflex.de)。
类
[`FlexLexer`](upsource:///platform/core-api/src/com/intellij/lexer/FlexLexer.java)
与
[`FlexAdapter`](upsource:///platform/core-api/src/com/intellij/lexer/FlexAdapter.java)
使 JFlex lexer 适配 IntelliJ Platform Lexer API。
我们有一个
[JFlex 的修补版本](https://github.com/JetBrains/intellij-deps-jflex)，
可以与在
[IntelliJ IDEA Community Edition](https://github.com/JetBrains/intellij-community)
源代码中位于 *tools/lexer/idea-flex.skeleton* 的 lexer skeleton 文件一起使用，以创建与
[`FlexAdapter`](upsource:///platform/core-api/src/com/intellij/lexer/FlexAdapter.java) 相兼容的 lexer。
JFlex 的修复版本提供了一个新的命令行选项 `--charat`，它可以改变 JFlex 生成的代码从而使其与 IntelliJ Platform skeleton 一起工作。
为词法解析启用 `--charat` 选项传递的数据类型为
[`CharSequence`](https://docs.oracle.com/javase/8/docs/api/java/lang/CharSequence.html)
而不是一个字符数组。


对于使用 JFlex 开发 lexer，[GrammarKit plugin](https://plugins.jetbrains.com/plugin/6606-grammar-kit) 是很有用的。
它为编译 JFlex 文件提供语法高亮和其他有用的特性。

> **注意** Lexer，尤其是基于 JFlex 的 lexer，需要以始终与文件的全部内容匹配的方式创建，token 之间没有任何间隔，并且为在其位置的无效字符生成特殊 token。
Lexer 必须不能因为一个无效的字符而过早地 abort。

**示例**:
- 针对 [Properties language plugin](upsource:///plugins/properties)
的
[`Lexer`](upsource:///plugins/properties/src/com/intellij/lang/properties/parsing/Properties.flex) 定义
- [自定义语言支持教程：Lexer](/tutorials/custom_language_support/lexer_and_parser_definition.md)

用于 lexer 的 token 的类型通过
[`IElementType`](upsource:///platform/core-api/src/com/intellij/psi/tree/IElementType.java) 的实例定义。
所有语言通用的许多 token 类型定义在
[`TokenType`](upsource:///platform/core-api/src/com/intellij/psi/TokenType.java)
接口中。
自定义语言插件应该在适用的情况下重用这些 token 类型。
对于所有其他 token 类型，插件需要去创建新的
[`IElementType`](upsource:///platform/core-api/src/com/intellij/psi/tree/IElementType.java)
实例并与使用 token 类型的语言相关联。
每当 lexer 遇到特定的 token 类型时，都应该返回相同的 
[`IElementType`](upsource:///platform/core-api/src/com/intellij/psi/tree/IElementType.java)
实例。

**示例:**
针对
[Properties language plugin](upsource:///plugins/properties) 的
[Token 类型](upsource:///plugins/properties/properties-psi-api/src/com/intellij/lang/properties/parsing/PropertiesTokenTypes.java)


可以在 lexer 级别中实现的一个重要特性是在文件中混合语言，例如，嵌入 Java 代码片段到某些模板语言中。
如果一个语言支持（动词）将其代码片段嵌入另一种语言，则它需要为可以嵌入的不同类型的片段定义变色龙（译者注：原文为 chameleon） token 类型，并且这些 token 类型需要实现
[`ILazyParseableElementType`](upsource:///platform/core-api/src/com/intellij/psi/tree/ILazyParseableElementType.java)
接口。
封闭语言的 lexer 需要把嵌入式语言的整个片段作为单个变色龙 token 返回，类型由嵌入语言定义。
为了解析变色龙 token 的内容，IDE 将会通过调用
[`ILazyParseableElementType.parseContents()`](upsource:///platform/core-api/src/com/intellij/psi/tree/ILazyParseableElementType.java) 来调用嵌入式语言的解析器。
