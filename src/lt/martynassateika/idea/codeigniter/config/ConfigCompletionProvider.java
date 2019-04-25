/*
 * Copyright 2019 Martynas Sateika
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lt.martynassateika.idea.codeigniter.config;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ProcessingContext;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import java.util.Collection;
import java.util.Collections;
import lt.martynassateika.idea.codeigniter.CodeIgniterProjectComponent;
import lt.martynassateika.idea.codeigniter.psi.MyPsiUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Provides possible config keys for 'config' calls.
 *
 * @author martynas.sateika
 * @since 0.5.0
 */
public class ConfigCompletionProvider extends CompletionProvider<CompletionParameters> {

  @Override
  protected void addCompletions(
      @NotNull CompletionParameters completionParameters,
      @NotNull ProcessingContext processingContext,
      @NotNull CompletionResultSet completionResultSet) {
    PsiElement originalPosition = completionParameters.getOriginalPosition();
    if (originalPosition != null) {
      Project project = originalPosition.getProject();
      if (CodeIgniterProjectComponent.isEnabled(project)) {
        if (isConfigItemNameElement(originalPosition)) {
          FileBasedIndex index = FileBasedIndex.getInstance();
          Collection<String> allKeys = index.getAllKeys(ConfigFileIndex.KEY, project);
          for (String key : allKeys) {
            index.getFilesWithKey(ConfigFileIndex.KEY, Collections.singleton(key),
                file -> {
                  ConfigLookupElement lookupElement = new ConfigLookupElement(
                      StringUtil.unquoteString(key),
                      file
                  );
                  completionResultSet.addElement(lookupElement);
                  return false;
                }, GlobalSearchScope.getScopeRestrictedByFileTypes(
                    GlobalSearchScope.allScope(project),
                    PhpFileType.INSTANCE
                )
            );
          }
        }
      }
    }
  }

  /**
   * @param element location at caret
   * @return true if the caret is on an element representing a config item name
   */
  private static boolean isConfigItemNameElement(PsiElement element) {
    StringLiteralExpression literalExpression = MyPsiUtil
        .getParentOfType(element, StringLiteralExpression.class);
    if (literalExpression != null) {
      return CiConfigUtil.isConfigItemNameElement(literalExpression);
    }
    return false;
  }

  @NotNull
  public static PsiElementPattern.Capture<LeafPsiElement> getPlace() {
    return PlatformPatterns
        .psiElement(LeafPsiElement.class)
        .withParent(StringLiteralExpression.class)
        .withLanguage(PhpLanguage.INSTANCE);
  }

  private static class ConfigLookupElement extends LookupElement {

    @NotNull
    private final String configItemName;

    @NotNull
    private final VirtualFile file;

    ConfigLookupElement(@NotNull String configItemName, @NotNull VirtualFile file) {
      this.configItemName = configItemName;
      this.file = file;
    }

    @NotNull
    @Override
    public String getLookupString() {
      return configItemName;
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
      super.renderElement(presentation);
      presentation.setIcon(IconLoader.findIcon("/icons/php-icon.png"));
      presentation.setTypeText(file.getNameWithoutExtension());
      presentation.setTypeGrayed(true);
    }

  }

}
