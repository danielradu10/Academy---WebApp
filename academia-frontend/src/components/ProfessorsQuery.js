import React, { useState } from "react";
import {
  fetchProfessors,
  fetchProfessorsByLink,
  fetchDisciplinesForProfessor,
  deleteProfessorById,
  deleteDiscipline,
  removeDisciplineFromAllStudents,
  fetchDisciplinesForProfessorByLink,
} from "../services/api";

const ProfessorsQuery = () => {
  const [formData, setFormData] = useState({
    acadRank: "",
    lastName: "",
    firstName: "",
    page: "",
    itemsPerPage: "",
  });
  const [professors, setProfessors] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [links, setLinks] = useState(null);
  const [selectedDisciplines, setSelectedDisciplines] = useState(null);
  const [disciplinesNextPageLink, setDisciplinesNextPageLink] = useState(null); // Link pentru pagina următoare a disciplinelor


  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const queryParams = {};
      Object.keys(formData).forEach((key) => {
        if (formData[key]) {
          queryParams[key] = formData[key];
        }
      });
      const data = await fetchProfessors(queryParams);
      console.log(data);
      setProfessors(data.professors.content || []);
      setLinks(data._links || null)
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleNextPage = async (pageLink) => {
    if (!pageLink) return;

    setLoading(true);
    setError(null);
    try {
      const data = await fetchProfessorsByLink(pageLink);
      setProfessors(data.professors.content || []);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteProfessor = async (professorId) => {
    if (!window.confirm("Are you sure you want to delete this professor?")) return;

    try {
      await deleteProfessorById(professorId);
      alert("Professor deleted successfully!");
      setProfessors((prev) => prev.filter((prof) => prof.professorId !== professorId));
    } catch (err) {
      alert(`Error deleting professor: ${err.message}`);
    }
  };

  const handleViewDisciplines = async (professorId) => {
    try {
      const data = await fetchDisciplinesForProfessor(professorId);
      setSelectedDisciplines({
        professorId,
        disciplines: data.disciplines,
      });
      setDisciplinesNextPageLink(data._links?.nextPage?.href || null);

    } catch (err) {
      alert("Failed to fetch disciplines.");
    }
  };

  const handleNextDisciplinesPage = async () => {
    if (!disciplinesNextPageLink) return;

    try {
      const data = await fetchDisciplinesForProfessorByLink(disciplinesNextPageLink);
      setSelectedDisciplines((prev) => ({
        ...prev,
        disciplines: [...prev.disciplines, ...data.disciplines], // Adăugăm noile discipline la lista existentă
      }));
      setDisciplinesNextPageLink(data._links?.nextPage?.href || null); // Actualizăm link-ul pentru pagina următoare
    } catch (err) {
      alert("Failed to fetch next page of disciplines.");
    }
  };


  const handleDeleteDiscipline = async (disciplineCode) => {
    if (!window.confirm("Are you sure you want to delete this discipline?")) return;

    try {
      const response = await deleteDiscipline(disciplineCode);
      alert(`Discipline deleted: ${response.message}`);
      setSelectedDisciplines((prev) => ({
        ...prev,
        disciplines: prev.disciplines.filter(
          (discipline) => discipline.discipline.disciplineCode !== disciplineCode
        ),
      }));
    } catch (err) {
      alert(`Error deleting discipline: ${err.message}`);
    }
  };

  const handleRemoveFromAllStudents = async (disciplineCode) => {
    try {
      const response = await removeDisciplineFromAllStudents(disciplineCode);
      alert(`Removed discipline from all students: ${response.message}`);
    } catch (err) {
      alert(`Error removing discipline from all students: ${err.message}`);
    }
  };

  return (
    <div>
      <h2>Search Professors</h2>
      <form onSubmit={handleSubmit} style={{ maxWidth: "600px", margin: "0 auto" }}>
        <div>
          <label>Academic Rank:</label>
          <select name="acadRank" value={formData.acadRank} onChange={handleChange}>
            <option value="">-- Select Degree --</option>
            <option value="Assistant">Assistant</option>
            <option value="Main">Main</option>
            <option value="Conf">Conf</option>
            <option value="Professor">Professor</option>
          </select>
        </div>
        <div>
          <label>Last Name:</label>
          <input
            type="text"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
          />
        </div>
        <div>
          <label>First Name:</label>
          <input
            type="text"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
          />
        </div>
        <div>
          <label>Page:</label>
          <input
            type="number"
            name="page"
            value={formData.page}
            onChange={handleChange}
          />
        </div>
        <div>
          <label>Items Per Page:</label>
          <input
            type="number"
            name="itemsPerPage"
            value={formData.itemsPerPage}
            onChange={handleChange}
          />
        </div>
        <button type="submit">Search</button>
      </form>

      {loading && <p>Loading professors...</p>}
      {error && <p style={{ color: "red" }}>Error: {error}</p>}

      <div>
        <h3>Results</h3>
        {professors.length === 0 ? (
          <p>No professors found.</p>
        ) : (
          <ul>
            {professors.map((professor) => (
              <li key={professor.professorId}>
                <strong>{professor.firstName} {professor.lastName}</strong> - {professor.emailProfessor}
                <p><strong>Degree:</strong> {professor.degree}</p>
                <p><strong>Association Type:</strong> {professor.associationType}</p>
                <button onClick={() => handleViewDisciplines(professor.professorId)}>
                  View Disciplines
                </button>
                <button onClick={() => handleDeleteProfessor(professor.professorId)}>
                  Delete Professor
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>

      
        <button onClick={() => handleNextPage(links.linkToPreviousPage.href)} style={{ marginTop: "20px" }}>
          Previous page
        </button>
        <button onClick={() => handleNextPage(links.linkToNextPage.href)} style={{ marginTop: "20px" }}>
          Next Page
        </button>
      

      {selectedDisciplines && (
        <div style={{ marginTop: "20px", border: "1px solid black", padding: "10px" }}>
          <h3>Disciplines for Professor ID: {selectedDisciplines.professorId}</h3>
          <ul>
            {selectedDisciplines.disciplines.map((discipline) => (
              <li key={discipline.discipline.disciplineCode}>
                {discipline.discipline.disciplineName}
                <button onClick={() => handleRemoveFromAllStudents(discipline.discipline.disciplineCode)}>
                  Remove from All Students
                </button>
                <button onClick={() => handleDeleteDiscipline(discipline.discipline.disciplineCode)}>
                  Delete Discipline
                </button>
              </li>
            ))}
          </ul>
          {disciplinesNextPageLink && (
            <button onClick={handleNextDisciplinesPage} style={{ marginTop: "10px" }}>
              Next Disciplines Page
            </button>
          )}
        </div>
      )}
    </div>
  );
};

export default ProfessorsQuery;
