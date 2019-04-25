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

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter;
import com.intellij.util.indexing.FileBasedIndex.InputFilter;
import com.intellij.util.indexing.FileBasedIndexExtension;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression;
import com.jetbrains.php.lang.psi.elements.ArrayIndex;
import com.jetbrains.php.lang.psi.elements.Variable;
import com.jetbrains.php.lang.psi.visitors.PhpRecursiveElementVisitor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lt.martynassateika.idea.codeigniter.compat.VoidDataExternalizer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Index for CI configuration items.
 *
 * @author martynas.sateika
 * @since 0.5.0
 */
public class ConfigFileIndex extends FileBasedIndexExtension<String, Void> {

  @NonNls
  static final ID<String, Void> KEY = ID.create("codeigniter.config.file.index");

  private final MyDataIndexer myDataIndexer = new MyDataIndexer();

  @NotNull
  @Override
  public ID<String, Void> getName() {
    return KEY;
  }

  @NotNull
  @Override
  public DataIndexer<String, Void, FileContent> getIndexer() {
    return myDataIndexer;
  }

  @NotNull
  @Override
  public KeyDescriptor<String> getKeyDescriptor() {
    return EnumeratorStringDescriptor.INSTANCE;
  }

  @NotNull
  @Override
  public DataExternalizer<Void> getValueExternalizer() {
    return VoidDataExternalizer.INSTANCE;
  }

  @Override
  public int getVersion() {
    return 0;
  }

  @NotNull
  @Override
  public InputFilter getInputFilter() {
    return new DefaultFileTypeSpecificInputFilter(PhpFileType.INSTANCE);
  }

  @Override
  public boolean dependsOnFileContent() {
    return true;
  }

  private class MyDataIndexer implements DataIndexer<String, Void, FileContent> {

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public Map<String, Void> map(@NotNull FileContent fileContent) {
      VirtualFile file = fileContent.getFile();
      PsiManager psiManager = PsiManager.getInstance(fileContent.getProject());
      PsiFile psiFile = psiManager.findFile(file);
      if (psiFile instanceof PhpFile) {
        if (CiConfigUtil.isConfigFile((PhpFile) psiFile)) {
          Map<String, Void> map = new HashMap<>();
          psiFile.accept(new PhpRecursiveElementVisitor() {
            @Override
            public void visitPhpArrayAccessExpression(ArrayAccessExpression expression) {
              PsiElement firstChild = expression.getFirstChild();
              if (firstChild instanceof Variable) {
                Variable variable = (Variable) firstChild;
                if (variable.getName().equals("config")) {
                  ArrayIndex index = expression.getIndex();
                  if (index != null) {
                    map.put(index.getText(), null);
                  }
                }
              }
            }
          });
          return map;
        }
      }
      return Collections.emptyMap();
    }
  }

}
