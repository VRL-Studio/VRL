/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff1;

import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class CEList {

    private List<CodeEntityWithIndex> entities = new ArrayList<>();
    private int index = 0;
    private int id = 0;
    private CompilationUnitDeclaration root;

    /**
     * empty constructor
     */
    public CEList() {
    }

    /**
     *
     * @param entities names of CodeEntityWithIndex
     */
    public CEList(List<CodeEntityWithIndex> entities) {
        this.entities = new ArrayList<>(entities);
    }

    /**
     *
     * @param entities names of CodeEntities
     * @param index index of current CodeEntity
     */
    public CEList(List<CodeEntityWithIndex> entities, int index) {
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
    public CEList(CEList entities, Boolean bool) {

        if (bool) {
            //this.entities = new ArrayList<>(entities.getEntities());
            this.entities.clear();
            for (int i = 0; i < entities.size(); i++) {
                this.entities.add(new CodeEntityWithIndex(entities.get(i))); // clone()
            }
        } else {
            this.entities = entities.getEntities();
        }
        this.index = entities.index;
    }

    public CEList(CompilationUnitDeclaration root, int id) {
        this.entities = convertTreeToList(root, id);
        this.id = id;
    }

    public CEList(CEList entities) {
        this.index = entities.index;
        this.entities = convertTreeToList(root, id);

    }

    /**
     *
     * @param entities CodeEntityList with CodeEntities and index
     * @param index index of current CodeEntity
     */
    public CEList(CEList entities, int index) {
        this.entities = new ArrayList<>(entities.entities);
        if (index > -1 && index < entities.size()) {
            this.index = index;
        }
    }

    /**
     *
     * @param entity Code Entity with index 0
     */
    public CEList(CodeEntity entity) {
        this.entities = new ArrayList<>();
        this.entities.add(new CodeEntityWithIndex(0, entity));
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
     * @param i index in the names
     * @return CodeEntity with the given index
     */
    public CodeEntityWithIndex get(int i) {
        return entities.get(i);
    }

    public CodeEntityWithIndex get(CodeEntityWithIndex codeEntityWithIndex) {
        int i = entities.indexOf(codeEntityWithIndex);
        return entities.get(i);
    }

    public CodeEntityWithIndex getCurrentCodeEntityWithIndex() {
        return entities.get(index);
    }

    public CodeEntity getCodeEntity(int i) {
        return entities.get(i).getCodeEntity();
    }

    /**
     *
     * @param index index in the source names
     * @param entity CodeEntity to add in the names
     */
    public void add(int index, CodeEntity entity) {
        entities.add(new CodeEntityWithIndex(index, entity));
    }

    /**
     *
     * @param enityWithIndex Entity with Index
     */
    public void add(CodeEntityWithIndex enityWithIndex) {
        entities.add(enityWithIndex);
    }

    /**
     *
     * @param i index of the CodeEntityWithIndex to remove
     */
    public void remove(int i) {
        entities.remove(i);
    }

    /**
     *
     * @param e CodeEntityWithIndex to remove
     */
    public void remove(CodeEntityWithIndex e) {
        entities.remove(e);
    }

    /**
     *
     * @param index
     * @param e
     */
    public void remove(int index, CodeEntity e) {
        entities.remove(new CodeEntityWithIndex(index, e));
    }

    /**
     *
     * @param pos
     * @param entity CodeEntity to add on the given position
     */
    public void addOnPos(int pos, CodeEntityWithIndex entity) {
        entities.add(pos, entity);
    }

    public void setCodeEntityWithIndexOnCurrPos(CodeEntityWithIndex entity) {
        entities.set(index, entity);
    }

    /**
     *
     * @param pos
     * @param entity CodeEntity to set on the given position
     */
    public void setOnPos(int pos, CodeEntityWithIndex entity) {
        entities.set(pos, entity);
    }

    public void setCodeEntityOnPos(int index, int i, CodeEntity codeEntity) {
        entities.set(index, new CodeEntityWithIndex(i, codeEntity));
    }

    /**
     *
     * @param codeEntity to set on the current index in the list
     */
    public void setCodeEntityInListOnCurrPos(CodeEntity codeEntity) {
        CodeEntityWithIndex entity = this.getCurrentCodeEntityWithIndex();
        entity.setCodeEntity(codeEntity);
        entities.set(index, entity);
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

    public void updateIndex() {
        if (this.index > this.size()) {
            this.index = this.size() - 1;
        } else if (this.index < 0) {
            this.index = 0;
        }

    }

    /**
     *
     * @return names of CodeEnitities
     */
    public List<CodeEntityWithIndex> getEntities() {
        return entities;
    }

    /**
     *
     * @param entities names of CodeEntities to set
     */
    public void setEntities(List<CodeEntityWithIndex> entities) {
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
        final CEList other = (CEList) obj;
        if (this.size() != other.size()) {
            return false;
        }

        Boolean bool = true;

        for (int i = 0; i < this.size(); i++) {
            if (!compareNames(this.get(i), other.get(i))) { // vergleiche die CodeEntites index ist errelevant
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

    @Override
    public String toString() {
        return "CodeEntityList{" + "entities=" + entities + '}';
    }

    private String getEntityName(CodeEntityWithIndex entity) {
        return SimilarityMetric.getCodeEntityName(entity.getCodeEntity());
    }

    private String getCodeEntityName(CodeEntity entity) {
        return SimilarityMetric.getCodeEntityName(entity);
    }

    public String getEntityName(int i) {
        return getEntityName(this.entities.get(i));
    }

    private boolean compareNames(CodeEntityWithIndex codeEntity1, CodeEntityWithIndex codeEntity2) {

        if (codeEntity1 == null || codeEntity2 == null || codeEntity1.getCodeEntity().getClass() != codeEntity2.getCodeEntity().getClass()) {
            return false;
        }
        return getEntityName(codeEntity1).equals(getEntityName(codeEntity2));
    }

    public boolean compNames(CodeEntityWithIndex codeEntity1, CodeEntityWithIndex codeEntity2) {
        return getEntityName(codeEntity1).equals(getEntityName(codeEntity2));
    }

    public boolean compNames(CodeEntity codeEntity1, CodeEntity codeEntity2) {
        return getCodeEntityName(codeEntity1).equals(getCodeEntityName(codeEntity2));
    }

    public ArrayList<CodeEntity> getListWithCodeEntities() {
        ArrayList<CodeEntity> codeEntities = new ArrayList<>();

        if (!entities.isEmpty()) {
            for (CodeEntityWithIndex codeEntityWithIndex : entities) {
                codeEntities.add(codeEntityWithIndex.getCodeEntity());
            }
        }
        return codeEntities;
    }

    public ArrayList<CodeEntity> getListWithCodeEntities(List<CodeEntityWithIndex> entities) {
        ArrayList<CodeEntity> codeEntities = new ArrayList<>();

        if (!entities.isEmpty()) {
            for (CodeEntityWithIndex codeEntityWithIndex : entities) {
                codeEntities.add(codeEntityWithIndex.getCodeEntity());
            }
        }
        return codeEntities;
    }

    public ArrayList<String> getCodeEntitieNames() {
        ArrayList<String> names = new ArrayList<>();

        if (!entities.isEmpty()) {
            for (CodeEntityWithIndex codeEntityWithIndex : entities) {
                names.add(getEntityName(codeEntityWithIndex));
            }
        }
        return names;
    }

    public void update(int firstIndex) { // erstes Element in der List ist der Root Element; neue Numerierung
        if (!entities.isEmpty()) {
            Scope root = (Scope) entities.get(0).getCodeEntity();
            entities.clear();
            root.visitScopeAndAllSubElements((CodeEntity e) -> {
                if (e instanceof Scope || e instanceof Variable) {
                    entities.add(new CodeEntityWithIndex(0, e));
                }
            });
            for (int i = firstIndex; i < entities.size() + firstIndex; i++) {
                entities.get(i - firstIndex).setTreeIndex(i);
            }
        }
    }

    public void updateList() {
         if (!entities.isEmpty()) {
             
         }
    }

    public ArrayList<CodeEntity> getSubListWithCodeEntities(CodeEntityWithIndex sublistRoot) {
        int rootIndex = getEntities().indexOf(sublistRoot);
        return getListWithCodeEntities(this.entities.subList(rootIndex, size()));
    }

    public ArrayList<CodeEntityWithIndex> getSubtree(CodeEntityWithIndex node) {
        ArrayList<CodeEntityWithIndex> subtree = new ArrayList<>();
        ArrayList<CodeEntity> helpList = new ArrayList<>();
        Scope subtreeRoot = (Scope) node.getCodeEntity();

        subtreeRoot.visitScopeAndAllSubElements((CodeEntity e) -> {
            if (e instanceof Scope || e instanceof Variable) {
                if (this.getListWithCodeEntities().contains(e)) {
                    helpList.add(e);
                }
            }
        });

        int count = helpList.size();
        int b = this.entities.indexOf(node);

        for (int i = b; i < b + count; i++) {
            if (helpList.contains(entities.get(i).getCodeEntity())) {
                subtree.add(this.entities.get(i));
            }
        }

        return subtree;
    }

    public ArrayList<CodeEntityWithIndex> convertTreeToList(Scope scope, int index) { // TODO Set Index optimieren
        ArrayList<CodeEntityWithIndex> codeEntities = new ArrayList();
        scope.visitScopeAndAllSubElements(new Consumer<CodeEntity>() {
            
            int i = index;

            public void accept(CodeEntity e) {
                if (e instanceof Scope || e instanceof Variable) {
                    codeEntities.add(new CodeEntityWithIndex(i++, e));
                }
            }
        });
        
        return codeEntities;
    }

}
