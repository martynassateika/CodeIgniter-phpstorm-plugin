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

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.PhpLangUtil;
import com.jetbrains.php.lang.psi.elements.ConstantReference;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.Statement;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import lt.martynassateika.idea.codeigniter.CodeIgniterProjectComponent;
import lt.martynassateika.idea.codeigniter.inspection.CodeIgniterInspection;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author martynas.sateika
 * @since 0.2.0
 */
public class CodeIgniterReturnedViewNotUsedInspection extends CodeIgniterInspection {

  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return "Returned view data not used";
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
    return new PhpElementVisitor() {
      @Override
      public void visitPhpConstantReference(ConstantReference reference) {
        Project project = reference.getProject();
        if (CodeIgniterProjectComponent.isEnabled(project)) {
          if (PhpLangUtil.isTrue(reference)) {
            if (CiViewUtil.isArgumentOfLoadView(reference, 2)) {
              if (isBareStatement(reference)) {
                problemsHolder.registerProblem(reference, "Returned view data not used");
              }
            }
          }
        }
      }
    };
  }

  /**
   * @param reference constant reference
   * @return true if the method reference ('$this->load') is at the start of a statement
   */
  private static boolean isBareStatement(ConstantReference reference) {
    MethodReference methodReference = PsiTreeUtil.getParentOfType(reference, MethodReference.class);
    return methodReference != null && methodReference.getParent() instanceof Statement;
  }

}
