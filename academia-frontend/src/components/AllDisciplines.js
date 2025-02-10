import React, { useState } from "react";

const GetDisciplines = () => {
  const [filters, setFilters] = useState({
    page: 0,
    itemsPerPage: 5,
    type: "",
    category: "",
  });
  const [disciplinesResponse, setDisciplinesResponse] = useState(null); // Răspunsul complet de la API
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFilters((prevFilters) => ({
      ...prevFilters,
      [name]: value,
    }));
  };

  const fetchDisciplines = async (url = "http://localhost:8080/academia/disciplines") => {
    setError(null);
    setLoading(true);

    const jwtToken = localStorage.getItem("jwt");
    if (!jwtToken) {
      setError("Authorization token missing.");
      setLoading(false);
      return;
    }

    try {
      const queryParams = new URLSearchParams({
        page: filters.page,
        itemsPerPage: filters.itemsPerPage,
        type: filters.type,
        category: filters.category,
      }).toString();

      const response = await fetch(`${url}?${queryParams}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        console.log(data);
        setDisciplinesResponse(data);
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to fetch disciplines.");
      }
    } catch (err) {
      setError("An error occurred while fetching disciplines.");
    } finally {
      setLoading(false);
    }
  };

  const fetchDisciplinesByLink = async (link) => {
    setError(null);
    setLoading(true);

    const jwtToken = localStorage.getItem("jwt");
    if (!jwtToken) {
      setError("Authorization token missing.");
      setLoading(false);
      return;
    }

    try {
      const response = await fetch(link, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        console.log(data);
        setDisciplinesResponse(data);
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Failed to fetch disciplines.");
      }
    } catch (err) {
      setError("An error occurred while fetching disciplines.");
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (url) => {
    if (url) {
        fetchDisciplinesByLink(url);
    }
  };

  const handleLinkAction = async (url, method = "GET") => {
    const jwtToken = localStorage.getItem("jwt");
    if (!jwtToken) {
      setError("Authorization token missing.");
      return;
    }

    try {
      const response = await fetch(url, {
        method,
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        alert(JSON.stringify(data, null, 2)); // Afișează datele în alert
      } else {
        setError("Failed to perform action.");
      }
    } catch (err) {
      setError("An error occurred while performing the action.");
    }
    fetchDisciplines();
  };

  return (
    <div>
      <h2>Get Disciplines</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {loading && <p>Loading...</p>}

      {/* Formular pentru filtre */}
      <form
        onSubmit={(e) => {
          e.preventDefault();
          fetchDisciplines();
        }}
      >
        <div>
          <label>Page:</label>
          <input
            type="number"
            name="page"
            value={filters.page}
            onChange={handleInputChange}
          />
        </div>
        <div>
          <label>Items per Page:</label>
          <input
            type="number"
            name="itemsPerPage"
            value={filters.itemsPerPage}
            onChange={handleInputChange}
          />
        </div>
        <div>
          <label>Type:</label>
          <select name="type" value={filters.type} onChange={handleInputChange}>
            <option value="">All</option>
            <option value="Mandatory">Mandatory</option>
            <option value="Optional">Optional</option>
            <option value="FreeChoice">FreeChoice</option>
          </select>
        </div>
        <div>
          <label>Category:</label>
          <select
            name="category"
            value={filters.category}
            onChange={handleInputChange}
          >
            <option value="">All</option>
            <option value="Domain">Domain</option>
            <option value="Speciality">Speciality</option>
            <option value="Adjacency">Adjacency</option>
          </select>
        </div>
        <button type="submit">Search</button>
      </form>

      {/* Lista disciplinelor */}
      {disciplinesResponse && (
        <div>
          <h3>Disciplines</h3>
          <ul>
            {disciplinesResponse.disciplines.map((disciplineResponse, index) => (
              <li key={index}>
                <p>
                  <strong>Name:</strong> {disciplineResponse.discipline.disciplineName}
                </p>
                <p>
                  <strong>Code:</strong> {disciplineResponse.discipline.disciplineCode}
                </p>
                <p>
                  <strong>Category:</strong> {disciplineResponse.discipline.disciplineCategory}
                </p>
                <p>
                  <strong>Type:</strong> {disciplineResponse.discipline.disciplineType}
                </p>
                <p>
                  <strong>Professor:</strong>{" "}
                  {disciplineResponse.discipline.professor.firstName}{" "}
                  {disciplineResponse.discipline.professor.lastName}
                </p>
                {/* Butoane pentru linkurile HATEOAS */}
                <div>
                  {disciplineResponse._links.self && (
                    <button
                      onClick={() =>
                        handleLinkAction(disciplineResponse._links.self.href)
                      }
                    >
                      View Discipline
                    </button>
                  )}
                  {disciplineResponse._links.linkToProfessorTitular && (
                    <button
                      onClick={() =>
                        handleLinkAction(
                          disciplineResponse._links.linkToProfessorTitular.href
                        )
                      }
                    >
                      View Professor
                    </button>
                  )}
                  {disciplineResponse._links.linkToDeleteDiscipline && (
                    <button
                      onClick={() =>
                        handleLinkAction(
                          disciplineResponse._links.linkToDeleteDiscipline.href,
                          "DELETE"
                        )
                      }
                    >
                      Delete Discipline
                    </button>
                  )}
                  {disciplineResponse._links.linkToRemoveDisciplineFromStudents && (
                    <button
                      onClick={() =>
                        handleLinkAction(
                          disciplineResponse._links.linkToRemoveDisciplineFromStudents.href,
                          "PATCH"
                        )
                      }
                    >
                      Remove from Students
                    </button>
                  )}
                </div>
              </li>
            ))}
          </ul>
          {/* Paginare */}
          <div>
            {disciplinesResponse._links.previousPage && (
              <button
                onClick={() =>
                  handlePageChange(disciplinesResponse._links.previousPage.href)
                }
              >
                Previous Page
              </button>
            )}
            {disciplinesResponse._links.nextPage && (
              <button
                onClick={() =>
                  handlePageChange(disciplinesResponse._links.nextPage.href)
                }
              >
                Next Page
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default GetDisciplines;
