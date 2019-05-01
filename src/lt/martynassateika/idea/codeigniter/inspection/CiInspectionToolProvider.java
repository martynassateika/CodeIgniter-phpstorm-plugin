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

package lt.martynassateika.idea.codeigniter.inspection;

import com.intellij.codeInspection.InspectionToolProvider;
import lt.martynassateika.idea.codeigniter.language.CodeIgniterTranslationDoesNotExistInspection;
import lt.martynassateika.idea.codeigniter.view.CodeIgniterReturnedViewNotUsedInspection;
import lt.martynassateika.idea.codeigniter.view.CodeIgniterSimplifiableViewNameInspection;
import lt.martynassateika.idea.codeigniter.view.CodeIgniterViewDoesNotExistInspection;
import org.jetbrains.annotations.NotNull;

/**
 * Provider of CodeIgniter-specific inspections.
 *
 * @author martynas.sateika
 * @since 0.2.0
 */
public class CiInspectionToolProvider implements InspectionToolProvider {

  @NotNull
  @Override
  public Class[] getInspectionClasses() {
    return new Class[]{
        CodeIgniterReturnedViewNotUsedInspection.class,
        CodeIgniterSimplifiableViewNameInspection.class,
        CodeIgniterTranslationDoesNotExistInspection.class,
        CodeIgniterViewDoesNotExistInspection.class,
    };
  }

}
