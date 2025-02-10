import React, { useState } from "react";

const InsertStudent = () => {
  const [studentData, setStudentData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    groupName: "",
    yearDegree: "",
    studyCycle: "",
  });
  const [responseLinks, setResponseLinks] = useState(null); // Stocăm linkurile HATEOAS
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");

  const handleViewStudent = async () => {
    try {
      const jwtToken = localStorage.getItem("jwt");
      const response = await fetch(responseLinks.linkToStudent.href, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      });
  
      if (response.ok) {
        const data = await response.json();
        console.log("Student Data:", data); // Sau afișează-l pe interfață
        alert(`${data.message}`)
        alert(`Student ID: ${data.student.studentId}\n Student Name: ${data.student.firstName} ${data.student.lastName}\n Group: ${data.student.groupName}\n Year degree: ${data.student.yearDegree}`);
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to fetch student data.");
      }
    } catch (err) {
      setError("An error occurred while fetching student data.");
    }
  };
  
  const handleViewStudentDisciplines = async () => {
    try {
      const jwtToken = localStorage.getItem("jwt");
      const response = await fetch(responseLinks.linkToStudentDisciplines.href, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      });
  
      if (response.ok) {
        const data = await response.json();
        console.log("Disciplines Data:", data); // Sau afișează-le pe interfață
        alert(`Disciplines: ${data.disciplines.map((d) => d.name).join(", ")}`);
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to fetch student disciplines.");
      }
    } catch (err) {
      setError("An error occurred while fetching student disciplines.");
    }
  };
  
  const handleDeleteStudent = async () => {
    try {
      const jwtToken = localStorage.getItem("jwt");
      const response = await fetch(responseLinks.linkToDeleteStudent.href, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      });
  
      if (response.ok) {
        alert("Student deleted successfully.");
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to delete student.");
      }
    } catch (err) {
      setError("An error occurred while deleting the student.");
    }
  };
  

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setStudentData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccessMessage("");

    const jwtToken = localStorage.getItem("jwt");

    try {
      const response = await fetch("http://localhost:8080/academia/students/insert", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`,
        },
        body: JSON.stringify(studentData),
      });

      if (response.status === 201) {
        const data = await response.json();
        setResponseLinks(data._links);
        setSuccessMessage(data.message);
        setStudentData({
          firstName: "",
          lastName: "",
          email: "",
          groupName: "",
          yearDegree: "",
          studyCycle: "",
        });
      } else if (response.status === 409) {
        const data = await response.json();
        setError(data.message); // "Email already exists!"
      } else {
        const data = await response.json();
        setError(data.message || "Failed to insert student.");
      }
    } catch (err) {
      setError("An error occurred while trying to insert the student.");
    }
  };

  return (
    <div>
      <h2>Insert Student</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {successMessage && <p style={{ color: "green" }}>{successMessage}</p>}

      {/* Formular pentru introducerea studentului */}
      <form onSubmit={handleSubmit}>
        <div>
          <label>First Name:</label>
          <input
            type="text"
            name="firstName"
            value={studentData.firstName}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label>Last Name:</label>
          <input
            type="text"
            name="lastName"
            value={studentData.lastName}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label>Email:</label>
          <input
            type="email"
            name="email"
            value={studentData.email}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label>Group Name:</label>
          <input
            type="text"
            name="groupName"
            value={studentData.groupName}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label>Year Degree:</label>
          <input
            type="number"
            name="yearDegree"
            value={studentData.yearDegree}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label>Study Cycle:</label>
          <input
            type="text"
            name="studyCycle"
            value={studentData.studyCycle}
            onChange={handleInputChange}
            required
          />
        </div>
        <button type="submit">Insert Student</button>
      </form>

      {/* Afișează linkurile HATEOAS */}
      {responseLinks && (
        <div style={{ marginTop: "20px" }}>
            <h3>Links</h3>
            <button onClick={handleViewStudent}>View Student</button>
            <button onClick={handleViewStudentDisciplines}>View Student Disciplines</button>
            {responseLinks.linkToDeleteStudent && (
            <button onClick={handleDeleteStudent}>Delete Student</button>
            )}
        </div>
)}

    </div>
  );
};

export default InsertStudent;
