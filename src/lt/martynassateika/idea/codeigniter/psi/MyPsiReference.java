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

package lt.martynassateika.idea.codeigniter.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple PSI reference implementation.
 *
 * @author martynas.sateika
 * @since 0.1.0
 */
public class MyPsiReference implements PsiReference {

  private final PsiElement resolve;

  private final StringLiteralExpression literalExpression;

  public MyPsiReference(PsiElement resolve, StringLiteralExpression literalExpression) {
    this.resolve = resolve;
    this.literalExpression = literalExpression;
  }

  @NotNull
  @Override
  public PsiElement getElement() {
    return literalExpression;
  }

  @NotNull
  @Override
  public TextRange getRangeInElement() {
    int start = literalExpression.getTextRange().getStartOffset();
    return literalExpression.getTextRange().shiftRight(-start);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    return resolve;
  }

  @NotNull
  @Override
  public String getCanonicalText() {
    return "";
  }

  @Override
  public PsiElement handleElementRename(String s) throws IncorrectOperationException {
    throw new UnsupportedOperationException();
  }

  @Override
  public PsiElement bindToElement(@NotNull PsiElement psiElement)
      throws IncorrectOperationException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isReferenceTo(PsiElement psiElement) {
    return resolve() == psiElement;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }

  @Override
  public boolean isSoft() {
    return true;
  }

}
