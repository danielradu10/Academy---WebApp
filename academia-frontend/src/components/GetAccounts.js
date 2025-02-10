import React, { useState } from "react";

const GetAccounts = () => {
  const [accounts, setAccounts] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleFetchAccounts = async () => {
    setError(null);
    setLoading(true);

    const jwtToken = localStorage.getItem("jwt");
    if (!jwtToken) {
      setError("You must be logged in as an admin to fetch accounts.");
      setLoading(false);
      return;
    }

    try {
      const response = await fetch("http://localhost:8000/accounts", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        console.log(data)
        setAccounts(data.accounts); 
      } else {
        const errorData = await response.json();
        setError(errorData.detail || "Failed to fetch accounts.");
      }
    } catch (err) {
      setError("An error occurred while fetching accounts.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Get Accounts</h2>
      <button onClick={handleFetchAccounts} disabled={loading}>
        {loading ? "Loading..." : "Fetch Accounts"}
      </button>
      {error && <p style={{ color: "red" }}>{error}</p>}

      {accounts.length > 0 && (
        <div>
          <h3>Accounts List</h3>
          <ul>
            {accounts.map((account, index) => (
              <li key={index}>
                <p>
                  <strong>Username:</strong> {account.email}
                </p>
                <p>
                  <strong>Password:</strong> {account.password}
                </p>
                <p>
                  <strong>Role:</strong> {account.role}
                </p>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default GetAccounts;
