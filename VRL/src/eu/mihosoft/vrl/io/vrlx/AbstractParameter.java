/* 
 * AbstractParameter.java
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

package eu.mihosoft.vrl.io.vrlx;

import eu.mihosoft.vrl.io.Base64;
import eu.mihosoft.vrl.reflection.CustomParamData;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.system.VRL;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;
import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;


/**
 * Abstract representation of a input parameter or return value. This class is
 * only used for XML serialization.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class AbstractParameter {

    /**
     * serialized object encoded as XML or compressed base64 string
     */
    private String valueObjectData;
    /**
     * the value options of this parameter
     */
    private String valueOptions;
    /**
     * custom data of this parameter
     */
    private CustomParamData customData;
    
    /**
     * Name of the type class
     */
    private String type;

    /**
     * Constructor.
     */
    public AbstractParameter() {
        //
    }

    /**
     * Constructor.
     * <p>
     * Creates a new abstract parameter object from a given type representation.
     * </p>
     * @param t the type representation that is to be used to create an new
     * object
     */
    public AbstractParameter(TypeRepresentationBase t) {
        setValueOptions(t.getValueOptions());
        
        setType(t.getType().getName());

        if (!t.noSerialization()) {
            
            if (t.getViewValueWithoutValidation() instanceof Serializable) {

                Serializable value =
                        (Serializable) t.getViewValueWithoutValidation();

                ValueEncoder valueEncoder = new ValueEncoder();
                setValueObjectData(valueEncoder.encodeParamValue(
                        value, t.preferBinarySerialization()));

                if (getValueObjectData() == null) {
                    VisualCanvas mainCanvas = (VisualCanvas) t.getMainCanvas();
                    MessageBox box = mainCanvas.getMessageBox();
                    box.addUniqueMessage("Can't save value:",
                            "TypeRepresentation&lt;" + t.getType().toString()
                            + "&gt;: value not serializable!",
                            t.getConnector(),
                            MessageType.WARNING);
                }

            } else {
                if (t.getViewValueWithoutValidation() != null && t.isInput()) {
                    VisualCanvas mainCanvas = (VisualCanvas) t.getMainCanvas();
                    MessageBox box = mainCanvas.getMessageBox();
                    box.addUniqueMessage("Can't save value:",
                            "TypeRepresentation&lt;" + t.getType().toString()
                            + "&gt;: value not serializable!",
                            t.getConnector(),
                            MessageType.WARNING);
                }
            }
        }

        setCustomData(t.getCustomData());
    }

    /**
     * Returns the deserialized value object.
     * @return the deserialized value object
     */
    public Object getValueObject() {
        Object result = null;
        if (valueObjectData != null) {
            ValueDecoder valueDecoder = new ValueDecoder();
            result = valueDecoder.decodeParamValue(valueObjectData);
        }
        return result;
    }

    /**
     * Returns the value object data.
     * @return the value object data (encoded as XML or compressed base64)
     */
    public final String getValueObjectData() {
        return valueObjectData;
    }

    /**
     * Defines the value object data.
     * @param valueObjectData the value object Data to set
     */
    public final void setValueObjectData(String valueObjectData) {
        this.valueObjectData = valueObjectData;
    }

    /**
     * Returns the value options of this parameter.
     * @return the value option string
     */
    public String getValueOptions() {
        return valueOptions;
    }

    /**
     * Defines the value options of this parameter.
     * @param valueOptions the value options to set
     */
    public final void setValueOptions(String valueOptions) {
        this.valueOptions = valueOptions;
    }

    /**
     * @return the customData
     */
    public CustomParamData getCustomData() {
        return customData;
    }

    /**
     * @param customData the customData to set
     */
    public final void setCustomData(CustomParamData customData) {
        this.customData = customData;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
}
/**
 * Encodes value objects to either XML string or compressed base64 strings
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ValueEncoder implements ExceptionListener {

    /**
     * indicates whether XML encoding is possible
     */
    private boolean encodingErrorXML = false;
    /**
     * the XML encoder to use
     */
    private XMLEncoder encoder;

    /**
     * Constructor.
     */
    public ValueEncoder() {
    }

    @Override
    public void exceptionThrown(Exception e) {
        encodingErrorXML = true;
    }

    /**
     * Encodes serializable value to compressed base64 string
     * @param value the value that is to be encoded
     * @return the base64 value data
     */
    private String encodeToBase64(Serializable value) {
        return Base64.encodeObject(value, Base64.GZIP);
    }

    /**
     * Encodes serializable value.
     * <p>
     * The encoder tries XML serialization first. If errors occur it tries
     * binary serialization.
     * </p>
     * @param value the value that is to be encoded
     * @param prefereBinary defines whether to prefere binary serialization
     * @return string either containing XML or compressed base64 encoded data
     */
    public String encodeParamValue(Serializable value, boolean prefereBinary) {
        String valueData = null;

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        if (!prefereBinary) {

            encoder = new XMLEncoder(out);
            encoder.setExceptionListener(this);

            encoder.writeObject(value);
            encoder.close();
        }

        if (!encodingErrorXML && !prefereBinary) {
            valueData = out.toString();
        } else {

            valueData = encodeToBase64(value);

            if (!encodingErrorXML) {
                System.out.println(">> AbstractParameter:"
                        + " prefering binary serialization. Using Base64!");
            } else {
                System.out.println(">> AbstractParameter:"
                        + " can't use XML persistence. Using Base64!");
            }

            if (valueData == null) {
                System.out.println(
                        ">> AbstractParameter:"
                        + " can't use Base64 persistence."
                        + " Serialization failed!");
            }
        }

        return valueData;
    }
}

/**
 * Decodes value objects from either XML string or compressed base64 strings
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ValueDecoder implements ExceptionListener {

    /**
     * indicates whether XML decoding is possible
     */
    private boolean decodingErrorXML = false;
    /**
     * the XML decoder to use
     */
    private XMLDecoder decoder;

    /**
     * Constructor.
     */
    public ValueDecoder() {
    }

    @Override
    public void exceptionThrown(Exception e) {
        decodingErrorXML = true;
    }

    /**
     * decodes serializable value from compressed base64 string
     * @param value the value that is to be decoded
     * @return the value object
     */
    private Object decodeFromBase64(String value) {
        Object result = null;

        try {
            result = Base64.decodeToObject(value, VRL.getInternalPluginClassLoader());
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        return result;
    }

    /**
     * Decodes serializable value.
     * <p>
     * The decoder tries XML deserialization first. If errors occur it tries
     * binary deserialization.
     * </p>
     * @param value the value that is to be decoded
     * @return the value object
     */
    public Object decodeParamValue(String data) {
        Object valueObject;
        ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes());

        valueObject = decoder = new XMLDecoder(in, null, this);
        
        Exception exception = null;

        try {
            valueObject = decoder.readObject();
        } catch (Exception ex) {
            exception = ex;
            decodingErrorXML = true;
        }
        decoder.close();

        if (decodingErrorXML) {
            System.out.println(">> AbstractParameter:"
                    + " can't use XML persistence. Using Base64!");
            valueObject = decodeFromBase64(data);
        }
        
        if (valueObject == null && exception!=null) {
            System.err.println(">> AbstractParameter: error while decoding param:");
            exception.printStackTrace(System.err);
        }

        return valueObject;
    }
}
