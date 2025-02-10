import React, { useEffect, useState } from "react";
import {deleteDisciplineByLink, fetchDisciplinesForStudentByLink, fetchProfessorTitularByLink } from "../services/api";

const DisciplinesList = ({ linkToStudentDisciplines }) => {
  const [disciplines, setDisciplines] = useState([]);
  const [links, setLinks] = useState(null)
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDisciplines = async () => {
      setLoading(true);
      try {
        const data = await fetchDisciplinesForStudentByLink(linkToStudentDisciplines);
        console.log(data);
        setDisciplines(data.disciplines || []);
        setLinks(data._links)
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    if (linkToStudentDisciplines) {
      console.log(linkToStudentDisciplines);
      fetchDisciplines();
    }
  }, [linkToStudentDisciplines]);

  const handlePage = async (pageLink) => {
    if (pageLink){
      const data = await fetchDisciplinesForStudentByLink(pageLink.href)
      console.log("From page handling: ", data)
      setDisciplines(data.disciplines || [])
      setLinks(data._links)
    }
  }

  const handleViewDiscipline = async (disciplineLink) => {
    try {
      const jwt = localStorage.getItem("jwt");
      if (!jwt) {
        console.error("JWT not found");
        return;
      }
  
      const response = await fetch(disciplineLink, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });
  
      if (!response.ok) {
        throw new Error(`Failed to fetch discipline details: ${response.statusText}`);
      }
  
      const data = await response.json();
      console.log("Discipline details:", data);
  
      // Deschide un tab nou și afișează detaliile disciplinei
      const newWindow = window.open();
      if (newWindow) {
        newWindow.document.write("<pre>" + JSON.stringify(data, null, 2) + "</pre>");
        newWindow.document.title = "Discipline Details";
      } else {
        alert("Failed to open new tab. Please allow popups in your browser.");
      }
    } catch (error) {
      console.error("Error fetching discipline details:", error);
      alert(`Error fetching discipline details: ${error.message}`);
    }
  };

  const handleViewProfessorTitular = async(professorLink) =>{
      const data = await fetchProfessorTitularByLink(professorLink)
      console.log("Profesor link: ", professorLink)
      const newWindow = window.open();
      if (newWindow) {
        newWindow.document.write("<pre>" + JSON.stringify(data, null, 2) + "</pre>");
        newWindow.document.title = "Discipline Details";
      } else {
        alert("Failed to open new tab. Please allow popups in your browser.");
      }
  }

  const handleDeleteDiscipline = async(disciplineLink, method) =>{
    if (!window.confirm("Are you sure you want to delete this discipline?")) return;
        try {
          const response = await deleteDisciplineByLink(disciplineLink, method);
          alert(`Discipline deleted: ${response.message}`);
        } catch (err) {
          alert(`Error deleting discipline: ${err.message}`);
        }  
  } 
  

  if (loading) {
    return <p>Loading disciplines...</p>;
  }

  if (error) {
    return <p style={{ color: "red" }}>Error: {error}</p>;
  }

  return (
    <div style={{ marginTop: "20px", padding: "10px", border: "1px solid gray" }}>
      <h4>Disciplines</h4>
      {disciplines.length === 0 ? (
        <p>No disciplines found for this student.</p>
      ) : (
        <div>
        <ul>
          {disciplines.map((disciplineItem) => {
            const { discipline, _links } = disciplineItem;
            return (
              <li key={discipline.disciplineCode}>
                <strong>{discipline.disciplineName}</strong>{" "}
                <button onClick={() => handleViewProfessorTitular(_links.linkToProfessorTitular.href)}>
                  View Professor Details
                </button>

                <button onClick={() => handleViewDiscipline(_links.self.href)}>
                  View Discipline Details
                </button>

                <button onClick={() => handleDeleteDiscipline(_links.linkToDeleteDiscipline.href, _links.linkToDeleteDiscipline.method)}>
                  Delete discipline
                </button>
              </li>
            );
          })}
        </ul>
        <button onClick={() => handlePage(links.previousPage)}> Previous page </button>
        <button onClick={() => handlePage(links.nextPage)}> Next page </button>
        </div>
        
      )}
    </div>
  );
};

export default DisciplinesList;
