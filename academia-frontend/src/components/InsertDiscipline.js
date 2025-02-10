import React, { useState } from "react";

const InsertDiscipline = () => {
  const [disciplineData, setDisciplineData] = useState({
    disciplineCode: "",
    disciplineName: "",
    professorMail: "",
    yearDegree: "",
    disciplineType: "Mandatory",
    disciplineCategory: "Domain",
    examType: "Exam",
  });
  const [responseLinks, setResponseLinks] = useState(null);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setDisciplineData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccessMessage("");
    setLoading(true);

    const jwtToken = localStorage.getItem("jwt");
    if (!jwtToken) {
      setError("You must be logged in as an admin to insert a discipline.");
      setLoading(false);
      return;
    }

    try {
      const response = await fetch(
        "http://localhost:8080/academia/disciplines/insert",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwtToken}`,
          },
          body: JSON.stringify({
            ...disciplineData,
            yearDegree: parseInt(disciplineData.yearDegree, 10),
          }),
        }
      );

      if (response.ok) {
        const data = await response.json();
        setSuccessMessage(data.message);
        setResponseLinks(data._links);

        const secondResponse = await fetch(
          "http://localhost:8001/materials/disciplines/insert",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${jwtToken}`,
            },
            body: JSON.stringify({
              code: disciplineData.disciplineCode,
              titular: disciplineData.professorMail,
            }),
          }
        );

        if (secondResponse.ok) {
          const secondData = await secondResponse.json();
          setSuccessMessage(
            `${data.message}. ${secondData.message} Discipline successfully inserted in materials!`
          );
        } else {
          const errorData = await secondResponse.json();
          setError(
            `First call succeeded, but second call failed: ${
              errorData.detail || "Unknown error."
            }`
          );
        }
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to insert discipline.");
      }
    } catch (err) {
      setError("An error occurred while trying to insert the discipline.");
    } finally {
      setLoading(false);
    }
  };

  const handleLinkAction = async (link, method = "GET") => {
    try {
      const jwtToken = localStorage.getItem("jwt");
      if (!jwtToken) {
        setError("Authorization token missing.");
        return;
      }

      const response = await fetch(link, {
        method: method,
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      });

      if (!response.ok) {
        setError(`Failed to perform action. HTTP status: ${response.status}`);
        return;
      }

      const contentType = response.headers.get("Content-Type");
      if (contentType && contentType.includes("application/json")) {
        const data = await response.json();
        console.log("Response Data:", data);
        alert(JSON.stringify(data, null, 2)); 
      } else if (contentType && contentType.includes("text")) {
        const text = await response.text();
        alert(text);
      } else {
      
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        window.open(url, "_blank");
      }
    } catch (err) {
      setError("An error occurred while performing the action.");
    }
  };

  return (
    <div>
      <h2>Insert Discipline</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {successMessage && <p style={{ color: "green" }}>{successMessage}</p>}
      {loading && <p>Loading...</p>}

      <form onSubmit={handleSubmit}>
        <div>
          <label>Discipline Code:</label>
          <input
            type="text"
            name="disciplineCode"
            value={disciplineData.disciplineCode}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label>Discipline Name:</label>
          <input
            type="text"
            name="disciplineName"
            value={disciplineData.disciplineName}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label>Professor Email:</label>
          <input
            type="email"
            name="professorMail"
            value={disciplineData.professorMail}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label>Year Degree:</label>
          <input
            type="number"
            name="yearDegree"
            value={disciplineData.yearDegree}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label>Discipline Type:</label>
          <select
            name="disciplineType"
            value={disciplineData.disciplineType}
            onChange={handleInputChange}
          >
            <option value="Mandatory">Mandatory</option>
            <option value="Optional">Optional</option>
            <option value="FreeChoice">FreeChoice</option>
          </select>
        </div>
        <div>
          <label>Discipline Category:</label>
          <select
            name="disciplineCategory"
            value={disciplineData.disciplineCategory}
            onChange={handleInputChange}
          >
            <option value="Domain">Domain</option>
            <option value="Speciality">Speciality</option>
            <option value="Adjacency">Adjacency</option>
          </select>
        </div>
        <div>
          <label>Exam Type:</label>
          <select
            name="examType"
            value={disciplineData.examType}
            onChange={handleInputChange}
          >
            <option value="Exam">Exam</option>
            <option value="Colocviu">Colocviu</option>
          </select>
        </div>
        <button type="submit">Insert Discipline</button>
      </form>

      {responseLinks && (
        <div style={{ marginTop: "20px" }}>
          <h3>Links</h3>
          <button onClick={() => handleLinkAction(responseLinks.self.href)}>
            View Discipline
          </button>
          <button
            onClick={() =>
              handleLinkAction(responseLinks.linkToProfessorTitular.href)
            }
          >
            View Professor
          </button>
          <button
            onClick={() =>
              handleLinkAction(responseLinks.linkToDeleteDiscipline.href, responseLinks.linkToDeleteDiscipline.method)
            }
          >
            Delete Discipline
          </button>
          <button
            onClick={() =>
              handleLinkAction(
                responseLinks.linkToRemoveDisciplineFromStudents.href,
                responseLinks.linkToRemoveDisciplineFromStudents.method
              )
            }
          >
            Remove Discipline from Students
          </button>
        </div>
      )}
    </div>
  );
};

export default InsertDiscipline;
