const form = document.querySelector("#registration-form");
const submitButton = document.querySelector("#submit-button");
const statusMessage = document.querySelector("#form-status");
const passwordInput = document.querySelector("#password");
const passwordToggle = document.querySelector("#password-toggle");

passwordToggle.addEventListener("click", () => {
  const showingPassword = passwordInput.type === "password";
  passwordInput.type = showingPassword ? "text" : "password";
  passwordToggle.textContent = showingPassword ? "Hide" : "Show";
  passwordToggle.setAttribute("aria-label", showingPassword ? "Hide password" : "Show password");
  passwordToggle.setAttribute("aria-pressed", String(showingPassword));
});

function setFieldError(fieldName, message) {
  const field = form.elements.namedItem(fieldName);
  const error = document.querySelector(`#${fieldName}-error`);
  field.setAttribute("aria-invalid", message ? "true" : "false");
  error.textContent = message;
}

function clearFieldErrors() {
  for (const fieldName of ["name", "email", "password"]) {
    setFieldError(fieldName, "");
  }
}

function getErrorMessage(response, body) {
  if (typeof body === "string" && body.trim()) return body;
  if (body && typeof body === "object") {
    return body.detail || body.message || body.title || "Please check the submitted details and try again.";
  }
  return `Registration failed (HTTP ${response.status}). Please try again.`;
}

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  clearFieldErrors();
  statusMessage.textContent = "";
  statusMessage.classList.remove("is-error");

  if (!form.reportValidity()) return;

  const formData = new FormData(form);
  const employee = {
    name: formData.get("name").trim(),
    email: formData.get("email").trim(),
    role: "Faculty",
    password: formData.get("password")
  };

  submitButton.disabled = true;
  submitButton.querySelector(".submit-label").textContent = "Creating account...";

  try {
    const response = await fetch("/api/employees", {
      method: "POST",
      headers: { "Content-Type": "application/json", "Accept": "application/json" },
      body: JSON.stringify(employee)
    });
    const responseText = await response.text();
    let responseBody = responseText;
    try {
      responseBody = responseText ? JSON.parse(responseText) : null;
    } catch {
      responseBody = responseText;
    }

    if (!response.ok) {
      throw new Error(getErrorMessage(response, responseBody));
    }

    const savedEmployee = responseBody || {};
    statusMessage.textContent = savedEmployee.id
      ? `Registration complete. Your employee ID is ${savedEmployee.id}.`
      : "Registration complete.";
    form.reset();
    passwordInput.type = "password";
    passwordToggle.textContent = "Show";
    passwordToggle.setAttribute("aria-label", "Show password");
    passwordToggle.setAttribute("aria-pressed", "false");
  } catch (error) {
    statusMessage.textContent = error instanceof TypeError
      ? "Could not reach the server. Check that the application is running and try again."
      : error.message;
    statusMessage.classList.add("is-error");
  } finally {
    submitButton.disabled = false;
    submitButton.querySelector(".submit-label").textContent = "Create account";
  }
});