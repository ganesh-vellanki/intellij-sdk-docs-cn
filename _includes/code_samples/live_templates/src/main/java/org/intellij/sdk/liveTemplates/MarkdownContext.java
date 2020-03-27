// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.liveTemplates;

import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class MarkdownContext extends TemplateContextType {
  protected MarkdownContext() {
    super("MARKDOWN", "Markdown");
  }

  @Override
  public boolean isInContext(@NotNull PsiFile file, int offset) {
    return file.getName().endsWith(".md");
  }
}
