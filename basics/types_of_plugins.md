---
title: 插件的主要类型
---
<!-- Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file. -->

可以通过添加插件来修改和调整基于 *IntelliJ Platform* 的产品，以用于自定义目的。所有可下载的插件都可以在 [JetBrains 插件仓库](https://plugins.jetbrains.com/) 上找到。

最常见的插件包括：

* 自定义语言支持
* 框架整合
* 工具整合
* 用户界面附加组件

## 自定义语言支持

自定义语言支持提供使用特定编程语言的基础功能。这包括：

* 文件类型识别
* 词法分析
* 语法高亮
* 格式化
* 代码 insight 与代码补全
* 检查和快速修复
* Intention actions

参考 [自定义语言支持讲解](/tutorials/custom_language_support_tutorial.md) 来学习更多关于该主题的信息。

## 框架整合

Framework integration consists of improved code insight features which are typical for a given framework, as well as the option to use framework-specific functionality directly from the IDE. Sometimes it also includes language support elements for a custom syntax or DSL.

* Specific code insight
* Direct access to framework-specific functionality

Refer to the [Struts 2 plugin](https://plugins.jetbrains.com/plugin/1698) as an example of framework integration.

## 工具整合

工具整合使直接在 IDE 中操纵第三方工具与组件成为可能，而无需切换上下文。
 
这意味着：

* 实施其他 Action 
* 相关 UI 组件
* 访问外部资源

以 [Gerrit 整合](https://plugins.jetbrains.com/plugin/7272?pr=idea)插件为例。

## 用户界面附加组件

Plugins in this category apply various changes to the standard user interface of the IDE. Some newly added components are interactive and provide new functionality, while others are limited to visual modifications only. The [Background Image](https://plugins.jetbrains.com/plugin/72) plugin may serve as an example.
这个类别的插件将各种更改应用于 IDE 的标准用户界面。