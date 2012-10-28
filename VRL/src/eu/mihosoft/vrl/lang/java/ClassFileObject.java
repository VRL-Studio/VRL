/* 
 * ClassFileObject.java
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

package eu.mihosoft.vrl.lang.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.SimpleJavaFileObject;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ClassFileObject extends SimpleJavaFileObject {

    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    private String name = null;
    private String packageName = null;

    public ClassFileObject(String name, Kind kind) {

        super(URI.create(
                "string:///" + name.replace('.', '/') + kind.extension), kind);

        init(name, null);
    }

    public ClassFileObject(String name, Kind kind, byte[] data) {

        super(URI.create(
                "string:///" + name.replace('.', '/') + kind.extension), kind);

        init(name.replace('.', '/'),data);
    }

    public void init(String name, byte[] data) {
        String fullName = super.getName();

        if (fullName.startsWith("/")) {
            fullName = fullName.substring(1);
        }

        if (fullName.contains("/")) {
            int pos = fullName.lastIndexOf('/');
            packageName = fullName.substring(0, pos);
            packageName = packageName.replace("/", ".");
            this.name = fullName.substring(pos + 1);
        } else {
            packageName = "";
            this.name = super.getName();
        }
        
        if (data!=null) {
            bos.write(data, 0, data.length);
            try {
                bos.close();
            } catch (IOException ex) {
                //
            }
        }
    }
    
    @Override
    public String getName() {
        return name;
    }

    public byte[] getClassBytes() {
        return bos.toByteArray();
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return bos;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        
        // return binary content as input stream
        return new ByteArrayInputStream(bos.toByteArray());
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.uri != null ? this.uri.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof ClassFileObject) {
            return o.hashCode() == hashCode();
        }
        return false;
    }
}
