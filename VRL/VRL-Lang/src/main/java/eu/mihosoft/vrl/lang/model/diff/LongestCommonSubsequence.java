/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.vrl.lang.model.CodeEntity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class LongestCommonSubsequence {

    public static List<CodeEntity> lcs(List<CodeEntity> a, List<CodeEntity> b) {
        List<CodeEntity> x;
        List<CodeEntity> y;

        int alen = a.size();
        int blen = b.size();
        if (alen == 0 || blen == 0) {
            return new ArrayList<>();
        } else if (compareEntities(a.get(alen - 1), b.get(blen - 1))) {
            List<CodeEntity> list = lcs(a.subList(0, alen - 1), b.subList(0, blen - 1));
            list.add(a.get(alen - 1));
            return list;
        } else {
            x = lcs(a, b.subList(0, blen - 1));
            y = lcs(a.subList(0, alen - 1), b);
        }
        return (x.size() > y.size()) ? x : y;
    }

    static boolean compareEntities(CodeEntity codeEntity1, CodeEntity codeEntity2) {
        return SimilarityMetric.getCodeEntityName(codeEntity1).equals(SimilarityMetric.getCodeEntityName(codeEntity2));

    }

}
