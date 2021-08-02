/*
 * Copyright 2018 Martynas Sateika
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

package lt.martynassateika.idea.codeigniter.language;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.AssignmentExpression;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lt.martynassateika.idea.codeigniter.CodeIgniterProjectComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Folding builder that replaces language keys with actual translations in calls to 'lang'.
 *
 * @author martynas.sateika
 * @since 0.1.0
 */
public class LanguageFoldingBuilder extends FoldingBuilderEx {

  @NotNull
  @Override
  public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement psiElement,
      @NotNull Document document, boolean quick) {
    Project project = psiElement.getProject();
    if (!CodeIgniterProjectComponent.isEnabled(project)) {
      return FoldingDescriptor.EMPTY;
    }

    List<FoldingDescriptor> descriptors = new ArrayList<>();
    Collection<FunctionReference> functionReferences = PsiTreeUtil
        .findChildrenOfType(psiElement, FunctionReference.class);
    for (FunctionReference reference : functionReferences) {
      if ("lang".equals(reference.getName())) {
        PsiElement[] parameters = reference.getParameters();
        if (parameters.length > 0) {
          PsiElement firstParameter = parameters[0];
          if (firstParameter instanceof StringLiteralExpression) {
            String translation = getTranslation(project, (StringLiteralExpression) firstParameter);
            if (translation != null) {
              FoldingDescriptor foldingDescriptor = createDescriptor(firstParameter, translation);
              descriptors.add(foldingDescriptor);
            }
          }
        }
      }
    }
    return descriptors.toArray(FoldingDescriptor.EMPTY);
  }

  /**
   * Creates a folding descriptor for a PSI element.
   *
   * @param element element to fold
   * @param translation translation to display
   */
  private static FoldingDescriptor createDescriptor(PsiElement element, String translation) {
    return new FoldingDescriptor(element.getNode(), element.getTextRange()) {
      @Override
      public String getPlaceholderText() {
        return translation;
      }
    };
  }

  /**
   * @param project current project
   * @param expression language key
   * @return translation if found, else null
   */
  @Nullable
  private static String getTranslation(Project project, StringLiteralExpression expression) {
    List<AssignmentExpression> translations = CiLanguageUtil.findTranslationsFor(project, expression);
    if (translations.isEmpty()) {
      return null;
    }
    // TODO Sorting
    AssignmentExpression firstTranslation = translations.get(0);
    PhpPsiElement value = firstTranslation.getValue();
    return value == null ? null : StringUtil.unquoteString(value.getText());
  }

  @Nullable
  @Override
  public String getPlaceholderText(@NotNull ASTNode astNode) {
    return null;
  }

  @Override
  public boolean isCollapsedByDefault(@NotNull ASTNode astNode) {
    return true;
  }

}
