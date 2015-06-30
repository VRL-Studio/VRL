/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.system;

/**
 * Initialization failure. Plugin developers can use this interface to describte
 * initialization failures.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface InitFailure {

    public String getReason();

    public boolean failed();

    static InitFailure failure(String reason) {
        return new InitFailureImpl(reason, true);
    }

    static InitFailure success() {
        return InitFailureImpl.success;
    }
}

final class InitFailureImpl implements InitFailure {
    
    static final InitFailure success = new InitFailureImpl("", false);

    private final String reason;
    private final boolean failure;

    public InitFailureImpl(String reason, boolean failure) {
        this.reason = reason;
        this.failure = failure;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public boolean failed() {
        return failure;
    }
}
