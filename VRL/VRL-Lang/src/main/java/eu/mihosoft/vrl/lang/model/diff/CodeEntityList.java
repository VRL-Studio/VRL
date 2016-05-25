/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.vrl.instrumentation.CompositeTransformingVisitorSupport;
import eu.mihosoft.vrl.instrumentation.VRLVisualizationTransformation;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import eu.mihosoft.vrl.lang.model.Variable;
import static eu.mihosoft.vrl.lang.model.diff.MainClass.fromCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.codehaus.groovy.control.SourceUnit;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class CodeEntityList { // anpassen an CELIST

    private List<CodeEntity> entities = new ArrayList<>();
    private int index = 0;

    /**
     * empty constructor
     */
    public CodeEntityList() {
    }

    public CodeEntityList(CompilationUnitDeclaration root) {
        this.entities = convertTreeToList(root);
    }

    /**
     *
     * @param entities list of CodeEntities
     */
    public CodeEntityList(List<CodeEntity> entities) {
        this.entities = new ArrayList<>(entities);
    }

    /**
     *
     * @param entities list of CodeEntities
     * @param index index of current CodeEntity
     */
    public CodeEntityList(List<CodeEntity> entities, int index) {
        this.entities = new ArrayList<>(entities);

        if (index > -1 && index < entities.size()) {
            this.index = index;
        }
    }

    /**
     *
     * @param entities CodeEntityList with CodeEntities and index
     * @param bool ?
     */
    public CodeEntityList(CodeEntityList entities, Boolean bool) {

        if (bool) {
            this.entities = new ArrayList<>(entities.getEntities());
        } else {
            this.entities = entities.getEntities();
        }
        this.index = entities.index;
    }

    /**
     *
     * @param entities CodeEntityList with CodeEntities and index
     * @param index index of current CodeEntity
     */
    public CodeEntityList(CodeEntityList entities, int index) {
        this.entities = new ArrayList<>(entities.entities);
        if (index > -1 && index < entities.size()) {
            this.index = index;
        }

    }

    /**
     *
     * @return size of the List of CodeEntities
     */
    public int size() {
        return entities.size();
    }

    /**
     *
     * @param i index in the list
     * @return CodeEntity with the given index
     */
    public CodeEntity get(int i) {
        return entities.get(i);
    }

    /**
     *
     * @param entity CodeEntity to add in the list
     */
    public void add(CodeEntity entity) {
        entities.add(entity);
    }

    /**
     *
     * @param i index of the CodeEntity to remove
     */
    public void remove(int i) {
        entities.remove(i);
    }

    /**
     *
     * @param e CodeEntity to remove
     */
    public void remove(CodeEntity e) {
        entities.remove(e);
    }

    /**
     *
     * @param i index
     * @param entity CodeEntity to add on the given position
     */
    public void addOnPos(int i, CodeEntity entity) {
        entities.add(i, entity);
    }

    /**
     *
     * @param i index
     * @param entity CodeEntity to set on the given position
     */
    public void setOnPos(int i, CodeEntity entity) {
        entities.set(i, entity);
    }


    /**
     *
     * @return current index
     */
    public int getIndex() {
        return index;
    }

    /**
     *
     * @param index current index to set
     */
    public void setIndex(int index) {
        if (index < this.size() && index > -1) {
            this.index = index;
        }
    }

    public void increaseIndex() {
        this.index++;

    }

    public void decreaseIndex() {
        this.index--;
    }

    /**
     *
     * @return list of CodeEnitities
     */
    public List<CodeEntity> getEntities() {
        return entities;
    }

    /**
     *
     * @param entities list of CodeEntities to set
     */
    public void setEntities(List<CodeEntity> entities) {
        this.entities = entities;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final CodeEntityList other = (CodeEntityList) obj;
        if (this.size() != other.size()) {
            return false;
        }

        Boolean bool = true;

        for (int i = 0; i < this.size(); i++) {
            if (!compareNames(this.get(i), other.get(i))) {
                bool = false;
                break;
            } else {
            }// TODO: Struktur des Objektes überpüren compareStructure()
        }

        return bool;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.entities);
        hash = 97 * hash + this.index;
        return hash;
    }

    private String getEntityName(CodeEntity entity) {
        return SimilarityMetric.getCodeEntityName(entity);
    }

    public String getEntityName(int i) {
        return getEntityName(this.entities.get(i));
    }

    private boolean compareNames(CodeEntity codeEntity1, CodeEntity codeEntity2) {

        if (codeEntity1 == null || codeEntity2 == null || codeEntity1.getClass() != codeEntity2.getClass()) {
            return false;
        }
        return getEntityName(codeEntity1).equals(getEntityName(codeEntity2));
    }

    public boolean compNames(CodeEntity codeEntity1, CodeEntity codeEntity2) {
        return getEntityName(codeEntity1).equals(getEntityName(codeEntity2));
    }

    private ArrayList<CodeEntity> convertTreeToList(Scope scope) { 
        ArrayList<CodeEntity> codeEntities = new ArrayList();
        scope.visitScopeAndAllSubElements((CodeEntity e) -> {
            if (e instanceof Scope || e instanceof Variable) {
                codeEntities.add(e);
            }
        });

        return codeEntities;
    }

    public void updateList(CodeEntity codeEntity) {
            ArrayList<CodeEntity> codeEntities = new ArrayList();
            Scope root = (Scope) codeEntity;

            root.visitScopeAndAllSubElements((CodeEntity e) -> {
                if (e instanceof Scope || e instanceof Variable) {
                    codeEntities.add(e);
                }
            });
            
           for (int i = 0; i < codeEntities.size(); i++) {
               System.out.println("Update: " + SimilarityMetric.getCodeEntityName(codeEntities.get(i)) );
            
        }
            setEntities(codeEntities);
    }
    
    public CodeEntity getRoot(CodeEntity codeEntity) {
        CodeEntity ce = codeEntity;
        while (ce.getParent() != null) {
            ce = ce.getParent();
        }
        return ce;
    }
    
    public CompilationUnitDeclaration groovy2Model(String groovyCode) throws Exception {
        SourceUnit src = fromCode(groovyCode);
        CompositeTransformingVisitorSupport visitor = VRLVisualizationTransformation
                .init(src);
        visitor.visitModuleNode(src.getAST());
        CompilationUnitDeclaration model = (CompilationUnitDeclaration) visitor
                .getRoot().getRootObject();

        return model;
    }

    public String model2Groovy(CompilationUnitDeclaration cuDecl) throws Exception {
        return Scope2Code.getCode(cuDecl);
    }

    public CompilationUnitDeclaration clone(CompilationUnitDeclaration input) throws Exception {
        return groovy2Model(model2Groovy(input));
    }
}
