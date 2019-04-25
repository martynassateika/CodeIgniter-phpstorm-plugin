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

package lt.martynassateika.idea.codeigniter.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import lt.martynassateika.idea.codeigniter.config.ConfigCompletionProvider;
import lt.martynassateika.idea.codeigniter.helper.HelperCompletionProvider;
import lt.martynassateika.idea.codeigniter.language.LanguageCompletionProvider;
import lt.martynassateika.idea.codeigniter.model.ModelCompletionProvider;
import lt.martynassateika.idea.codeigniter.view.ViewCompletionProvider;

/**
 * @author martynas.sateika
 * @since 0.1.0
 */
public class MyCompletionContributor extends CompletionContributor {

  public MyCompletionContributor() {
    extend(CompletionType.BASIC, ConfigCompletionProvider.getPlace(),
        new ConfigCompletionProvider());
    extend(CompletionType.BASIC, HelperCompletionProvider.getPlace(),
        new HelperCompletionProvider());
    extend(CompletionType.BASIC, LanguageCompletionProvider.getPlace(),
        new LanguageCompletionProvider());
    extend(CompletionType.BASIC, ModelCompletionProvider.getPlace(), new ModelCompletionProvider());
    extend(CompletionType.BASIC, ViewCompletionProvider.getPlace(), new ViewCompletionProvider());
  }

}
