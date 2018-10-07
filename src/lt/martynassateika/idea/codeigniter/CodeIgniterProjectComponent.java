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

package lt.martynassateika.idea.codeigniter;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

/**
 * <p>Provides ...</p>
 * <p>
 * <p>Created on 07/10/2018 by martynas.sateika</p>
 *
 * @author martynas.sateika
 */
public class CodeIgniterProjectComponent implements ProjectComponent {

  public static boolean isEnabled(Project project) {
    CodeIgniterProjectSettings settings = ServiceManager
        .getService(project, CodeIgniterProjectSettings.class);
    return settings.isEnabled();
  }

}
