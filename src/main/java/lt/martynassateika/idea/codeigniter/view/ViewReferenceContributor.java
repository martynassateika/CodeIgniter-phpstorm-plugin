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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import lt.martynassateika.idea.codeigniter.CodeIgniterProjectComponent;
import lt.martynassateika.idea.codeigniter.psi.MyPsiReference;
import org.jetbrains.annotations.NotNull;

/**
 * Reference contributor for view files.
 *
 * @author martynas.sateika
 * @since 0.2.0
 */
public class ViewReferenceContributor extends PsiReferenceContributor {

  @Override
  public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
    registrar.registerReferenceProvider(PlatformPatterns.psiElement(),
        new PsiReferenceProvider() {
          @NotNull
          @Override
          public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement,
              @NotNull ProcessingContext processingContext) {
            Project project = psiElement.getProject();
            if (CodeIgniterProjectComponent.isEnabled(project)) {
              if (psiElement instanceof StringLiteralExpression) {
                if (CiViewUtil.isArgumentOfLoadView(psiElement, 0)) {
                  StringLiteralExpression stringLiteralExpression = (StringLiteralExpression) psiElement;
                  String relativePath = StringUtil.unquoteString(stringLiteralExpression.getText());
                  return CiViewUtil.findViewFiles(relativePath, project)
                      .stream()
                      .map(file -> new MyPsiReference(file, stringLiteralExpression))
                      .toArray(PsiReference[]::new);
                }
              }
            }
            return PsiReference.EMPTY_ARRAY;
          }
        });
  }

}
