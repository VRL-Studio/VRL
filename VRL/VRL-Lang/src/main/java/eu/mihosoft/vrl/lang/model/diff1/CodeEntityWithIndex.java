/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff1;

import eu.mihosoft.vrl.lang.model.CodeEntity;
import java.util.Objects;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class CodeEntityWithIndex {

    private int treeIndex;
    private CodeEntity codeEntity;

    public CodeEntityWithIndex(int treeIndex, CodeEntity codeEntity) {
        this.treeIndex = treeIndex;
        this.codeEntity = codeEntity;
    }
    
    public CodeEntityWithIndex(CodeEntityWithIndex codeEntityWithIndex) {
        this.treeIndex = codeEntityWithIndex.treeIndex;
        this.codeEntity = codeEntityWithIndex.codeEntity;
    }

    public int getTreeIndex() {
        return treeIndex;
    }

    public void setTreeIndex(int treeIndex) {
        this.treeIndex = treeIndex;
    }

    public CodeEntity getCodeEntity() {
        return codeEntity;
    }

    public void setCodeEntity(CodeEntity codeEntity) {
        this.codeEntity = codeEntity;
    }

    public String getName(){
        return SimilarityMetric.getCodeEntityName(codeEntity);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.codeEntity);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CodeEntityWithIndex other = (CodeEntityWithIndex) obj;
        return Objects.equals(this.codeEntity, other.codeEntity);
    }
    
}
