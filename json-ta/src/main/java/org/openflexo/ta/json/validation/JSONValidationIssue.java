package org.openflexo.ta.json.validation;

/** One diagnostic produced while validating a JSON document against a JSON schema document. */
public class JSONValidationIssue {

	private final String path;
	private final String message;
	private final JSONValidationSeverity severity;
	private final String keyword;

	public JSONValidationIssue(String path, String message, JSONValidationSeverity severity, String keyword) {
		this.path = path;
		this.message = message;
		this.severity = severity;
		this.keyword = keyword;
	}

	public String getPath() {
		return path;
	}

	public String getMessage() {
		return message;
	}

	public JSONValidationSeverity getSeverity() {
		return severity;
	}

	public String getKeyword() {
		return keyword;
	}

	@Override
	public String toString() {
		return severity + " " + path + " [" + keyword + "] " + message;
	}
}
