import React, { useState } from "react";

const PatchDisciplines = ({ studentId }) => {
  const [disciplineCodes, setDisciplineCodes] = useState(""); // Lista de coduri de discipline
  const [responseLinks, setResponseLinks] = useState(null); // Linkurile returnate
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");

  const handleInputChange = (e) => {
    setDisciplineCodes(e.target.value); // Actualizează lista de coduri
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccessMessage("");

    const jwtToken = localStorage.getItem("jwt");
    if (!jwtToken) {
      setError("Authorization token missing.");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/academia/student/add", {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`,
        },
        body: JSON.stringify({
          studentId,
          disciplineCodes: disciplineCodes.split(",").map((code) => code.trim()), // Transmite codurile de discipline
        }),
      });

      if (response.ok) {
        const data = await response.json();
        setSuccessMessage(data.message);
        setResponseLinks(data._links);
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to patch disciplines.");
      }
    } catch (err) {
      setError("An error occurred while patching disciplines.");
    }
  };

  const handleLinkClick = async (url) => {
    const jwtToken = localStorage.getItem("jwt");
    if (!jwtToken) {
      setError("Authorization token missing.");
      return;
    }

    try {
      const response = await fetch(url, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        alert(JSON.stringify(data, null, 2)); // Afișează datele într-un alert
      } else {
        setError("Failed to fetch link data.");
      }
    } catch (err) {
      setError("An error occurred while fetching link data.");
    }
  };

  return (
    <div>
      <h4>Patch Disciplines for Student ID: {studentId}</h4>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {successMessage && <p style={{ color: "green" }}>{successMessage}</p>}

      <form onSubmit={handleSubmit}>
        <label>Discipline Codes (comma-separated):</label>
        <input
          type="text"
          value={disciplineCodes}
          onChange={handleInputChange}
          placeholder="e.g., CODE1, CODE2, CODE3"
          required
        />
        <button type="submit">Add Disciplines</button>
      </form>

      {responseLinks && (
        <div style={{ marginTop: "20px" }}>
          <h5>Response Links</h5>
          {responseLinks.linkToStudent && (
            <button onClick={() => handleLinkClick(responseLinks.linkToStudent.href)}>
              View Student
            </button>
          )}
          {responseLinks.linkToStudentDisciplines && (
            <button onClick={() => handleLinkClick(responseLinks.linkToStudentDisciplines.href)}>
              View Student Disciplines
            </button>
          )}
        </div>
      )}
    </div>
  );
};

export default PatchDisciplines;
