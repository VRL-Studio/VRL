/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

//import eu.mihosoft.vrl.lang.VLangUtilsNew;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.ArgumentType;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.Comment;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.ElseIfDeclaration;
import eu.mihosoft.vrl.lang.model.IfDeclaration;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import eu.mihosoft.vrl.lang.model.ScopeInvocation;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration;
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.WhileDeclaration;
import static eu.mihosoft.vrl.lang.model.diff.MainClass.groovy2Model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class SimilarityMetric {

    public static void main(String[] args) throws Exception {

        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class ClassA {\n"
                + "  void meth(){}\n"
                + "  void c3(){\n"
                + "int j = 1; \n"
                + "j++; \n"
                + "for(int k = 0;k<10;k++){\n"
                + "j++;\n"
                + "}\n"
                + "}\n"
                + "  void c1(){}\n"
                + "}");
        CompilationUnitDeclaration targetModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;"
                + "public class ClassAA {\n"
                + "  void meth11(){}\n"
                + "  void c1(int j){\n"
                + "int i=0;\n"
                + "}\n"
                + "  void c3(){}\n"
                + "}"
        );

        CodeEntity sourceCodeEntity = sourceModel.getDeclaredClasses().get(0);
        CodeEntity targetCodeEntity = targetModel.getDeclaredClasses().get(0);
        double computeSimilarityMetric = computeSimilarityMetric(sourceCodeEntity, targetCodeEntity);
        System.out.println("Similarity from " + getName(sourceCodeEntity) + " and " + getName(targetCodeEntity) + " is " + computeSimilarityMetric);
    }

    /**
     *
     * @param str string
     * @return pairs of adjacent characters from the string
     */
    private static HashSet<String> pairs(String str) {

        String string = str.toUpperCase();

        HashSet<String> pairs = new HashSet();

        if (string.length() > 1) {
            for (int i = 0; i < string.length() - 1; i++) {
                String s = "" + string.charAt(i) + string.charAt(i + 1);
                pairs.add(s);
            }
        } else {
            for (int i = 0; i < string.length(); i++) {
                String s = "" + string.charAt(i);
                pairs.add(s);
            }
        }
        return pairs;
    }

    /**
     *
     * @param codeEntity
     * @return entiy name
     */
    public static String getName(CodeEntity codeEntity) {

        String name = "default";

        if (codeEntity instanceof Scope) {
            Scope scopeEntity = (Scope) codeEntity;
            if (scopeEntity instanceof CompilationUnitDeclaration) {
                CompilationUnitDeclaration compilationUnitDeclaration = (CompilationUnitDeclaration) scopeEntity;
                name = compilationUnitDeclaration.getPackageName();
                //name = VLangUtilsNew.shortNameFromFullClassName(compilationUnitDeclarationFullName(compilationUnitDeclaration.getName()));
            } else if (scopeEntity instanceof ClassDeclaration) {
                ClassDeclaration classDeclaration = (ClassDeclaration) scopeEntity;
                name = VLangUtils.shortNameFromFullClassName(classDeclaration.getName());
            } else if (scopeEntity instanceof MethodDeclaration) {
                MethodDeclaration methodDeclaration = (MethodDeclaration) scopeEntity;
                name = VLangUtils.shortNameFromFullClassName(methodDeclaration.getName());
            } else {
                System.out.println("This Scope is from type " + scopeEntity.getClass().toString());
                name = scopeEntity.getName();
            }
        } else if (codeEntity instanceof Variable) {
            Variable variableEntity = (Variable) codeEntity;
            name = variableEntity.getName();
        }

        return name;
    }

    /**
     * @param name full compilation unit declaration name
     * @return compilation unit declaration full name without .groovy
     */
    private static String compilationUnitDeclarationFullName(String name) {

        name = name.replace('.', '/');
        String[] path = name.split("/");

        String result = "";

        if (path.length > 0) {
            for (int i = 0; i < path.length - 1; i++) {
                if (i > 0) {
                    result += ".";
                }
                result += path[i];
            }
        }

        return result;
    }

    /**
     *
     * @param codeEntity code entity
     * @return code fragment as string
     */
    public static String getCodeFragment(CodeEntity codeEntity) {

        String codeFragment = "default";

        if (codeEntity instanceof Invocation) {
            Invocation invocationEntity = (Invocation) codeEntity;
            if (invocationEntity instanceof ScopeInvocation == false) { //?
//                if (invocationEntity instanceof ReturnStatementInvocation) {
//                    ReturnStatementInvocation rsi = (ReturnStatementInvocation) invocationEntity;
//                    codeFragment = rsi.getMethodName();
//                } else if (invocationEntity instanceof DeclarationInvocation) {
//                    codeFragment = invocationEntity.getMethodName();
//                } else {
//                    codeFragment = Scope2Code.getCode(invocationEntity);
//                }
                
                codeFragment = Scope2Code.getCode(invocationEntity);
            }
        } else if (codeEntity instanceof Argument) {
            
            Argument argumentEntity = (Argument) codeEntity;
            String name = "default";
            if (argumentEntity.getArgType().equals(ArgumentType.CONSTANT)) {
                name = argumentEntity.getType().getShortName();
            } else if (argumentEntity.getArgType().equals(ArgumentType.VARIABLE)) {
                name = argumentEntity.getVariable().get().getName();
            } else if (argumentEntity.getArgType().equals(ArgumentType.INVOCATION)) {
                name = argumentEntity.getInvocation().get().getMethodName();
            } else {
            }
            codeFragment = name;//argumentEntity.getArgType() + " " + name;
//        } else if (codeEntity instanceof ConstantValue) {
//            ConstantValue constantValueEntity = (ConstantValue) codeEntity;
//            codeFragment = constantValueEntity.getValue(Object.class).toString();
        } else if (codeEntity instanceof Comment) {
            Comment commentEntity = (Comment) codeEntity;
            codeFragment = commentEntity.getComment();
        }
        return codeFragment;
    }

    /**
     *
     * @param codeEntity code entity
     * @return name or code fragment
     */
    public static String getCodeEntityName(CodeEntity codeEntity) {

        String codeEntityName;

        if (codeEntity instanceof Scope || codeEntity instanceof Variable) {
            codeEntityName = getName(codeEntity);
        } else {
            codeEntityName = getCodeFragment(codeEntity);
        }
        return codeEntityName;
    }

    /**
     *
     * @param s1 old name
     * @param s2 new name
     * @return normalized value of name similarity (1 - names are identical)
     */
    public static double nameSimilarity(String s1, String s2) {

        HashSet<String> pairs1 = pairs(s1);
        HashSet<String> pairs2 = pairs(s2);
        double union = pairs1.size() + pairs2.size();
        pairs1.retainAll(pairs2);
        double intersection = pairs1.size();

        return intersection * 2.0 / union;
    }

    /**
     *
     * @param ce1 target CodeEntity
     * @param ce2 source CodeEntity
     * @return normalized value of name similarity
     */
    public static double nameSimilarity(CodeEntity ce1, CodeEntity ce2) {

        HashSet<String> pairs1 = pairs(getCodeEntityName(ce1));
        HashSet<String> pairs2 = pairs(getCodeEntityName(ce2));
        double union = pairs1.size() + pairs2.size();
        pairs1.retainAll(pairs2);
        double intersection = pairs1.size();

        return intersection * 2.0 / union;
    }

    /**
     *
     * @param listOfComments1 old comment
     * @param listOfComments2 new comment
     * @return normalized value of comment similarity
     */
    private static double commentSimilarity(List<Comment> listOfComments1, List<Comment> listOfComments2) {

        List<String> comments1 = new ArrayList();
        List<String> comments2 = new ArrayList();
        for (Comment comment : listOfComments1) {
            String[] words = comment.getComment().split(Pattern.quote(" "));
            comments1.addAll(Arrays.asList(words));
        }
        for (Comment comment : listOfComments2) {
            String[] words = comment.getComment().split(Pattern.quote(" "));
            comments2.addAll(Arrays.asList(words));
        }
        double lcsSize = (double) lcs(comments1, comments2).size();
        return 2.0 * lcsSize / ((double) (comments1.size() + comments2.size()));
    }

    /**
     *
     * @param ce1
     * @param ce2
     * @return similarity of comments
     */
    public static double commentSimilarity(CodeEntity ce1, CodeEntity ce2) {
        double commentSimilarity = 0.0;

        if (ce1 instanceof Scope && ce2 instanceof Scope) {
            Scope scope1 = (Scope) ce1;
            Scope scope2 = (Scope) ce2;

            commentSimilarity = commentSimilarity(scope1.getComments(), scope2.getComments());
        }
        return commentSimilarity;
    }

    /**
     *
     * @param string1 sequence of words
     * @param string2 sequence of words
     * @return similarity between sequences of words
     */
    private static double wordsSimilarity(String string1, String string2) {

        String[] words1 = string1.split(Pattern.quote(" "));
        String[] words2 = string2.split(Pattern.quote(" "));

        double lcsSize = (double) lcs(Arrays.asList(words1), Arrays.asList(words2)).size();
        return 2.0 * lcsSize / ((double) (words1.length + words2.length));
    }

    /**
     *
     * @param ce1
     * @param ce2
     * @return normalized value of package similarity
     */
    public static double packageSimilarity(CodeEntity ce1, CodeEntity ce2) {

        double packageSimilarity = 0.0;

        if (ce1 instanceof CompilationUnitDeclaration && ce2 instanceof CompilationUnitDeclaration) {
            CompilationUnitDeclaration cud1 = (CompilationUnitDeclaration) ce1;
            CompilationUnitDeclaration cud2 = (CompilationUnitDeclaration) ce2;
            packageSimilarity = packageSimilarity(cud1.getName(), cud2.getName());
        }
        return packageSimilarity;
    }

    /**
     *
     * @param p1 old packagename
     * @param p2 new packagename
     * @return normalized value of package similarity
     */
    private static double packageSimilarity(String p1, String p2) {

        String[] words1 = p1.split(Pattern.quote("."));
        String[] words2 = p2.split(Pattern.quote("."));
        double lcsSize = (double) lcs(Arrays.asList(words1), Arrays.asList(words2)).size();
        return 2.0 * lcsSize / ((double) (words1.length + words2.length));
    }

    /**
     *
     * @param a list with strings
     * @param b list with strings
     * @return longest common subsequence from the both lists
     */
    public static List<String> lcs(List<String> a, List<String> b) {
        List<String> x;
        List<String> y;

        int alen = a.size();
        int blen = b.size();
        if (alen == 0 || blen == 0) {
            return new ArrayList<>();
        } else if (a.get(alen - 1).equals(b.get(blen - 1))) {
            List<String> list = lcs(a.subList(0, alen - 1), b.subList(0, blen - 1));
            list.add(a.get(alen - 1));
            return list;
        } else {
            x = lcs(a, b.subList(0, blen - 1));
            y = lcs(a.subList(0, alen - 1), b);
        }
        return (x.size() > y.size()) ? x : y;
    }

    /**
     *
     * @param e1 source node
     * @param e2 target node
     * @param relationType relation type
     * @return normalized value of structure similarint: 1 - nodes have
     * identical structur
     */
    private static double structureSimilarity(CodeEntity e1, CodeEntity e2, String relationType) {

        System.out.println("Structure Similarity - relation type: " + '"' + relationType + '"');

        double structureSimilarity;
        HashSet<CodeEntity> e_of_r1 = getEntitiesOfRelation(e1, relationType);
        HashSet<CodeEntity> e_of_r2 = getEntitiesOfRelation(e2, relationType);

        if (e_of_r1.isEmpty() && e_of_r2.isEmpty()) {
            System.out.println("Empty Set!! ");
            pow++;

            String e1Name = getCodeEntityName(e1);
            String e2Name = getCodeEntityName(e2);
            if (e1 instanceof Scope || e1 instanceof Variable) {
                structureSimilarity = Math.pow(nameSimilarity(e1Name, e2Name), pow);
            } else {
                structureSimilarity = Math.pow(wordsSimilarity(e1Name, e2Name), pow);
            }
        } else {
            int bevoreCount = 0, afterCount = 0;

            Iterator<CodeEntity> i1 = e_of_r1.iterator();
            while (i1.hasNext()) {
                CodeEntity er1 = i1.next();
                String er1Name = getCodeEntityName(er1);
                Iterator<CodeEntity> i2 = e_of_r2.iterator();
                while (i2.hasNext()) {
                    CodeEntity er2 = i2.next();
                    String er2Name = getCodeEntityName(er2);
                    if (er1Name.equals(er2Name)) {
                        bevoreCount += getCount(e1, er1, relationType);
                        afterCount += getCount(e2, er2, relationType);
                        i1.remove();
                        i2.remove();
                    }
                }

            }
            int bevoreLeftCount = 0, afterLeftCount = 0;
            for (CodeEntity er1 : e_of_r1) {
                bevoreLeftCount += getCount(e1, er1, relationType);
            }
            for (CodeEntity er2 : e_of_r2) {
                afterLeftCount += getCount(e2, er2, relationType);
            }
            int min = Math.min(bevoreCount, afterCount);
            int max = Math.max(bevoreCount, afterCount);
            structureSimilarity = min * 1.0 / (double) (max + bevoreLeftCount + afterLeftCount);
        }
        System.out.println("StructureSim " + structureSimilarity);
        return structureSimilarity;
    }

    /**
     *
     * @param entity node
     * @param relationType typ of relation
     * @return list of entities related to the variableEntity with the given
     * relation type
     */
    private static HashSet getEntitiesOfRelation(CodeEntity entity, String relationType) {

        HashSet<CodeEntity> set = new HashSet();

        if (entity instanceof Scope) {
            Scope scope = (Scope) entity;
            switch (scope.getType().name()) {
                case "COMPILATION_UNIT":
                    CompilationUnitDeclaration cUnitDecl = (CompilationUnitDeclaration) scope;
                    switch (relationType) {
                        case "class":
                            System.out.println("COMPILATION_UNIT - Classes !!!");
                            set = new HashSet(cUnitDecl.getDeclaredClasses());
                            break;
//                        case "scope": // class ist ebenfalls ein scope ?!
//                            set = new HashSet(cUnitDecl.getScopes());
//                            System.out.println("COMPILATION_UNIT - Scopes !");
//                            break;
                        case "variable":
                            set = new HashSet(cUnitDecl.getVariables());
                            System.out.println("COMPILATION_UNIT - Variables !");
                            break;
                        case "comment":
                            set = new HashSet(cUnitDecl.getComments());
                            System.out.println("COMPILATION_UNIT - Comments !");
                            break;
                        case "parent":
                            set = new HashSet();
                            set.add(cUnitDecl.getParent());
                            System.out.println("COMPILATION_UNIT - Parent !");
                            break;
                    }
                    break;
                case "CLASS":
                    ClassDeclaration classDecl = (ClassDeclaration) scope;
                    System.out.println("CLASS");
                    switch (relationType) {
                        case "method":
                            set = new HashSet(classDecl.getDeclaredMethods());
                            System.out.println("CLASS - Methods !!!");
                            break;
                        case "implClass":
                            set = new HashSet(classDecl.getImplements().getTypes());
                            System.out.println("CLASS - Implemented Classes");
                            break;
//                        case "scope":
//                            set = new HashSet(classDecl.getScopes());
//                            System.out.println("CLASS - Scopes !");
//                            break;
                        case "variable":
                            set = new HashSet(classDecl.getVariables());
                            System.out.println("CLASS - Variables !");
                            break;
                        case "comment":
                            set = new HashSet(classDecl.getComments());
                            System.out.println("CLASS - Comments !");
                            break;
                        case "parent":
                            set = new HashSet();
                            set.add(classDecl.getParent());
                            System.out.println("CLASS - Parent !");
                            break;
                    }
                    break;

                case "METHOD":
                    MethodDeclaration methodDecl = (MethodDeclaration) scope;
                    switch (relationType) {
                        case "parameter":                                       //  TODO!!
                            System.out.println("METHOD - Parameters !");
                            set = new HashSet(methodDecl.getParameters().getParamenters());
                            break;
                        case "modifiers":
                            System.out.println("METHOD - Modifiers !!!");
                            set = new HashSet(methodDecl.getModifiers().getModifiers());
                            break;
//                        case "scope":
//                            set = new HashSet(methodDecl.getScopes());
//                            System.out.println("METHOD - Scopes !");
//                            break;
                        case "variable":
                            set = new HashSet(methodDecl.getVariables());
                            System.out.println("METHOD - Variables !");
                            break;
                        case "comment":
                            set = new HashSet(methodDecl.getComments());
                            System.out.println("METHOD - Comments !");
                            break;
                        case "parent":
                            set = new HashSet();
                            set.add(methodDecl.getParent());
                            System.out.println("METHOD - Parent !");
                            break;
                    }
                    break;

                case "FOR":
                    SimpleForDeclaration forDecl = (SimpleForDeclaration) scope;
                    switch (relationType) {
                        case "scope":
                            set = new HashSet(forDecl.getScopes());
                            System.out.println("FOR - Scopes !");
                            break;
                        case "variable":
                            set = new HashSet(forDecl.getVariables());
                            System.out.println("FOR - Variables !");
                            break;
                        case "comment":
                            set = new HashSet(forDecl.getComments());
                            System.out.println("FOR - Comments !");
                            break;
                        case "parent":
                            set = new HashSet();
                            set.add(forDecl.getParent());
                            System.out.println("FOR - Parent !");
                            break;
                    }
                    break;
                case "WHILE":
                    WhileDeclaration whileDecl = (WhileDeclaration) scope;
                    switch (relationType) {
                        case "scope":
                            set = new HashSet(whileDecl.getScopes());
                            System.out.println("WHILE - Scopes !");
                            break;
                        case "variable":
                            set = new HashSet(whileDecl.getVariables());
                            System.out.println("WHILE - Variables !");
                            break;
                        case "comment":
                            set = new HashSet(whileDecl.getComments());
                            System.out.println("WHILE - Comments !");
                            break;
                        case "parent":
                            set = new HashSet();
                            set.add(whileDecl.getParent());
                            System.out.println("WHILE - Parent !");
                            break;
                    }
                    break;
                case "IF":
                    IfDeclaration ifDecl = (IfDeclaration) scope;
                    switch (relationType) {
                        case "scope":
                            set = new HashSet(ifDecl.getScopes());
                            System.out.println("IF - Scopes !");
                            break;
                        case "variable":
                            set = new HashSet(ifDecl.getVariables());
                            System.out.println("IF - Variables !");
                            break;
                        case "comment":
                            set = new HashSet(ifDecl.getComments());
                            System.out.println("IF - Comments !");
                            break;
                        case "parent":
                            set = new HashSet();
                            set.add(ifDecl.getParent());
                            System.out.println("IF - Parent !");
                            break;
                    }
                    break;
                case "ELSE":
                    ElseIfDeclaration elseIfDecl = (ElseIfDeclaration) scope;
                    switch (relationType) {
                        case "scope":
                            set = new HashSet(elseIfDecl.getScopes());
                            System.out.println("ELSE - Scopes !");
                            break;
                        case "variable":
                            set = new HashSet(elseIfDecl.getVariables());
                            System.out.println("ELSE - Variables !");
                            break;
                        case "comment":
                            set = new HashSet(elseIfDecl.getComments());
                            System.out.println("ELSE - Comments !");
                            break;
                        case "parent":
                            set = new HashSet();
                            set.add(elseIfDecl.getParent());
                            System.out.println("ELSE - Parent !");
                            break;
                    }
                    break;
                case "NONE":
                    break;
                default:
                    switch (relationType) {
                        case "scope":
                            set = new HashSet(scope.getScopes());
                            System.out.println("default - Scopes !");
                            break;
                        case "variable":
                            set = new HashSet(scope.getVariables());
                            System.out.println("default - Variables !");
                            break;
                        case "comment":
                            set = new HashSet(scope.getComments());
                            System.out.println("default - Comments !");
                            break;
                        case "parent":
                            set = new HashSet();
                            set.add(scope.getParent());
                            System.out.println("default - Parent !");
                            break;
                    }
            }

        } else if (entity instanceof Variable) {
            Variable variable = (Variable) entity;
            switch (relationType) {
                case "parent":
                    set = new HashSet();
                    set.add(variable.getScope());
                    break;
            }
        } else if (entity instanceof Invocation) {
            Invocation invocation = (Invocation) entity;
            switch (relationType) {
                case "argument":
                    set = new HashSet(invocation.getArguments());
                    break;
                case "parent":
                    set = new HashSet();
                    set.add(invocation.getParent());
                    break;

            }
        }
//        else if (entity instanceof Argument) {
//            Argument argumentEntity = (Argument) entity;
//            switch (relationType) {
//                case "parent":
//                    set = new HashSet();
//                    set.add(argumentEntity.);
//                    break;
//            }
//
//        } else if (entity instanceof ConstantValue) {
//            ConstantValue constantValueEntity = (ConstantValue) entity;
//            switch (relationType) {
//                case "parent":
//                    set = new HashSet();
//                    set.add(constantValueEntity.getParent());
//                    break;
//            }
//        }
        else if (entity instanceof Comment) {
            Comment commentEntity = (Comment) entity;
            switch (relationType) {
                case "parent":
                    set = new HashSet();
                    set.add(commentEntity.getParent());
                    break;
            }
        }
        return set;
    }

    /**
     *
     * @param e1 main variableEntity
     * @param er1 variableEntity related to variableEntity e1
     * @param relationType the type of relation
     * @return how many times in the file he given type of relation appears
     * between two given entities
     */
    private static int getCount(CodeEntity e1, CodeEntity er1, String relationType) { // 
        return 1;
    }

    private static int pow = 0;

    /**
     *
     * @param e1 source node
     * @param e2 target node
     * @return normalized value of the similarity between code entities
     */
    public static double computeSimilarityMetric(CodeEntity e1, CodeEntity e2) {

        double similarity = 0.0;
        if (e1.getClass().equals(e2.getClass())) {

            List<String> relationTypes = new ArrayList();
            relationTypes.add("parent");
            if (e1 instanceof Invocation) {
                //    relationTypes.add("argument");
            } else if (e1 instanceof Scope) {
                //  relationTypes.add("scope");
                if (e1 instanceof CompilationUnitDeclaration) {
                    relationTypes.add("class");
                } else if (e1 instanceof ClassDeclaration) {
                    relationTypes.add("method");
                    //     relationTypes.add("implClass");
                } else if (e1 instanceof MethodDeclaration) {
                    //   relationTypes.add("parameter");
                    // relationTypes.add("modifier");
                }
                relationTypes.add("variable");
                //relationTypes.add("comment");
            }

            int N = relationTypes.size();
            double nameSimilarity;
            double metric = 0.0;

            String e1Name = getCodeEntityName(e1);
            String e2Name = getCodeEntityName(e2);

            if (e1 instanceof Scope || e1 instanceof Variable) {
                if (e1 instanceof CompilationUnitDeclaration) {
                    nameSimilarity = packageSimilarity(e1, e2);
                } else {
                    nameSimilarity = nameSimilarity(e1Name, e2Name);
                }
//                System.out.println("Name Similarity von " + e1Name + " und " + e2Name + " ist: " + nameSimilarity);
            } else {
                nameSimilarity = wordsSimilarity(e1Name, e2Name);
//                System.out.println("Words Similarity von " + e1Name + " und " + e2Name + " ist: " + nameSimilarity);
            }

            for (String type : relationTypes) {
                metric += structureSimilarity(e1, e2, type);
//                System.out.println("Metric " + metric);
//                System.out.println("--------------------------------------------");
            }
            similarity = (nameSimilarity + metric) / (nameSimilarity + N);
        }
        return similarity;
    }
}
