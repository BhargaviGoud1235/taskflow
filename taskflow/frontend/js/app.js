const API_BASE = "http://localhost:8080/api";

let state = {
  token: null,
  user: null,
  projects: [],
  currentProjectId: null,
  tasks: [],
};

// ---------- Auth screen tab switching ----------
document.querySelectorAll(".tab-btn").forEach((btn) => {
  btn.addEventListener("click", () => {
    document.querySelectorAll(".tab-btn").forEach((b) => b.classList.remove("active"));
    btn.classList.add("active");
    const tab = btn.dataset.tab;
    document.getElementById("loginForm").classList.toggle("hidden", tab !== "login");
    document.getElementById("registerForm").classList.toggle("hidden", tab !== "register");
  });
});

// ---------- API helper ----------
async function apiFetch(path, options = {}) {
  const headers = options.headers || {};
  headers["Content-Type"] = "application/json";
  if (state.token) headers["Authorization"] = "Bearer " + state.token;

  const res = await fetch(API_BASE + path, { ...options, headers });

  if (!res.ok) {
    let message = "Request failed";
    try {
      const errBody = await res.json();
      message = errBody.message || JSON.stringify(errBody);
    } catch (e) {
      // ignore parse errors
    }
    throw new Error(message);
  }

  if (res.status === 204) return null;
  return res.json();
}

// ---------- Login ----------
document.getElementById("loginForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const email = document.getElementById("loginEmail").value;
  const password = document.getElementById("loginPassword").value;
  const errorEl = document.getElementById("loginError");
  errorEl.textContent = "";

  try {
    const data = await apiFetch("/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    });
    onAuthSuccess(data);
  } catch (err) {
    errorEl.textContent = err.message;
  }
});

// ---------- Register ----------
document.getElementById("registerForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const name = document.getElementById("registerName").value;
  const email = document.getElementById("registerEmail").value;
  const password = document.getElementById("registerPassword").value;
  const errorEl = document.getElementById("registerError");
  errorEl.textContent = "";

  try {
    const data = await apiFetch("/auth/register", {
      method: "POST",
      body: JSON.stringify({ name, email, password }),
    });
    onAuthSuccess(data);
  } catch (err) {
    errorEl.textContent = err.message;
  }
});

function onAuthSuccess(data) {
  state.token = data.token;
  state.user = { name: data.name, email: data.email, role: data.role };
  document.getElementById("authScreen").classList.add("hidden");
  document.getElementById("app").classList.remove("hidden");
  document.getElementById("userNameLabel").textContent = data.name;
  loadProjects();
}

document.getElementById("logoutBtn").addEventListener("click", () => {
  state = { token: null, user: null, projects: [], currentProjectId: null, tasks: [] };
  document.getElementById("app").classList.add("hidden");
  document.getElementById("authScreen").classList.remove("hidden");
});

// ---------- Projects ----------
async function loadProjects() {
  try {
    state.projects = await apiFetch("/projects/mine");
    renderProjectList();
  } catch (err) {
    alert("Failed to load projects: " + err.message);
  }
}

function renderProjectList() {
  const list = document.getElementById("projectList");
  list.innerHTML = "";
  state.projects.forEach((p) => {
    const li = document.createElement("li");
    li.textContent = p.name;
    li.className = p.id === state.currentProjectId ? "active" : "";
    li.addEventListener("click", () => selectProject(p.id));
    list.appendChild(li);
  });
}

async function selectProject(projectId) {
  state.currentProjectId = projectId;
  renderProjectList();
  const project = state.projects.find((p) => p.id === projectId);

  document.getElementById("noProjectSelected").classList.add("hidden");
  document.getElementById("projectView").classList.remove("hidden");
  document.getElementById("projectTitle").textContent = project.name;
  document.getElementById("projectDescription").textContent = project.description || "";

  await loadTasks(projectId);
}

document.getElementById("newProjectBtn").addEventListener("click", () => {
  document.getElementById("projectModal").classList.remove("hidden");
});

document.getElementById("cancelProjectBtn").addEventListener("click", () => {
  document.getElementById("projectModal").classList.add("hidden");
});

