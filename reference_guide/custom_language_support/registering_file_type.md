---
title: 注册文件类型
---
<!-- Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file. -->

开发自定义语言插件的第一个步骤是注册与语言相关联的文件类型。
IDE 通常通过查看文件名称来决定文件的类型。 

自定义语言类型从
[`LanguageFileType`](upsource:///platform/core-api/src/com/intellij/openapi/fileTypes/LanguageFileType.java),
类派生，它传递
[`Language`](upsource:///platform/core-api/src/com/intellij/lang/Language.java)
子类到它的基础类构造器。

为了注册文件类型，插件开发者提供一个
[`FileTypeFactory`](upsource:///platform/platform-api/src/com/intellij/openapi/fileTypes/FileTypeFactory.java) 的子类, 它通过 `com.intellij.fileTypeFactory` 扩展点注册。
> **注意**当仅定位 2019.2 或更高版本，使用 `com.intellij.fileType` 扩展点优先于使用专用 `FileTypeFactory`。

**示例**:
- [`LanguageFileType`](upsource:///platform/core-api/src/com/intellij/openapi/fileTypes/LanguageFileType.java)
subclass in
[Properties language plugin](upsource:///plugins/properties/properties-psi-api/src/com/intellij/lang/properties/PropertiesFileType.java)
- [自定义语言支持教程：语言与文件类型](/tutorials/custom_language_support/language_and_filetype.md)

为了检验文件类型是否正确地注册，你可以实现
[`LanguageFileType.getIcon()`](upsource:///platform/core-api/src/com/intellij/openapi/fileTypes/LanguageFileType.java)
方法并检验与你的文件类型关联的文件是否显示了正确的图标（参见 [Working with Icons and Images](/reference_guide/work_with_icons_and_images.md)）。

If you want IDEs to show a hint prompting users that your plugin supports a specific file type, see [Plugin Recommendations](https://plugins.jetbrains.com/docs/marketplace/intellij-plugin-recommendations.html).
如果你想要 IDE 显示提示来提醒用户你的插件支持特定的文件类型，参见[插件建议](https://plugins.jetbrains.com/docs/marketplace/intellij-plugin-recommendations.html).
