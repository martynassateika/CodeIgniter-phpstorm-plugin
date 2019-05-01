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

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import lt.martynassateika.idea.codeigniter.CodeIgniterProjectComponent;
import lt.martynassateika.idea.codeigniter.inspection.CodeIgniterInspection;
import lt.martynassateika.idea.codeigniter.psi.MyPsiReference;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author martynas.sateika
 * @since 0.5.1
 */
public class CodeIgniterTranslationDoesNotExistInspection extends CodeIgniterInspection {

  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return "Translation does not exist";
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
    return new PhpElementVisitor() {
      @Override
      public void visitPhpStringLiteralExpression(StringLiteralExpression expression) {
        Project project = expression.getProject();
        if (CodeIgniterProjectComponent.isEnabled(project)) {
          if (CiLanguageUtil.isLanguageLineKeyElement(expression)) {
            // TODO Reference check enough?
            if (!MyPsiReference.referencesElement(expression)) {
              problemsHolder.registerProblem(expression, String.format(
                  "Translation for '%s' does not exist",
                  expression.getContents()
              ));
            }
          }
        }
      }
    };
  }

}
