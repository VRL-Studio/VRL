/* 
 * CopyrightInfoImpl.java
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

package eu.mihosoft.vrl.system;

import java.util.ArrayList;
import java.util.Collection;


/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CopyrightInfoImpl implements CopyrightInfo {

    private Collection<CopyrightInfo> thirdPartyCopyrightInfos =
            new ArrayList<CopyrightInfo>();
    private String projectName;
    private String projectPage;
    private String copyrightStatement;
    private LicenseInfo licenseInfo;
    
    private String plainText;

    public CopyrightInfoImpl() {
    }
    
    public CopyrightInfoImpl(String plainText) {
        this.plainText = plainText;
    }

    public CopyrightInfoImpl(String projectName,
            String copyrightStatement,
            String projectPage,
            String licenseName, String licenseText) {

        this.projectName = projectName;
        this.copyrightStatement = copyrightStatement;
        this.projectPage = projectPage;
        this.licenseInfo = new LisenseInfoImpl(licenseName, licenseText);
    }

    public CopyrightInfoImpl addThirdPartyCopyrightInfo(CopyrightInfo info) {
        thirdPartyCopyrightInfos.add(info);
        return this;
    }

    public void setLicense(String licenseName, String licenseText) {
        this.licenseInfo = new LisenseInfoImpl(licenseName, licenseText);
    }

    @Override
    public String getCopyrightStatement() {
        return copyrightStatement;
    }

    @Override
    public LicenseInfo getLicense() {
        return licenseInfo;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public Iterable<CopyrightInfo> getThirdPartyCopyrightInfos() {
        return thirdPartyCopyrightInfos;
    }

    /**
     * @param thirdPartyCopyrightInfos the thirdPartyCopyrightInfos to set
     */
    public void setThirdPartyCopyrightInfos(Collection<CopyrightInfo> thirdPartyCopyrightInfos) {
        this.thirdPartyCopyrightInfos = thirdPartyCopyrightInfos;
    }

    /**
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @param copyrightStatement the copyrightStatement to set
     */
    public void setCopyrightStatement(String copyrightStatement) {
        this.copyrightStatement = copyrightStatement;
    }

    @Override
    public String getProjectPage() {
        return projectPage;
    }

    /**
     * @param projectPage the projectPage to set
     */
    public void setProjectPage(String projectPage) {
        this.projectPage = projectPage;
    }
    
    public boolean isPlainText() {
        return plainText!=null && !plainText.isEmpty();
    }

    /**
     * @return the plainText
     */
    public String getPlainText() {
        return plainText;
    }

    /**
     * @param plainText the plainText to set
     */
    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }
}
class LisenseInfoImpl implements LicenseInfo {

    private String licenseName;
    private String licenseText;

    public LisenseInfoImpl() {
    }

    public LisenseInfoImpl(String licenseName, String licenseText) {
        this.licenseName = licenseName;
        this.licenseText = licenseText;
    }

    /**
     * @return the licenseName
     */
    @Override
    public String getLicenseName() {
        return licenseName;
    }

    /**
     * @param licenseName the licenseName to set
     */
    public void setLicenseName(String licenseName) {
        this.licenseName = licenseName;
    }

    /**
     * @return the licenseText
     */
    @Override
    public String getLicenseText() {
        return licenseText;
    }

    /**
     * @param licenseText the licenseText to set
     */
    public void setLicenseText(String licenseText) {
        this.licenseText = licenseText;
    }
}
