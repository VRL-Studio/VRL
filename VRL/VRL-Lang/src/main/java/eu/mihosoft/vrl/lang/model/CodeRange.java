/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import java.util.Objects;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class CodeRange implements ICodeRange {

    private final ICodeLocation begin;
    private final ICodeLocation end;

    /**
     * Constructor.
     *
     * @param begin beginning index, inclusive
     * @param end ending index, exclusive
     */
    public CodeRange(ICodeLocation begin, ICodeLocation end) {
        this.begin = begin;
        this.end = end;
    }

    public CodeRange(int begin, int end) {
        this.begin = new CodeLocation(begin);
        this.end = new CodeLocation(end);
    }

    @Override
    public ICodeLocation getBegin() {
        return this.begin;
    }

    @Override
    public ICodeLocation getEnd() {
        return this.end;
    }

    @Override
    public int compareTo(ICodeRange o) {
        return this.getBegin().compareTo(o.getBegin());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CodeRange other = (CodeRange) obj;
        if (!Objects.equals(this.begin, other.begin)) {
            return false;
        }
        if (!Objects.equals(this.end, other.end)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.begin);
        hash = 29 * hash + Objects.hashCode(this.end);
        return hash;
    }

    @Override
    public boolean contains(ICodeRange o) {

        boolean weContainTheBeginningIndex = this.getBegin().compareTo(o.getBegin()) < 1;
        boolean weContainTheEndingIndex = this.getEnd().compareTo(o.getEnd()) > -1;

        return weContainTheBeginningIndex && weContainTheEndingIndex;
    }

    @Override
    public boolean contains(ICodeLocation o) {
        boolean weContainTheBeginningIndex = this.getBegin().compareTo(o) < 1;
        boolean weContainTheEndingIndex = this.getEnd().compareTo(o) > -1;

        return weContainTheBeginningIndex && weContainTheEndingIndex;
    }

    @Override
    public ICodeRange intersection(ICodeRange o) {
        return new CodeRange(Math.max(this.getBegin().getCharIndex(), o.getBegin().getCharIndex()),
                Math.min(this.getEnd().getCharIndex(), o.getEnd().getCharIndex()));
    }

    @Override
    public int size() {
        return getEnd().getCharIndex() - getBegin().getCharIndex();
    }

    @Override
    public boolean isEmpty() {
        return size() <= 0;
    }

}
