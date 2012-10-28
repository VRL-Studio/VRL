/* 
 * CompletionProviderGroup.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2012 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2012 by Michael Hoffer
 * 
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * see: http://opensource.org/licenses/LGPL-3.0
 *      file://path/to/VRL/src/eu/mihosoft/vrl/resources/license/lgplv3.txt
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * This version of VRL includes copyright notice and attribution requirements.
 * According to the LGPL this information must be displayed even if you modify
 * the source code of VRL. Neither the VRL Canvas attribution icon nor any
 * copyright statement/attribution may be removed.
 *
 * Attribution Requirements:
 *
 * If you create derived work you must do three things regarding copyright
 * notice and author attribution.
 *
 * First, the following text must be displayed on the Canvas:
 * "based on VRL source code". In this case the VRL canvas icon must be removed.
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. In addition
 * you must cite the publications listed below. A suitable notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Publications:
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 */

package eu.mihosoft.vrl.lang.visual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CompletionProviderGroup extends DefaultCompletionProvider {

    private Collection<CompletionProvider> completionProviders =
            new ArrayList<CompletionProvider>();

    public CompletionProviderGroup() {
        //
    }

    @Override
    @SuppressWarnings("unchecked")
    public List getCompletions(JTextComponent tc) {

        List<Completion> result = new ArrayList<Completion>();

        for (CompletionProvider provider : completionProviders) {
            result.addAll(provider.getCompletions(tc));
        }

        // lsort in alphabetical order
        Collections.sort(result, new Comparator<Completion>() {

            @Override
            public int compare(Completion o1, Completion o2) {
                String name1 = o1.getReplacementText();
                String name2 = o2.getReplacementText();

                if (o1 instanceof VCompletion) {
                    VCompletion vc1 = (VCompletion) o1;

                    CompletionType type1 = vc1.getType();

                    if (o2 instanceof VCompletion) {
                        VCompletion vc2 = (VCompletion) o2;

                        CompletionType type2 = vc2.getType();

                        if ((type1 == CompletionType.METHOD
                                || type1 == CompletionType.FIELD)
                                && (type2 != CompletionType.METHOD
                                || type2 != CompletionType.FIELD)) {
                            return -1;
                        } else if ((type1 != CompletionType.METHOD
                                || type1 != CompletionType.FIELD)
                                && (type2 == CompletionType.METHOD
                                || type2 == CompletionType.FIELD)) {
                            return 1;
                        }

                        if (type1 == CompletionType.SHORTHAND
                                && type2 != CompletionType.SHORTHAND) {
                            return -1;
                        } else if (type1 != CompletionType.SHORTHAND
                                && type2 == CompletionType.SHORTHAND) {
                            return 1;
                        }

                    } else {
                        return -1;
                    }
                } else if (o2 instanceof VCompletion) {
                    VCompletion vc2 = (VCompletion) o2;

                    CompletionType type2 = vc2.getType();

                    if (type2 == CompletionType.METHOD 
                            || type2 == CompletionType.FIELD) {
                        return 1;
                    } else if (type2 == CompletionType.SHORTHAND) {
                        return 1;
                    }
                }

                //return name2.compareTo(name1);

                Integer name1L = name1.length();
                Integer name2L = name2.length();
                return name1L.compareTo(name2L);

            }
        });

//        // length is more important than alphabetical order
//        Collections.sort(result, new Comparator<Completion>() {
//
//            @Override
//            public int compare(Completion o1, Completion o2) {
//                Integer name1 = o1.getReplacementText().length();
//                Integer name2 = o2.getReplacementText().length();
//                return name1.compareTo(name2);
//            }
//        });

//        // lsort in alphabetical order
//        Collections.sort(result, new Comparator<Completion>() {
//
//            @Override
//            public int compare(Completion o1, Completion o2) {
//                String name1 = o1.getReplacementText();
//                String name2 = o2.getReplacementText();
//                return name2.compareTo(name1);
//            }
//        });
//
//        // length is more important than alphabetical order
//        Collections.sort(result, new Comparator<Completion>() {
//
//            @Override
//            public int compare(Completion o1, Completion o2) {
//                Integer name1 = o1.getReplacementText().length();
//                Integer name2 = o2.getReplacementText().length();
//                return name1.compareTo(name2);
//            }
//        });

        return result;
    }

    public void addProvider(CompletionProvider provider) {
        completionProviders.add(provider);
    }

    public void removeProvider(CompletionProvider provider) {
        completionProviders.remove(provider);
    }
}
