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

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression;
import com.jetbrains.php.lang.psi.elements.ArrayIndex;
import com.jetbrains.php.lang.psi.elements.AssignmentExpression;
import com.jetbrains.php.lang.psi.elements.FieldReference;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Various PSI utility methods.
 *
 * @author martynas.sateika
 * @since 0.1.0
 */
public class MyPsiUtil {

  /**
   * @param psiElement element whose parent to inspect
   * @param parentClass wanted parent class
   * @param <T> parent class type
   * @return parent of <tt>psiElement</tt> if its class is <tt>parentClass</tt>, else null
   */
  @Nullable
  public static <T> T getParentOfType(@Nullable PsiElement psiElement,
      @NotNull Class<T> parentClass) {
    if (psiElement != null) {
      PsiElement parent = psiElement.getParent();
      if (parent != null) {
        if (parentClass.isInstance(parent)) {
          //noinspection unchecked
          return (T) parent;
        }
      }
    }
    return null;
  }

  /**
   * @param element an element
   * @param functionName function name
   * @param idx function parameter index (0-based)
   * @return true if the element is in argument #{@code idx} of function {@code functionName}
   */
  public static boolean isArgumentOfFunction(PsiElement element, String functionName, int idx) {
    ParameterList parameterList = MyPsiUtil
        .getParentOfType(element, ParameterList.class);
    if (parameterList != null) {
      PsiElement[] parameters = parameterList.getParameters();
      if (parameters.length > idx && parameters[idx] == element) {
        FunctionReference functionReference = MyPsiUtil
            .getParentOfType(parameterList, FunctionReference.class);
        return functionReference != null && functionName.equals(functionReference.getName());
      }
    }
    return false;
  }

  /**
   * Helps spot arguments of methods such as {@code $this->load->view('foo')}.
   *
   * @param element an element
   * @param fieldName field name
   * @param methodName method name
   * @param idx method parameter index (0-based)
   * @return true if the element is in argument #{@code idx} of method {@code methodName}
   */
  public static boolean isArgumentOfMethod(PsiElement element, String fieldName, String methodName,
      int idx) {
    ParameterList parameterList = MyPsiUtil
        .getParentOfType(element, ParameterList.class);
    if (parameterList != null) {
      PsiElement[] parameters = parameterList.getParameters();
      if (parameters.length > idx && parameters[idx] == element) {
        // Find method reference, e.g. 'view'
        MethodReference methodReference = MyPsiUtil
            .getParentOfType(parameterList, MethodReference.class);
        if (methodReference != null && methodName.equals(methodReference.getName())) {
          // Find field reference, e.g. 'load'
          PsiElement firstChild = methodReference.getFirstChild();
          if (firstChild instanceof FieldReference) {
            return fieldName.equals(((FieldReference) firstChild).getName());
          }
        }
      }
    }
    return false;
  }

  /**
   * @param expression an assignment expression
   * @param text expected array index
   * @return {@code true} if the supplied expression is an array access expression with a string
   * literal index
   */
  public static boolean isArrayAccessWithStringIndex(AssignmentExpression expression, String text) {
    PhpPsiElement variable = expression.getVariable();
    if (variable instanceof ArrayAccessExpression) {
      ArrayAccessExpression arrayAccessExpression = (ArrayAccessExpression) variable;
      ArrayIndex index = arrayAccessExpression.getIndex();
      if (index != null && index.getValue() instanceof StringLiteralExpression) {
        StringLiteralExpression indexValue = (StringLiteralExpression) index.getValue();
        return indexValue.getContents().equals(text);
      }
    }
    return false;
  }

}
