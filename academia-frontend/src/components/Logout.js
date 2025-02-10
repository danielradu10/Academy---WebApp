import React from "react";

const LogoutButton = ({ onLogout }) => {
  const handleLogout = async () => {
    const jwt = localStorage.getItem("jwt");

    try {
      const response = await fetch("http://localhost:8000/logout", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwt}`,
        },
        body: JSON.stringify({ jwt }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        console.error("Logout failed:", errorData.detail);
        alert(`Logout failed: ${errorData.detail}`);
        return;
      }

      const result = await response.json();
      if (result.successful) {
        console.log("Logout successful!");
      } else {
        console.warn("Logout not successful, but proceeding...");
      }
    } catch (err) {
      console.error("Logout request failed:", err);
      alert("An error occurred during logout. Please try again.");
    }

    localStorage.removeItem("jwt");
    onLogout(); 
  };

  return <button onClick={handleLogout}>Logout</button>;
};

export default LogoutButton;
