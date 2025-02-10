const API_BASE_URL = "http://localhost:8080/academia";
const HOST_MATERIALS = "http://127.0.0.1:8001"
const API_MATERIALS_URL = "http://127.0.0.1:8001/materials"
export async function fetchStudents() {
    const jwt = localStorage.getItem("jwt"); 
  
    const response = await fetch(`${API_BASE_URL}/students`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${jwt}`, 
        "Content-Type": "application/json",
      },
    });
  
    if (!response.ok) {
      console.log("Response not ok, writing the response:");
      console.log(response);
      throw new Error("Failed to fetch students");
    }
  
    return response.json();
  }

  export async function fetchStudentById(id) {
    const response = await fetch(`http://localhost:8080/academia/students/${id}`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${localStorage.getItem("jwt")}`, 
      },
    });
  
    if (!response.ok) {
      throw new Error("Failed to fetch student details");
    }
  
    return response.json(); 
  }

  export async function fetchStudentByEmail(email) {
    const jwt = localStorage.getItem("jwt");
    const response = await fetch(`${API_BASE_URL}/student?email=${encodeURIComponent(email)}`, {
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
  
    if (!response.ok) {
      throw new Error("Failed to fetch student by email");
    }
  
    return response.json();
  }
  

  export async function fetchDisciplinesByStudentId(id) {
    const response = await fetch(`http://localhost:8080/academia/students/${id}/disciplines`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${localStorage.getItem("jwt")}`, 
      },
    });
  
    if (!response.ok) {
      console.log(response);
      throw new Error("Failed to fetch student's disciplines!");
    }
  
    return response.json(); 
  }

  export async function fetchProfessorByEmail(email) {
    const jwt = localStorage.getItem("jwt");
    const response = await fetch(`${API_BASE_URL}/professor?email=${encodeURIComponent(email)}`, {
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
  
    if (!response.ok) {
      throw new Error("Failed to fetch professor by email");
    }
  
    return response.json();
  }


  export async function insertProfessor(professorData) {
    const jwt = localStorage.getItem("jwt"); 
    const response = await fetch(`${API_BASE_URL}/professors/insert`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${jwt}`,
      },
      body: JSON.stringify(professorData),
    });
  
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Failed to insert professor");
    }
  
    return response.json(); 
  }


  export async function fetchProfessors(params = {}) {
    const jwt = localStorage.getItem("jwt");
    const query = new URLSearchParams(params).toString();
    const response = await fetch(`http://localhost:8080/academia/professors?${query}`, {
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
  
    if (!response.ok) {
      throw new Error("Failed to fetch professors");
    }
  
    return response.json();
  }

  export async function fetchProfessorsByLink(link) {
    const jwt = localStorage.getItem("jwt");
    const response = await fetch(link, {
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
  
    if (!response.ok) {
      throw new Error("Failed to fetch next page of professors");
    }
  
    return response.json();
  }


  export async function fetchDisciplinesForProfessor(professorId) {
    const jwt = localStorage.getItem("jwt");
    const response = await fetch(`${API_BASE_URL}/professors/${professorId}/disciplines`, {
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
  
    if (!response.ok) {
      throw new Error(`Failed to fetch disciplines for professor with ID ${professorId}`);
    }
  
    return response.json();
  }


  export async function deleteProfessorById(id) {
    const jwt = localStorage.getItem("jwt");
    const response = await fetch(`${API_BASE_URL}/professors/${id}/delete`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
  
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Failed to delete professor");
    }
  
    return response.json();
  }
  
  export async function deleteDiscipline(disciplineCode) {
    const jwt = localStorage.getItem("jwt");
    const response = await fetch(`${API_BASE_URL}/discipline/${disciplineCode}/delete`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
  
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Failed to delete discipline");
    }
  
    return response.json();
  }

  export async function deleteDisciplineByLink(disciplineLink, method) {
    const jwt = localStorage.getItem("jwt");
    const response = await fetch(disciplineLink, {
      method: method,
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
  
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Failed to delete discipline");
    }
  
    return response.json();
  }
  
  export async function removeDisciplineFromAllStudents(disciplineCode) {
    const jwt = localStorage.getItem("jwt");
    const response = await fetch(`${API_BASE_URL}/discipline/${disciplineCode}/remove`, {
      method: "PATCH",
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
  
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Failed to remove discipline from all students");
    }
  
    return response.json();
  }
  
  export async function fetchDisciplinesForProfessorByLink(link) {
    const jwt = localStorage.getItem("jwt"); // Token-ul JWT pentru autorizare
  
    try {
      const response = await fetch(link, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });
  
      if (!response.ok) {
        throw new Error(`Failed to fetch disciplines from link: ${response.status}`);
      }
  
      const data = await response.json();
      return data;
    } catch (error) {
      console.error("Error fetching disciplines by link:", error);
      throw error;
    }
  }  


  export const fetchStudentsByQuery = async (params) => {
    const jwt = localStorage.getItem("jwt"); // Token-ul JWT pentru autorizare

    const queryString = Object.keys(params)
      .filter((key) => params[key]) 
      .map((key) => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
      .join("&");
    
    console.log(`${API_BASE_URL}/students?${queryString}`)
    const response = await fetch(`${API_BASE_URL}/students?${queryString}`, {
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
    if (!response.ok) {
      const errorText = await response.text();
      console.log("Din api call: failed " + errorText)
      
      throw new Error("Failed to fetch students");
    }
    const data = await response.json();
    return data
  };

  export async function fetchStudentsByLink(link) {
    const jwt = localStorage.getItem("jwt"); // Token-ul JWT pentru autorizare
  
    try {
      const response = await fetch(link, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });
  
      if (!response.ok) {
        throw new Error(`Failed to fetch students from link: ${response.status}`);
      }
  
      const data = await response.json();
      return data;
    } catch (error) {
      console.error("Error fetching students by link:", error);
      throw error;
    }
  }  

  export async function fetchDisciplinesForStudentByLink(link) {
    const jwt = localStorage.getItem("jwt"); // Token-ul JWT pentru autorizare
  
    try {
      const response = await fetch(link, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });
  
      if (!response.ok) {
        throw new Error(`Failed to fetch disciplines of student from link: ${response.status}`);
      }
  
      const data = await response.json();
      return data;
    } catch (error) {
      console.error("Error fetching disciplines of student by link:", error);
      throw error;
    }
  }  


  export async function fetchProfessorTitularByLink(link) {
    const jwt = localStorage.getItem("jwt"); // Token-ul JWT pentru autorizare
  
    try {
      const response = await fetch(link, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });
  
      if (!response.ok) {
        if (response.status == 403){
            alert("Forbidden")
            return
        }
      }
  
      const data = await response.json();
      return data;
    } catch (error) {
      console.error("Error fetching professor titular of discilpine by link:", error);
      throw error;
    }
  }  

  


  
  
  export async function fetchLabMaterials(disciplineCode) {
    const jwt = localStorage.getItem("jwt"); // Token-ul JWT pentru autorizare
  
    try {
      const response = await fetch(`${API_MATERIALS_URL}/disciplines/${disciplineCode}/labs`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json", 
        },
      });
  
      if (!response.ok) {
        console.log("Response not ok, writing the response:");
        console.log(response);
        throw new Error("Failed to fetch lab materials");
      }
  
      const data = await response.json();
  
      return data;
    } catch (error) {
      console.error("Error fetching lab materials:", error);
      throw error;
    }
  }
  
  export async function fetchLabMaterialsByUrl(url) {
    const jwt = localStorage.getItem("jwt"); // Token-ul JWT pentru autorizare
  
    try {
      const response = await fetch(`${HOST_MATERIALS}${url}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json", 
        },
      });
  
      if (!response.ok) {
        console.log("Response not ok, writing the response:");
        console.log(response);
        throw new Error("Failed to fetch lab materials");
      }
  
      const data = await response.json();
  
      return data;
    } catch (error) {
      console.error("Error fetching lab materials:", error);
      throw error;
    }
  }
  

  export async function insertLabMaterials(disciplineCode, files) {
    const jwt = localStorage.getItem("jwt");
  
    const formData = new FormData();
    files.forEach((file) => {
      formData.append("files", file); 
    });
  
    try {
      const response = await fetch(
        `${API_MATERIALS_URL}/disciplines/${disciplineCode}/labs/insert`,
        {
          method: "PATCH",
          headers: {
            Authorization: `Bearer ${jwt}`, 
           
          },
          body: formData, 
        }
      );
  
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.detail || "Failed to insert lab materials");
      }
  
      return await response.json(); 
    } catch (error) {
      console.error("Error inserting lab materials:", error);
      throw error;
    }
  }


  export async function downloadLabMaterial(materialLink, name) {
    const jwt = localStorage.getItem("jwt"); // Token-ul JWT pentru autorizare
  
    try {
      const response = await fetch(`${HOST_MATERIALS}${materialLink}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwt}`, // Header-ul de autorizare
        },
      });
  
      if (!response.ok) {
        throw new Error(`Failed to download material: ${response.statusText}`);
      }
  
      const blob = await response.blob(); // Obținem conținutul ca Blob
      return { blob, name };
    } catch (error) {
      console.error("Error downloading material:", error);
      throw error;
    }
  }


    

  
