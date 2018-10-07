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
import lt.martynassateika.idea.codeigniter.psi.MyPsiReference;
import org.jetbrains.annotations.NotNull;

/**
 * Reference contributor for CI translations.
 *
 * @author martynas.sateika
 * @since 0.1.0
 */
public class LanguageReferenceContributor extends PsiReferenceContributor {

  @Override
  public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
    psiReferenceRegistrar.registerReferenceProvider(PlatformPatterns.psiElement(),
        new PsiReferenceProvider() {
          @SuppressWarnings("deprecation")
          @NotNull
          @Override
          public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement,
              @NotNull ProcessingContext processingContext) {
            if (psiElement instanceof StringLiteralExpression) {
              StringLiteralExpression stringLiteralExpression = (StringLiteralExpression) psiElement;
              Project project = psiElement.getProject();
              List<AssignmentExpression> translations = CiLanguageUtil
                  .findTranslationsFor(project, psiElement.getText());
              List<PsiReference> references = new ArrayList<>(translations.size());
              for (AssignmentExpression translation : translations) {
                ArrayAccessExpression arrayAccessExpression = (ArrayAccessExpression) translation
                    .getVariable();
                if (arrayAccessExpression != null) {
                  ArrayIndex index = arrayAccessExpression.getIndex();
                  references.add(new MyPsiReference(index, stringLiteralExpression));
                }
              }
              return references.toArray(PsiReference.EMPTY_ARRAY);
            }
            return PsiReference.EMPTY_ARRAY;
          }
        });
  }

}
