package eu.mihosoft.transverse;

public interface ValidationContext {
	void info(String message);

	void warn(String message);

	void err(String message);

	void fail(String message);
}