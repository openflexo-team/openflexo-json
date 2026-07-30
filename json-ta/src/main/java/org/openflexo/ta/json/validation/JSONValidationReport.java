package org.openflexo.ta.json.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Validation report for a JSON document checked against a JSON schema document. */
public class JSONValidationReport {

	private final List<JSONValidationIssue> issues = new ArrayList<>();

	public boolean isValid() {
		for (JSONValidationIssue issue : issues) {
			if (issue.getSeverity() == JSONValidationSeverity.ERROR) {
				return false;
			}
		}
		return true;
	}

	public List<JSONValidationIssue> getIssues() {
		return Collections.unmodifiableList(issues);
	}

	public void addIssue(JSONValidationIssue issue) {
		if (issue != null) {
			issues.add(issue);
		}
	}

	public void addError(String path, String keyword, String message) {
		addIssue(new JSONValidationIssue(path, message, JSONValidationSeverity.ERROR, keyword));
	}

	@Override
	public String toString() {
		return "JSONValidationReport(valid=" + isValid() + ", issues=" + issues + ")";
	}
}
