import React, { useState } from "react";
import { insertProfessor } from "../services/api"; 

const InsertProfessorForm = () => {
  const [formData, setFormData] = useState({
    lastName: "",
    firstName: "",
    emailProfessor: "",
    degree: "Assistant", 
    associationType: "Main", 
    affiliation: "",
  });
  const [message, setMessage] = useState(""); 

  const degreeOptions = ["Assistant", "Main", "Conf", "Professor"];
  const associationOptions = ["Main", "Associate", "Extern"];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await insertProfessor(formData); 
      setMessage(`Success: ${response.message}`);
    } catch (error) {
      setMessage(`Error: ${error.message}`);
    }
  };

  return (
    <div>
      <h2>Insert Professor</h2>
      <form onSubmit={handleSubmit} style={{ maxWidth: "400px", margin: "0 auto", textAlign: "left" }}>
        <div>
          <label>Last Name:</label>
          <input
            type="text"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label>First Name:</label>
          <input
            type="text"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label>Email:</label>
          <input
            type="email"
            name="emailProfessor"
            value={formData.emailProfessor}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label>Degree:</label>
          <select
            name="degree"
            value={formData.degree}
            onChange={handleChange}
            required
          >
            {degreeOptions.map((option) => (
              <option key={option} value={option}>
                {option}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label>Association Type:</label>
          <select
            name="associationType"
            value={formData.associationType}
            onChange={handleChange}
            required
          >
            {associationOptions.map((option) => (
              <option key={option} value={option}>
                {option}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label>Affiliation:</label>
          <input
            type="text"
            name="affiliation"
            value={formData.affiliation}
            onChange={handleChange}
            required
          />
        </div>
        <button type="submit">Submit</button>
      </form>
      {message && <p style={{ color: message.startsWith("Error") ? "red" : "green" }}>{message}</p>}
    </div>
  );
};

export default InsertProfessorForm;