document.getElementById("projectForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const name = document.getElementById("newProjectName").value;
  const description = document.getElementById("newProjectDesc").value;

  try {
    const project = await apiFetch("/projects", {
      method: "POST",
      body: JSON.stringify({ name, description }),
    });
    state.projects.push(project);
    document.getElementById("projectModal").classList.add("hidden");
    document.getElementById("projectForm").reset();
    renderProjectList();
    selectProject(project.id);
  } catch (err) {
    alert("Failed to create project: " + err.message);
  }
});

// ---------- Tasks ----------
async function loadTasks(projectId) {
  try {
    state.tasks = await apiFetch(`/tasks/project/${projectId}`);
    renderBoard();
  } catch (err) {
    alert("Failed to load tasks: " + err.message);
  }
}

function renderBoard() {
  ["TODO", "IN_PROGRESS", "DONE"].forEach((status) => {
    const col = document.getElementById("col-" + status);
    col.innerHTML = "";
    state.tasks
      .filter((t) => t.status === status)
      .forEach((task) => col.appendChild(renderTaskCard(task)));
  });
}

function renderTaskCard(task) {
  const card = document.createElement("div");
  card.className = `task-card priority-${task.priority}`;

  const title = document.createElement("h5");
  title.textContent = task.title;
  card.appendChild(title);

  if (task.description) {
    const desc = document.createElement("p");
    desc.textContent = task.description;
    card.appendChild(desc);
  }

  const meta = document.createElement("div");
  meta.className = "task-meta";
  meta.innerHTML = `<span>${task.priority}</span><span>${task.dueDate || "No due date"}</span>`;
  card.appendChild(meta);

  const actions = document.createElement("div");
  actions.className = "task-actions";

  const statuses = ["TODO", "IN_PROGRESS", "DONE"];
  const currentIndex = statuses.indexOf(task.status);

  if (currentIndex > 0) {
    const backBtn = document.createElement("button");
    backBtn.textContent = "\u2190 Move back";
    backBtn.addEventListener("click", () => updateTaskStatus(task.id, statuses[currentIndex - 1]));
    actions.appendChild(backBtn);
  }

  if (currentIndex < statuses.length - 1) {
    const nextBtn = document.createElement("button");
    nextBtn.textContent = "Move forward \u2192";
    nextBtn.addEventListener("click", () => updateTaskStatus(task.id, statuses[currentIndex + 1]));
    actions.appendChild(nextBtn);
  }

  const deleteBtn = document.createElement("button");
  deleteBtn.textContent = "Delete";
  deleteBtn.addEventListener("click", () => deleteTask(task.id));
  actions.appendChild(deleteBtn);

  card.appendChild(actions);
  return card;
}

async function updateTaskStatus(taskId, newStatus) {
  try {
    await apiFetch(`/tasks/${taskId}/status`, {
      method: "PATCH",
      body: JSON.stringify({ status: newStatus }),
    });
    await loadTasks(state.currentProjectId);
  } catch (err) {
    alert("Failed to update task: " + err.message);
  }
}

async function deleteTask(taskId) {
  if (!confirm("Delete this task?")) return;
  try {
    await apiFetch(`/tasks/${taskId}`, { method: "DELETE" });
    await loadTasks(state.currentProjectId);
  } catch (err) {
    alert("Failed to delete task: " + err.message);
  }
}

document.getElementById("newTaskBtn").addEventListener("click", () => {
  document.getElementById("taskModal").classList.remove("hidden");
});

document.getElementById("cancelTaskBtn").addEventListener("click", () => {
  document.getElementById("taskModal").classList.add("hidden");
});

document.getElementById("taskForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const title = document.getElementById("newTaskTitle").value;
  const description = document.getElementById("newTaskDesc").value;
  const priority = document.getElementById("newTaskPriority").value;
  const dueDate = document.getElementById("newTaskDueDate").value || null;

  try {
    await apiFetch("/tasks", {
      method: "POST",
      body: JSON.stringify({
        title,
        description,
        priority,
        dueDate,
        projectId: state.currentProjectId,
      }),
    });
    document.getElementById("taskModal").classList.add("hidden");
    document.getElementById("taskForm").reset();
    await loadTasks(state.currentProjectId);
  } catch (err) {
    alert("Failed to create task: " + err.message);
  }
});
