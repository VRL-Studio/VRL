package eu.mihosoft.vrl.lang.model;

import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;

public class CodeEntityImpl implements CodeEntity {

	protected String id;
	protected Scope parent;
	private ICodeRange range;
	protected VFlow flow;
	private ObservableCodeImpl observableCode;
	private boolean textRenderingEnabled = true;

	public CodeEntityImpl() {
		super();
	}

	@Override
	public Scope getParent() {
	    return parent;
	}

	/**
	 * @return the id
	 */
	@Override
	public String getId() {
	    return id;
	}

	/**
	 * @param id the id to set
	 */
	@Override
	public void setId(String id) {
	    this.id = id;
	}

	/**
	 * @return the location
	 */
	@Override
	public ICodeRange getRange() {
	    return range;
	}

	/**
	 * @param location the location to set
	 */
	@Override
	public void setRange(ICodeRange location) {
	    this.range = location;
	}

	@Override
	public VNode getNode() {
	    return this.flow.getModel();
	}

	private ObservableCodeImpl getObservable() {
	    if (observableCode == null) {
	        observableCode = new ObservableCodeImpl();
	    }
	
	    return observableCode;
	}

	@Override
	public void addEventHandler(ICodeEventType type, CodeEventHandler eventHandler) {
	    getObservable().addEventHandler(type, eventHandler);
	}

	@Override
	public void removeEventHandler(ICodeEventType type, CodeEventHandler eventHandler) {
	    getObservable().removeEventHandler(type, eventHandler);
	}

	@Override
	public void fireEvent(CodeEvent evt) {
	    getObservable().fireEvent(evt);
	
	    if (!evt.isCaptured() && getParent() != null) {
	        getParent().fireEvent(evt);
	    }
	}

	/**
	 * @return the textRenderingEnabled
	 */
	public boolean isTextRenderingEnabled() {
	    return textRenderingEnabled;
	}

}