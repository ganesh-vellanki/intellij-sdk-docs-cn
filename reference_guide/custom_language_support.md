---
title: 自定义语言支持
---
<!-- Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file. -->

*IntelliJ Platform* 是一个强大的平台，可用于构建针对*任何*语言的开发工具。
大部分 IDE 特性由独立于语言与特定于语言的部分组成，并且你只需要花费少量的精力就可以为你的语言支持特定特性：
你只需要实现特定于语言的部分，平台就为你提供独立于语言的部分。

这个部分的文档将解释*Language API*的主要概念，并且将引导你完成开发自定义语言插件通常需要执行的步骤。
你可以从 *Language API* class 的 JavaDoc 注释以及 Properties 语言支持的源代码获得额外关于 *Language API* 的信息，它们是
[IntelliJ IDEA Community Edition](https://github.com/JetBrains/intellij-community)
源代码的一部分


如果你更喜欢完整的示例而不是此页面上提供的详细介绍，请查看有关在 “.properties” 文件示例中如何定义自定义语言支持的分步教程：
[自定义语言支持教程](/tutorials/custom_language_support_tutorial.md)

The webinar [How We Built Comma, the Raku IDE, on the IntelliJ Platform](https://blog.jetbrains.com/platform/2020/01/webinar-recording-how-we-built-comma-the-raku-ide-on-the-intellij-platform/) offers an excellent introduction as well.

提供自定义语言支持包含以下主要步骤：

* [注册文件类型](/reference_guide/custom_language_support/registering_file_type.md)
* [实现词法分析器](/reference_guide/custom_language_support/implementing_lexer.md)
* [实现解析器与 PSI](/reference_guide/custom_language_support/implementing_parser_and_psi.md)
* [语法高亮和错误高亮](/reference_guide/custom_language_support/syntax_highlighting_and_error_highlighting.md)
* [References and Resolve](/reference_guide/custom_language_support/references_and_resolve.md)
* [代码补全](/reference_guide/custom_language_support/code_completion.md)
* [Find Usages](/reference_guide/custom_language_support/find_usages.md)
* [Rename Refactoring](/reference_guide/custom_language_support/rename_refactoring.md)
* [安全删除的重构](/reference_guide/custom_language_support/safe_delete_refactoring.md)
* [代码格式化](/reference_guide/custom_language_support/code_formatting.md)
* [Code Inspections and Intentions](/reference_guide/custom_language_support/code_inspections_and_intentions.md)
* [结构视图](/reference_guide/custom_language_support/structure_view.md)
* [Surround With](/reference_guide/custom_language_support/surround_with.md)
* [跳转到类与跳转到标识](/reference_guide/custom_language_support/go_to_class_and_go_to_symbol.md)
* [文档](/reference_guide/custom_language_support/documentation.md)
* [其他次要特性](/reference_guide/custom_language_support/additional_minor_features.md)


请在[插件开发论坛](https://intellij-support.jetbrains.com/hc/en-us/community/topics/200366979-IntelliJ-IDEA-Open-API-and-Plugin-Development)提问或建议缺失的主题。