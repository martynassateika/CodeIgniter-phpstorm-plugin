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

import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression;
import com.jetbrains.php.lang.psi.elements.ArrayIndex;
import com.jetbrains.php.lang.psi.elements.AssignmentExpression;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import java.util.ArrayList;
import java.util.List;

import lt.martynassateika.idea.codeigniter.CodeIgniterProjectSettings;
import lt.martynassateika.idea.codeigniter.psi.MyPsiReference;
import org.jetbrains.annotations.NotNull;

/**
 * Reference contributor for CI configuration items.
 *
 * @author martynas.sateika
 * @since 0.5.0
 */
public class ConfigReferenceContributor extends PsiReferenceContributor {

  @Override
  public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
    psiReferenceRegistrar.registerReferenceProvider(PlatformPatterns.psiElement(),
        new PsiReferenceProvider() {
          @NotNull
          @Override
          public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement,
              @NotNull ProcessingContext processingContext) {
            Project project = psiElement.getProject();
            
            if (CodeIgniterProjectSettings.getInstance(project).isEnabled()) {
              if (psiElement instanceof StringLiteralExpression) {
                StringLiteralExpression literalExpression = (StringLiteralExpression) psiElement;
                if (CiConfigUtil.isConfigItemNameElement(psiElement)) {
                  List<AssignmentExpression> configValues = CiConfigUtil
                      .findValuesFor(project, literalExpression);
                  return getValueReferences(literalExpression, configValues);
                }
              }
            }
            return PsiReference.EMPTY_ARRAY;
          }
        });
  }

  @NotNull
  private PsiReference[] getValueReferences(StringLiteralExpression stringLiteralExpression,
      List<AssignmentExpression> configValues) {
    List<PsiReference> references = new ArrayList<>(configValues.size());
    for (AssignmentExpression translation : configValues) {
      ArrayAccessExpression arrayAccessExpression = (ArrayAccessExpression) translation
          .getVariable();
      if (arrayAccessExpression != null) {
        ArrayIndex index = arrayAccessExpression.getIndex();
        references.add(new MyPsiReference(index, stringLiteralExpression));
      }
    }
    return references.toArray(PsiReference.EMPTY_ARRAY);
  }

}
