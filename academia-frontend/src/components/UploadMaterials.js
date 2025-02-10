import React, { useState } from "react";
import { insertLabMaterials, fetchLabMaterials, downloadLabMaterial, fetchLabMaterialsByUrl } from "../services/api";

const UploadMaterialsForm = ({ disciplines }) => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [message, setMessage] = useState("");
  const [selectedDiscipline, setSelectedDiscipline] = useState("");
  const [materials, setMaterials] = useState([]); 
  const [paginationLinks, setPaginationLinks] = useState(null); 

  const handleFileChange = (e) => {
    setSelectedFile(e.target.files[0]);
  };

  const handleDisciplineChange = (e) => {
    setSelectedDiscipline(e.target.value);
    setMaterials([]);
    setPaginationLinks(null);

  };

  const handleFetchMaterials = async () => {
    if (!selectedDiscipline) {
      alert("Please select a discipline first.");
      return;
    }

    try {
      const fetchedMaterials = await fetchLabMaterials(selectedDiscipline);
      console.log(fetchedMaterials);
      setMaterials(fetchedMaterials.materials); 
      setPaginationLinks(fetchedMaterials._links); 

    } catch (error) {
      console.error("Failed to fetch materials:", error);
    }
  };

  const handlePreview = async (materialLink, name) => {
    try {
      const { blob } = await downloadLabMaterial(materialLink, name);

      const url = window.URL.createObjectURL(blob);
      window.open(url, "_blank"); 
    } catch (error) {
      console.error("Failed to preview material:", error);
      alert("Failed to preview material.");
    }
  };

  const handlePageChange = async (url) => {
    try {
      const fetchedMaterials = await fetchLabMaterialsByUrl(url);
      console.log(fetchedMaterials);
      setMaterials(fetchedMaterials.materials); 
      setPaginationLinks(fetchedMaterials._links); 

    } catch (error) {
      console.error("Failed to fetch materials:", error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedFile) {
      alert("Please select a file before submitting.");
      return;
    }
    if (!selectedDiscipline) {
      alert("Please select a discipline.");
      return;
    }

    try {
      const formData = new FormData();
      formData.append("files", selectedFile);

      const response = await insertLabMaterials(selectedDiscipline, formData);
      setMessage(`Upload successful: ${response.message}`);
    } catch (error) {
      setMessage(`Upload failed: ${error.message}`);
    }
  };

  return (
    <div style={{ maxWidth: "600px", margin: "0 auto" }}>
 
      <h2>Upload Lab Materials</h2>
      {message && <p>{message}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label>Select Discipline:</label>
          <select value={selectedDiscipline} onChange={handleDisciplineChange} required>
            <option value="">-- Select Discipline --</option>
            {disciplines.map((discipline) => (
              <option key={discipline.discipline.disciplineCode} value={discipline.discipline.disciplineCode}>
                {discipline.discipline.disciplineName}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label>Select File:</label>
          <input type="file" onChange={handleFileChange} required />
        </div>
        <button type="submit">Upload</button>
      </form>

      <h3>Fetch and Display Materials</h3>
      <button onClick={handleFetchMaterials}>Fetch Materials</button>

      {materials.length > 0 ? (
        <ul>
          {materials.map((material, index) => (
            <li key={index}>
              {material.name}
              <button
                style={{ marginLeft: "10px" }}
                onClick={() => handlePreview(material._links.self, material.name)}
              >
                Preview
              </button>
            </li>
          ))}
        </ul>
      ) : (
        <p>No materials found.</p>
      )}

      {paginationLinks && (
              <div style={{ marginTop: "10px" }}>
                {paginationLinks.previousPage && (
                  <button onClick={() => handlePageChange(paginationLinks.previousPage)}>
                    Previous Page
                  </button>
                )}
                {paginationLinks.nextPage && (
                  <button onClick={() => handlePageChange(paginationLinks.nextPage)}>
                    Next Page
                  </button>
                )}
              </div>
            )}
    </div>
  );
};

export default UploadMaterialsForm;
