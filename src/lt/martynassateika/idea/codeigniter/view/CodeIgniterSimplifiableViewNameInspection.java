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

package lt.martynassateika.idea.codeigniter.view;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import lt.martynassateika.idea.codeigniter.CodeIgniterProjectComponent;
import lt.martynassateika.idea.codeigniter.PhpExtensionUtil;
import lt.martynassateika.idea.codeigniter.inspection.CodeIgniterInspection;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.NotNull;

/**
 * @author martynas.sateika
 * @since 0.2.0
 */
public class CodeIgniterSimplifiableViewNameInspection extends CodeIgniterInspection {

  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return "Simplifiable view name";
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
    return new PhpElementVisitor() {
      @Override
      public void visitPhpStringLiteralExpression(StringLiteralExpression expression) {
        Project project = expression.getProject();
        if (CodeIgniterProjectComponent.isEnabled(project)) {
          if (CiViewUtil.isArgumentOfLoadView(expression, 0)) {
            String relativePath = StringUtil.unquoteString(expression.getText());
            String withoutExtension = PhpExtensionUtil.removeIfPresent(relativePath);
            if (!relativePath.equals(withoutExtension)) {
              problemsHolder
                  .registerProblem(expression, "Unnecessary file extension in view name",
                      new RemoveExtensionQuickFix(withoutExtension));
            }
          }
        }
      }
    };
  }

  private class RemoveExtensionQuickFix implements LocalQuickFix {

    private final String newValue;

    RemoveExtensionQuickFix(String newValue) {
      this.newValue = newValue;
    }

    @Nls(capitalization = Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
      return "Remove 'php' extension";
    }

    @Nls(capitalization = Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
      return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
      PsiElement psiElement = problemDescriptor.getPsiElement();
      StringLiteralExpression expression = (StringLiteralExpression) psiElement;

      PsiElement newElement = PhpPsiElementFactory
          .createFromText(project, PhpElementTypes.STRING, "'" + newValue + "'");

      if (newElement != null) {
        expression.replace(newElement);
      }
    }

  }

}
