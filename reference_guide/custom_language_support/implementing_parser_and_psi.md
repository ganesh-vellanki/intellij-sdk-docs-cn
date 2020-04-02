---
title: 实现解析器与 PSI
---
<!-- Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file. -->

在 IntelliJ Platform 中解析文件的过程有两步。
首先, 建立一个抽象语法树（AST），定义程序的结构。
AST 节点通过 IDE 内部地创建，并且由
[`ASTNode`](upsource:///platform/core-api/src/com/intellij/lang/ASTNode.java)
类的示例表示。
每个 AST 节点都有一个相关联的元素类型
[`IElementType`](upsource:///platform/core-api/src/com/intellij/psi/tree/IElementType.java)
实例，并且元素类型由语言插件定义。
文件的 AST 树的顶级节点需要拥有一个特殊的元素类型，它实现了
[`IFileElementType`](upsource:///platform/core-api/src/com/intellij/psi/tree/IFileElementType.java)
接口。

AST 节点直接映射到底层文档的文本范围。
AST 的最底层节点与 lexer 返回的单个 token 匹配，并且高阶的节点与多 token 片段匹配。
在 AST 树上的节点执行的操作，例如插入，移除，重新排序节点等等，立即反映为底层文档的文本的改变。

其次，PSI、或是程序结构接口、树是构建在 AST 上的，添加用于处理特定语言构造的语义与方法。
PSI 树的节点由实现了
[`PsiElement`](upsource:///platform/core-api/src/com/intellij/psi/PsiElement.java)
接口的类表示，并且在
[`ParserDefinition.createElement()`](upsource:///platform/core-api/src/com/intellij/lang/ParserDefinition.java)
方法中通过语言插件创建。
对于文件的 PSI 树的顶层节点需要实现
[`PsiFile`](upsource:///platform/core-api/src/com/intellij/psi/PsiFile.java)
接口，并且在
[`ParserDefinition.createFile()`](upsource:///platform/core-api/src/com/intellij/lang/ParserDefinition.java)
方法中创建。

**示例**:
[Properties language plugin](upsource:///plugins/properties)
的
[`ParserDefinition`](upsource:///plugins/properties/properties-psi-impl/src/com/intellij/lang/properties/parsing/PropertiesParserDefinition.java)


PSI 的生命周期在 [Fundamentals](/platform/fundamentals.md) 中有更详细的描述。

PSI 实现的基类，包含
[`PsiFileBase`](upsource:///platform/core-impl/src/com/intellij/extapi/psi/PsiFileBase.java)，它是
[`PsiFile`](upsource:///platform/core-api/src/com/intellij/psi/PsiFile.java)
的基础实现，
以及
[`ASTWrapperPsiElement`](upsource:///platform/core-impl/src/com/intellij/extapi/psi/ASTWrapperPsiElement.java)，它是
[`PsiElement`](upsource:///platform/core-api/src/com/intellij/psi/PsiElement.java)
的基础实现，
它们由 *IntelliJ Platform* 提供。

虽然可以手写 parser，但我们更推荐使用
[Grammar-Kit](https://plugins.jetbrains.com/plugin/6606-grammar-kit) 插件从 grammar 中生成 parser 与相应的 PSI 类。
除了代码生成，它还为编辑 grammar 文件提供了各种特性：语法高亮，快速导航，重构等等。
Grammar-Kit 插件使用它自己的引擎构建；可以在
[GitHub](https://github.com/JetBrains/Grammar-Kit) 中找到它的源代码。

为了重用已存在的 ANTLRv4 grammar，参见 [antlr4-intellij-adaptor](https://github.com/antlr/antlr4-intellij-adaptor) 库。

语言插件提供 parser 实现，作为
[`PsiParser`](upsource:///platform/core-api/src/com/intellij/lang/PsiParser.java)
接口的实现，它从
[`ParserDefinition.createParser()`](upsource:///platform/core-api/src/com/intellij/lang/ParserDefinition.java) 中返回。
Parser 接收一个
[`PsiBuilder`](upsource:///platform/core-api/src/com/intellij/lang/PsiBuilder.java)
类的实例，它用于从 lexer 中获取 token 流，以及保持正在构建的 AST 的中间状态。
Parser 必须处理由 lexer 返回的所有 token（即使根据语法，该 token 是无效的），也就是说，直到
[`PsiBuilder.getTokenType()`](upsource:///platform/core-api/src/com/intellij/lang/PsiBuilder.java)
返回 `null`。

**示例**：
[Properties language plugin](upsource:///plugins/properties/properties-psi-impl/src/com/intellij/lang/properties/)
的
[`PsiParser`](upsource:///plugins/properties/properties-psi-impl/src/com/intellij/lang/properties/parsing/PropertiesParser.java) 实现。

Parser 的工作原理是：在从 lexer 接收的 token 流中设置成对的标记（
[`PsiBuilder.Marker`](upsource:///platform/core-api/src/com/intellij/lang/PsiBuilder.java)
实例）。
每对标记都为在 AST 树中的单个节点定义 lexer token 的范围。
如果一对标记嵌套在另一对标记中（在其起点之后开始，在其终点之前结束），它将成为外部对（译者注：pair）的子节点

The element type for the marker pair and for the AST node created from it is specified when the end marker is set, which is done by making call to
[`PsiBuilder.Marker.done()`](upsource:///platform/core-api/src/com/intellij/lang/PsiBuilder.java).
Also, it is possible to drop a start marker before its end marker has been set.
The `drop()` method drops only a single start marker without affecting any markers added after it, and the `rollbackTo()` method drops the start marker and all markers added after it and reverts the lexer position to the start marker.
These methods can be used to implement lookahead when parsing.

The method
[`PsiBuilder.Marker.precede()`](upsource:///platform/core-api/src/com/intellij/lang/PsiBuilder.java)
is useful for right-to-left parsing when you don't know how many markers you need at a certain position until you read more input.
For example, a binary expression `a+b+c` needs to be parsed as `( (a+b) + c )`.
Thus, two start markers are needed at the position of the token 'a', but that is not known until the token 'c' is read.
When the parser reaches the '+' token following 'b', it can call `precede()` to duplicate the start marker at 'a' position, and then put its matching end marker after 'c'.

An important feature of
[`PsiBuilder`](upsource:///platform/core-api/src/com/intellij/lang/PsiBuilder.java)
is its handling of whitespace and comments.
The types of tokens which are treated as whitespace or comments are defined by the methods `getWhitespaceTokens()` and `getCommentTokens()` in the
[`ParserDefinition`](upsource:///platform/core-api/src/com/intellij/lang/ParserDefinition.java)
class.
[`PsiBuilder`](upsource:///platform/core-api/src/com/intellij/lang/PsiBuilder.java)
automatically omits whitespace and comment tokens from the stream of tokens it passes to
[`PsiParser`](upsource:///platform/core-api/src/com/intellij/lang/PsiParser.java),
and adjusts the token ranges of AST nodes so that leading and trailing whitespace tokens are not included in the node.

The token set returned from
[`ParserDefinition.getCommentTokens()`](upsource:///platform/core-api/src/com/intellij/lang/ParserDefinition.java)
is also used to search for TODO items.

In order to better understand the process of building a PSI tree for a simple expression, you can refer to the following diagram:

![PsiBuilder](img/PsiBuilder.gif)

In general, there is no single right way to implement a PSI for a custom language, and the plugin author can choose the PSI structure and set of methods which are the most convenient for the code which uses the PSI (error analysis, refactorings and so on).
However, there is one base interface which needs to be used by a custom language PSI implementation in order to support features like rename and find usages.
Every element which can be renamed or referenced (a class definition, a method definition and so on) needs to implement the
[`PsiNamedElement`](upsource:///platform/core-api/src/com/intellij/psi/PsiNamedElement.java)
interface, with methods `getName()` and `setName()`.

A number of functions which can be used for implementing and using the PSI can be found in the `com.intellij.psi.util` package, and in particular in the
[`PsiUtil`](upsource:///java/java-psi-api/src/com/intellij/psi/util/PsiUtil.java)
and
[`PsiTreeUtil`](upsource:///platform/core-api/src/com/intellij/psi/util/PsiTreeUtil.java)
classes.

A very helpful tool for debugging the PSI implementation is the
[PsiViewer plugin](https://plugins.jetbrains.com/plugin/227-psiviewer).
It can show you the structure of the PSI built by your plugin, the properties of every PSI element and highlight its text range.

Please see
[Indexing and PSI Stubs](/basics/indexing_and_psi_stubs.md)
for advanced topics.
