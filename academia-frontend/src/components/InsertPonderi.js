import React, { useState } from "react";

const InsertPonderiForm = ({ disciplineCode }) => {
  const [probes, setProbes] = useState([""]);
  const [weights, setWeights] = useState([""]);
  const [message, setMessage] = useState("");
  const [error, setError] = useState(null);
  const [responseLinks, setResponseLinks] = useState(null);

  const handleProbeChange = (index, value) => {
    const newProbes = [...probes];
    newProbes[index] = value;
    setProbes(newProbes);
  };

  const handleWeightChange = (index, value) => {
    const newWeights = [...weights];
    newWeights[index] = value;
    setWeights(newWeights);
  };

  const addRow = () => {
    setProbes([...probes, ""]);
    setWeights([...weights, ""]);
  };

  const removeRow = (index) => {
    setProbes(probes.filter((_, i) => i !== index));
    setWeights(weights.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError(null);

    const jwtToken = localStorage.getItem("jwt");
    if (!jwtToken) {
      setError("You must be logged in to insert ponderi.");
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8001/materials/disciplines/${disciplineCode}/ponderi/insert`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwtToken}`,
          },
          body: JSON.stringify({ probe: probes, ponderi: weights }),
        }
      );

      if (response.ok) {
        const data = await response.json();
        setMessage(data.message);
        setResponseLinks(data.links);
      } else {
        const errorData = await response.json();
        setError(errorData.detail || "Failed to insert ponderi.");
      }
    } catch (err) {
      setError("An error occurred while inserting ponderi.");
    }
  };

  const handleViewPonderi = async () => {
    try {
      const jwtToken = localStorage.getItem("jwt");
      if (!jwtToken) {
        setError("Authorization token missing.");
        return;
      }
      console.log(`http://127.0.0.1:8001${responseLinks.self}`);
      const response = await fetch(`http://127.0.0.1:8001${responseLinks.self}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        console.log(data)
        const details = data.data
        .map((item) => `Proba: ${item.proba}, Pondere: ${item.pondere}%`)
        .join("\n");
        alert(`Ponderi Details:\n${details}`);
      } else {
        setError("Failed to fetch ponderi details.");
      }
    } catch (err) {
      setError("An error occurred while fetching ponderi details.");
    }
  };

  return (
    <div style={{ maxWidth: "600px", margin: "0 auto" }}>
      <h2>Insert Ponderi</h2>
      {message && <p style={{ color: "green" }}>{message}</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}

      <form onSubmit={handleSubmit}>
        <table>
          <thead>
            <tr>
              <th>Probe</th>
              <th>Pondere (%)</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {probes.map((probe, index) => (
              <tr key={index}>
                <td>
                  <input
                    type="text"
                    value={probe}
                    onChange={(e) => handleProbeChange(index, e.target.value)}
                    required
                  />
                </td>
                <td>
                  <input
                    type="number"
                    value={weights[index]}
                    onChange={(e) => handleWeightChange(index, e.target.value)}
                    required
                  />
                </td>
                <td>
                  <button
                    type="button"
                    onClick={() => removeRow(index)}
                    disabled={probes.length === 1}
                  >
                    Remove
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <button type="button" onClick={addRow}>
          Add Row
        </button>
        <button type="submit">Submit</button>
      </form>

      {responseLinks && (
        <div style={{ marginTop: "20px" }}>
          <button onClick={handleViewPonderi}>View Ponderi</button>
        </div>
      )}
    </div>
  );
};

export default InsertPonderiForm;
