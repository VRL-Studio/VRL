/* 
 * CopyrightInfo2HTML.java
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
 * Third, add an additional notice, stating that you modified VRL. A suitable
 * notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Please cite the publication(s) listed below.
 *
 * Publications:
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */

package eu.mihosoft.vrl.system;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class CopyrightInfo2HTML {

    private String copyrightText = "";
    private String textFont = "<font size=+0>";
    private String licenseHeaderFont = "<font size=+1>";
    private String webpageHeaderFont = "<font size=+1>";
    private String projectHeaderFont = "<font size=+2>";
    private String thirdpartyProjectHeaderFont = "<font size=+1>";
    private String thirdPartyHeaderFont = "<font size=+2>";

    public CopyrightInfo2HTML(CopyrightInfo copyrightInfo) {
        setInfo(copyrightInfo);
    }

    private void setInfo(CopyrightInfo info) {
//        this.copyrightInfo = info;

        if (info.isPlainText()) {
            copyrightText = "<pre>" + info.getPlainText() + "</pre>";
        } else {

            String thirdpartyText =
                    "<br><p>" + getThirdPartyHeaderFont() + "<b>Thirdparty Licenses:</b></p><br>";

            for (CopyrightInfo i : info.getThirdPartyCopyrightInfos()) {
                thirdpartyText += new CopyrightInfo2HTMLConverter(this, i, false).toString();
                thirdpartyText += "<br>";
            }

            copyrightText += new CopyrightInfo2HTMLConverter(this, info, true).toString();
            copyrightText += thirdpartyText;
        }
    }

    @Override
    public String toString() {
        return copyrightText;
    }

    /**
     * @return the textFont
     */
    public String getTextFont() {
        return textFont;
    }

    /**
     * @param textFont the textFont to set
     */
    public void setTextFont(String textFont) {
        this.textFont = textFont;
    }

    /**
     * @return the licenseHeaderFont
     */
    public String getLicenseHeaderFont() {
        return licenseHeaderFont;
    }

    /**
     * @param licenseHeaderFont the licenseHeaderFont to set
     */
    public void setLicenseHeaderFont(String licenseHeaderFont) {
        this.licenseHeaderFont = licenseHeaderFont;
    }

    /**
     * @return the projectHeaderFont
     */
    public String getProjectHeaderFont() {
        return projectHeaderFont;
    }

    /**
     * @param projectHeaderFont the projectHeaderFont to set
     */
    public void setProjectHeaderFont(String projectHeaderFont) {
        this.projectHeaderFont = projectHeaderFont;
    }

    /**
     * @return the thirdpartyProjectHeaderFont
     */
    public String getThirdpartyProjectHeaderFont() {
        return thirdpartyProjectHeaderFont;
    }

    /**
     * @param thirdpartyProjectHeaderFont the thirdpartyProjectHeaderFont to set
     */
    public void setThirdpartyProjectHeaderFont(String thirdpartyProjectHeaderFont) {
        this.thirdpartyProjectHeaderFont = thirdpartyProjectHeaderFont;
    }

    /**
     * @return the thirdPartyHeaderFont
     */
    public String getThirdPartyHeaderFont() {
        return thirdPartyHeaderFont;
    }

    /**
     * @param thirdPartyHeaderFont the thirdPartyHeaderFont to set
     */
    public void setThirdPartyHeaderFont(String thirdPartyHeaderFont) {
        this.thirdPartyHeaderFont = thirdPartyHeaderFont;
    }

    /**
     * @return the webpageHeaderFont
     */
    public String getWebpageHeaderFont() {
        return webpageHeaderFont;
    }

    /**
     * @param webpageHeaderFont the webpageHeaderFont to set
     */
    public void setWebpageHeaderFont(String webpageHeaderFont) {
        this.webpageHeaderFont = webpageHeaderFont;
    }
}

class CopyrightInfo2HTMLConverter {

    private CopyrightInfo2HTML info;
    private CopyrightInfo cInfo;
    private boolean base;

    public CopyrightInfo2HTMLConverter(CopyrightInfo2HTML info, CopyrightInfo cInfo, boolean base) {
        this.info = info;
        this.cInfo = cInfo;
        this.base = base;
    }

    @Override
    public String toString() {

        String fontSize = info.getThirdpartyProjectHeaderFont();

        if (base) {
            fontSize = info.getProjectHeaderFont();
        }

        String text =
                "<p>" + fontSize + "<b>Project: " + cInfo.getProjectName() + "</b></p>"
                + "<p>" + info.getTextFont() + cInfo.getCopyrightStatement() + "</p>"
                + "<p>" + info.getWebpageHeaderFont()
                + "<b>Link:</b> " + info.getTextFont()
                + cInfo.getProjectPage() + "</p>"
                + "<p>" + info.getLicenseHeaderFont()
                + "<b>License:</b> " + info.getTextFont()
                + cInfo.getLicense().getLicenseName() + "</p>"
                + "<p>" + info.getTextFont() + cInfo.getLicense().getLicenseText() + "</p>";

        return text;

    }
}
