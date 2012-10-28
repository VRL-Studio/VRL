/* 
 * ClipboardUtil.java
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

package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.io.vrlx.AbstractParameter;
import eu.mihosoft.vrl.system.VSysUtil;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.MessageType;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ClipboardUtil {

    public static String paramDataToXml(List<TypeRepresentationBase> params) {

        Canvas canvas = null;

        if (!params.isEmpty()) {
            canvas = params.get(0).getMainCanvas();
        } else {
            return "";
        }

        String result = "";

        ArrayList<AbstractParameter> valueData = new ArrayList<AbstractParameter>();

        canvas.setSavingSession(true);

        try {

            for (TypeRepresentationBase t : params) {
                try {
                    valueData.add(new AbstractParameter((TypeRepresentationBase) t));
                } catch (Exception ex) {

                    ex.printStackTrace(System.err);

                    String msgTitle = "Errors while saving Parameter(s):";
                    String msg = ">> Cannot save Parameter(s) of method \""
                            + "";

                    System.err.println(">> " + msgTitle);
                    System.err.println(" --> " + msg);
                    System.err.println(" --> still proceeding...");

                    if (t.getMainCanvas() != null) {
                        t.getMainCanvas().getMessageBox().
                                addMessage(
                                msgTitle, msg,
                                t.getConnector(),
                                MessageType.WARNING);
                    }

                }
            }

        } catch (Exception ex) {
            System.out.println(">> cannot write param content to xml");
            ex.printStackTrace(System.err);
        }


        canvas.setSavingSession(false);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        XMLEncoder e = new XMLEncoder(out);

        e.writeObject(valueData);

        e.close();

        try {
            result = new String(out.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ClipboardUtil.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return result;
    }

    private static List<AbstractParameter> readAbstractParamsFromXML(String xml) {
        List<AbstractParameter> valueData = new ArrayList<AbstractParameter>();
        try {
            XMLDecoder d = new XMLDecoder(new ByteArrayInputStream(xml.getBytes("UTF-8")));

            Object obj = d.readObject();

            if (obj != null && obj instanceof List) {
                @SuppressWarnings("unchecked")
                List<AbstractParameter> localData = (List<AbstractParameter>) obj;
                valueData = localData;
            }

            d.close();

        } catch (Exception ex) {
            System.out.println(">> cannot read param content from xml");
        }
        return valueData;
    }

    public static boolean isClipboardContentCompatible(List<TypeRepresentationBase> params) {

        if (VSysUtil.copyFromClipboard().isEmpty()) {
            return false;
        }

        String typeSignature1 = "types:";

        for (TypeRepresentationBase tRep : params) {
            typeSignature1 += tRep.getType().getName() + ":";
        }

        String typeSignature2 = "types:";

        List<AbstractParameter> valueData =
                readAbstractParamsFromXML(VSysUtil.copyFromClipboard());

        for (AbstractParameter aRep : valueData) {
            typeSignature2 += aRep.getType() + ":";
        }

        return typeSignature1.equals(typeSignature2);
    }

    public static void xmlToParamData(String xml, List<TypeRepresentationBase> params) {

        Canvas canvas = null;

        if (!params.isEmpty()) {
            canvas = params.get(0).getMainCanvas();
        } else {
            return;
        }

        canvas.setLoadingSession(true);

        try {
            List<AbstractParameter> valueData = readAbstractParamsFromXML(xml);


            ArrayList<Object> values =
                    new ArrayList<Object>();
            ArrayList<String> valueOptions = new ArrayList<String>();
            ArrayList<CustomParamData> customParamDataList =
                    new ArrayList<CustomParamData>();

            for (AbstractParameter p : valueData) {
                values.add(p.getValueObject());
                valueOptions.add(p.getValueOptions());
                customParamDataList.add(p.getCustomData());
            }

            for (int i = 0; i < valueOptions.size(); i++) {
                params.get(i).setValueOptions(valueOptions.get(i));
                params.get(i).evaluateValueOptions();
            }

            for (int i = 0; i < params.size(); i++) {
                TypeRepresentationBase tRep = params.get(i);
                tRep.setViewValue(values.get(i));
            }

            for (int i = 0; i < customParamDataList.size(); i++) {

                CustomParamData paramData = customParamDataList.get(i);

                // to support old versions (pre 3.8.11, 03.05.2010) we create an
                // empty custom paramDataObject to prevent null pointer exceptions
                if (paramData != null) {
                    params.get(i).setCustomData(paramData);
                }

                params.get(i).setCustomData(customParamDataList.get(i));
                params.get(i).evaluateCustomParamData();
            }

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        canvas.setLoadingSession(false);
    }

    public static void paramsToClipboard(List<TypeRepresentationBase> params) {
        VSysUtil.copyToClipboard(paramDataToXml(params));
    }

    public static void clipboardToParams(List<TypeRepresentationBase> params) {
        xmlToParamData(VSysUtil.copyFromClipboard(), params);
    }
}
