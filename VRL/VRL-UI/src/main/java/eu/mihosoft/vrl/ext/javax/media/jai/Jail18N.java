/*
 * $RCSfile: JaiI18N.java,v $
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * $Revision: 1.1 $
 * $Date: 2005/02/11 04:55:47 $
 * $State: Exp $
 */
package eu.mihosoft.vrl.ext.javax.media.jai;

class JaiI18N {
    static String packageName = "com.sun.media.jai.mlib";

    public static String getString(String key) {
        return PropertyUtil.getString(packageName, key);
    }
}

