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
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import eu.mihosoft.vrl.lang.model.Variable;
import static eu.mihosoft.vrl.lang.model.diff.MainClass.fromCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.groovy.control.SourceUnit;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class CodeEntityList {

    private List<CodeEntity> entities = new ArrayList<>();
    private int index;

    /**
     * empty constructor
     */
    public CodeEntityList() {
    }

    public CodeEntityList(CompilationUnitDeclaration root) {
        index = 0;
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

        if (bool && !entities.getEntities().isEmpty()) {

            CompilationUnitDeclaration cuDecl;
            if (entities.get(0) instanceof CompilationUnitDeclaration) {
                cuDecl = (CompilationUnitDeclaration) entities.get(0);
            } else {
                cuDecl = (CompilationUnitDeclaration) getRoot(entities.get(0));
            }
            CompilationUnitDeclaration cudClone;
            try {
                cudClone = clone(cuDecl);
                this.entities = convertTreeToList(cudClone);
            } catch (Exception ex) {
                Logger.getLogger(CodeEntityList.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        if (index < this.size() + 1 && index > -1) {
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

    /**
     *
     * @return list with code entity names
     */
    public ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<>();

        for (CodeEntity codeEntity : getEntities()) {
            names.add(getEntityName(codeEntity));
        }

        return names;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.entities);
        hash = 97 * hash + this.index;
        return hash;
    }

    /**
     *
     * @param entity code entity
     * @return name as string
     */
    private String getEntityName(CodeEntity entity) {
        return SimilarityMetric.getCodeEntityName(entity);
    }

    /**
     *
     * @param i position in the list with code entities
     * @return name of code entity with pos i as string
     */
    public String getEntityName(int i) {
        return getEntityName(this.entities.get(i));
    }

    /**
     *
     * @param codeEntity1 code entity
     * @param codeEntity2 code entity
     * @return true if entity names are equal and the code entities are from the
     * same type
     */
    public boolean compareNames(CodeEntity codeEntity1, CodeEntity codeEntity2) {

        if (codeEntity1 == null || codeEntity2 == null || codeEntity1.getClass() != codeEntity2.getClass()) {
            return false;
        }
        return getEntityName(codeEntity1).equals(getEntityName(codeEntity2));
    }

    /**
     *
     * @param codeEntity1 code entity
     * @param codeEntity2 code entity
     * @return true if entity names are equal
     */
    public boolean compNames(CodeEntity codeEntity1, CodeEntity codeEntity2) {
        return getEntityName(codeEntity1).equals(getEntityName(codeEntity2));
    }

    /**
     *
     * @param scope root element
     * @return list of code entities - scopes and variables
     */
    private ArrayList<CodeEntity> convertTreeToList(Scope scope) {
        ArrayList<CodeEntity> codeEntities = new ArrayList();
        scope.visitScopeAndAllSubElements((CodeEntity e) -> {
            if (e instanceof Scope || e instanceof Variable) {
                codeEntities.add(e);
            }
        });

        return codeEntities;
    }

    /**
     *
     * @param scope root element
     * @return list of all code entities
     */
    public static ArrayList<CodeEntity> convertTreeToListAllElems(Scope scope) {
        ArrayList<CodeEntity> codeEntities = new ArrayList();
        scope.visitScopeAndAllSubElements((CodeEntity e) -> {
            codeEntities.add(e);
            if (e instanceof Scope) {
                Scope s = (Scope) e;
                codeEntities.addAll(s.getVariables());
            }
            if (e instanceof Invocation) {
                Invocation inv = (Invocation) e;
                codeEntities.addAll(inv.getArguments());
            }
        });

        for (int i = 0; i < codeEntities.size(); i++) {
            System.out.println(i + ": " + SimilarityMetric.getCodeEntityName(codeEntities.get(i)) + " ---> Type: " + codeEntities.get(i).getClass().getSimpleName());
        }

        return codeEntities;
    }

    public static int subtreeSize(Scope root) {
        ArrayList<CodeEntity> codeEntities = new ArrayList();
        root.visitScopeAndAllSubElements((CodeEntity e) -> {
            if (e instanceof Scope || e instanceof Variable) {
                codeEntities.add(e);
            }
        });
        return codeEntities.size();

    }

    /**
     *
     * @param codeEntity code entity
     */
    public void updateCodeEntityList(CodeEntity codeEntity) {
        ArrayList<CodeEntity> codeEntities = new ArrayList();
        CompilationUnitDeclaration cudClone = (CompilationUnitDeclaration) getRoot(codeEntity);
        try {
            cudClone = clone(cudClone);
        } catch (Exception ex) {
            Logger.getLogger(CodeEntityList.class.getName()).log(Level.SEVERE, null, ex);
        }

        cudClone.visitScopeAndAllSubElements((CodeEntity e) -> {
            if (e instanceof Scope || e instanceof Variable) {
                codeEntities.add(e);
            }
        });

        for (int i = 0; i < codeEntities.size(); i++) {
            System.out.println("Update: " + SimilarityMetric.getCodeEntityName(codeEntities.get(i)));
        }
        this.setEntities(codeEntities);
    }

    /**
     *
     * @param codeEntity code entity
     */
    public void updateCodeEntityListAllEntities(CodeEntity codeEntity) {
        ArrayList<CodeEntity> codeEntities = new ArrayList();
        CompilationUnitDeclaration cudClone = (CompilationUnitDeclaration) getRoot(codeEntity);
        try {
            cudClone = clone(cudClone);
        } catch (Exception ex) {
            Logger.getLogger(CodeEntityList.class.getName()).log(Level.SEVERE, null, ex);
        }

        cudClone.visitScopeAndAllSubElements((CodeEntity e) -> {
            codeEntities.add(e);
        });

        for (int i = 0; i < codeEntities.size(); i++) {
            System.out.println(i + ": " + SimilarityMetric.getCodeEntityName(codeEntities.get(i)));
            System.out.println("Type: " + codeEntities.get(i).getClass());
        }
        this.setEntities(codeEntities);
    }

    /**
     *
     * @param codeEntity child node
     * @return root of the tree
     */
    public static CodeEntity getRoot(CodeEntity codeEntity) {
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

    private CompilationUnitDeclaration clone(CompilationUnitDeclaration input) throws Exception {
        return groovy2Model(model2Groovy(input));
    }
}
